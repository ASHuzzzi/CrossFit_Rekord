package ru.lizzzi.crossfit_rekord.fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.Map;

import ru.lizzzi.crossfit_rekord.R;
import ru.lizzzi.crossfit_rekord.interfaces.TitleChange;
import ru.lizzzi.crossfit_rekord.model.MyResultViewModel;

public class MyResultsFragment extends Fragment {

    private MyResultViewModel viewModel;
    private TextView textMonthlyTraining;
    private TextView textPreviousMonthlyTraining;
    private TextView textTrainingDynamics;
    private TextView textSc;
    private TextView textRx;
    private TextView textRxPlus;
    private TextView textLastDateSession;
    private TextView textLastWodLevel;
    private TextView textLastWod;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_result, container, false);
        viewModel = ViewModelProviders.of(this).get(MyResultViewModel.class);

        initButtonOneRepeatHighs(view);
        textMonthlyTraining = view.findViewById(R.id.textMonthlyTraining);
        textPreviousMonthlyTraining = view.findViewById(R.id.textPreviousMonthlyTraining);
        textTrainingDynamics = view.findViewById(R.id.textTrainingDynamics);
        textSc = view.findViewById(R.id.textSc);
        textRx = view.findViewById(R.id.textRx);
        textRxPlus = view.findViewById(R.id.textRxPlus);
        textLastDateSession = view.findViewById(R.id.textLastDateSession);
        textLastWodLevel = view.findViewById(R.id.textLastWodLevel);
        textLastWod = view.findViewById(R.id.textLastWod);
        return view;
    }

    private void initButtonOneRepeatHighs(View rootView) {
        Button buttonOneRepeatHighs = rootView.findViewById(R.id.buttonOneRepeatHighs);
        buttonOneRepeatHighs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OneRepeatHighsFragment fragment = new OneRepeatHighsFragment();
                FragmentManager fragmentManager = getFragmentManager();
                if (fragmentManager != null) {
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.setCustomAnimations(
                            R.anim.pull_in_right,
                            R.anim.push_out_left,
                            R.anim.pull_in_left,
                            R.anim.push_out_right);
                    fragmentTransaction.replace(R.id.container, fragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        TitleChange listenerTitleChange = (TitleChange) getActivity();
        if (listenerTitleChange != null) {
            listenerTitleChange.changeTitle(
                    R.string.title_MyResult_Fragment,
                    R.string.title_MyResult_Fragment);
        }

        textMonthlyTraining.setText(String.valueOf(viewModel.getMonthlyTraining()));
        textPreviousMonthlyTraining.setText(String.valueOf(viewModel.getPreviousMonthlyTraining()));
        long percent =
                Math.round((((double) viewModel.getMonthlyTraining()/ (double) viewModel.getPreviousMonthlyTraining())-1)*100);
        String dynamics = "(" + percent + "%)";
        textTrainingDynamics.setText(dynamics);
        Map<String, String> lastTraining = viewModel.getLastTraining();
        textSc.setText(String.valueOf(viewModel.getScLevel()));
        textRx.setText(String.valueOf(viewModel.getRxLevel()));
        textRxPlus.setText(String.valueOf(viewModel.getRxPlusLevel()));
        textLastDateSession.setText(lastTraining.get("dateSession"));
        textLastWodLevel.setText(lastTraining.get("wodLevel"));
        textLastWod.setText(lastTraining.get("wod"));
    }
}
