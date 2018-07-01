package ru.lizzzi.crossfit_rekord.fragments;


import android.app.Activity;
import android.support.v4.app.LoaderManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import ru.lizzzi.crossfit_rekord.R;
import ru.lizzzi.crossfit_rekord.loaders.AboutMeLoader;

/**
 * Created by basso on 07.03.2018.
 */
//TODO см в хэлпе User properties. Сначала логин, от него юзер -> нужные параметры.
public class AboutMeFragment extends Fragment implements LoaderManager.LoaderCallbacks<Boolean> {

    private static final String APP_PREFERENCES = "audata";
    private static final String APP_PREFERENCES_OBJECTID = "ObjectId";
    private static final String APP_PREFERENCES_USERNAME = "Username";
    private static final String APP_PREFERENCES_USERSURNAME = "Usersurname";
    private static final String APP_PREFERENCES_CARDNUMBER = "cardNumber";
    private static final String APP_PREFERENCES_EMAIL = "Email";
    private static final String APP_PREFERENCES_PHONE = "Phone";
    SharedPreferences mSettings;

    private TextView tvCardNumber;
    private EditText etName;
    private EditText etSurname;
    private EditText etEmail;
    private EditText etPhone;
    private Button btChangeUserData;
    private ProgressBar pbAboutMe;

    private NetworkCheck NetworkCheck; //переменная для проврки сети

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_aboutme, container, false);

        mSettings = getContext().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        tvCardNumber = v.findViewById(R.id.tvCardNumber);
        etName = v.findViewById(R.id.etName);
        etSurname = v.findViewById(R.id.etSurname);
        etEmail = v.findViewById(R.id.etEmail);
        etPhone = v.findViewById(R.id.etPhone);
        btChangeUserData = v.findViewById(R.id.btnChangeUserData);
        pbAboutMe = v.findViewById(R.id.pbAboutMe);

        btChangeUserData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //убираем клавиатуру после нажатия на кнопку
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }

                NetworkCheck = new NetworkCheck(getContext());
                boolean resultCheck = NetworkCheck.checkInternet();
                if (resultCheck){
                    ChangeUIElements(1);
                    startAsyncTaskLoader(
                            mSettings.getString(APP_PREFERENCES_OBJECTID, ""),
                            tvCardNumber.getText().toString(),
                            etName.getText().toString(),
                            etSurname.getText().toString(),
                            etEmail.getText().toString(),
                            etPhone.getText().toString()
                    );

                }else {
                    ChangeUIElements(0);
                    Toast.makeText(getContext(), "Нет подключения", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        pbAboutMe.setVisibility(View.INVISIBLE);
        tvCardNumber.setText(mSettings.getString(APP_PREFERENCES_CARDNUMBER, ""));
        etName.setText(mSettings.getString(APP_PREFERENCES_USERNAME, ""));
        etSurname.setText(mSettings.getString(APP_PREFERENCES_USERSURNAME, ""));
        etEmail.setText(mSettings.getString(APP_PREFERENCES_EMAIL, ""));
        etPhone.setText(mSettings.getString(APP_PREFERENCES_PHONE, ""));
    }

    private void ChangeUIElements(int status){
        if (status == 1){
            pbAboutMe.setVisibility(View.VISIBLE);
            btChangeUserData.setPressed(true);
        }else{
            pbAboutMe.setVisibility(View.INVISIBLE);
            btChangeUserData.setPressed(false);
        }
    }

    private void startAsyncTaskLoader(String objectid, String carNumber, String name,
                                      String surname, String email, String phone) {
        Bundle bundle = new Bundle();
        bundle.putString("objectid", objectid);
        bundle.putString("cardNumber", carNumber);
        bundle.putString("name", name);
        bundle.putString("surname", surname);
        bundle.putString("email", email);
        bundle.putString("phone", phone);
        int LOADERID = 1;
        getLoaderManager().initLoader(LOADERID, bundle, this).forceLoad();
    }


    @Override
    public Loader<Boolean> onCreateLoader(int id, Bundle args) {
        AboutMeLoader loader;
        loader = new AboutMeLoader(getContext(), args);
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Boolean> loader, Boolean data) {
        if (data){
            SharedPreferences.Editor editor = mSettings.edit();
            editor.putString(APP_PREFERENCES_USERNAME, etName.getText().toString());
            editor.putString(APP_PREFERENCES_USERSURNAME, etSurname.getText().toString());
            editor.putString(APP_PREFERENCES_EMAIL, etEmail.getText().toString());
            editor.putString(APP_PREFERENCES_PHONE, etPhone.getText().toString());
            editor.apply();
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
