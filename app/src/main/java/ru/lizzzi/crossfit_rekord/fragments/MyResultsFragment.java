package ru.lizzzi.crossfit_rekord.fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import ru.lizzzi.crossfit_rekord.R;
import ru.lizzzi.crossfit_rekord.adapters.RecyclerAdapterMyResults;
import ru.lizzzi.crossfit_rekord.interfaces.TitleChange;
import ru.lizzzi.crossfit_rekord.model.MyResultsViewModel;

public class MyResultsFragment extends Fragment implements View.OnFocusChangeListener {

    private EditText etMyWeight;
    private EditText etDeadlift;
    private EditText etSnatch;
    private EditText etSquatClean;
    private EditText etClean;
    private EditText etSquatSnatches;
    private EditText etCleanAndJerk;
    private EditText etFrontSquat;
    private EditText etBackSquat;
    private EditText etShoulderPress;
    private EditText etPushPress;
    private EditText etBenchPress;
    private EditText etSumoDeadlift;
    private EditText etThruster;
    private EditText etDumbellSnatch;
    private EditText etRowM;
    private EditText etRowCal;
    private RecyclerView recyclerResults;

    private RecyclerAdapterMyResults adapter;

    private MyResultsViewModel viewModel;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_my_results, container, false);
        viewModel =
                ViewModelProviders.of(MyResultsFragment.this).get(MyResultsViewModel.class);

        recyclerResults = view.findViewById(R.id.recyclerResults);
        adapter = new RecyclerAdapterMyResults(MyResultsFragment.this);
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(getContext());
        recyclerResults = view.findViewById(R.id.recyclerResults);
        recyclerResults.setLayoutManager(layoutManager);
        recyclerResults.setAdapter(adapter);
        recyclerResults.setVisibility(View.INVISIBLE);

        etMyWeight = view.findViewById(R.id.etMyWeight);
        etDeadlift = view.findViewById(R.id.editResult);
        etSnatch = view.findViewById(R.id.etSnatch);
        etSquatClean = view.findViewById(R.id.etSquatClean);
        etClean = view.findViewById(R.id.etClean);
        etSquatSnatches = view.findViewById(R.id.etSquatSnatches);
        etCleanAndJerk = view.findViewById(R.id.etCleanAndJerk);
        etFrontSquat = view.findViewById(R.id.etFrontSquat);
        etBackSquat = view.findViewById(R.id.etBackSquat);
        etShoulderPress = view.findViewById(R.id.etShoulderPress);
        etPushPress = view.findViewById(R.id.etPushPress);
        etBenchPress = view.findViewById(R.id.etBenchPress);
        etSumoDeadlift = view.findViewById(R.id.etSumoDeadlift);
        etThruster = view.findViewById(R.id.etThruster);
        etDumbellSnatch = view.findViewById(R.id.etDumbellSnatch);
        etRowM = view.findViewById(R.id.etRowM);
        etRowCal = view.findViewById(R.id.etRowCal);

        etMyWeight.setOnFocusChangeListener(this);
        etDeadlift.setOnFocusChangeListener(this);
        etDeadlift.setOnFocusChangeListener(this);
        etSnatch.setOnFocusChangeListener(this);
        etSquatClean.setOnFocusChangeListener(this);
        etClean.setOnFocusChangeListener(this);
        etSquatSnatches.setOnFocusChangeListener(this);
        etCleanAndJerk.setOnFocusChangeListener(this);
        etFrontSquat.setOnFocusChangeListener(this);
        etBackSquat.setOnFocusChangeListener(this);
        etShoulderPress.setOnFocusChangeListener(this);
        etPushPress.setOnFocusChangeListener(this);
        etBenchPress.setOnFocusChangeListener(this);
        etSumoDeadlift.setOnFocusChangeListener(this);
        etThruster.setOnFocusChangeListener(this);
        etDumbellSnatch.setOnFocusChangeListener(this);
        etRowM.setOnFocusChangeListener(this);
        etRowCal.setOnFocusChangeListener(this);

        Button btnSaveMyResult = view.findViewById(R.id.btnSaveMyResult);
        btnSaveMyResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String stResult;
                String stExercise;

                if (etMyWeight.getText().toString().length() > 0){
                    stResult = etMyWeight.getText().toString();
                    stExercise = getResources().getString(R.string.strDBMyResultMyWeightEN);
                    saveResultInDB(stExercise, stResult);
                }

                if (etDeadlift.getText().toString().length() > 0){
                    stResult = etDeadlift.getText().toString();
                    stExercise = getResources().getString(R.string.strDBMyResultDeadliftEN);
                    saveResultInDB(stExercise, stResult);
                }

                if (etSnatch.getText().toString().length() > 0){
                    stResult = etSnatch.getText().toString();
                    stExercise = getResources().getString(R.string.strDBMyResultSnatchEN);
                    saveResultInDB(stExercise, stResult);
                }

                if (etSquatClean.getText().toString().length() > 0){
                    stResult = etSquatClean.getText().toString();
                    stExercise = getResources().getString(R.string.strDBMyResultSquatCleanEN);
                    saveResultInDB(stExercise, stResult);
                }

                if (etClean.getText().toString().length() > 0){
                    stResult = etClean.getText().toString();
                    stExercise = getResources().getString(R.string.strDBMyResultCleanEN);
                    saveResultInDB(stExercise, stResult);
                }

                if (etSquatSnatches.getText().toString().length() > 0){
                    stResult = etSquatSnatches.getText().toString();
                    stExercise = getResources().getString(R.string.strDBMyResultSquatSnatchesEN);
                    saveResultInDB(stExercise, stResult);
                }

                if (etCleanAndJerk.getText().toString().length() > 0){
                    stResult = etCleanAndJerk.getText().toString();
                    stExercise = getResources().getString(R.string.strDBMyResultCleanAndJerkEN);
                    saveResultInDB(stExercise, stResult);
                }

                if (etFrontSquat.getText().toString().length() > 0){
                    stResult = etFrontSquat.getText().toString();
                    stExercise = getResources().getString(R.string.strDBMyResultFrontSquatEN);
                    saveResultInDB(stExercise, stResult);
                }

                if (etBackSquat.getText().toString().length() > 0){
                    stResult = etBackSquat.getText().toString();
                    stExercise = getResources().getString(R.string.strDBMyResultBackSquatEN);
                    saveResultInDB(stExercise, stResult);
                }

                if (etShoulderPress.getText().toString().length() > 0){
                    stResult = etShoulderPress.getText().toString();
                    stExercise = getResources().getString(R.string.strDBMyResultShoulderPressEN);
                    saveResultInDB(stExercise, stResult);
                }

                if (etPushPress.getText().toString().length() > 0){
                    stResult = etPushPress.getText().toString();
                    stExercise = getResources().getString(R.string.strDBMyResultPushPressEN);
                    saveResultInDB(stExercise, stResult);
                }

                if (etBenchPress.getText().toString().length() > 0){
                    stResult = etBenchPress.getText().toString();
                    stExercise = getResources().getString(R.string.strDBMyResultBenchPressEN);
                    saveResultInDB(stExercise, stResult);
                }

                if (etSumoDeadlift.getText().toString().length() > 0){
                    stResult = etSumoDeadlift.getText().toString();
                    stExercise = getResources().getString(R.string.strDBMyResultSumoDeadliftEN);
                    saveResultInDB(stExercise, stResult);
                }

                if (etThruster.getText().toString().length() > 0){
                    stResult = etThruster.getText().toString();
                    stExercise = getResources().getString(R.string.strDBMyResultThrusterEN);
                    saveResultInDB(stExercise, stResult);
                }

                if (etDumbellSnatch.getText().toString().length() > 0){
                    stResult = etDumbellSnatch.getText().toString();
                    stExercise = getResources().getString(R.string.strDBMyResultDumbellSnatchEN);
                    saveResultInDB(stExercise, stResult);
                }

                if (etRowM.getText().toString().length() > 0){
                    stResult = etRowM.getText().toString();
                    stExercise = getResources().getString(R.string.strDBMyResultRowMEN);
                    saveResultInDB(stExercise, stResult);
                }

                if (etRowCal.getText().toString().length() > 0){
                    stResult = etRowCal.getText().toString();
                    stExercise = getResources().getString(R.string.strDBMyResultRowCalEN);
                    saveResultInDB(stExercise, stResult);
                }
                //prepareEditText();
                Toast.makeText(getContext(), "Данные обновлены", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    @Override
    public  void onStart() {
        super.onStart();

        TitleChange listenerTitleChange = (TitleChange) getActivity();
        if (listenerTitleChange != null) {
            listenerTitleChange.changeTitle(
                    R.string.title_MyResults_Fragment,
                    R.string.title_MyResults_Fragment);
        }

        if (adapter.isEmpty()) {
            adapter.setExercise(viewModel.getResults());
            recyclerResults.setVisibility(View.VISIBLE);
        }

        //prepareEditText();
    }

    /*private void prepareEditText(){
        Map<String, String> mapExercise;
        mapExercise = viewModel.getResults();

        String stExercise;
        String stResult;
        String stEmpty = getResources().getString(R.string.empty);
        String stShow;

        stExercise = getResources().getString(R.string.strDBMyResultMyWeightEN);
        if (mapExercise.containsKey(stExercise)){
            stResult = mapExercise.get(stExercise);
            stShow = stEmpty + stResult;
            etMyWeight.setText(stShow);
        }

        stExercise = getResources().getString(R.string.strDBMyResultDeadliftEN);
        if (mapExercise.containsKey(stExercise)){
            stResult = mapExercise.get(stExercise);
            stShow = stEmpty + stResult;
            etDeadlift.setText(stShow);
        }

        stExercise = getResources().getString(R.string.strDBMyResultSnatchEN);
        if (mapExercise.containsKey(stExercise)){
            stResult = mapExercise.get(stExercise);
            stShow = stEmpty + stResult;
            etSnatch.setText(stShow);
        }

        stExercise = getResources().getString(R.string.strDBMyResultSquatCleanEN);
        if (mapExercise.containsKey(stExercise)){
            stResult = mapExercise.get(stExercise);
            stShow = stEmpty + stResult;
            etSquatClean.setText(stShow);
        }

        stExercise = getResources().getString(R.string.strDBMyResultCleanEN);
        if (mapExercise.containsKey(stExercise)){
            stResult = mapExercise.get(stExercise);
            stShow = stEmpty + stResult;
            etClean.setText(stShow);
        }

        stExercise = getResources().getString(R.string.strDBMyResultSquatSnatchesEN);
        if (mapExercise.containsKey(stExercise)){
            stResult = mapExercise.get(stExercise);
            stShow = stEmpty + stResult;
            etSquatSnatches.setText(stShow);
        }

        stExercise = getResources().getString(R.string.strDBMyResultCleanAndJerkEN);
        if (mapExercise.containsKey(stExercise)){
            stResult = mapExercise.get(stExercise);
            stShow = stEmpty + stResult;
            etCleanAndJerk.setText(stShow);
        }

        stExercise = getResources().getString(R.string.strDBMyResultFrontSquatEN);
        if (mapExercise.containsKey(stExercise)){
            stResult = mapExercise.get(stExercise);
            stShow = stEmpty + stResult;
            etFrontSquat.setText(stShow);
        }

        stExercise = getResources().getString(R.string.strDBMyResultBackSquatEN);
        if (mapExercise.containsKey(stExercise)){
            stResult = mapExercise.get(stExercise);
            stShow = stEmpty + stResult;
            etBackSquat.setText(stShow);
        }

        stExercise = getResources().getString(R.string.strDBMyResultShoulderPressEN);
        if (mapExercise.containsKey(stExercise)){
            stResult = mapExercise.get(stExercise);
            stShow = stEmpty + stResult;
            etShoulderPress.setText(stShow);
        }

        stExercise = getResources().getString(R.string.strDBMyResultPushPressEN);
        if (mapExercise.containsKey(stExercise)){
            stResult = mapExercise.get(stExercise);
            stShow = stEmpty + stResult;
            etPushPress.setText(stShow);
        }

        stExercise = getResources().getString(R.string.strDBMyResultBenchPressEN);
        if (mapExercise.containsKey(stExercise)){
            stResult = mapExercise.get(stExercise);
            stShow = stEmpty + stResult;
            etBenchPress.setText(stShow);
        }

        stExercise = getResources().getString(R.string.strDBMyResultSumoDeadliftEN);
        if (mapExercise.containsKey(stExercise)){
            stResult = mapExercise.get(stExercise);
            stShow = stEmpty + stResult;
            etSumoDeadlift.setText(stShow);
        }

        stExercise = getResources().getString(R.string.strDBMyResultThrusterEN);
        if (mapExercise.containsKey(stExercise)){
            stResult = mapExercise.get(stExercise);
            stShow = stEmpty + stResult;
            etThruster.setText(stShow);
        }

        stExercise = getResources().getString(R.string.strDBMyResultDumbellSnatchEN);
        if (mapExercise.containsKey(stExercise)){
            stResult = mapExercise.get(stExercise);
            stShow = stEmpty + stResult;
            etDumbellSnatch.setText(stShow);
        }

        stExercise = getResources().getString(R.string.strDBMyResultRowMEN);
        if (mapExercise.containsKey(stExercise)){
            stResult = mapExercise.get(stExercise);
            stShow = stEmpty + stResult;
            etRowM.setText(stShow);
        }

        stExercise = getResources().getString(R.string.strDBMyResultRowCalEN);
        if (mapExercise.containsKey(stExercise)){
            stResult = mapExercise.get(stExercise);
            stShow = stEmpty + stResult;
            etRowCal.setText(stShow);
        }
    }*/

    private void saveResultInDB(String exercise, String result) {
        if (result.isEmpty()) {
            result = "0";
        } else {
            if (result.length() > 1 && result.startsWith("0")) {
                result = result.substring(1);
            }
        }

        viewModel.saveResults(exercise, result);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {

        if(hasFocus){
            String stResult;
            switch (v.getId()){
                case R.id.etMyWeight:
                    stResult = etMyWeight.getText().toString();
                    if (stResult.equals("0")){
                        etMyWeight.setText("");
                    }
                    break;
                case R.id.editResult:
                    stResult = etDeadlift.getText().toString();
                    if (stResult.equals("0")){
                        etDeadlift.setText("");
                    }
                    break;
                case R.id.etSnatch:
                    stResult = etSnatch.getText().toString();
                    if (stResult.equals("0")){
                        etSnatch.setText("");
                    }
                    break;
                case R.id.etSquatClean:
                    stResult = etSquatClean.getText().toString();
                    if (stResult.equals("0")){
                        etSquatClean.setText("");
                    }
                    break;
                case R.id.etClean:
                    stResult = etClean.getText().toString();
                    if (stResult.equals("0")){
                        etClean.setText("");
                    }
                    break;
                case R.id.etSquatSnatches:
                    stResult = etSquatSnatches.getText().toString();
                    if (stResult.equals("0")){
                        etSquatSnatches.setText("");
                    }
                    break;
                case R.id.etCleanAndJerk:
                    stResult = etCleanAndJerk.getText().toString();
                    if (stResult.equals("0")){
                        etCleanAndJerk.setText("");
                    }
                    break;
                case R.id.etFrontSquat:
                    stResult = etFrontSquat.getText().toString();
                    if (stResult.equals("0")){
                        etFrontSquat.setText("");
                    }
                    break;
                case R.id.etBackSquat:
                    stResult = etBackSquat.getText().toString();
                    if (stResult.equals("0")){
                        etBackSquat.setText("");
                    }
                    break;
                case R.id.etShoulderPress:
                    stResult = etShoulderPress.getText().toString();
                    if (stResult.equals("0")){
                        etShoulderPress.setText("");
                    }
                    break;
                case R.id.etPushPress:
                    stResult = etPushPress.getText().toString();
                    if (stResult.equals("0")){
                        etPushPress.setText("");
                    }
                    break;
                case R.id.etBenchPress:
                    stResult = etBenchPress.getText().toString();
                    if (stResult.equals("0")){
                        etBenchPress.setText("");
                    }
                    break;
                case R.id.etSumoDeadlift:
                    stResult = etSumoDeadlift.getText().toString();
                    if (stResult.equals("0")){
                        etSumoDeadlift.setText("");
                    }
                    break;
                case R.id.etThruster:
                    stResult = etThruster.getText().toString();
                    if (stResult.equals("0")){
                        etThruster.setText("");
                    }
                    break;
                case R.id.etDumbellSnatch:
                    stResult = etDumbellSnatch.getText().toString();
                    if (stResult.equals("0")){
                        etDumbellSnatch.setText("");
                    }
                    break;
                case R.id.etRowM:
                    stResult = etRowM.getText().toString();
                    if (stResult.equals("0")){
                        etRowM.setText("");
                    }
                    break;
                case R.id.etRowCal:
                    stResult = etRowCal.getText().toString();
                    if (stResult.equals("0")){
                        etRowCal.setText("");
                    }
                    break;
            }
        }else {
            String stResult;
            switch (v.getId()){

                case R.id.etMyWeight:
                    stResult = etMyWeight.getText().toString();
                    if (stResult.equals("")){
                        etMyWeight.setText("0");
                    }
                    break;
                case R.id.editResult:
                    stResult = etDeadlift.getText().toString();
                    if (stResult.equals("")){
                        etDeadlift.setText("0");
                    }
                    break;

                case R.id.etSnatch:
                    stResult = etSnatch.getText().toString();
                    if (stResult.equals("")){
                        etSnatch.setText("0");
                    }
                    break;
                case R.id.etSquatClean:
                    stResult = etSquatClean.getText().toString();
                    if (stResult.equals("")){
                        etSquatClean.setText("0");
                    }
                    break;
                case R.id.etClean:
                    stResult = etClean.getText().toString();
                    if (stResult.equals("")){
                        etClean.setText("0");
                    }
                    break;
                case R.id.etSquatSnatches:
                    stResult = etSquatSnatches.getText().toString();
                    if (stResult.equals("")){
                        etSquatSnatches.setText("0");
                    }
                    break;
                case R.id.etCleanAndJerk:
                    stResult = etCleanAndJerk.getText().toString();
                    if (stResult.equals("")){
                        etCleanAndJerk.setText("0");
                    }
                    break;
                case R.id.etFrontSquat:
                    stResult = etFrontSquat.getText().toString();
                    if (stResult.equals("")){
                        etFrontSquat.setText("0");
                    }
                    break;
                case R.id.etBackSquat:
                    stResult = etBackSquat.getText().toString();
                    if (stResult.equals("")){
                        etBackSquat.setText("0");
                    }
                    break;
                case R.id.etShoulderPress:
                    stResult = etShoulderPress.getText().toString();
                    if (stResult.equals("")){
                        etShoulderPress.setText("0");
                    }
                    break;
                case R.id.etPushPress:
                    stResult = etPushPress.getText().toString();
                    if (stResult.equals("")){
                        etPushPress.setText("0");
                    }
                    break;
                case R.id.etBenchPress:
                    stResult = etBenchPress.getText().toString();
                    if (stResult.equals("")){
                        etBenchPress.setText("0");
                    }
                    break;

                case R.id.etSumoDeadlift:
                    stResult = etSumoDeadlift.getText().toString();
                    if (stResult.equals("")){
                        etSumoDeadlift.setText("0");
                    }
                    break;

                case R.id.etThruster:
                    stResult = etThruster.getText().toString();
                    if (stResult.equals("")){
                        etThruster.setText("0");
                    }
                    break;
                case R.id.etDumbellSnatch:
                    stResult = etDumbellSnatch.getText().toString();
                    if (stResult.equals("")){
                        etDumbellSnatch.setText("0");
                    }
                    break;
                case R.id.etRowM:
                    stResult = etRowM.getText().toString();
                    if (stResult.equals("")){
                        etRowM.setText("0");
                    }
                    break;
                case R.id.etRowCal:
                    stResult = etRowCal.getText().toString();
                    if (stResult.equals("")){
                        etRowCal.setText("0");
                    }
                    break;
            }
        }
    }
}
