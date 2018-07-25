package ru.lizzzi.crossfit_rekord.fragments;

import android.annotation.SuppressLint;

import android.os.Bundle;
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

/**
 * Created by Liza on 13.03.2018.
 */

public class WorkoutDetailsFragment extends Fragment{





    private TextView tvSelectedDay;
    Bundle bundle;


    @SuppressLint("HandlerLeak")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setRetainInstance(true);

        View v = inflater.inflate(R.layout.fragment_workout_details, container, false);
        getActivity().setTitle("Результаты тренировки");
        tvSelectedDay = v.findViewById(R.id.tvSelectedDay);



        // Find the view pager that will allow the user to swipe between fragments
        ViewPager viewPager = (ViewPager) v.findViewById(R.id.vp_1);

        // Create an adapter that knows which fragment should be shown on each page
        PagerAdapterWorkoutDetails adapter2 = new PagerAdapterWorkoutDetails(this, getChildFragmentManager());

        // Set the adapter onto the view pager
        viewPager.setAdapter(adapter2);

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) v.findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);

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

        bundle = getArguments();
        final String ri = "07/09/2018";
        bundle.putString("Selected_day", ri);
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
}
