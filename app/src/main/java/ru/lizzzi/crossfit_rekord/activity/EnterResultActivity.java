package ru.lizzzi.crossfit_rekord.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import ru.lizzzi.crossfit_rekord.R;
import ru.lizzzi.crossfit_rekord.data.CalendarWodDBHelper;
import ru.lizzzi.crossfit_rekord.inspectionСlasses.Network;
import ru.lizzzi.crossfit_rekord.loaders.SaveLoadResultLoader;

public class EnterResultActivity
        extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Boolean> {

    private Network network;

    private Handler handlerOpenFragment;
    private Thread threadClickOnbuttonSave;
    private Runnable runnableClickOnbuttonSave;

    private EditText ResultOfSkill;
    private EditText ResultOfWoD;
    private ProgressBar progressBar;
    private Button buttonSaveUpload;

    private RadioButton radioButtonSc;
    private RadioButton radioButtonRx;
    private RadioButton radioButtonRxPlus;

    private boolean flag; //флаг показывает есть ли данные о тренировки от фрагмента
    private boolean flagDelete =  false; //флаг показывает, что нажата кнопка удалить

    private String wodLevel; //переменная для передачи уровня тренировки. По умолчанию Sc.

    private static final String APP_PREFERENCES = "audata";
    private static final String APP_PREFERENCES_OBJECTID = "ObjectId";
    private static final String APP_PREFERENCES_SELECTEDDAY = "SelectedDay";

    private final int LOADER_START_SAVE = 2;
    private final int LOADER_START_DELETE = 3;
    private final int LOADER_START_UPLOAD= 4;

    

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_result);

        wodLevel = getResources().getString(R.string.strActivityERLevelSc);

        ResultOfSkill = findViewById(R.id.etResultSkill);
        ResultOfWoD = findViewById(R.id.etResultWoD);

        progressBar = findViewById(R.id.progressBarSaveUpload);
        buttonSaveUpload = findViewById(R.id.buttonSaveUpload);

        radioButtonSc = findViewById(R.id.rbSc);
        radioButtonRx = findViewById(R.id.rbRx);
        radioButtonRxPlus = findViewById(R.id.rbRxP);
        RadioGroup radioGroup = findViewById(R.id.rgSelectLevel);

        initActionBar();

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.rbSc:
                        wodLevel = getResources().getString(R.string.strActivityERLevelSc);
                        break;
                    case R.id.rbRx:
                        wodLevel = getResources().getString(R.string.strActivityERLevelRx);
                        break;
                    case R.id.rbRxP:
                        wodLevel = getResources().getString(R.string.strActivityERLevelRxPlus);
                }
            }
        });

        buttonSaveUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String forDeleteSpace = ResultOfSkill.getText().toString();
                if (forDeleteSpace.length() > 1) {
                    if(forDeleteSpace.endsWith(" ")) {
                        forDeleteSpace =  forDeleteSpace.substring(0, forDeleteSpace.length() - 1);
                        ResultOfSkill.setText(forDeleteSpace);
                    }
                    if (forDeleteSpace.startsWith(" ")) {
                        forDeleteSpace = forDeleteSpace.substring(1);
                        ResultOfSkill.setText(forDeleteSpace);
                    }
                }

                forDeleteSpace = ResultOfWoD.getText().toString();
                if (forDeleteSpace.length() > 1) {
                    if(forDeleteSpace.endsWith(" ")) {
                        forDeleteSpace =  forDeleteSpace.substring(0, forDeleteSpace.length() - 1);
                        ResultOfWoD.setText(forDeleteSpace);
                    }
                    if(forDeleteSpace.startsWith(" ")) {
                        forDeleteSpace = forDeleteSpace.substring(1);
                        ResultOfWoD.setText(forDeleteSpace);
                    }
                }

                progressBar.setVisibility(View.VISIBLE);
                buttonSaveUpload.setClickable(false);
                threadClickOnbuttonSave = new Thread(runnableClickOnbuttonSave);
                threadClickOnbuttonSave.setDaemon(true);
                runnableClickOnbuttonSave.run();
            }
        });

        //хэндлер для показа progress bar'a
        handlerOpenFragment = new Handler() {
            @SuppressLint("ShowToast")
            @Override
            public void handleMessage(Message msg) {
                Bundle bundle = msg.getData();
                boolean resultCheck = bundle.getBoolean("networkCheck");
                if (resultCheck) {
                    progressBar.setVisibility(View.VISIBLE);
                    buttonSaveUpload.setClickable(false);

                    int loaderId = (flagDelete)
                            ? LOADER_START_DELETE
                            : (flag)
                                ? LOADER_START_UPLOAD
                                : LOADER_START_SAVE;
                    restartAsyncTaskLoader(loaderId);
                } else {
                    progressBar.setVisibility(View.INVISIBLE);
                    buttonSaveUpload.setClickable(true);
                    Toast.makeText(
                            EnterResultActivity.this,
                            "Нет подключения к сети!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        };

        //поток запускаемый кнопкой сохранить/записать и из меню "удалить"
        runnableClickOnbuttonSave = new Runnable() {
            @Override
            public void run() {
                network = new Network(EnterResultActivity.this);
                Bundle bundle = new Bundle();
                bundle.putBoolean("networkCheck", network.checkConnection());
                Message msg = handlerOpenFragment.obtainMessage();
                msg.setData(bundle);
                handlerOpenFragment.sendMessage(msg);
            }
        };
    }

    private void initActionBar() {
        this.setSupportActionBar((Toolbar) findViewById(R.id.toolbarER));
        final ActionBar actionBar = this.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(false);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        progressBar.setVisibility(View.INVISIBLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(getSupportActionBar()).setTitle("Мои результаты тренировки");
        }

        Intent intent = getIntent();
        flag = intent.getBooleanExtra("flag", false);
        if (flag) {
            ResultOfSkill.setText(intent.getStringExtra("skill"));

            //создаем флаг для проверки чтобы точно какая-то кнопка была выбрана
            boolean checkLevelFlag = false;
            wodLevel = intent.getStringExtra("level");
            switch(wodLevel) {
                case "Sc":
                    radioButtonSc.setChecked(true);
                    checkLevelFlag = true;
                    break;
                case "Rx":
                    radioButtonRx.setChecked(true);
                    checkLevelFlag = true;
                    break;
                case "Rx+":
                    radioButtonRxPlus.setChecked(true);
                    checkLevelFlag = true;
                    break;
            }

            //если никакая кнопка не была выбрана, то по умолчанию выбираем Sc.
            if (!checkLevelFlag) {
                radioButtonSc.setChecked(true);
                wodLevel = getResources().getString(R.string.strActivityERLevelSc);
            }

            ResultOfWoD.setText(intent.getStringExtra("results"));

        } else {
            ResultOfSkill.setText("");
            radioButtonSc.setChecked(true);
            wodLevel = getResources().getString(R.string.strActivityERLevelSc);
            ResultOfWoD.setText("");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    private void restartAsyncTaskLoader(int loaderId){
        Bundle bundle = new Bundle();
        if (loaderId != LOADER_START_DELETE) {
            bundle.putString(
                    String.valueOf(SaveLoadResultLoader.USER_SKILL),
                    String.valueOf(ResultOfSkill.getText()));
            bundle.putString(String.valueOf(
                    SaveLoadResultLoader.USER_WOD_LEVEL),
                    wodLevel);
            bundle.putString(String.valueOf(
                    SaveLoadResultLoader.USER_WOD_RESULT),
                    String.valueOf(ResultOfWoD.getText()));
        }
        Loader<Boolean> loader = 
                getSupportLoaderManager().restartLoader(loaderId, bundle, this);
        loader.forceLoad();

    }

    @NonNull
    @Override
    public Loader<Boolean> onCreateLoader(int id, Bundle args) {
        Loader<Boolean> loader;
        loader = new SaveLoadResultLoader(this, args, id);
        return loader;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Boolean> loader, Boolean result) {
        if (result){
            SharedPreferences sharedPreferences = 
                    this.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
            String selectedDay = sharedPreferences.getString(APP_PREFERENCES_SELECTEDDAY, "");
            long dateForLoader = 0;
            try {
                SimpleDateFormat dateFormat = 
                        new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
                Date date = dateFormat.parse(selectedDay);
                dateForLoader = date.getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }

            CalendarWodDBHelper dbHelper = new CalendarWodDBHelper(this);
            switch (loader.getId()) {
                
                case LOADER_START_SAVE:
                    dbHelper.saveDate(
                            sharedPreferences.getString(APP_PREFERENCES_OBJECTID, ""), 
                            dateForLoader);
                    break;

                case LOADER_START_DELETE:
                    dbHelper.deleteDate(
                            sharedPreferences.getString(APP_PREFERENCES_OBJECTID, ""), 
                            dateForLoader);
                    break;

                case LOADER_START_UPLOAD:
                    dbHelper.saveDate(
                            sharedPreferences.getString(APP_PREFERENCES_OBJECTID, ""), 
                            dateForLoader);
                    break;
            }
            setResult(RESULT_OK, new Intent());
            finish();
        }else {
            Toast.makeText(
                    EnterResultActivity.this, 
                    "Не удалось! Повторите попытку", 
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Boolean> loader) {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_er_drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.delete:
                if (flag) {
                    AlertDialog.Builder builder = 
                            new AlertDialog.Builder(EnterResultActivity.this);
                    builder.setTitle("Внимание!")
                            .setMessage("Удалить результаты?")
                            .setCancelable(false)
                            .setPositiveButton("Да",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            flagDelete =  true;
                                            progressBar.setVisibility(View.VISIBLE);
                                            buttonSaveUpload.setPressed(true);
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
                    alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorAccent));
                    alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
                } else {
                    Toast.makeText(
                            EnterResultActivity.this, 
                            "Нет данных для удаления", 
                            Toast.LENGTH_SHORT).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
    }
}
