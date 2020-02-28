package ru.lizzzi.crossfit_rekord.ui.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import ru.lizzzi.crossfit_rekord.ui.fragments.StartScreenSliderFragment;

public class PageAdapterStartScreenSlider extends FragmentPagerAdapter {

    public PageAdapterStartScreenSlider(FragmentManager fragmentManager){
        super(fragmentManager);
    }

    @Override
    public Fragment getItem(int position) {
        return StartScreenSliderFragment.newInstance(position);
    }

    @Override
    public int getCount() {
        return 4;
    }
}
