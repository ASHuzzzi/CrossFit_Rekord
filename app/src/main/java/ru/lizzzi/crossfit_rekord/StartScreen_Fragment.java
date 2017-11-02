package ru.lizzzi.crossfit_rekord;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.util.Map;

import ru.profit_group.scorocode_sdk.Callbacks.CallbackLoginUser;
import ru.profit_group.scorocode_sdk.Responses.user.ResponseLogin;
import ru.profit_group.scorocode_sdk.ScorocodeSdk;
import ru.profit_group.scorocode_sdk.scorocode_objects.User;

public class StartScreen_Fragment extends Fragment {

    public static final String APPLICATION_ID = "24accf90596a4630a107e14d03a6a3a7";
    public static final String MASTER_KEY = "aee8341a0a22449ebd6a707702689c4e";
    public static final String CLIENT_KEY = "f539a69f0d5940a38e0ca0e83a394d00";
    public static final String FILE_KEY = "c785108f61304a2680a53e1a44ae15b2";
    private static final String MESSAGE_KEY = "e812ec1547b84b62bc9a5c145d442f77";
    private static final String SCRIPT_KEY = "6920f997815244f2bc77949974e4b215";
    private static final String WEBSOCKET_KEY = "6920f997815244f2bc77949974e4b215";

    public static final String APP_PREFERENCES = "audata";
    public static final String APP_PREFERENCES_USERNAME = "Username";
    public static final String APP_PREFERENCES_EMAIL = "Email";
    public static final String APP_PREFERENCES_PASSWORD = "Password";
    SharedPreferences mSettings;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_start_screen, container, false);

        ScorocodeSdk.initWith(APPLICATION_ID, CLIENT_KEY, MASTER_KEY, FILE_KEY, MESSAGE_KEY, SCRIPT_KEY, WEBSOCKET_KEY);

        mSettings = getContext().getSharedPreferences(APP_PREFERENCES, getContext().MODE_PRIVATE);

        Button subscription = ((Button) v.findViewById(R.id.button_subscription));
        Button schedule = ((Button) v.findViewById(R.id.button_schedule));
        Button record_training = ((Button) v.findViewById(R.id.button_record_training));
        Button description = (Button) v.findViewById(R.id.button_definition);
        final Button contacts = ((Button) v.findViewById(R.id.button_contacts));

        subscription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TransactionFragment(Price_Fragment.class);

            }
        });

        schedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TransactionFragment(Table_Fragment.class);
            }
        });

        record_training.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Map<String, ?> allPreferences = mSettings.getAll();
                boolean containtsusername = mSettings.contains(APP_PREFERENCES_USERNAME);
                boolean containtemail = mSettings.contains(APP_PREFERENCES_EMAIL);
                boolean containtpassword = mSettings.contains(APP_PREFERENCES_PASSWORD);
                if (containtsusername == true && containtpassword == true && containtemail == true) {

                    User user = new User();
                    user.login(mSettings.getString(APP_PREFERENCES_EMAIL, ""), mSettings.getString(APP_PREFERENCES_PASSWORD, ""), new CallbackLoginUser() {
                        @Override
                        public void onLoginSucceed(ResponseLogin responseLogin) {
                            //Toast.makeText(getContext(), "Авторизация успешно", Toast.LENGTH_SHORT).show();
                            TransactionFragment(RecordForTraining_Fragment.class);
                        }

                        @Override
                        public void onLoginFailed(String errorCode, String errorMessage) {
                            Toast.makeText(getContext(), "Авторизация не прошла. Попробуйте снова.", Toast.LENGTH_SHORT).show();
                        }
                    });

                }else {
                    TransactionFragment(Login_Fragment.class);
                }

            }
        });

        contacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        description.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TransactionFragment(Definition_Fragment.class);
            }
        });

        return v;
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
}