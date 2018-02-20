package ru.lizzzi.crossfit_rekord;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.persistence.DataQueryBuilder;

import java.util.List;
import java.util.Map;

/*
  Created by Liza on 11.10.2017.
 */

public class Table_Fragment extends Fragment{

    private ProgressBar mProgressBar;
    ListView lvItemsInStorehouse;
    View v;
    RecyclerAdapter_Table adapter;
    List<Map> result;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_table, container, false);

        Button button_monday= v.findViewById(R.id.day_1);
        Button button_tuesday= v.findViewById(R.id.day_2);
        Button button_wednesday= v.findViewById(R.id.day_3);
        Button button_thursday= v.findViewById(R.id.day_4);
        Button button_friday= v.findViewById(R.id.day_5);
        Button button_saturday= v.findViewById(R.id.day_6);
        Button button_sunday= v.findViewById(R.id.day_7);
        mProgressBar = v.findViewById(R.id.progressBar);
        lvItemsInStorehouse = v.findViewById(R.id.lvTable);

        StartNewAsyncTask("1");

        button_monday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StartNewAsyncTask("1");
            }
        });

        button_tuesday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StartNewAsyncTask("2");
            }
        });

        button_wednesday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StartNewAsyncTask("3");
            }
        });

        button_thursday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StartNewAsyncTask("4");
            }
        });

        button_friday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StartNewAsyncTask("5");
            }
        });

        button_saturday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StartNewAsyncTask("6");
            }
        });

        button_sunday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StartNewAsyncTask("7");
            }
        });



        return v;
    }

    private void StartNewAsyncTask(String sNumberOfDay){
        final DownloadTable downloadTable = new DownloadTable();
        downloadTable.execute(sNumberOfDay);
    }

    @SuppressLint("StaticFieldLeak")
    private class DownloadTable extends AsyncTask<String,Void, Void>{

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            lvItemsInStorehouse.setVisibility(View.INVISIBLE);
            mProgressBar.setVisibility(View.VISIBLE);

        }

        @Override
        protected Void doInBackground(String... params) {

            String dayofweek = params[0];
            String whereClause = "day_of_week = " + dayofweek;
            DataQueryBuilder queryBuilder = DataQueryBuilder.create();
            queryBuilder.setWhereClause(whereClause);
            queryBuilder.setSortBy("start_time");
            /*
            setPageSize(20)  - пока использую этот метод. Но в будущем надо бы переделать
            корректно на динамику.
             */
            queryBuilder.setPageSize(20);
            result = Backendless.Data.of("Table").find(queryBuilder);
            if (result != null){
                adapter = new RecyclerAdapter_Table(getContext(), result, R.layout.item_lv_table);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            super.onPostExecute(result);

            mProgressBar.setVisibility(View.INVISIBLE);
            if (adapter.getCount() > 0){
                lvItemsInStorehouse.setAdapter(adapter);
                lvItemsInStorehouse.setVisibility(View.VISIBLE);
            }else {
                Toast.makeText(getContext(), "Нет данных", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
