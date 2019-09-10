package ru.lizzzi.crossfit_rekord.fragments;

import android.app.Activity;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
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
import ru.lizzzi.crossfit_rekord.model.PasswordRecoveryViewModel;

public class PasswordRecoveryFragment extends Fragment {

    private EditText editEmailForRecovery;
    private Button buttonRecoveryPassword;
    private ProgressBar progressBar;

    private PasswordRecoveryViewModel viewModel;

    private boolean LOADING = true;
    private boolean WAITING = false;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_email, container, false);
        viewModel = ViewModelProviders.of(PasswordRecoveryFragment.this)
                .get(PasswordRecoveryViewModel.class);
        editEmailForRecovery = view.findViewById(R.id.editTextEmailForRecovery);
        buttonRecoveryPassword = view.findViewById(R.id.buttonRecoveryEmail);
        progressBar = view.findViewById(R.id.progressbar);

        buttonRecoveryPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userEmail = editEmailForRecovery.getText().toString();
                if (userEmail.endsWith(" ")) {
                    userEmail = userEmail.substring(0, userEmail.length() - 1);
                    editEmailForRecovery.setText(userEmail);
                }

                boolean userEmailIsCorrect = isEmailCorrect(userEmail);
                if (editEmailForRecovery.getText().length()== 0 || userEmailIsCorrect) {
                    editEmailForRecovery.setFocusableInTouchMode(true);
                    editEmailForRecovery.setFocusable(true);
                    editEmailForRecovery.requestFocus();
                    showToast("Введите почту!");
                    return;
                }

                //убираем клавиатуру после нажатия на кнопку
                InputMethodManager inputMethodManager =
                        (InputMethodManager) Objects.requireNonNull(getActivity())
                                .getSystemService(Activity.INPUT_METHOD_SERVICE);
                if (inputMethodManager != null) {
                    inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }

                uiState(LOADING);
                LiveData<Boolean> liveDataConnection = viewModel.checkNetwork();
                liveDataConnection.observe(
                        PasswordRecoveryFragment.this,
                        new Observer<Boolean>() {
                    @Override
                    public void onChanged(final Boolean isConnected) {
                        if (isConnected) {
                            final LiveData<Boolean> liveData = viewModel.recoverPassword(
                                    editEmailForRecovery.getText().toString());
                            liveData.observe(
                                    PasswordRecoveryFragment.this,
                                    new Observer<Boolean>() {
                                @Override
                                public void onChanged(Boolean isRecovered) {
                                    showToast((isRecovered)
                                            ? "Ожидайте письма"
                                            : "Повторите попытку");
                                    uiState(WAITING);
                                    if (isRecovered) {
                                        openLoginFragment();
                                    }
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
                    R.string.title_PasswordRecovery_Fragment,
                    R.string.title_AboutMe_Fragment);
        }
    }

    private void uiState(boolean loading) {
        progressBar.setVisibility(
                (loading)
                        ? View.VISIBLE
                        : View.INVISIBLE);
        buttonRecoveryPassword.setPressed(loading);
    }

    private static boolean isEmailCorrect(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return !matcher.matches();
    }

    private void openLoginFragment() {
        FragmentManager fragmentManager = getFragmentManager();
        if (fragmentManager != null) {
            for (int i = 0; i < (fragmentManager.getBackStackEntryCount()-1); i++) {
                fragmentManager.popBackStack();
            }
            Fragment fragment = new LoginFragment();
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

    private void showToast(String toastText) {
        Toast.makeText(getContext(), toastText, Toast.LENGTH_SHORT).show();
    }
}