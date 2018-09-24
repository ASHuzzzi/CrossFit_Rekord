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
import ru.lizzzi.crossfit_rekord.loaders.WorkoutDetailsLoaders;

public class TL2ResultFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Map>>{


    private static final int RESULT_OK = -1;
    private LinearLayout ll1;
    private RecyclerView rvItemsInWod;

    private NetworkCheck NetworkCheck; //переменная для проврки сети

    private Handler handlerOpenFragment;
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

        handlerOpenFragment = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Bundle bundle = msg.getData();
                String result_check = bundle.getString("result");
                if (result_check != null){
                    if (result_check.equals("false")){
                        llLayoutError.setVisibility(View.VISIBLE);
                        pbProgressBar.setVisibility(View.INVISIBLE);
                    }else {
                        String status = bundle.getString("status");
                        if(status != null && status.equals("start")){
                            llLayoutError.setVisibility(View.INVISIBLE);
                            ll1.setVisibility(View.INVISIBLE);
                            pbProgressBar.setVisibility(View.VISIBLE);
                        }else {
                            llLayoutError.setVisibility(View.INVISIBLE);
                            ll1.setVisibility(View.VISIBLE);
                            pbProgressBar.setVisibility(View.INVISIBLE);
                        }
                    }
                }
            }
        };

        //поток запускаемый при создании экрана
        runnableOpenFragment = new Runnable() {
            @Override
            public void run() {
                Message msg = handlerOpenFragment.obtainMessage();
                Bundle bundle = new Bundle();
                bundle.putString("result", String.valueOf(true));
                bundle.putString("status", String.valueOf("start"));
                msg.setData(bundle);
                handlerOpenFragment.sendMessage(msg);

                NetworkCheck = new NetworkCheck(getContext());
                boolean resultCheck = NetworkCheck.checkInternet();
                if (resultCheck){
                    loadSessionAsyncTaskLoader();

                }else {
                    bundle.putString("result", String.valueOf(false));
                    msg.setData(bundle);
                    handlerOpenFragment.sendMessage(msg);
                }

            }
        };

        buttonError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
        getLoaderManager().initLoader(LOADER_ID, bundle, this).forceLoad();
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

        if (data.size() > 0){
            adapter = new RecyclerAdapterWorkoutDetails(getContext(), data);
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
            rvItemsInWod.setLayoutManager(mLayoutManager);
            rvItemsInWod.setAdapter(adapter);
        }else {
            rvItemsInWod.setAdapter(null);
        }

        Message msg = handlerOpenFragment.obtainMessage();
        Bundle bundle = new Bundle();
        bundle.putString("result", String.valueOf(true));
        bundle.putString("status", String.valueOf("finish"));
        msg.setData(bundle);
        handlerOpenFragment.sendMessage(msg);

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
        }

    }

    @Override
    public void onResume() {
        super.onResume();

        if (adapter == null){
            threadUpdateFragment = new Thread(runnableOpenFragment);
            threadUpdateFragment.setDaemon(true);
            threadUpdateFragment.start();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CHOOSE_THIEF) {
            if (resultCode == RESULT_OK && adapter != null) {
                threadUpdateFragment.run();

            }
        }
    }

}