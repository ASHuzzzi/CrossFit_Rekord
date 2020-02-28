package ru.lizzzi.crossfit_rekord.model;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.backendless.exceptions.BackendlessFault;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import ru.lizzzi.crossfit_rekord.backend.BackendApi;
import ru.lizzzi.crossfit_rekord.utils.NetworkCheck;
import ru.lizzzi.crossfit_rekord.items.ScheduleItem;
import ru.lizzzi.crossfit_rekord.items.ScheduleWeekly;
import ru.lizzzi.crossfit_rekord.interfaces.BackendResponseCallback;

public class RecordForTrainingViewModel extends AndroidViewModel {
    private ScheduleWeekly scheduleParnas, scheduleMyzhestvo;
    private Executor executor = new ThreadPoolExecutor(
            0,
            1,
            60,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>());
    private MutableLiveData<Boolean> liveDataParnas, liveDataMyzhestvo;
    private BackendApi backendApi;
    private int GYM_PARNAS = 1;
    private int selectedDay, selectedDayForUri, selectedGym;
    private boolean isToday;
    private Date today, tomorrow, afterTomorrow;
    private Calendar calendar;


    public RecordForTrainingViewModel(@NonNull Application application) {
        super(application);
        backendApi = new BackendApi();
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

    public LiveData<Boolean> getScheduleParnas() {
        if (liveDataParnas == null) {
            liveDataParnas = new MutableLiveData<>();
        }
        executor.execute(new Runnable() {
            @Override
            public void run() {
                backendApi.getScheduleWeekly(String.valueOf(selectedGym), new BackendResponseCallback<ScheduleWeekly>() {
                    @Override
                    public void handleSuccess(ScheduleWeekly scheduleWeekly) {
                        scheduleParnas = scheduleWeekly;
                        liveDataParnas.postValue(true);
                    }

                    @Override
                    public void handleFault(BackendlessFault fault) {
                        liveDataParnas.postValue(false);
                    }
                });
            }
        });
        return liveDataParnas;
    }

    public LiveData<Boolean> getScheduleMyzhestvo() {
        if (liveDataMyzhestvo == null) {
            liveDataMyzhestvo = new MutableLiveData<>();
        }
        executor.execute(new Runnable() {
            @Override
            public void run() {
                backendApi.getScheduleWeekly(String.valueOf(selectedGym), new BackendResponseCallback<ScheduleWeekly>() {
                    @Override
                    public void handleSuccess(ScheduleWeekly scheduleWeekly) {
                        scheduleMyzhestvo = scheduleWeekly;
                        liveDataMyzhestvo.postValue(true);
                    }

                    @Override
                    public void handleFault(BackendlessFault fault) {
                        liveDataMyzhestvo.postValue(false);
                    }
                });
            }
        });
        return liveDataMyzhestvo;
    }

    public List<ScheduleItem> getGymSchedule() {
        switch (selectedDay) {
            case Calendar.SUNDAY:
                return  (isSelectedGymParnas())
                        ? scheduleParnas.getScheduleSunday()
                        : scheduleMyzhestvo.getScheduleSunday();
            case Calendar.MONDAY:
            default:
                return  (isSelectedGymParnas())
                        ? scheduleParnas.getScheduleMonday()
                        : scheduleMyzhestvo.getScheduleMonday();
            case Calendar.TUESDAY:
                return  (isSelectedGymParnas())
                        ? scheduleParnas.getScheduleTuesday()
                        : scheduleMyzhestvo.getScheduleTuesday();
            case Calendar.WEDNESDAY:
                return  (isSelectedGymParnas())
                        ? scheduleParnas.getScheduleWednesday()
                        : scheduleMyzhestvo.getScheduleWednesday();
            case Calendar.THURSDAY:
                return  (isSelectedGymParnas())
                        ? scheduleParnas.getScheduleThursday()
                        : scheduleMyzhestvo.getScheduleThursday();
            case Calendar.FRIDAY:
                return  (isSelectedGymParnas())
                        ? scheduleParnas.getScheduleFriday()
                        : scheduleMyzhestvo.getScheduleFriday();
            case Calendar.SATURDAY:
                return  (isSelectedGymParnas())
                        ? scheduleParnas.getScheduleSaturday()
                        : scheduleMyzhestvo.getScheduleSaturday();
        }
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
