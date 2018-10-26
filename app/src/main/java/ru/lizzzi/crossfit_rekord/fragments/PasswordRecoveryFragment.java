package ru.lizzzi.crossfit_rekord.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import ru.lizzzi.crossfit_rekord.interfaces.InterfaceChangeTitle;
import ru.lizzzi.crossfit_rekord.loaders.RecoveryEmailLoader;

public class PasswordRecoveryFragment extends Fragment implements LoaderManager.LoaderCallbacks<Boolean>{

    private EditText etRecPasEmail;
    private Button btnRecPasSend;
    private ProgressBar pbRecPas;

    private ru.lizzzi.crossfit_rekord.inspectionСlasses.NetworkCheck NetworkCheck; //переменная для проврки сети

    private Handler handlerPasswordRecoveryFragment;
    private Thread threadPasswordRecoveryFragment;
    private Runnable runnablePasswordRecoveryFragment;

    private int openFragment = 1;

    @SuppressLint("HandlerLeak")
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_email, container, false);

        etRecPasEmail = v.findViewById(R.id.etRecPasEmail);
        btnRecPasSend = v.findViewById(R.id.btnRecPasSend);
        pbRecPas = v.findViewById(R.id.pbRecPas);

        //хэндлер для потока runnableOpenFragment
        handlerPasswordRecoveryFragment = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == openFragment){
                    TransactionFragment(LoginFragment.class);
                }else {
                    Bundle bundle = msg.getData();
                    String result_check = bundle.getString("result");
                    if (result_check != null && result_check.equals("true")){
                        ChangeUIElements(1);
                        startAsyncTaskLoader(etRecPasEmail.getText().toString());

                    }else{
                        ChangeUIElements(0);
                        Toast.makeText(getActivity(), "Нет подключения", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        };

        //поток запускаемый при создании экрана (запуск происходит из onStart)
        runnablePasswordRecoveryFragment = new Runnable() {
            @Override
            public void run() {

                NetworkCheck = new NetworkCheck(getActivity());
                boolean resultCheck = NetworkCheck.checkInternet();
                Bundle bundle = new Bundle();
                if (resultCheck){
                    bundle.putString("result", String.valueOf(true));

                }else {
                    bundle.putString("result", String.valueOf(false));
                }
                Message msg = handlerPasswordRecoveryFragment.obtainMessage();
                msg.setData(bundle);
                handlerPasswordRecoveryFragment.sendMessage(msg);
            }
        };

        btnRecPasSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String stCheckSpace = etRecPasEmail.getText().toString();
                if(stCheckSpace.endsWith(" ")){
                    stCheckSpace = stCheckSpace.substring(0, stCheckSpace.length() - 1);
                    etRecPasEmail.setText(stCheckSpace);
                }

                if (etRecPasEmail.getText().length()== 0 || !isEmailValid(etRecPasEmail.getText().toString())){
                    etRecPasEmail.setFocusableInTouchMode(true);
                    etRecPasEmail.setFocusable(true);
                    etRecPasEmail.requestFocus();
                    Toast.makeText(getActivity(), "Введите почту!", Toast.LENGTH_SHORT).show();
                    return;
                }

                //убираем клавиатуру после нажатия на кнопку
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }

                threadPasswordRecoveryFragment = new Thread(runnablePasswordRecoveryFragment);
                threadPasswordRecoveryFragment.setDaemon(true);
                threadPasswordRecoveryFragment.start();
            }
        });

        return v;
    }

    @Override
    public  void onStart() {
        super.onStart();
        if (getActivity() instanceof InterfaceChangeTitle){
            InterfaceChangeTitle listernerChangeTitle = (InterfaceChangeTitle) getActivity();
            listernerChangeTitle.changeTitle(R.string.title_PasswordRecovery_Fragment, R.string.title_AboutMe_Fragment);
        }

    }

    private void startAsyncTaskLoader(String e_mailOld) {
        Bundle bundle = new Bundle();
        bundle.putString("e_mailOld", e_mailOld);
        int LOADERID = 1;
        getLoaderManager().restartLoader(LOADERID, bundle, this).forceLoad();
    }


    @Override
    public Loader<Boolean> onCreateLoader(int id, Bundle args) {
        RecoveryEmailLoader loader;
        loader = new RecoveryEmailLoader(getActivity(), args);
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Boolean> loader, Boolean data) {

        if (data){

            Toast.makeText(getActivity(), "Ожидайте письма", Toast.LENGTH_SHORT).show();
            handlerPasswordRecoveryFragment.sendEmptyMessage(openFragment);
        }else {
            Toast.makeText(getActivity(), "Повторите попытку", Toast.LENGTH_SHORT).show();
        }
        ChangeUIElements(0);

    }

    @Override
    public void onLoaderReset(Loader<Boolean> loader) {

    }

    private void ChangeUIElements(int status){
        if (status == 1){
            pbRecPas.setVisibility(View.VISIBLE);
            btnRecPasSend.setPressed(true);
        }else{
            pbRecPas.setVisibility(View.INVISIBLE);
            btnRecPasSend.setPressed(false);
        }
    }

    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private void TransactionFragment(Class fragmentClass) {

        Fragment fragment = null;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        FragmentManager fragmentManager = getFragmentManager();
        for(int i = 0; i < (fragmentManager.getBackStackEntryCount()-1); i++) {
            fragmentManager.popBackStack();
        }
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.replace(R.id.container, fragment);
        ft.commit();
    }
}
