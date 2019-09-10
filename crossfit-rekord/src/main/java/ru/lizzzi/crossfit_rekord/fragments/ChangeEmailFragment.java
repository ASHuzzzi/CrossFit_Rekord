package ru.lizzzi.crossfit_rekord.fragments;

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
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ru.lizzzi.crossfit_rekord.R;
import ru.lizzzi.crossfit_rekord.interfaces.TitleChange;
import ru.lizzzi.crossfit_rekord.model.ChangeEmailViewModel;

public class ChangeEmailFragment extends Fragment {

    private TextView textOldEmail;
    private EditText editNewEmail;
    private ProgressBar progressBar;
    private Button buttonChangeEmail;

    private ChangeEmailViewModel viewModel;

    private boolean LOADING = true;
    private boolean WAITING = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_change_email, container, false);
        viewModel = ViewModelProviders.of(ChangeEmailFragment.this)
                .get(ChangeEmailViewModel.class);

        textOldEmail = view.findViewById(R.id.textOldEmail);
        editNewEmail = view.findViewById(R.id.editTextNewEmail);
        progressBar = view.findViewById(R.id.progressbar);

        buttonChangeEmail = view.findViewById(R.id.buttonChangeEmail);
        buttonChangeEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String checkSpace = editNewEmail.getText().toString();
                if(checkSpace.endsWith(" ")) {
                    checkSpace = checkSpace.substring(0, checkSpace.length() - 1);
                    editNewEmail.setText(checkSpace);
                }

                if (editNewEmail.getText().length()== 0 ||
                        isEmailValid(editNewEmail.getText().toString())) {
                    editNewEmail.setFocusableInTouchMode(true);
                    editNewEmail.setFocusable(true);
                    editNewEmail.requestFocus();
                    showToast("Введите почту!");
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
                liveDataConnection.observe(ChangeEmailFragment.this, new Observer<Boolean>() {
                    @Override
                    public void onChanged(final Boolean isConnected) {
                        if (isConnected) {
                            final LiveData<Boolean> liveData =
                                    viewModel.changeEmail(editNewEmail.getText().toString());
                            liveData.observe(ChangeEmailFragment.this, new Observer<Boolean>() {
                                @Override
                                public void onChanged(Boolean isSaved) {
                                    if (isSaved) {
                                        textOldEmail.setText(viewModel.getUserEmail());
                                        editNewEmail.setText(
                                                getResources().getString(R.string.empty));
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

    @Override
    public  void onStart() {
        super.onStart();
        TitleChange listenerTitleChange = (TitleChange) getActivity();
        if (listenerTitleChange != null) {
            listenerTitleChange.changeTitle(
                    R.string.title_ChangeEmail_Fragment,
                    R.string.title_AboutMe_Fragment);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        progressBar.setVisibility(View.INVISIBLE);
        textOldEmail.setText(viewModel.getUserEmail());
        editNewEmail.setText(getResources().getString(R.string.empty));
    }

    private void uiState(boolean loading) {
        progressBar.setVisibility(
                (loading)
                        ? View.VISIBLE
                        : View.INVISIBLE);
        buttonChangeEmail.setPressed(loading);
    }

    private boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return !matcher.matches();
    }

    private void showToast(String toastText) {
        Toast.makeText(getContext(), toastText, Toast.LENGTH_SHORT).show();
    }
}
