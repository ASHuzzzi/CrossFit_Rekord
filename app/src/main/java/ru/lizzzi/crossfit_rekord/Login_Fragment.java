package ru.lizzzi.crossfit_rekord;


import android.content.Context;
import android.content.SharedPreferences;
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

import ru.profit_group.scorocode_sdk.Callbacks.CallbackRegisterUser;
import ru.profit_group.scorocode_sdk.scorocode_objects.User;


public class Login_Fragment extends Fragment {
    /*
    public static final String APPLICATION_ID = "24accf90596a4630a107e14d03a6a3a7";
    public static final String MASTER_KEY = "aee8341a0a22449ebd6a707702689c4e";
    public static final String CLIENT_KEY = "f539a69f0d5940a38e0ca0e83a394d00";
    public static final String FILE_KEY = "c785108f61304a2680a53e1a44ae15b2";
    private static final String MESSAGE_KEY = "e812ec1547b84b62bc9a5c145d442f77";
    private static final String SCRIPT_KEY = "6920f997815244f2bc77949974e4b215";
    private static final String WEBSOCKET_KEY = "6920f997815244f2bc77949974e4b215";
*/

    public static final String APP_PREFERENCES = "audata";
    public static final String APP_PREFERENCES_USERNAME = "Username";
    public static final String APP_PREFERENCES_EMAIL = "Email";
    public static final String APP_PREFERENCES_PASSWORD = "Password";
    SharedPreferences mSettings;


    public Login_Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_login, container, false);
        Button register = (Button) v.findViewById(R.id.btn_register);
        final EditText etusername = (EditText) v.findViewById(R.id.username);
        final EditText etpassword = (EditText) v.findViewById(R.id.password);
        final EditText etemail = (EditText)v.findViewById(R.id.email);

        getContext();
        mSettings = getContext().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = mSettings.edit();


        //ScorocodeSdk.initWith(APPLICATION_ID, CLIENT_KEY, MASTER_KEY, FILE_KEY, MESSAGE_KEY, SCRIPT_KEY, WEBSOCKET_KEY);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String susername = etusername.getText().toString();
                final String spassword = etpassword.getText().toString();
                final String semail = etemail.getText().toString();

                new User().register(susername, semail, spassword, new CallbackRegisterUser() {
                    @Override
                    public void onRegisterSucceed() {


                        editor.putString(APP_PREFERENCES_USERNAME, susername);
                        editor.putString(APP_PREFERENCES_EMAIL, semail);
                        editor.putString(APP_PREFERENCES_PASSWORD, spassword);
                        editor.apply();

                        Toast.makeText(getContext(), "Новый пользователь зарегистрирован", Toast.LENGTH_SHORT).show();
                        Fragment fragment = null;
                        Class fragmentClass;
                        fragmentClass = RecordForTraining_Fragment.class;
                        try {
                            fragment = (Fragment) fragmentClass.newInstance();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        FragmentManager fragmentManager = getFragmentManager();
                        fragmentManager.popBackStack();
                        FragmentTransaction ft = fragmentManager.beginTransaction();
                        ft.replace(R.id.container, fragment);
                        ft.addToBackStack(null);
                        ft.commit();

                    }

                    @Override
                    public void onRegisterFailed(String errorCode, String errorMessage) {
                        Toast.makeText(getContext(), "Ошибка", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

        return v;
    }

}
