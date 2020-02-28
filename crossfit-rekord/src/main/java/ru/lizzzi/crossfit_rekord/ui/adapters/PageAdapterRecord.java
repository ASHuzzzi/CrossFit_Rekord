package ru.lizzzi.crossfit_rekord.ui.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import ru.lizzzi.crossfit_rekord.R;
import ru.lizzzi.crossfit_rekord.ui.fragments.RecordForTrainingFragment;

public class PageAdapterRecord extends FragmentPagerAdapter {
    private Context context;

    public PageAdapterRecord(FragmentManager fragmentManager, Context context) {
        super(fragmentManager);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        int gym = (position == 0)
                ? context.getResources().getInteger(R.integer.selectSheduleParnas)
                : context.getResources().getInteger(R.integer.selectSheduleMyzhestvo);
        Bundle bundle = new Bundle();
        bundle.putInt("gym", gym);
        Fragment tableFragment =  new RecordForTrainingFragment();
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
