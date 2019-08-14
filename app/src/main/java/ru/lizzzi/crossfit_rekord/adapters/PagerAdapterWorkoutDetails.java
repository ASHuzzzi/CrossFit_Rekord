package ru.lizzzi.crossfit_rekord.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import ru.lizzzi.crossfit_rekord.R;
import ru.lizzzi.crossfit_rekord.fragments.WorkoutDetailsTL.TL1WodFragment;
import ru.lizzzi.crossfit_rekord.fragments.WorkoutDetailsTL.TL2ResultFragment;

public class PagerAdapterWorkoutDetails extends FragmentPagerAdapter {

    private Context context;

    public PagerAdapterWorkoutDetails(FragmentManager fragmentManager, Context context) {
        super(fragmentManager);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new TL1WodFragment();
            case 1:
                return new TL2ResultFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return context.getString(R.string.strWorkoutDetailsWod);
            case 1:
                return context.getString(R.string.strWorkoutDetailsResult);
            default:
                return null;
        }
    }
}
