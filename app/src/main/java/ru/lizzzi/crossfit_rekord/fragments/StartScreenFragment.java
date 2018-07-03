package ru.lizzzi.crossfit_rekord.fragments;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import ru.lizzzi.crossfit_rekord.R;

public class StartScreenFragment extends Fragment {

    private CheckAuthData checkAuthData = new CheckAuthData();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_start_screen, container, false);
        getActivity().setTitle(R.string.app_name);

        Button schedule = v.findViewById(R.id.button_schedule);
        Button recordTraining = v.findViewById(R.id.button_record_training);
        final Button description = v.findViewById(R.id.button_definition);
        Button calendarWod = v.findViewById(R.id.button_calendar_wod);
        final Button contacts = v.findViewById(R.id.button_contacts);

        schedule.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {

                openFragment(TableFragment.class);
            }
        });

        recordTraining.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (checkAuthData.checkAuthData(getContext())){
                    openFragment(RecordForTrainingSelectFragment.class);
                }else {
                    LoginFragment yfc = new LoginFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("fragment", String.valueOf(R.string.strRecordFragment));
                    yfc.setArguments(bundle);
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction ft = fragmentManager.beginTransaction();
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    ft.replace(R.id.container, yfc);
                    ft.addToBackStack(null);
                    ft.commit();
                }
            }
        });

        description.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                openFragment(CharacterFragment.class);
            }
        });

        calendarWod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFragment(CalendarWodFragment.class);
            }
        });

        contacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        return v;
    }

    private void openFragment(Class fragmentClass) {

        Fragment fragment = null;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.replace(R.id.container, fragment);
        ft.addToBackStack(null);
        ft.commit();

    }
    public void onResume() {

        super.onResume();

    }
}