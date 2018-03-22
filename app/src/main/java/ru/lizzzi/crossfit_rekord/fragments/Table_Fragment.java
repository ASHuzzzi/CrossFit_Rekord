package ru.lizzzi.crossfit_rekord.fragments;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
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

import java.util.List;
import java.util.Map;

import ru.lizzzi.crossfit_rekord.DownloadTableInLoader;
import ru.lizzzi.crossfit_rekord.R;
import ru.lizzzi.crossfit_rekord.adapters.RecyclerAdapter_Table;

/*
  Created by Liza on 11.10.2017.
 */

public class Table_Fragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Map>> {

    private ProgressBar mProgressBar;
    ListView lvItemsInTable;
    View v;
    RecyclerAdapter_Table adapter;
    int iNumberOfDay; //выбранный пользователем день
    int iPreviousOfDay; // в случае если надо будет вернуть данный предыдущего выбранного дня
    Button button_monday;
    Button button_tuesday;
    Button button_wednesday;
    Button button_thursday;
    Button button_friday;
    Button button_saturday;
    Button button_sunday;
    Network_check network_check;
    private Loader<String> loader;
    public int LOADER_ID = 1;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_table, container, false);

        button_monday= v.findViewById(R.id.day_1);
        button_tuesday= v.findViewById(R.id.day_2);
        button_wednesday= v.findViewById(R.id.day_3);
        button_thursday= v.findViewById(R.id.day_4);
        button_friday= v.findViewById(R.id.day_5);
        button_saturday= v.findViewById(R.id.day_6);
        button_sunday= v.findViewById(R.id.day_7);
        mProgressBar = v.findViewById(R.id.progressBar);
        lvItemsInTable = v.findViewById(R.id.lvTable);

        final LinearLayout layouterror = v.findViewById(R.id.Layout_Error);
        final LinearLayout layoutbuttonDayOfWeek = v.findViewById(R.id.Layout_Button_Day_of_Week);
        Button button_error = v.findViewById(R.id.button5);




        network_check = new Network_check(getContext());
        if (network_check.checkInternet()){
            iNumberOfDay = 1;
            //StartNewAsyncTask(iNumberOfDay);
            layouterror.setVisibility(View.INVISIBLE);
            layoutbuttonDayOfWeek.setVisibility(View.VISIBLE);
            Bundle bundle = new Bundle();
            bundle.putString(String.valueOf(DownloadTableInLoader.ARG_WORD), String.valueOf(iNumberOfDay));
            getLoaderManager().initLoader(LOADER_ID, bundle, this).forceLoad();

        }else {
            mProgressBar.setVisibility(View.INVISIBLE);
            layouterror.setVisibility(View.VISIBLE);
            layoutbuttonDayOfWeek.setVisibility(View.INVISIBLE);
        }

        button_error.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (network_check.checkInternet()){
                    iNumberOfDay = 1;
                    StartNewAsyncTask(iNumberOfDay);
                    layouterror.setVisibility(View.INVISIBLE);
                    layoutbuttonDayOfWeek.setVisibility(View.VISIBLE);
                }else {
                    mProgressBar.setVisibility(View.INVISIBLE);
                    layouterror.setVisibility(View.VISIBLE);
                    layoutbuttonDayOfWeek.setVisibility(View.INVISIBLE);
                }
            }
        });

        button_monday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iNumberOfDay = 1;
                StartNewAsyncTask(iNumberOfDay);
            }
        });

        button_tuesday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iNumberOfDay = 2;
                StartNewAsyncTask(iNumberOfDay);
            }
        });

        button_wednesday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iNumberOfDay = 3;
                StartNewAsyncTask(iNumberOfDay);
            }
        });

        button_thursday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iNumberOfDay = 4;
                StartNewAsyncTask(iNumberOfDay);
            }
        });

        button_friday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iNumberOfDay = 5;
                StartNewAsyncTask(iNumberOfDay);
            }
        });

        button_saturday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iNumberOfDay = 6;
                StartNewAsyncTask(iNumberOfDay);
            }
        });

        button_sunday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iNumberOfDay = 7;
                StartNewAsyncTask(iNumberOfDay);
            }
        });

        return v;
    }

    private void StartNewAsyncTask(int sNumberOfDay){
        final DownloadTable downloadTable = new DownloadTable();
        downloadTable.execute(sNumberOfDay);
    }

    @Override
    public Loader<List<Map>> onCreateLoader(int id, Bundle args) {

        Loader<List<Map>> loader = null;
        loader = new DownloadTableInLoader(getContext(), args);
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<List<Map>> loader, List<Map> data) {

        if (data != null){
            adapter = new RecyclerAdapter_Table(getContext(), data, R.layout.item_lv_table);
        }

        mProgressBar.setVisibility(View.INVISIBLE);
        if (adapter != null){
            lvItemsInTable.setAdapter(adapter);
            lvItemsInTable.setVisibility(View.VISIBLE);
            PreSelectionButtonDay(iNumberOfDay); //ToDo разобраться почему эта функция работает только в этом месте
            iPreviousOfDay = iNumberOfDay;

        }else {
            if (!network_check.checkInternet()){
                Toast.makeText(getContext(), "Нет подключения", Toast.LENGTH_SHORT).show();
                PreSelectionButtonDay(iPreviousOfDay);
            }else {
                Toast.makeText(getContext(), "Нет данных", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Map>> loader) {

    }

    @SuppressLint("StaticFieldLeak")
    private class DownloadTable extends AsyncTask<Integer,Void, Void>{

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            if(network_check.checkInternet()){
                lvItemsInTable.setVisibility(View.INVISIBLE);
                mProgressBar.setVisibility(View.VISIBLE);
                PreSelectionButtonDay(8);

            }
        }

        @Override
        protected Void doInBackground(Integer... params) {

            if (network_check.checkInternet()) {
                List<Map> result;
                int dayofweek = params[0];
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

        @SuppressLint("ResourceAsColor")
        @Override
        protected void onPostExecute(Void result){
            super.onPostExecute(result);

            mProgressBar.setVisibility(View.INVISIBLE);
            if (adapter != null){
                lvItemsInTable.setAdapter(adapter);
                lvItemsInTable.setVisibility(View.VISIBLE);
                PreSelectionButtonDay(iNumberOfDay); //ToDo разобраться почему эта функция работает только в этом месте
                iPreviousOfDay = iNumberOfDay;

            }else {
                if (!network_check.checkInternet()){
                    Toast.makeText(getContext(), "Нет подключения", Toast.LENGTH_SHORT).show();
                    PreSelectionButtonDay(iPreviousOfDay);
                }else {
                    Toast.makeText(getContext(), "Нет данных", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void PreSelectionButtonDay(int iDayOfWeek){
        if (iDayOfWeek == 1 ){
            SelectButtonDay(true, false, false, false, false, false, false);

        }else if (iDayOfWeek == 2){
            SelectButtonDay(false, true, false, false, false, false, false);

        }else if (iDayOfWeek == 3){
            SelectButtonDay(false, false, true, false, false, false, false);

        }else if (iDayOfWeek == 4){
            SelectButtonDay(false, false, false, true, false, false, false);

        }else if (iDayOfWeek == 5){
            SelectButtonDay(false, false, false, false, true, false, false);

        }else if (iDayOfWeek == 6){
            SelectButtonDay(false, false, false, false, false, true, false);

        }else if (iDayOfWeek == 7){
            SelectButtonDay(false, false, false, false, false, false, true);

        }else {
            SelectButtonDay(false, false, false, false, false, false, false);
        }
    }

    private void SelectButtonDay(boolean m, boolean tu, boolean w, boolean th, boolean f, boolean sa, boolean su) {

        button_monday.setPressed(m);
        button_tuesday.setPressed(tu);
        button_wednesday.setPressed(w);
        button_thursday.setPressed(th);
        button_friday.setPressed(f);
        button_saturday.setPressed(sa);
        button_sunday.setPressed(su);
    }

    @Override
    public void onPause(){
        super.onPause();
    }
}
