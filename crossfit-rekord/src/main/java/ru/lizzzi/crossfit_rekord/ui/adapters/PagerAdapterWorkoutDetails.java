package ru.lizzzi.crossfit_rekord.ui.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import ru.lizzzi.crossfit_rekord.R;
import ru.lizzzi.crossfit_rekord.ui.fragments.WorkoutExerciseFragment;
import ru.lizzzi.crossfit_rekord.ui.fragments.WodResultFragment;

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
                return new WorkoutExerciseFragment();
            case 1:
                return new WodResultFragment();
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
                return "Тренировка";
            case 1:
                return context.getString(R.string.strWorkoutDetailsResult);
            default:
                return null;
        }
    }
}
