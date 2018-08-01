package ru.lizzzi.crossfit_rekord.fragments;

import android.annotation.SuppressLint;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import ru.lizzzi.crossfit_rekord.R;
import ru.lizzzi.crossfit_rekord.adapters.PagerAdapterWorkoutDetails;

/**
 * Created by Liza on 13.03.2018.
 */

public class WorkoutDetailsFragment extends Fragment{

    private static final String APP_PREFERENCES = "audata";
    private static final String APP_PREFERENCES_SELECTEDDAY = "SelectedDay";

    private TextView tvSelectedDay;


    @SuppressLint("HandlerLeak")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setRetainInstance(true);

        View v = inflater.inflate(R.layout.fragment_workout_details, container, false);
        getActivity().setTitle("Результаты тренировки");
        tvSelectedDay = v.findViewById(R.id.tvSelectedDay);



        // Find the view pager that will allow the user to swipe between fragments
        final ViewPager viewPager = v.findViewById(R.id.vp_1);

        // Create an adapter that knows which fragment should be shown on each page
        PagerAdapterWorkoutDetails adapter2 = new PagerAdapterWorkoutDetails(this, getChildFragmentManager());

        // Set the adapter onto the view pager
        viewPager.setAdapter(adapter2);

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = v.findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 1) {
                    //buttonEnterReult.setVisibility(View.VISIBLE);
                }else{
                    //buttonEnterReult.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });




        return v;
    }


    @Override
    public void onResume() {
        super.onResume();

        //bundle = getArguments();
        //final String ri = bundle.getString("tag");
        //bundle.putString("Selected_day", ri);
        /*if (adapter == null){
            threadOpenFragment.start();
        }*/

        SharedPreferences mSettings = getContext().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        final String ri =  mSettings.getString(APP_PREFERENCES_SELECTEDDAY, "");
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

        Date ddd = null;
        try {
            ddd = sdf.parse(ri);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf2 = new SimpleDateFormat("EEEE dd MMMM");
        String sdsd = sdf2.format(ddd);
        tvSelectedDay.setText(sdsd);
    }

    public void onPause(){
        super.onPause();
    }

    public void Test(){
        Fragment fragment = null;
        Class fragmentClass = EnterResultFragment.class;
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
}
