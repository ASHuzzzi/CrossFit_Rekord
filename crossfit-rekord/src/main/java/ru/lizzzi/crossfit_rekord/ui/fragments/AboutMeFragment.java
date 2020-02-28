package ru.lizzzi.crossfit_rekord.ui.fragments;

import android.app.Activity;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
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

import ru.lizzzi.crossfit_rekord.R;
import ru.lizzzi.crossfit_rekord.interfaces.TitleChange;
import ru.lizzzi.crossfit_rekord.model.AboutMeViewModel;

/**
 * Created by basso on 07.03.2018.
 */

public class AboutMeFragment extends Fragment {

    private TextView textCardNumber;
    private EditText editName;
    private EditText editSurname;
    private EditText editPhone;
    private Button buttonChangeUserData;
    private ProgressBar progressBar;

    private AboutMeViewModel viewModel;
    private boolean LOADING = true;
    private boolean WAITING = false;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_about_me, container, false);
        viewModel = ViewModelProviders.of(AboutMeFragment.this).get(AboutMeViewModel.class);

        textCardNumber = view.findViewById(R.id.textCardNumber);
        editName = view.findViewById(R.id.editName);
        editSurname = view.findViewById(R.id.editSurname);
        editPhone = view.findViewById(R.id.editPhone);
        progressBar = view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        initButtonChangeUserData(view);
        initTextChangeEmail(view);
        initTextChangePassword(view);

        return view;
    }

    private void initButtonChangeUserData(View rootView) {
        buttonChangeUserData = rootView.findViewById(R.id.buttonChangeUserData);
        buttonChangeUserData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editName.setText(deleteSpaceInEnd(editName.getText().toString()));
                editSurname.setText(deleteSpaceInEnd(editSurname.getText().toString()));
                editPhone.setText(deleteSpaceInEnd(editPhone.getText().toString()));

                //убираем клавиатуру после нажатия на кнопку
                InputMethodManager inputMethodManager = (InputMethodManager)
                        Objects.requireNonNull(getContext())
                                .getSystemService(Activity.INPUT_METHOD_SERVICE);
                if (inputMethodManager != null) {
                    inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }

                uiState(LOADING);
                LiveData<Boolean> liveDataConnection = viewModel.checkNetwork();
                liveDataConnection.observe(AboutMeFragment.this, new Observer<Boolean>() {
                    @Override
                    public void onChanged(Boolean isConnected) {
                        if (isConnected) {
                            final LiveData<Boolean> liveData = viewModel.saveUserData(
                                    editName.getText().toString(),
                                    editSurname.getText().toString(),
                                    editPhone.getText().toString());
                            liveData.observe(AboutMeFragment.this, new Observer<Boolean>() {
                                @Override
                                public void onChanged(Boolean isSaved) {
                                    Toast.makeText(
                                            getContext(),
                                            (isSaved)
                                                    ? "Данные обновлены"
                                                    : "Повторите сохранение",
                                            Toast.LENGTH_SHORT).show();
                                    uiState(WAITING);
                                    liveData.removeObservers(AboutMeFragment.this);
                                }
                            });
                        } else {
                            uiState(WAITING);
                            Toast.makeText(
                                    getContext(),
                                    "Нет подключения",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private void initTextChangeEmail(View rootView) {
        TextView textChangeEmail = rootView.findViewById(R.id.textOpenChangeEmail);
        textChangeEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFragment(ChangeEmailFragment.class);
            }
        });
    }

    private void initTextChangePassword(View rootView) {
        TextView textChangePassword = rootView.findViewById(R.id.textOpenChangePas);
        textChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFragment(ChangePasswordFragment.class);
            }
        });
    }


    private String deleteSpaceInEnd(String inputText) {
        return (inputText.endsWith(" "))
                ? inputText.substring(0, inputText.length() - 1)
                : inputText;
    }

    private void uiState(boolean loading) {
        progressBar.setVisibility(
                (loading)
                        ? View.VISIBLE
                        : View.INVISIBLE);
        buttonChangeUserData.setPressed(loading);
    }

    private void openFragment(Class fragmentClass) {
        try {
            Fragment fragment = (Fragment) fragmentClass.newInstance();
            FragmentManager fragmentManager = getFragmentManager();
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
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (java.lang.InstantiationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public  void onStart() {
        super.onStart();
        TitleChange listenerTitleChange = (TitleChange) getActivity();
        if (listenerTitleChange != null) {
            listenerTitleChange.changeTitle(
                    R.string.title_AboutMe_Fragment,
                    R.string.title_AboutMe_Fragment);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        textCardNumber.setText(viewModel.getCardNumber());
        editName.setText(viewModel.getUserName());
        editSurname.setText(viewModel.getUserSurname());
        editPhone.setText(viewModel.getPhone());
        editName.setSelection(editName.getText().length());
    }
}
