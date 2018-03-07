package ru.lizzzi.crossfit_rekord;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class StartScreen_Fragment extends Fragment {

    public static final String APP_PREFERENCES = "audata";
    public static final String APP_PREFERENCES_OBJECTID = "ObjectId";
    SharedPreferences mSettings;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_start_screen, container, false);

        getContext();
        mSettings = getContext().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);


        Button subscription = v.findViewById(R.id.button_subscription);
        Button schedule = v.findViewById(R.id.button_schedule);
        Button record_training = v.findViewById(R.id.button_record_training);
        Button description = v.findViewById(R.id.button_definition);
        final Button contacts = v.findViewById(R.id.button_contacts);
        Button result = v.findViewById(R.id.button_result);

        subscription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TransactionFragment(Price_Fragment.class);

            }
        });

        schedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TransactionFragment(Table_Fragment.class);
            }
        });

        record_training.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean containtobjectid = mSettings.contains(APP_PREFERENCES_OBJECTID);
                if (containtobjectid) {

                    TransactionFragment(RecordForTraining_Fragment.class);

                }else {
                    TransactionFragment(Login_Fragment.class);
                }

            }
        });

        contacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        description.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TransactionFragment(Character_Fragment.class);
            }
        });

        result.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TransactionFragment(Result_Fragment.class);
            }
        });

        return v;
    }

    private void TransactionFragment(Class fragmentClass) {

        Fragment fragment = null;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.container, fragment);
        ft.addToBackStack(null);
        ft.commit();

    }
}