package ru.lizzzi.crossfit_rekord;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import ru.lizzzi.crossfit_rekord.data.DefinitionDBHelper;
import ru.lizzzi.crossfit_rekord.data.DefinitionDbContarct;
import ru.lizzzi.crossfit_rekord.data.WodDBHelper;


/**
 * A simple {@link Fragment} subclass.
 */
public class New_Result_Fragment extends Fragment {

    Date date = new Date();
    private DefinitionDBHelper mDbHelper = new DefinitionDBHelper(getContext());
    private WodDBHelper mDbHelper2 = new WodDBHelper(getContext());
    private ArrayList<String> Item_list = new ArrayList<>();


    public New_Result_Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new__result, container, false);
        // Inflate the layout for this fragment

        EditText etDay = (EditText) view.findViewById(R.id.etDay);
        EditText etMonth = (EditText) view.findViewById(R.id.etMonth);
        EditText etWodCaption = (EditText) view.findViewById(R.id.etWodCaption);
        EditText etTime = (EditText) view.findViewById(R.id.etTime);
        EditText etLevel = (EditText) view.findViewById(R.id.etLevel);
        EditText etResult = (EditText) view.findViewById(R.id.etResult);

        SimpleDateFormat sdf_day = new SimpleDateFormat("dd");
        SimpleDateFormat sdf_Month = new SimpleDateFormat("MMMM");

        String currentDay = sdf_day.format(date);
        String currentMonth = sdf_Month.format(date);

        etDay.setText(currentDay);
        etMonth.setText(currentMonth);

        mDbHelper = new DefinitionDBHelper(getContext());


        try {
            mDbHelper.createDataBase();
        } catch (IOException ioe) {
            throw new Error("Unable to create database");
        }

        //mDbHelper.openDataBase();
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        Item_list.clear();

        String[] columns = new  String[]{DefinitionDbContarct.DBdefinition.Column_termin};
        Cursor cursor = db.query(DefinitionDbContarct.DBdefinition.TABLE_NAME,
                columns,
                DefinitionDbContarct.DBdefinition.Column_attribute + "= 'protocol'",
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
                Item_list.add(name);
            }while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        mDbHelper2 = new WodDBHelper(getContext());
        try {
            mDbHelper2.createDataBase();
        } catch (IOException ioe) {
            throw new Error("Unable to create database");
        }


        return view;
    }

}
