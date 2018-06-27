package ru.lizzzi.crossfit_rekord.fragments;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;

import ru.lizzzi.crossfit_rekord.R;


//* Created by basso on 07.03.2018.

public class LoginFragment extends Fragment {

    private NetworkCheck NetworkCheck; //переменная для проврки сети

    public static final String APP_PREFERENCES = "audata";
    public static final String APP_PREFERENCES_OBJECTID = "ObjectId";
    public static final String APP_PREFERENCES_USERNAME = "Username";
    public static final String APP_PREFERENCES_EMAIL = "Email";
    public static final String APP_PREFERENCES_CARDNUMBER = "cardNumber";
    SharedPreferences mSettings;

    private Button btnComeIn;
    private ProgressBar pbLogin;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_login, container, false);
        final EditText tvCardNumber = v.findViewById(R.id.editText4);
        final EditText tvPassword = v.findViewById(R.id.editText5);
        btnComeIn = v.findViewById(R.id.button2);
        Button btnRegisration = v.findViewById(R.id.button4);
        pbLogin = v.findViewById(R.id.pbLogin);

        getContext();
        mSettings = getContext().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = mSettings.edit();

        btnComeIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tvCardNumber.getText().length() != 13 ){
                    tvCardNumber.setFocusable(true);
                    Toast.makeText(getContext(), "Номер карты не корректный", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (tvPassword.getText().length() != 13 ){
                    tvPassword.setFocusable(true);
                    Toast.makeText(getContext(), "Введите пароль", Toast.LENGTH_SHORT).show();
                    return;
                }

                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }

                NetworkCheck = new NetworkCheck(getContext());
                ChangeUIElements(1);
                if (NetworkCheck.checkInternet()){

                    Backendless.UserService.login(tvCardNumber.getText().toString() , tvPassword.getText().toString(), new AsyncCallback<BackendlessUser>() {

                        @Override
                        public void handleResponse(BackendlessUser response) {
                            BackendlessUser user = Backendless.UserService.CurrentUser();

                            editor.putString(APP_PREFERENCES_OBJECTID, user.getObjectId());
                            //editor.putString(APP_PREFERENCES_EMAIL, user.getEmail());
                            editor.putString(APP_PREFERENCES_CARDNUMBER, String.valueOf(user.getProperty("cardNumber")));
                            editor.putString(APP_PREFERENCES_USERNAME, String.valueOf(user.getProperty("name")));
                            editor.putString(APP_PREFERENCES_USERNAME, String.valueOf(user.getProperty("suname")));
                            editor.apply();

                            TransactionFragment(RecordForTrainingSelectFragment.class);
                        }

                        @Override
                        public void handleFault(BackendlessFault fault) {
                            ChangeUIElements(0);
                            Toast.makeText(getContext(), "Авторизация не прошла.", Toast.LENGTH_SHORT).show();
                        }
                    });
                    Backendless.UserService.CurrentUser();
                }else {
                    ChangeUIElements(0);
                    Toast.makeText(getContext(), "Нет подключения", Toast.LENGTH_SHORT);
                }

            }
        });

        btnRegisration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TransactionFragment(RegistryFragment.class);
            }
        });
        return  v;
    }

    private void TransactionFragment(Class fragmentClass) {

        Fragment fragment = null;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.container, fragment);
        ft.addToBackStack(null);
        ft.commit();

    }

    private void ChangeUIElements(int status){
        if (status == 1){
            pbLogin.setVisibility(View.VISIBLE);
            btnComeIn.setPressed(true);
        }else{
            pbLogin.setVisibility(View.INVISIBLE);
            btnComeIn.setPressed(false);
        }
    }
}
