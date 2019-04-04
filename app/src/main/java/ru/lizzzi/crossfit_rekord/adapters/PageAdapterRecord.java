package ru.lizzzi.crossfit_rekord.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import ru.lizzzi.crossfit_rekord.R;
import ru.lizzzi.crossfit_rekord.fragments.RecordForTrainingTL.TL1ParnasFragment;
import ru.lizzzi.crossfit_rekord.fragments.RecordForTrainingTL.TL2MyzhestvoFragment;

public class PageAdapterRecord extends FragmentPagerAdapter {
    private Context mContext;

    public PageAdapterRecord(FragmentManager fm, Context context) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new TL1ParnasFragment();
        } else if (position == 1){
            return new TL2MyzhestvoFragment();
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
                return mContext.getString(R.string.gymNameParnas);
            case 1:
                return mContext.getString(R.string.gymNameMyzhestvo);
            default:
                return null;
        }
    }
}
