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
import android.widget.Toast;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ru.lizzzi.crossfit_rekord.R;
import ru.lizzzi.crossfit_rekord.interfaces.TitleChange;
import ru.lizzzi.crossfit_rekord.interfaces.ToggleStatusChange;
import ru.lizzzi.crossfit_rekord.model.RegistryViewModel;
import ru.lizzzi.crossfit_rekord.services.LoadNotificationsService;

public class RegistryFragment extends Fragment {

    private EditText editTextUserName;
    private EditText editTextUserPassword;
    private EditText editTextCheckUserPassword;
    private EditText editTextUserEmail;
    private ProgressBar progressBar;
    private Button buttonRegister;

    private boolean LOADING = true;
    private boolean WAIT = false;

    private RegistryViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_registry, container, false);
        viewModel =
                ViewModelProviders.of(RegistryFragment.this).get(RegistryViewModel.class);
        editTextUserName = view.findViewById(R.id.editTextUserName);
        editTextUserPassword = view.findViewById(R.id.EditTextUserPassword);
        editTextCheckUserPassword = view.findViewById(R.id.editTextUserPassword);
        editTextUserEmail = view.findViewById(R.id.editTextUserEmail);
        progressBar = view.findViewById(R.id.progressbar);
        buttonRegister = view.findViewById(R.id.buttonRegisterNewUser);

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userName = editTextUserName.getText().toString();
                if (userName.endsWith(" ")) {
                    userName = userName.substring(0, userName.length() - 1);
                    editTextUserName.setText(userName);
                }

                if (editTextUserName.getText().length() == 0) {
                    takeFocusOnView(editTextUserName, "Введите имя!");
                    return;
                }

                boolean userEmailIsCorrect = isEmailCorrect(editTextUserEmail.getText().toString());
                if (editTextUserEmail.getText().length() == 0 || !userEmailIsCorrect) {
                    takeFocusOnView(editTextUserEmail, "Введите почту!");
                    return;
                }

                if (editTextUserPassword.getText().length() == 0) {
                    takeFocusOnView(editTextUserPassword, "Введите пароль");
                    return;
                }

                if (editTextCheckUserPassword.getText().length() == 0) {
                    takeFocusOnView(editTextCheckUserPassword, "Повторите пароль");
                    return;
                }

                if (!editTextUserPassword.getText().toString().equals(
                        editTextCheckUserPassword.getText().toString())) {
                    Toast.makeText(getContext(), "Пароли не совпадают!", Toast.LENGTH_SHORT).show();
                    return;
                }

                //убираем клавиатуру после нажатия на кнопку
                InputMethodManager inputManager =
                        (InputMethodManager) Objects.requireNonNull(getContext())
                                .getSystemService(Activity.INPUT_METHOD_SERVICE);
                if (inputManager != null) {
                    inputManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }

                uiState(LOADING);
                if (viewModel.checkNetwork()) {
                    LiveData<Boolean> liveData = viewModel.registered(
                            editTextUserName.getText().toString(),
                            editTextUserEmail.getText().toString(),
                            editTextUserPassword.getText().toString());
                    liveData.observe(RegistryFragment.this, new Observer<Boolean>() {
                        @Override
                        public void onChanged(Boolean isRegistered) {
                            if (isRegistered) {
                                loadNotification();
                                ToggleStatusChange toggleStatusChange = (ToggleStatusChange) getActivity();
                                if (toggleStatusChange != null) {
                                    toggleStatusChange.changeToggleStatus(true);
                                }
                                openStartScreenFragment();
                            } else {
                                uiState(WAIT);
                                Toast.makeText(
                                        getContext(),
                                        "Неверный логин или пароль!",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    uiState(WAIT);
                    Toast.makeText(
                            getContext(),
                            "Нет подключения",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }

    private void takeFocusOnView(EditText editText, String toastText) {
        editText.setFocusableInTouchMode(true);
        editText.setFocusable(true);
        editText.requestFocus();
        Toast.makeText(getContext(), toastText, Toast.LENGTH_SHORT).show();
    }

    private void uiState(boolean isLoading) {
        progressBar.setVisibility(
                (isLoading)
                        ? View.VISIBLE
                        : View.INVISIBLE
        );
        buttonRegister.setPressed(isLoading);
    }

    private void loadNotification() {
        if (!isServiceRunning()) {
            Intent intent;
            // Создаем Intent для вызова сервиса,
            // кладем туда параметр времени и код задачи
            int LOAD_NOTIFICATION = 1;
            String PARAM_TIME = "time";
            String PARAM_TASK = "task";
            intent = new Intent(getContext(), LoadNotificationsService.class)
                    .putExtra(PARAM_TIME, 7)
                    .putExtra(PARAM_TASK, LOAD_NOTIFICATION);
            // стартуем сервис
            Objects.requireNonNull(getActivity()).startService(intent);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        TitleChange listenerTitleChange = (TitleChange) getActivity();
        if (listenerTitleChange != null) {
            listenerTitleChange.changeTitle(
                    R.string.title_Regystry_Fragment,
                    R.string.title_Regystry_Fragment);
        }
    }


    private boolean isServiceRunning() {
        ActivityManager manager = (ActivityManager) Objects.requireNonNull(getActivity())
                .getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            for (ActivityManager.RunningServiceInfo service : Objects.requireNonNull(manager)
                    .getRunningServices(Integer.MAX_VALUE)) {
                if (LoadNotificationsService.class.getName().equals(service.service.getClassName())) {
                    return true;
                }
            }
        }
        return false;
    }

    private void openStartScreenFragment() {
        Fragment fragment = new StartScreenFragment();
        FragmentManager fragmentManager = getFragmentManager();
        if (fragmentManager != null) {
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
    }

    private boolean isEmailCorrect(String userEmail) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(userEmail);
        return matcher.matches();
    }

}