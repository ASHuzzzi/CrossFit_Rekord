package ru.lizzzi.crossfit_rekord.data;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import ru.lizzzi.crossfit_rekord.items.TermItem;

public class DefinitionStorage extends SQLiteOpenHelper{

    private static final String DATABASE_NAME = "DefinitionDirectory.db";
    private static final int DATABASE_VERSION = 1;
    private SQLiteDatabase database;
    private final Context context;

    public DefinitionStorage(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        if (checkDataBase()) {
            database = this.getReadableDatabase();
        } else {
            copyDataBase();
        }
    }

    private boolean checkDataBase() {
        SQLiteDatabase database = null;
        try {
            @SuppressLint("SdCardPath")
            String DATABASE_PATH = "/data/data/ru.lizzzi.crossfit_rekord/databases/";
            String dbPath = DATABASE_PATH + DATABASE_NAME;
            database = SQLiteDatabase.openDatabase(
                    dbPath,
                    null,
                    SQLiteDatabase.OPEN_READONLY);
        } catch(SQLiteException exception) {
            Log.i("RekordInfo", exception.getMessage());
        }
        if (database != null) {
            database.close();
        }
        return database != null;
    }

    private void copyDataBase() {
        try {
            //Открываем локальную БД как входящий поток
            InputStream inputStream = context.getAssets().open("db/" + DATABASE_NAME);

            //Путь ко вновь созданной БД
            SQLiteDatabase database = this.getReadableDatabase();
            String outFileName = database.getPath();
            database.close();

            //Открываем пустую базу данных как исходящий поток
            OutputStream outputStream = new FileOutputStream(outFileName);

            //перемещаем байты из входящего файла в исходящий
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            //закрываем потоки
            outputStream.flush();
            outputStream.close();
            inputStream.close();
        } catch (IOException exception) {
            Log.w("RekordWarning", "");
            exception.printStackTrace();
        }
    }

    @Override
    public synchronized void close() {
        if(database != null)
            database.close();
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        context.deleteDatabase(DefinitionDB.TABLE_NAME);
        copyDataBase();
    }

    public List<String> getListCharacters() {
        database = this.getReadableDatabase();
        ArrayList<String> listCharacter = new ArrayList<>();
        String[] columns = new  String[] {DefinitionDB.CHARACTER};
        Cursor cursor = database.query(
                true,
                DefinitionDB.TABLE_NAME,
                columns,
                null,
                null,
                null,
                null,
                null,
                null);
        if (cursor !=null && cursor.moveToFirst()) {
            do {
                String character = "";
                for (String columnName : cursor.getColumnNames()) {
                    character = cursor.getString(cursor.getColumnIndex(columnName));
                }
                listCharacter.add(character);
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
        database.close();
        return listCharacter;
    }

    public List<TermItem> getTermsAndDefinitions(String character){
        database = this.getReadableDatabase();
        List<TermItem> terms = new ArrayList<>();
        String[] columns = new  String[] { DefinitionDB.TERM, DefinitionDB.DEFINITION};
        Cursor cursor = database.query(
                DefinitionDB.TABLE_NAME,
                columns,
                DefinitionDB.CHARACTER + "= '" + character + "'",
                null,
                null,
                null,
                null);
        if (cursor !=null && cursor.moveToFirst()) {
            do {
                String term = cursor.getString(cursor.getColumnIndex(DefinitionDB.TERM));
                String definition = cursor.getString(cursor.getColumnIndex(DefinitionDB.DEFINITION));
                TermItem termItem = new TermItem(term, definition);
                terms.add(termItem);
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
        database.close();
        return terms;
    }

    public static final class DefinitionDB implements BaseColumns {
        final static String TABLE_NAME = "definition";
        final static String TERM = "termin";
        final static String DEFINITION = "description";
        final static String CHARACTER = "character";
    }
}