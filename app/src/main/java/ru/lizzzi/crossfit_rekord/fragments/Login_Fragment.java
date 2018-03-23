package ru.lizzzi.crossfit_rekord.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;

import ru.lizzzi.crossfit_rekord.R;


//* Created by basso on 07.03.2018.

public class Login_Fragment extends Fragment {

    public static final String APP_PREFERENCES = "audata";
    public static final String APP_PREFERENCES_OBJECTID = "ObjectId";
    public static final String APP_PREFERENCES_USERNAME = "Username";
    public static final String APP_PREFERENCES_EMAIL = "Email";
    SharedPreferences mSettings;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_login, container, false);
        final EditText tvEmail = v.findViewById(R.id.editText4);
        final EditText tvPassword = v.findViewById(R.id.editText5);
        Button btnComeIn = v.findViewById(R.id.button2);
        Button btnRegisration = v.findViewById(R.id.button4);

        getContext();
        mSettings = getContext().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = mSettings.edit();

        btnComeIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tvEmail.getText().length() == 0 && !android.util.Patterns.EMAIL_ADDRESS.matcher((CharSequence) tvEmail).matches()){
                    tvEmail.setFocusable(true);
                    Toast.makeText(getContext(), "Email не корректный", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (tvPassword.getText().length() == 0){
                    tvPassword.setFocusable(true);
                    Toast.makeText(getContext(), "Введите пароль", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (checkInternet()){

                    Backendless.UserService.login(tvEmail.getText().toString() , tvPassword.getText().toString(), new AsyncCallback<BackendlessUser>() {

                        @Override
                        public void handleResponse(BackendlessUser response) {
                            BackendlessUser user = Backendless.UserService.CurrentUser();

                            editor.putString(APP_PREFERENCES_OBJECTID, user.getObjectId());
                            // TODO Сделать получение имени юзера
                            editor.putString(APP_PREFERENCES_EMAIL, user.getEmail());
                            editor.putString(APP_PREFERENCES_USERNAME, String.valueOf(user.getProperty("name")));
                            editor.apply();

                            TransactionFragment(RecordForTraining_Fragment.class);
                        }

                        @Override
                        public void handleFault(BackendlessFault fault) {
                            Toast.makeText(getContext(), "Авторизация не прошла.", Toast.LENGTH_SHORT).show();
                        }
                    });
                    Backendless.UserService.CurrentUser();
                }

            }
        });

        btnRegisration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TransactionFragment(Registry_Fragment.class);
            }
        });
        return  v;
    }

    private void TransactionFragment(Class fragmentClass) {

        android.support.v4.app.Fragment fragment = null;
        try {
            fragment = (android.support.v4.app.Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.container, fragment);
        ft.addToBackStack(null);
        ft.commit();

    }

    public boolean checkInternet() {

        ConnectivityManager cm = (ConnectivityManager)getActivity().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm != null ? cm.getActiveNetworkInfo() : null;
        // проверка подключения
        return activeNetwork != null && activeNetwork.isConnected();

    }
}
