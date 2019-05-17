package ru.lizzzi.crossfit_rekord.data;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DefinitionDBHelper  extends SQLiteOpenHelper{

    // путь к базе данных вашего приложения
    @SuppressLint("SdCardPath")
    private static final String DB_PATH = "/data/data/ru.lizzzi.crossfit_rekord/databases/";
    private static final String DB_NAME = "DefinitionDirectory.db";
    private SQLiteDatabase database;
    private final Context context;

    public DefinitionDBHelper(Context context) {
        super(context, DB_NAME, null, 1);
        this.context = context;
    }

    /**
     * Создает пустую базу данных и перезаписывает ее нашей собственной базой
     * */
    public void createDataBase() throws IOException {
        if(!checkDataBase()) {
            //вызывая этот метод создаем пустую базу, позже она будет перезаписана
            this.getReadableDatabase();
            try {
                copyDataBase();
            } catch (IOException e) {
                throw new Error("Error copying database");
            }
        }
    }

    /**
     * Проверяет, существует ли уже эта база, чтобы не копировать каждый раз при запуске приложения
     * @return true если существует, false если не существует
     */
    private boolean checkDataBase() {
        database = null;
        try {
            String myPath = DB_PATH + DB_NAME;
            database = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        } catch(SQLiteException e) {
            throw new Error("The base does not exist yet");
        }
        if (database != null) {
            database.close();
        }
        return database != null;
    }

    /**
     * Копирует базу из папки assets заместо созданной локальной БД
     * Выполняется путем копирования потока байтов.
     * */
    private void copyDataBase() throws IOException {
        //Открываем локальную БД как входящий поток
        InputStream inputStream = context.getAssets().open("db/" + DB_NAME);

        //Путь ко вновь созданной БД
        database = this.getReadableDatabase();
        String outFileName = database.getPath();
        database.close();

        //Открываем пустую базу данных как исходящий поток
        OutputStream outputStream = new FileOutputStream(outFileName);

        //перемещаем байты из входящего файла в исходящий
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer))>0) {
            outputStream.write(buffer, 0, length);
        }

        //закрываем потоки
        outputStream.flush();
        outputStream.close();
        inputStream.close();
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
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    }

    // Здесь можно добавить вспомогательные методы для доступа и получения данных из БД
    // вы можете возвращать курсоры через "return myDataBase.query(....)", это облегчит их использование
    // в создании адаптеров для ваших view
    public ArrayList<String> getListCharacters() {
        database = this.getReadableDatabase();
        ArrayList<String> listCharacter = new ArrayList<>();
        String[] columns = new  String[] {DefinitionDbContarct.DBdefinition.Column_character};
        Cursor cursor = database.query(
                true,
                DefinitionDbContarct.DBdefinition.TABLE_NAME,
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
                for (String cn : cursor.getColumnNames()) {
                    character = cursor.getString(cursor.getColumnIndex(cn));
                }
                listCharacter.add(character);
            }while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
        database.close();
        return listCharacter;
    }

    public List<Map<String, Object>> getTerminsAndDefinitions(String character){
        database = this.getReadableDatabase();
        List<Map<String, Object>> termsOfSelectedCharacter = new ArrayList<>();
        String[] columns = new  String[] {
                DefinitionDbContarct.DBdefinition.Column_termin,
                DefinitionDbContarct.DBdefinition.Column_description};
        Cursor cursor = database.query(
                DefinitionDbContarct.DBdefinition.TABLE_NAME,
                columns,
                DefinitionDbContarct.DBdefinition.Column_character + "= '" + character + "'",
                null,
                null,
                null,
                null);
        if (cursor !=null && cursor.moveToFirst()) {
            do {
                Map<String, Object> itemList = new HashMap<>();
                String termin = cursor.getString(
                        cursor.getColumnIndex(DefinitionDbContarct.DBdefinition.Column_termin));
                String description = cursor.getString(
                        cursor.getColumnIndex(DefinitionDbContarct.DBdefinition.Column_description));
                itemList.put("termin", termin);
                itemList.put("description", description);
                termsOfSelectedCharacter.add(itemList);
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
        database.close();
        return termsOfSelectedCharacter;
    }
}