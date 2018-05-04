package ru.lizzzi.crossfit_rekord.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import java.util.Date;
import java.util.List;

import ru.lizzzi.crossfit_rekord.R;
import ru.lizzzi.crossfit_rekord.loaders.Calendar_wod_Loader;


public class Calendar_wod_Fragment extends Fragment implements  OnDateSelectedListener, OnMonthChangedListener, LoaderManager.LoaderCallbacks<List<Date>> {

    MaterialCalendarView mcv;
    ProgressBar pb_calendar_wod;

    private Handler handler_open_fragment;
    private Thread thread_open_fragment;

    private Network_check network_check;

    private int LOADER_ID = 1; //идентефикатор loader'а

    @SuppressLint("HandlerLeak")
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        final View v = inflater.inflate(R.layout.fragment_calendar_wod, container, false);

        mcv = v.findViewById(R.id.calendarView);
        final LinearLayout Layout_Error_Calendar_Wod = v.findViewById(R.id.Layout_Error_Calendar_Wod);
        Button bt_error_calendar_wod = v.findViewById(R.id.bt_error_calendar_wod);
        pb_calendar_wod = v.findViewById(R.id.pb_calendar_wod);

        mcv.state().edit()
                .setMaximumDate(CalendarDay.from(2018, 0, 1))
                .setMaximumDate(CalendarDay.from(2019, 0, 31))
                .commit();
        mcv.setOnDateChangedListener(this);
        mcv.setOnMonthChangedListener(this);

        mcv.setVisibility(View.INVISIBLE);
        Layout_Error_Calendar_Wod.setVisibility(View.INVISIBLE);
        pb_calendar_wod.setVisibility(View.VISIBLE);

        //хэндлер для потока runnable_open_fragment
        handler_open_fragment = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Bundle bundle = msg.getData();
                String result_check = bundle.getString("result");
                if (result_check != null){
                    if (result_check.equals("false")){
                        Layout_Error_Calendar_Wod.setVisibility(View.VISIBLE);
                        pb_calendar_wod.setVisibility(View.INVISIBLE);
                    }
                }
            }
        };

        //поток запускаемый при создании экрана (запуск происходит из onResume)
        Runnable runnable_open_fragment = new Runnable() {
            @Override
            public void run() {
                network_check = new Network_check(getContext());
                boolean result_check = network_check.checkInternet();
                if (result_check){
                    FirstStartAsyncTaskLoader();

                }else {
                    Message msg = handler_open_fragment.obtainMessage();
                    Bundle bundle = new Bundle();
                    bundle.putString("result", String.valueOf(false));
                    msg.setData(bundle);
                    handler_open_fragment.sendMessage(msg);
                }
            }
        };
        thread_open_fragment = new Thread(runnable_open_fragment);
        thread_open_fragment.setDaemon(true);

        bt_error_calendar_wod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Layout_Error_Calendar_Wod.setVisibility(View.INVISIBLE);
                pb_calendar_wod.setVisibility(View.VISIBLE);
                thread_open_fragment.run();
            }
        });

        thread_open_fragment.start();
        return v;
    }

    private void FirstStartAsyncTaskLoader(){
        if(isAdded()){
            if (getLoaderManager().hasRunningLoaders()) {
                getLoaderManager().destroyLoader(LOADER_ID);
            }
            getLoaderManager().initLoader(LOADER_ID, null,this).forceLoad();
        }
    }

    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
        String selectedDate;
        String Day;
        String Month;

        mcv.setDateSelected(date, false);
        if (date.getDay() <10){
            Day = "0" + date.getDay();
        }else{
            Day = String.valueOf(date.getDay());
        }

        if (date.getMonth() <10){
            Month = "0" + (date.getMonth() + 1);
        }else{
            Month = String.valueOf((date.getMonth() + 1));
        }


        selectedDate = Month + "/" + Day + "/" + date.getYear();
        Workout_details_Fragment yfc = new Workout_details_Fragment();
        Bundle bundle = new Bundle();
        bundle.putString("tag", selectedDate);
        yfc.setArguments(bundle);

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.container, yfc);
        ft.addToBackStack(null);

        ft.commit();

    }

    @Override
    public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
        Toast.makeText(getContext(), "Выбран" + date.getMonth(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public Loader<List<Date>> onCreateLoader(int id, Bundle args) {
        Loader<List<Date>> loader;
        loader = new Calendar_wod_Loader(getContext(), args);
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<List<Date>> loader, List<Date> data) {

        if (data != null){
            for (int i = 0; i <data.size(); i++){
                mcv.setDateSelected(CalendarDay.from(data.get(i)), true);
            }
            mcv.setVisibility(View.VISIBLE);
        }else{
            Toast.makeText(getContext(), "Нет данных", Toast.LENGTH_SHORT).show();
        }

        pb_calendar_wod.setVisibility(View.INVISIBLE);

    }

    @Override
    public void onLoaderReset(Loader<List<Date>> loader) {

    }

    public void onStop() {
        super.onStop();
        if (getLoaderManager().hasRunningLoaders()) {
            getLoaderManager().destroyLoader(LOADER_ID);
        }
    }
}
