package ru.lizzzi.crossfit_rekord;


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


public class Registry_Fragment extends Fragment {

    public static final String APP_PREFERENCES = "audata";
    public static final String APP_PREFERENCES_USERNAME = "Username";
    public static final String APP_PREFERENCES_EMAIL = "Email";
    public static final String APP_PREFERENCES_PASSWORD = "Password";
    public static final String APP_PREFERENCES_OBJECTID = "ObjectId";
    SharedPreferences mSettings;


    public Registry_Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_registry, container, false);
        Button register = v.findViewById(R.id.btn_register);
        final EditText etusername = v.findViewById(R.id.username);
        final EditText etpassword = v.findViewById(R.id.password);
        final EditText etemail = v.findViewById(R.id.email);

        getContext();
        mSettings = getContext().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = mSettings.edit();

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (checkInternet()){
                    final String susername = etusername.getText().toString();
                    final String spassword = etpassword.getText().toString();
                    final String semail = etemail.getText().toString();

                    BackendlessUser user = new BackendlessUser();
                    user.setEmail( semail );
                    user.setPassword( spassword );
                    user.setProperty( "name", susername);


                    Backendless.UserService.register(user, new AsyncCallback<BackendlessUser>() {
                        @Override
                        public void handleResponse(BackendlessUser response) {

                            editor.putString(APP_PREFERENCES_USERNAME, susername);
                            editor.putString(APP_PREFERENCES_EMAIL, semail);
                            editor.putString(APP_PREFERENCES_PASSWORD, spassword);
                            editor.putString(APP_PREFERENCES_OBJECTID, response.getObjectId());
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
                        public void handleFault(BackendlessFault fault) {

                            Toast.makeText(getContext(), fault.getMessage(), Toast.LENGTH_LONG).show();
                        }

                    });
                }else {
                    Toast.makeText(getContext(), "Нет подключения" , Toast.LENGTH_SHORT).show();
                }

            }
        });

        return v;
    }

    public boolean checkInternet() {

        ConnectivityManager cm = (ConnectivityManager)getActivity().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm != null ? cm.getActiveNetworkInfo() : null;
        // проверка подключения
        return activeNetwork != null && activeNetwork.isConnected();

    }

}