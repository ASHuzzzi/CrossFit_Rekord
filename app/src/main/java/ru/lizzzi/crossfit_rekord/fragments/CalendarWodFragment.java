package ru.lizzzi.crossfit_rekord.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
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

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import ru.lizzzi.crossfit_rekord.R;
import ru.lizzzi.crossfit_rekord.data.CalendarWodDBHelper;
import ru.lizzzi.crossfit_rekord.loaders.CalendarWodLoader;


public class CalendarWodFragment extends Fragment implements  OnDateSelectedListener,
        OnMonthChangedListener, LoaderManager.LoaderCallbacks<List<Date>> {

    private Handler handlerOpenFragment;
    private Thread threadOpenFragment;

    private NetworkCheck NetworkCheck;

    private int LOADER_ID = 1; //идентефикатор loader'а

    private CalendarWodDBHelper mDBHelper;

    private static final String APP_PREFERENCES = "audata";
    private static final String APP_PREFERENCES_OBJECTID = "ObjectId";
    private SharedPreferences mSettings;

    private int month;

    private LinearLayout layoutErrorCalendarWod;
    private MaterialCalendarView mcv;
    private ProgressBar pbCalendarWod;

    @SuppressLint("SimpleDateFormat") final SimpleDateFormat sdf2 = new SimpleDateFormat("MM.dd.yyyy");
    @SuppressLint("SimpleDateFormat") final SimpleDateFormat sdf3 = new SimpleDateFormat("MM");

    @SuppressLint("HandlerLeak")
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_calendar_wod, container, false);

        mcv = v.findViewById(R.id.calendarView);
        layoutErrorCalendarWod = v.findViewById(R.id.Layout_Error_Calendar_Wod);
        Button btErrorCalendarWod = v.findViewById(R.id.bt_error_calendar_wod);
        pbCalendarWod = v.findViewById(R.id.pb_calendar_wod);

        mDBHelper = new CalendarWodDBHelper(getContext());
        try {
            mDBHelper.createDataBase();
        } catch (IOException ioe) {
            throw new Error("Unable to create database");
        }

        mcv.state().edit()
                .setMinimumDate(CalendarDay.from(2018, 0, 1))
                .setMaximumDate(CalendarDay.from(2019, 0, 31))
                .commit();
        mcv.setOnDateChangedListener(this);
        mcv.setOnMonthChangedListener(this);
        mcv.setSaveEnabled(true);

        getLoaderManager().initLoader(LOADER_ID, null,this);


        Date date = new Date();
        GregorianCalendar gcCalendarDay = new GregorianCalendar();

        gcCalendarDay.add(Calendar.DAY_OF_YEAR, 1);
        gcCalendarDay.setTime(date);
        month = Integer.parseInt(sdf3.format(date));

        //хэндлер для потока runnableOpenFragment
        handlerOpenFragment = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Bundle bundle = msg.getData();
                String resultCheck = bundle.getString("result");
                String status = bundle.getString("status");
                if (resultCheck != null){
                    if (resultCheck.equals("false")){
                        layoutErrorCalendarWod.setVisibility(View.VISIBLE);
                        pbCalendarWod.setVisibility(View.INVISIBLE);
                    }else
                        if (status != null){
                            if (status.equals("load")){
                                mcv.setVisibility(View.INVISIBLE);
                                layoutErrorCalendarWod.setVisibility(View.INVISIBLE);
                                pbCalendarWod.setVisibility(View.VISIBLE);
                            }else {
                                mcv.setVisibility(View.VISIBLE);
                                layoutErrorCalendarWod.setVisibility(View.INVISIBLE);
                                pbCalendarWod.setVisibility(View.INVISIBLE);
                            }
                        }

                }
            }
        };

        //поток запускаемый при создании экрана (запуск происходит из onResume)
        Runnable runnableOpenFragment = new Runnable() {
            @Override
            public void run() {
                NetworkCheck = new NetworkCheck(getContext());
                boolean resultCheck = NetworkCheck.checkInternet();
                if (resultCheck){
                    Message msg = handlerOpenFragment.obtainMessage();
                    Bundle bundle = new Bundle();
                    bundle.putString("result", String.valueOf(true));
                    bundle.putString("status", "load");
                    msg.setData(bundle);
                    handlerOpenFragment.sendMessage(msg);

                    Calendar cal = Calendar.getInstance();
                    long timenow = cal.getTimeInMillis();
                    long interval = 7776000000L;
                    long timeStart = timenow - interval;

                    loadDates(timeStart, timenow);

                }else {
                    Message msg = handlerOpenFragment.obtainMessage();
                    Bundle bundle = new Bundle();
                    bundle.putString("result", String.valueOf(false));
                    msg.setData(bundle);
                    handlerOpenFragment.sendMessage(msg);
                }
            }
        };
        threadOpenFragment = new Thread(runnableOpenFragment);
        threadOpenFragment.setDaemon(true);

        btErrorCalendarWod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layoutErrorCalendarWod.setVisibility(View.INVISIBLE);
                pbCalendarWod.setVisibility(View.VISIBLE);
                threadOpenFragment.run();
            }
        });

        return v;
    }

    private void startAsyncTaskLoader(long timeStart, long timenow){
        if(isAdded()){
            if (getLoaderManager().hasRunningLoaders()) {
                getLoaderManager().destroyLoader(LOADER_ID);
            }

            String stNowDate = sdf2.format(timenow);
            String stStartDate = sdf2.format(timeStart);

            Bundle bundle = new Bundle();
            bundle.putString("startDay", stStartDate);
            bundle.putString("nowDay", stNowDate);
            getLoaderManager().restartLoader(LOADER_ID, bundle,this).forceLoad();
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
        WorkoutDetailsFragment yfc = new WorkoutDetailsFragment();
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

        //TODO Проверить запись в базу и получении из нее уже загруженных данных

        int checkMonth = month - (date.getMonth() + 1);
        if ((checkMonth == 2) || (checkMonth == -2) || (checkMonth == -10) || (checkMonth == 10)){
            long timeStart = date.getDate().getTime() - 2592000000L ;
            long interval = 2592000000L + 2592000000L;
            long timenow = date.getDate().getTime() + interval;

            loadDates(timeStart, timenow);

            month = date.getMonth() + 1;
        }

    }

    @Override
    public Loader<List<Date>> onCreateLoader(int id, Bundle args) {
        Loader<List<Date>> loader;
        loader = new CalendarWodLoader(getContext(), args);
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<List<Date>> loader, List<Date> data) {

        if (data != null){
            for (int i = 0; i <data.size(); i++){
                long lDate = data.get(i).getTime();
                mSettings = getContext().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
                mDBHelper.saveDates(mSettings.getString(APP_PREFERENCES_OBJECTID, ""), lDate);
            }
            showDates(data);

        }else{
            Toast.makeText(getContext(), "Нет данных", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Date>> loader) {

    }

    public void onResume(){
        super.onResume();
        if (threadOpenFragment.getState() == Thread.State.NEW){
            threadOpenFragment.start();
        }


    }
    public void onPause(){
        super.onPause();
    }

    public void onStop() {
        super.onStop();
        if (getLoaderManager().hasRunningLoaders()) {
            getLoaderManager().destroyLoader(LOADER_ID);
        }
    }

    public void loadDates(long ltimeStart, long ltimenow){
        mSettings = getContext().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        List<Date> dates = mDBHelper.selectDates(
                mSettings.getString(APP_PREFERENCES_OBJECTID, ""),
                ltimeStart,
                ltimenow);
        if (dates.size() > 0) {
            showDates(dates);
        }else {
            startAsyncTaskLoader(ltimeStart, ltimenow);
        }
    }

    public void showDates(List<Date> dates){
        for (int i = 0; i <dates.size(); i++){
            mcv.setDateSelected(CalendarDay.from(dates.get(i)), true);
        }

        Message msg = handlerOpenFragment.obtainMessage();
        Bundle bundle = new Bundle();
        bundle.putString("result", String.valueOf(true));
        bundle.putString("status", "finish");
        msg.setData(bundle);
        handlerOpenFragment.sendMessage(msg);
    }
}