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
import java.util.Date;
import java.util.List;

import ru.lizzzi.crossfit_rekord.R;
import ru.lizzzi.crossfit_rekord.data.CalendarWodDBContract;
import ru.lizzzi.crossfit_rekord.data.CalendarWodDBHelper;
import ru.lizzzi.crossfit_rekord.loaders.CalendarWodLoader;


public class CalendarWodFragment extends Fragment implements  OnDateSelectedListener, OnMonthChangedListener, LoaderManager.LoaderCallbacks<List<Date>> {

    MaterialCalendarView mcv;
    ProgressBar pbCalendarWod;

    private Handler handlerOpenFragment;
    private Thread threadOpenFragment;

    private NetworkCheck NetworkCheck;

    private int LOADER_ID = 1; //идентефикатор loader'а

    private CalendarWodDBHelper mDBHelper;

    private static final String APP_PREFERENCES = "audata";
    private static final String APP_PREFERENCES_OBJECTID = "ObjectId";
    SharedPreferences mSettings;

    @SuppressLint("HandlerLeak")
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        final View v = inflater.inflate(R.layout.fragment_calendar_wod, container, false);

        mcv = v.findViewById(R.id.calendarView);
        final LinearLayout layoutErrorCalendarWod = v.findViewById(R.id.Layout_Error_Calendar_Wod);
        Button btErrorCalendarWod = v.findViewById(R.id.bt_error_calendar_wod);
        pbCalendarWod = v.findViewById(R.id.pb_calendar_wod);

        mDBHelper = new CalendarWodDBHelper(getContext());

        try {
            mDBHelper.createDataBase();
        } catch (IOException ioe) {
            throw new Error("Unable to create database");
        }

        mcv.state().edit()
                .setMaximumDate(CalendarDay.from(2018, 0, 1))
                .setMaximumDate(CalendarDay.from(2019, 0, 31))
                .commit();
        mcv.setOnDateChangedListener(this);
        mcv.setOnMonthChangedListener(this);

        mcv.setVisibility(View.INVISIBLE);
        layoutErrorCalendarWod.setVisibility(View.INVISIBLE);
        pbCalendarWod.setVisibility(View.VISIBLE);

        //хэндлер для потока runnableOpenFragment
        handlerOpenFragment = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Bundle bundle = msg.getData();
                String resultCheck = bundle.getString("result");
                if (resultCheck != null){
                    if (resultCheck.equals("false")){
                        layoutErrorCalendarWod.setVisibility(View.VISIBLE);
                        pbCalendarWod.setVisibility(View.INVISIBLE);
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
                    List<Date> dates;
                    mSettings = getContext().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
                    dates = mDBHelper.selectDates(mSettings.getString(APP_PREFERENCES_OBJECTID, ""));
                    if (dates.size() > 0) {

                    }else {
                        firstStartAsyncTaskLoader();
                    }


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

        threadOpenFragment.start();
        return v;
    }

    private void firstStartAsyncTaskLoader(){
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
        Toast.makeText(getContext(), "Выбран" + date.getMonth(), Toast.LENGTH_SHORT).show();
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
                mcv.setDateSelected(CalendarDay.from(data.get(i)), true);
            }
            mcv.setVisibility(View.VISIBLE);
        }else{
            Toast.makeText(getContext(), "Нет данных", Toast.LENGTH_SHORT).show();
        }

        pbCalendarWod.setVisibility(View.INVISIBLE);

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
