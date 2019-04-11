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
import java.util.Objects;

import ru.lizzzi.crossfit_rekord.R;
import ru.lizzzi.crossfit_rekord.data.CalendarWodDBHelper;
import ru.lizzzi.crossfit_rekord.inspectionСlasses.NetworkCheck;
import ru.lizzzi.crossfit_rekord.loaders.SaveLoadResultLoader;

public class EnterResultActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Boolean> {

    private NetworkCheck networkCheck; //переменная для проврки сети

    private Handler handlerOpenFragment;
    private Thread threadClickOnbuttonSave;
    private Runnable runnableClickOnbuttonSave;

    private EditText etResultSkill;
    private EditText etResultWoD;
    private ProgressBar pbSaveUpload;
    private Button btnSave;

    private RadioButton rbSC;
    private RadioButton rbRx;
    private RadioButton rbRxP;

    private boolean flag; //флаг показывает есть ли данные о тренировки от фрагмента
    private boolean flagDelete =  false; //флаг показывает, что нажата кнопка удалить

    private String stLevel; //переменная для передачи уровня тренировки. По умолчанию Sc.

    private static final String APP_PREFERENCES = "audata";
    private static final String APP_PREFERENCES_OBJECTID = "ObjectId";
    private static final String APP_PREFERENCES_SELECTEDDAY = "SelectedDay";

    private final int LOADER_START_SAVE = 2;
    private final int LOADER_START_DELETE = 3;
    private final int LOADER_START_UPLOAD= 4;

    @SuppressLint("SimpleDateFormat") final SimpleDateFormat sdf2 = new SimpleDateFormat("MM/dd/yyyy");

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_result);


        stLevel = getResources().getString(R.string.strActivityERLevelSc);

        etResultSkill = findViewById(R.id.etResultSkill);
        etResultWoD = findViewById(R.id.etResultWoD);

        pbSaveUpload = findViewById(R.id.progressBarSaveUpload);
        btnSave = findViewById(R.id.buttonSaveUpload);

        rbSC = findViewById(R.id.rbSc);
        rbRx = findViewById(R.id.rbRx);
        rbRxP = findViewById(R.id.rbRxP);
        RadioGroup rgSelectLevel = findViewById(R.id.rgSelectLevel);

        initActionBar();

        rgSelectLevel.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.rbSc:
                        stLevel = getResources().getString(R.string.strActivityERLevelSc);
                        break;
                    case R.id.rbRx:
                        stLevel = getResources().getString(R.string.strActivityERLevelRx);
                        break;
                    case R.id.rbRxP:
                        stLevel = getResources().getString(R.string.strActivityERLevelRxPlus);
                }
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String deleteSpace = etResultSkill.getText().toString();
                if (deleteSpace.length() > 1) {
                    if(deleteSpace.endsWith(" ")) {
                        deleteSpace =  deleteSpace.substring(0, deleteSpace.length() - 1);
                        etResultSkill.setText(deleteSpace);
                    }
                    if (deleteSpace.startsWith(" ")) {
                        deleteSpace = deleteSpace.substring(1);
                        etResultSkill.setText(deleteSpace);
                    }
                }

                deleteSpace = etResultWoD.getText().toString();
                if (deleteSpace.length() > 1) {
                    if(deleteSpace.endsWith(" ")) {
                        deleteSpace =  deleteSpace.substring(0, deleteSpace.length() - 1);
                        etResultWoD.setText(deleteSpace);
                    }
                    if(deleteSpace.startsWith(" ")) {
                        deleteSpace = deleteSpace.substring(1);
                        etResultWoD.setText(deleteSpace);
                    }
                }

                pbSaveUpload.setVisibility(View.VISIBLE);
                btnSave.setClickable(false);
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
                    pbSaveUpload.setVisibility(View.VISIBLE);
                    btnSave.setClickable(false);


                    int loaderId;
                    if (flagDelete) {
                        loaderId = LOADER_START_DELETE;
                    } else {
                        if (flag) {
                            loaderId = LOADER_START_UPLOAD;
                        } else {
                            loaderId = LOADER_START_SAVE;
                        }
                    }
                    restartAsyncTaskLoader(loaderId);
                } else {
                    pbSaveUpload.setVisibility(View.INVISIBLE);
                    btnSave.setClickable(true);
                    Toast.makeText(EnterResultActivity.this, "Нет подключения к сети!", Toast.LENGTH_SHORT).show();
                }
            }
        };

        //поток запускаемый кнопкой сохранить/записать и из меню "удалить"
        runnableClickOnbuttonSave = new Runnable() {
            @Override
            public void run() {

                networkCheck = new NetworkCheck(EnterResultActivity.this);
                Bundle bundle = new Bundle();
                boolean resultCheck = networkCheck.checkInternet();
                if (resultCheck) {
                    bundle.putBoolean("networkCheck", true);
                } else {
                    bundle.putBoolean("networkCheck", false);
                }
                Message msg = handlerOpenFragment.obtainMessage();
                msg.setData(bundle);
                handlerOpenFragment.sendMessage(msg);
            }
        };



    }

    private void initActionBar() {
        /*Toolbar toolbar = findViewById(R.id.toolbarER);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });*/

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

        pbSaveUpload.setVisibility(View.INVISIBLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(getSupportActionBar()).setTitle("Мои результаты тренировки");
        }

        Intent intent = getIntent();
        flag = intent.getBooleanExtra("flag", false);
        if (flag){
            etResultSkill.setText(intent.getStringExtra("skill"));

            //создаем флаг для проверки чтобы точно какая-то кнопка была выбрана
            boolean checkLevelFlag = false;
            stLevel = intent.getStringExtra("level");
            switch(stLevel){
                case "Sc":
                    rbSC.setChecked(true);
                    checkLevelFlag = true;
                    break;
                case "Rx":
                    rbRx.setChecked(true);
                    checkLevelFlag = true;
                    break;
                case "Rx+":
                    rbRxP.setChecked(true);
                    checkLevelFlag = true;
                    break;
            }

            //если никакая кнопка не была выбрана, то по умолчанию выбираем Sc.
            if (!checkLevelFlag){
                rbSC.setChecked(true);
                stLevel = getResources().getString(R.string.strActivityERLevelSc);
            }

            etResultWoD.setText(intent.getStringExtra("results"));

        }else{
            etResultSkill.setText("");
            rbSC.setChecked(true);
            stLevel = getResources().getString(R.string.strActivityERLevelSc);
            etResultWoD.setText("");

        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    private void restartAsyncTaskLoader(int loaderId){
        Loader<Boolean> mLoader;
        Bundle bundle = new Bundle();

        switch (loaderId){
            case LOADER_START_SAVE:
                bundle.putString(String.valueOf(SaveLoadResultLoader.ARG_USERSKIL), String.valueOf(etResultSkill.getText()));
                bundle.putString(String.valueOf(SaveLoadResultLoader.ARG_USERWODLEVEL), stLevel);
                bundle.putString(String.valueOf(SaveLoadResultLoader.ARG_USERWODRESULT), String.valueOf(etResultWoD.getText()));
                mLoader = getSupportLoaderManager().restartLoader(loaderId, bundle, this);
                mLoader.forceLoad();
                break;

            case LOADER_START_DELETE:
                mLoader = getSupportLoaderManager().restartLoader(loaderId,null, this);
                mLoader.forceLoad();
                break;

            case LOADER_START_UPLOAD:
                bundle.putString(String.valueOf(SaveLoadResultLoader.ARG_USERSKIL), String.valueOf(etResultSkill.getText()));
                bundle.putString(String.valueOf(SaveLoadResultLoader.ARG_USERWODLEVEL), stLevel);
                bundle.putString(String.valueOf(SaveLoadResultLoader.ARG_USERWODRESULT), String.valueOf(etResultWoD.getText()));
                mLoader = getSupportLoaderManager().restartLoader(loaderId, bundle, this);
                mLoader.forceLoad();
                break;
        }

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
                case LOADER_START_SAVE:
                    mDBHelper.saveDates(mSettings.getString(APP_PREFERENCES_OBJECTID, ""), lDate);
                    break;

                case LOADER_START_DELETE:
                    mDBHelper.deleteDate(mSettings.getString(APP_PREFERENCES_OBJECTID, ""), lDate);
                    break;

                case LOADER_START_UPLOAD:
                    mDBHelper.saveDates(mSettings.getString(APP_PREFERENCES_OBJECTID, ""), lDate);
                    break;
            }
            Intent answerIntent = new Intent();
            setResult(RESULT_OK, answerIntent);
            finish();
        }else {
            Toast.makeText(EnterResultActivity.this, "Не удалось! Повторите попытку", Toast.LENGTH_SHORT).show();
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
                    AlertDialog.Builder builder = new AlertDialog.Builder(EnterResultActivity.this);
                    builder.setTitle("Внимание!")
                            .setMessage("Удалить результаты?")
                            .setCancelable(false)
                            .setPositiveButton("Да",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            flagDelete =  true;
                                            pbSaveUpload.setVisibility(View.VISIBLE);
                                            btnSave.setPressed(true);
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
                    Toast.makeText(EnterResultActivity.this, "Нет данных для удаления", Toast.LENGTH_SHORT).show();
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
