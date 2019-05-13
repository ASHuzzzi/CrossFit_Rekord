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
import ru.lizzzi.crossfit_rekord.inspectionСlasses.Network;
import ru.lizzzi.crossfit_rekord.interfaces.ChangeTitle;
import ru.lizzzi.crossfit_rekord.loaders.CalendarWodLoader;

public class CalendarWodFragment extends Fragment implements  OnDateSelectedListener,
        OnMonthChangedListener, LoaderManager.LoaderCallbacks<List<Date>> {

    private Handler handlerOpenFragment;

    private final static int LOADER_ID = 1; //идентефикатор loader'а

    private CalendarWodDBHelper calendarWodDBHelper;

    private static final String APP_PREFERENCES = "audata";
    private static final String APP_PREFERENCES_OBJECTID = "ObjectId";
    private static final String APP_PREFERENCES_SELECTEDDAY = "SelectedDay";
    private static final String APP_PREFERENCES_SELECTEDDAYMONTH = "SelectedDayMonth";
    private SharedPreferences mSettings;

    private int month;

    private LinearLayout layoutErrorCalendarWod;
    private MaterialCalendarView calendarView;
    private ProgressBar progressBar;

    private long timeStart;
    private long timeInterval;
    private long timeFinish;

    private List<Date> selectDates;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @SuppressLint("HandlerLeak")
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_calendar_wod, container, false);

        calendarView = view.findViewById(R.id.calendarView);
        layoutErrorCalendarWod = view.findViewById(R.id.Layout_Error_Calendar_Wod);
        Button buttonErrorCalendarWod = view.findViewById(R.id.bt_error_calendar_wod);
        progressBar = view.findViewById(R.id.pb_calendar_wod);

        mSettings = getContext().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        calendarView.setVisibility(View.INVISIBLE);
        layoutErrorCalendarWod.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        calendarWodDBHelper = new CalendarWodDBHelper(getContext());
        try {
            calendarWodDBHelper.createDataBase();
        } catch (IOException ioe) {
            throw new Error("Unable to create database");
        }

        Calendar c = Calendar.getInstance();
        int maximumDateYear = c.get(Calendar.YEAR);
        int maximumDateMonth = c.get(Calendar.MONTH);
        int maximumDateDay = c.getActualMaximum(Calendar.DAY_OF_MONTH);

        calendarView.state().edit()
                .setMinimumDate(CalendarDay.from(2019, 0, 1))
                .setMaximumDate(CalendarDay.from(maximumDateYear, maximumDateMonth, maximumDateDay))
                .commit();
        calendarView.setOnDateChangedListener(this);
        calendarView.setOnMonthChangedListener(this);
        calendarView.setSaveEnabled(true);

        getLoaderManager().initLoader(LOADER_ID, null,this);

        //хэндлер для потока runnableOpenFragment
        handlerOpenFragment = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Bundle bundle = msg.getData();
                boolean checkDone = bundle.getBoolean("result");
                String status = bundle.getString("status");
                if (checkDone) {
                    layoutErrorCalendarWod.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                } else {
                    layoutErrorCalendarWod.setVisibility(View.GONE);
                    if (status != null && status.equals("load")) {
                        calendarView.setVisibility(View.INVISIBLE);
                        progressBar.setVisibility(View.VISIBLE);
                        startAsyncTaskLoader(timeStart, timeFinish);
                    } else {
                        calendarView.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                }
            }
        };

        buttonErrorCalendarWod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layoutErrorCalendarWod.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                startAsyncTaskLoader(timeStart, timeFinish);
            }
        });
        return view;
    }

    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
        String selectedDate;
        String dateForOpenCalendar; //дата которая запишется в файл, чтобы потом использовать ее при открытии календаря
        Date convertSelectDate;
        String Day;
        String Month;
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdfCheckTime2 = new SimpleDateFormat("MM/dd/yyyy");

        calendarView.setDateSelected(date, false);
        if (date.getDay() < 10) {
            Day = "0" + date.getDay();
        } else {
            Day = String.valueOf(date.getDay());
        }

        if (date.getMonth() < 10) {
            Month = "0" + (date.getMonth() + 1);
        } else {
            Month = String.valueOf((date.getMonth() + 1));
        }

        selectedDate = Month + "/" + Day + "/" + date.getYear();
        dateForOpenCalendar = Month + "/01/" + date.getYear();

        try {
            convertSelectDate = sdfCheckTime2.parse(selectedDate);
            GregorianCalendar calendarday = new GregorianCalendar();
            Date today = calendarday.getTime();
            if (convertSelectDate.getTime() <= today.getTime()) {
                //SharedPreferences mSettings = getContext().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = mSettings.edit();
                editor.putString(APP_PREFERENCES_SELECTEDDAY, selectedDate);
                editor.putString(APP_PREFERENCES_SELECTEDDAYMONTH, dateForOpenCalendar);
                editor.apply();

                WorkoutDetailsFragment fragment = new WorkoutDetailsFragment();
                Bundle bundle = new Bundle();
                bundle.putString("tag", selectedDate);
                fragment.setArguments(bundle);

                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction ft = fragmentManager.beginTransaction();
                ft.setCustomAnimations(R.anim.pull_in_right, R.anim.push_out_left, R.anim.pull_in_left, R.anim.push_out_right);
                ft.replace(R.id.container, fragment);
                ft.addToBackStack(null);
                ft.commit();
            } else {
                for (int i = 0; i < selectDates.size(); i++) {
                    calendarView.setDateSelected(CalendarDay.from(selectDates.get(i)), true);
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
            timeInterval = 2592000000L + 2592000000L;
            timeFinish = date.getDate().getTime() + timeInterval;

            calendarView.setVisibility(View.INVISIBLE);
            layoutErrorCalendarWod.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            getDates(timeStart, timeFinish);

            month = date.getMonth();
        }

    }

    private void startAsyncTaskLoader(long timeStart, long timeFinish){
        if (isAdded()) {
            if (getLoaderManager().hasRunningLoaders()) {
                getLoaderManager().destroyLoader(LOADER_ID);
            }
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf2 = new SimpleDateFormat("MM.dd.yyyy");
            String finishDate = sdf2.format(timeFinish);
            String startDate = sdf2.format(timeStart);
            Bundle bundle = new Bundle();
            bundle.putString("startDay", startDate);
            bundle.putString("nowDay", finishDate);
            getLoaderManager().restartLoader(LOADER_ID, bundle,this).forceLoad();
        }
    }

    @NonNull
    @Override
    public Loader<List<Date>> onCreateLoader(int id, Bundle bundle) {
        return new CalendarWodLoader(getContext(), bundle);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Date>> loader, List<Date> dates) {
        if (dates != null) {
            for (int i = 0; i <dates.size(); i++) {
                long time = dates.get(i).getTime();
                mSettings = getContext().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
                calendarWodDBHelper.saveDates(mSettings.getString(APP_PREFERENCES_OBJECTID, ""), time);
            }
            showSelectedDates(dates);

        } else {
            layoutErrorCalendarWod.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
            calendarView.setVisibility(View.INVISIBLE);
            Toast.makeText(getContext(), "Нет данных", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<Date>> loader) {

    }

    @Override
    public  void onStart() {
        super.onStart();
        if (layoutErrorCalendarWod.getVisibility() == View.GONE) {
            String selectedDay = mSettings.getString(APP_PREFERENCES_SELECTEDDAY, "");
            Calendar calendar = Calendar.getInstance();
            if (selectedDay.equals("0") || selectedDay.equals("")) {
                timeFinish = calendar.getTimeInMillis();
                timeInterval = 7776000000L;
                timeStart = timeFinish - timeInterval;
                month = calendar.get(Calendar.MONTH);
            } else {
                try {
                    @SuppressLint("SimpleDateFormat") final SimpleDateFormat sdf3 = new SimpleDateFormat("MM/dd/yyyy");
                    Date date = sdf3.parse(selectedDay);
                    calendarView.setCurrentDate(date);
                    calendar.setTime(date);
                    month = calendar.get(Calendar.MONTH);
                    timeFinish = date.getTime();
                    timeInterval = 3024000000L;
                    timeStart = timeFinish - timeInterval;
                    timeFinish = timeFinish + timeInterval;
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            calendarView.setVisibility(View.INVISIBLE);
            layoutErrorCalendarWod.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            getDates(timeStart, timeFinish);
        }


        if (getActivity() instanceof ChangeTitle) {
            ChangeTitle listernerChangeTitle = (ChangeTitle) getActivity();
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
        calendarWodDBHelper.close();
    }

    public void getDates(final long timeStart, final long timeFinish) {
        mSettings = getContext().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        List<Date> selectDates = calendarWodDBHelper.selectDates(
                mSettings.getString(APP_PREFERENCES_OBJECTID, ""),
                timeStart,
                timeFinish);

        if (selectDates.size() > 0) {
            showSelectedDates(selectDates);
        } else {
            Runnable runnableOpenFragment = new Runnable() {

                @Override
                public void run() {
                    Network network = new Network(getContext());
                    Bundle bundle = new Bundle();
                    boolean checkDone = network.checkConnection();
                    if (checkDone) {
                        CalendarWodFragment.this.timeStart = timeStart;
                        CalendarWodFragment.this.timeFinish = timeFinish;
                        bundle.putString("status", "load");
                    }
                    bundle.putBoolean("result", checkDone);
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

    public void showSelectedDates(List<Date> dates) {
        selectDates = dates;
        for (int i = 0; i < selectDates.size(); i++) {
            calendarView.setDateSelected(CalendarDay.from(selectDates.get(i)), true);
        }
        calendarView.setVisibility(View.VISIBLE);
        layoutErrorCalendarWod.setVisibility(View.GONE);
        progressBar.setVisibility(View.INVISIBLE);
    }
}
