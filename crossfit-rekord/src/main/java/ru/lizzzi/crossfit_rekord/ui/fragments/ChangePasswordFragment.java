package ru.lizzzi.crossfit_rekord.ui.fragments;

import android.app.Activity;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.Objects;

import ru.lizzzi.crossfit_rekord.R;
import ru.lizzzi.crossfit_rekord.interfaces.TitleChange;
import ru.lizzzi.crossfit_rekord.model.ChangePasswordViewModel;

public class ChangePasswordFragment extends Fragment {

    private EditText editOldPassword;
    private EditText editNewPassword;
    private EditText editRepeatPassword;
    private ProgressBar progressBar;
    private Button buttonChangePassword;

    private ChangePasswordViewModel viewModel;

    private boolean LOADING = true;
    private boolean WAITING = false;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_change_password, container, false);
        viewModel = ViewModelProviders.of(ChangePasswordFragment.this)
                .get(ChangePasswordViewModel.class);

        editOldPassword = view.findViewById(R.id.editPasswordOld);
        editNewPassword = view.findViewById(R.id.editPasswordNew);
        editRepeatPassword = view.findViewById(R.id.editPasswordRepeat);
        progressBar = view.findViewById(R.id.progressBar);
        buttonChangePassword = view.findViewById(R.id.buttonChangePassword);

        buttonChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editNewPassword.getText().length() == 0) {
                    setFocusOnView(editNewPassword, "Введите пароль");
                    return;
                }

                if (editRepeatPassword.getText().length() == 0) {
                    setFocusOnView(editRepeatPassword, "Повторите пароль");
                    return;
                }

                String checkSpace = viewModel.getUserPassword();
                if(!editOldPassword.getText().toString().equals(checkSpace)) {
                    setFocusOnView(editOldPassword, "Неправильный старый пароль!");
                    return;
                }

                if(!editNewPassword.getText().toString()
                        .equals(editRepeatPassword.getText().toString())) {
                    showToast("Пароли не совпадают!");
                    return;
                }

                //убираем клавиатуру после нажатия на кнопку
                InputMethodManager inputMethodManager =
                        (InputMethodManager) Objects.requireNonNull(getContext())
                                .getSystemService(Activity.INPUT_METHOD_SERVICE);
                if (inputMethodManager != null) {
                    inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }

                uiState(LOADING);
                LiveData<Boolean> liveDataConnection = viewModel.checkNetwork();
                liveDataConnection.observe(ChangePasswordFragment.this, new Observer<Boolean>() {
                    @Override
                    public void onChanged(final Boolean isConnected) {
                        if (isConnected) {
                            final LiveData<Boolean> liveData =
                                    viewModel.changePassword(editNewPassword.getText().toString());
                            liveData.observe(ChangePasswordFragment.this, new Observer<Boolean>() {
                                @Override
                                public void onChanged(Boolean isSaved) {
                                    if (isSaved) {
                                        editOldPassword
                                                .setText(getResources().getString(R.string.empty));
                                        editNewPassword
                                                .setText(getResources().getString(R.string.empty));
                                        editRepeatPassword.
                                                setText(getResources().getString(R.string.empty));
                                    }
                                    showToast((isSaved)
                                            ? "Данные обновлены"
                                            : "Повторите сохранение");
                                    uiState(WAITING);
                                }
                            });
                        } else {
                            uiState(WAITING);
                            showToast("Нет подключения");
                        }
                    }
                });
            }
        });

        return view;
    }

    private void setFocusOnView(View view, String toastText) {
        view.setFocusableInTouchMode(true);
        view.setFocusable(true);
        view.requestFocus();
        showToast(toastText);
    }

    @Override
    public  void onStart() {
        super.onStart();
        TitleChange listenerTitleChange = (TitleChange) getActivity();
        if (listenerTitleChange != null) {
            listenerTitleChange.changeTitle(
                    R.string.title_ChangePassword_Fragment,
                    R.string.title_AboutMe_Fragment);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        progressBar.setVisibility(View.INVISIBLE);
        editOldPassword.setText(getResources().getString(R.string.empty));
        editNewPassword.setText(getResources().getString(R.string.empty));
        editRepeatPassword.setText(getResources().getString(R.string.empty));
    }

    private void uiState(boolean loading) {
        progressBar.setVisibility(
                (loading)
                        ? View.VISIBLE
                        : View.INVISIBLE);
        buttonChangePassword.setPressed(loading);
    }

    private void showToast(String toastText) {
        Toast.makeText(getContext(), toastText, Toast.LENGTH_SHORT).show();
    }
}
