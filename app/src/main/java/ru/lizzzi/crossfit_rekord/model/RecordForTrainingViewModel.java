package ru.lizzzi.crossfit_rekord.model;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import ru.lizzzi.crossfit_rekord.backendless.BackendlessQueries;
import ru.lizzzi.crossfit_rekord.inspectionСlasses.NetworkCheck;

public class RecordForTrainingViewModel extends AndroidViewModel {
    private List<List<Map>> scheduleParnas;
    private List<List<Map>> scheduleMyzhestvo;
    private Executor executor = new ThreadPoolExecutor(
            0,
            1,
            60,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>());
    private MutableLiveData<List<List<Map>>> liveDataParnas;
    private MutableLiveData<List<List<Map>>> liveDataMyzhestvo;
    private BackendlessQueries backendlessQuery;
    private int GYM_PARNAS = 1;
    private int selectedDay;
    private boolean isToday;
    private Date today;
    private Date tomorrow;
    private Date afterTomorrow;
    private GregorianCalendar gregorianCalendar;
    private int selectedDayForUri;
    private int selectedGym;


    public RecordForTrainingViewModel(@NonNull Application application) {
        super(application);
        backendlessQuery = new BackendlessQueries();
        gregorianCalendar = new GregorianCalendar();
        selectedDay = gregorianCalendar.get(Calendar.DAY_OF_WEEK);
        today = new Date();
        gregorianCalendar.add(Calendar.DAY_OF_YEAR, 1);
        tomorrow = gregorianCalendar.getTime();
        gregorianCalendar.add(Calendar.DAY_OF_YEAR, 1);
        afterTomorrow = gregorianCalendar.getTime();
        isToday = true;
        selectedGym = GYM_PARNAS;
        selectedDayForUri = 0;
    }

    public LiveData<List<List<Map>>> loadScheduleParnas() {
        if (liveDataParnas == null) {
            liveDataParnas = new MutableLiveData<>();
        }
        executor.execute(new Runnable() {
            @Override
            public void run() {
                List<Map> loadedSchedule = backendlessQuery.loadSchedule(String.valueOf(selectedGym));
                if (loadedSchedule != null) {
                    scheduleParnas = splitRawSchedule(loadedSchedule);
                    liveDataParnas.postValue(scheduleParnas);
                } else {
                    liveDataParnas.postValue(null);
                }
            }
        });
        return liveDataParnas;
    }

    public LiveData<List<List<Map>>> loadScheduleMyzhestvo() {
        if (liveDataMyzhestvo == null) {
            liveDataMyzhestvo = new MutableLiveData<>();
        }
        executor.execute(new Runnable() {
            @Override
            public void run() {
                List<Map> loadedSchedule = backendlessQuery.loadSchedule(String.valueOf(selectedGym));
                if (loadedSchedule != null) {
                    scheduleMyzhestvo = splitRawSchedule(loadedSchedule);
                    liveDataMyzhestvo.postValue(scheduleMyzhestvo);
                } else {
                    liveDataMyzhestvo.postValue(null);
                }
            }
        });
        return liveDataMyzhestvo;
    }

    private List<List<Map>> splitRawSchedule(List<Map> loadedSchedule){
        List<Map> scheduleMonday = new ArrayList<>();
        List<Map> scheduleTuesday = new ArrayList<>();
        List<Map> scheduleWednesday = new ArrayList<>();
        List<Map> scheduleThursday = new ArrayList<>();
        List<Map> scheduleFriday = new ArrayList<>();
        List<Map> scheduleSaturday = new ArrayList<>();
        List<Map> scheduleSunday = new ArrayList<>();
        int numberOfWeekday;
        for (int i = 0; i < loadedSchedule.size(); i++) {
            numberOfWeekday = (int) loadedSchedule.get(i).get("day_of_week");
            switch (numberOfWeekday){
                case 1:
                    scheduleMonday.add(loadedSchedule.get(i));
                    break;
                case 2:
                    scheduleTuesday.add(loadedSchedule.get(i));
                    break;
                case 3:
                    scheduleWednesday.add(loadedSchedule.get(i));
                    break;
                case 4:
                    scheduleThursday.add(loadedSchedule.get(i));
                    break;
                case 5:
                    scheduleFriday.add(loadedSchedule.get(i));
                    break;
                case 6:
                    scheduleSaturday.add(loadedSchedule.get(i));
                    break;
                case 7:
                    scheduleSunday.add(loadedSchedule.get(i));
                    break;
            }
        }
        List<List<Map>> scheduleForGym = new ArrayList<>();
        if (scheduleSunday.size() > 0) {
            scheduleForGym.add(scheduleSunday);
        }
        if (scheduleMonday.size() > 0) {
            scheduleForGym.add(scheduleMonday);
        }
        if (scheduleTuesday.size() > 0) {
            scheduleForGym.add(scheduleTuesday);
        }
        if (scheduleWednesday.size() > 0) {
            scheduleForGym.add(scheduleWednesday);
        }
        if (scheduleThursday.size() > 0) {
            scheduleForGym.add(scheduleThursday);
        }
        if (scheduleFriday.size() > 0) {
            scheduleForGym.add(scheduleFriday);
        }
        if (scheduleSaturday.size() > 0) {
            scheduleForGym.add(scheduleSaturday);
        }
        return scheduleForGym;
    }

    private List<List<Map>> getGymSchedule(int selectedGym) {
        return  (selectedGym == GYM_PARNAS) ? scheduleParnas : scheduleMyzhestvo;
    }

    public boolean checkNetwork() {
        NetworkCheck network = new NetworkCheck(getApplication());
        return network.checkConnection();
    }

    public boolean isSelectedGymParnas() {
        return selectedGym == GYM_PARNAS;
    }

    public void setSelectedDay(int newSelectedDay) {
        selectedDay = newSelectedDay;
    }

    public int getSelectedDay() {
        return selectedDay;
    }

    public void setToday(boolean newValue) {
        isToday = newValue;
    }

    public boolean getIsToday() {
        return isToday;
    }

    public Date getToday() {
        return today;
    }

    public Date getTomorrow() {
        return tomorrow;
    }

    public Date getAfterTomorrow() {
        return afterTomorrow;
    }

    public List<Map> getSchedule(Date date) {
        gregorianCalendar.setTime(date);
        selectedDay = gregorianCalendar.get(Calendar.DAY_OF_WEEK);
        if (date == today) {
            selectedDayForUri = 0;
            setToday(true);
        }
        if (date == tomorrow) {
            selectedDayForUri = 1;
            setToday(false);
        }
        if (date == afterTomorrow) {
            selectedDayForUri = 2;
            setToday(false);
        }
        return getGymSchedule(selectedGym).get(selectedDay - 1);
    }

    public void setSelectedGym(int newSelectedGym) {
        selectedGym = newSelectedGym;
    }

    public int getSelectedGym() {
        return selectedGym;
    }

    public int getSelectedDayForUri() {
        return selectedDayForUri;
    }
}
