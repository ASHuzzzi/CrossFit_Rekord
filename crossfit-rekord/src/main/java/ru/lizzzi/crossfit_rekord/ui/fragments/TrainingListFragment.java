package ru.lizzzi.crossfit_rekord.ui.fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Calendar;

import ru.lizzzi.crossfit_rekord.R;
import ru.lizzzi.crossfit_rekord.ui.adapters.RecyclerAdapterTraining;
import ru.lizzzi.crossfit_rekord.interfaces.TitleChange;
import ru.lizzzi.crossfit_rekord.model.TrainingListViewModel;

public class TrainingListFragment extends Fragment {

    private TrainingListViewModel viewModel;
    private RecyclerAdapterTraining adapter;

    public static final String TIME_START = "timeStart";
    public static final String TIME_END = "timeEnd";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_training_list, container, false);
        viewModel = ViewModelProviders.of(this).get(TrainingListViewModel.class);

        Bundle bundle = getArguments();
        viewModel.setTimeStart((bundle != null)
                ? bundle.getLong(TIME_START)
                : Calendar.getInstance().getTimeInMillis());

        viewModel.setTimeEnd((bundle != null)
                ? bundle.getLong(TIME_END)
                : Calendar.getInstance().getTimeInMillis());

        RecyclerView recyclerView = view.findViewById(R.id.recyclerTraining);
        adapter = new RecyclerAdapterTraining(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
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
        adapter.add(viewModel.getTraining());
        adapter.notifyDataSetChanged();
    }

    public void openFragment(String dateSession) {
        viewModel.saveDateInPrefs(dateSession);
        try {
            WorkoutDetailsFragment fragment = new WorkoutDetailsFragment();
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
}
