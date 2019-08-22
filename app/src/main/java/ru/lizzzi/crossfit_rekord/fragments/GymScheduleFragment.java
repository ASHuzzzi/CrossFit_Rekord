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
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import java.util.Calendar;
import java.util.Objects;

import ru.lizzzi.crossfit_rekord.R;
import ru.lizzzi.crossfit_rekord.adapters.RecyclerAdapterSchedule;
import ru.lizzzi.crossfit_rekord.inspectionСlasses.UriParser;
import ru.lizzzi.crossfit_rekord.interfaces.TitleChange;
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

    private RecyclerAdapterSchedule adapter; //адаптер для списка тренировок
    private GymScheduleViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_table, container, false);

        viewModel = ViewModelProviders.of(GymScheduleFragment.this)
                .get(GymScheduleViewModel.class);
        Bundle bundle = getArguments();
        viewModel.setSelectedGym((bundle != null)
                ? bundle.getInt("gym")
                : getResources().getInteger(R.integer.selectSheduleParnas));

        initButtonsOfWeekDays(view);
        initButtonError(view);
        initItemsInTable(view);

        imageTable = view.findViewById(R.id.ivTable);

        progressBar = view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        layoutError = view.findViewById(R.id.linLayError);
        layoutError.setVisibility(View.INVISIBLE);

        return view;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initButtonsOfWeekDays(View rootView) {
        buttonMonday = rootView.findViewById(R.id.day_1);
        buttonMonday.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                takeOnButtonClick(Calendar.MONDAY);
                return true ;
            }
        });

        buttonTuesday = rootView.findViewById(R.id.day_2);
        buttonTuesday.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                takeOnButtonClick(Calendar.TUESDAY);
                return true ;
            }
        });

        buttonWednesday = rootView.findViewById(R.id.day_3);
        buttonWednesday.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                takeOnButtonClick(Calendar.WEDNESDAY);
                return true ;
            }
        });

        buttonThursday = rootView.findViewById(R.id.day_4);
        buttonThursday.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                takeOnButtonClick(Calendar.THURSDAY);
                return true ;
            }
        });

        buttonFriday = rootView.findViewById(R.id.day_5);
        buttonFriday.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                takeOnButtonClick(Calendar.FRIDAY);
                return true ;
            }
        });

        buttonSaturday = rootView.findViewById(R.id.day_6);
        buttonSaturday.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                takeOnButtonClick(Calendar.SATURDAY);
                return true ;
            }
        });

        buttonSunday = rootView.findViewById(R.id.day_7);
        buttonSunday.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                takeOnButtonClick(Calendar.SUNDAY);
                return true ;
            }
        });
    }

    private void initItemsInTable(View rootView) {
        adapter = new RecyclerAdapterSchedule(GymScheduleFragment.this);
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(getContext());
        itemsInTable = rootView.findViewById(R.id.lvTable);
        itemsInTable.setLayoutManager(layoutManager);
        itemsInTable.setAdapter(adapter);
        itemsInTable.setVisibility(View.INVISIBLE);
    }

    private void initButtonError(View rootView) {
        Button buttonError = rootView.findViewById(R.id.buttonError);
        buttonError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                layoutError.setVisibility(View.INVISIBLE);
                checkNetworkConnection();
            }
        });
    }

    private void takeOnButtonClick(int selectedDay) {
        if (!adapter.isEmpty()) {
            viewModel.setSelectedDay(selectedDay);
            adapter.setScheduleItems(viewModel.getSchedule());
            adapter.notifyDataSetChanged();
            setPressedButtons();
        }
    }

    //метод подготоавливающий состояние кнопок в зависимости от выбранного дня
    private void setPressedButtons() {
        unpressedButtons();
        int selectedDayOfWeek = viewModel.getSelectedDay();
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

    private void unpressedButtons() {
        buttonMonday.setPressed(false);
        buttonTuesday.setPressed(false);
        buttonWednesday.setPressed(false);
        buttonThursday.setPressed(false);
        buttonFriday.setPressed(false);
        buttonSaturday.setPressed(false);
        buttonSunday.setPressed(false);
    }

    @Override
    public  void onStart() {
        super.onStart();
        if (adapter.isEmpty()) {
            unpressedButtons();
            checkNetworkConnection();
        } else {
            setPressedButtons();
        }

        TitleChange listenerTitleChange = (TitleChange) getActivity();
        if (listenerTitleChange != null) {
            listenerTitleChange.changeTitle(
                    R.string.title_Table_Fragment,
                    R.string.title_Table_Fragment);
        }

        int backgroundImage = (viewModel.isSelectedGymParnas())
                ? R.drawable.background_foto_1
                : R.drawable.background_foto_2;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            imageTable.setImageDrawable(getResources().getDrawable(
                    backgroundImage, getActivity().getTheme()));
        } else {
            imageTable.setImageDrawable(getResources().getDrawable(backgroundImage));
        }
    }

    private void checkNetworkConnection() {
        LiveData<Boolean> liveDataConnection = viewModel.checkNetwork();
        liveDataConnection.observe(GymScheduleFragment.this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isConnected) {
                if (isConnected) {
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
        });
    }

    private void loadScheduleParnas() {
        LiveData<Boolean> liveDataParnas =
                viewModel.loadScheduleParnas();
        setObserveForLiveData(liveDataParnas);
    }

    private void loadScheduleMyzhestvo() {
        LiveData<Boolean> liveDataMyzhestvo =
                viewModel.loadScheduleMyzhestvo();
        setObserveForLiveData(liveDataMyzhestvo);
    }

    private void setObserveForLiveData(LiveData<Boolean> listLiveData) {
        listLiveData.observe(GymScheduleFragment.this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean scheduleIsLoaded) {
                if (scheduleIsLoaded) {
                    adapter.setScheduleItems(viewModel.getSchedule());
                    adapter.notifyDataSetChanged();
                    setPressedButtons();
                    itemsInTable.setVisibility(View.VISIBLE);
                }
                layoutError.setVisibility(
                        (scheduleIsLoaded)
                                ? View.INVISIBLE
                                : View.VISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

    public int getSelectedDay() {
        return viewModel.getSelectedDay();
    }

    public void openBrowserForRecording(int selectedDay, String startTime, String scheduleType) {
        UriParser uriParser = new UriParser();
        Uri uri = uriParser.getURI(viewModel.getSelectedGym(), selectedDay, startTime, scheduleType);
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        intent.setData(uri);
        startActivity(intent);
        Objects.requireNonNull(getActivity()).overridePendingTransition(
                R.anim.pull_in_right,
                R.anim.push_out_left);
    }
}