package ru.lizzzi.crossfit_rekord.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.lizzzi.crossfit_rekord.R;
import ru.lizzzi.crossfit_rekord.adapters.PageAdapterSchedule;
import ru.lizzzi.crossfit_rekord.interfaces.ChangeTitle;

public class ScheduleFragment extends Fragment{

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setRetainInstance(true);

        View view = inflater.inflate(R.layout.fragment_tl_table, container, false);
        // Find the view pager that will allow the user to swipe between fragments
        final ViewPager viewPager = view.findViewById(R.id.vp_1_record);

        // Create an adapter that knows which fragment should be shown on each page
        PageAdapterSchedule adapterSchedule =
                new PageAdapterSchedule(getChildFragmentManager(), getContext());

        // Set the adapter onto the view pager
        viewPager.setAdapter(adapterSchedule);

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = view.findViewById(R.id.sliding_tabs_record);
        tabLayout.setupWithViewPager(viewPager);
        return view;
    }

    @Override
    public  void onStart() {
        super.onStart();
        if (getActivity() instanceof ChangeTitle){
            ChangeTitle listernerChangeTitle = (ChangeTitle) getActivity();
            listernerChangeTitle.changeTitle(R.string.title_Table_Fragment, R.string.title_Table_Fragment);
        }

    }
}