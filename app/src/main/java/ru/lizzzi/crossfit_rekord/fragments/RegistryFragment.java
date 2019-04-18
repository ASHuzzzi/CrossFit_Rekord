package ru.lizzzi.crossfit_rekord.fragments;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ru.lizzzi.crossfit_rekord.R;
import ru.lizzzi.crossfit_rekord.inspectionСlasses.Network;
import ru.lizzzi.crossfit_rekord.interfaces.InterfaceChangeTitle;
import ru.lizzzi.crossfit_rekord.interfaces.InterfaceChangeToggleStatus;
import ru.lizzzi.crossfit_rekord.loaders.RegistryLoader;
import ru.lizzzi.crossfit_rekord.services.LoadNotificationsService;


public class RegistryFragment extends Fragment implements LoaderManager.LoaderCallbacks<Boolean> {

    private EditText etUserName;
    private EditText etPassword;
    private EditText etCheckPassword;
    private EditText etEmail;
    private ProgressBar pbRegistry;
    private Button btnRegister;

    private Handler handlerRegistryFragment;
    private Thread threadRegistryFragment;
    private Runnable runnableRegistryFragment;

    private int openFragment = 1;

    public RegistryFragment() {
        // Required empty public constructor
    }


    @SuppressLint("HandlerLeak")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_registry, container, false);
        etUserName = v.findViewById(R.id.username);
        etPassword = v.findViewById(R.id.password);
        etCheckPassword = v.findViewById(R.id.checkpassword);
        etEmail = v.findViewById(R.id.email);
        pbRegistry = v.findViewById(R.id.pbRegistry);
        btnRegister = v.findViewById(R.id.btnRegister);

        //хэндлер для потока runnableOpenFragment
        handlerRegistryFragment = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == openFragment){
                    TransactionFragment();
                }else {
                    Bundle bundle = msg.getData();
                    String result_check = bundle.getString("result");
                    if (result_check != null && result_check.equals("true")){
                        startAsyncTaskLoader(
                                etUserName.getText().toString(),
                                etEmail.getText().toString(),
                                etPassword.getText().toString());
                    }else{
                        ChangeUIElements(0);
                        Toast.makeText(getContext(), "Нет подключения", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        };

        //поток запускаемый при создании экрана (запуск происходит из onStart)
        runnableRegistryFragment = new Runnable() {
            @Override
            public void run() {

                Network network = new Network(getContext());
                Bundle bundle = new Bundle();
                boolean checkDone = network.checkConnection();
                if (checkDone) {
                    bundle.putString("result", String.valueOf(true));

                }else {
                    bundle.putString("result", String.valueOf(false));
                }
                Message msg = handlerRegistryFragment.obtainMessage();
                msg.setData(bundle);
                handlerRegistryFragment.sendMessage(msg);
            }
        };

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String stCheckSpace = etUserName.getText().toString();
                if(stCheckSpace.endsWith(" ")){
                    stCheckSpace = stCheckSpace.substring(0, stCheckSpace.length() - 1);
                    etUserName.setText(stCheckSpace);
                }

                if (etUserName.getText().length()== 0){
                    etUserName.setFocusableInTouchMode(true);
                    etUserName.setFocusable(true);
                    etUserName.requestFocus();
                    Toast.makeText(getContext(), "Введите имя!", Toast.LENGTH_SHORT).show();
                    return;
                }


                stCheckSpace = etEmail.getText().toString();
                if(stCheckSpace.endsWith(" ")){
                    stCheckSpace = stCheckSpace.substring(0, stCheckSpace.length() - 1);
                    etEmail.setText(stCheckSpace);
                }

                if (etEmail.getText().length()== 0 || !isEmailValid(etEmail.getText().toString())){
                    etEmail.setFocusableInTouchMode(true);
                    etEmail.setFocusable(true);
                    etEmail.requestFocus();
                    Toast.makeText(getContext(), "Введите почту!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(etPassword.getText().length()== 0 || etCheckPassword.getText().length()==0){
                    if(etPassword.getText().length()== 0){
                        etPassword.setFocusableInTouchMode(true);
                        etPassword.setFocusable(true);
                        etPassword.requestFocus();
                        Toast.makeText(getContext(), "Введите пароль", Toast.LENGTH_SHORT).show();
                    }else {
                        etCheckPassword.setFocusableInTouchMode(true);
                        etCheckPassword.setFocusable(true);
                        etCheckPassword.requestFocus();
                        Toast.makeText(getContext(), "Повторите пароль", Toast.LENGTH_SHORT).show();
                    }
                    return;
                }

                if(!etPassword.getText().toString().equals(etCheckPassword.getText().toString())){
                    Toast.makeText(getContext(), "Пароли не совпадают!", Toast.LENGTH_SHORT).show();
                    return;
                }

                //убираем клавиатуру после нажатия на кнопку
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }

                ChangeUIElements(1);
                threadRegistryFragment = new Thread(runnableRegistryFragment);
                threadRegistryFragment.setDaemon(true);
                threadRegistryFragment.start();
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



    private void startAsyncTaskLoader(String stName,  String stEmail, String stPassword) {
        Bundle bundle = new Bundle();
        bundle.putString("userName", stName);
        bundle.putString("e_mail" , stEmail);
        bundle.putString("password" , stPassword);
        int LOADERID = 1;
        getLoaderManager().initLoader(LOADERID, bundle,this).forceLoad();
    }

    @NonNull
    @Override
    public Loader<Boolean> onCreateLoader(int id, Bundle args) {
        RegistryLoader loader;
        loader = new RegistryLoader(getContext(), args);
        return loader;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Boolean> loader, Boolean data) {
        if (data){
            StartService();
            InterfaceChangeToggleStatus interfaceChangeToggleStatus = (InterfaceChangeToggleStatus) getActivity();
            interfaceChangeToggleStatus.changeToggleStatus(true);
            handlerRegistryFragment.sendEmptyMessage(openFragment);
        }else{
            ChangeUIElements(0);
            Toast.makeText(getContext(), "Неверный логин или пароль!", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onLoaderReset(@NonNull Loader<Boolean> loader) {

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

    private void TransactionFragment() {

        Fragment fragment = null;
        try {
            fragment = StartScreenFragment.class.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        FragmentManager fragmentManager = getFragmentManager();
        for(int i = 0; i < (fragmentManager.getBackStackEntryCount()-1); i++) {
            fragmentManager.popBackStack();
        }
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.setCustomAnimations(R.anim.pull_in_right, R.anim.push_out_left, R.anim.pull_in_left, R.anim.push_out_right);
        ft.replace(R.id.container, fragment);
        ft.commit();
    }

    private static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

}