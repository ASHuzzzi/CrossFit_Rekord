package ru.lizzzi.crossfit_rekord.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
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

import ru.lizzzi.crossfit_rekord.R;
import ru.lizzzi.crossfit_rekord.inspectionСlasses.NetworkCheck;
import ru.lizzzi.crossfit_rekord.interfaces.InterfaceChangeTitle;
import ru.lizzzi.crossfit_rekord.loaders.ChangePasswordLoader;

public class ChangePasswordFragment extends Fragment implements LoaderManager.LoaderCallbacks<Boolean>{
    private static final String APP_PREFERENCES = "audata";
    private static final String APP_PREFERENCES_EMAIL = "Email";
    private static final String APP_PREFERENCES_PASSWORD = "Password";
    private SharedPreferences mSettings;

    private EditText etPasswordOld;
    private EditText etPasswordNew;
    private EditText etPasswordRepeat;
    private ProgressBar pbChangePassword;
    private Button btnChangePassword;

    private ru.lizzzi.crossfit_rekord.inspectionСlasses.NetworkCheck NetworkCheck; //переменная для проврки сети

    private Handler handlerChangeEmailFragment;
    private Thread threadChangeEmailFragment;
    private Runnable runnableChangeEmailFragment;

    @SuppressLint("HandlerLeak")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_change_password, container, false);

        mSettings = getContext().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        etPasswordOld = v.findViewById(R.id.etPasswordOld);
        etPasswordNew = v.findViewById(R.id.etPasswordNew);
        etPasswordRepeat = v.findViewById(R.id.etPasswordRepeat);
        pbChangePassword = v.findViewById(R.id.pbChangePassword);
        btnChangePassword = v.findViewById(R.id.btnChangePassword);

        //хэндлер для потока runnableOpenFragment
        handlerChangeEmailFragment = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Bundle bundle = msg.getData();
                String result_check = bundle.getString("result");
                if (result_check != null && result_check.equals("true")){
                    ChangeUIElements(1);
                    startAsyncTaskLoader(
                            mSettings.getString(APP_PREFERENCES_EMAIL, ""),
                            etPasswordOld.getText().toString(),
                            etPasswordNew.getText().toString()

                    );
                }else{
                    ChangeUIElements(0);
                    Toast.makeText(getContext(), "Нет подключения", Toast.LENGTH_SHORT).show();
                }
            }
        };

        //поток запускаемый при создании экрана (запуск происходит из onStart)
        runnableChangeEmailFragment = new Runnable() {
            @Override
            public void run() {

                NetworkCheck = new NetworkCheck(getContext());
                boolean resultCheck = NetworkCheck.checkInternet();
                Bundle bundle = new Bundle();
                if (resultCheck){
                    bundle.putString("result", String.valueOf(true));

                }else {
                    bundle.putString("result", String.valueOf(false));
                }
                Message msg = handlerChangeEmailFragment.obtainMessage();
                msg.setData(bundle);
                handlerChangeEmailFragment.sendMessage(msg);
            }
        };

        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String stCheckPas = mSettings.getString(APP_PREFERENCES_PASSWORD, "");
                if(!etPasswordOld.getText().toString().equals(stCheckPas)){
                    etPasswordOld.setFocusableInTouchMode(true);
                    etPasswordOld.setFocusable(true);
                    etPasswordOld.requestFocus();
                    Toast.makeText(getContext(), "Неправильный старый пароль!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(etPasswordNew.getText().length()== 0 || etPasswordRepeat.getText().length()==0){
                    if(etPasswordNew.getText().length()== 0){
                        etPasswordNew.setFocusableInTouchMode(true);
                        etPasswordNew.setFocusable(true);
                        etPasswordNew.requestFocus();
                        Toast.makeText(getContext(), "Введите пароль", Toast.LENGTH_SHORT).show();
                    }else {
                        etPasswordRepeat.setFocusableInTouchMode(true);
                        etPasswordRepeat.setFocusable(true);
                        etPasswordRepeat.requestFocus();
                        Toast.makeText(getContext(), "Повторите пароль", Toast.LENGTH_SHORT).show();
                    }
                    return;
                }

                if(!etPasswordNew.getText().toString().equals(etPasswordRepeat.getText().toString())){
                    Toast.makeText(getContext(), "Пароли не совпадают!", Toast.LENGTH_SHORT).show();
                    return;
                }
                //убираем клавиатуру после нажатия на кнопку
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }

                threadChangeEmailFragment = new Thread(runnableChangeEmailFragment);
                threadChangeEmailFragment.setDaemon(true);
                threadChangeEmailFragment.start();
            }
        });

        return v;
    }

    @Override
    public  void onStart() {
        super.onStart();
        if (getActivity() instanceof InterfaceChangeTitle){
            InterfaceChangeTitle listernerChangeTitle = (InterfaceChangeTitle) getActivity();
            listernerChangeTitle.changeTitle(R.string.title_ChangePassword_Fragment, R.string.title_AboutMe_Fragment);
        }

    }

    @Override
    public void onResume() {
        super.onResume();

        pbChangePassword.setVisibility(View.INVISIBLE);
        etPasswordOld.setText(getResources().getString(R.string.empty));
        etPasswordNew.setText(getResources().getString(R.string.empty));
        etPasswordRepeat.setText(getResources().getString(R.string.empty));
    }

    private void ChangeUIElements(int status){
        if (status == 1){
            pbChangePassword.setVisibility(View.VISIBLE);
            btnChangePassword.setPressed(true);
        }else{
            pbChangePassword.setVisibility(View.INVISIBLE);
            btnChangePassword.setPressed(false);
        }
    }

    private void startAsyncTaskLoader(String e_mail, String oldPassword, String newPassword) {
        Bundle bundle = new Bundle();
        bundle.putString("e_mail", e_mail);
        bundle.putString("oldPassword", oldPassword);
        bundle.putString("newPassword", newPassword);
        int LOADERID = 1;
        getLoaderManager().restartLoader(LOADERID, bundle, this).forceLoad();
    }


    @Override
    public Loader<Boolean> onCreateLoader(int id, Bundle args) {
        ChangePasswordLoader loader;
        loader = new ChangePasswordLoader(getContext(), args);
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Boolean> loader, Boolean data) {
        if (data){
            etPasswordOld.setText(getResources().getString(R.string.empty));
            etPasswordNew.setText(getResources().getString(R.string.empty));
            etPasswordRepeat.setText(getResources().getString(R.string.empty));
            Toast.makeText(getContext(), "Данные обновлены", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(getContext(), "Повторите сохранение", Toast.LENGTH_SHORT).show();
        }
        ChangeUIElements(0);

    }

    @Override
    public void onLoaderReset(Loader<Boolean> loader) {

    }
}
