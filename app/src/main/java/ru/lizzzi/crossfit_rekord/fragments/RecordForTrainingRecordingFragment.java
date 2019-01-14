package ru.lizzzi.crossfit_rekord.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
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
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import ru.lizzzi.crossfit_rekord.R;
import ru.lizzzi.crossfit_rekord.adapters.RecyclerAdapterRecord;
import ru.lizzzi.crossfit_rekord.inspectionСlasses.NetworkCheck;
import ru.lizzzi.crossfit_rekord.interfaces.InterfaceChangeTitle;
import ru.lizzzi.crossfit_rekord.loaders.RecordForTrainingRecordingLoadPeopleLoader;


public class RecordForTrainingRecordingFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Map>>{

    private static final String APP_PREFERENCES = "audata";
    private static final String APP_PREFERENCES_USERNAME = "Username";
    private static final String APP_PREFERENCES_OBJECTID = "ObjectId";
    private SharedPreferences mSettings;

    private RecyclerView rvRecord;
    private String stUserName;
    private String stUserId;

    private String stTimeSelect;

    private RecyclerAdapterRecord adapter;
    private Button btRegister;
    private Bundle bundle;

    private Loader<List<Map>> mLoader;

    NetworkCheck networkCheck;

    LinearLayout llSelectedWorkout;
    ProgressBar progressBar2;
    LinearLayout layoutError;
    LinearLayout layoutEmptyList;
    String stDateSelectFull;

    private Handler handlerOpenFragment;
    private Thread threadOpenFragment;
    private Runnable runnableOpenFragment;

    private Thread threadClickOnbutton;
    private Runnable runnableClickOnbutton;


    @SuppressLint("HandlerLeak")
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_record_for_training_recording, container, false);

        llSelectedWorkout = v.findViewById(R.id.llSelectedWorkout);
        TextView tvSelectedDay = v.findViewById(R.id.tvSelectedDay);
        TextView tvSelectedTime = v.findViewById(R.id.tvSelectedTime);
        TextView tvSelectedType = v.findViewById(R.id.tvSelectedType);

        rvRecord = v.findViewById(R.id.lvRecord);
        btRegister = v.findViewById(R.id.btRecord);

        progressBar2 = v.findViewById(R.id.progressBar2);
        layoutError = v.findViewById(R.id.Layout_Error);
        Button btNetworkError = v.findViewById(R.id.button7);
        layoutEmptyList = v.findViewById(R.id.Layout_emptylist2);

        mSettings = getContext().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        stUserName =  mSettings.getString(APP_PREFERENCES_USERNAME, "");


        bundle = getArguments();
        String stDateSelect = bundle.getString("dateshow");
        stDateSelectFull = bundle.getString("datefull");
        stTimeSelect = bundle.getString("time");


        tvSelectedDay.setText(stDateSelect);
        tvSelectedTime.setText(stTimeSelect);
        tvSelectedType.setText(bundle.getString("type"));
        switch (Objects.requireNonNull(bundle.getString("type"))){
            case "CrossFit":
                tvSelectedType.setTextColor(getResources().getColor(R.color.color_Table_CrossFit));
                break;
            case "On-Ramp":
                tvSelectedType.setTextColor(getResources().getColor(R.color.color_Table_On_Ramp));
                break;
            case "Open Gym":
                tvSelectedType.setTextColor(getResources().getColor(R.color.color_Table_Open_Gym));
                break;
            case "Stretching":
                tvSelectedType.setTextColor(getResources().getColor(R.color.color_Table_Stretching));
                break;
            case "CrossFit Kids":
                tvSelectedType.setTextColor(getResources().getColor(R.color.color_Table_Crossfit_Kids));
                break;
            case "Athleticism/TRX":
                tvSelectedType.setTextColor(getResources().getColor(R.color.color_Table_TRX));
                break;
            case "Gymnastics/Defence":
                tvSelectedType.setTextColor(R.drawable.table_item_gymnastics_and_defence);
                break;
            case "Endurance/Lady class":
                tvSelectedType.setTextColor(getResources().getColor(R.color.color_Table_Endurance));
                break;
            case "Weightlifting":
                tvSelectedType.setTextColor(getResources().getColor(R.color.color_Table_Weighlifting));
                break;

        }

        //хэндлер для обоих потоков. Какой именно поток вызвал хэндлер передается в key
        handlerOpenFragment = new Handler() {
            @SuppressLint("ShowToast")
            @Override
            public void handleMessage(Message msg) {
                Bundle bundle = msg.getData();
                String switchs = bundle.getString("switch"); //показывает какой поток вызвал
                String resultCheck = bundle.getString("networkCheck");
                if (switchs != null && switchs.equals("open")){
                    if (resultCheck != null && resultCheck.equals("false")) {
                        layoutError.setVisibility(View.VISIBLE);
                        progressBar2.setVisibility(View.INVISIBLE);
                    }else {
                        firstStartAsyncTaskLoader();
                    }
                }else {
                    if (resultCheck != null && resultCheck.equals("false")) {
                        if (layoutError.getVisibility() == View.INVISIBLE){
                            Toast.makeText(getContext(), "Нет подключения", Toast.LENGTH_SHORT).show();
                        }else {
                            rvRecord.setVisibility(View.INVISIBLE);
                            progressBar2.setVisibility(View.VISIBLE);
                        }

                    }else {
                        if (layoutError.getVisibility() == View.VISIBLE) {
                            layoutError.setVisibility(View.INVISIBLE);
                        }

                        rvRecord.setVisibility(View.INVISIBLE);

                        if (stUserId.equals("noId")){
                            restartAsyncTaskLoader(2);

                        }else {
                            restartAsyncTaskLoader(3);

                        }
                    }
                }
            }
        };

        //поток запускаемый при создании экрана (запуск происходит из onStart)
        runnableOpenFragment = new Runnable() {
            @Override
            public void run() {
                networkCheck = new NetworkCheck(getContext());
                boolean resultCheck = networkCheck.checkInternet();
                Bundle bundle = new Bundle();
                if (resultCheck){
                    bundle.putString("networkCheck", String.valueOf(true));

                }else {
                    bundle.putString("networkCheck", String.valueOf(false));

                }

                Message msg = handlerOpenFragment.obtainMessage();
                bundle.putString("switch", "open");
                msg.setData(bundle);
                handlerOpenFragment.sendMessage(msg);
            }
        };


        //поток запускаемый кнопкой удалить/записаться
        runnableClickOnbutton = new Runnable() {
            @Override
            public void run() {

                networkCheck = new NetworkCheck(getContext());
                boolean resultCheck = networkCheck.checkInternet();
                Bundle bundle = new Bundle();
                if (resultCheck){
                    bundle.putString("networkCheck", String.valueOf(true));

                }else {
                    bundle.putString("networkCheck", String.valueOf(false));
                }
                bundle.putString("switch", "onclick");
                Message msg = handlerOpenFragment.obtainMessage();
                msg.setData(bundle);
                handlerOpenFragment.sendMessage(msg);
            }
        };
        threadClickOnbutton = new Thread(runnableClickOnbutton);
        threadClickOnbutton.setDaemon(true);

        btRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                rvRecord.setVisibility(View.INVISIBLE);
                btRegister.setVisibility(View.INVISIBLE);
                progressBar2.setVisibility(View.VISIBLE);
                layoutEmptyList.setVisibility(View.INVISIBLE);

                threadClickOnbutton = new Thread(runnableClickOnbutton);
                threadClickOnbutton.setDaemon(true);
                threadClickOnbutton.start();
            }
        });

        btNetworkError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar2.setVisibility(View.VISIBLE);
                layoutError.setVisibility(View.INVISIBLE);

                threadOpenFragment = new Thread(runnableOpenFragment);
                threadOpenFragment.setDaemon(true);
                threadOpenFragment.start();
            }
        });

        return v;
    }

    private void firstStartAsyncTaskLoader(){
        bundle = new Bundle();
        bundle.putString(String.valueOf(RecordForTrainingRecordingLoadPeopleLoader.ARG_DATE), stDateSelectFull);
        bundle.putString(String.valueOf(RecordForTrainingRecordingLoadPeopleLoader.ARG_TIME), stTimeSelect);
        int LOADER_SHOW_LIST = 1;
        mLoader = getLoaderManager().initLoader(LOADER_SHOW_LIST, bundle, this);
        mLoader.forceLoad();
    }

    private void restartAsyncTaskLoader(int loader_id){
        int LOADER_WRITE_ITEM = 2;
        int LOADER_DELETE_ITEM = 3;
        switch (loader_id){
            case 2:
                stUserId =  mSettings.getString(APP_PREFERENCES_OBJECTID, "");
                bundle.putString(String.valueOf(RecordForTrainingRecordingLoadPeopleLoader.ARG_USERNAME), stUserName);
                bundle.putString(String.valueOf(RecordForTrainingRecordingLoadPeopleLoader.ARG_USERID), stUserId);
                mLoader = getLoaderManager().restartLoader(LOADER_WRITE_ITEM,bundle, this);
                mLoader.forceLoad();
                break;

            case 3:
                bundle.putString(String.valueOf(RecordForTrainingRecordingLoadPeopleLoader.ARG_USERID), stUserId);
                mLoader = getLoaderManager().restartLoader(LOADER_DELETE_ITEM,bundle, this);
                mLoader.forceLoad();
                break;
        }
    }

    @NonNull
    @Override
    public Loader<List<Map>> onCreateLoader(int id, Bundle args) {
        Loader<List<Map>> loader;
        loader = new RecordForTrainingRecordingLoadPeopleLoader(getContext(), args, id);
        return loader;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Map>> loader, List<Map> data) {

        if (data != null) {
            if( data.size() > 0){
                if (checkUser(data)) {
                    btRegister.setText(R.string.delete_entry);

                }else {
                    btRegister.setText(R.string.whrite_entry);
                    stUserId = "noId";
                }

                adapter = new RecyclerAdapterRecord(getContext(), data);
                LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
                rvRecord.setLayoutManager(mLayoutManager);
                rvRecord.setAdapter(adapter);
                rvRecord.setVisibility(View.VISIBLE);
                btRegister.setVisibility(View.VISIBLE);
                progressBar2.setVisibility(View.INVISIBLE);

            }else {
                btRegister.setText(R.string.whrite_entry);
                stUserId = "noId";
                rvRecord.setVisibility(View.INVISIBLE);
                btRegister.setVisibility(View.VISIBLE);
                progressBar2.setVisibility(View.INVISIBLE);
                layoutEmptyList.setVisibility(View.VISIBLE);
            }

        }else {
            layoutError.setVisibility(View.VISIBLE);
            progressBar2.setVisibility(View.INVISIBLE);

        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader loader) {

    }

    //проверяем по objectId наличие пользователя среди записавшихся
    private boolean checkUser(List<Map> data){
        for (int i = 0; i< data.size(); i++){
            if (data.get(i).containsValue(String.valueOf(stUserName))){
                stUserId = String.valueOf(data.get(i).get("objectId"));
                return true;
            }
        }
        return false;
    }

    //в onStart делаем проверку на наличие данных в адаптаре. При первом запуске адаптер пустой и
    //будет запущен поток.
    //при возврате через кнопку back адаптер будет не пустым поток не запуститься. что сохранит
    //состояние адаптера в положении перед открытием нового фрагмента
    @Override
    public  void onStart() {
        super.onStart();
        if (getActivity() instanceof InterfaceChangeTitle){
            InterfaceChangeTitle listernerChangeTitle = (InterfaceChangeTitle) getActivity();
            listernerChangeTitle.changeTitle(R.string.title_RecordForTrainingRecording_Fragment, R.string.title_RecordForTraining_Fragment);
        }

        if (adapter == null){
            rvRecord.setVisibility(View.INVISIBLE);
            btRegister.setVisibility(View.INVISIBLE);
            progressBar2.setVisibility(View.VISIBLE);
            layoutError.setVisibility(View.INVISIBLE);
            layoutEmptyList.setVisibility(View.INVISIBLE);
        }

        if (adapter == null && layoutEmptyList.getVisibility() == View.INVISIBLE){
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
