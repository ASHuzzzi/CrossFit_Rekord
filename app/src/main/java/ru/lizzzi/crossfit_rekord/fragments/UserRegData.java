package ru.lizzzi.crossfit_rekord.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ru.lizzzi.crossfit_rekord.R;
import ru.lizzzi.crossfit_rekord.interfaces.InterfaceChangeTitle;
import ru.lizzzi.crossfit_rekord.loaders.UserRegDataLoader;

public class UserRegData extends Fragment implements LoaderManager.LoaderCallbacks<Boolean>{

    private static final String APP_PREFERENCES = "audata";
    private static final String APP_PREFERENCES_CARDNUMBER = "cardNumber";
    private static final String APP_PREFERENCES_EMAIL = "Email";
    private static final String APP_PREFERENCES_PASSWORD = "Password";
    private SharedPreferences mSettings;

    private EditText etEmail;
    private EditText etPassword;
    private EditText etRepPassword;
    private ProgressBar pbUserGegData;
    private Button btnChangeUserRegData;

    private NetworkCheck NetworkCheck; //переменная для проврки сети

    private Handler handlerUserRegDataFragment;
    private Thread threadUserRegDataFragment;
    private Runnable runnableUserRegDataFragment;

    @SuppressLint("HandlerLeak")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_userregdata, container, false);

        mSettings = getContext().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        etEmail = v.findViewById(R.id.etEmail);
        etPassword = v.findViewById(R.id.etPassword);
        etRepPassword = v.findViewById(R.id.etRepPasword);
        pbUserGegData = v.findViewById(R.id.pbUserGegData);
        btnChangeUserRegData = v.findViewById(R.id.btnChangeUserRegData);

        //хэндлер для потока runnableOpenFragment
        handlerUserRegDataFragment = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Bundle bundle = msg.getData();
                String result_check = bundle.getString("result");
                if (result_check != null && result_check.equals("true")){
                    ChangeUIElements(1);
                    startAsyncTaskLoader(
                            mSettings.getString(APP_PREFERENCES_CARDNUMBER, ""),
                            etEmail.getText().toString(),
                            mSettings.getString(APP_PREFERENCES_PASSWORD, ""),
                            etPassword.getText().toString()

                    );
                }else{
                    ChangeUIElements(0);
                    Toast.makeText(getContext(), "Нет подключения", Toast.LENGTH_SHORT).show();
                }
            }
        };

        //поток запускаемый при создании экрана (запуск происходит из onStart)
        runnableUserRegDataFragment = new Runnable() {
            @Override
            public void run() {

                NetworkCheck = new NetworkCheck(getContext());
                boolean resultCheck = NetworkCheck.checkInternet();
                Bundle bundle = new Bundle();
                if (resultCheck){
                    bundle.putString("result", String.valueOf(true));

                }else {
                    bundle.putString("result", String.valueOf(false));
                }
                Message msg = handlerUserRegDataFragment.obtainMessage();
                msg.setData(bundle);
                handlerUserRegDataFragment.sendMessage(msg);
            }
        };

        btnChangeUserRegData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (etEmail.getText().length()== 0 || !isEmailValid(etEmail.getText().toString())){
                    etEmail.setFocusableInTouchMode(true);
                    etEmail.setFocusable(true);
                    etEmail.requestFocus();
                    Toast.makeText(getContext(), "Введите почту!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(etPassword.getText().length()== 0 || etRepPassword.getText().length()==0){
                    if(etPassword.getText().length()== 0){
                        etPassword.setFocusableInTouchMode(true);
                        etPassword.setFocusable(true);
                        etPassword.requestFocus();
                        Toast.makeText(getContext(), "Введите пароль", Toast.LENGTH_SHORT).show();
                    }else {
                        etRepPassword.setFocusableInTouchMode(true);
                        etRepPassword.setFocusable(true);
                        etRepPassword.requestFocus();
                        Toast.makeText(getContext(), "Повторите пароль", Toast.LENGTH_SHORT).show();
                    }
                    return;
                }

                if(!etPassword.getText().toString().equals(etRepPassword.getText().toString())){
                    Toast.makeText(getContext(), "Пароли не совпадают!", Toast.LENGTH_SHORT).show();
                    return;
                }

                //убираем клавиатуру после нажатия на кнопку
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }

                threadUserRegDataFragment = new Thread(runnableUserRegDataFragment);
                threadUserRegDataFragment.setDaemon(true);
                threadUserRegDataFragment.start();
            }
        });

        return v;
    }

    @Override
    public  void onStart() {
        super.onStart();
        if (getActivity() instanceof InterfaceChangeTitle){
            InterfaceChangeTitle listernerChangeTitle = (InterfaceChangeTitle) getActivity();
            listernerChangeTitle.changeTitle(R.string.title_UserRegData_Fragment, R.string.title_AboutMe_Fragment);
        }

    }

    @Override
    public void onResume() {
        super.onResume();

        pbUserGegData.setVisibility(View.INVISIBLE);
        etEmail.setText(mSettings.getString(APP_PREFERENCES_EMAIL, ""));
        etPassword.setText(mSettings.getString(APP_PREFERENCES_PASSWORD, ""));
        etRepPassword.setText(getResources().getString(R.string.empty));
    }

    private void ChangeUIElements(int status){
        if (status == 1){
            pbUserGegData.setVisibility(View.VISIBLE);
            btnChangeUserRegData.setPressed(true);
        }else{
            pbUserGegData.setVisibility(View.INVISIBLE);
            btnChangeUserRegData.setPressed(false);
        }
    }

    private void startAsyncTaskLoader(String carNumber, String e_mail, String oldPassword, String newPassword) {
        Bundle bundle = new Bundle();
        bundle.putString("cardNumber", carNumber);
        bundle.putString("e_mail", e_mail);
        bundle.putString("oldPassword", oldPassword);
        bundle.putString("newPassword", newPassword);
        int LOADERID = 1;
        getLoaderManager().restartLoader(LOADERID, bundle, this).forceLoad();
    }


    @Override
    public Loader<Boolean> onCreateLoader(int id, Bundle args) {
        UserRegDataLoader loader;
        loader = new UserRegDataLoader(getContext(), args);
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Boolean> loader, Boolean data) {
        if (data){
            Toast.makeText(getContext(), "Данные обновлены", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(getContext(), "Повторите сохранение", Toast.LENGTH_SHORT).show();
        }
        ChangeUIElements(0);

    }

    @Override
    public void onLoaderReset(Loader<Boolean> loader) {

    }

    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
