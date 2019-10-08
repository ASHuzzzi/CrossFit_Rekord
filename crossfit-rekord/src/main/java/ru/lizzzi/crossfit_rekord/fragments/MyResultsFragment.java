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
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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
    private RelativeLayout layoutPreviousTraining;
    private TextView textLastDateSession;
    private TextView textLastWodLevel;
    private TextView textLastWod;
    private Button buttonMonthlyTraining;
    private Button buttonPreviousMonthlyTraining;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_result, container, false);
        viewModel = ViewModelProviders.of(this).get(MyResultViewModel.class);

        textMonthlyTraining = view.findViewById(R.id.textMonthlyTraining);
        textPreviousMonthlyTraining = view.findViewById(R.id.textPreviousMonthlyTraining);
        textTrainingDynamics = view.findViewById(R.id.textTrainingDynamics);
        textSc = view.findViewById(R.id.textSc);
        textRx = view.findViewById(R.id.textRx);
        textRxPlus = view.findViewById(R.id.textRxPlus);
        layoutPreviousTraining = view.findViewById(R.id.layoutPreviousTraining);
        textLastDateSession = view.findViewById(R.id.textLastDateSession);
        textLastWodLevel = view.findViewById(R.id.textLastWodLevel);
        textLastWod = view.findViewById(R.id.textLastWod);
        buttonMonthlyTraining = view.findViewById(R.id.buttonMonthlyTraining);
        buttonMonthlyTraining.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putLong(TraningListFragment.TIME_START, viewModel.getStartThisMonth());
                bundle.putLong(TraningListFragment.TIME_END, viewModel.getEndThisMonth());
                openFragment(TraningListFragment.class, bundle);
            }
        });
        buttonPreviousMonthlyTraining = view.findViewById(R.id.buttonPreviousMonthlyTraining);
        buttonPreviousMonthlyTraining.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putLong(TraningListFragment.TIME_START, viewModel.getStartPreviousMonth());
                bundle.putLong(TraningListFragment.TIME_END, viewModel.getStartThisMonth());
                openFragment(TraningListFragment.class, bundle);
            }
        });
        initButtonShowPreviousTraining(view);
        initButtonOneRepeatHighs(view);
        return view;
    }

    private void initButtonShowPreviousTraining(View rootView) {
        Button buttonShowPreviousTraining = rootView.findViewById(R.id.buttonShowPreviousTraining);
        buttonShowPreviousTraining.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.saveDateInPrefs();
                openFragment(WorkoutDetailsFragment.class, null);
            }
        });
    }

    private void initButtonOneRepeatHighs(View rootView) {
        Button buttonOneRepeatHighs = rootView.findViewById(R.id.buttonOneRepeatHighs);
        buttonOneRepeatHighs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFragment(OneRepeatHighsFragment.class, null);
            }
        });
    }

    private void openFragment(Class fragmentClass, Bundle bundle) {
        try {
            Fragment fragment = (Fragment) fragmentClass.newInstance();
            fragment.setArguments(bundle);
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
        } catch (Exception e) {
            e.printStackTrace();
        }

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
        String dynamics = "(" + viewModel.getTrainingRatio() + "%)";
        textTrainingDynamics.setText(dynamics);
        textSc.setText(String.valueOf(viewModel.getScLevel()));
        textRx.setText(String.valueOf(viewModel.getRxLevel()));
        textRxPlus.setText(String.valueOf(viewModel.getRxPlusLevel()));
        if (!viewModel.isLastTrainingEmpty()) {
            layoutPreviousTraining.setVisibility(View.VISIBLE);
            Date date = new Date();
            date.setTime(Long.parseLong(viewModel.getDateSession()));
            SimpleDateFormat dateFormat =
                    new SimpleDateFormat(" dd MMMM (EEEE)", Locale.getDefault());
            String formatDate = dateFormat.format(date);
            textLastDateSession.setText(formatDate);
            textLastWodLevel.setText(viewModel.getWodLevel());
            textLastWod.setText(viewModel.getWod());
        } else {
            layoutPreviousTraining.setVisibility(View.GONE);
        }
    }
}
