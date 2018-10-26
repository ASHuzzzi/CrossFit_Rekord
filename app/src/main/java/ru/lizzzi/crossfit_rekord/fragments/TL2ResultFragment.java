package ru.lizzzi.crossfit_rekord.fragments;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
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

import ru.lizzzi.crossfit_rekord.activity.EnterResultActivity;
import ru.lizzzi.crossfit_rekord.R;
import ru.lizzzi.crossfit_rekord.adapters.RecyclerAdapterWorkoutDetails;
import ru.lizzzi.crossfit_rekord.inspectionСlasses.NetworkCheck;
import ru.lizzzi.crossfit_rekord.loaders.WorkoutDetailsLoaders;

public class TL2ResultFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Map>>{


    private static final int RESULT_OK = -1;
    private LinearLayout ll1;
    private RecyclerView rvItemsInWod;

    private ru.lizzzi.crossfit_rekord.inspectionСlasses.NetworkCheck NetworkCheck; //переменная для проврки сети

    private Handler handlerOpenFragmentTL2;
    private Thread threadUpdateFragment;

    private static final String APP_PREFERENCES = "audata";
    private static final String APP_PREFERENCES_SELECTEDDAY = "SelectedDay";
    private static final String APP_PREFERENCES_OBJECTID = "ObjectId";
    private SharedPreferences mSettings;

    private LinearLayout llLayoutError;
    private ProgressBar pbProgressBar;
    private Button buttonEnterReult;

    static final private int CHOOSE_THIEF = 0;

    private String stSkill;
    private String stWoDLevel;
    private String stWoDResults;
    private boolean flag;

    private RecyclerAdapterWorkoutDetails adapter;
    private Runnable runnableOpenFragment;


    public TL2ResultFragment() {
        // Required empty public constructor
    }


    @SuppressLint("HandlerLeak")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_tl2result, container, false);

        ll1 = v.findViewById(R.id.ll1);
        rvItemsInWod = v.findViewById(R.id.lvWodResult);
        buttonEnterReult = v.findViewById(R.id.btnOpenEnterResult);
        llLayoutError = v.findViewById(R.id.Layout_Error);
        pbProgressBar = v.findViewById(R.id.progressBar4);
        Button buttonError = v.findViewById(R.id.button5);

        mSettings = getContext().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        handlerOpenFragmentTL2 = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Bundle bundle = msg.getData();
                String result_check = bundle.getString("result");
                if (result_check != null && result_check.equals("true")){
                    loadSessionAsyncTaskLoader();

                }else {
                    ll1.setVisibility(View.INVISIBLE);
                    llLayoutError.setVisibility(View.VISIBLE);
                    pbProgressBar.setVisibility(View.INVISIBLE);
                }
            }
        };

        //поток запускаемый при создании экрана
        runnableOpenFragment = new Runnable() {
            @Override
            public void run() {

                NetworkCheck = new NetworkCheck(getContext());
                boolean resultCheck = NetworkCheck.checkInternet();
                Bundle bundle = new Bundle();

                if (resultCheck){
                    bundle.putString("status", String.valueOf("start"));
                    bundle.putString("result", String.valueOf(true));

                }else {
                    bundle.putString("result", String.valueOf(false));

                }
                Message msg = handlerOpenFragmentTL2.obtainMessage();

                msg.setData(bundle);
                handlerOpenFragmentTL2.sendMessage(msg);

            }
        };

        buttonError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ll1.setVisibility(View.INVISIBLE);
                llLayoutError.setVisibility(View.INVISIBLE);
                pbProgressBar.setVisibility(View.VISIBLE);

                threadUpdateFragment = new Thread(runnableOpenFragment);
                threadUpdateFragment.setDaemon(true);
                threadUpdateFragment.start();
            }
        });

        buttonEnterReult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(v.getContext(), EnterResultActivity.class);
                if (flag){
                    intent.putExtra("flag", true);
                    intent.putExtra("skill", stSkill);
                    intent.putExtra("level", stWoDLevel);
                    intent.putExtra("results", stWoDResults);
                }else {
                    intent.putExtra("flag", false);
                }
                startActivityForResult(intent, CHOOSE_THIEF);

            }
        });

        return v;
    }

    private void loadSessionAsyncTaskLoader(){

        String selectedDay =  mSettings.getString(APP_PREFERENCES_SELECTEDDAY, "");

        Bundle bundle = new Bundle();
        bundle.putString("Selected_day", selectedDay);
        bundle.putString("Table", "results");
        int LOADER_ID = 1;
        getLoaderManager().restartLoader(LOADER_ID, bundle, this).forceLoad();
    }

    @Override
    public Loader<List<Map>> onCreateLoader(int id, Bundle args) {
        Loader<List<Map>> loader;
        loader = new WorkoutDetailsLoaders(getContext(), args);
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<List<Map>> loader, List<Map> data) {
        flag = false;

        pbProgressBar.setVisibility(View.INVISIBLE);
        if (data != null && data.size() > 0){
            for (int i = 0; i < data.size(); i++){
                String stObjectId =  mSettings.getString(APP_PREFERENCES_OBJECTID, "");

                if(data.get(i).containsValue(stObjectId)){
                    stSkill = String.valueOf(data.get(i).get("skill"));
                    stWoDLevel = String.valueOf(data.get(i).get("wod_level"));
                    stWoDResults = String.valueOf(data.get(i).get("wod_result"));
                    flag = true;
                }
            }

            if (flag){
                buttonEnterReult.setText(R.string.strEditDeleteResult);
            }else {
                buttonEnterReult.setText(R.string.strEnterResult);
            }

            adapter = new RecyclerAdapterWorkoutDetails(getContext(), data);
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
            rvItemsInWod.setLayoutManager(mLayoutManager);
            rvItemsInWod.setAdapter(adapter);

        }else {
            if (adapter != null){
                LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
                rvItemsInWod.setLayoutManager(mLayoutManager);
                rvItemsInWod.setAdapter(null);
            }
        }
        llLayoutError.setVisibility(View.INVISIBLE);
        ll1.setVisibility(View.VISIBLE);
    }

    @Override
    public void onLoaderReset(Loader<List<Map>> loader) {
    }

    @Override
    public  void onStart() {
        super.onStart();
        if (adapter == null){
            llLayoutError.setVisibility(View.INVISIBLE);
            ll1.setVisibility(View.INVISIBLE);
            pbProgressBar.setVisibility(View.VISIBLE);

            if (Thread.currentThread() != threadUpdateFragment){
                threadUpdateFragment = new Thread(runnableOpenFragment);
                threadUpdateFragment.setDaemon(true);
                threadUpdateFragment.start();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CHOOSE_THIEF) {
            if (resultCode == RESULT_OK) {
                ll1.setVisibility(View.INVISIBLE);
                pbProgressBar.setVisibility(View.VISIBLE);
                threadUpdateFragment.run();

            }
        }
    }

}