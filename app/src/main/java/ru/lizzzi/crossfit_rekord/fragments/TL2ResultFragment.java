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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.List;
import java.util.Map;

import ru.lizzzi.crossfit_rekord.activity.EnterResultActivity;
import ru.lizzzi.crossfit_rekord.R;
import ru.lizzzi.crossfit_rekord.adapters.RecyclerAdapterWorkoutDetails;
import ru.lizzzi.crossfit_rekord.loaders.WorkoutDetailsLoaders;

public class TL2ResultFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Map>>{


    RecyclerAdapterWorkoutDetails adapter;
    private LinearLayout ll1;
    ListView lvItemsInWod;

    private NetworkCheck NetworkCheck; //переменная для проврки сети

    private Handler handlerOpenFragment;
    private Thread threadOpenFragment2;

    private static final String APP_PREFERENCES = "audata";
    private static final String APP_PREFERENCES_SELECTEDDAY = "SelectedDay";
    private static final String APP_PREFERENCES_OBJECTID = "ObjectId";
    SharedPreferences mSettings;

    private LinearLayout llMain;
    private LinearLayout llLayoutError;
    private ProgressBar pbProgressBar;
    private Button buttonEnterReult;

    public TL2ResultFragment() {
        // Required empty public constructor
    }


    @SuppressLint("HandlerLeak")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_tl2result, container, false);
        ll1 = v.findViewById(R.id.ll1);
        ll1.setVisibility(View.INVISIBLE);
        lvItemsInWod = v.findViewById(R.id.lvWodResult);

        buttonEnterReult = v.findViewById(R.id.btnOpenEnterResult);
        //buttonEnterReult.setVisibility(View.INVISIBLE);

        Button buttonError = v.findViewById(R.id.button5);
        llLayoutError = v.findViewById(R.id.Layout_Error);
        pbProgressBar = v.findViewById(R.id.progressBar4);

        llLayoutError.setVisibility(View.INVISIBLE);
        ll1.setVisibility(View.INVISIBLE);
        pbProgressBar.setVisibility(View.VISIBLE);

        handlerOpenFragment = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Bundle bundle = msg.getData();
                String result_check = bundle.getString("result");
                if (result_check != null){
                    if (result_check.equals("false")){
                        llLayoutError.setVisibility(View.VISIBLE);
                        pbProgressBar.setVisibility(View.INVISIBLE);
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

                }else {
                    Message msg = handlerOpenFragment.obtainMessage();
                    Bundle bundle = new Bundle();
                    bundle.putString("result", String.valueOf(false));
                    msg.setData(bundle);
                    handlerOpenFragment.sendMessage(msg);
                }
            }
        };
        threadOpenFragment2 = new Thread(runnableOpenFragment);
        threadOpenFragment2.setDaemon(true);

        buttonError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pbProgressBar.setVisibility(View.VISIBLE);
                llLayoutError.setVisibility(View.INVISIBLE);
                threadOpenFragment2.run();
            }
        });

        buttonEnterReult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(v.getContext(), EnterResultActivity.class);
                v.getContext().startActivity(intent);

            }
        });

        return v;
    }

    private void loadSessionAsyncTaskLoader(){

        SharedPreferences mSettings = getContext().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
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

        mSettings = getContext().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        String stObjectId =  mSettings.getString(APP_PREFERENCES_OBJECTID, "");
        boolean flag = false;
         for (int i = 0; i < data.size(); i++){
             if(data.get(i).containsValue(stObjectId)){
                 flag = true;
             }
         }

         if (flag){
             buttonEnterReult.setText(R.string.strEditDeleteResult);
         }else {
             buttonEnterReult.setText(R.string.strEnterResult);
         }


        pbProgressBar.setVisibility(View.INVISIBLE);
        if (data.size() > 0){
            adapter = new RecyclerAdapterWorkoutDetails(getContext(), data, R.layout.item_lv_workout_details);
            lvItemsInWod.setAdapter(adapter);
            ll1.setVisibility(View.VISIBLE);
        }else {
            ll1.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Map>> loader) {

    }

    @Override
    public void onResume() {
        super.onResume();

        if (adapter == null){
            threadOpenFragment2.start();
        }

    }

}
