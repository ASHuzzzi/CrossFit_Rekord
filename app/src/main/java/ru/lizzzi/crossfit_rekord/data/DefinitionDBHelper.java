package ru.lizzzi.crossfit_rekord.data;

import android.annotation.SuppressLint;
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

public class DefinitionDBHelper  extends SQLiteOpenHelper{

    // путь к базе данных вашего приложения
    @SuppressLint("SdCardPath")
    private static final String DB_PATH = "/data/data/ru.lizzzi.crossfit_rekord/databases/";
    private static final String DB_NAME = "DefinitionDirectory.db";
    private SQLiteDatabase myDataBase;
    private final Context mContext;

    public DefinitionDBHelper(Context context) {
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

    // Здесь можно добавить вспомогательные методы для доступа и получения данных из БД
    // вы можете возвращать курсоры через "return myDataBase.query(....)", это облегчит их использование
    // в создании адаптеров для ваших view
    public ArrayList<String> selectCharacter(){
        myDataBase = this.getReadableDatabase();

        ArrayList<String> arrListCharacter = new ArrayList<>();

        String[] columns = new  String[]{DefinitionDbContarct.DBdefinition.Column_character};
        Cursor cursor = myDataBase.query(true,
                DefinitionDbContarct.DBdefinition.TABLE_NAME,
                columns,
                null,
                null,
                null,
                null,
                null,
                null);
        if (cursor !=null && cursor.moveToFirst()){
            do {
                String name = "";
                for (String cn : cursor.getColumnNames()) {
                    name = cursor.getString(cursor.getColumnIndex(cn));
                }
                arrListCharacter.add(name);
            }while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
        return arrListCharacter;
    }

    public ArrayList<String> selectTermin(String character){
        myDataBase = this.getReadableDatabase();

        ArrayList<String> arrListTermin = new ArrayList<>();
        String[] columns = new  String[]{DefinitionDbContarct.DBdefinition.Column_termin};
        Cursor cursor = myDataBase.query(DefinitionDbContarct.DBdefinition.TABLE_NAME,
                columns,
                DefinitionDbContarct.DBdefinition.Column_character + "= '" + character + "'",
                null,
                null,
                null,
                null);
        if (cursor !=null && cursor.moveToFirst()){
            do {
                String name = "";
                for (String cn : cursor.getColumnNames()) {
                    name = cursor.getString(cursor.getColumnIndex(cn));
                }
                arrListTermin.add(name);
            }while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
        return arrListTermin;
    }

    public ArrayList<String> selectDescription(String character){
        myDataBase = this.getReadableDatabase();

        ArrayList<String> arrListDefinition = new ArrayList<>();
        String[]  columns = new  String[]{DefinitionDbContarct.DBdefinition.Column_description};
        Cursor cursor = myDataBase.query(DefinitionDbContarct.DBdefinition.TABLE_NAME,
                columns,
                DefinitionDbContarct.DBdefinition.Column_character + "= '" + character + "'",
                null,
                null,
                null,
                null);
        if (cursor !=null && cursor.moveToFirst()){
            do {
                String name = "";
                for (String cn : cursor.getColumnNames()) {
                    name = cursor.getString(cursor.getColumnIndex(cn));
                }
                arrListDefinition.add(name);
            }while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
        return arrListDefinition;
    }
}
