package ru.lizzzi.crossfit_rekord.fragments;

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

import java.util.Map;

import ru.lizzzi.crossfit_rekord.R;
import ru.lizzzi.crossfit_rekord.interfaces.TitleChange;
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
            if (viewModel.canShowWodDetails()) {
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
        LiveData<Map<String, String>> liveData = viewModel.getWorkout();
        liveData.observe(WorkoutExerciseFragment.this, new Observer<Map<String, String>>() {
            @Override
            public void onChanged(Map<String, String> wodOfDay) {
                progressBar.setVisibility(View.INVISIBLE);
                if (wodOfDay != null && !wodOfDay.isEmpty()) {
                    showWorkoutExercise(wodOfDay);
                } else {
                    textEmptyData.setText(getResources().getText(R.string.TL1NoData1));
                    layoutEmptyData.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void showWorkoutExercise(Map<String, String> wodOfDay) {
        layoutMain.setVisibility(View.VISIBLE);

        if (viewModel.checkMapValueOnEmptiness(wodOfDay, WorkoutExerciseViewModel.WARM_UP)) {
            cardWarmUp.setVisibility(View.GONE);
        } else {
            cardWarmUp.setVisibility(View.VISIBLE);
            textWarmUp.setText(wodOfDay.get(WorkoutExerciseViewModel.WARM_UP));
        }

        if (viewModel.checkMapValueOnEmptiness(wodOfDay, WorkoutExerciseViewModel.SKILL)) {
            cardSkill.setVisibility(View.GONE);
        } else {
            cardSkill.setVisibility(View.VISIBLE);
            textSkill.setText(wodOfDay.get(WorkoutExerciseViewModel.SKILL));
        }

        if (viewModel.checkMapValueOnEmptiness(wodOfDay, WorkoutExerciseViewModel.WOD)) {
            cardWOD.setVisibility(View.GONE);
        } else {
            cardWOD.setVisibility(View.VISIBLE);
            textWOD.setText(wodOfDay.get(WorkoutExerciseViewModel.WOD));
        }

        if (viewModel.checkMapValueOnEmptiness(wodOfDay, WorkoutExerciseViewModel.SC)) {
            cardSc.setVisibility(View.GONE);
        } else {
            cardSc.setVisibility(View.VISIBLE);
            textLevelSc.setText(wodOfDay.get(WorkoutExerciseViewModel.SC));
        }

        if (viewModel.checkMapValueOnEmptiness(wodOfDay, WorkoutExerciseViewModel.RX)) {
            cardRx.setVisibility(View.GONE);
        } else {
            cardRx.setVisibility(View.VISIBLE);
            textLevelRx.setText(wodOfDay.get(WorkoutExerciseViewModel.RX));
        }

        if (viewModel.checkMapValueOnEmptiness(wodOfDay, WorkoutExerciseViewModel.RX_PLUS)) {
            cardRxPlus.setVisibility(View.GONE);
        } else {
            cardRxPlus.setVisibility(View.VISIBLE);
            textLevelRxPlus.setText(wodOfDay.get(WorkoutExerciseViewModel.RX_PLUS));
        }

        if (viewModel.checkMapValueOnEmptiness(wodOfDay, WorkoutExerciseViewModel.POST_WORKOUT)) {
            cardPostWorkout.setVisibility(View.GONE);
        } else {
            cardPostWorkout.setVisibility(View.VISIBLE);
            textPostWorkout.setText(wodOfDay.get(WorkoutExerciseViewModel.POST_WORKOUT));
        }
    }
}
