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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
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
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        etResultSkill = findViewById(R.id.etResultSkill);
        etResultLevel = findViewById(R.id.etResultLevel);
        etResultWoD = findViewById(R.id.etResultWoD);

        pbSaveUpload = findViewById(R.id.pbSaveUpload);
        btnSave = findViewById(R.id.btnSaveUpload);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String stCheckSpace = etResultSkill.getText().toString();
                if(stCheckSpace.endsWith(" ")){
                    stCheckSpace.substring(0, stCheckSpace.length() - 1);
                    etResultSkill.setText(stCheckSpace);
                }

                stCheckSpace = etResultLevel.getText().toString();
                if(stCheckSpace.endsWith(" ")){
                    stCheckSpace.substring(0, stCheckSpace.length() - 1);
                    etResultLevel.setText(stCheckSpace);
                }

                stCheckSpace = etResultWoD.getText().toString();
                if(stCheckSpace.endsWith(" ")){
                    stCheckSpace.substring(0, stCheckSpace.length() - 1);
                    etResultWoD.setText(stCheckSpace);
                }

                pbSaveUpload.setVisibility(View.VISIBLE);
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
                String resultCheck = bundle.getString("networkCheck");
                if (resultCheck != null && resultCheck.equals("true")) {
                    pbSaveUpload.setVisibility(View.VISIBLE);

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
                    pbSaveUpload.setVisibility(View.INVISIBLE);
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
                if (resultCheck){
                    bundle.putString("networkCheck", String.valueOf(true));

                }else {
                    bundle.putString("networkCheck", String.valueOf(false));

                }

                Message msg = handlerOpenFragment.obtainMessage();
                msg.setData(bundle);
                handlerOpenFragment.sendMessage(msg);
            }
        };



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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_er_drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId()==R.id.delete)
        {
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
                                        pbSaveUpload.setVisibility(View.VISIBLE);
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
            }else {
                Toast.makeText(EnterResultActivity.this, "Нет данных для удаления", Toast.LENGTH_SHORT).show();
            }
        }

        return true;
    }
}
