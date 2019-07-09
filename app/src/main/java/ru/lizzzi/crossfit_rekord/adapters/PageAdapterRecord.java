package ru.lizzzi.crossfit_rekord.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import ru.lizzzi.crossfit_rekord.R;
import ru.lizzzi.crossfit_rekord.fragments.RecordForTrainingTL.RecordForTrainingFragment;

public class PageAdapterRecord extends FragmentPagerAdapter {
    private Context context;

    public PageAdapterRecord(FragmentManager fragmentManager, Context context) {
        super(fragmentManager);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment tableFragment =  new RecordForTrainingFragment();
        Bundle bundle = new Bundle();
        int gym = context.getResources().getInteger(R.integer.selectSheduleParnas);
        if (position == 1) {
            gym = context.getResources().getInteger(R.integer.selectSheduleMyzhestvo);
        }
        bundle.putInt("gym", gym);
        tableFragment.setArguments(bundle);
        return tableFragment;
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
