package ru.lizzzi.crossfit_rekord.data;

import android.annotation.SuppressLint;
import android.content.ContentValues;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SQLiteStorageUserResult extends SQLiteOpenHelper {

    private static String DATABASE_NAME = "MyResult.db";
    private SQLiteDatabase database;
    private final Context context;
    private static final int DATABASE_VERSION = 4;

    public SQLiteStorageUserResult(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        if (checkDataBase()) {
            database = this.getReadableDatabase();
        } else {
            copyDataBase();
        }
    }

    /**
     * Проверяет, существует ли уже эта база, чтобы не копировать каждый раз при запуске приложения
     * @return true если существует, false если не существует
     */
    private boolean checkDataBase() {
        SQLiteDatabase database = null;

        try{
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
        if(database != null) {
            database.close();
        }
        return database != null;
    }

    /**
     * Копирует базу из папки assets заместо созданной локальной БД
     * Выполняется путем копирования потока байтов.
     * */
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
                newValues.put(MyResultDB.EXERCISE, "MyWeight");
                newValues.put(MyResultDB.RESULT, "0");
                database.insert(
                        MyResultDB.TABLE_NAME,
                        null,
                        newValues);
                break;
            default:
                try {
                    InputStream inputStream = context.getAssets().open("db/" + DATABASE_NAME);
                    String outFileName = database.getPath();
                    OutputStream outputStream = new FileOutputStream(outFileName);

                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = inputStream.read(buffer)) > 0) {
                        outputStream.write(buffer, 0, length);
                    }

                    outputStream.flush();
                    outputStream.close();
                    inputStream.close();
                } catch (IOException exception) {
                    Log.w("RekordWarning", "");
                    exception.printStackTrace();
                }
        }
    }

    private void upgradeDbToThirdVersion(SQLiteDatabase database) {
        List<Map<String, String>> valueFromDb = getResultForOldDB(database);
        context.deleteDatabase(MyResultDB.TABLE_NAME);
        onCreate(database);
        try {
            InputStream inputStream = context.getAssets().open("db/" + DATABASE_NAME);
            String outFileName = database.getPath();
            OutputStream outputStream = new FileOutputStream(outFileName);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            outputStream.flush();
            outputStream.close();
            inputStream.close();
        } catch (IOException exception) {
            Log.w("RekordWarning", "");
            exception.printStackTrace();
        }

        for (int i = 0; i < valueFromDb.size(); i++) {
            for (Map.Entry<String, String> entry : valueFromDb.get(i).entrySet()) {
                String exercise = entry.getKey();
                String result = entry.getValue();
                ContentValues newValues2 = new ContentValues();
                newValues2.put(MyResultDB.EXERCISE, exercise);
                newValues2.put(MyResultDB.RESULT, result);
                database.update(
                        MyResultDB.TABLE_NAME,
                        newValues2,
                        MyResultDB.EXERCISE + "= ?",
                        new String[]{exercise});
            }
        }
    }

    private List<Map<String, String>> getResultForOldDB(SQLiteDatabase database) {
        List<Map<String, String>> results = new ArrayList<>();

        Cursor cursor = database.query(
                MyResultDB.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null,
                null);
        if (cursor != null && cursor.moveToFirst()) {
            String exercise;
            String result;
            do {
                Map<String, String> exerciseResult =  new HashMap<>();
                exercise = cursor.getString(cursor.getColumnIndex(MyResultDB.EXERCISE));
                result = cursor.getString(cursor.getColumnIndex(MyResultDB.RESULT));
                exerciseResult.put("result", result);
                exerciseResult.put("exercise", exercise);
                results.add(exerciseResult);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return results;
    }

    public void setResult(String exercise, String result) {
        database = this.getWritableDatabase();
        ContentValues newValues = new ContentValues();
        newValues.put(MyResultDB.EXERCISE, exercise);
        newValues.put(MyResultDB.RESULT, result);
        database.update(
                MyResultDB.TABLE_NAME,
                newValues,
                MyResultDB.EXERCISE + "= ?",
                new String[]{exercise});
        database.close();
    }

    public List<Map<String, String>> getResult() {
        database = this.getReadableDatabase();
        List<Map<String, String>> results = new ArrayList<>();

        Cursor cursor = database.query(
                MyResultDB.TABLE_NAME,
                null,
                MyResultDB.EXERCISE + "!= ?",
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
                exercise = cursor.getString(cursor.getColumnIndex(MyResultDB.EXERCISE));
                exerciseRu = cursor.getString(cursor.getColumnIndex(MyResultDB.EXERCISE_RU));
                result = cursor.getString(cursor.getColumnIndex(MyResultDB.RESULT));
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
                MyResultDB.EXERCISE + "= ?",
                new String[] { "MyWeight" },
                null,
                null,
                null,
                null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                result = cursor.getString(cursor.getColumnIndex(MyResultDB.RESULT));
            } while (cursor.moveToNext());
            cursor.close();
        }
        database.close();
        return result;
    }

    public static final class MyResultDB implements BaseColumns {

        final static String TABLE_NAME = "myResult";
        final static String EXERCISE = "Exercise";
        final static String EXERCISE_RU = "ExerciseRu";
        final static String RESULT = "Result";
    }
}
