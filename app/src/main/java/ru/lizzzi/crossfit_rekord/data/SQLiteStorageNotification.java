package ru.lizzzi.crossfit_rekord.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SQLiteStorageNotification extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Notification.db";
    private static final int DATABASE_VERSION = 2;
    private SQLiteDatabase database;

    public SQLiteStorageNotification(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        String SQL_CREATE_ROWER_TABLE =
                "CREATE TABLE "
                + Notification.TABLE_NAME + " ("
                + Notification.COLUMN_CODE_NOTE + " INTEGER NOT NULL, "
                + Notification.COLUMN_DATE_NOTE + " INTEGER NOT NULL, "
                + Notification.COLUMN_HEADER + " TEXT, "
                + Notification.COLUMN_NUMBER_NOTE + " INTEGER, "
                + Notification.COLUMN_TEXT + " TEXT, "
                + Notification.COLUMN_VIEWED + " BOOLEAN);";
        database.execSQL(SQL_CREATE_ROWER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            database.execSQL("DROP TABLE notification");
            onCreate(database);
        }
    }

    @Override
    public synchronized void close() {
        if(database != null)
            database.close();
        super.close();
    }

    public long dateLastCheck() {
        database = this.getReadableDatabase();
        long lastDateCheck = 0;
        String[] columns = new  String[] {
                "MAX(" +
                Notification.COLUMN_DATE_NOTE + ")"
        };
        Cursor cursor = database.query(true,
                Notification.TABLE_NAME,
                columns,
                null,
                null,
                null,
                null,
                null,
                null);
        if (cursor !=null && cursor.moveToFirst()) {
            lastDateCheck = cursor.getLong(0);
        }
        if (cursor != null) {
            cursor.close();
        }
        database.close();
        return lastDateCheck;
    }

    public void saveNotification(
            long dateNote,
            String header,
            String text,
            String codeNote,
            boolean viewed) {
        database = this.getWritableDatabase();
        ContentValues newValues = new ContentValues();
        newValues.put(Notification.COLUMN_DATE_NOTE, dateNote);
        newValues.put(Notification.COLUMN_HEADER, header);
        newValues.put(Notification.COLUMN_TEXT, text);
        newValues.put(Notification.COLUMN_CODE_NOTE, codeNote);
        newValues.put(Notification.COLUMN_VIEWED, viewed);
        database.insert(Notification.TABLE_NAME, null, newValues);
        database.close();
    }

    public List<Map<String, Object>> loadNotification() {
        database = this.getReadableDatabase();
        List<Map<String, Object>> listNotification = new ArrayList<>();
        Cursor cursor = database.query(true,
                Notification.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                Notification.COLUMN_DATE_NOTE + " DESC",
                null);
        if (cursor != null && cursor.moveToFirst()) {

            String dateNote;
            String header;
            String text;
            boolean viewed;
            do {
                Map<String, Object> mapNotification = new HashMap<>();
                dateNote = cursor.getString(cursor.getColumnIndex(Notification.COLUMN_DATE_NOTE));
                header = cursor.getString(cursor.getColumnIndex(Notification.COLUMN_HEADER));
                text = cursor.getString(cursor.getColumnIndex(Notification.COLUMN_TEXT));
                viewed = cursor.getInt(cursor.getColumnIndex(Notification.COLUMN_VIEWED)) > 0;

                mapNotification.put("dateNote", dateNote);
                mapNotification.put("header", header);
                mapNotification.put("text", text);
                mapNotification.put("viewed", viewed);
                listNotification.add(mapNotification);
            } while (cursor.moveToNext());
            cursor.close();
        }
        database.close();
        return listNotification;
    }

    public ArrayList<String> getNotification(long date) {
        database = this.getReadableDatabase();
        ArrayList<String> notificationProperty = new ArrayList<>();
        String[] columns = new  String[] {
                Notification.COLUMN_HEADER,
                Notification.COLUMN_TEXT,
                Notification.COLUMN_VIEWED
        };
        Cursor cursor = database.query(
                Notification.TABLE_NAME,
                columns,
                Notification.COLUMN_DATE_NOTE + "= '" + date + "'",
                null,
                null,
                null,
                null);
        if (cursor !=null && cursor.moveToFirst()) {
            do {
                for (String value : cursor.getColumnNames()) {
                    String property = cursor.getString(cursor.getColumnIndex(value));
                    notificationProperty.add(property);
                }
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
        database.close();
        return notificationProperty;
    }

    public void updateStatusNotification(long date, boolean isViewed) {
        database = this.getWritableDatabase();
        ContentValues statusNotification = new ContentValues();
        statusNotification.put("viewed", isViewed);
        database.update(
                Notification.TABLE_NAME,
                statusNotification,
                Notification.COLUMN_DATE_NOTE + "= '" + date + "'",
                null);
        database.close();
    }

    public int getUnreadNotifications() {
        database = this.getReadableDatabase();
        int lastDateCheck = 0;
        String[] columns = new String[] {
                "COUNT(" +
                Notification.COLUMN_VIEWED +
                ")"
        };
        Cursor cursor = database.query(true,
                Notification.TABLE_NAME,
                columns,
                Notification.COLUMN_VIEWED + "= '0'",
                null,
                null,
                null,
                null,
                null);
        if (cursor != null && cursor.moveToFirst()) {
            lastDateCheck = cursor.getInt(0);
        }
        if (cursor != null) {
            cursor.close();
        }
        database.close();
        return lastDateCheck;
    }

    public void deleteNotification(long date) {
        database = this.getReadableDatabase();
        database.delete(
                Notification.TABLE_NAME,
                Notification.COLUMN_DATE_NOTE + "= '" + date + "'",
                null
        );
        database.close();
    }

    public static final class Notification implements BaseColumns {
        final static String TABLE_NAME = "notification";
        final static String COLUMN_CODE_NOTE = "codeNote";
        final static String COLUMN_DATE_NOTE = "dateNote";
        final static String COLUMN_HEADER = "header";
        final static String COLUMN_NUMBER_NOTE = "numberNote";
        final static String COLUMN_TEXT = "text";
        final static String COLUMN_VIEWED = "viewed";
    }
}