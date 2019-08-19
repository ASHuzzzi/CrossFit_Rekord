package ru.lizzzi.crossfit_rekord.fragments;

import android.app.Activity;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import ru.lizzzi.crossfit_rekord.activity.EnterResultActivity;
import ru.lizzzi.crossfit_rekord.R;
import ru.lizzzi.crossfit_rekord.adapters.RecyclerAdapterWorkoutDetails;
import ru.lizzzi.crossfit_rekord.model.WodResultViewModel;

public class WodResultFragment extends Fragment {

    private final int ACTIVITY_CODE = 0;

    private LinearLayout layoutMain;
    private RecyclerView recViewUsers;
    private LinearLayout layoutError;
    private ProgressBar progressBar;
    private Button buttonSaveResults;
    private RecyclerAdapterWorkoutDetails adapter;

    private WodResultViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_wod_result, container, false);
        viewModel =
                ViewModelProviders.of(WodResultFragment.this).get(WodResultViewModel.class);

        layoutMain = view.findViewById(R.id.linLayMain);
        recViewUsers = view.findViewById(R.id.recViewUsers);
        layoutError = view.findViewById(R.id.linLayError);
        progressBar = view.findViewById(R.id.progressBar);

        initButtonError(view);
        initButtonSaveResults(view);
        return view;
    }

    private void initButtonError(View rootView) {
        Button buttonError = rootView.findViewById(R.id.buttonError);
        buttonError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkNetworkConnection();
            }
        });
    }

    private void initButtonSaveResults(View rootView) {
        buttonSaveResults = rootView.findViewById(R.id.buttonSaveResults);
        buttonSaveResults.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isUserResultsAvailable = viewModel.userResultsAvailable();
                Intent intent = new Intent(view.getContext(), EnterResultActivity.class);
                if (isUserResultsAvailable) {
                    intent.putExtra("skill", viewModel.getUserResults().get("skill"));
                    intent.putExtra("level", viewModel.getUserResults().get("wodLevel"));
                    intent.putExtra("results", viewModel.getUserResults().get("wodResult"));
                }
                intent.putExtra("flag", isUserResultsAvailable);
                startActivityForResult(intent, ACTIVITY_CODE);
                Objects.requireNonNull(getActivity()).overridePendingTransition(
                        R.anim.pull_in_right,
                        R.anim.push_out_left);
            }
        });
    }

    @Override
    public  void onStart() {
        super.onStart();
        if (adapter == null) {
            checkNetworkConnection();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ACTIVITY_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                checkNetworkConnection();
            }
        }
    }

    private void checkNetworkConnection() {
        layoutError.setVisibility(View.INVISIBLE);
        layoutMain.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        LiveData<Boolean> liveDataConnection = viewModel.checkNetwork();
        liveDataConnection.observe(WodResultFragment.this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isConnected) {
                if (isConnected) {
                    LiveData<List<Map>> liveData = viewModel.getWorkoutResult();
                    liveData.observe(WodResultFragment.this, new Observer<List<Map>>() {
                        @Override
                        public void onChanged(@Nullable List<Map> results) {
                            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
                            recViewUsers.setLayoutManager(layoutManager);
                            if (results != null && !results.isEmpty()) {
                                adapter = new RecyclerAdapterWorkoutDetails(getContext(), results);
                                recViewUsers.setAdapter(adapter);
                            } else {
                                if (adapter != null) {
                                    recViewUsers.setAdapter(null);
                                }
                            }
                            recViewUsers.setAdapter(adapter);
                            progressBar.setVisibility(View.INVISIBLE);
                            layoutMain.setVisibility(View.VISIBLE);
                            buttonSaveResults.setText(
                                    viewModel.userResultsAvailable()
                                            ? R.string.strEditDeleteResult
                                            : R.string.strEnterResult);
                        }
                    });
                } else {
                    layoutError.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }
        });
    }
}