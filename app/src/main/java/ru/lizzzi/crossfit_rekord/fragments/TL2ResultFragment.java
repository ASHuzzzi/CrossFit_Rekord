package ru.lizzzi.crossfit_rekord.fragments;


import android.annotation.SuppressLint;
import android.content.Context;
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
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.List;
import java.util.Map;

import ru.lizzzi.crossfit_rekord.R;
import ru.lizzzi.crossfit_rekord.adapters.RecyclerAdapterWorkoutDetails;
import ru.lizzzi.crossfit_rekord.loaders.WorkoutDetailsLoaders;

public class TL2ResultFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Map>>{


    RecyclerAdapterWorkoutDetails adapter;
    private LinearLayout ll1;
    ListView lvItemsInWod;

    private NetworkCheck NetworkCheck; //переменная для проврки сети

    private Handler handlerFragment;
    private Thread threadOpenFragment2;

    private static final String APP_PREFERENCES = "audata";
    private static final String APP_PREFERENCES_SELECTEDDAY = "SelectedDay";

    public TL2ResultFragment() {
        // Required empty public constructor
    }


    @SuppressLint("HandlerLeak")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tl2result, container, false);
        ll1 = v.findViewById(R.id.ll1);
        ll1.setVisibility(View.INVISIBLE);
        lvItemsInWod = v.findViewById(R.id.lvWodResult);

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

                }else {
                    Message msg = handlerFragment.obtainMessage();
                    Bundle bundle = new Bundle();
                    bundle.putString("result", String.valueOf(false));
                    msg.setData(bundle);
                    handlerFragment.sendMessage(msg);
                }
            }
        };
        threadOpenFragment2 = new Thread(runnableOpenFragment);
        threadOpenFragment2.setDaemon(true);

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
        if (data != null && data.size() > 0){
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
