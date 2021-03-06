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
import ru.lizzzi.crossfit_rekord.adapters.PageAdapterRecord;
import ru.lizzzi.crossfit_rekord.interfaces.TitleChange;

public class RecordForTrainingSelectFragment extends Fragment {

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {
        setRetainInstance(true);

        View view = inflater.inflate(
                R.layout.fragment_record_for_training_tl,
                container,
                false);

        final ViewPager viewPager = view.findViewById(R.id.vp_1_record);
        PageAdapterRecord adapterRecord =
                new PageAdapterRecord(getChildFragmentManager(), getContext());
        viewPager.setAdapter(adapterRecord);
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
                    R.string.title_RecordForTraining_Fragment,
                    R.string.title_RecordForTraining_Fragment);
        }
    }
}
