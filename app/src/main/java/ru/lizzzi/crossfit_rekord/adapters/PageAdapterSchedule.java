package ru.lizzzi.crossfit_rekord.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import ru.lizzzi.crossfit_rekord.R;
import ru.lizzzi.crossfit_rekord.fragments.TableTL.GymSheduleFragment;

public class PageAdapterSchedule extends FragmentPagerAdapter {
    private Context context;

    public PageAdapterSchedule(FragmentManager fragmentManager, Context context) {
        super(fragmentManager);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment gymScheduleFragment =  new GymSheduleFragment();
        Bundle bundle = new Bundle();
        int gym = context.getResources().getInteger(R.integer.selectSheduleParnas);
        if (position == 1) {
            gym = context.getResources().getInteger(R.integer.selectSheduleMyzhestvo);
        }
        bundle.putInt("gym", gym);
        gymScheduleFragment.setArguments(bundle);
        return gymScheduleFragment;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return context.getString(R.string.gymNameParnas);
            case 1:
                return context.getString(R.string.gymNameMyzhestvo);
            default:
                return null;
        }
    }
}
