package ru.lizzzi.crossfit_rekord.fragments;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.LoaderManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import ru.lizzzi.crossfit_rekord.interfaces.InterfaceChangeTitle;
import ru.lizzzi.crossfit_rekord.loaders.AboutMeLoader;

/**
 * Created by basso on 07.03.2018.
 */
//TODO см в хэлпе User properties. Сначала логин, от него юзер -> нужные параметры.
public class AboutMeFragment extends Fragment implements LoaderManager.LoaderCallbacks<Boolean> {

    private static final String APP_PREFERENCES = "audata";
    private static final String APP_PREFERENCES_OBJECTID = "ObjectId";
    private static final String APP_PREFERENCES_USERNAME = "Username";
    private static final String APP_PREFERENCES_USERSURNAME = "Usersurname";
    private static final String APP_PREFERENCES_CARDNUMBER = "cardNumber";
    private static final String APP_PREFERENCES_EMAIL = "Email";
    private static final String APP_PREFERENCES_PHONE = "Phone";
    private SharedPreferences mSettings;

    private TextView tvCardNumber;
    private EditText etName;
    private EditText etSurname;
    private EditText etEmail;
    private EditText etPhone;
    private Button btChangeUserData;
    private ProgressBar pbAboutMe;

    private NetworkCheck NetworkCheck; //переменная для проврки сети

    private Handler handlerAboutMeFragment;
    private Thread threadAboutMeFragment;
    private Runnable runnableAboutMeFragment;

    @SuppressLint("HandlerLeak")
    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_aboutme, container, false);

        mSettings = getContext().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        tvCardNumber = v.findViewById(R.id.tvCardNumber);
        etName = v.findViewById(R.id.etName);
        etSurname = v.findViewById(R.id.etSurname);
        etEmail = v.findViewById(R.id.etEmail);
        etPhone = v.findViewById(R.id.etPhone);
        btChangeUserData = v.findViewById(R.id.btnChangeUserData);
        pbAboutMe = v.findViewById(R.id.pbAboutMe);

        //хэндлер для потока runnableOpenFragment
        handlerAboutMeFragment = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Bundle bundle = msg.getData();
                String result_check = bundle.getString("result");
                if (result_check != null && result_check.equals("true")){
                    ChangeUIElements(1);
                    startAsyncTaskLoader(
                            mSettings.getString(APP_PREFERENCES_OBJECTID, ""),
                            tvCardNumber.getText().toString(),
                            etName.getText().toString(),
                            etSurname.getText().toString(),
                            etEmail.getText().toString(),
                            etPhone.getText().toString()
                    );
                }else{
                    ChangeUIElements(0);
                    Toast.makeText(getContext(), "Нет подключения", Toast.LENGTH_SHORT).show();
                }
            }
        };

        //поток запускаемый при создании экрана (запуск происходит из onStart)
        runnableAboutMeFragment = new Runnable() {
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
                Message msg = handlerAboutMeFragment.obtainMessage();
                msg.setData(bundle);
                handlerAboutMeFragment.sendMessage(msg);
            }
        };

        btChangeUserData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isEmailValid(etEmail.getText().toString())){
                    //убираем клавиатуру после нажатия на кнопку
                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }

                    threadAboutMeFragment = new Thread(runnableAboutMeFragment);
                    threadAboutMeFragment.setDaemon(true);
                    threadAboutMeFragment.start();
                }else {
                    etEmail.setFocusableInTouchMode(true);
                    etEmail.setFocusable(true);
                    etEmail.requestFocus();
                    Toast.makeText(getContext(), "Введите почту!", Toast.LENGTH_SHORT).show();
                }


            }
        });

        return v;
    }

    @Override
    public  void onStart() {
        super.onStart();
        if (getActivity() instanceof InterfaceChangeTitle){
            InterfaceChangeTitle listernerChangeTitle = (InterfaceChangeTitle) getActivity();
            listernerChangeTitle.changeTitle(R.string.title_AboutMe_Fragment, R.string.title_AboutMe_Fragment);
        }

    }

    @Override
    public void onResume() {
        super.onResume();

        pbAboutMe.setVisibility(View.INVISIBLE);
        tvCardNumber.setText(mSettings.getString(APP_PREFERENCES_CARDNUMBER, ""));
        etName.setText(mSettings.getString(APP_PREFERENCES_USERNAME, ""));
        etSurname.setText(mSettings.getString(APP_PREFERENCES_USERSURNAME, ""));
        etEmail.setText(mSettings.getString(APP_PREFERENCES_EMAIL, ""));
        etPhone.setText(mSettings.getString(APP_PREFERENCES_PHONE, ""));
    }

    private void ChangeUIElements(int status){
        if (status == 1){
            pbAboutMe.setVisibility(View.VISIBLE);
            btChangeUserData.setPressed(true);
        }else{
            pbAboutMe.setVisibility(View.INVISIBLE);
            btChangeUserData.setPressed(false);
        }
    }

    private void startAsyncTaskLoader(String objectid, String carNumber, String name,
                                      String surname, String e_mail, String phone) {
        Bundle bundle = new Bundle();
        bundle.putString("objectid", objectid);
        bundle.putString("cardNumber", carNumber);
        bundle.putString("name", name);
        bundle.putString("surname", surname);
        bundle.putString("e_mail", e_mail);
        bundle.putString("phone", phone);
        int LOADERID = 1;
        getLoaderManager().restartLoader(LOADERID, bundle, this).forceLoad();
    }


    @Override
    public Loader<Boolean> onCreateLoader(int id, Bundle args) {
        AboutMeLoader loader;
        loader = new AboutMeLoader(getContext(), args);
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Boolean> loader, Boolean data) {
        if (data){
            SharedPreferences.Editor editor = mSettings.edit();
            editor.putString(APP_PREFERENCES_USERNAME, etName.getText().toString());
            editor.putString(APP_PREFERENCES_USERSURNAME, etSurname.getText().toString());
            editor.putString(APP_PREFERENCES_EMAIL, etEmail.getText().toString());
            editor.putString(APP_PREFERENCES_PHONE, etPhone.getText().toString());
            editor.apply();
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
