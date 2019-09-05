package ru.lizzzi.crossfit_rekord.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SQLiteStorageWod extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "CalendarWod.db";
    private static final int DATABASE_VERSION = 2;
    private SQLiteDatabase database;

    public SQLiteStorageWod(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        String SQL_CREATE_TABLE =
                "CREATE TABLE " + DbHelper.TABLE_NAME + " ("
                        + DbHelper.DATE_SESSION + " INTEGER NOT NULL, "
                        + DbHelper.OBJECT_ID + " TEXT, "
                        + DbHelper.WARM_UP + " TEXT, "
                        + DbHelper.SKILL + " TEXT, "
                        + DbHelper.WOD + " TEXT, "
                        + DbHelper.SC + " TEXT, "
                        + DbHelper.RX + " TEXT, "
                        + DbHelper.RX_PLUS + " TEXT);";
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
                    long dateInMilliseconds =
                            Long.parseLong(cursor.getString(cursor.getColumnIndex(columnName)));
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

    public void saveDates(String userId, List<Date> dates) {
        database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        long time;
        for (int i = 0; i < dates.size(); i++) {
            time = dates.get(i).getTime();
            values.put(DbHelper.OBJECT_ID, userId);
            values.put(DbHelper.DATE_SESSION, time);
            database.insert(DbHelper.TABLE_NAME, null, values);
            values.clear();
        }
        database.close();
    }

    public void saveDate(String userId, long date) {
        database = this.getWritableDatabase();
        ContentValues newValues = new ContentValues();
        newValues.put(DbHelper.OBJECT_ID, userId);
        newValues.put(DbHelper.DATE_SESSION, date);
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

    public static final class DbHelper {

        final static String TABLE_NAME = "calendar_wod";
        final static String DATE_SESSION = "date_session";
        final static String SC = "sc";
        final static String RX = "rx";
        final static String RX_PLUS = "rx_plus";
        final static String WARM_UP = "warm_up";
        final static String SKILL = "skill";
        final static String WOD = "wod";
        final static String OBJECT_ID = "object_id";
    }
}
