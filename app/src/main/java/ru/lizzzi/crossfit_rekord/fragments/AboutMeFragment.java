package ru.lizzzi.crossfit_rekord.fragments;


import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.content.SharedPreferences;
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

import ru.lizzzi.crossfit_rekord.R;

/**
 * Created by basso on 07.03.2018.
 */
//TODO см в хэлпе User properties. Сначала логин, от него юзер -> нужные параметры.
public class AboutMeFragment extends Fragment implements LoaderManager.LoaderCallbacks<Void> {

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

    @Override
    public Loader<Void> onCreateLoader(int i, Bundle bundle) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Void> loader, Void aVoid) {

    }

    @Override
    public void onLoaderReset(Loader<Void> loader) {

    }
}
