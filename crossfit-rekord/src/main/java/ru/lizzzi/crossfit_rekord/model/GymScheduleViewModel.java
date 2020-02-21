package ru.lizzzi.crossfit_rekord.model;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.backendless.exceptions.BackendlessFault;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import ru.lizzzi.crossfit_rekord.backendless.BackendlessQueries;
import ru.lizzzi.crossfit_rekord.inspectionСlasses.NetworkCheck;
import ru.lizzzi.crossfit_rekord.items.ScheduleItem;
import ru.lizzzi.crossfit_rekord.items.ScheduleWeekly;
import ru.lizzzi.crossfit_rekord.interfaces.BackendlessResponseCallback;

public class GymScheduleViewModel extends AndroidViewModel {

    private ScheduleWeekly scheduleParnas, scheduleMyzhestvo;
    private Executor executor = new ThreadPoolExecutor(
            0,
            1,
            60,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>());
    private MutableLiveData<Boolean> liveDataParnas, liveDataMyzhestvo;
    private BackendlessQueries backendlessQuery;
    private int GYM_PARNAS = 1;
    private int selectedDay, selectedGym;

    public GymScheduleViewModel(@NonNull Application application) {
        super(application);
        backendlessQuery = new BackendlessQueries();
        selectedDay = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        selectedGym = GYM_PARNAS;
    }

    public LiveData<Boolean> getScheduleParnas() {
        if (liveDataParnas == null) {
            liveDataParnas = new MutableLiveData<>();
        }
        executor.execute(new Runnable() {
            @Override
            public void run() {
                backendlessQuery.getScheduleWeekly(
                        String.valueOf(selectedGym),
                        new BackendlessResponseCallback<ScheduleWeekly>() {
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
                backendlessQuery.getScheduleWeekly(String.valueOf(selectedGym), new BackendlessResponseCallback<ScheduleWeekly>() {
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

    public List<ScheduleItem> getSchedule() {
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

    public void setSelectedDay(int selectedDay) {
        this.selectedDay = selectedDay;
    }

    public int getSelectedDay() {
        return selectedDay;
    }

    public void setSelectedGym(int selectedGym) {
        this.selectedGym = selectedGym;
    }

    public int getSelectedGym() {
        return selectedGym;
    }

    public List<Integer> setDaysWhenRecordingIsPossible() {
        Calendar calendar = Calendar.getInstance();
        List<Integer> daysWhenRecordingIsPossible = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            calendar.add(Calendar.DAY_OF_WEEK, i);
            daysWhenRecordingIsPossible.add(calendar.get(Calendar.DAY_OF_WEEK));
            calendar.clear();
            calendar = Calendar.getInstance();
        }
        return daysWhenRecordingIsPossible;
    }
}
