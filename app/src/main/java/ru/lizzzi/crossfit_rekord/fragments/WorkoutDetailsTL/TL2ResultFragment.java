package ru.lizzzi.crossfit_rekord.fragments.WorkoutDetailsTL;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
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
import java.util.Objects;

import ru.lizzzi.crossfit_rekord.activity.EnterResultActivity;
import ru.lizzzi.crossfit_rekord.R;
import ru.lizzzi.crossfit_rekord.adapters.RecyclerAdapterWorkoutDetails;
import ru.lizzzi.crossfit_rekord.inspectionСlasses.NetworkCheck;
import ru.lizzzi.crossfit_rekord.loaders.WorkoutDetailsLoaders;

public class TL2ResultFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Map>>{

    private static final int RESULT_OK = -1;
    private LinearLayout linLayMain;
    private RecyclerView recViewItemsInWod;

    private Handler handlerOpenFragment;
    private Thread threadUpdateFragment;

    private final String APP_PREFERENCES = "audata";
    private final String APP_PREFERENCES_SELECTEDDAY = "SelectedDay";
    private final String APP_PREFERENCES_OBJECTID = "ObjectId";
    private SharedPreferences sharedPreferences;

    private LinearLayout linLayError;
    private ProgressBar progressBar;
    private Button buttonSaveReult;

    static final private int CHOOSE_THIEF = 0;

    private String skill;
    private String wodLevel;
    private String wodResults;
    private boolean haveCurrentUserResult;

    private RecyclerAdapterWorkoutDetails adapter;
    private Runnable runnableOpenFragment;

    public TL2ResultFragment() {
    }

    @SuppressLint("HandlerLeak")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_tl2result, container, false);

        linLayMain = view.findViewById(R.id.linLayMain);
        recViewItemsInWod = view.findViewById(R.id.lvWodResult);
        buttonSaveReult = view.findViewById(R.id.btnOpenEnterResult);
        linLayError = view.findViewById(R.id.Layout_Error);
        progressBar = view.findViewById(R.id.progressBar4);
        Button buttonError = view.findViewById(R.id.button5);

        sharedPreferences = Objects.requireNonNull(getContext()).getSharedPreferences(
                APP_PREFERENCES,
                Context.MODE_PRIVATE);
        buttonSaveReult.setText(R.string.strEnterResult);

        handlerOpenFragment = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Bundle bundle = msg.getData();
                boolean checkDone = bundle.getBoolean("result");
                if (checkDone) {
                    getTrainingResults();
                } else {
                    linLayMain.setVisibility(View.INVISIBLE);
                    linLayError.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }
        };

        //поток запускаемый при создании экрана
        runnableOpenFragment = new Runnable() {
            @Override
            public void run() {
                NetworkCheck network = new NetworkCheck(getContext());
                Bundle bundle = new Bundle();
                boolean checkDone = network.checkConnection();
                if (checkDone) {
                    bundle.putString("status", "start");
                }
                bundle.putBoolean("result", checkDone);
                Message msg = handlerOpenFragment.obtainMessage();
                msg.setData(bundle);
                handlerOpenFragment.sendMessage(msg);
            }
        };

        buttonError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                linLayMain.setVisibility(View.INVISIBLE);
                linLayError.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);

                threadUpdateFragment = new Thread(runnableOpenFragment);
                threadUpdateFragment.setDaemon(true);
                threadUpdateFragment.start();
            }
        });

        buttonSaveReult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), EnterResultActivity.class);
                if (haveCurrentUserResult) {
                    intent.putExtra("flag", true);
                    intent.putExtra("skill", skill);
                    intent.putExtra("level", wodLevel);
                    intent.putExtra("results", wodResults);
                } else {
                    intent.putExtra("flag", false);
                }
                startActivityForResult(intent, CHOOSE_THIEF);
                Objects.requireNonNull(getActivity()).overridePendingTransition(
                        R.anim.pull_in_right,
                        R.anim.push_out_left);
            }
        });
        return view;
    }

    private void getTrainingResults(){
        String selectedDay =  sharedPreferences.getString(APP_PREFERENCES_SELECTEDDAY, "");
        Bundle bundle = new Bundle();
        bundle.putString("Selected_day", selectedDay);
        bundle.putString("Table", "results");
        int LOADER_ID = 1;
        getLoaderManager().restartLoader(LOADER_ID, bundle, this).forceLoad();
    }

    @NonNull
    @Override
    public Loader<List<Map>> onCreateLoader(int id, Bundle args) {
        return new WorkoutDetailsLoaders(getContext(), args);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Map>> loader, List<Map> data) {
        haveCurrentUserResult = false;
        progressBar.setVisibility(View.INVISIBLE);
        if (data != null && data.size() > 0) {
            for (int i = 0; i < data.size(); i++) {
                String currentUserId =
                        sharedPreferences.getString(APP_PREFERENCES_OBJECTID, "");
                if (data.get(i).containsValue(currentUserId)) {
                    skill = String.valueOf(data.get(i).get("skill"));
                    wodLevel = String.valueOf(data.get(i).get("wod_level"));
                    wodResults = String.valueOf(data.get(i).get("wod_result"));
                    haveCurrentUserResult = true;
                    buttonSaveReult.setText(R.string.strEditDeleteResult);
                }
            }
            adapter = new RecyclerAdapterWorkoutDetails(getContext(), data);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
            recViewItemsInWod.setLayoutManager(layoutManager);
            recViewItemsInWod.setAdapter(adapter);

        } else {
            if (adapter != null) {
                LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
                recViewItemsInWod.setLayoutManager(layoutManager);
                recViewItemsInWod.setAdapter(null);
            }
        }
        linLayError.setVisibility(View.INVISIBLE);
        linLayMain.setVisibility(View.VISIBLE);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<Map>> loader) {
    }

    @Override
    public  void onStart() {
        super.onStart();
        if (adapter == null) {
            linLayError.setVisibility(View.INVISIBLE);
            linLayMain.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);

            if (Thread.currentThread() != threadUpdateFragment) {
                threadUpdateFragment = new Thread(runnableOpenFragment);
                threadUpdateFragment.setDaemon(true);
                threadUpdateFragment.start();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CHOOSE_THIEF) {
            if (resultCode == RESULT_OK) {
                linLayMain.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                threadUpdateFragment.run();
            }
        }
    }
}