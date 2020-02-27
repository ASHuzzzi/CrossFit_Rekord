package ru.lizzzi.crossfit_rekord.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import ru.lizzzi.crossfit_rekord.items.NotificationItem;

public class SQLiteStorageNotification extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Notification.db";
    private static final int DATABASE_VERSION = 2;
    private SQLiteDatabase database;

    public SQLiteStorageNotification(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        String SQL_CREATE_TABLE =
                "CREATE TABLE "
                + Notification.TABLE_NAME + " ("
                + Notification.COLUMN_CODE_NOTE + " INTEGER NOT NULL, "
                + Notification.COLUMN_DATE_NOTE + " INTEGER NOT NULL, "
                + Notification.COLUMN_HEADER + " TEXT, "
                + Notification.COLUMN_NUMBER_NOTE + " INTEGER, "
                + Notification.COLUMN_TEXT + " TEXT, "
                + Notification.COLUMN_VIEWED + " BOOLEAN);";
        database.execSQL(SQL_CREATE_TABLE);
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
        String[] columns = new  String[] { "MAX(" + Notification.COLUMN_DATE_NOTE + ")" };
        Cursor cursor = database.query(
                true,
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

    public void saveNotification(NotificationItem notificationItems) {
        database = this.getWritableDatabase();
        ContentValues newValues = new ContentValues();
        newValues.put(Notification.COLUMN_DATE_NOTE, notificationItems.getDate());
        newValues.put(Notification.COLUMN_HEADER, notificationItems.getHeader());
        newValues.put(Notification.COLUMN_TEXT, notificationItems.getText());
        newValues.put(Notification.COLUMN_CODE_NOTE, notificationItems.getCodeNote());
        newValues.put(Notification.COLUMN_VIEWED, notificationItems.isView());
        database.insert(Notification.TABLE_NAME, null, newValues);
        database.close();
    }

    public List<NotificationItem> getNotification() {
        database = this.getReadableDatabase();
        List<NotificationItem> notificationList = new ArrayList<>();
        Cursor cursor = database.query(
                true,
                Notification.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                Notification.COLUMN_DATE_NOTE + " DESC",
                null);
        if (cursor != null && cursor.moveToFirst()) {
            long dateNote;
            String header;
            String text;
            boolean view;
            NotificationItem notificationItems;

            do {
                dateNote = cursor.getLong(cursor.getColumnIndex(Notification.COLUMN_DATE_NOTE));
                header = cursor.getString(cursor.getColumnIndex(Notification.COLUMN_HEADER));
                text = cursor.getString(cursor.getColumnIndex(Notification.COLUMN_TEXT));
                view = cursor.getInt(cursor.getColumnIndex(Notification.COLUMN_VIEWED)) > 0;
                notificationItems = new NotificationItem("", dateNote, header, text, view);
                notificationList.add(notificationItems);
            } while (cursor.moveToNext());
            cursor.close();
        }
        database.close();
        return notificationList;
    }

    public NotificationItem getNotification(long date) {
        database = this.getReadableDatabase();
        NotificationItem notification = new NotificationItem("", 0, "", "", false);
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
            String header;
            String text;
            boolean view;
            do {
                header = cursor.getString(cursor.getColumnIndex(Notification.COLUMN_HEADER));
                text = cursor.getString(cursor.getColumnIndex(Notification.COLUMN_TEXT));
                view = cursor.getInt(cursor.getColumnIndex(Notification.COLUMN_VIEWED)) > 0;
                notification = new NotificationItem("", date, header, text, view);
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
        database.close();
        return notification;
    }

    public void setNotificationViewed(long date) {
        database = this.getWritableDatabase();
        ContentValues statusNotification = new ContentValues();
        statusNotification.put(Notification.COLUMN_VIEWED, true);
        database.update(
                Notification.TABLE_NAME,
                statusNotification,
                Notification.COLUMN_DATE_NOTE + "= '" + date + "'",
                null);
        database.close();
    }

    public int getQuantityUnreadNotifications() {
        database = this.getReadableDatabase();
        int quantityUnreadNotifications = 0;
        String[] columns = new String[] { "COUNT(" + Notification.COLUMN_VIEWED + ")" };
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
            quantityUnreadNotifications = cursor.getInt(0);
        }
        if (cursor != null) {
            cursor.close();
        }
        database.close();
        return quantityUnreadNotifications;
    }

    public void deleteNotification(long date) {
        database = this.getReadableDatabase();
        database.delete(
                Notification.TABLE_NAME,
                Notification.COLUMN_DATE_NOTE + "= '" + date + "'",
                null);
        database.close();
    }

    public static final class Notification {
        final static String TABLE_NAME = "notification";
        final static String COLUMN_CODE_NOTE = "codeNote";
        final static String COLUMN_DATE_NOTE = "dateNote";
        final static String COLUMN_HEADER = "header";
        final static String COLUMN_NUMBER_NOTE = "numberNote";
        final static String COLUMN_TEXT = "text";
        final static String COLUMN_VIEWED = "viewed";
    }
}