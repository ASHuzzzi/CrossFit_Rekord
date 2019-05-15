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
import ru.lizzzi.crossfit_rekord.interfaces.ChangeTitle;
import ru.lizzzi.crossfit_rekord.interfaces.ChangeToggleStatus;
import ru.lizzzi.crossfit_rekord.loaders.LoginLoader;
import ru.lizzzi.crossfit_rekord.services.LoadNotificationsService;


//* Created by basso on 07.03.2018.

public class LoginFragment extends Fragment implements LoaderManager.LoaderCallbacks<Boolean> {

    private Button buttonLogin;
    private ProgressBar progressBar;
    private EditText editTextUserEmail;
    private EditText editTextUserPassword;

    private Handler handlerLogin;
    private Thread threadLogin;
    private Runnable runnableLogin;

    private final static int LOGIN_IS_DONE = 1;
    private final static int WAIT_STATE = 0;
    private final static int LOAD_STATE = 1;


    @SuppressLint("HandlerLeak")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        editTextUserEmail = view.findViewById(R.id.editTextEmail);
        editTextUserPassword = view.findViewById(R.id.editTextPassword);
        buttonLogin = view.findViewById(R.id.buttonLogin);
        progressBar = view.findViewById(R.id.progressbar);
        TextView textContacts = view.findViewById(R.id.textContacts);
        Button buttonRegistration = view.findViewById(R.id.buttonRegistration);
        TextView textRecoveryPassword = view.findViewById(R.id.textRecoveryPassword);

        //хэндлер для потока runnableOpenFragment
        handlerLogin = new Handler() {
            @Override
            public void handleMessage(Message message) {
                if (message.what == LOGIN_IS_DONE) {
                    openNewFragment(StartScreenFragment.class);
                } else {
                    Bundle bundle = message.getData();
                    boolean checkDone = bundle.getBoolean("result");
                    if (checkDone) {
                        startLoginLoader(
                                editTextUserEmail.getText().toString(),
                                editTextUserPassword.getText().toString());
                    } else {
                        changeUIOnState(WAIT_STATE);
                        Toast.makeText(getContext(), "Нет подключения", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        };

        //поток запускаемый при создании экрана (запуск происходит из onStart)
        runnableLogin = new Runnable() {
            @Override
            public void run() {
                Network network = new Network(getContext());
                boolean checkDone = network.checkConnection();
                Bundle bundle = new Bundle();
                bundle.putBoolean("result", checkDone);
                Message msg = handlerLogin.obtainMessage();
                msg.setData(bundle);
                handlerLogin.sendMessage(msg);
            }
        };

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userEmail = editTextUserEmail.getText().toString();
                if (userEmail.endsWith(" ")) {
                    userEmail = userEmail.substring(0, userEmail.length() - 1);
                    editTextUserEmail.setText(userEmail);
                }

                boolean userEmailIsCorrect = isEmailCorrect(userEmail);
                if (editTextUserEmail.getText().length() == 0 || userEmailIsCorrect) {
                    editTextUserEmail.setFocusableInTouchMode(true);
                    editTextUserEmail.setFocusable(true);
                    editTextUserEmail.requestFocus();
                    Toast.makeText(getContext(), "Введите почту!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (editTextUserPassword.getText().length() == 0) {
                    editTextUserPassword.setFocusableInTouchMode(true);
                    editTextUserPassword.setFocusable(true);
                    editTextUserPassword.requestFocus();
                    Toast.makeText(getContext(), "Введите пароль", Toast.LENGTH_SHORT).show();
                    return;
                }

                //убираем клавиатуру после нажатия на кнопку
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }

                changeUIOnState(LOAD_STATE);
                threadLogin = new Thread(runnableLogin);
                threadLogin.setDaemon(true);
                threadLogin.start();

            }
        });

        textContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openNewFragment(ContactsFragment.class);
            }
        });

        buttonRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openNewFragment(RegistryFragment.class);
            }
        });

        textRecoveryPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openNewFragment(PasswordRecoveryFragment.class);
            }
        });
        return view;
    }

    @Override
    public  void onStart() {
        super.onStart();
        if (getActivity() instanceof ChangeTitle) {
            ChangeTitle listerner = (ChangeTitle) getActivity();
            listerner.changeTitle(R.string.title_Login_Fragment, R.string.title_Login_Fragment);
        }
    }

    private void changeUIOnState(int state) {
        if (state == LOAD_STATE) {
            progressBar.setVisibility(View.VISIBLE);
            buttonLogin.setPressed(true);
        } else {
            progressBar.setVisibility(View.INVISIBLE);
            buttonLogin.setPressed(false);
        }
    }

    private void startLoginLoader(String userEmail, String userPassword) {
        Bundle bundle = new Bundle();
        bundle.putString("e_mail" , userEmail);
        bundle.putString("password" , userPassword);
        int LOGIN_LOADER = 1;
        getLoaderManager().initLoader(LOGIN_LOADER, bundle,this).forceLoad();
    }

    @NonNull
    @Override
    public Loader<Boolean> onCreateLoader(int id, Bundle bundle) {
        return new LoginLoader(getContext(), bundle);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Boolean> loader, Boolean userIsLoggined) {
        if (userIsLoggined) {
            startService();
            ChangeToggleStatus changeToggleStatus = (ChangeToggleStatus) getActivity();
            changeToggleStatus.changeToggleStatus(true);
            handlerLogin.sendEmptyMessage(LOGIN_IS_DONE);
        } else {
            changeUIOnState(WAIT_STATE);
            Toast.makeText(getContext(), "Неверный логин или пароль!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Boolean> loader) {
    }

    private void startService() {
        if (!isServiceRunning(LoadNotificationsService.class)) {
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

    private boolean isServiceRunning(Class<?> serviceClass) {
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

    private void openNewFragment(Class fragmentClass) {
        Fragment fragment = null;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        FragmentManager fragmentManager = getFragmentManager();
        if (fragmentClass == StartScreenFragment.class) {
            for (int i = 0; i < (fragmentManager.getBackStackEntryCount()); i++) {
                fragmentManager.popBackStack();
            }
        }
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(
                R.anim.pull_in_right,
                R.anim.push_out_left,
                R.anim.pull_in_left,
                R.anim.push_out_right);
        fragmentTransaction.replace(R.id.container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private static boolean isEmailCorrect(String userEmail) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(userEmail);
        return !matcher.matches();
    }
}