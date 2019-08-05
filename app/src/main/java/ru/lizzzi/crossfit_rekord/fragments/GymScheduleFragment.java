package ru.lizzzi.crossfit_rekord.fragments;

import android.annotation.SuppressLint;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import ru.lizzzi.crossfit_rekord.R;
import ru.lizzzi.crossfit_rekord.adapters.RecyclerAdapterTable;
import ru.lizzzi.crossfit_rekord.inspectionСlasses.UriParser;
import ru.lizzzi.crossfit_rekord.interfaces.ChangeTitle;
import ru.lizzzi.crossfit_rekord.interfaces.ListenerRecordForTrainingSelect;
import ru.lizzzi.crossfit_rekord.model.GymScheduleViewModel;

public class GymScheduleFragment extends Fragment {
    private ProgressBar progressBar;
    private RecyclerView itemsInTable;
    private Button buttonMonday;
    private Button buttonTuesday;
    private Button buttonWednesday;
    private Button buttonThursday;
    private Button buttonFriday;
    private Button buttonSaturday;
    private Button buttonSunday;
    private LinearLayout layoutError;
    private ImageView imageTable;

    private RecyclerAdapterTable adapter; //адаптер для списка тренировок
    private GymScheduleViewModel viewModel;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_table, container, false);
        viewModel = ViewModelProviders.of(GymScheduleFragment.this)
                .get(GymScheduleViewModel.class);

        buttonMonday = view.findViewById(R.id.day_1);
        buttonTuesday = view.findViewById(R.id.day_2);
        buttonWednesday = view.findViewById(R.id.day_3);
        buttonThursday = view.findViewById(R.id.day_4);
        buttonFriday = view.findViewById(R.id.day_5);
        buttonSaturday = view.findViewById(R.id.day_6);
        buttonSunday = view.findViewById(R.id.day_7);
        Button buttonError = view.findViewById(R.id.button5);
        layoutError = view.findViewById(R.id.Layout_Error);
        progressBar = view.findViewById(R.id.progressBar);
        itemsInTable = view.findViewById(R.id.lvTable);
        imageTable = view.findViewById(R.id.ivTable);

        layoutError.setVisibility(View.INVISIBLE);
        itemsInTable.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);

        Bundle bundle = getArguments();
        viewModel.setSelectedGym((bundle != null)
                ? bundle.getInt("gym")
                : Objects.requireNonNull(
                getContext()).getResources().getInteger(R.integer.selectSheduleParnas));

        buttonError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                layoutError.setVisibility(View.INVISIBLE);
                checkNetworkConnection();
            }
        });

        buttonMonday.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                takeOnButtonClick(Calendar.MONDAY);
                return true ;
            }
        });

        buttonTuesday.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                takeOnButtonClick(Calendar.TUESDAY);
                return true ;
            }
        });

        buttonWednesday.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                takeOnButtonClick(Calendar.WEDNESDAY);
                return true ;
            }
        });

        buttonThursday.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                takeOnButtonClick(Calendar.THURSDAY);
                return true ;
            }
        });

        buttonFriday.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                takeOnButtonClick(Calendar.FRIDAY);
                return true ;
            }
        });

        buttonSaturday.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                takeOnButtonClick(Calendar.SATURDAY);
                return true ;
            }
        });

        buttonSunday.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                takeOnButtonClick(Calendar.SUNDAY);
                return true ;
            }
        });
        return view;
    }

    private void takeOnButtonClick(int selectedDay) {
        if (adapter != null) {
            viewModel.setSelectedDay(selectedDay);
            showSchedule(
                    viewModel.getSchedule(viewModel.getSelectedGym())
                            .get(viewModel.getSelectedDay() - 1));
        }
    }

    private void showSchedule(final List<Map> dailySchedule){
        adapter = new RecyclerAdapterTable(getContext(), dailySchedule, new ListenerRecordForTrainingSelect() {
            @Override
            public void selectTime(String stStartTime, String stTypesItem) {
                Calendar calendar = Calendar.getInstance();
                List<Integer> daysWhenRecordingIsPossible = new ArrayList<>();
                for (int i=0; i < 3; i++) {
                    calendar.add(Calendar.DAY_OF_WEEK, i);
                    daysWhenRecordingIsPossible.add(calendar.get(Calendar.DAY_OF_WEEK));
                    calendar.clear();
                    calendar = Calendar.getInstance();
                }
                if (daysWhenRecordingIsPossible.contains(viewModel.getSelectedDay())) {
                    try {
                        SimpleDateFormat dateFormat =
                                new SimpleDateFormat("HH:mm", Locale.getDefault());
                        calendar = Calendar.getInstance();
                        int hourNow = calendar.get(Calendar.HOUR_OF_DAY);
                        Date selectTime = dateFormat.parse(stStartTime);
                        calendar.setTime(selectTime);
                        int selectHour = calendar.get(Calendar.HOUR_OF_DAY);
                        boolean selectedToday =
                                daysWhenRecordingIsPossible.get(0).equals(viewModel.getSelectedDay());
                        //проверяем чтобы выбранное время было позже чем сейчас
                        if (selectedToday && (selectHour <= hourNow)) {
                            Toast toast = Toast.makeText(
                                    getContext(), 
                                    "Выберите более позднее время.", 
                                    Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        } else {
                            int numberDayOfWeek =
                                    daysWhenRecordingIsPossible.indexOf(viewModel.getSelectedDay());
                            UriParser uriParser = new UriParser();
                            Uri uri = uriParser.getURI(
                                    viewModel.getSelectedGym(),
                                    numberDayOfWeek, 
                                    stStartTime, 
                                    stTypesItem);
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_VIEW);
                            intent.addCategory(Intent.CATEGORY_BROWSABLE);
                            intent.setData(uri);
                            startActivity(intent);
                            Objects.requireNonNull(getActivity()).overridePendingTransition(
                                    R.anim.pull_in_right,
                                    R.anim.push_out_left);
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } else {
                    String nameDayOfWeek = calendar.getDisplayName(
                            Calendar.DAY_OF_WEEK,
                            Calendar.LONG, Locale.getDefault());
                    Toast toast = Toast.makeText(
                            getContext(), 
                            "Запись возможна на сегодня (" + nameDayOfWeek  + ") и два дня вперед", 
                            Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            }
        });
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        itemsInTable.setLayoutManager(mLayoutManager);
        itemsInTable.setAdapter(adapter);
        setPressedButtons(viewModel.getSelectedDay());
    }

    //метод подготоавливающий состояние кнопок в зависимости от выбранного дня
    private void setPressedButtons(int selectedDayOfWeek) {
        unpressedButtons();
        switch (selectedDayOfWeek) {
            case Calendar.MONDAY:
                buttonMonday.setPressed(true);
                break;
            case Calendar.TUESDAY:
                buttonTuesday.setPressed(true);
                break;
            case Calendar.WEDNESDAY:
                buttonWednesday.setPressed(true);
                break;
            case Calendar.THURSDAY:
                buttonThursday.setPressed(true);
                break;
            case Calendar.FRIDAY:
                buttonFriday.setPressed(true);
                break;
            case Calendar.SATURDAY:
                buttonSaturday.setPressed(true);
                break;
            case Calendar.SUNDAY:
                buttonSunday.setPressed(true);
                break;
        }
    }

    //метод применяющий выбор кнопок
    private void unpressedButtons() {
        buttonMonday.setPressed(false);
        buttonTuesday.setPressed(false);
        buttonWednesday.setPressed(false);
        buttonThursday.setPressed(false);
        buttonFriday.setPressed(false);
        buttonSaturday.setPressed(false);
        buttonSunday.setPressed(false);
    }

    //в onStart делаем проверку на наличие данных в адаптаре. При первом запуске адаптер пустой и
    //будет запущен поток.
    //при возврате через кнопку back адаптер будет не пустым поток не запуститься. что сохранит
    //состояние адаптера в положении перед открытием нового фрагмента
    @Override
    public  void onStart() {
        super.onStart();
        if (adapter == null) {
            int UNPRESS_ALL_BUTTONS = 8;
            setPressedButtons(UNPRESS_ALL_BUTTONS);
            checkNetworkConnection();
        } else {
            setPressedButtons(viewModel.getSelectedDay());
        }

        if (getActivity() instanceof ChangeTitle) {
            ChangeTitle listernerChangeTitle = (ChangeTitle) getActivity();
            listernerChangeTitle.changeTitle(R.string.title_Table_Fragment, R.string.title_Table_Fragment);
        }

        int backgroundImage = (viewModel.isSelectedGymParnas())
                ? R.drawable.background_foto_1
                : R.drawable.background_foto_2;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            imageTable.setImageDrawable(getResources().getDrawable(
                    backgroundImage,
                    Objects.requireNonNull(getContext()).getTheme()));
        } else {
            imageTable.setImageDrawable(getResources().getDrawable(backgroundImage));
        }
    }

    private void checkNetworkConnection() {
        if (viewModel.checkNetwork()) {
            viewModel.setSelectedDay(Calendar.MONDAY);
            if (viewModel.isSelectedGymParnas()) {
                loadScheduleParnas();
            } else {
                loadScheduleMyzhestvo();
            }
        } else {
            layoutError.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    private void loadScheduleParnas() {
        LiveData<List<List<Map>>> liveDataParnas =
                viewModel.loadScheduleParnas();
        setObserveForLiveData(liveDataParnas);
    }

    private void loadScheduleMyzhestvo() {
        LiveData<List<List<Map>>> liveDataMyzhestvo =
                viewModel.loadScheduleMyzhestvo();
        setObserveForLiveData(liveDataMyzhestvo);
    }

    private void setObserveForLiveData(LiveData<List<List<Map>>> listLiveData) {
        listLiveData.observe(GymScheduleFragment.this, new Observer<List<List<Map>>>() {
            @Override
            public void onChanged(@Nullable List<List<Map>> lists) {
                if (lists != null) {
                    showSchedule(lists.get(viewModel.getSelectedDay() - 1));
                    layoutError.setVisibility(View.INVISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                    itemsInTable.setVisibility(View.VISIBLE);
                } else {
                    progressBar.setVisibility(View.INVISIBLE);
                    layoutError.setVisibility(View.VISIBLE);
                }
            }
        });
    }
}