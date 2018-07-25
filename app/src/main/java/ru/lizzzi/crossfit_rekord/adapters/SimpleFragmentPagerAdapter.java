package ru.lizzzi.crossfit_rekord.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import ru.lizzzi.crossfit_rekord.TL_1_Fragment;
import ru.lizzzi.crossfit_rekord.fragments.WorkoutDetailsFragment;
import ru.lizzzi.crossfit_rekord.TL_2_Fragment;

public class SimpleFragmentPagerAdapter extends FragmentPagerAdapter {

    public SimpleFragmentPagerAdapter(WorkoutDetailsFragment context, FragmentManager fm) {
        super(fm);
        WorkoutDetailsFragment mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new TL_1_Fragment();
        } else if (position == 1){
            return new TL_2_Fragment();
        } else {
            return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    // This determines the title for each tab
    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        switch (position) {
            case 0:
                return "WoD"; //mContext.getString(R.string.category_usefulinfo);
            case 1:
                return "Результаты";//mContext.getString(R.string.category_places);
            default:
                return null;
        }
    }

}
