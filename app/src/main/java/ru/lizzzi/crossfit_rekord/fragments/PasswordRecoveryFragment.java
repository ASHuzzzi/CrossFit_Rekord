package ru.lizzzi.crossfit_rekord.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ru.lizzzi.crossfit_rekord.R;
import ru.lizzzi.crossfit_rekord.inspectionСlasses.NetworkCheck;
import ru.lizzzi.crossfit_rekord.interfaces.TitleChange;
import ru.lizzzi.crossfit_rekord.loaders.RecoveryEmailLoader;

public class PasswordRecoveryFragment extends Fragment implements LoaderManager.LoaderCallbacks<Boolean>{

    private EditText editTextEmailForRecovery;
    private Button buttonRecoveryPassword;
    private ProgressBar progressBar;

    private Handler handlerPasswordRecovery;
    private Thread threadPasswordRecovery;
    private Runnable runnablePasswordRecovery;

    private final static int LOGIN_IS_DONE = 1;
    private final static int WAIT_STATE = 0;
    private final static int LOAD_STATE = 1;

    @SuppressLint("HandlerLeak")
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_email, container, false);
        editTextEmailForRecovery = view.findViewById(R.id.editTextEmailForRecovery);
        buttonRecoveryPassword = view.findViewById(R.id.buttonRecoveryEmail);
        progressBar = view.findViewById(R.id.progressbar);

        //хэндлер для потока runnableOpenFragment
        handlerPasswordRecovery = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == LOGIN_IS_DONE) {
                    openLoginFragment();
                } else {
                    Bundle bundle = msg.getData();
                    boolean checkDone = bundle.getBoolean("result");
                    if (checkDone) {
                        changeUIOnState(LOAD_STATE);
                        startRecoveryPasswordLoader(editTextEmailForRecovery.getText().toString());

                    } else {
                        changeUIOnState(WAIT_STATE);
                        Toast.makeText(getActivity(), "Нет подключения", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        };

        //поток запускаемый при создании экрана (запуск происходит из onStart)
        runnablePasswordRecovery = new Runnable() {
            @Override
            public void run() {
                NetworkCheck network = new NetworkCheck(getContext());
                boolean checkDone = network.checkConnection();
                Bundle bundle = new Bundle();
                bundle.putBoolean("result", checkDone);
                Message msg = handlerPasswordRecovery.obtainMessage();
                msg.setData(bundle);
                handlerPasswordRecovery.sendMessage(msg);
            }
        };

        buttonRecoveryPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userEmail = editTextEmailForRecovery.getText().toString();
                if (userEmail.endsWith(" ")) {
                    userEmail = userEmail.substring(0, userEmail.length() - 1);
                    editTextEmailForRecovery.setText(userEmail);
                }

                boolean userEmailIsCorrect = isEmailCorrect(userEmail);
                if (editTextEmailForRecovery.getText().length()== 0 || userEmailIsCorrect) {
                    editTextEmailForRecovery.setFocusableInTouchMode(true);
                    editTextEmailForRecovery.setFocusable(true);
                    editTextEmailForRecovery.requestFocus();
                    Toast.makeText(getActivity(), "Введите почту!", Toast.LENGTH_SHORT).show();
                    return;
                }

                //убираем клавиатуру после нажатия на кнопку
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
                threadPasswordRecovery = new Thread(runnablePasswordRecovery);
                threadPasswordRecovery.setDaemon(true);
                threadPasswordRecovery.start();
            }
        });
        return view;
    }

    @Override
    public  void onStart() {
        super.onStart();
        if (getActivity() instanceof TitleChange) {
            TitleChange listernerTitleChange = (TitleChange) getActivity();
            listernerTitleChange.changeTitle(R.string.title_PasswordRecovery_Fragment, R.string.title_AboutMe_Fragment);
        }
    }

    private void startRecoveryPasswordLoader(String oldUserEmail) {
        Bundle bundle = new Bundle();
        bundle.putString("e_mailOld", oldUserEmail);
        int RECOVERY_LOADER = 1;
        getLoaderManager().restartLoader(RECOVERY_LOADER, bundle, this).forceLoad();
    }


    @NonNull
    @Override
    public Loader<Boolean> onCreateLoader(int id, Bundle bundle) {
        return new RecoveryEmailLoader(getActivity(), bundle);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Boolean> loader, Boolean recoveryIsGone) {
        if (recoveryIsGone) {
            Toast.makeText(getActivity(), "Ожидайте письма", Toast.LENGTH_SHORT).show();
            handlerPasswordRecovery.sendEmptyMessage(LOGIN_IS_DONE);
        } else {
            Toast.makeText(getActivity(), "Повторите попытку", Toast.LENGTH_SHORT).show();
        }
        changeUIOnState(WAIT_STATE);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Boolean> loader) {
    }

    private void changeUIOnState(int state) {
        if (state == LOAD_STATE) {
            progressBar.setVisibility(View.VISIBLE);
            buttonRecoveryPassword.setPressed(true);
        } else {
            progressBar.setVisibility(View.INVISIBLE);
            buttonRecoveryPassword.setPressed(false);
        }
    }

    private static boolean isEmailCorrect(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return !matcher.matches();
    }

    private void openLoginFragment() {
        Fragment fragment = null;
        try {
            fragment = LoginFragment.class.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        FragmentManager fragmentManager = getFragmentManager();
        for (int i = 0; i < (fragmentManager.getBackStackEntryCount()-1); i++) {
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