package ru.lizzzi.crossfit_rekord.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import ru.lizzzi.crossfit_rekord.fragments.StartScreenSliderFragment;

public class PageAdapterStartScreenSlider extends FragmentPagerAdapter {

    public PageAdapterStartScreenSlider(FragmentManager fm){
        super(fm);
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
