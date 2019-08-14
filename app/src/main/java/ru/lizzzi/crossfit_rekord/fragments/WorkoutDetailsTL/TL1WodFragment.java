package ru.lizzzi.crossfit_rekord.fragments.WorkoutDetailsTL;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
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
import ru.lizzzi.crossfit_rekord.model.TL1WodViewModel;

public class TL1WodFragment extends Fragment {

    private TextView textWarmUp;
    private TextView textSkill;
    private TextView textWOD;
    private TextView textLevelSc;
    private TextView textLevelRx;
    private TextView textLevelRxPlus;
    private LinearLayout layoutMain;
    private LinearLayout layoutError;
    private LinearLayout layoutEmptyData;
    private TextView textEmptyData;
    private ProgressBar progressBar;

    private TL1WodViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tl1wod, container, false);
        viewModel = ViewModelProviders.of(TL1WodFragment.this).get(TL1WodViewModel.class);

        textWarmUp = view.findViewById(R.id.tvWarmUp);
        textSkill = view.findViewById(R.id.tvSkill);
        textWOD = view.findViewById(R.id.tvWOD);
        textLevelSc = view.findViewById(R.id.tvLevelSc);
        textLevelRx = view.findViewById(R.id.tvLevelRx);
        textLevelRxPlus = view.findViewById(R.id.tvLevelRxplus);
        textEmptyData = view.findViewById(R.id.tvTL1ED1);

        layoutMain = view.findViewById(R.id.llMain);
        Button buttonError = view.findViewById(R.id.button5);
        layoutError = view.findViewById(R.id.Layout_Error);
        layoutEmptyData = view.findViewById(R.id.llEmptyData);
        progressBar = view.findViewById(R.id.progressBar3);

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
            if (viewModel.canShowWodDetails()) {
                checkNetworkConnection();
            } else {
                textEmptyData.setText(getResources().getText(R.string.TL1NoTime1));
                progressBar.setVisibility(View.INVISIBLE);
                layoutEmptyData.setVisibility(View.VISIBLE);
            }
        }

        TitleChange listenerTitleChange = (TitleChange) getActivity();
        if (listenerTitleChange != null) {
            listenerTitleChange.changeTitle(
                    R.string.title_WorkoutDetails_Fragment,
                    R.string.title_CalendarWod_Fragment);
        }
    }

    private void checkNetworkConnection() {
        layoutError.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        if (viewModel.checkNetwork()) {
            LiveData<Map<String, String>> liveData = viewModel.getWorkout("exercises");
            liveData.observe(TL1WodFragment.this, new Observer<Map<String, String>>() {
                @Override
                public void onChanged(Map<String, String> wodOfDay) {
                    if (wodOfDay != null && wodOfDay.size() > 0) {
                        textWarmUp.setText(wodOfDay.get("warmup"));
                        textSkill.setText(wodOfDay.get("skill"));
                        textWOD.setText(wodOfDay.get("wod"));
                        textLevelSc.setText(wodOfDay.get("Sc"));
                        textLevelRx.setText(wodOfDay.get("Rx"));
                        textLevelRxPlus.setText(wodOfDay.get("Rxplus"));
                        layoutMain.setVisibility(View.VISIBLE);
                    } else {
                        textEmptyData.setText(getResources().getText(R.string.TL1NoData1));
                        layoutEmptyData.setVisibility(View.VISIBLE);
                    }
                    progressBar.setVisibility(View.INVISIBLE);
                }
            });
        } else {
            layoutError.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
        }
    }
}
