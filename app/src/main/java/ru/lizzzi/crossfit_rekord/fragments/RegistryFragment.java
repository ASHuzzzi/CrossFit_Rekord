package ru.lizzzi.crossfit_rekord.fragments;


import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ru.lizzzi.crossfit_rekord.R;
import ru.lizzzi.crossfit_rekord.interfaces.InterfaceChangeTitle;
import ru.lizzzi.crossfit_rekord.interfaces.InterfaceChangeToggleStatus;
import ru.lizzzi.crossfit_rekord.loaders.LoginLoader;
import ru.lizzzi.crossfit_rekord.loaders.RegistryLoader;
import ru.lizzzi.crossfit_rekord.services.LoadNotificationsService;


public class RegistryFragment extends Fragment implements LoaderManager.LoaderCallbacks<Boolean> {

    public static final String APP_PREFERENCES = "audata";
    public static final String APP_PREFERENCES_USERNAME = "Username";
    public static final String APP_PREFERENCES_EMAIL = "Email";
    public static final String APP_PREFERENCES_PASSWORD = "Password";
    public static final String APP_PREFERENCES_OBJECTID = "ObjectId";
    SharedPreferences mSettings;

    private EditText etUserName;
    private EditText etPassword;
    private EditText etEmail;
    private ProgressBar pbRegistry;
    private Button btnRegister;

    private Handler handlerLoginFragment;
    private Thread threadLoginFragment;
    private Runnable runnableLoginFragment;

    private int openFragment = 1;

    private NetworkCheck NetworkCheck; //переменная для проврки сети

    public RegistryFragment() {
        // Required empty public constructor
    }


    @SuppressLint("HandlerLeak")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_registry, container, false);
        etUserName = v.findViewById(R.id.username);
        etPassword = v.findViewById(R.id.password);
        etEmail = v.findViewById(R.id.email);
        pbRegistry = v.findViewById(R.id.pbRegistry);
        btnRegister = v.findViewById(R.id.btnRegister);

        getContext();
        mSettings = getContext().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = mSettings.edit();

        //хэндлер для потока runnableOpenFragment
        handlerLoginFragment = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == openFragment){
                    TransactionFragment(StartScreenFragment.class);
                }else {
                    Bundle bundle = msg.getData();
                    String result_check = bundle.getString("result");
                    if (result_check != null && result_check.equals("true")){
                        startAsyncTaskLoader(etEmail.getText().toString(), etPassword.getText().toString());
                    }else{
                        ChangeUIElements(0);
                        Toast.makeText(getContext(), "Нет подключения", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        };

        //поток запускаемый при создании экрана (запуск происходит из onStart)
        runnableLoginFragment = new Runnable() {
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
                Message msg = handlerLoginFragment.obtainMessage();
                msg.setData(bundle);
                handlerLoginFragment.sendMessage(msg);
            }
        };

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (checkInternet()){
                    final String stUserName = etUserName.getText().toString();
                    final String stPassword = etPassword.getText().toString();
                    final String stEmail = etEmail.getText().toString();

                    BackendlessUser user = new BackendlessUser();
                    user.setEmail( stEmail );
                    user.setPassword( stPassword );
                    user.setProperty( "name", stUserName);


                    Backendless.UserService.register(user, new AsyncCallback<BackendlessUser>() {
                        @Override
                        public void handleResponse(BackendlessUser response) {

                            editor.putString(APP_PREFERENCES_USERNAME, stUserName);
                            editor.putString(APP_PREFERENCES_EMAIL, stEmail);
                            editor.putString(APP_PREFERENCES_PASSWORD, stPassword);
                            editor.putString(APP_PREFERENCES_OBJECTID, response.getObjectId());
                            editor.apply();
                            Toast.makeText(getContext(), "Новый пользователь зарегистрирован", Toast.LENGTH_SHORT).show();
                            Fragment fragment = null;
                            Class fragmentClass;
                            fragmentClass = RecordForTrainingSelectFragment.class;
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

    @Override
    public  void onStart() {
        super.onStart();
        if (getActivity() instanceof InterfaceChangeTitle){
            InterfaceChangeTitle listernerChangeTitle = (InterfaceChangeTitle) getActivity();
            listernerChangeTitle.changeTitle(R.string.title_Regystry_Fragment, R.string.title_Regystry_Fragment);
        }
    }

    private void ChangeUIElements(int status){
        if (status == 1){
            pbRegistry.setVisibility(View.VISIBLE);
            btnRegister.setPressed(true);
        }else{
            pbRegistry.setVisibility(View.INVISIBLE);
            btnRegister.setPressed(false);
        }
    }



    private void startAsyncTaskLoader(String stEmail, String stPassword) {
        Bundle bundle = new Bundle();
        bundle.putString("e_mail" , stEmail);
        bundle.putString("password" , stPassword);
        int LOADERID = 1;
        getLoaderManager().initLoader(LOADERID, bundle,this).forceLoad();
    }

    @Override
    public Loader<Boolean> onCreateLoader(int id, Bundle args) {
        RegistryLoader loader;
        loader = new RegistryLoader(getContext(), args);
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Boolean> loader, Boolean data) {
        if (data){
            StartService();
            InterfaceChangeToggleStatus interfaceChangeToggleStatus = (InterfaceChangeToggleStatus) getActivity();
            interfaceChangeToggleStatus.changeToggleStatus(true);
            handlerLoginFragment.sendEmptyMessage(openFragment);
        }else{
            ChangeUIElements(0);
            Toast.makeText(getContext(), "Неверный логин или пароль!", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onLoaderReset(Loader<Boolean> loader) {

    }

    private void StartService(){
        if (!isMyServiceRunning(LoadNotificationsService.class)){
            Intent intent;

            // Создаем Intent для вызова сервиса,
            // кладем туда параметр времени и код задачи
            int LOAD_NOTIFICATION = 1;
            String PARAM_TIME = "time";
            String PARAM_TASK = "task";
            intent = new Intent(getContext(), LoadNotificationsService.class).putExtra(PARAM_TIME, 7)
                    .putExtra(PARAM_TASK, LOAD_NOTIFICATION);
            // стартуем сервис
            getActivity().startService(intent);
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            for (ActivityManager.RunningServiceInfo service : Objects.requireNonNull(manager).getRunningServices(Integer.MAX_VALUE)) {
                if (serviceClass.getName().equals(service.service.getClassName())) {
                    return true;
                }
            }
        }
        return false;
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
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.replace(R.id.container, fragment);
        ft.commit();
    }

    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

}