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
    private static final int DATABASE_VERSION = 5;
    public static final String MY_WEIGHT = "MyWeight";

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
                updateDBForVersion1(database);
                break;

            case 4:
                updateDBForVersion4(database);
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

    private void updateDBForVersion1(SQLiteDatabase database) {
        ContentValues newValues = new ContentValues();
        newValues.put(MyResultDB.EXERCISE, MY_WEIGHT);
        newValues.put(MyResultDB.RESULT, "0");
        database.insert(
                MyResultDB.TABLE_NAME,
                null,
                newValues);
    }

    private void updateDBForVersion4(SQLiteDatabase database) {
        Map<String, String> changeMapForExercise = new HashMap<>();
        changeMapForExercise.put("Clean", "Power Clean");
        changeMapForExercise.put("Row (m)", "Row (500m.)");
        changeMapForExercise.put("Row (cal)", "Row (20 cal.)");
        updateDBValue(database, changeMapForExercise, MyResultDB.EXERCISE, MyResultDB.EXERCISE);

        Map<String, String> changeMapForExerciseRu = new HashMap<>();
        changeMapForExerciseRu.put("Гребля (25 кал)", "Гребля (20 кал)");
        changeMapForExerciseRu.put("Гребля (500м)", "Гребля (500м.)");
        updateDBValue(database, changeMapForExerciseRu, MyResultDB.EXERCISE_RU, MyResultDB.EXERCISE_RU);

        database.execSQL(
                "ALTER TABLE " +
                        MyResultDB.TABLE_NAME +
                        " ADD COLUMN " +
                        MyResultDB.UNIT +
                        " TEXT;");

        Map<String, String> unitForExercise = new HashMap<>();
        unitForExercise.put("Deadlift", "кг");
        unitForExercise.put("Snatch", "кг");
        unitForExercise.put("Squat Clean", "кг");
        unitForExercise.put("Power Clean", "кг");
        unitForExercise.put("Squat Snatches", "кг");
        unitForExercise.put("Clean and jerk", "кг");
        unitForExercise.put("Front Squat", "кг");
        unitForExercise.put("Back Squat", "кг");
        unitForExercise.put("Shoulder Press", "кг");
        unitForExercise.put("Push Press", "кг");
        unitForExercise.put("Bench Press", "кг");
        unitForExercise.put("Sumo Deadlift", "кг");
        unitForExercise.put("Thruster", "кг");
        unitForExercise.put("Dumbell Snatch", "кг");
        unitForExercise.put("Row (500m.)", "время");
        unitForExercise.put("Row (20 cal.)", "время");
        unitForExercise.put("MyWeight", "кг");
        updateDBValue(database, unitForExercise, MyResultDB.UNIT, MyResultDB.EXERCISE);
    }

    private void updateDBValue(SQLiteDatabase database,
                               Map<String, String> changeMap,
                               String whatChange,
                               String whereChanging) {
        for (Map.Entry<String, String> entry : changeMap.entrySet()) {
            ContentValues newValues = new ContentValues();
            newValues.put(whatChange, entry.getValue());
            database.update(
                    MyResultDB.TABLE_NAME,
                    newValues,
                    whereChanging + "= ?",
                    new String[]{entry.getKey()});
        }
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
                new String[] { MY_WEIGHT },
                null,
                null,
                null,
                null);
        if (cursor != null && cursor.moveToFirst()) {
            String exercise;
            String exerciseRu;
            String result;
            String unit;
            do {
                Map<String, String> exerciseResult =  new HashMap<>();
                exercise = cursor.getString(cursor.getColumnIndex(MyResultDB.EXERCISE));
                exerciseRu = cursor.getString(cursor.getColumnIndex(MyResultDB.EXERCISE_RU));
                result = cursor.getString(cursor.getColumnIndex(MyResultDB.RESULT));
                unit = cursor.getString(cursor.getColumnIndex(MyResultDB.UNIT));
                exerciseResult.put(MyResultDB.RESULT, result);
                exerciseResult.put(MyResultDB.EXERCISE, exercise);
                exerciseResult.put(MyResultDB.EXERCISE_RU, exerciseRu);
                exerciseResult.put(MyResultDB.UNIT, unit);
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
                new String[] { MY_WEIGHT },
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
        private final static String TABLE_NAME = "myResult";
        public final static String EXERCISE = "Exercise";
        public final static String EXERCISE_RU = "ExerciseRu";
        public final static String UNIT = "Unit";
        public final static String RESULT = "Result";
    }
}
