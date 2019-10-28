package ru.lizzzi.crossfit_rekord.model;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import ru.lizzzi.crossfit_rekord.backendless.BackendlessQueries;
import ru.lizzzi.crossfit_rekord.data.SQLiteStorageWod;
import ru.lizzzi.crossfit_rekord.inspectionСlasses.NetworkCheck;

public class CalendarWodViewModel  extends AndroidViewModel {

    private SQLiteStorageWod dbStorage;

    private List<Date> selectDates;
    private MutableLiveData<List<Date>> liveData;
    private MutableLiveData<Boolean> liveDataConnection;
    private Executor executor = new ThreadPoolExecutor(
            0,
            1,
            60,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>());

    private final String APP_PREFERENCES_OBJECTID = "ObjectId";
    private final String APP_PREFERENCES_SELECTEDDAY = "SelectedDay";
    private SharedPreferences sharedPreferences;

    private String userObjectID;
    private long timeStart;
    private long timeFinish;
    private Calendar calendar;

    public CalendarWodViewModel(@NonNull Application application) {
        super(application);
        String APP_PREFERENCES = "audata";
        sharedPreferences = getApplication().getSharedPreferences(
                APP_PREFERENCES,
                Context.MODE_PRIVATE);
        userObjectID = sharedPreferences.getString(APP_PREFERENCES_OBJECTID, "");
        dbStorage = new SQLiteStorageWod(getApplication());
        calendar = Calendar.getInstance();
    }

    public LiveData<List<Date>> loadDates() {
        liveData = new MutableLiveData<>();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                BackendlessQueries backendlessQuery = new BackendlessQueries();
                List<Map> trainingResults = backendlessQuery.loadingCalendarWod(
                        userObjectID,
                        timeStart,
                        timeFinish);
                if (trainingResults != null) {
                    ArrayList<Date> datesForLoadInLocalDb = new ArrayList<>();
                    Date parseDate;
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
                            "EEE MMM dd HH:mm:ss z yyyy",
                            Locale.ENGLISH);
                    for (int i = 0; i < trainingResults.size(); i++) {
                        String dateInStringFormat = String.valueOf(
                                trainingResults.get(i).get(backendlessQuery.TABLE_RESULTS_DATE_SESSION));
                        try {
                            parseDate = simpleDateFormat.parse(dateInStringFormat);
                            datesForLoadInLocalDb.add(parseDate);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                    selectDates = datesForLoadInLocalDb;
                    String userId = sharedPreferences.getString(APP_PREFERENCES_OBJECTID, "");
                    dbStorage.saveDates(userId, trainingResults);
                    liveData.postValue(selectDates);
                } else {
                    liveData.postValue(null);
                }
            }
        });
        return liveData;
    }

    public LiveData<Boolean> checkNetwork() {
        liveDataConnection = new MutableLiveData<>();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                NetworkCheck networkCheck = new NetworkCheck(getApplication());
                boolean isConnected = networkCheck.checkConnection();
                liveDataConnection.postValue(isConnected);
            }
        });
        return liveDataConnection;
    }

    public List<Date> getDates() {
        return selectDates = dbStorage.selectDates(
                sharedPreferences.getString(APP_PREFERENCES_OBJECTID, ""),
                timeStart,
                timeFinish);
    }

    public List<Date> getSelectDates() {
        return selectDates;
    }

    public int getSelectDatesSize() {
        return selectDates.size();
    }

    public void setSelectDates(List<Date> selectDates) {
        this.selectDates = selectDates;
    }

    public void saveDateInPrefs(Date date) {
        calendar.setTime(date);
        String Day = (calendar.get(Calendar.DATE) < 10)
                ? "0" + calendar.get(Calendar.DATE)
                : String.valueOf(calendar.get(Calendar.DATE));
        String Month = (calendar.get(Calendar.MONTH) < 10)
                ? "0" + (calendar.get(Calendar.MONTH) + 1)
                : String.valueOf((calendar.get(Calendar.MONTH) + 1));
        String selectedDate = Month + "/" + Day + "/" + calendar.get(Calendar.YEAR);
        sharedPreferences.edit()
                .putString(APP_PREFERENCES_SELECTEDDAY, selectedDate)
                .apply();
    }

    public Date getDate() {
        Date date;
        String selectedDay = getDateFromPreferences();
        if (selectedDay.equals("0") || selectedDay.equals("")) {
            date = calendar.getTime();
        } else {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat(
                        "MM/dd/yyyy",
                        Locale.getDefault());
                date = dateFormat.parse(selectedDay);
            } catch (ParseException e) {
                date = calendar.getTime();
            }
        }
        setTimePeriod(date);
        return date;
    }

    private String getDateFromPreferences() {
        return sharedPreferences.getString(APP_PREFERENCES_SELECTEDDAY, "");
    }

    public void monthChanged(CalendarDay calendarDay) {
        setTimePeriod(calendarDay.getDate());
    }

    private void setTimePeriod(Date date) {
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        int timeZoneOffset = TimeZone.getDefault().getRawOffset();
        timeStart = calendar.getTimeInMillis() + timeZoneOffset;
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        timeFinish = calendar.getTimeInMillis() + timeZoneOffset;
    }

    public boolean earlierThanToday(Date selectedDate) {
        calendar.setTime(selectedDate);
        return calendar.before(Calendar.getInstance());
    }
}
