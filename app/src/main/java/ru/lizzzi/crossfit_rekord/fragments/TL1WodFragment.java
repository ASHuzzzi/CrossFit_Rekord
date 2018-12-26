package ru.lizzzi.crossfit_rekord.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import ru.lizzzi.crossfit_rekord.R;
import ru.lizzzi.crossfit_rekord.inspectionСlasses.NetworkCheck;
import ru.lizzzi.crossfit_rekord.loaders.WorkoutDetailsLoaders;


public class TL1WodFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Map>>{

    private TextView tvWarmUp;
    private TextView tvSkill;
    private TextView tvWOD;
    private TextView tvLevelSc;
    private TextView tvLevelRx;
    private TextView tvLevelRxPlus;
    private LinearLayout llMain;
    private LinearLayout llLayoutError;
    private LinearLayout llEmptyData;
    private ProgressBar pbProgressBar;

    private ru.lizzzi.crossfit_rekord.inspectionСlasses.NetworkCheck NetworkCheck; //переменная для проврки сети

    private Handler handlerOpenFragment;
    private Thread threadOpenFragment;

    private static final String APP_PREFERENCES = "audata";
    private static final String APP_PREFERENCES_SELECTEDDAY = "SelectedDay";

    private Runnable runnableOpenFragment;

    public TL1WodFragment() {
    }

    @SuppressLint("HandlerLeak")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tl1wod, container, false);
        tvWarmUp = v.findViewById(R.id.tvWarmUp);
        tvSkill = v.findViewById(R.id.tvSkill);
        tvWOD = v.findViewById(R.id.tvWOD);
        tvLevelSc = v.findViewById(R.id.tvLevelSc);
        tvLevelRx = v.findViewById(R.id.tvLevelRx);
        tvLevelRxPlus = v.findViewById(R.id.tvLevelRxplus);

        llMain = v.findViewById(R.id.llMain);
        Button buttonError = v.findViewById(R.id.button5);
        llLayoutError = v.findViewById(R.id.Layout_Error);
        llEmptyData = v.findViewById(R.id.llEmptyData);
        pbProgressBar = v.findViewById(R.id.progressBar3);


        //хэндлер для потока runnableOpenFragment
        handlerOpenFragment = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Bundle bundle = msg.getData();
                String result_check = bundle.getString("result");
                if (result_check != null && result_check.equals("true")){
                    loadExerciseAsyncTaskLoader();
                }else {
                    llLayoutError.setVisibility(View.VISIBLE);
                    pbProgressBar.setVisibility(View.INVISIBLE);
                }
            }
        };

        runnableOpenFragment = new Runnable() {
            @Override
            public void run() {
                NetworkCheck = new NetworkCheck(getContext());
                boolean resultCheck = NetworkCheck.checkInternet();
                Bundle bundle = new Bundle();
                if (resultCheck){
                    bundle.putString("result", String.valueOf(true));

                }else {
                    bundle.putString("result", String.valueOf(false));

                }

                Message msg = handlerOpenFragment.obtainMessage();
                msg.setData(bundle);
                handlerOpenFragment.sendMessage(msg);
            }
        };

        buttonError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pbProgressBar.setVisibility(View.VISIBLE);
                llLayoutError.setVisibility(View.INVISIBLE);
                threadOpenFragment = new Thread(runnableOpenFragment);
                threadOpenFragment.setDaemon(true);
                threadOpenFragment.start();
            }
        });

        return v;
    }

    private void loadExerciseAsyncTaskLoader(){

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            SharedPreferences mSettings = Objects.requireNonNull(getContext()).getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
            String selectedDay =  mSettings.getString(APP_PREFERENCES_SELECTEDDAY, "");
            Bundle bundle = new Bundle();
            bundle.putString("Selected_day", selectedDay);
            bundle.putString("Table", "exercises");
            int LOADER_ID2 = 2;
            getLoaderManager().initLoader(LOADER_ID2, bundle, this).forceLoad();
        }
    }

    @NonNull
    @Override
    public Loader<List<Map>> onCreateLoader(int id, Bundle args) {
        Loader<List<Map>> loader;
        loader = new WorkoutDetailsLoaders(getContext(), args);
        return loader;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Map>> loader, List<Map> data) {
        pbProgressBar.setVisibility(View.INVISIBLE);
        if (data != null && data.size() > 0){
            if(!String.valueOf(data.get(0).get("warmup")).equals("null")){
                tvWarmUp.setText(String.valueOf(data.get(0).get("warmup")));
            }else {
                tvWarmUp.setText("—");
            }
            if(!String.valueOf(data.get(0).get("skill")).equals("null")){
                tvSkill.setText(String.valueOf(data.get(0).get("skill")));
            }else {
                tvSkill.setText("—");
            }
            if(!String.valueOf(data.get(0).get("wod")).equals("null")){
                tvWOD.setText(String.valueOf(data.get(0).get("wod")));
            }else {
                tvWOD.setText("—");
            }
            if(!String.valueOf(data.get(0).get("Sc")).equals("null")){
                tvLevelSc.setText(String.valueOf(data.get(0).get("Sc")));
            }else {
                tvLevelSc.setText("—");
            }
            if(!String.valueOf(data.get(0).get("Rx")).equals("null")){
                tvLevelRx.setText(String.valueOf(data.get(0).get("Rx")));
            }else {
                tvLevelRx.setText("—");
            }
            if(!String.valueOf(data.get(0).get("Rxplus")).equals("null")){
                tvLevelRxPlus.setText(String.valueOf(data.get(0).get("Rxplus")));
            }else {
                tvLevelRxPlus.setText("—");
            }
            llMain.setVisibility(View.VISIBLE);
        }else {
            llEmptyData.setVisibility(View.VISIBLE);

        }

    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<Map>> loader) {

    }

    @Override
    public void onStart(){
        super.onStart();

        if (tvWarmUp.length() < 1){
            llLayoutError.setVisibility(View.INVISIBLE);
            llMain.setVisibility(View.INVISIBLE);
            llEmptyData.setVisibility(View.INVISIBLE);
            pbProgressBar.setVisibility(View.VISIBLE);

            threadOpenFragment = new Thread(runnableOpenFragment);
            threadOpenFragment.setDaemon(true);
            threadOpenFragment.start();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
