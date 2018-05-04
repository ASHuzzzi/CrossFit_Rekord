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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ru.lizzzi.crossfit_rekord.R;
import ru.lizzzi.crossfit_rekord.adapters.RecyclerAdapter_Workout_details;
import ru.lizzzi.crossfit_rekord.loaders.Workout_details_Loaders;

/**
 * Created by Liza on 13.03.2018.
 */

public class Workout_details_Fragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Map>> {


    RecyclerAdapter_Workout_details adapter;
    ListView lvItemsInWod;
    TextView tvWarmUp;
    TextView tvSkill;
    TextView tvWOD;
    TextView tvLevelA;
    TextView tvLevelB;
    TextView tvLevelC;
    List<String> list;
    private int LOADER_ID = 1; //идентефикатор loader'а
    private int LOADER_ID2 = 2; //идентефикатор loader'а
    Bundle bundle;
    private LinearLayout ll1;
    private Network_check network_check; //переменная для проврки сети

    private Handler handler_fragment;
    private Thread thread_open_fragment;

    private Thread thread_click_onbutton;

    @SuppressLint("HandlerLeak")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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
        ll1 = v.findViewById(R.id.ll1);
        ll1.setVisibility(View.INVISIBLE);

        handler_fragment = new Handler() {
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

        //поток запускаемый при создании экрана (запуск происходит из onResume)
        Runnable runnable_open_fragment = new Runnable() {
            @Override
            public void run() {
                network_check = new Network_check(getContext());
                boolean result_check = network_check.checkInternet();
                if (result_check){
                    LoadSessionAsyncTaskLoader();
                    LoadExerciseAsyncTaskLoader();

                }else {
                    Message msg = handler_fragment.obtainMessage();
                    Bundle bundle = new Bundle();
                    bundle.putString("result", String.valueOf(false));
                    msg.setData(bundle);
                    handler_fragment.sendMessage(msg);
                }
            }
        };
        thread_open_fragment = new Thread(runnable_open_fragment);
        thread_open_fragment.setDaemon(true);
        thread_open_fragment.start();

        //поток запускаемыq кнопками выборающими дня недели
        /*Runnable runnable_click_onbutton = new Runnable() {
            @Override
            public void run() {
                network_check = new Network_check(getContext());
                boolean result_check = network_check.checkInternet();
                if (result_check){


                }else {
                    Message msg = handler_fragment.obtainMessage();
                    Bundle bundle = new Bundle();
                    bundle.putString("result", String.valueOf(false));
                    msg.setData(bundle);
                    handler_fragment.sendMessage(msg);
                }
            }
        };
        thread_click_onbutton = new Thread(runnable_click_onbutton);
        thread_click_onbutton.setDaemon(true);
        thread_click_onbutton.start();*/



        return v;
    }

    private void LoadSessionAsyncTaskLoader(){
        bundle.putString("Table", "Training_sessions");
        getLoaderManager().initLoader(LOADER_ID, bundle, this).forceLoad();
    }

    private void LoadExerciseAsyncTaskLoader(){
        bundle.putString("Table", "Exercise_assignment");
        getLoaderManager().initLoader(LOADER_ID2, bundle, this).forceLoad();
    }

    @Override
    public Loader<List<Map>> onCreateLoader(int id, Bundle args) {
        Loader<List<Map>> loader;
        loader = new Workout_details_Loaders(getContext(), args);
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<List<Map>> loader, List<Map> data) {
        int id = loader.getId();
        if (id == LOADER_ID){
            adapter = new RecyclerAdapter_Workout_details(getContext(), data, R.layout.item_lv_workout_details);
            lvItemsInWod.setAdapter(adapter);
        }else {
            for (int i = 0; i< data.size(); i++){
                list = new ArrayList<String>(data.get(i).values());
            }
            if (list != null && list.size() > 0){
                tvWarmUp.setText(String.valueOf(list.get(0)));
                tvSkill.setText(String.valueOf(list.get(6)));
                tvWOD.setText(String.valueOf(list.get(9)));
                tvLevelA.setText((String.valueOf(list.get(2))));
                tvLevelB.setText((String.valueOf(list.get(10))));
                tvLevelC.setText((String.valueOf(list.get(1))));
            }
        }
        if (adapter != null && list != null ){
            ll1.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onLoaderReset(Loader<List<Map>> loader) {

    }

    public void onStart() {
        super.onStart();


    }
}
