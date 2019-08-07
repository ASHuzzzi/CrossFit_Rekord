package ru.lizzzi.crossfit_rekord.fragments;

import android.app.Activity;
import android.app.ActivityManager;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ru.lizzzi.crossfit_rekord.R;
import ru.lizzzi.crossfit_rekord.interfaces.ChangeTitle;
import ru.lizzzi.crossfit_rekord.interfaces.ChangeToggleStatus;
import ru.lizzzi.crossfit_rekord.model.LoginViewModel;
import ru.lizzzi.crossfit_rekord.services.LoadNotificationsService;


//* Created by basso on 07.03.2018.

public class LoginFragment extends Fragment {

    private Button buttonLogin;
    private ProgressBar progressBar;
    private EditText editTextUserEmail;
    private EditText editTextUserPassword;

    private boolean loading = true;
    private boolean wait = false;
    private LoginViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        viewModel = ViewModelProviders.of(LoginFragment.this).get(LoginViewModel.class);

        editTextUserEmail = view.findViewById(R.id.editTextEmail);
        editTextUserPassword = view.findViewById(R.id.editTextPassword);
        progressBar = view.findViewById(R.id.progressbar);

        buttonLogin = view.findViewById(R.id.buttonLogin);
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
                    showToast("Введите почту!");
                    return;
                }

                if (editTextUserPassword.getText().length() == 0) {
                    editTextUserPassword.setFocusableInTouchMode(true);
                    editTextUserPassword.setFocusable(true);
                    editTextUserPassword.requestFocus();
                    showToast("Введите пароль");
                    return;
                }

                //убираем клавиатуру после нажатия на кнопку
                InputMethodManager inputMethodManager = (InputMethodManager) getContext()
                        .getSystemService(Activity.INPUT_METHOD_SERVICE);
                if (inputMethodManager != null) {
                    inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }

                uiLoadingState(loading);
                if (viewModel.checkNetwork()) {
                    LiveData<Boolean>  liveData = viewModel.getLogin(
                            editTextUserEmail.getText().toString(),
                            editTextUserPassword.getText().toString());
                    liveData.observe(LoginFragment.this, new Observer<Boolean>() {
                        @Override
                        public void onChanged(Boolean loggedIn) {
                            if (loggedIn) {
                                startService();
                                ChangeToggleStatus changeToggleStatus =
                                        (ChangeToggleStatus) getActivity();
                                changeToggleStatus.changeToggleStatus(true);
                                openNewFragment(StartScreenFragment.class);
                            } else {
                                uiLoadingState(wait);
                                showToast("Неверный логин или пароль!");
                            }
                        }
                    });
                } else {
                    uiLoadingState(wait);
                    showToast("Нет подключения");
                }
            }
        });

        TextView textContacts = view.findViewById(R.id.textContacts);
        textContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openNewFragment(ContactsFragment.class);
            }
        });

        Button buttonRegistration = view.findViewById(R.id.buttonRegistration);
        buttonRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openNewFragment(RegistryFragment.class);
            }
        });

        TextView textRecoveryPassword = view.findViewById(R.id.textRecoveryPassword);
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

    private void uiLoadingState(boolean loading) {
        progressBar.setVisibility((loading)
                ? View.VISIBLE
                : View.INVISIBLE);
        buttonLogin.setPressed(loading);
    }

    private void startService() {
        if (!isServiceRunning()) {

            // Создаем Intent для вызова сервиса,
            // кладем туда параметр времени и код задачи
            int LOAD_NOTIFICATION = 1;
            String PARAM_TIME = "time";
            String PARAM_TASK = "task";
            Intent intent = new Intent(getContext(), LoadNotificationsService.class)
                    .putExtra(PARAM_TIME, 7)
                    .putExtra(PARAM_TASK, LOAD_NOTIFICATION);
            // стартуем сервис
            getActivity().startService(intent);
        }
    }

    private boolean isServiceRunning() {
        ActivityManager manager = (ActivityManager) Objects.requireNonNull(getActivity())
                .getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            for (ActivityManager.RunningServiceInfo service
                    : Objects.requireNonNull(manager).getRunningServices(Integer.MAX_VALUE)) {
                if (LoadNotificationsService.class.getName().equals(service.service.getClassName())) {
                    return true;
                }
            }
        }
        return false;
    }

    private void openNewFragment(Class fragmentClass) {
        try {
            Fragment fragment = (Fragment) fragmentClass.newInstance();
            FragmentManager fragmentManager = getFragmentManager();
            if (fragmentClass == StartScreenFragment.class && fragmentManager != null) {
                for (int i = 0; i < (fragmentManager.getBackStackEntryCount()); i++) {
                    fragmentManager.popBackStack();
                }
            }
            if (fragmentManager != null) {
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

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static boolean isEmailCorrect(String userEmail) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(userEmail);
        return !matcher.matches();
    }

    private void showToast(String toastText) {
        Toast.makeText(getContext(), toastText, Toast.LENGTH_SHORT).show();
    }
}