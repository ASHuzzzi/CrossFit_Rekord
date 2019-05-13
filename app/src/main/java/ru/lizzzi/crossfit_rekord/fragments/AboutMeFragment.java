package ru.lizzzi.crossfit_rekord.fragments;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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

import ru.lizzzi.crossfit_rekord.R;
import ru.lizzzi.crossfit_rekord.inspectionСlasses.Network;
import ru.lizzzi.crossfit_rekord.interfaces.ChangeTitle;
import ru.lizzzi.crossfit_rekord.loaders.AboutMeLoader;

/**
 * Created by basso on 07.03.2018.
 */

public class AboutMeFragment extends Fragment implements LoaderManager.LoaderCallbacks<Boolean> {

    private static final String APP_PREFERENCES = "audata";
    private static final String APP_PREFERENCES_EMAIL = "Email";
    private static final String APP_PREFERENCES_USERNAME = "Username";
    private static final String APP_PREFERENCES_USERSURNAME = "Usersurname";
    private static final String APP_PREFERENCES_CARDNUMBER = "cardNumber";
    private static final String APP_PREFERENCES_PHONE = "Phone";
    private static final String APP_PREFERENCES_PASSWORD = "Password";
    private SharedPreferences mSettings;

    private TextView tvCardNumber;
    private EditText etName;
    private EditText etSurname;
    private EditText etPhone;
    private Button btChangeUserData;
    private ProgressBar pbAboutMe;

    private Handler handlerAboutMeFragment;
    private Thread threadAboutMeFragment;
    private Runnable runnableAboutMeFragment;

    @SuppressLint("HandlerLeak")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_aboutme, container, false);

        mSettings = getContext().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        tvCardNumber = v.findViewById(R.id.tvCardNumber);
        TextView tvOpenChangeEmail = v.findViewById(R.id.tvOpenChangeEmail);
        TextView tvOpenChangePas = v.findViewById(R.id.tvOpenChangePas);
        etName = v.findViewById(R.id.etName);
        etSurname = v.findViewById(R.id.etSurname);
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
                            mSettings.getString(APP_PREFERENCES_EMAIL, ""),
                            mSettings.getString(APP_PREFERENCES_PASSWORD, ""),
                            etName.getText().toString(),
                            etSurname.getText().toString(),
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

                Network network = new Network(getContext());
                Bundle bundle = new Bundle();
                boolean checkDone = network.checkConnection();
                if (checkDone){
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

                String stCheckSpace = etName.getText().toString();
                if(stCheckSpace.endsWith(" ")){
                    stCheckSpace = stCheckSpace.substring(0, stCheckSpace.length() - 1);
                    etName.setText(stCheckSpace);
                }

                stCheckSpace = etSurname.getText().toString();
                if(stCheckSpace.endsWith(" ")){
                    stCheckSpace = stCheckSpace.substring(0, stCheckSpace.length() - 1);
                    etSurname.setText(stCheckSpace);
                }

                stCheckSpace = etPhone.getText().toString();
                if(stCheckSpace.endsWith(" ")){
                    stCheckSpace = stCheckSpace.substring(0, stCheckSpace.length() - 1);
                    etPhone.setText(stCheckSpace);
                }



                //убираем клавиатуру после нажатия на кнопку
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }

                threadAboutMeFragment = new Thread(runnableAboutMeFragment);
                threadAboutMeFragment.setDaemon(true);
                threadAboutMeFragment.start();


            }
        });

        tvOpenChangeEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment =  new ChangeEmailFragment();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction ft = fragmentManager.beginTransaction();
                ft.setCustomAnimations(R.anim.pull_in_right, R.anim.push_out_left, R.anim.pull_in_left, R.anim.push_out_right);
                ft.replace(R.id.container, fragment);
                ft.addToBackStack(null);
                ft.commit();
            }
        });

        tvOpenChangePas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment =  new ChangePasswordFragment();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction ft = fragmentManager.beginTransaction();
                ft.setCustomAnimations(R.anim.pull_in_right, R.anim.push_out_left, R.anim.pull_in_left, R.anim.push_out_right);
                ft.replace(R.id.container, fragment);
                ft.addToBackStack(null);
                ft.commit();
            }
        });

        return v;
    }

    @Override
    public  void onStart() {
        super.onStart();
        if (getActivity() instanceof ChangeTitle){
            ChangeTitle listernerChangeTitle = (ChangeTitle) getActivity();
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
        etPhone.setText(mSettings.getString(APP_PREFERENCES_PHONE, ""));

        etName.setSelection(etName.getText().length());
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

    private void startAsyncTaskLoader(String stEmail, String carNumber, String name,
                                      String surname, String phone) {
        Bundle bundle = new Bundle();
        bundle.putString("e-mail", stEmail);
        bundle.putString("password", carNumber);
        bundle.putString("name", name);
        bundle.putString("surname", surname);
        bundle.putString("phone", phone);
        int LOADERID = 1;
        getLoaderManager().restartLoader(LOADERID, bundle, this).forceLoad();
    }


    @NonNull
    @Override
    public Loader<Boolean> onCreateLoader(int id, Bundle args) {
        AboutMeLoader loader;
        loader = new AboutMeLoader(getContext(), args);
        return loader;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Boolean> loader, Boolean data) {
        if (data){
            Toast.makeText(getContext(), "Данные обновлены", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(getContext(), "Повторите сохранение", Toast.LENGTH_SHORT).show();
        }
        ChangeUIElements(0);

    }

    @Override
    public void onLoaderReset(@NonNull Loader<Boolean> loader) {

    }
}
