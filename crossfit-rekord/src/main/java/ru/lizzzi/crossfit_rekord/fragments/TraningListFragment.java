package ru.lizzzi.crossfit_rekord.fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import ru.lizzzi.crossfit_rekord.R;
import ru.lizzzi.crossfit_rekord.interfaces.TitleChange;
import ru.lizzzi.crossfit_rekord.model.TrainingListViewModel;

public class TraningListFragment extends Fragment {

    private TrainingListViewModel viewModel;
    private List<Map> trainingList;

    public static final String TIME_START = "timeStart";
    public static final String TIME_END = "timeEnd";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_training_list, container, false);
        viewModel = ViewModelProviders.of(this).get(TrainingListViewModel.class);

        Bundle bundle = getArguments();
        viewModel.setTimeStart((bundle != null)
                ? bundle.getLong(TIME_START)
                : Calendar.getInstance().getTimeInMillis());

        viewModel.setTimeEnd((bundle != null)
                ? bundle.getLong(TIME_END)
                : Calendar.getInstance().getTimeInMillis());

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        TitleChange listenerTitleChange = (TitleChange) getActivity();
        if (listenerTitleChange != null) {
            listenerTitleChange.changeTitle(
                    R.string.title_TrainingList_Fragment,
                    R.string.title_MyResult_Fragment);
        }
        trainingList = viewModel.getTraining();
    }
}
