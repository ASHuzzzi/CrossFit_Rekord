package ru.lizzzi.crossfit_rekord.ui.fragments;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import ru.lizzzi.crossfit_rekord.R;
import ru.lizzzi.crossfit_rekord.items.WodItem;
import ru.lizzzi.crossfit_rekord.model.WorkoutExerciseViewModel;

public class WorkoutExerciseFragment extends Fragment {

    private CardView cardWarmUp;
    private CardView cardSkill;
    private CardView cardWOD;
    private CardView cardSc;
    private CardView cardRx;
    private CardView cardRxPlus;
    private CardView cardPostWorkout;
    private TextView textWarmUp;
    private TextView textSkill;
    private TextView textWOD;
    private TextView textLevelSc;
    private TextView textLevelRx;
    private TextView textLevelRxPlus;
    private TextView textPostWorkout;
    private LinearLayout layoutMain;
    private LinearLayout layoutError;
    private LinearLayout layoutEmptyData;
    private TextView textEmptyData;
    private ProgressBar progressBar;

    private WorkoutExerciseViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(
                R.layout.fragment_workout_exercise,
                container,
                false);
        viewModel = ViewModelProviders.of(WorkoutExerciseFragment.this)
                .get(WorkoutExerciseViewModel.class);

        cardWarmUp = view.findViewById(R.id.cardWarmUp);
        cardSkill = view.findViewById(R.id.cardSkill);
        cardWOD = view.findViewById(R.id.cardWOD);
        cardSc = view.findViewById(R.id.cardSc);
        cardRx = view.findViewById(R.id.cardRx);
        cardRxPlus = view.findViewById(R.id.cardRxPlus);
        cardPostWorkout = view.findViewById(R.id.cardPostWorkout);

        textWarmUp = view.findViewById(R.id.tvWarmUp);
        textSkill = view.findViewById(R.id.tvSkill);
        textWOD = view.findViewById(R.id.tvWOD);
        textLevelSc = view.findViewById(R.id.tvLevelSc);
        textLevelRx = view.findViewById(R.id.tvLevelRx);
        textLevelRxPlus = view.findViewById(R.id.tvLevelRxplus);
        textPostWorkout = view.findViewById(R.id.tvPostWorkout);

        textEmptyData = view.findViewById(R.id.tvTL1ED1);

        layoutMain = view.findViewById(R.id.layoutMain);
        layoutError = view.findViewById(R.id.linLayError);
        layoutEmptyData = view.findViewById(R.id.llEmptyData);
        progressBar = view.findViewById(R.id.progressBar3);

        Button buttonError = view.findViewById(R.id.buttonError);
        buttonError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkNetworkConnection();
            }
        });
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (textWarmUp.length() < 1) {
            layoutMain.setVisibility(View.INVISIBLE);
            layoutEmptyData.setVisibility(View.INVISIBLE);
            layoutError.setVisibility(View.INVISIBLE);
            if (viewModel.canShowWod()) {
                checkNetworkConnection();
            } else {
                textEmptyData.setText(getResources().getText(R.string.TL1NoTime1));
                progressBar.setVisibility(View.INVISIBLE);
                layoutEmptyData.setVisibility(View.VISIBLE);
            }
        }
    }

    private void checkNetworkConnection() {
        layoutError.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        LiveData<Boolean> liveDataConnection = viewModel.checkNetwork();
        liveDataConnection.observe(WorkoutExerciseFragment.this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isConnected) {
                if (isConnected) {
                    loadWorkoutExercise();
                } else {
                    layoutError.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    private void loadWorkoutExercise() {
        LiveData<WodItem> liveData = viewModel.getWorkout();
        liveData.observe(WorkoutExerciseFragment.this, new Observer<WodItem>() {
            @Override
            public void onChanged(WodItem wodItem) {
                progressBar.setVisibility(View.INVISIBLE);
                if (wodItem != null && !wodItem.isEmpty()) {
                    showWorkoutExercise(wodItem);
                } else {
                    textEmptyData.setText(getResources().getText(R.string.TL1NoData1));
                    layoutEmptyData.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void showWorkoutExercise(WodItem wodOfDay) {
        layoutMain.setVisibility(View.VISIBLE);

        if (wodOfDay.getWarmUp().isEmpty()) {
            cardWarmUp.setVisibility(View.GONE);
        } else {
            cardWarmUp.setVisibility(View.VISIBLE);
            textWarmUp.setText(wodOfDay.getWarmUp());
        }

        if (wodOfDay.getSkill().isEmpty()) {
            cardSkill.setVisibility(View.GONE);
        } else {
            cardSkill.setVisibility(View.VISIBLE);
            textSkill.setText(wodOfDay.getSkill());
        }

        if (wodOfDay.getWod().isEmpty()) {
            cardWOD.setVisibility(View.GONE);
        } else {
            cardWOD.setVisibility(View.VISIBLE);
            textWOD.setText(wodOfDay.getWod());
        }

        if (wodOfDay.getSc().isEmpty()) {
            cardSc.setVisibility(View.GONE);
        } else {
            cardSc.setVisibility(View.VISIBLE);
            textLevelSc.setText(wodOfDay.getSc());
        }

        if (wodOfDay.getRx().isEmpty()) {
            cardRx.setVisibility(View.GONE);
        } else {
            cardRx.setVisibility(View.VISIBLE);
            textLevelRx.setText(wodOfDay.getRx());
        }

        if (wodOfDay.getRxPlus().isEmpty()) {
            cardRxPlus.setVisibility(View.GONE);
        } else {
            cardRxPlus.setVisibility(View.VISIBLE);
            textLevelRxPlus.setText(wodOfDay.getRxPlus());
        }

        if (wodOfDay.getPostWorkout().isEmpty()) {
            cardPostWorkout.setVisibility(View.GONE);
        } else {
            cardPostWorkout.setVisibility(View.VISIBLE);
            textPostWorkout.setText(wodOfDay.getPostWorkout());
        }
    }
}
