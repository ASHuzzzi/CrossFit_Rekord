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
import android.widget.Toast;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ru.lizzzi.crossfit_rekord.R;
import ru.lizzzi.crossfit_rekord.inspectionСlasses.Network;
import ru.lizzzi.crossfit_rekord.interfaces.ChangeTitle;
import ru.lizzzi.crossfit_rekord.interfaces.ChangeToggleStatus;
import ru.lizzzi.crossfit_rekord.loaders.RegistryLoader;
import ru.lizzzi.crossfit_rekord.services.LoadNotificationsService;


public class RegistryFragment extends Fragment implements LoaderManager.LoaderCallbacks<Boolean> {

    private EditText editTextUserName;
    private EditText editTextUserPassword;
    private EditText editTextCheckUserPassword;
    private EditText editTextUserEmail;
    private ProgressBar progressBar;
    private Button buttonRegister;

    private Handler handlerRegistry;
    private Thread threadRegistry;
    private Runnable runnableRegistry;

    private final static int LOGIN_IS_DONE = 1;
    private final static int WAIT_STATE = 0;
    private final static int LOAD_STATE = 1;

    @SuppressLint("HandlerLeak")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_registry, container, false);
        editTextUserName = view.findViewById(R.id.editTextUserName);
        editTextUserPassword = view.findViewById(R.id.EditTextUserPassword);
        editTextCheckUserPassword = view.findViewById(R.id.editTextUserPassword);
        editTextUserEmail = view.findViewById(R.id.editTextUserEmail);
        progressBar = view.findViewById(R.id.progressbar);
        buttonRegister = view.findViewById(R.id.buttonRegisterNewUser);

        //хэндлер для потока runnableOpenFragment
        handlerRegistry = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == LOGIN_IS_DONE) {
                    openStartScreenFragment();
                } else {
                    Bundle bundle = msg.getData();
                    boolean checkDone = bundle.getBoolean("result");
                    if (checkDone) {
                        startRegistryLoader(
                                editTextUserName.getText().toString(),
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
        runnableRegistry = new Runnable() {
            @Override
            public void run() {
                Network network = new Network(getContext());
                Bundle bundle = new Bundle();
                bundle.putBoolean("result", network.checkConnection());
                Message msg = handlerRegistry.obtainMessage();
                msg.setData(bundle);
                handlerRegistry.sendMessage(msg);
            }
        };

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userName = editTextUserName.getText().toString();
                if (userName.endsWith(" ")) {
                    userName = userName.substring(0, userName.length() - 1);
                    editTextUserName.setText(userName);
                }

                if (editTextUserName.getText().length() == 0) {
                    editTextUserName.setFocusableInTouchMode(true);
                    editTextUserName.setFocusable(true);
                    editTextUserName.requestFocus();
                    Toast.makeText(getContext(), "Введите имя!", Toast.LENGTH_SHORT).show();
                    return;
                }

                String userEmail = editTextUserEmail.getText().toString();
                boolean userEmailIsCorrect = isEmailCorrect(userEmail);
                if (editTextUserEmail.getText().length() == 0 || userEmailIsCorrect) {
                    editTextUserEmail.setFocusableInTouchMode(true);
                    editTextUserEmail.setFocusable(true);
                    editTextUserEmail.requestFocus();
                    Toast.makeText(getContext(), "Введите почту!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (editTextUserPassword.getText().length() == 0 || editTextCheckUserPassword.getText().length() == 0) {
                    if (editTextUserPassword.getText().length()== 0) {
                        editTextUserPassword.setFocusableInTouchMode(true);
                        editTextUserPassword.setFocusable(true);
                        editTextUserPassword.requestFocus();
                        Toast.makeText(getContext(), "Введите пароль", Toast.LENGTH_SHORT).show();
                    } else {
                        editTextCheckUserPassword.setFocusableInTouchMode(true);
                        editTextCheckUserPassword.setFocusable(true);
                        editTextCheckUserPassword.requestFocus();
                        Toast.makeText(getContext(), "Повторите пароль", Toast.LENGTH_SHORT).show();
                    }
                    return;
                } else {
                    if (!editTextUserPassword.getText().toString().equals(editTextCheckUserPassword.getText().toString())) {
                        Toast.makeText(getContext(), "Пароли не совпадают!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                //убираем клавиатуру после нажатия на кнопку
                InputMethodManager inputManager = (InputMethodManager) getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
                if (inputManager != null) {
                    inputManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }

                changeUIOnState(LOAD_STATE);
                threadRegistry = new Thread(runnableRegistry);
                threadRegistry.setDaemon(true);
                threadRegistry.start();
            }
        });
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getActivity() instanceof ChangeTitle) {
            ChangeTitle listernerChangeTitle = (ChangeTitle) getActivity();
            listernerChangeTitle.changeTitle(R.string.title_Regystry_Fragment, R.string.title_Regystry_Fragment);
        }
    }

    private void changeUIOnState(int state) {
        if (state == LOAD_STATE) {
            progressBar.setVisibility(View.VISIBLE);
            buttonRegister.setPressed(true);
        } else {
            progressBar.setVisibility(View.INVISIBLE);
            buttonRegister.setPressed(false);
        }
    }

    private void startRegistryLoader(String userName, String userEmail, String userPassword) {
        Bundle bundle = new Bundle();
        bundle.putString("userName", userName);
        bundle.putString("e_mail" , userEmail);
        bundle.putString("password" , userPassword);
        int REGISTRY_LOADER = 1;
        getLoaderManager().initLoader(REGISTRY_LOADER, bundle,this).forceLoad();
    }

    @NonNull
    @Override
    public Loader<Boolean> onCreateLoader(int id, Bundle bundle) {
        return new RegistryLoader(getContext(), bundle);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Boolean> loader, Boolean userIsLoggined) {
        if (userIsLoggined) {
            loadNotification();
            ChangeToggleStatus changeToggleStatus = (ChangeToggleStatus) getActivity();
            changeToggleStatus.changeToggleStatus(true);
            handlerRegistry.sendEmptyMessage(LOGIN_IS_DONE);
        } else {
            changeUIOnState(WAIT_STATE);
            Toast.makeText(getContext(), "Неверный логин или пароль!", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onLoaderReset(@NonNull Loader<Boolean> loader) {
    }

    private void loadNotification() {
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

    private void openStartScreenFragment() {

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
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(
                R.anim.pull_in_right,
                R.anim.push_out_left,
                R.anim.pull_in_left,
                R.anim.push_out_right);
        fragmentTransaction.replace(R.id.container, fragment);
        fragmentTransaction.commit();
    }

    private static boolean isEmailCorrect(String userEmail) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(userEmail);
        return !matcher.matches();
    }

}