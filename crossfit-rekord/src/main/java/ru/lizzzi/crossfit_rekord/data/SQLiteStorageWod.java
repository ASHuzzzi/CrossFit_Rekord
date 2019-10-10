package ru.lizzzi.crossfit_rekord.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.lizzzi.crossfit_rekord.backendless.BackendlessQueries;

public class SQLiteStorageWod extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "CalendarWod.db";
    private static final int DATABASE_VERSION = 3;
    private SQLiteDatabase database;

    public static final String DATE_SESSION = "dateSession";
    public static final String WOD_LEVEL = "wodLevel";
    public static final String WOD = "wod";
    public static final int EMPTY_VALUE = 0;

    public SQLiteStorageWod(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        String SQL_CREATE_TABLE =
                "CREATE TABLE " + DbHelper.TABLE_NAME + " ("
                        + DbHelper.DATE_SESSION + " REAL NOT NULL, "
                        + DbHelper.OBJECT_ID + " TEXT, "
                        + DbHelper.WARM_UP + " TEXT, "
                        + DbHelper.SKILL + " TEXT, "
                        + DbHelper.WOD_LEVEL + " TEXT,"
                        + DbHelper.WOD + " TEXT);";
        database.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            database.execSQL("DROP TABLE " + DbHelper.TABLE_NAME);
            onCreate(database);
        }
    }

    @Override
    public synchronized void close() {
        if(database != null)
            database.close();
        super.close();
    }


    public List<Date> selectDates(String objectId, long timeStart, long timeNow) {
        database = this.getReadableDatabase();

        ArrayList<Date> selectedDates = new ArrayList<>();
        String[] columns = new String[]{DbHelper.DATE_SESSION};
        String selection =
                DbHelper.OBJECT_ID + "= '" + objectId +
                "' AND "
                + DbHelper.DATE_SESSION + " BETWEEN " + timeStart + " AND " + timeNow;
        Cursor cursor = database.query(
                DbHelper.TABLE_NAME,
                columns,
                selection,
                null,
                null,
                null,
                null);
        if (cursor !=null && cursor.moveToFirst()) {
            do {
                Date dateOfWod = new Date();

                for (String columnName : cursor.getColumnNames()) {
                    long dateInMilliseconds = cursor.getLong(cursor.getColumnIndex(columnName));
                    dateOfWod.setTime(dateInMilliseconds);
                    selectedDates.add(dateOfWod);
                }

            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
        return selectedDates;
    }

    public void saveDates(String userId, List<Map> trainingResults) {
        database = this.getWritableDatabase();
        ContentValues newValues = new ContentValues();
        Date date;
        String skillResult;
        String wodLevel;
        String wodResult;
        BackendlessQueries queries = new BackendlessQueries();
        for (int i = 0; i < trainingResults.size(); i++) {
            date = (Date) trainingResults.get(i).get(queries.TABLE_RESULTS_DATE_SESSION);
            if (date != null) {
                skillResult = (trainingResults.get(i).get(queries.TABLE_RESULTS_SKILL) != null)
                        ? String.valueOf(trainingResults.get(i).get(queries.TABLE_RESULTS_SKILL))
                        : "";
                wodLevel = (trainingResults.get(i).get(queries.TABLE_RESULTS_WOD_LEVEL) != null)
                        ? String.valueOf(trainingResults.get(i).get(queries.TABLE_RESULTS_WOD_LEVEL))
                        : "";
                wodResult = (trainingResults.get(i).get(queries.TABLE_RESULTS_WOD_RESULT) != null)
                        ? String.valueOf(trainingResults.get(i).get(queries.TABLE_RESULTS_WOD_RESULT))
                        : "";
                newValues.put(DbHelper.OBJECT_ID, userId);
                newValues.put(DbHelper.DATE_SESSION, date.getTime());
                newValues.put(DbHelper.SKILL, skillResult);
                newValues.put(DbHelper.WOD_LEVEL, wodLevel);
                newValues.put(DbHelper.WOD, wodResult);
                database.insert(DbHelper.TABLE_NAME, null, newValues);
                newValues.clear();
            }
        }
        database.close();
    }

    public void saveDate(String userId,
                         long date,
                         String userSkill,
                         String wodLevel,
                         String userWodResult) {
        database = this.getWritableDatabase();
        ContentValues newValues = new ContentValues();
        newValues.put(DbHelper.OBJECT_ID, userId);
        newValues.put(DbHelper.DATE_SESSION, date);
        newValues.put(DbHelper.SKILL, userSkill);
        newValues.put(DbHelper.WOD_LEVEL, wodLevel);
        newValues.put(DbHelper.WOD, userWodResult);
        database.insert(DbHelper.TABLE_NAME, null, newValues);
        database.close();
    }

    public void deleteDate(String userId, long date) {
        database = this.getWritableDatabase();

        String selection =
                DbHelper.OBJECT_ID + "= '" + userId +
                "' AND " +
                DbHelper.DATE_SESSION + "= '" + date + "'";
        database.delete(DbHelper.TABLE_NAME, selection, null);
        database.close();
    }

    public int getTrainingQuantity(long timeStart, long timeEnd) {
        database = this.getReadableDatabase();
        int trainingQuantity = 0;
        String[] columns = new String[] { "COUNT(" + DbHelper.DATE_SESSION + ")" };
        String selection =
                DbHelper.DATE_SESSION + " >= '" + timeStart
                + "' AND " +
                DbHelper.DATE_SESSION + " <= '" + timeEnd + "'";
        Cursor cursor = database.query(
                true,
                DbHelper.TABLE_NAME,
                columns,
                selection,
                null,
                null,
                null,
                null,
                null);
        if (cursor != null && cursor.moveToFirst()) {
            trainingQuantity = cursor.getInt(0);
            cursor.close();
        }
        return trainingQuantity;
    }

    public Map<String, String> getLastTraining() {
        database = this.getReadableDatabase();
        Map<String, String>  lastTraining = new HashMap<>();
        String[] columns = new String[] { "MAX(" + DbHelper.DATE_SESSION + ")", "*" };
        Cursor cursor = database.query(
                DbHelper.TABLE_NAME,
                columns,
                null,
                null,
                null,
                null,
                null,
                null);
        if (cursor != null && cursor.moveToFirst()) {
            String dateSession;
            String wodLevel;
            String wod;

            do {
                dateSession = String.valueOf(cursor.getLong(cursor.getColumnIndex(DbHelper.DATE_SESSION)));
                wodLevel = cursor.getString(cursor.getColumnIndex(DbHelper.WOD_LEVEL));
                wod = cursor.getString(cursor.getColumnIndex(DbHelper.WOD));
                lastTraining.put(DATE_SESSION, dateSession);
                if (wodLevel != null) {
                    lastTraining.put(WOD_LEVEL, wodLevel);
                } else {
                    lastTraining.put(WOD_LEVEL, String.valueOf(EMPTY_VALUE));
                }
                if (wod != null) {
                    lastTraining.put(WOD, wod);
                } else {
                    lastTraining.put(WOD, String.valueOf(EMPTY_VALUE));
                }
            } while (cursor.moveToNext());
            cursor.close();
        }
        return lastTraining;
    }

    public List<String> getLevelsTrainingForPeriod(long timeStart, long timeEnd) {
        database = this.getReadableDatabase();
        List<String> levels = new ArrayList<>();
        String[] columns = new String[] { DbHelper.WOD_LEVEL };
        String selection =
                DbHelper.DATE_SESSION + " >= '" + timeStart
                        + "' AND " +
                        DbHelper.DATE_SESSION + " <= '" + timeEnd + "'";
        Cursor cursor = database.query(
                DbHelper.TABLE_NAME,
                columns,
                selection,
                null,
                null,
                null,
                null,
                null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                levels.add(cursor.getString(cursor.getColumnIndex(DbHelper.WOD_LEVEL)));
            } while (cursor.moveToNext());
            cursor.close();
            cursor.close();
        }
        return levels;
    }

    public List<Map> getTrainingForPeriod(long timeStart, long timeEnd) {
        database = this.getReadableDatabase();
        String[] columns = new String[] { DbHelper.DATE_SESSION, DbHelper.WOD_LEVEL, DbHelper.WOD };
        String selection =
                DbHelper.DATE_SESSION + " >= '" + timeStart
                        + "' AND " +
                        DbHelper.DATE_SESSION + " <= '" + timeEnd + "'";
        Cursor cursor = database.query(
                DbHelper.TABLE_NAME,
                columns,
                selection,
                null,
                null,
                null,
                DbHelper.DATE_SESSION,
                null);
        List<Map> trainingList = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            String dateSession;
            String wodLevel;
            String wod;
            do {
                dateSession = String.valueOf(cursor.getLong(cursor.getColumnIndex(DbHelper.DATE_SESSION)));
                wodLevel = cursor.getString(cursor.getColumnIndex(DbHelper.WOD_LEVEL));
                wod = cursor.getString(cursor.getColumnIndex(DbHelper.WOD));
                Map<String, String> training = new HashMap<>();
                training.put(DATE_SESSION, dateSession);
                if (wodLevel != null) {
                    training.put(WOD_LEVEL, wodLevel);
                } else {
                    training.put(WOD_LEVEL, String.valueOf(EMPTY_VALUE));
                }
                if (wod != null) {
                    training.put(WOD, wod);
                } else {
                    training.put(WOD, String.valueOf(EMPTY_VALUE));
                }
                trainingList.add(training);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return trainingList;
    }

    private static final class DbHelper {

        final static String TABLE_NAME = "calendarWod";
        final static String DATE_SESSION = "date_session";
        final static String WOD_LEVEL = "wodLevel";
        final static String WARM_UP = "warm_up";
        final static String SKILL = "skill";
        final static String WOD = "wod";
        final static String OBJECT_ID = "object_id";
    }
}
