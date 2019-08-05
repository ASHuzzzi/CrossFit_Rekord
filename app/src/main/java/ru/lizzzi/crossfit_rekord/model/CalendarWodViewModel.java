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
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import ru.lizzzi.crossfit_rekord.backendless.BackendlessQueries;
import ru.lizzzi.crossfit_rekord.data.SQLiteStorageWod;
import ru.lizzzi.crossfit_rekord.inspectionСlasses.Network;

public class CalendarWodViewModel  extends AndroidViewModel {

    private SQLiteStorageWod dbStorage;
    private BackendlessQueries backendlessQuery;

    private List<Date> selectDates;
    private MutableLiveData<List<Date>> liveData;
    private Executor executor = new ThreadPoolExecutor(
            0,
            1,
            60,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>());

    private static final String APP_PREFERENCES = "audata";
    private static final String APP_PREFERENCES_OBJECTID = "ObjectId";
    private static final String APP_PREFERENCES_SELECTEDDAY = "SelectedDay";
    private static final String APP_PREFERENCES_SELECTEDDAYMONTH = "SelectedDayMonth";
    private SharedPreferences sharedPreferences;

    private String userObjectID;
    private int month;
    private long timeStart;
    private long timeInterval;
    private long timeFinish;

    public CalendarWodViewModel(@NonNull Application application) {
        super(application);
        backendlessQuery = new BackendlessQueries();
        sharedPreferences =
                getApplication().getSharedPreferences(
                        APP_PREFERENCES,
                        Context.MODE_PRIVATE);
        userObjectID = sharedPreferences.getString(APP_PREFERENCES_OBJECTID, "");
        dbStorage = new SQLiteStorageWod(getApplication());
        dbStorage.createDataBase();
        Calendar calendar = GregorianCalendar.getInstance();
        month = calendar.get(Calendar.MONTH);
    }

    public LiveData<List<Date>> loadDates() {
        if (liveData == null) {
            liveData = new MutableLiveData<>();
        }
        executor.execute(new Runnable() {
            @Override
            public void run() {
                SimpleDateFormat dateFormat = new SimpleDateFormat(
                        "MM.dd.yyyy",
                        Locale.getDefault());
                String startDate = dateFormat.format(timeStart);
                String finishDate = dateFormat.format(timeFinish);
                List<Map> loadedDates = backendlessQuery.loadCalendarWod(
                        userObjectID,
                        startDate,
                        finishDate);
                if (loadedDates != null) {
                    ArrayList<Date> datesForLoadInLocalDb = new ArrayList<>();
                    Date parseDate;
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
                            "EEE MMM dd HH:mm:ss z yyyy",
                            Locale.ENGLISH);
                    for (int i = 0; i < loadedDates.size(); i++) {
                        String dateInStringFormat =
                                String.valueOf(loadedDates.get(i).get("date_session"));
                        try {
                            parseDate = simpleDateFormat.parse(dateInStringFormat);
                            datesForLoadInLocalDb.add(parseDate);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                    selectDates = datesForLoadInLocalDb;
                    String userId = sharedPreferences.getString(APP_PREFERENCES_OBJECTID, "");
                    dbStorage.saveDates(userId, selectDates);
                    liveData.postValue(selectDates);
                } else {
                    liveData.postValue(null);
                }
            }
        });
        return liveData;
    }

    public boolean checkNetwork() {
        Network network = new Network(getApplication());
        return network.checkConnection();
    }

    public List<Date> getDates() {
        return dbStorage.selectDates(
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

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public void saveDateInPrefs(String selectedDate, String dateForSave) {
        sharedPreferences.edit()
                .putString(APP_PREFERENCES_SELECTEDDAY, selectedDate)
                .putString(APP_PREFERENCES_SELECTEDDAYMONTH, dateForSave)
                .apply();
    }

    public Date getPeriodBoundaries() {
        Date date = null;
        String selectedDay = getSavedDate();
        Calendar calendar = Calendar.getInstance();
        if (selectedDay.equals("0") || selectedDay.equals("")) {
            timeFinish = calendar.getTimeInMillis();
            timeInterval = 7776000000L;
        } else {
            try {
                SimpleDateFormat dateFormat =
                        new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
                date = dateFormat.parse(selectedDay);
                calendar.setTime(date);
                timeFinish = date.getTime();
                timeInterval = 3024000000L;
                timeFinish = timeFinish + timeInterval;
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        timeStart = timeFinish - timeInterval;
        setMonth(calendar.get(Calendar.MONTH));
        return date;
    }

    private String getSavedDate() {
        return sharedPreferences.getString(APP_PREFERENCES_SELECTEDDAY, "");
    }

    public void monthChanged(CalendarDay date) {
        timeStart = date.getDate().getTime() - 2592000000L ;
        timeInterval = 2592000000L + 2592000000L;
        timeFinish = date.getDate().getTime() + timeInterval;
        setMonth(date.getMonth());
    }
}
