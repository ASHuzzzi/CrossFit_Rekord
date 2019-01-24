package ru.lizzzi.crossfit_rekord.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import ru.lizzzi.crossfit_rekord.R;
import ru.lizzzi.crossfit_rekord.fragments.WorkoutDetailsTL.TL1WodFragment;
import ru.lizzzi.crossfit_rekord.fragments.WorkoutDetailsTL.TL2ResultFragment;

public class PagerAdapterWorkoutDetails extends FragmentPagerAdapter {

    private Context mContext;

    public PagerAdapterWorkoutDetails(FragmentManager fm, Context context) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new TL1WodFragment();
        } else if (position == 1){
            return new TL2ResultFragment();
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
                return mContext.getString(R.string.strWorkoutDetailsWod);
            case 1:
                return mContext.getString(R.string.strWorkoutDetailsResult);
            default:
                return null;
        }
    }

}
