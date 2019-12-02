package ru.lizzzi.crossfit_rekord.fragments;

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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import ru.lizzzi.crossfit_rekord.R;
import ru.lizzzi.crossfit_rekord.adapters.PagerAdapterWorkoutDetails;
import ru.lizzzi.crossfit_rekord.interfaces.TitleChange;

/**
 * Created by Liza on 13.03.2018.
 */

public class WorkoutDetailsFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        setRetainInstance(true);

        View view = inflater.inflate(R.layout.fragment_workout_details, container, false);

        // Find the view pager that will allow the user to swipe between fragments
        final ViewPager viewPager = view.findViewById(R.id.vp_1);

        // Create an adapter that knows which fragment should be shown on each page
        PagerAdapterWorkoutDetails pageAdapter =
                new PagerAdapterWorkoutDetails(getChildFragmentManager(), getContext());

        // Set the adapter onto the view pager
        viewPager.setAdapter(pageAdapter);

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = view.findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);

        return view;
    }

    @Override
    public  void onStart() {
        super.onStart();
        TitleChange listenerTitleChange = (TitleChange) getActivity();
        if (listenerTitleChange != null) {
            listenerTitleChange.changeTitle(
                    R.string.title_WorkoutDetails_Fragment,
                    R.string.title_CalendarWod_Fragment);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        try {
            String APP_PREFERENCES = "audata";
            String APP_PREFERENCES_SELECTEDDAY = "SelectedDay";
            SharedPreferences sharedPreferences =
                    requireContext().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
            String savedDate =  sharedPreferences.getString(APP_PREFERENCES_SELECTEDDAY, "");
            SimpleDateFormat parseFormat =
                    new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
            Date parsedDate = parseFormat.parse(savedDate);
            SimpleDateFormat titleFormat =
                    new SimpleDateFormat("d MMMM (EEEE)", Locale.getDefault());
            String dateForTitle = titleFormat.format(parsedDate);
            requireActivity().setTitle(dateForTitle);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
