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

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import ru.lizzzi.crossfit_rekord.R;
import ru.lizzzi.crossfit_rekord.adapters.RecyclerAdapterRecordForTrainingSelect;
import ru.lizzzi.crossfit_rekord.inspectionСlasses.UriParser;
import ru.lizzzi.crossfit_rekord.interfaces.ChangeTitle;
import ru.lizzzi.crossfit_rekord.interfaces.ListenerRecordForTrainingSelect;
import ru.lizzzi.crossfit_rekord.model.RecordForTrainingViewModel;

public class RecordForTrainingFragment extends Fragment {

    private LinearLayout linLayoutError;
    private LinearLayout linLayoutSchedule;
    private RecyclerView recyclerViewSchedule;
    private ProgressBar progressBar;
    private Button buttonToday;
    private Button buttonTomorrow;
    private Button buttonAfterTomorrow;
    private ImageView imageBackground;

    private RecyclerAdapterRecordForTrainingSelect adapter;
    private RecordForTrainingViewModel viewModel;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_record_for_training_select, container, false);
        viewModel = ViewModelProviders.of(RecordForTrainingFragment.this).
                get(RecordForTrainingViewModel.class);
        Objects.requireNonNull(getActivity()).setTitle(R.string.title_RecordForTraining_Fragment);

        buttonToday = view.findViewById(R.id.btToday);
        buttonTomorrow = view.findViewById(R.id.btTommorow);
        buttonAfterTomorrow = view.findViewById(R.id.btAftertommorow);
        Button buttonError = view.findViewById(R.id.button6);
        recyclerViewSchedule = view.findViewById(R.id.rvTrainingTime);
        linLayoutError = view.findViewById(R.id.llEror_RfTS);
        linLayoutSchedule = view.findViewById(R.id.llListTime);
        progressBar = view.findViewById(R.id.pbRfTS);
        imageBackground = view.findViewById(R.id.iv_RfTS);

        Bundle bundle = getArguments();
        viewModel.setSelectedGym((bundle != null)
                ? bundle.getInt("gym")
                : Objects.requireNonNull(
                        getContext()).getResources().getInteger(R.integer.selectSheduleParnas));

        final SimpleDateFormat simpleDateFormat =
                new SimpleDateFormat("EEE.\n d MMMM", Locale.getDefault());
        buttonToday.setText(simpleDateFormat.format(viewModel.getToday()));
        buttonTomorrow.setText(simpleDateFormat.format(viewModel.getTomorrow()));
        buttonAfterTomorrow.setText(simpleDateFormat.format(viewModel.getAfterTomorrow()));

        buttonError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                linLayoutError.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                checkNetworkConnection();
            }
        });

        buttonToday.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (adapter != null) {
                    drawList(viewModel.getSchedule(viewModel.getToday()));
                }
                return true ;
            }
        });

        buttonTomorrow.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (adapter != null) {
                    drawList(viewModel.getSchedule(viewModel.getTomorrow()));
                }
                return true ;
            }
        });

        buttonAfterTomorrow.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (adapter != null) {
                    drawList(viewModel.getSchedule(viewModel.getAfterTomorrow()));
                }
                return true ;
            }
        });
        return view;
    }

    private void drawList(List<Map> dailySchedule){
        adapter = new RecyclerAdapterRecordForTrainingSelect(
                getContext(),
                dailySchedule,
                viewModel.getIsToday(),
                new ListenerRecordForTrainingSelect() {
            @Override
            public void selectTime(String startTime, String typesItem) {
                if (startTime.equals("outTime") && typesItem.equals("outTime")) {
                    Toast toast = Toast.makeText(
                            getContext(),
                            "Тренировка уже прошла. Выбери более позднее время!",
                            Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                } else {
                    UriParser uriParser = new UriParser();
                    Uri uri = uriParser.getURI(
                            viewModel.getSelectedGym(),
                            viewModel.getSelectedDayForUri(),
                            startTime,
                            typesItem);
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.addCategory(Intent.CATEGORY_BROWSABLE);
                    intent.setData(uri);
                    startActivity(intent);
                    Objects.requireNonNull(getActivity()).
                            overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
                }
            }
        });

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerViewSchedule.setLayoutManager(mLayoutManager);
        recyclerViewSchedule.setAdapter(adapter);
        setPressedButtons(viewModel.getSelectedDayForUri());
    }

    private void setPressedButtons(int selectedDay) {
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
        if (adapter == null) {
            linLayoutSchedule.setVisibility(View.INVISIBLE);
            linLayoutError.setVisibility(View.INVISIBLE);
            checkNetworkConnection();
        } else {
            setPressedButtons(viewModel.getSelectedDayForUri());
            linLayoutSchedule.setVisibility(View.VISIBLE);
        }

        if (getActivity() instanceof ChangeTitle) {
            ChangeTitle listernerChangeTitle = (ChangeTitle) getActivity();
            listernerChangeTitle.changeTitle(R.string.title_RecordForTraining_Fragment, R.string.title_RecordForTraining_Fragment);
        }
        int backgroundImage =
                (viewModel.isSelectedGymParnas())
                ? R.drawable.background_foto_1
                : R.drawable.background_foto_2;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            imageBackground.setImageDrawable(getResources().getDrawable(
                    backgroundImage, Objects.requireNonNull(getContext()).getTheme()));
        } else {
            imageBackground.setImageDrawable(getResources().getDrawable(backgroundImage));
        }
    }

    private void checkNetworkConnection() {
        if (viewModel.checkNetwork()) {
            linLayoutError.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            viewModel.setToday(true);
            if (viewModel.isSelectedGymParnas()) {
                loadScheduleParnas();
            } else {
                loadScheduleMyzhestvo();
            }
        } else {
            linLayoutError.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    private void loadScheduleParnas() {
        LiveData<List<List<Map>>> liveDataParnas = viewModel.loadScheduleParnas();
        setObserveForLiveData(liveDataParnas);
    }

    private void loadScheduleMyzhestvo() {
        LiveData<List<List<Map>>> liveDataMyzhestvo = viewModel.loadScheduleMyzhestvo();
        setObserveForLiveData(liveDataMyzhestvo);
    }

    private void setObserveForLiveData(LiveData<List<List<Map>>> listLiveData) {
        listLiveData.observe(RecordForTrainingFragment.this, new Observer<List<List<Map>>>() {
            @Override
            public void onChanged(@Nullable List<List<Map>> lists) {
                if (lists != null) {
                    drawList(lists.get(viewModel.getSelectedDay() - 1));
                    linLayoutError.setVisibility(View.INVISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                    linLayoutSchedule.setVisibility(View.VISIBLE);
                } else {
                    linLayoutError.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                    linLayoutSchedule.setVisibility(View.INVISIBLE);
                }
            }
        });
    }
}