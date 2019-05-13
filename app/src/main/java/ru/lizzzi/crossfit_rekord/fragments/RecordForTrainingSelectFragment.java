package ru.lizzzi.crossfit_rekord.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.lizzzi.crossfit_rekord.R;
import ru.lizzzi.crossfit_rekord.adapters.PageAdapterRecord;
import ru.lizzzi.crossfit_rekord.interfaces.ChangeTitle;


public class RecordForTrainingSelectFragment extends Fragment{

    @SuppressLint({"HandlerLeak", "ClickableViewAccessibility"})
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setRetainInstance(true);

        View v = inflater.inflate(R.layout.fragment_record_for_training_tl, container, false);

        // Find the view pager that will allow the user to swipe between fragments
        final ViewPager viewPager = v.findViewById(R.id.vp_1_record);

        // Create an adapter that knows which fragment should be shown on each page
        PageAdapterRecord adapter2 = new PageAdapterRecord(getChildFragmentManager(), getContext());

        // Set the adapter onto the view pager
        viewPager.setAdapter(adapter2);

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = v.findViewById(R.id.sliding_tabs_record);
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

        if (getActivity() instanceof ChangeTitle){
            ChangeTitle listernerChangeTitle = (ChangeTitle) getActivity();
            listernerChangeTitle.changeTitle(R.string.title_RecordForTraining_Fragment, R.string.title_RecordForTraining_Fragment);
        }

    }
}
