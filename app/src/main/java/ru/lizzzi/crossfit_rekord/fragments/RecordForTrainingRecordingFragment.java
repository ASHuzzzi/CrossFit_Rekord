package ru.lizzzi.crossfit_rekord.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import ru.lizzzi.crossfit_rekord.loaders.RecordForTrainingRecording_LoadPeople_Loader;


public class RecordForTrainingRecordingFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Map>>{

    public static final String APP_PREFERENCES = "audata";
    public static final String APP_PREFERENCES_USERNAME = "Username";
    SharedPreferences mSettings;

    private RecyclerView lvRecord;
    TextView tvSelectedDay;
    TextView tvSelectedTime;
    TextView tvSelectedType;
    String stUserName;
    String stUserId;

    String stDateSelect;
    String stTimeSelect;

    public int LOADER_SHOW_LIST = 1;
    public int LOADER_WRITE_ITEM = 2;
    public int LOADER_DELETE_ITEM = 3;
    RecyclerAdapterRecord adapter;
    Button btRegister;
    Button btNetworkError;
    Bundle bundle;

    private Loader<List<Map>> mLoader;

    NetworkCheck networkCheck;

    LinearLayout llSelectedWorkout;
    ProgressBar progressBar2;
    LinearLayout layoutError;
    LinearLayout layoutEmptyList;
    String stDateSelectFull;

    private Handler handlerOpenFragment;
    private Thread threadOpenFragment;

    private Thread threadClickOnbutton;


    @SuppressLint("HandlerLeak")
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_record_for_training_recording, container, false);

        llSelectedWorkout = v.findViewById(R.id.llSelectedWorkout);
        tvSelectedDay = v.findViewById(R.id.tvSelectedDay);
        tvSelectedTime = v.findViewById(R.id.tvSelectedTime);
        tvSelectedType = v.findViewById(R.id.tvSelectedType);

        lvRecord = v.findViewById(R.id.lvRecord);
        btRegister = v.findViewById(R.id.btRecord);

        progressBar2 = v.findViewById(R.id.progressBar2);
        layoutError = v.findViewById(R.id.Layout_Error);
        btNetworkError = v.findViewById(R.id.button7);
        layoutEmptyList = v.findViewById(R.id.Layout_emptylist2);

        lvRecord.setVisibility(View.INVISIBLE);
        btRegister.setVisibility(View.INVISIBLE);
        progressBar2.setVisibility(View.VISIBLE);
        layoutError.setVisibility(View.INVISIBLE);
        layoutEmptyList.setVisibility(View.INVISIBLE);

        mSettings = getContext().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        stUserName =  mSettings.getString(APP_PREFERENCES_USERNAME, "");

        bundle = getArguments();
        stDateSelect = bundle.getString("dateshow");
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
            case "Weightlifting/Athleticism":
                tvSelectedType.setTextColor(getResources().getColor(R.color.color_Table_Weighlifting));
                break;
            case "Gymnastics/Defence":
                tvSelectedType.setTextColor(R.drawable.table_item_gymnastics_and_defence);
                break;
            case "Rowing/Lady class":
                tvSelectedType.setTextColor(R.drawable.table_item_rowing_and_ladyclass);
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
                String resultCheck;
                if (switchs != null){
                    if (switchs.equals("open")){ //поток при первом запуске экрана
                        resultCheck = bundle.getString("open");
                        if (resultCheck != null) {
                            if (resultCheck.equals("false")) {
                                layoutError.setVisibility(View.VISIBLE);
                                progressBar2.setVisibility(View.INVISIBLE);
                            }
                        }
                    }else{
                        resultCheck = bundle.getString("onclick"); //поток от нажатия кнопок
                        if (resultCheck != null) {
                            if (resultCheck.equals("false")) {
                                if (layoutError.getVisibility() == View.INVISIBLE){
                                    Toast.makeText(getContext(), "Нет подключения", Toast.LENGTH_SHORT).show();
                                }
                            }else {
                                lvRecord.setVisibility(View.INVISIBLE);
                                progressBar2.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                }
            }
        };

        //поток запускаемый при создании экрана (запуск происходит из onResume)
        Runnable runnableOpenFragment = new Runnable() {
            @Override
            public void run() {
                networkCheck = new NetworkCheck(getContext());
                boolean resultCheck = networkCheck.checkInternet();
                if (resultCheck){
                    firstStartAsyncTaskLoader();

                }else {
                    Message msg = handlerOpenFragment.obtainMessage();
                    Bundle bundle = new Bundle();
                    bundle.putString("open", String.valueOf(false));
                    bundle.putString("switch", "open");
                    msg.setData(bundle);
                    handlerOpenFragment.sendMessage(msg);
                }
            }
        };
        threadOpenFragment = new Thread(runnableOpenFragment);
        threadOpenFragment.setDaemon(true);

        //поток запускаемый кнопкой удалить/записаться
        Runnable runnableClickOnbutton = new Runnable() {
            @Override
            public void run() {
                Message msg = handlerOpenFragment.obtainMessage();
                Bundle bundle = new Bundle();
                networkCheck = new NetworkCheck(getContext());
                boolean resultCheck = networkCheck.checkInternet();
                if (resultCheck){
                    bundle.putString("onclick", String.valueOf(true));
                    if (layoutError.getVisibility() == View.VISIBLE) {
                        layoutError.setVisibility(View.INVISIBLE);
                    }
                    if (stUserId.equals("noId")){
                        lvRecord.setVisibility(View.INVISIBLE);
                        restartAsyncTaskLoader(2);

                    }else {
                        lvRecord.setVisibility(View.INVISIBLE);
                        restartAsyncTaskLoader(3);

                    }

                }else {
                    bundle.putString("onclick", String.valueOf(false));
                }
                bundle.putString("switch", "onclick");
                msg.setData(bundle);
                handlerOpenFragment.sendMessage(msg);
            }
        };
        threadClickOnbutton = new Thread(runnableClickOnbutton);
        threadClickOnbutton.setDaemon(true);

        btRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                lvRecord.setVisibility(View.INVISIBLE);
                btRegister.setVisibility(View.INVISIBLE);
                progressBar2.setVisibility(View.VISIBLE);
                layoutEmptyList.setVisibility(View.INVISIBLE);

                threadClickOnbutton.run();
            }
        });

        btNetworkError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar2.setVisibility(View.VISIBLE);
                layoutError.setVisibility(View.INVISIBLE);
                threadClickOnbutton.run();
            }
        });

        return v;
    }

    private void firstStartAsyncTaskLoader(){
        bundle = new Bundle();
        bundle.putString(String.valueOf(RecordForTrainingRecording_LoadPeople_Loader.ARG_DATE), stDateSelectFull);
        bundle.putString(String.valueOf(RecordForTrainingRecording_LoadPeople_Loader.ARG_TIME), stTimeSelect);
        mLoader = getLoaderManager().initLoader(LOADER_SHOW_LIST, bundle, this);
        mLoader.forceLoad();
    }

    private void restartAsyncTaskLoader(int loader_id){
        switch (loader_id){
            case 2:
                bundle.putString(String.valueOf(RecordForTrainingRecording_LoadPeople_Loader.ARG_USERNAME), stUserName);
                mLoader = getLoaderManager().restartLoader(LOADER_WRITE_ITEM,bundle, this);
                mLoader.forceLoad();
                break;
            case 3:
                bundle.putString(String.valueOf(RecordForTrainingRecording_LoadPeople_Loader.ARG_USERID), stUserId);
                mLoader = getLoaderManager().restartLoader(LOADER_DELETE_ITEM,bundle, this);
                mLoader.forceLoad();
                break;
        }

    }

    @Override
    public Loader<List<Map>> onCreateLoader(int id, Bundle args) {
        Loader<List<Map>> loader;
        loader = new RecordForTrainingRecording_LoadPeople_Loader(getContext(), args, id);
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<List<Map>> loader, List<Map> data) {

        if (data != null && data.size() > 0) {
            if (checkUser(data)) {
                btRegister.setText(R.string.delete_entry);

            }else {
                btRegister.setText(R.string.whrite_entry);
                stUserId = "noId";
            }


            adapter = new RecyclerAdapterRecord(getContext(), data);
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
            lvRecord.setLayoutManager(mLayoutManager);
            lvRecord.setAdapter(adapter);
            lvRecord.setVisibility(View.VISIBLE);
            btRegister.setVisibility(View.VISIBLE);
            progressBar2.setVisibility(View.INVISIBLE);
        }else {
            btRegister.setText(R.string.whrite_entry);
            stUserId = "noId";
            lvRecord.setVisibility(View.INVISIBLE);
            btRegister.setVisibility(View.VISIBLE);
            progressBar2.setVisibility(View.INVISIBLE);
            layoutEmptyList.setVisibility(View.VISIBLE);
        }


    }

    @Override
    public void onLoaderReset(Loader loader) {

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

    //в onResume делаем проверку на наличие данных в адаптаре. При первом запуске адаптер пустой и
    //будет запущен поток.
    //при возврате через кнопку back адаптер будет не пустым поток не запуститься. что сохранит
    //состояние адаптера в положении перед открытием нового фрагмента
    @Override
    public void onResume() {
        super.onResume();
        if (adapter == null && layoutEmptyList.getVisibility() == View.INVISIBLE){
            threadOpenFragment.start();
        }
    }
}
