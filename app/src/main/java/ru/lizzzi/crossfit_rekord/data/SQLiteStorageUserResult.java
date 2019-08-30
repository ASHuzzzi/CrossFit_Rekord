package ru.lizzzi.crossfit_rekord.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SQLiteStorageUserResult extends SQLiteOpenHelper {

    private static String DATABASE_NAME = "MyResult.db";
    private SQLiteDatabase database;
    private final Context context;
    private static final int DATABASE_VERSION = 3;

    public SQLiteStorageUserResult(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    /**
     * Создает пустую базу данных и перезаписывает ее нашей собственной базой
     * */
    public void createDataBase() {

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
        SQLiteDatabase database = null;

        try{
            // путь к базе данных вашего приложения
            String DATABASE_PATH = "/data/data/ru.lizzzi.crossfit_rekord/databases/";
            String dbPath = DATABASE_PATH + DATABASE_NAME;
            database = SQLiteDatabase.openDatabase(
                    dbPath,
                    null,
                    SQLiteDatabase.OPEN_READONLY);
            this.database = this.getReadableDatabase();
        } catch(SQLiteException e) {
            //база еще не существует
        }
        if(database != null) {
            database.close();
        }
        return database != null;
    }

    /**
     * Копирует базу из папки assets заместо созданной локальной БД
     * Выполняется путем копирования потока байтов.
     * */
    private void copyDataBase() throws IOException{
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
    }

    @Override
    public synchronized void close() {
        if(database != null) {
            database.close();
        }
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        switch (oldVersion) {
            case 1:
                ContentValues newValues = new ContentValues();
                newValues.put(MyResultDB.columnExercise, "MyWeight");
                newValues.put(MyResultDB.columnResult, "0");
                database.insert(
                        MyResultDB.TABLE_NAME,
                        null,
                        newValues);
                break;
            case 3:
                List<Map<String, String>> tempValue = getResult();
                database.execSQL("DROP TABLE history");
                try {
                    copyDataBase();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                for (int i = 0; i < tempValue.size(); i++) {
                    for (Map.Entry<String, String> entry : tempValue.get(i).entrySet()) {
                        String exercise = entry.getKey();
                        String result = entry.getValue();
                        setResult(exercise, result);
                    }
                }
                break;
        }
    }

    public void setResult(String exercise, String result) {
        database = this.getWritableDatabase();
        ContentValues newValues = new ContentValues();
        newValues.put(MyResultDB.columnExercise, exercise);
        newValues.put(MyResultDB.columnResult, result);
        database.update(
                MyResultDB.TABLE_NAME,
                newValues,
                MyResultDB.columnExercise + "= ?",
                new String[]{exercise});
        database.close();
    }

    public List<Map<String, String>> getResult() {
        database = this.getReadableDatabase();
        List<Map<String, String>> results = new ArrayList<>();

        Cursor cursor = database.query(
                MyResultDB.TABLE_NAME,
                null,
                MyResultDB.columnExercise + "!= ?",
                new String[] { "MyWeight" },
                null,
                null,
                null,
                null);
        if (cursor != null && cursor.moveToFirst()) {
            String exercise;
            String exerciseRu;
            String result;
            do {
                Map<String, String> exerciseResult =  new HashMap<>();
                exercise = cursor.getString(cursor.getColumnIndex(MyResultDB.columnExercise));
                exerciseRu = cursor.getString(cursor.getColumnIndex(MyResultDB.columnExerciseRU));
                result = cursor.getString(cursor.getColumnIndex(MyResultDB.columnResult));
                exerciseResult.put("result", result);
                exerciseResult.put("exercise", exercise);
                exerciseResult.put("exerciseRu", exerciseRu);
                results.add(exerciseResult);
            } while (cursor.moveToNext());
            cursor.close();
        }
        database.close();
        return results;
    }

    public String getWeight() {
        database = this.getReadableDatabase();
        String result = "";
        Cursor cursor = database.query(
                MyResultDB.TABLE_NAME,
                null,
                MyResultDB.columnExercise + "= ?",
                new String[] { "MyWeight" },
                null,
                null,
                null,
                null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                result = cursor.getString(cursor.getColumnIndex(MyResultDB.columnResult));
            } while (cursor.moveToNext());
            cursor.close();
        }
        database.close();
        return result;
    }

    public static final class MyResultDB implements BaseColumns {

        final static String TABLE_NAME = "myResult";
        final static String columnExercise = "Exercise";
        final static String columnExerciseRU = "ExerciseRu";
        final static String columnResult = "Result";
    }
}
