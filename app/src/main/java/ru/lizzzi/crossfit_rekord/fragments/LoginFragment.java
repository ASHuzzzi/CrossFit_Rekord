package ru.lizzzi.crossfit_rekord.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;

import android.content.Context;
import android.content.Intent;
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
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ru.lizzzi.crossfit_rekord.R;
import ru.lizzzi.crossfit_rekord.inspectionСlasses.Network;
import ru.lizzzi.crossfit_rekord.interfaces.InterfaceChangeTitle;
import ru.lizzzi.crossfit_rekord.interfaces.InterfaceChangeToggleStatus;
import ru.lizzzi.crossfit_rekord.loaders.LoginLoader;
import ru.lizzzi.crossfit_rekord.services.LoadNotificationsService;


//* Created by basso on 07.03.2018.

public class LoginFragment extends Fragment implements LoaderManager.LoaderCallbacks<Boolean> {

    private Button buttonLogin;
    private ProgressBar progressBar;
    private EditText editTextEmail;
    private EditText editTextPassword;

    private Handler handlerLoginFragment;
    private Thread threadLoginFragment;
    private Runnable runnableLoginFragment;

    private int openFragment = 1;

    @SuppressLint("HandlerLeak")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_login, container, false);
        editTextEmail = v.findViewById(R.id.editText4);
        editTextPassword = v.findViewById(R.id.editText5);
        buttonLogin = v.findViewById(R.id.button2);
        progressBar = v.findViewById(R.id.pbLogin);
        Button buttonContacts = v.findViewById(R.id.btContacts);
        Button buttonRegisration = v.findViewById(R.id.btnRegisration);
        TextView textRecoveryPassword = v.findViewById(R.id.tvRecPas);

        //хэндлер для потока runnableOpenFragment
        handlerLoginFragment = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == openFragment){
                    TransactionFragment(StartScreenFragment.class);
                }else {
                    Bundle bundle = msg.getData();
                    boolean checkDone = bundle.getBoolean("result");
                    if (checkDone) {
                        startAsyncTaskLoader(
                                editTextEmail.getText().toString(),
                                editTextPassword.getText().toString());
                    } else {
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
                Network network = new Network(getContext());
                Bundle bundle = new Bundle();
                boolean checkDone = network.checkConnection();
                bundle.putBoolean("result", checkDone);
                Message msg = handlerLoginFragment.obtainMessage();
                msg.setData(bundle);
                handlerLoginFragment.sendMessage(msg);
            }
        };

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String UserEmail = editTextEmail.getText().toString();
                if (UserEmail.endsWith(" ")) {
                    UserEmail = UserEmail.substring(0, UserEmail.length() - 1);
                    editTextEmail.setText(UserEmail);
                }

                if (editTextEmail.getText().length() == 0 || isEmailValid(editTextEmail.getText().toString())) {
                    editTextEmail.setFocusableInTouchMode(true);
                    editTextEmail.setFocusable(true);
                    editTextEmail.requestFocus();
                    Toast.makeText(getContext(), "Введите почту!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (editTextPassword.getText().length()== 0){
                    editTextPassword.setFocusableInTouchMode(true);
                    editTextPassword.setFocusable(true);
                    editTextPassword.requestFocus();
                    Toast.makeText(getContext(), "Введите пароль", Toast.LENGTH_SHORT).show();
                    return;
                }

                //убираем клавиатуру после нажатия на кнопку
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }

                ChangeUIElements(1);
                threadLoginFragment = new Thread(runnableLoginFragment);
                threadLoginFragment.setDaemon(true);
                threadLoginFragment.start();

            }
        });

        buttonContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TransactionFragment(ContactsFragment.class);
            }
        });

        buttonRegisration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TransactionFragment(RegistryFragment.class);
            }
        });

        textRecoveryPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TransactionFragment(PasswordRecoveryFragment.class);
            }
        });

        return  v;
    }

    @Override
    public  void onStart() {
        super.onStart();
        if (getActivity() instanceof InterfaceChangeTitle) {
            InterfaceChangeTitle listernerChangeTitle = (InterfaceChangeTitle) getActivity();
            listernerChangeTitle.changeTitle(R.string.title_Login_Fragment, R.string.title_Login_Fragment);
        }
    }

    private void ChangeUIElements(int status) {
        if (status == 1) {
            progressBar.setVisibility(View.VISIBLE);
            buttonLogin.setPressed(true);
        } else {
            progressBar.setVisibility(View.INVISIBLE);
            buttonLogin.setPressed(false);
        }
    }

    private void startAsyncTaskLoader(String stEmail, String stPassword) {
        Bundle bundle = new Bundle();
        bundle.putString("e_mail" , stEmail);
        bundle.putString("password" , stPassword);
        int LOADER_ID = 1;
        getLoaderManager().initLoader(LOADER_ID, bundle,this).forceLoad();
    }

    @NonNull
    @Override
    public Loader<Boolean> onCreateLoader(int id, Bundle args) {
        return new LoginLoader(getContext(), args);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Boolean> loader, Boolean data) {
        if (data) {
            startService();
            InterfaceChangeToggleStatus interfaceChangeToggleStatus = (InterfaceChangeToggleStatus) getActivity();
            interfaceChangeToggleStatus.changeToggleStatus(true);
            handlerLoginFragment.sendEmptyMessage(openFragment);
        }else{
            ChangeUIElements(0);
            Toast.makeText(getContext(), "Неверный логин или пароль!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Boolean> loader) {
    }

    private void startService() {
        if (!isMyServiceRunning(LoadNotificationsService.class)) {
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
        if (fragmentClass == StartScreenFragment.class){
            for(int i = 0; i < (fragmentManager.getBackStackEntryCount()); i++) {
                fragmentManager.popBackStack();
            }
        }
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.setCustomAnimations(R.anim.pull_in_right, R.anim.push_out_left, R.anim.pull_in_left, R.anim.push_out_right);
        ft.replace(R.id.container, fragment);
        ft.addToBackStack(null);
        ft.commit();
    }

    private static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return !matcher.matches();
    }
}