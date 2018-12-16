package ru.lizzzi.crossfit_rekord.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.io.IOException;
import java.util.Map;

import ru.lizzzi.crossfit_rekord.R;
import ru.lizzzi.crossfit_rekord.data.MyResultDBHelper;
import ru.lizzzi.crossfit_rekord.interfaces.InterfaceChangeTitle;

public class MyResultsFragment extends Fragment {

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

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_my_results, container, false);

        etDeadlift = v.findViewById(R.id.etDeadlift);
        etSnatch = v.findViewById(R.id.etSnatch);
        etSquatClean = v.findViewById(R.id.etSquatClean);
        etClean = v.findViewById(R.id.etClean);
        etSquatSnatches = v.findViewById(R.id.etSquatSnatches);
        etCleanAndJerk = v.findViewById(R.id.etCleanAndJerk);
        etFrontSquat = v.findViewById(R.id.etFrontSquat);
        etBackSquat = v.findViewById(R.id.etBackSquat);
        etShoulderPress = v.findViewById(R.id.etShoulderPress);
        etPushPress = v.findViewById(R.id.etPushPress);
        etBenchPress = v.findViewById(R.id.etBenchPress);
        etSumoDeadlift = v.findViewById(R.id.etSumoDeadlift);
        etThruster = v.findViewById(R.id.etThruster);
        etDumbellSnatch = v.findViewById(R.id.etDumbellSnatch);
        etRowM = v.findViewById(R.id.etRowM);
        etRowCal = v.findViewById(R.id.etRowCal);

        Button btnSaveMyResult = v.findViewById(R.id.btnSaveMyResult);
        btnSaveMyResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String stResult;
                String stExercise;

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
                prepareEditText();
            }
        });

        return v;
    }

    @Override
    public  void onStart() {
        super.onStart();

        if (getActivity() instanceof InterfaceChangeTitle){
            InterfaceChangeTitle listernerChangeTitle = (InterfaceChangeTitle) getActivity();
            listernerChangeTitle.changeTitle(R.string.title_MyResults_Fragment, R.string.title_MyResults_Fragment);
        }

        prepareEditText();

    }

    private void prepareEditText(){
        Map<String, String> mapExercise;
        mapExercise = loadResiltFromDB();

        String stExercise;
        String stResult;
        String stEmpty = getResources().getString(R.string.empty);

        stExercise = getResources().getString(R.string.strDBMyResultDeadliftEN);
        if (mapExercise.containsKey(stExercise)){
            stResult = mapExercise.get(stExercise);
            String stShow = stEmpty + stResult;
            etDeadlift.setText(stShow);
        }

        stExercise = getResources().getString(R.string.strDBMyResultSnatchEN);
        if (mapExercise.containsKey(stExercise)){
            stResult = mapExercise.get(stExercise);
            String stShow = stEmpty + stResult;
            etSnatch.setText(stShow);
        }

        stExercise = getResources().getString(R.string.strDBMyResultSquatCleanEN);
        if (mapExercise.containsKey(stExercise)){
            stResult = mapExercise.get(stExercise);
            String stShow = stEmpty + stResult;
            etSquatClean.setText(stShow);
        }

        stExercise = getResources().getString(R.string.strDBMyResultCleanEN);
        if (mapExercise.containsKey(stExercise)){
            stResult = mapExercise.get(stExercise);
            String stShow = stEmpty + stResult;
            etClean.setText(stShow);
        }

        stExercise = getResources().getString(R.string.strDBMyResultSquatSnatchesEN);
        if (mapExercise.containsKey(stExercise)){
            stResult = mapExercise.get(stExercise);
            String stShow = stEmpty + stResult;
            etSquatSnatches.setText(stShow);
        }

        stExercise = getResources().getString(R.string.strDBMyResultCleanAndJerkEN);
        if (mapExercise.containsKey(stExercise)){
            stResult = mapExercise.get(stExercise);
            String stShow = stEmpty + stResult;
            etCleanAndJerk.setText(stShow);
        }

        stExercise = getResources().getString(R.string.strDBMyResultFrontSquatEN);
        if (mapExercise.containsKey(stExercise)){
            stResult = mapExercise.get(stExercise);
            String stShow = stEmpty + stResult;
            etFrontSquat.setText(stShow);
        }

        stExercise = getResources().getString(R.string.strDBMyResultBackSquatEN);
        if (mapExercise.containsKey(stExercise)){
            stResult = mapExercise.get(stExercise);
            String stShow = stEmpty + stResult;
            etBackSquat.setText(stShow);
        }

        stExercise = getResources().getString(R.string.strDBMyResultShoulderPressEN);
        if (mapExercise.containsKey(stExercise)){
            stResult = mapExercise.get(stExercise);
            String stShow = stEmpty + stResult;
            etShoulderPress.setText(stShow);
        }

        stExercise = getResources().getString(R.string.strDBMyResultPushPressEN);
        if (mapExercise.containsKey(stExercise)){
            stResult = mapExercise.get(stExercise);
            String stShow = stEmpty + stResult;
            etPushPress.setText(stShow);
        }

        stExercise = getResources().getString(R.string.strDBMyResultBenchPressEN);
        if (mapExercise.containsKey(stExercise)){
            stResult = mapExercise.get(stExercise);
            String stShow = stEmpty + stResult;
            etBenchPress.setText(stShow);
        }

        stExercise = getResources().getString(R.string.strDBMyResultSumoDeadliftEN);
        if (mapExercise.containsKey(stExercise)){
            stResult = mapExercise.get(stExercise);
            String stShow = stEmpty + stResult;
            etSumoDeadlift.setText(stShow);
        }

        stExercise = getResources().getString(R.string.strDBMyResultThrusterEN);
        if (mapExercise.containsKey(stExercise)){
            stResult = mapExercise.get(stExercise);
            String stShow = stEmpty + stResult;
            etThruster.setText(stShow);
        }

        stExercise = getResources().getString(R.string.strDBMyResultDumbellSnatchEN);
        if (mapExercise.containsKey(stExercise)){
            stResult = mapExercise.get(stExercise);
            String stShow = stEmpty + stResult;
            etDumbellSnatch.setText(stShow);
        }

        stExercise = getResources().getString(R.string.strDBMyResultRowMEN);
        if (mapExercise.containsKey(stExercise)){
            stResult = mapExercise.get(stExercise);
            String stShow = stEmpty + stResult;
            etRowM.setText(stShow);
        }

        stExercise = getResources().getString(R.string.strDBMyResultRowCalEN);
        if (mapExercise.containsKey(stExercise)){
            stResult = mapExercise.get(stExercise);
            String stShow = stEmpty + stResult;
            etRowCal.setText(stShow);
        }
    }

    private void saveResultInDB(String stExercise, String stResult){
        if (stResult.length() == 0 ){
            stResult = "0";
        }else{
            if (stResult.length()>1 && stResult.startsWith("0")){
                stResult = stResult.substring(1, stResult.length());
            }
        }



        MyResultDBHelper mDbHelper = new MyResultDBHelper(getContext());
        mDbHelper.saveResult(stExercise, stResult);
        mDbHelper.close();
    }


    private Map<String, String> loadResiltFromDB(){
        MyResultDBHelper mDbHelper = new MyResultDBHelper(getContext());

        try {
            mDbHelper.createDataBase();
        } catch (IOException ioe) {
            throw new Error("Unable to create database");
        }

        return  mDbHelper.loadResult();
    }
}
