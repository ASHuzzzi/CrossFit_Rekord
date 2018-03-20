package ru.lizzzi.crossfit_rekord.fragments;

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

import ru.lizzzi.crossfit_rekord.R;

public class StartScreen_Fragment extends Fragment {

    SharedPreferences mSettings;
    public static final String APP_PREFERENCES = "audata";
    public static final String APP_PREFERENCES_OBJECTID = "ObjectId";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_start_screen, container, false);

        Button schedule = v.findViewById(R.id.button_schedule);
        Button record_training = v.findViewById(R.id.button_record_training);
        Button description = v.findViewById(R.id.button_definition);
        Button calendar_wod = v.findViewById(R.id.button_calendar_wod);
        final Button contacts = v.findViewById(R.id.button_contacts);

        schedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenFragment(Table_Fragment.class);
            }
        });

        record_training.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getContext();
                mSettings = getContext().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
                boolean containtobjectid = mSettings.contains(APP_PREFERENCES_OBJECTID);
                if (containtobjectid) {
                    OpenFragment(RecordForTraining_Fragment.class);

                }else {
                    OpenFragment(Login_Fragment.class);
                }

            }
        });

        description.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenFragment(Character_Fragment.class);
            }
        });

        calendar_wod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenFragment(Calendar_wod_Fragment.class);
            }
        });

        contacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        return v;
    }

    private void OpenFragment(Class fragmentClass) {

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