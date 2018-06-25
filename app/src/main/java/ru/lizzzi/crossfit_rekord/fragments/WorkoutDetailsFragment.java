package ru.lizzzi.crossfit_rekord.fragments;

import android.annotation.SuppressLint;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import ru.lizzzi.crossfit_rekord.R;
import ru.lizzzi.crossfit_rekord.adapters.RecyclerAdapterWorkoutDetails;
import ru.lizzzi.crossfit_rekord.loaders.WorkoutDetailsLoaders;

/**
 * Created by Liza on 13.03.2018.
 */

public class WorkoutDetailsFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Map>> {


    RecyclerAdapterWorkoutDetails adapter;
    ListView lvItemsInWod;
    TextView tvWarmUp;
    TextView tvSkill;
    TextView tvWOD;
    TextView tvLevelA;
    TextView tvLevelB;
    TextView tvLevelC;
    private TextView tvSelectedDay;
    Bundle bundle;
    private LinearLayout ll1;
    private NetworkCheck NetworkCheck; //переменная для проврки сети

    private Handler handlerFragment;
    private Thread threadOpenFragment;

    @SuppressLint("HandlerLeak")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setRetainInstance(true);
        View v = inflater.inflate(R.layout.fragment_workout_details, container, false);
        getActivity().setTitle("Результаты тренировки");

        bundle = getArguments();
        final String ri = bundle.getString("tag");
        bundle.putString("Selected_day", ri);


        lvItemsInWod = v.findViewById(R.id.lvWodResult);
        tvWarmUp = v.findViewById(R.id.tvWarmUp);
        tvSkill = v.findViewById(R.id.tvSkill);
        tvWOD = v.findViewById(R.id.tvWOD);
        tvLevelA = v.findViewById(R.id.tvLevelA);
        tvLevelB = v.findViewById(R.id.tvLevelB);
        tvLevelC = v.findViewById(R.id.tvLevelC);
        tvSelectedDay = v.findViewById(R.id.tvSelectedDay);
        ll1 = v.findViewById(R.id.ll1);
        ll1.setVisibility(View.INVISIBLE);

        handlerFragment = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Bundle bundle = msg.getData();
                String result_check = bundle.getString("result");
                if (result_check != null){
                    if (result_check.equals("false")){
                        ll1.setVisibility(View.INVISIBLE);
                    }else {
                        ll1.setVisibility(View.VISIBLE);
                    }
                }
            }
        };

        //поток запускаемый при создании экрана
        Runnable runnableOpenFragment = new Runnable() {
            @Override
            public void run() {
                NetworkCheck = new NetworkCheck(getContext());
                boolean resultCheck = NetworkCheck.checkInternet();
                if (resultCheck){
                    loadSessionAsyncTaskLoader();
                    loadExerciseAsyncTaskLoader();
                    try {
                        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

                        Date ddd = sdf.parse(ri);

                        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf2 = new SimpleDateFormat("EEEE dd MMMM");
                        String sdsd = sdf2.format(ddd);
                        tvSelectedDay.setText(sdsd);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                }else {
                    Message msg = handlerFragment.obtainMessage();
                    Bundle bundle = new Bundle();
                    bundle.putString("result", String.valueOf(false));
                    msg.setData(bundle);
                    handlerFragment.sendMessage(msg);
                }
            }
        };
        threadOpenFragment = new Thread(runnableOpenFragment);
        threadOpenFragment.setDaemon(true);


        return v;
    }

    private void loadSessionAsyncTaskLoader(){
        bundle.putString("Table", "results");
        int LOADER_ID = 1;
        getLoaderManager().initLoader(LOADER_ID, bundle, this).forceLoad();
    }

    private void loadExerciseAsyncTaskLoader(){
        bundle.putString("Table", "exercises");
        int LOADER_ID2 = 2;
        getLoaderManager().initLoader(LOADER_ID2, bundle, this).forceLoad();
    }

    @Override
    public Loader<List<Map>> onCreateLoader(int id, Bundle args) {
        Loader<List<Map>> loader;
        loader = new WorkoutDetailsLoaders(getContext(), args);
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<List<Map>> loader, List<Map> data) {
        int id = loader.getId();
        if (id == 1){
            adapter = new RecyclerAdapterWorkoutDetails(getContext(), data, R.layout.item_lv_workout_details);
            lvItemsInWod.setAdapter(adapter);
        }else {
            if (data != null && data.size() > 0){
                tvWarmUp.setText(String.valueOf(data.get(0).get("warmup")));
                tvSkill.setText(String.valueOf(data.get(0).get("skill")));
                tvWOD.setText(String.valueOf(data.get(0).get("wod")));
                tvLevelA.setText(String.valueOf(data.get(0).get("A")));
                tvLevelB.setText(String.valueOf(data.get(0).get("B")));
                tvLevelC.setText(String.valueOf(data.get(0).get("C")));
            }
        }
        if (adapter != null && data != null ){
            ll1.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onLoaderReset(Loader<List<Map>> loader) {

    }

    @Override
    public void onResume() {
        super.onResume();

        //bundle = getArguments();
        //final String ri = bundle.getString("tag");
        //bundle.putString("Selected_day", ri);
        if (adapter == null){
            threadOpenFragment.start();
        }

    }

    public void onPause(){
        super.onPause();
    }
}
