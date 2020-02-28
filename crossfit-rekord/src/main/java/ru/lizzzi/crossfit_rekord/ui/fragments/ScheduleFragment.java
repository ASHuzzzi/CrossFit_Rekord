package ru.lizzzi.crossfit_rekord.ui.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.lizzzi.crossfit_rekord.R;
import ru.lizzzi.crossfit_rekord.ui.adapters.PageAdapterSchedule;
import ru.lizzzi.crossfit_rekord.interfaces.TitleChange;

public class ScheduleFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        setRetainInstance(true);
        View view = inflater.inflate(R.layout.fragment_tl_table, container, false);

        final ViewPager viewPager = view.findViewById(R.id.vp_1_record);
        PageAdapterSchedule adapterSchedule =
                new PageAdapterSchedule(getChildFragmentManager(), getContext());
        viewPager.setAdapter(adapterSchedule);
        TabLayout tabLayout = view.findViewById(R.id.sliding_tabs_record);
        tabLayout.setupWithViewPager(viewPager);
        return view;
    }

    @Override
    public  void onStart() {
        super.onStart();
        TitleChange listenerTitleChange = (TitleChange) getActivity();
        if (listenerTitleChange != null) {
            listenerTitleChange.changeTitle(
                    R.string.title_Table_Fragment,
                    R.string.title_Table_Fragment);
        }

    }
}
