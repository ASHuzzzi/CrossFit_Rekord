package ru.lizzzi.crossfit_rekord.fragments;

import android.annotation.SuppressLint;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import ru.lizzzi.crossfit_rekord.R;
import ru.lizzzi.crossfit_rekord.adapters.PagerAdapterWorkoutDetails;
import ru.lizzzi.crossfit_rekord.interfaces.InterfaceChangeTitle;

/**
 * Created by Liza on 13.03.2018.
 */

public class WorkoutDetailsFragment extends Fragment{

    private static final String APP_PREFERENCES = "audata";
    private static final String APP_PREFERENCES_SELECTEDDAY = "SelectedDay";

    private TextView tvSelectedDay;

    @SuppressLint("HandlerLeak")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setRetainInstance(true);

        View v = inflater.inflate(R.layout.fragment_workout_details, container, false);
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
    public  void onStart() {
        super.onStart();
        if (getActivity() instanceof InterfaceChangeTitle){
            InterfaceChangeTitle listernerChangeTitle = (InterfaceChangeTitle) getActivity();
            listernerChangeTitle.changeTitle(R.string.title_WorkoutDetails_Fragment, R.string.title_CalendarWod_Fragment);
        }

    }


    @Override
    public void onResume() {
        super.onResume();

        try {
            SharedPreferences mSettings = getContext().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
            String ri =  mSettings.getString(APP_PREFERENCES_SELECTEDDAY, "");
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
            Date ddd = sdf.parse(ri);
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf2 = new SimpleDateFormat("EEEE dd MMMM");
            String sdsd = sdf2.format(ddd);
            tvSelectedDay.setText(sdsd);
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }
}
