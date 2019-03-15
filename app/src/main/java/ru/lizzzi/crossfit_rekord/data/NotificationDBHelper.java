package ru.lizzzi.crossfit_rekord.data;

import android.annotation.SuppressLint;
import android.content.ContentValues;
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
import java.util.List;
import java.util.Map;

import ru.lizzzi.crossfit_rekord.data.NotificationDBContract.Notification;

public class NotificationDBHelper extends SQLiteOpenHelper {

    // путь к базе данных вашего приложения
    @SuppressLint("SdCardPath")
    private static String DB_PATH = "/data/data/ru.lizzzi.crossfit_rekord/databases/";
    private static String DB_NAME = "Notification.db";
    private SQLiteDatabase myDataBase;
    private final Context mContext;

    public NotificationDBHelper(Context context) {
        super(context, DB_NAME, null, 1);
        this.mContext = context;
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
        InputStream myInput = mContext.getAssets().open("db/" + DB_NAME);

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
        myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        myDataBase.disableWriteAheadLogging();
    }

    @Override
    public synchronized void close() {
        if(myDataBase != null)
            myDataBase.close();
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public long datelastcheck(){
        myDataBase = this.getReadableDatabase();
        long stLastDateCheck = 0;
        String[] columns = new  String[]{"MAX(" +
                NotificationDBContract.Notification.columnDateNote + ")"};
        Cursor cursor = myDataBase.query(true,
                NotificationDBContract.Notification.TABLE_NAME,
                columns,
                null,
                null,
                null,
                null,
                null,
                null);
        if (cursor !=null && cursor.moveToFirst()){
            do {

                for (String cn : cursor.getColumnNames()) {
                    stLastDateCheck = cursor.getLong(cursor.getColumnIndex(cn));
                }
            }while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
        myDataBase.close();
        return stLastDateCheck;
    }

    public void saveNotification(long dateNote, String header, String text, String codeNote, int viewed){
        myDataBase = this.getWritableDatabase();
        ContentValues newValues = new ContentValues();

        newValues.put(Notification.columnDateNote, dateNote);
        newValues.put(Notification.columnHeader, header);
        newValues.put(Notification.columnText, text);
        newValues.put(Notification.columnCodeNote, codeNote);
        newValues.put(Notification.columnViewed, viewed);
        myDataBase.insert(Notification.TABLE_NAME, null, newValues);
        myDataBase.close();
    }

    public List<Map<String, Object>> loadNotification() {
        myDataBase = this.getReadableDatabase();
        List<Map<String, Object>> listNotification = new ArrayList<>();

        Cursor cursor = myDataBase.query(true,
                Notification.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                Notification.columnDateNote + " DESC",
                null);
        if (cursor != null && cursor.moveToFirst()) {

            String stDateNote;
            String stHeader;
            String stText;
            String stViewed;


            do {
                Map<String, Object> mapNotification = new HashMap<>();
                stDateNote = cursor.getString(cursor.getColumnIndex(Notification.columnDateNote));
                stHeader = cursor.getString(cursor.getColumnIndex(Notification.columnHeader));
                stText = cursor.getString(cursor.getColumnIndex(Notification.columnText));
                stViewed = cursor.getString(cursor.getColumnIndex(Notification.columnViewed));

                mapNotification.put("dateNote", stDateNote);
                mapNotification.put("header", stHeader);
                mapNotification.put("text", stText);
                mapNotification.put("viewed", stViewed);
                listNotification.add(mapNotification);
            } while (cursor.moveToNext());
            cursor.close();
        }

        return listNotification;
    }

    public ArrayList<String> selectTextNotification(long date){
        myDataBase = this.getReadableDatabase();

        ArrayList<String> arrListNotification = new ArrayList<>();
        String[]  columns = new  String[]{Notification.columnText, Notification.columnViewed};
        Cursor cursor = myDataBase.query(Notification.TABLE_NAME,
                columns,
                Notification.columnDateNote + "= '" + date + "'",
                null,
                null,
                null,
                null);
        if (cursor !=null && cursor.moveToFirst()){
            do {
                for (String cn : cursor.getColumnNames()) {

                    String stText = cursor.getString(cursor.getColumnIndex(cn));
                    arrListNotification.add(stText);

                }

            }while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
        return arrListNotification;
    }

    public void updateStatusNotification(long date, int status){
        myDataBase = this.getWritableDatabase();

        ContentValues statusNotification = new ContentValues();
        statusNotification.put("viewed", status);
        myDataBase.update(Notification.TABLE_NAME,
                statusNotification,
                Notification.columnDateNote + "= '" + date + "'",
                null);
    }

    public int countNotification() {
        myDataBase = this.getReadableDatabase();
        int stLastDateCheck = 0;
        String[] columns = new String[]{"COUNT(" +
                Notification.columnViewed + ")"};
        Cursor cursor = myDataBase.query(true,
                NotificationDBContract.Notification.TABLE_NAME,
                columns,
                Notification.columnViewed + "= '0'",
                null,
                null,
                null,
                null,
                null);
        if (cursor != null && cursor.moveToFirst()) {
            do {

                for (String cn : cursor.getColumnNames()) {
                    stLastDateCheck = cursor.getInt(cursor.getColumnIndex(cn));
                }
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
        return stLastDateCheck;
    }

    public void deleteNotification(long date){
        myDataBase = this.getReadableDatabase();

        myDataBase.delete(
                Notification.TABLE_NAME,
                Notification.columnDateNote + "= '" + date + "'",
                null
        );
    }

    //проверяем наличие таблицы в базе. если её нет, то повторяем копирование.
    public boolean checkTable(){
        myDataBase = this.getReadableDatabase();
        try {
            Cursor cursor = myDataBase.query(Notification.TABLE_NAME,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null);

            if (cursor != null) {
                cursor.close();
            }
            return true;
        } catch (SQLException e) {
            try {
                copyDataBase();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return false;
        }
    }
}