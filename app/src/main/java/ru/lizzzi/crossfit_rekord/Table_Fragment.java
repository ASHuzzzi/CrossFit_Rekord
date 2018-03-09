package ru.lizzzi.crossfit_rekord;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.persistence.DataQueryBuilder;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

import ru.lizzzi.crossfit_rekord.adapters.RecyclerAdapter_Table;

/*
  Created by Liza on 11.10.2017.
 */

public class Table_Fragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private ProgressBar mProgressBar;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    ListView lvItemsInTable;
    View v;
    RecyclerAdapter_Table adapter;
    String sNumberOfDay;
    Button button_monday;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_table, container, false);

        button_monday= v.findViewById(R.id.day_1);
        Button button_tuesday= v.findViewById(R.id.day_2);
        Button button_wednesday= v.findViewById(R.id.day_3);
        Button button_thursday= v.findViewById(R.id.day_4);
        Button button_friday= v.findViewById(R.id.day_5);
        Button button_saturday= v.findViewById(R.id.day_6);
        Button button_sunday= v.findViewById(R.id.day_7);
        mProgressBar = v.findViewById(R.id.progressBar);
        lvItemsInTable = v.findViewById(R.id.lvTable);
        mSwipeRefreshLayout = v.findViewById(R.id.swipe_container);
        final LinearLayout layouterror = v.findViewById(R.id.Layout_Error);
        final LinearLayout layoutbuttonDayOfWeek = v.findViewById(R.id.Layout_Button_Day_of_Week);
        Button button_error = v.findViewById(R.id.button5);



        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        if (checkInternet()){
            sNumberOfDay = "1";
            StartNewAsyncTask(sNumberOfDay);
            layouterror.setVisibility(View.INVISIBLE);
            layoutbuttonDayOfWeek.setVisibility(View.VISIBLE);
            mSwipeRefreshLayout.setEnabled(true);
        }else {
            mProgressBar.setVisibility(View.INVISIBLE);
            layouterror.setVisibility(View.VISIBLE);
            layoutbuttonDayOfWeek.setVisibility(View.INVISIBLE);
            mSwipeRefreshLayout.setEnabled(false);
        }

        button_error.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkInternet()){
                    sNumberOfDay = "1";
                    StartNewAsyncTask(sNumberOfDay);
                    layouterror.setVisibility(View.INVISIBLE);
                    layoutbuttonDayOfWeek.setVisibility(View.VISIBLE);
                    mSwipeRefreshLayout.setEnabled(true);
                }else {
                    mProgressBar.setVisibility(View.INVISIBLE);
                    layouterror.setVisibility(View.VISIBLE);
                    layoutbuttonDayOfWeek.setVisibility(View.INVISIBLE);
                    mSwipeRefreshLayout.setEnabled(false);
                }
            }
        });

        button_monday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sNumberOfDay = "1";
                StartNewAsyncTask(sNumberOfDay);
            }
        });

        button_tuesday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sNumberOfDay = "2";
                StartNewAsyncTask(sNumberOfDay);
            }
        });

        button_wednesday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sNumberOfDay = "3";
                StartNewAsyncTask(sNumberOfDay);
            }
        });

        button_thursday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sNumberOfDay = "4";
                StartNewAsyncTask(sNumberOfDay);
            }
        });

        button_friday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sNumberOfDay = "5";
                StartNewAsyncTask(sNumberOfDay);
            }
        });

        button_saturday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sNumberOfDay = "6";
                StartNewAsyncTask(sNumberOfDay);
            }
        });

        button_sunday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sNumberOfDay = "7";
                StartNewAsyncTask(sNumberOfDay);
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
            if(checkInternet()){
                lvItemsInTable.setVisibility(View.INVISIBLE);
                mProgressBar.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected Void doInBackground(String... params) {

            if (checkInternet()) {
                List<Map> result;
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
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            super.onPostExecute(result);


            mProgressBar.setVisibility(View.INVISIBLE);
            if (adapter != null){
                lvItemsInTable.setAdapter(adapter);
                lvItemsInTable.setVisibility(View.VISIBLE);

                if (sNumberOfDay.equals("1")){
                    button_monday.setBackgroundResource(R.color.selectButton);
                }else {
                    button_monday.setBackgroundResource(android.R.drawable.btn_default);
                }
            }else {
                if (checkInternet()){
                    Toast.makeText(getContext(), "Нет подключения", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getContext(), "Нет данных", Toast.LENGTH_SHORT).show();
                }

            }
        }
    }

    public boolean checkInternet() {

        ConnectivityManager cm = (ConnectivityManager)getActivity().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm != null ? cm.getActiveNetworkInfo() : null;
        // проверка подключения
        return activeNetwork != null && activeNetwork.isConnected();

    }

    @Override
    public void onRefresh() {

        if (checkInternet()){
            StartNewAsyncTask(sNumberOfDay);
            mSwipeRefreshLayout.setRefreshing(false);
        }else {
            mSwipeRefreshLayout.setRefreshing(false);
            mSwipeRefreshLayout.showContextMenu();
            Toast.makeText(getContext(), "Нет подключения к сети", Toast.LENGTH_SHORT).show();

        }


        /*new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Отменяем анимацию обновления
                if (checkInternet()){


                }else{
                    mSwipeRefreshLayout.setRefreshing(false);
                }

            }
        }, 6000);*/
    }
}
