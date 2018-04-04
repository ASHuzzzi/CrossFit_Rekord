package ru.lizzzi.crossfit_rekord.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import ru.lizzzi.crossfit_rekord.R;
import ru.lizzzi.crossfit_rekord.adapters.RecyclerAdapter_RecordForTrainingSelect;
import ru.lizzzi.crossfit_rekord.interfaces.Listener_RecordForTrainingSelect;
import ru.lizzzi.crossfit_rekord.loaders.Table_Fragment_Loader;


public class RecordForTrainingSelect_Fragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Map>>{

    Date date;
    GregorianCalendar calendarday;
    GregorianCalendar numberdayweek ;
    String time_select;
    String date_select_full;
    String date_select_show;

    RecyclerAdapter_RecordForTrainingSelect adapter2;

    RecyclerView rvTreningTime;

    int iNumberOfDay;

    public int LOADER_ID = 1;
    Network_check network_check;
    private Loader<List<Map>> mLoader;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_record_for_training_select, container, false);

        Button btToday = v.findViewById(R.id.btToday);
        Button btTommorow = v.findViewById(R.id.btTommorow);
        Button btAftertommorow = v.findViewById(R.id.btAftertommorow);
        rvTreningTime = v.findViewById(R.id.rvTrainingTime);

        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("EEEE dd MMMM");
        @SuppressLint("SimpleDateFormat") final SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM");

        date = new Date();
        calendarday = new GregorianCalendar();
        calendarday.add(Calendar.DAY_OF_YEAR, 1);
        final Date tomorrow = calendarday.getTime();

        calendarday.add(Calendar.DAY_OF_YEAR, 1);
        final Date aftertomorrow = calendarday.getTime();

        final String currentToday = sdf.format(date);
        final String currentTomorrow = sdf.format(tomorrow);
        final String currentAftertommorow = sdf.format(aftertomorrow);

        btToday.setText(currentToday);
        btTommorow.setText(currentTomorrow);
        btAftertommorow.setText(currentAftertommorow);

        //date_select = String.valueOf(date);

        network_check = new Network_check(getContext());

        numberdayweek = new GregorianCalendar();
        iNumberOfDay = numberdayweek.get(Calendar.DAY_OF_WEEK)-1;

        Bundle bundle = new Bundle();
        bundle.putString(String.valueOf(Table_Fragment_Loader.ARG_WORD), String.valueOf(iNumberOfDay));
        mLoader =  getLoaderManager().initLoader(LOADER_ID, bundle, this);
        mLoader.forceLoad();

        btToday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendarday.setTime(date);
                iNumberOfDay = numberdayweek.get(Calendar.DAY_OF_WEEK)-1;
                StartNewAsyncTaskLoader(iNumberOfDay);
                date_select_full = sdf2.format(date);
                date_select_show = currentToday;
            }
        });

        btTommorow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendarday.setTime(tomorrow);
                iNumberOfDay = numberdayweek.get(Calendar.DAY_OF_WEEK);
                StartNewAsyncTaskLoader(iNumberOfDay);
                date_select_full = sdf2.format(tomorrow);
                date_select_show = currentTomorrow;
            }
        });

        btAftertommorow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendarday.setTime(aftertomorrow);
                iNumberOfDay = numberdayweek.get(Calendar.DAY_OF_WEEK)+1;
                StartNewAsyncTaskLoader(iNumberOfDay);
                date_select_full = sdf2.format(aftertomorrow);
                date_select_show = currentAftertommorow;

            }
        });

        return v;
    }

    private void StartNewAsyncTaskLoader(int day_select){

        Bundle bundle = new Bundle();
        bundle.putString(String.valueOf(Table_Fragment_Loader.ARG_WORD), String.valueOf(day_select));
        getLoaderManager().restartLoader(LOADER_ID, bundle,this).onContentChanged();
    }
    @Override
    public Loader<List<Map>> onCreateLoader(int id, Bundle args) {
        if (network_check.checkInternet()) {
            Loader<List<Map>> loader;
            loader = new Table_Fragment_Loader(getContext(), args);
            return loader;
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<List<Map>> loader, List<Map> data) {
        if (data != null){
            adapter2 = new RecyclerAdapter_RecordForTrainingSelect(getContext(), data, new Listener_RecordForTrainingSelect(){
                @Override
                public void SelectTime(String start_time, String types_item) {
                    time_select = start_time;
                    String typesitem = types_item;

                    RecordForTrainingRecording_Fragment yfc =  new RecordForTrainingRecording_Fragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("time", time_select);
                    bundle.putString("datefull", date_select_full);
                    bundle.putString("dateshow", date_select_show);
                    bundle.putString("type", typesitem);
                    yfc.setArguments(bundle);
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction ft = fragmentManager.beginTransaction();
                    ft.replace(R.id.container, yfc);
                    ft.addToBackStack(null);
                    ft.commit();
                }

            });
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
            rvTreningTime.setLayoutManager(mLayoutManager);
            rvTreningTime.setAdapter(adapter2);
        }

    }

    @Override
    public void onLoaderReset(Loader<List<Map>> loader) {

    }


    public boolean checkInternet() {

        ConnectivityManager cm = (ConnectivityManager)getActivity().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm != null ? cm.getActiveNetworkInfo() : null;
        // проверка подключения
        return activeNetwork != null && activeNetwork.isConnected();

    }

    @Override
    public void onResume() {
        super.onResume();

    }
}
