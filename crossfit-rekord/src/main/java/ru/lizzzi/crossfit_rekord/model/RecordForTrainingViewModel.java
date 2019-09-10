package ru.lizzzi.crossfit_rekord.model;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
    private MutableLiveData<Boolean> liveDataParnas;
    private MutableLiveData<Boolean> liveDataMyzhestvo;
    private BackendlessQueries backendlessQuery;
    private int GYM_PARNAS = 1;
    private int selectedDay;
    private boolean isToday;
    private Date today;
    private Date tomorrow;
    private Date afterTomorrow;
    private Calendar calendar;
    private int selectedDayForUri;
    private int selectedGym;


    public RecordForTrainingViewModel(@NonNull Application application) {
        super(application);
        backendlessQuery = new BackendlessQueries();
        calendar = Calendar.getInstance();
        selectedDay = calendar.get(Calendar.DAY_OF_WEEK);
        today = new Date();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        tomorrow = calendar.getTime();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        afterTomorrow = calendar.getTime();
        isToday = true;
        selectedGym = GYM_PARNAS;
        selectedDayForUri = 0;
    }

    public LiveData<Boolean> loadScheduleParnas() {
        if (liveDataParnas == null) {
            liveDataParnas = new MutableLiveData<>();
        }
        executor.execute(new Runnable() {
            @Override
            public void run() {
                List<Map> loadedSchedule = backendlessQuery.loadingSchedule(String.valueOf(selectedGym));
                if ((loadedSchedule != null) && !loadedSchedule.isEmpty()) {
                    scheduleParnas = splitRawSchedule(loadedSchedule);
                    liveDataParnas.postValue(true);
                } else {
                    liveDataParnas.postValue(false);
                }
            }
        });
        return liveDataParnas;
    }

    public LiveData<Boolean> loadScheduleMyzhestvo() {
        if (liveDataMyzhestvo == null) {
            liveDataMyzhestvo = new MutableLiveData<>();
        }
        executor.execute(new Runnable() {
            @Override
            public void run() {
                List<Map> loadedSchedule = backendlessQuery.loadingSchedule(String.valueOf(selectedGym));
                if ((loadedSchedule != null) && !loadedSchedule.isEmpty()) {
                    scheduleMyzhestvo = splitRawSchedule(loadedSchedule);
                    liveDataMyzhestvo.postValue(true);
                } else {
                    liveDataMyzhestvo.postValue(false);
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

    public List<Map> getGymSchedule() {
        return  (selectedGym == GYM_PARNAS)
                ? scheduleParnas.get(selectedDay - 1)
                : scheduleMyzhestvo.get(selectedDay - 1);
    }

    public LiveData<Boolean> checkNetwork() {
        final MutableLiveData<Boolean> liveDataConnection = new MutableLiveData<>();
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

    public boolean isSelectedGymParnas() {
        return selectedGym == GYM_PARNAS;
    }


    public int getSelectedDay() {
        return selectedDay;
    }

    public void setIsToday(boolean newValue) {
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

    public void setToday() {
        selectedDayForUri = 0;
        setIsToday(true);
        calendar.setTime(today);
        selectedDay = calendar.get(Calendar.DAY_OF_WEEK);
    }

    public void setTomorrow() {
        selectedDayForUri = 1;
        setIsToday(false);
        calendar.setTime(tomorrow);
        selectedDay = calendar.get(Calendar.DAY_OF_WEEK);
    }

    public void setAfterTomorrow() {
        selectedDayForUri = 2;
        setIsToday(false);
        calendar.setTime(afterTomorrow);
        selectedDay = calendar.get(Calendar.DAY_OF_WEEK);
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
