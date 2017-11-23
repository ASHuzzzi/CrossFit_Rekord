package ru.lizzzi.crossfit_rekord;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import ru.lizzzi.crossfit_rekord.data.DefinitionDBHelper;
import ru.lizzzi.crossfit_rekord.data.DefinitionDbContarct;
import ru.lizzzi.crossfit_rekord.data.WodDBHelper;
import ru.lizzzi.crossfit_rekord.data.WodDbContract;


/**
 * A simple {@link Fragment} subclass.
 */
public class New_Result_F1_Fragment extends Fragment {

    Date date = new Date();
    EditText etDay;
    EditText etMonth;
    EditText etTime;
    AutoCompleteTextView autoCompleteTextView;
    EditText etLevel;
    EditText etResult;
    SQLiteDatabase db2;
    private DefinitionDBHelper mDbHelper = new DefinitionDBHelper(getContext());
    private WodDBHelper mDbHelper2 = new WodDBHelper(getContext());
    private ArrayList<String> Item_list = new ArrayList<>();


    public New_Result_F1_Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_result_f1, container, false);
        // Inflate the layout for this fragment

        etDay = (EditText) view.findViewById(R.id.etDay);
        etMonth = (EditText) view.findViewById(R.id.etMonth);
        etTime = (EditText) view.findViewById(R.id.etTime);
        etLevel = (EditText) view.findViewById(R.id.etLevel);
        etResult = (EditText) view.findViewById(R.id.etResult);

        Button button = (Button) view.findViewById(R.id.button);

        SimpleDateFormat sdf_day = new SimpleDateFormat("dd");
        SimpleDateFormat sdf_Month = new SimpleDateFormat("MMMM");
        SimpleDateFormat sdf_Year = new SimpleDateFormat("yyyy");

        String currentDay = sdf_day.format(date);
        String currentMonth = sdf_Month.format(date);
        final String currentYear = sdf_Year.format(date);

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

        db2 = mDbHelper2.getReadableDatabase();

        autoCompleteTextView = (AutoCompleteTextView) view.findViewById(R.id.aCTVWodCaption);
        autoCompleteTextView.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, Item_list));

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int Day = Integer.parseInt(etDay.getText().toString());
                String Month = etMonth.getText().toString();
                String Year = currentYear;
                String Time = etTime.getText().toString();
                String WodCaption = autoCompleteTextView.getText().toString();
                String Level = etLevel.getText().toString();
                String Result = etResult.getText().toString();
                String WodKey = Day + Month + Year + WodCaption;

                ContentValues values = new ContentValues();
                values.put(WodDbContract.DBCaptionWod.Column_Day, Day);
                values.put(WodDbContract.DBCaptionWod.Column_Month, Month);
                values.put(WodDbContract.DBCaptionWod.Column_Year, Year);
                values.put(WodDbContract.DBCaptionWod.Column_Time, Time);
                values.put(WodDbContract.DBCaptionWod.Column_CaptionWod, WodCaption);
                values.put(WodDbContract.DBCaptionWod.Column_Level, Level);
                values.put(WodDbContract.DBCaptionWod.Column_WodKey, WodKey);
                values.put(WodDbContract.DBCaptionWod.Column_Result, Result);

                db2.insert(WodDbContract.DBCaptionWod.TABLE_NAME,
                        null,
                        values);

                Fragment fragment = null;
                Class fragmentClass;
                fragmentClass = New_Result_F2_Fragment.class;
                New_Result_F2_Fragment yfc = new New_Result_F2_Fragment();
                Bundle bundle = new Bundle();
                bundle.putString("tag", WodKey);
                yfc.setArguments(bundle);

                try {
                    fragment = (Fragment) fragmentClass.newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.popBackStack();
                FragmentTransaction ft = fragmentManager.beginTransaction();
                ft.replace(R.id.container, yfc);
                ft.addToBackStack(null);
                ft.commit();
            }
        });

        return view;
    }

}
