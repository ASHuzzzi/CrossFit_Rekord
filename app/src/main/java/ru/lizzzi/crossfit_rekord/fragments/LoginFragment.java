package ru.lizzzi.crossfit_rekord.fragments;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.LoaderManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
import ru.lizzzi.crossfit_rekord.loaders.LoginLoader;


//* Created by basso on 07.03.2018.

public class LoginFragment extends Fragment implements LoaderManager.LoaderCallbacks<Void> {

    private static final int MSG_SHOW_DIALOG = 1 ;
    private NetworkCheck NetworkCheck; //переменная для проврки сети

    private Button btnComeIn;
    private ProgressBar pbLogin;
    private EditText tvCardNumber;
    private EditText tvPassword;

    Bundle bundle;

    private String stOpenableFragment;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_login, container, false);

        tvCardNumber = v.findViewById(R.id.editText4);
        tvPassword = v.findViewById(R.id.editText5);
        btnComeIn = v.findViewById(R.id.button2);
        pbLogin = v.findViewById(R.id.pbLogin);

        getActivity().setTitle("Авторизация");
        bundle = getArguments();
        stOpenableFragment = bundle.getString("fragment");

        btnComeIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tvCardNumber.getText().length() != 13 ){
                    tvCardNumber.setFocusable(true);
                    Toast.makeText(getContext(), "Номер карты не корректный", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (tvPassword.getText().length() != 13 ){
                    tvPassword.setFocusable(true);
                    Toast.makeText(getContext(), "Введите пароль", Toast.LENGTH_SHORT).show();
                    return;
                }

                //убираем клавиатуру после нажатия на кнопку
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }

                NetworkCheck = new NetworkCheck(getContext());
                ChangeUIElements(1);
                if (NetworkCheck.checkInternet()){
                    startAsyncTaskLoader(tvCardNumber.getText().toString(), tvPassword.getText().toString());

                }else {
                    ChangeUIElements(0);
                    Toast.makeText(getContext(), "Нет подключения", Toast.LENGTH_SHORT).show();
                }

            }
        });

        //убрал т.к. регистрация пока не планируется, только авторизация
        /*btnRegisration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TransactionFragment(RegistryFragment.class);
            }
        });*/
        return  v;
    }

    private void TransactionFragment(Class fragmentClass) {

        Fragment fragment = null;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.popBackStack();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.container, fragment);
        ft.addToBackStack(null);
        ft.commit();

    }

    private void ChangeUIElements(int status){
        if (status == 1){
            pbLogin.setVisibility(View.VISIBLE);
            btnComeIn.setPressed(true);
        }else{
            pbLogin.setVisibility(View.INVISIBLE);
            btnComeIn.setPressed(false);
        }
    }


    private void startAsyncTaskLoader(String iCardNumber, String stPassword) {
        Bundle bundle = new Bundle();
        bundle.putString("cardNumber" , iCardNumber);
        bundle.putString("password" , stPassword);
        int LOADERID = 1;
        getLoaderManager().initLoader(LOADERID, bundle,this).forceLoad();
    }

    @Override
    public Loader<Void> onCreateLoader(int id, Bundle args) {
        LoginLoader loader;
        loader = new LoginLoader(getContext(), args);
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Void> loader, Void data) {
        handler.sendEmptyMessage(MSG_SHOW_DIALOG);
    }

    @Override
    public void onLoaderReset(Loader<Void> loader) {

    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            if(msg.what == MSG_SHOW_DIALOG) {
                if (stOpenableFragment.equals(String.valueOf(R.string.strRecordFragment))){
                    TransactionFragment(RecordForTrainingSelectFragment.class);
                }

                if (stOpenableFragment.equals(String.valueOf(R.string.strAboutMeFragment))){
                    TransactionFragment(AboutMeFragment.class);
                }
                if (stOpenableFragment.equals(String.valueOf(R.string.strRecordForTrainingRecordingFragment))){
                    bundle.putString("time", bundle.getString("time"));
                    bundle.putString("datefull", bundle.getString("datefull"));
                    bundle.putString("dateshow", bundle.getString("dateshow"));
                    bundle.putString("type", bundle.getString("type"));
                    RecordForTrainingRecordingFragment yfc =  new RecordForTrainingRecordingFragment();
                    yfc.setArguments(bundle);
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction ft = fragmentManager.beginTransaction();
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    ft.replace(R.id.container, yfc);
                    ft.addToBackStack(null);
                    ft.commit();
                }


            }
        }
    };

}
