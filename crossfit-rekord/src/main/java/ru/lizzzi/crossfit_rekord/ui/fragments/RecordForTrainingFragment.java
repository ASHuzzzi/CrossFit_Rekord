package ru.lizzzi.crossfit_rekord.ui.fragments;

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

import java.text.SimpleDateFormat;
import java.util.Locale;

import ru.lizzzi.crossfit_rekord.R;
import ru.lizzzi.crossfit_rekord.ui.adapters.RecyclerAdapterRecordForTrainingSelect;
import ru.lizzzi.crossfit_rekord.utils.UriParser;
import ru.lizzzi.crossfit_rekord.interfaces.TitleChange;
import ru.lizzzi.crossfit_rekord.model.RecordForTrainingViewModel;

public class RecordForTrainingFragment extends Fragment {

    private LinearLayout layoutError;
    private RecyclerView layoutSchedule;
    private ProgressBar progressBar;
    private Button buttonToday;
    private Button buttonTomorrow;
    private Button buttonAfterTomorrow;
    private ImageView imageBackground;

    private RecyclerAdapterRecordForTrainingSelect adapter;
    private RecordForTrainingViewModel viewModel;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(
                R.layout.fragment_record_for_training_select,
                container,
                false);
        viewModel = ViewModelProviders.of(RecordForTrainingFragment.this).
                get(RecordForTrainingViewModel.class);

        Bundle bundle = getArguments();
        viewModel.setSelectedGym((bundle != null)
                ? bundle.getInt("gym")
                : getResources().getInteger(R.integer.selectSheduleParnas));

        initRecyclerViewSchedule(view);
        initButtonsOfDay(view);

        layoutError = view.findViewById(R.id.llEror_RfTS);
        layoutSchedule = view.findViewById(R.id.rvTrainingTime);
        progressBar = view.findViewById(R.id.pbRfTS);
        imageBackground = view.findViewById(R.id.iv_RfTS);

        Button buttonError = view.findViewById(R.id.button6);
        buttonError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkNetworkConnection();
            }
        });

        return view;
    }

    private void initRecyclerViewSchedule(View rootView) {
        adapter =
                new RecyclerAdapterRecordForTrainingSelect(RecordForTrainingFragment.this);
        RecyclerView recyclerViewSchedule = rootView.findViewById(R.id.rvTrainingTime);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerViewSchedule.setLayoutManager(layoutManager);
        recyclerViewSchedule.setAdapter(adapter);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initButtonsOfDay(View rootView) {
        SimpleDateFormat simpleDateFormat =
                new SimpleDateFormat("EEEE\n d MMM", Locale.getDefault());

        buttonToday = rootView.findViewById(R.id.btToday);
        buttonToday.setText(simpleDateFormat.format(viewModel.getToday()));
        buttonToday.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (!adapter.isEmpty()) {
                    viewModel.setToday();
                    adapter.setScheduleItems(viewModel.getGymSchedule());
                    adapter.notifyDataSetChanged();
                    setPressedButtons();
                }
                return true ;
            }
        });

        buttonTomorrow = rootView.findViewById(R.id.btTomorrow);
        buttonTomorrow.setText(simpleDateFormat.format(viewModel.getTomorrow()));
        buttonTomorrow.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (!adapter.isEmpty()) {
                    viewModel.setTomorrow();
                    adapter.setScheduleItems(viewModel.getGymSchedule());
                    adapter.notifyDataSetChanged();
                    setPressedButtons();
                }
                return true ;
            }
        });

        buttonAfterTomorrow = rootView.findViewById(R.id.btAfterTomorrow);
        buttonAfterTomorrow.setText(simpleDateFormat.format(viewModel.getAfterTomorrow()));
        buttonAfterTomorrow.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (!adapter.isEmpty()) {
                    viewModel.setAfterTomorrow();
                    adapter.setScheduleItems(viewModel.getGymSchedule());
                    adapter.notifyDataSetChanged();
                    setPressedButtons();
                }
                return true ;
            }
        });
    }

    private void setPressedButtons() {
        int selectedDay = viewModel.getSelectedDayForUri();
        switch (selectedDay) {
            case 0:
                pushButtons(true, false, false);
                break;
            case 1:
                pushButtons(false, true, false);
                break;
            case 2:
                pushButtons(false, false, true);
                break;
        }
    }

    //метод применяющий выбор кнопок
    private void pushButtons(boolean today, boolean tomorrow, boolean afterTomorrow) {
        buttonToday.setPressed(today);
        buttonTomorrow.setPressed(tomorrow);
        buttonAfterTomorrow.setPressed(afterTomorrow);
    }

    @Override
    public void onStart() {
        super.onStart();
        TitleChange listenerTitleChange = (TitleChange) getActivity();
        if (listenerTitleChange != null) {
            listenerTitleChange.changeTitle(
                    R.string.title_RecordForTraining_Fragment,
                    R.string.title_RecordForTraining_Fragment);
        }

        if (adapter.isEmpty()) {
            checkNetworkConnection();
        } else {
            setPressedButtons();
            layoutSchedule.setVisibility(View.VISIBLE);
        }

        int backgroundImage =
                (viewModel.isSelectedGymParnas())
                ? R.drawable.background_foto_1
                : R.drawable.background_foto_2;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            imageBackground.setImageDrawable(getResources().getDrawable(
                    backgroundImage,
                    getActivity().getTheme()));
        } else {
            imageBackground.setImageDrawable(getResources().getDrawable(backgroundImage));
        }
    }

    private void checkNetworkConnection() {
        progressBar.setVisibility(View.VISIBLE);
        layoutSchedule.setVisibility(View.INVISIBLE);
        layoutError.setVisibility(View.INVISIBLE);
        LiveData<Boolean> liveDataConnection = viewModel.checkNetwork();
        liveDataConnection.observe(RecordForTrainingFragment.this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isConnected) {
                if (isConnected) {
                    viewModel.setIsToday(true);
                    if (viewModel.isSelectedGymParnas()) {
                        loadScheduleParnas();
                    } else {
                        loadScheduleMyzhestvo();
                    }
                } else {
                    layoutError.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    private void loadScheduleParnas() {
        LiveData<Boolean> liveDataParnas = viewModel.getScheduleParnas();
        setObserveForLiveData(liveDataParnas);
    }

    private void loadScheduleMyzhestvo() {
        LiveData<Boolean> liveDataMyzhestvo = viewModel.getScheduleMyzhestvo();
        setObserveForLiveData(liveDataMyzhestvo);
    }

    private void setObserveForLiveData(LiveData<Boolean> listLiveData) {
        listLiveData.observe(RecordForTrainingFragment.this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean scheduleIsLoaded) {
                if (scheduleIsLoaded) {
                    setPressedButtons();
                    adapter.setScheduleItems(viewModel.getGymSchedule());
                    adapter.notifyDataSetChanged();
                } else {
                    layoutError.setVisibility(View.VISIBLE);
                }
                layoutSchedule.setVisibility((scheduleIsLoaded) ? View.VISIBLE : View.INVISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    public void openBrowserForRecording(String startTime, String workoutType) {
        UriParser uriParser = new UriParser();
        Uri uri = uriParser.getURI(
                viewModel.getSelectedGym(),
                viewModel.getSelectedDayForUri(),
                startTime,
                workoutType);
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        intent.setData(uri);
        startActivity(intent);
        requireActivity().overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
    }

    public boolean isToday() {
        return viewModel.getIsToday();
    }
}