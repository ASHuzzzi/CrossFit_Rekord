package ru.lizzzi.crossfit_rekord.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
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
import android.widget.ProgressBar;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import ru.lizzzi.crossfit_rekord.R;
import ru.lizzzi.crossfit_rekord.data.CalendarWodDBHelper;
import ru.lizzzi.crossfit_rekord.fragments.NetworkCheck;
import ru.lizzzi.crossfit_rekord.loaders.SaveLoadResultLoader;

public class EnterResultActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Boolean> {

    private NetworkCheck networkCheck; //переменная для проврки сети

    private Handler handlerOpenFragment;
    private Thread runnableClickOnbuttonSave;

    private EditText etResultSkill;
    private EditText etResultLevel;
    private EditText etResultWoD;
    private ProgressBar pbSaveUpload;
    private Button btnSave;

    public int LOADER_SAVE_ITEM = 2;
    public int LOADER_DELETE_ITEM = 3;
    public int LOADER_UPLOAD_ITEM = 4;

    private boolean flag; //флаг показывает есть ли данные о тренировки от фрагмента
    private boolean flagDelete =  false; //флаг показывает, что нажата кнопка удалить

    private static final String APP_PREFERENCES = "audata";
    private static final String APP_PREFERENCES_OBJECTID = "ObjectId";
    private static final String APP_PREFERENCES_SELECTEDDAY = "SelectedDay";

    @SuppressLint("SimpleDateFormat") final SimpleDateFormat sdf2 = new SimpleDateFormat("MM/dd/yyyy");

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

        pbSaveUpload = findViewById(R.id.pbSaveUpload);


        ImageButton imbDelete = findViewById(R.id.imbDelete);
        //mLoader = getSupportLoaderManager().initLoader(LOADER_SHOW_LIST, null, this);
        imbDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(flag){
                    AlertDialog.Builder builder = new AlertDialog.Builder(EnterResultActivity.this);
                    builder.setTitle("Внимание!")
                            .setMessage("Удалить результаты?")
                            .setCancelable(false)
                            .setPositiveButton("Да",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            flagDelete =  true;
                                            runnableClickOnbuttonSave.run();
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


        btnSave = findViewById(R.id.btnSaveUpload);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                runnableClickOnbuttonSave.run();
            }
        });

        Button btn = findViewById(R.id.button9);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        //хэндлер для показа progress bar'a
        handlerOpenFragment = new Handler() {
            @SuppressLint("ShowToast")
            @Override
            public void handleMessage(Message msg) {
                Bundle bundle = msg.getData();
                String switchs = bundle.getString("status");
                if (switchs != null){
                    if (switchs.equals("start")){
                        pbSaveUpload.setVisibility(View.VISIBLE);
                    }else{
                        pbSaveUpload.setVisibility(View.INVISIBLE);
                    }
                }
            }
        };

        //поток запускаемый кнопкой сохранить/записать
        Runnable runnableClickOnbuttonSave = new Runnable() {
            @Override
            public void run() {
                Message msg = handlerOpenFragment.obtainMessage();
                Bundle bundle = new Bundle();
                bundle.putString("status", "start");
                msg.setData(bundle);
                handlerOpenFragment.sendMessage(msg);
                networkCheck = new NetworkCheck(EnterResultActivity.this);
                boolean resultCheck = networkCheck.checkInternet();
                if (resultCheck){
                    if(flagDelete){
                        restartAsyncTaskLoader(3); //удалить
                    }else{
                        if(flag){
                            restartAsyncTaskLoader(4); //обновить
                        }else {
                            restartAsyncTaskLoader(2); //сохранить
                        }
                    }

                }else {
                    bundle.putString("status", "stop");

                }
            }
        };
        this.runnableClickOnbuttonSave = new Thread(runnableClickOnbuttonSave);
        this.runnableClickOnbuttonSave.setDaemon(true);


    }

    @Override
    protected void onStart() {
        super.onStart();

        pbSaveUpload.setVisibility(View.INVISIBLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(getSupportActionBar()).setTitle("Мои результаты тренировки");
        }
        btnSave.setText("Сохранить");

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
    }

    @Override
    protected void onResume() {
        super.onResume();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    private void restartAsyncTaskLoader(int loader_id){
        Loader<Boolean> mLoader;
        Bundle bundle = new Bundle();
        switch (loader_id){
            case 2:
                bundle.putString(String.valueOf(SaveLoadResultLoader.ARG_USERSKIL), String.valueOf(etResultSkill.getText()));
                bundle.putString(String.valueOf(SaveLoadResultLoader.ARG_USERWODLEVEL), String.valueOf(etResultLevel.getText()));
                bundle.putString(String.valueOf(SaveLoadResultLoader.ARG_USERWODRESULT), String.valueOf(etResultWoD.getText()));
                mLoader = getSupportLoaderManager().restartLoader(LOADER_SAVE_ITEM, bundle, this);
                mLoader.forceLoad();
                break;

            case 3:
                mLoader = getSupportLoaderManager().restartLoader(LOADER_DELETE_ITEM,null, this);
                mLoader.forceLoad();
                break;

            case 4:
                bundle.putString(String.valueOf(SaveLoadResultLoader.ARG_USERSKIL), String.valueOf(etResultSkill.getText()));
                bundle.putString(String.valueOf(SaveLoadResultLoader.ARG_USERWODLEVEL), String.valueOf(etResultLevel.getText()));
                bundle.putString(String.valueOf(SaveLoadResultLoader.ARG_USERWODRESULT), String.valueOf(etResultWoD.getText()));
                mLoader = getSupportLoaderManager().restartLoader(LOADER_UPLOAD_ITEM, bundle, this);
                mLoader.forceLoad();
                break;
        }

    }

    @Override
    public Loader<Boolean> onCreateLoader(int id, Bundle args) {
        Loader<Boolean> loader;
        loader = new SaveLoadResultLoader(this, args, id);
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Boolean> loader, Boolean result) {
        if (result){
            if (loader.getId() != 4){
                CalendarWodDBHelper mDBHelper = new CalendarWodDBHelper(this);
                SharedPreferences mSettings = this.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
                String stDay = mSettings.getString(APP_PREFERENCES_SELECTEDDAY, "");
                Date date;
                long lDate = 0;
                try {
                    date = sdf2.parse(stDay);
                    lDate = date.getTime();
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                switch (loader.getId()) {
                    case 2:
                        mDBHelper.saveDates(mSettings.getString(APP_PREFERENCES_OBJECTID, ""), lDate);
                        break;

                    case 3:
                        mDBHelper.deleteDate(mSettings.getString(APP_PREFERENCES_OBJECTID, ""), lDate);
                        break;
                }
            }
            Intent answerIntent = new Intent();
            setResult(RESULT_OK, answerIntent);
            finish();
        }else {
            Toast.makeText(EnterResultActivity.this, "Не удалось! Повторите попытку", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLoaderReset(Loader<Boolean> loader) {

    }
}
