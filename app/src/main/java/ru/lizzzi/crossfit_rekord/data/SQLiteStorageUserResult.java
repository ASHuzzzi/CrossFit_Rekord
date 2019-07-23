package ru.lizzzi.crossfit_rekord.data;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class SQLiteStorageUserResult extends SQLiteOpenHelper {

    // путь к базе данных вашего приложения
    @SuppressLint("SdCardPath")
    private static String DB_PATH = "/data/data/ru.lizzzi.crossfit_rekord/databases/";
    private static String DB_NAME = "MyResult.db";
    private SQLiteDatabase database;
    private final Context context;
    private static final int DB_VERSION = 2;

    public SQLiteStorageUserResult(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    /**
     * Создает пустую базу данных и перезаписывает ее нашей собственной базой
     * */
    public void createDataBase() throws IOException {

        if(!checkDataBase()){
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
    private boolean checkDataBase(){
        SQLiteDatabase checkDB = null;

        try{
            String myPath = DB_PATH + DB_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
            database = this.getReadableDatabase();
        }catch(SQLiteException e){
            //база еще не существует
        }
        if(checkDB != null){
            checkDB.close();
        }
        return checkDB != null;
    }

    /**
     * Копирует базу из папки assets заместо созданной локальной БД
     * Выполняется путем копирования потока байтов.
     * */
    private void copyDataBase() throws IOException{
        //Открываем локальную БД как входящий поток
        InputStream myInput = context.getAssets().open("db/" + DB_NAME);

        //Путь ко вновь созданной БД
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        String outFileName = sqLiteDatabase.getPath();
        sqLiteDatabase.close();

        //Открываем пустую базу данных как исходящий поток
        OutputStream myOutput = new FileOutputStream(outFileName);

        //перемещаем байты из входящего файла в исходящий
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer))>0){
            myOutput.write(buffer, 0, length);
        }

        //закрываем потоки
        myOutput.flush();
        myOutput.close();
        myInput.close();
    }

    public void openDataBase() throws SQLException {
        //открываем БД
        String myPath = DB_PATH + DB_NAME;
        database = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        database.disableWriteAheadLogging();
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
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        switch (oldVersion) {
            case 1:
                ContentValues newValues = new ContentValues();
                newValues.put(dbMyResult.columnExercise, "MyWeight");
                newValues.put(dbMyResult.columnResult, "0");
                sqLiteDatabase.insert(
                        dbMyResult.TABLE_NAME,
                        null,
                        newValues);
                break;
        }
    }

    public void setResult(String stExercise, String stResult){
        database = this.getWritableDatabase();
        ContentValues newValues = new ContentValues();
        newValues.put(dbMyResult.columnExercise, stExercise);
        newValues.put(dbMyResult.columnResult, stResult);
        database.update(
                dbMyResult.TABLE_NAME,
                newValues,
                dbMyResult.columnExercise + "= ?",
                new String[]{stExercise});
        database.close();
    }

    public Map<String, String> getResult() {
        database = this.getReadableDatabase();
        Map<String, String> result = new HashMap<>();

        Cursor cursor = database.query(
                dbMyResult.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null,
                null);
        if (cursor != null && cursor.moveToFirst()) {
            String stExercise;
            String stResult;
            do {
                stExercise = cursor.getString(cursor.getColumnIndex(dbMyResult.columnExercise));
                stResult = cursor.getString(cursor.getColumnIndex(dbMyResult.columnResult));

                result.put(stExercise, stResult);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return result;
    }

    public static final class dbMyResult implements BaseColumns {

        final static String TABLE_NAME = "myResult";
        final static String columnExercise = "Exercise";
        final static String columnResult = "Result";
    }
}
