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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import ru.lizzzi.crossfit_rekord.R;
import ru.lizzzi.crossfit_rekord.data.CalendarWodDBHelper;
import ru.lizzzi.crossfit_rekord.inspectionСlasses.NetworkCheck;
import ru.lizzzi.crossfit_rekord.interfaces.InterfaceChangeTitle;
import ru.lizzzi.crossfit_rekord.loaders.CalendarWodLoader;


public class CalendarWodFragment extends Fragment implements  OnDateSelectedListener,
        OnMonthChangedListener, LoaderManager.LoaderCallbacks<List<Date>> {

    private Handler handlerOpenFragment;

    private int LOADER_ID = 1; //идентефикатор loader'а

    private CalendarWodDBHelper mDBHelper;

    private static final String APP_PREFERENCES = "audata";
    private static final String APP_PREFERENCES_OBJECTID = "ObjectId";
    private static final String APP_PREFERENCES_SELECTEDDAY = "SelectedDay";
    private static final String APP_PREFERENCES_SELECTEDDAYMONTH = "SelectedDayMonth";
    private SharedPreferences mSettings;

    private int month;

    private LinearLayout layoutErrorCalendarWod;
    private MaterialCalendarView mcv;
    private ProgressBar pbCalendarWod;

    private long timenow;
    private long interval;
    private long timeStart;

    private List<Date> loadDates;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @SuppressLint("HandlerLeak")
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_calendar_wod, container, false);

        mcv = v.findViewById(R.id.calendarView);
        layoutErrorCalendarWod = v.findViewById(R.id.Layout_Error_Calendar_Wod);
        Button btErrorCalendarWod = v.findViewById(R.id.bt_error_calendar_wod);
        pbCalendarWod = v.findViewById(R.id.pb_calendar_wod);

        mcv.setVisibility(View.INVISIBLE);
        layoutErrorCalendarWod.setVisibility(View.INVISIBLE);
        pbCalendarWod.setVisibility(View.VISIBLE);

        mDBHelper = new CalendarWodDBHelper(getContext());
        try {
            mDBHelper.createDataBase();
        } catch (IOException ioe) {
            throw new Error("Unable to create database");
        }

        Calendar c = Calendar.getInstance();
        int mcvMaximumDateYear = c.get(Calendar.YEAR);
        int mcvMaximumDateMonth = c.get(Calendar.MONTH);
        int mcvMaximumDateDay = c.getActualMaximum(Calendar.DAY_OF_MONTH);

        mcv.state().edit()
                .setMinimumDate(CalendarDay.from(2018, 0, 1))
                .setMaximumDate(CalendarDay.from(mcvMaximumDateYear, mcvMaximumDateMonth, mcvMaximumDateDay))
                .commit();
        mcv.setOnDateChangedListener(this);
        mcv.setOnMonthChangedListener(this);
        mcv.setSaveEnabled(true);

        getLoaderManager().initLoader(LOADER_ID, null,this);

        //хэндлер для потока runnableOpenFragment
        handlerOpenFragment = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Bundle bundle = msg.getData();
                String resultCheck = bundle.getString("result");
                String status = bundle.getString("status");
                if (resultCheck != null && resultCheck.equals("false")){
                    layoutErrorCalendarWod.setVisibility(View.VISIBLE);
                    pbCalendarWod.setVisibility(View.INVISIBLE);
                }else {
                    if (status != null && status.equals("load")){
                        mcv.setVisibility(View.INVISIBLE);
                        layoutErrorCalendarWod.setVisibility(View.INVISIBLE);
                        pbCalendarWod.setVisibility(View.VISIBLE);
                        startAsyncTaskLoader(timeStart, timenow);
                    }else {
                        mcv.setVisibility(View.VISIBLE);
                        layoutErrorCalendarWod.setVisibility(View.INVISIBLE);
                        pbCalendarWod.setVisibility(View.INVISIBLE);
                    }
                }
            }
        };

        btErrorCalendarWod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layoutErrorCalendarWod.setVisibility(View.INVISIBLE);
                pbCalendarWod.setVisibility(View.VISIBLE);
                startAsyncTaskLoader(timeStart, timenow);
            }
        });

        return v;
    }



    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
        String selectedDate;
        String forMemoryDate; //дата которая запишется в файл, чтобы потом использовать ее при открытии календаря
        Date convertSelectDate;
        String Day;
        String Month;
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdfCheckTime2 = new SimpleDateFormat("MM/dd/yyyy");

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
        forMemoryDate = Month + "/01/" + date.getYear();

        try {
            convertSelectDate = sdfCheckTime2.parse(selectedDate);
            GregorianCalendar calendarday = new GregorianCalendar();
            Date today = calendarday.getTime();
            if (convertSelectDate.getTime() <= today.getTime()){
                SharedPreferences mSettings = getContext().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = mSettings.edit();
                editor.putString(APP_PREFERENCES_SELECTEDDAY, selectedDate);
                editor.putString(APP_PREFERENCES_SELECTEDDAYMONTH, forMemoryDate);
                editor.apply();

                WorkoutDetailsFragment yfc = new WorkoutDetailsFragment();
                Bundle bundle = new Bundle();
                bundle.putString("tag", selectedDate);
                yfc.setArguments(bundle);

                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction ft = fragmentManager.beginTransaction();
                ft.replace(R.id.container, yfc);
                ft.addToBackStack(null);

                ft.commit();
            }else {
                for (int i = 0; i <loadDates.size(); i++){
                    mcv.setDateSelected(CalendarDay.from(loadDates.get(i)), true);
                }
                Toast.makeText(getContext(), "Тренировки еще не было", Toast.LENGTH_SHORT).show();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {

        int swipeMonth = date.getMonth();
        int checkMonth = month - swipeMonth;
        if ((checkMonth == 2) || (checkMonth == -2) || (checkMonth == -10) || (checkMonth == 10)){
            timeStart = date.getDate().getTime() - 2592000000L ;
            interval = 2592000000L + 2592000000L;
            timenow = date.getDate().getTime() + interval;

            mcv.setVisibility(View.INVISIBLE);
            layoutErrorCalendarWod.setVisibility(View.INVISIBLE);
            pbCalendarWod.setVisibility(View.VISIBLE);
            loadDates(timeStart, timenow);

            month = date.getMonth();
        }

    }

    private void startAsyncTaskLoader(long timeStart, long timenow){
        if(isAdded()){
            if (getLoaderManager().hasRunningLoaders()) {
                getLoaderManager().destroyLoader(LOADER_ID);
            }

            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf2 = new SimpleDateFormat("MM.dd.yyyy");
            String stNowDate = sdf2.format(timenow);
            String stStartDate = sdf2.format(timeStart);

            Bundle bundle = new Bundle();
            bundle.putString("startDay", stStartDate);
            bundle.putString("nowDay", stNowDate);
            getLoaderManager().restartLoader(LOADER_ID, bundle,this).forceLoad();
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

            layoutErrorCalendarWod.setVisibility(View.VISIBLE);
            pbCalendarWod.setVisibility(View.INVISIBLE);
            mcv.setVisibility(View.INVISIBLE);
            Toast.makeText(getContext(), "Нет данных", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Date>> loader) {

    }

    @Override
    public  void onStart() {
        super.onStart();

        if(layoutErrorCalendarWod.getVisibility() == View.INVISIBLE){
            SharedPreferences mSettings = getContext().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
            String stDay = mSettings.getString(APP_PREFERENCES_SELECTEDDAYMONTH, "");

            Calendar cal = Calendar.getInstance();
            if(stDay.equals("0") || stDay.equals("")){

                timenow = cal.getTimeInMillis();
                interval = 7776000000L;
                timeStart = timenow - interval;
                month = cal.get(Calendar.MONTH);
            }else {
                try {
                    @SuppressLint("SimpleDateFormat") final SimpleDateFormat sdf3 = new SimpleDateFormat("MM/dd/yyyy");
                    Date date = sdf3.parse(stDay);
                    mcv.setCurrentDate(date);
                    cal.setTime(date);
                    month = cal.get(Calendar.MONTH);
                    timenow = date.getTime();
                    interval = 3024000000L;
                    //interval = 2592000000L;
                    //interval = 7776000000L;
                    timeStart = timenow - interval;
                    timenow = timenow + interval;
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            mcv.setVisibility(View.INVISIBLE);
            layoutErrorCalendarWod.setVisibility(View.INVISIBLE);
            pbCalendarWod.setVisibility(View.VISIBLE);

            loadDates(timeStart, timenow);
        }


        if (getActivity() instanceof InterfaceChangeTitle){
            InterfaceChangeTitle listernerChangeTitle = (InterfaceChangeTitle) getActivity();
            listernerChangeTitle.changeTitle(R.string.title_CalendarWod_Fragment, R.string.title_CalendarWod_Fragment);
        }
    }

    public void onResume(){
        super.onResume();
    }
    public void onPause(){
        super.onPause();
    }

    public void onStop() {
        super.onStop();
        if (getLoaderManager().hasRunningLoaders()) {
            getLoaderManager().destroyLoader(LOADER_ID);
        }
        mDBHelper.close();
    }

    public void loadDates(final long ltimeStart, final long ltimenow){
        mSettings = getContext().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        List<Date> dates = mDBHelper.selectDates(
                mSettings.getString(APP_PREFERENCES_OBJECTID, ""),
                ltimeStart,
                ltimenow);

        if (dates.size() > 0) {
            showDates(dates);
        }else {

            //поток запускаемый при создании экрана (запуск происходит из onResume)
            Runnable runnableOpenFragment = new Runnable() {

                @Override
                public void run() {
                    NetworkCheck networkCheck = new NetworkCheck(getContext());
                    boolean resultCheck = networkCheck.checkInternet();

                    Bundle bundle = new Bundle();
                    if (resultCheck) {
                        timeStart = ltimeStart;
                        timenow = ltimenow;

                        bundle.putString("result", String.valueOf(true));
                        bundle.putString("status", "load");

                    } else {
                        bundle.putString("result", String.valueOf(false));

                    }

                    Message msg = handlerOpenFragment.obtainMessage();
                    msg.setData(bundle);
                    handlerOpenFragment.sendMessage(msg);

                }
            };

            Thread threadOpenFragment = new Thread(runnableOpenFragment);
            threadOpenFragment.setDaemon(true);
            threadOpenFragment.start();
        }

    }

    public void showDates(List<Date> dates){
        loadDates = dates;
        for (int i = 0; i <dates.size(); i++){
            mcv.setDateSelected(CalendarDay.from(dates.get(i)), true);
        }

        mcv.setVisibility(View.VISIBLE);
        layoutErrorCalendarWod.setVisibility(View.INVISIBLE);
        pbCalendarWod.setVisibility(View.INVISIBLE);
    }
}
