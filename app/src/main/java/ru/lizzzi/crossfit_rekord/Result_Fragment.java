package ru.lizzzi.crossfit_rekord;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.io.IOException;
import java.util.ArrayList;

import ru.lizzzi.crossfit_rekord.adapters.RecyclerAdapter_Result;
import ru.lizzzi.crossfit_rekord.data.WodDBHelper;
import ru.lizzzi.crossfit_rekord.data.WodDbContract;


/**
 * A simple {@link Fragment} subclass.
 */
public class Result_Fragment extends Fragment {

    private RecyclerView.Adapter mAdapter;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<Caption_Wod> caption_wod = new ArrayList<>();;
    private WodDBHelper mDbHelper = new WodDBHelper(getContext());
    private ArrayList<String> Item_list = new ArrayList<>();


    public Result_Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_result, container, false);

        Button New_Result = ((Button) view.findViewById(R.id.button_newResult));

        mDbHelper = new WodDBHelper(getContext());

        try {
            mDbHelper.createDataBase();
        } catch (IOException ioe) {
            throw new Error("Unable to create database");
        }

        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] columns = new  String[]{WodDbContract.DBCaptionWod.Column_Day, WodDbContract.DBCaptionWod.Column_Month,
                WodDbContract.DBCaptionWod.Column_CaptionWod,WodDbContract.DBCaptionWod.Column_Level,
                WodDbContract.DBCaptionWod.Column_Result};
        Cursor cursor = db.query(WodDbContract.DBCaptionWod.TABLE_NAME,
                columns,
                null,
                null,
                null,
                null,
                null);
        if (cursor !=null && cursor.moveToFirst()){
            int i = 0;
            do {
                String day = cursor.getString(cursor.getColumnIndex(WodDbContract.DBCaptionWod.Column_Day));
                String month = cursor.getString(cursor.getColumnIndex(WodDbContract.DBCaptionWod.Column_Month));
                String caption = cursor.getString(cursor.getColumnIndex(WodDbContract.DBCaptionWod.Column_CaptionWod));
                String level = cursor.getString(cursor.getColumnIndex(WodDbContract.DBCaptionWod.Column_Level));
                String time = cursor.getString(cursor.getColumnIndex(WodDbContract.DBCaptionWod.Column_Result));

                caption_wod.add(new Caption_Wod(day, month,caption,level,time));
            }while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        mRecyclerView = (RecyclerView) view.findViewById(R.id.lv_caption_result);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new RecyclerAdapter_Result(caption_wod, new Listener_Result());
        mRecyclerView.setAdapter(mAdapter);

        New_Result.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = null;
                Class fragmentClass;
                fragmentClass = New_Result_F1_Fragment.class;
                try {
                    fragment = (Fragment) fragmentClass.newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction ft = fragmentManager.beginTransaction();
                ft.replace(R.id.container, fragment);
                ft.addToBackStack(null);
                ft.commit();
            }
        });



        // Inflate the layout for this fragment
        return view;
    }

}
