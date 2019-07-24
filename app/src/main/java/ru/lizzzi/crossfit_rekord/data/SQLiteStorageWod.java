package ru.lizzzi.crossfit_rekord.data;

import android.annotation.SuppressLint;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SQLiteStorageWod extends SQLiteOpenHelper {

    // путь к базе данных вашего приложения
    @SuppressLint("SdCardPath")
    private static final String DB_PATH = "/data/data/ru.lizzzi.crossfit_rekord/databases/";
    private static final String DB_NAME = "CalendarWod.db";
    private SQLiteDatabase database;
    private final Context context;

    public SQLiteStorageWod(Context context) {
        super(context, DB_NAME, null, 1);
        this.context = context;
    }

    /**
     * Создает пустую базу данных и перезаписывает ее нашей собственной базой
     * */
    public void createDataBase() {

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
            checkDB.disableWriteAheadLogging();
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

    public List<Date> selectDates(String objectId, long timeStart, long timeNow){
        database = this.getReadableDatabase();

        ArrayList<Date> arrListDates = new ArrayList<>();
        String[] columns = new String[]{dbCalendarWod.columnDateSession};
        String selection = dbCalendarWod.columnObjectId + "= '" + objectId + "' AND "
                + dbCalendarWod.columnDateSession + " BETWEEN " + timeStart + " AND "
                + timeNow;
        Cursor cursor = database.query(
                dbCalendarWod.TABLE_NAME,
                columns,
                selection,
                null,
                null,
                null,
                null);
        if (cursor !=null && cursor.moveToFirst()){
            do {
                Date dateFromDb = null;
                SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());

                for (String cn : cursor.getColumnNames()) {
                    long sDates = Long.parseLong(cursor.getString(cursor.getColumnIndex(cn)));
                    String ssDate = sdf.format(sDates);
                    try {
                        dateFromDb = sdf.parse(ssDate);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    arrListDates.add(dateFromDb);
                }

            }while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
        return arrListDates;
    }

    public void saveDates(String userId, List<Date> dates) {
        database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        long time;
        for (int i = 0; i < dates.size(); i++) {
            time = dates.get(i).getTime();
            values.put(dbCalendarWod.columnObjectId, userId);
            values.put(dbCalendarWod.columnDateSession, time);
            database.insert(dbCalendarWod.TABLE_NAME, null, values);
            values.clear();
        }
        database.close();
    }

    public void saveDate(String userId, long date) {
        database = this.getWritableDatabase();
        ContentValues newValues = new ContentValues();
        newValues.put(dbCalendarWod.columnObjectId, userId);
        newValues.put(dbCalendarWod.columnDateSession, date);
        database.insert(dbCalendarWod.TABLE_NAME, null, newValues);
        database.close();
    }

    public void deleteDate(String stObjectId, long lDate) {
        database = this.getWritableDatabase();

        String selection =
                dbCalendarWod.columnObjectId +
                "= '" +
                stObjectId +
                "' AND " +
                dbCalendarWod.columnDateSession +
                "= '" +
                lDate +
                "'";

        database.delete(
                dbCalendarWod.TABLE_NAME,
                selection,
                null
        );
        database.close();
    }

    public static final class dbCalendarWod implements BaseColumns {

        final static String TABLE_NAME = "calendarWod";
        final static String columnDateSession = "dateSession";
        final static String columnSc = "Sc";
        final static String columnRx = "Rx";
        final static String columnRxPlus = "Rx+";
        final static String columnWarmup = "warmup";
        final static String columnSkill = "skill";
        final static String columnWod = "wod";
        final static String columnObjectId = "objectId";
    }
}