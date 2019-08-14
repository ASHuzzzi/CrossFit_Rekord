package ru.lizzzi.crossfit_rekord.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ru.lizzzi.crossfit_rekord.R;
import ru.lizzzi.crossfit_rekord.inspectionСlasses.NetworkCheck;
import ru.lizzzi.crossfit_rekord.interfaces.TitleChange;
import ru.lizzzi.crossfit_rekord.loaders.ChangeEmailLoader;

public class ChangeEmailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Boolean>{

    private final String APP_PREFERENCES = "audata";
    private final String APP_PREFERENCES_EMAIL = "Email";
    private final String APP_PREFERENCES_PASSWORD = "Password";
    private SharedPreferences mSettings;

    private TextView tvOldEmail;
    private EditText etChangeEmail;
    private ProgressBar pbChangeEmail;
    private Button btnChangeEmail;

    private Handler handlerChangeEmailFragment;
    private Thread threadChangeEmailFragment;
    private Runnable runnableChangeEmailFragment;

    @SuppressLint("HandlerLeak")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_change_email, container, false);

        mSettings = getContext().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        tvOldEmail = v.findViewById(R.id.textOldEmail);
        etChangeEmail = v.findViewById(R.id.editTextNewEmail);
        pbChangeEmail = v.findViewById(R.id.progressbar);
        btnChangeEmail = v.findViewById(R.id.buttonChangeEmail);

        //хэндлер для потока runnableOpenFragment
        handlerChangeEmailFragment = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Bundle bundle = msg.getData();
                String result_check = bundle.getString("result");
                if (result_check != null && result_check.equals("true")){
                    ChangeUIElements(1);
                    startAsyncTaskLoader(
                            mSettings.getString(APP_PREFERENCES_EMAIL, ""),
                            etChangeEmail.getText().toString(),
                            mSettings.getString(APP_PREFERENCES_PASSWORD, "")

                    );
                }else{
                    ChangeUIElements(0);
                    Toast.makeText(getContext(), "Нет подключения", Toast.LENGTH_SHORT).show();
                }
            }
        };

        //поток запускаемый при создании экрана (запуск происходит из onStart)
        runnableChangeEmailFragment = new Runnable() {
            @Override
            public void run() {

                NetworkCheck network = new NetworkCheck(getContext());
                Bundle bundle = new Bundle();
                boolean checkDone = network.checkConnection();
                if (checkDone){
                    bundle.putString("result", String.valueOf(true));

                }else {
                    bundle.putString("result", String.valueOf(false));
                }
                Message msg = handlerChangeEmailFragment.obtainMessage();
                msg.setData(bundle);
                handlerChangeEmailFragment.sendMessage(msg);
            }
        };

        btnChangeEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String stCheckSpace = etChangeEmail.getText().toString();
                if(stCheckSpace.endsWith(" ")){
                    stCheckSpace = stCheckSpace.substring(0, stCheckSpace.length() - 1);
                    etChangeEmail.setText(stCheckSpace);
                }

                if (etChangeEmail.getText().length()== 0 || isEmailValid(etChangeEmail.getText().toString())){
                    etChangeEmail.setFocusableInTouchMode(true);
                    etChangeEmail.setFocusable(true);
                    etChangeEmail.requestFocus();
                    Toast.makeText(getContext(), "Введите почту!", Toast.LENGTH_SHORT).show();
                    return;
                }

                //убираем клавиатуру после нажатия на кнопку
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }

                threadChangeEmailFragment = new Thread(runnableChangeEmailFragment);
                threadChangeEmailFragment.setDaemon(true);
                threadChangeEmailFragment.start();
            }
        });

        return v;
    }

    @Override
    public  void onStart() {
        super.onStart();
        if (getActivity() instanceof TitleChange){
            TitleChange listernerTitleChange = (TitleChange) getActivity();
            listernerTitleChange.changeTitle(R.string.title_ChangeEmail_Fragment, R.string.title_AboutMe_Fragment);
        }

    }

    @Override
    public void onResume() {
        super.onResume();

        pbChangeEmail.setVisibility(View.INVISIBLE);
        tvOldEmail.setText(mSettings.getString(APP_PREFERENCES_EMAIL, ""));
        etChangeEmail.setText(getResources().getString(R.string.empty));

    }

    private void ChangeUIElements(int status){
        if (status == 1){
            pbChangeEmail.setVisibility(View.VISIBLE);
            btnChangeEmail.setPressed(true);
        }else{
            pbChangeEmail.setVisibility(View.INVISIBLE);
            btnChangeEmail.setPressed(false);
        }
    }

    private void startAsyncTaskLoader(
            String e_mailOld, String e_mailNew, String password) {
        Bundle bundle = new Bundle();
        bundle.putString("e_mailOld", e_mailOld);
        bundle.putString("e_mailNew", e_mailNew);
        bundle.putString("password", password);
        int LOADERID = 1;
        getLoaderManager().restartLoader(LOADERID, bundle, this).forceLoad();
    }


    @NonNull
    @Override
    public Loader<Boolean> onCreateLoader(int id, Bundle args) {
        ChangeEmailLoader loader;
        loader = new ChangeEmailLoader(getContext(), args);
        return loader;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Boolean> loader, Boolean data) {
        if (data){
            tvOldEmail.setText(mSettings.getString(APP_PREFERENCES_EMAIL, ""));
            etChangeEmail.setText(getResources().getString(R.string.empty));
            Toast.makeText(getContext(), "Данные обновлены", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(getContext(), "Повторите сохранение", Toast.LENGTH_SHORT).show();
        }
        ChangeUIElements(0);

    }

    @Override
    public void onLoaderReset(@NonNull Loader<Boolean> loader) {

    }

    private static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return !matcher.matches();
    }
}
