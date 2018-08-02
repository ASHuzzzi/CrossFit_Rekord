package ru.lizzzi.crossfit_rekord.activity;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.content.Context;

import java.util.List;
import java.util.Map;

import ru.lizzzi.crossfit_rekord.R;
import ru.lizzzi.crossfit_rekord.fragments.NetworkCheck;
import ru.lizzzi.crossfit_rekord.loaders.RecordForTrainingRecordingLoadPeopleLoader;
import ru.lizzzi.crossfit_rekord.loaders.SaveLoadResultLoader;

public class EnterResultActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Map>> {

    NetworkCheck networkCheck;

    private Handler handlerOpenFragment;
    private Thread threadOpenFragment;

    private Thread threadClickOnbutton;

    private EditText etResultSkill;
    private EditText etResultLevel;
    private EditText etResultWoD;

    Context context;

    public int LOADER_SHOW_LIST = 1;
    public int LOADER_WRITE_ITEM = 2;
    public int LOADER_DELETE_ITEM = 3;

    Bundle bundle;
    private Loader<List<Map>> mLoader;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_result);
        Toolbar toolbar = findViewById(R.id.toolbarER);
        setSupportActionBar(toolbar);

        etResultSkill = findViewById(R.id.etResultSkill);
        etResultLevel = findViewById(R.id.etResultLevel);
        etResultWoD = findViewById(R.id.etResultWoD);

        Button btn = findViewById(R.id.button9);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

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
                                /*layoutError.setVisibility(View.VISIBLE);
                                progressBar2.setVisibility(View.INVISIBLE);*/
                            }
                        }
                    }else{
                        resultCheck = bundle.getString("onclick"); //поток от нажатия кнопок
                        if (resultCheck != null) {
                            if (resultCheck.equals("false")) {/*
                                if (layoutError.getVisibility() == View.INVISIBLE){
                                    Toast.makeText(EnterResultActivity.this, "Нет подключения", Toast.LENGTH_SHORT).show();
                                }
                            }else {
                                lvRecord.setVisibility(View.INVISIBLE);
                                progressBar2.setVisibility(View.VISIBLE);*/
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
                networkCheck = new NetworkCheck(EnterResultActivity.this);
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
        /*Runnable runnableClickOnbutton = new Runnable() {
            @Override
            public void run() {
                Message msg = handlerOpenFragment.obtainMessage();
                Bundle bundle = new Bundle();
                networkCheck = new NetworkCheck(EnterResultActivity.this);
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
        threadClickOnbutton.setDaemon(true);*/


    }

    @Override
    protected void onResume() {
        super.onResume();
        getSupportActionBar().setTitle("Мои результаты тренировки");
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        threadOpenFragment.start();
    }


    private void firstStartAsyncTaskLoader(){
        mLoader = getSupportLoaderManager().initLoader(LOADER_SHOW_LIST, null, this);
        mLoader.forceLoad();
    }

    /*private void restartAsyncTaskLoader(int loader_id){
        switch (loader_id){
            case 2:
                bundle.putString(String.valueOf(RecordForTrainingRecordingLoadPeopleLoader.ARG_USERNAME), stUserName);
                mLoader = getSupportLoaderManager().restartLoader(LOADER_WRITE_ITEM,bundle, this);
                mLoader.forceLoad();
                break;
            case 3:
                bundle.putString(String.valueOf(RecordForTrainingRecordingLoadPeopleLoader.ARG_USERID), stUserId);
                mLoader = getSupportLoaderManager().restartLoader(LOADER_DELETE_ITEM,bundle, this);
                mLoader.forceLoad();
                break;
        }

    }*/

    @Override
    public Loader<List<Map>> onCreateLoader(int id, Bundle args) {
        Loader<List<Map>> loader;
        loader = new SaveLoadResultLoader(this, args, id);
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<List<Map>> loader, List<Map> data) {
        if (data.size() >0){
            etResultSkill.setText(String.valueOf(data.get(0).get("skill")));
            etResultLevel.setText(String.valueOf(data.get(0).get("wod_level")));
            etResultWoD.setText(String.valueOf(data.get(0).get("wod_result")));
        }else{
            Toast.makeText(EnterResultActivity.this, "Есть!", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onLoaderReset(Loader<List<Map>> loader) {

    }
}
