package ru.lizzzi.crossfit_rekord.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import android.content.Context;

import java.util.List;
import java.util.Map;

import ru.lizzzi.crossfit_rekord.R;
import ru.lizzzi.crossfit_rekord.fragments.NetworkCheck;
import ru.lizzzi.crossfit_rekord.loaders.SaveLoadResultLoader;

public class EnterResultActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Map>> {

    NetworkCheck networkCheck;

    private Handler handlerOpenFragment;
    private Thread threadOpenFragment;

    private Thread threadClickOnbutton;

    private EditText etResultSkill;
    private EditText etResultLevel;
    private EditText etResultWoD;

    private Button btnSave;

    Context context;

    public int LOADER_SHOW_LIST = 1;
    public int LOADER_SAVE_ITEM = 2;
    public int LOADER_DELETE_ITEM = 3;
    public int LOADER_UPLOAD_ITEM = 4;

    private NetworkCheck NetworkCheck; //переменная для проврки сети

    private boolean flag;

    private ImageButton imbDelete;

    public final static String THIEF = "THIEF";

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

        imbDelete = findViewById(R.id.imbDelete);

        imbDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(flag){
                    AlertDialog.Builder builder = new AlertDialog.Builder(EnterResultActivity.this);
                    builder.setTitle("Внимание!")
                            .setMessage("Удалить результаты?")
                            //.setIcon(R.drawable.ic_android_cat)
                            .setCancelable(false)
                            .setPositiveButton("Да",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            restartAsyncTaskLoader(3);
                                        }
                                    })
                            .setNegativeButton("Нет",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                }else {
                    Toast.makeText(EnterResultActivity.this, "Нет данных для удаления", Toast.LENGTH_SHORT).show();
                }

            }
        });


        btnSave = findViewById(R.id.btnSave);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NetworkCheck = new NetworkCheck(EnterResultActivity.this);
                boolean resultCheck = NetworkCheck.checkInternet();
                if (resultCheck){
                    threadClickOnbutton.run();
                }else {
                    Toast.makeText(EnterResultActivity.this, "Нет подключения", Toast.LENGTH_SHORT).show();
                }
            }
        });

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
        Runnable runnableClickOnbutton = new Runnable() {
            @Override
            public void run() {
                Message msg = handlerOpenFragment.obtainMessage();
                Bundle bundle = new Bundle();
                networkCheck = new NetworkCheck(EnterResultActivity.this);
                boolean resultCheck = networkCheck.checkInternet();
                if (resultCheck){
                    if(flag){
                        restartAsyncTaskLoader(4);
                    }else {
                        restartAsyncTaskLoader(2);
                    }
                    /*if(etResultSkill.getText().length() == 0 & etResultLevel.getText().length() == 0
                            & etResultWoD.getText().length() == 0){
                        restartAsyncTaskLoader(3);
                    }else {
                        restartAsyncTaskLoader(4);
                    }*/
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


    }

    @Override
    protected void onStart() {
        super.onStart();

        getSupportActionBar().setTitle("Мои результаты тренировки");

        Intent intent = getIntent();
        flag = intent.getBooleanExtra("flag", false);
        if (flag){
            etResultSkill.setText(intent.getStringExtra("skill"));
            etResultLevel.setText(intent.getStringExtra("level"));
            etResultWoD.setText(intent.getStringExtra("results"));

        }else{
            etResultSkill.setText("");
            etResultLevel.setText("");
            etResultWoD.setText("");

        }
        btnSave.setText("Сохранить");

    }

    @Override
    protected void onResume() {
        super.onResume();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        //threadOpenFragment.start();
    }


    private void firstStartAsyncTaskLoader(){
        mLoader = getSupportLoaderManager().initLoader(LOADER_SHOW_LIST, null, this);
        mLoader.forceLoad();
    }

    private void restartAsyncTaskLoader(int loader_id){
        switch (loader_id){
            case 2:

                break;

            case 3:

                mLoader = getSupportLoaderManager().restartLoader(LOADER_DELETE_ITEM,null, this);
                mLoader.forceLoad();
                break;

            case 4:
                Bundle bundle = new Bundle();
                bundle.putString(String.valueOf(SaveLoadResultLoader.ARG_USERSKIL), String.valueOf(etResultSkill.getText()));
                bundle.putString(String.valueOf(SaveLoadResultLoader.ARG_USERWODLEVEL), String.valueOf(etResultLevel.getText()));
                bundle.putString(String.valueOf(SaveLoadResultLoader.ARG_USERWODRESULT), String.valueOf(etResultWoD.getText()));
                mLoader = getSupportLoaderManager().restartLoader(LOADER_UPLOAD_ITEM, bundle, this);
                mLoader.forceLoad();
                break;
        }

    }

    @Override
    public Loader<List<Map>> onCreateLoader(int id, Bundle args) {
        Loader<List<Map>> loader;
        loader = new SaveLoadResultLoader(this, args, id);
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<List<Map>> loader, List<Map> data) {

        if (loader.getId() != 1){
            Intent answerIntent = new Intent();
            setResult(RESULT_OK, answerIntent);
            finish();
        }

        if (data != null && data.size() > 0){
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
