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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ru.lizzzi.crossfit_rekord.data.CalendarWodDBContract.dbCaleendarWod;

public class CalendarWodDBHelper extends SQLiteOpenHelper {

    // путь к базе данных вашего приложения
    @SuppressLint("SdCardPath")
    private static final String DB_PATH = "/data/data/ru.lizzzi.crossfit_rekord/databases/";
    private static final String DB_NAME = "CalendarWod.db";
    private SQLiteDatabase myDataBase;
    private final Context mContext;

    public CalendarWodDBHelper(Context context) {
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
        String outFileName = DB_PATH + DB_NAME;
        //String outFileName = DB_PATH;

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

    public List<Date> selectDates(String objectId, long timestart, long timenow){
        myDataBase = this.getReadableDatabase();

        ArrayList<Date> arrListDates = new ArrayList<>();
        String[] columns = new String[]{dbCaleendarWod.columnDateSession};
        String selection = dbCaleendarWod.columnObjectId + "= '" + objectId + "' AND "
                + dbCaleendarWod.columnDateSession + " BETWEEN " + timestart + " AND "
                + timenow;
        Cursor cursor = myDataBase.query(
                dbCaleendarWod.TABLE_NAME,
                columns,
                selection,
                null,
                null,
                null,
                null);
        if (cursor !=null && cursor.moveToFirst()){
            do {
                Date dateFromDb = null;
                @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");

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

    public void saveDates(String stObjectId, long lDate){
        myDataBase = this.getWritableDatabase();
        ContentValues newValues = new ContentValues();

        newValues.put(dbCaleendarWod.columnObjectId, stObjectId);
        newValues.put(dbCaleendarWod.columnDateSession, lDate);
        myDataBase.insert(dbCaleendarWod.TABLE_NAME, null, newValues);
    }

    public void deleteDate(String stObjectId, long lDate){
        myDataBase = this.getWritableDatabase();

        String selection = dbCaleendarWod.columnObjectId + "= '" + stObjectId + "' AND "
                + dbCaleendarWod.columnDateSession + "= '" + lDate + "'";

        //String[] columns = new String[]{dbCaleendarWod.columnDateSession};

        myDataBase.delete(
                dbCaleendarWod.TABLE_NAME,
                selection,
                null
        );
    }
}
