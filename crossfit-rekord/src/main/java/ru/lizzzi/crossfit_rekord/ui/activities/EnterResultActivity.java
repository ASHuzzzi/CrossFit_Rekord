package ru.lizzzi.crossfit_rekord.ui.activities;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
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
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;

import ru.lizzzi.crossfit_rekord.R;
import ru.lizzzi.crossfit_rekord.model.EnterResultViewModel;

public class EnterResultActivity extends AppCompatActivity {

    private EditText editResultOfSkill;
    private EditText editResultOfWoD;
    private ProgressBar progressBar;
    private Button buttonSaveUpload;
    private RadioButton radioButtonSc;
    private RadioButton radioButtonRx;
    private RadioButton radioButtonRxPlus;

    private EnterResultViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_result);
        viewModel = ViewModelProviders.of(EnterResultActivity.this)
                .get(EnterResultViewModel.class);

        editResultOfSkill = findViewById(R.id.etResultSkill);
        editResultOfWoD = findViewById(R.id.etResultWoD);
        progressBar = findViewById(R.id.progressBarSaveUpload);

        initActionBar();
        initTextEnterResult();
        initRadioGroup();
        initButtonSaveUpload();
    }


    private void initActionBar() {
        this.setSupportActionBar((Toolbar) findViewById(R.id.toolbarER));
        final ActionBar actionBar = this.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(false);
        }
    }

    private void initTextEnterResult() {
        TextView textEnterResult = findViewById(R.id.textEnterResult);
        String enterResultText = getResources().getString(R.string.strActivityERText) + " " + viewModel.getDateForShow();
        textEnterResult.setText(enterResultText);
    }

    private void initRadioGroup() {
        radioButtonSc = findViewById(R.id.rbSc);
        radioButtonRx = findViewById(R.id.rbRx);
        radioButtonRxPlus = findViewById(R.id.rbRxP);
        RadioGroup radioGroup = findViewById(R.id.rgSelectLevel);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rbSc:
                        viewModel.setWodLevel(getResources().getString(R.string.sc));
                        break;
                    case R.id.rbRx:
                        viewModel.setWodLevel(getResources().getString(R.string.rx));
                        break;
                    case R.id.rbRxP:
                        viewModel.setWodLevel(getResources().getString(R.string.rxPlus));
                }
            }
        });
    }

    private void  initButtonSaveUpload() {
        buttonSaveUpload = findViewById(R.id.buttonSaveUpload);
        buttonSaveUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editResultOfSkill.setText(deleteSpace(editResultOfSkill.getText().toString()));
                editResultOfWoD.setText(deleteSpace(editResultOfWoD.getText().toString()));
                changeWodResult();
            }
        });
    }

    private String deleteSpace(String inputText) {
        if (inputText.length() > 1) {
            if(inputText.endsWith(" ")) {
                inputText =  inputText.substring(0, inputText.length() - 1);
            }
            if (inputText.startsWith(" ")) {
                inputText = inputText.substring(1);
            }
        }
        return inputText;
    }

    private void changeWodResult() {
        progressBar.setVisibility(View.VISIBLE);
        buttonSaveUpload.setClickable(false);
        LiveData<Boolean> liveDataConnection = viewModel.checkNetwork();
        liveDataConnection.observe(EnterResultActivity.this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isConnected) {
                if (isConnected) {
                    LiveData<Boolean> liveData = viewModel.saveWorkoutResult(
                            editResultOfSkill.getText().toString(),
                            editResultOfWoD.getText().toString());
                    liveData.observe(EnterResultActivity.this, new Observer<Boolean>() {
                        @Override
                        public void onChanged(Boolean isSaved) {
                            if (isSaved) {
                                setResult(RESULT_OK, new Intent());
                                finish();
                            } else {
                                showToast("Не удалось! Повторите попытку");
                            }
                        }
                    });
                } else {
                    progressBar.setVisibility(View.INVISIBLE);
                    buttonSaveUpload.setClickable(true);
                    showToast("Нет подключения к сети!");
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        progressBar.setVisibility(View.INVISIBLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(getSupportActionBar()).setTitle("Результаты тренировки");
        }

        radioButtonSc.setChecked(true);
        Intent intent = getIntent();
        viewModel.setHaveTrainingData(intent.getBooleanExtra("flag", false));
        if (viewModel.isHaveTrainingData()) {
            editResultOfSkill.setText(intent.getStringExtra("skill"));
            viewModel.setWodLevel(intent.getStringExtra("level"));
            switch(viewModel.getWodLevel()) {
                case "Rx":
                    radioButtonRx.setChecked(true);
                    break;
                case "Rx+":
                    radioButtonRxPlus.setChecked(true);
                    break;
            }
            editResultOfWoD.setText(intent.getStringExtra("results"));
        } else {
            editResultOfSkill.setText("");
            viewModel.setWodLevel(getResources().getString(R.string.sc));
            editResultOfWoD.setText("");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
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
                if (viewModel.isHaveTrainingData()) {
                    AlertDialog.Builder builder = 
                            new AlertDialog.Builder(EnterResultActivity.this);
                    builder.setTitle("Внимание!")
                            .setMessage("Удалить результаты?")
                            .setCancelable(false)
                            .setPositiveButton("Да",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            viewModel.setActionDelete();
                                            changeWodResult();
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
                    alert.getButton(AlertDialog.BUTTON_POSITIVE)
                            .setTextColor(getResources().getColor(R.color.colorAccent));
                    alert.getButton(AlertDialog.BUTTON_NEGATIVE)
                            .setTextColor(getResources().getColor(R.color.colorPrimary));
                } else {
                    showToast("Нет данных для удаления");
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

    private void showToast(String toastText) {
        Toast.makeText(EnterResultActivity.this, toastText, Toast.LENGTH_SHORT).show();
    }
}
