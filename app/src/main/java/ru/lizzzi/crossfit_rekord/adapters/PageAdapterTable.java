package ru.lizzzi.crossfit_rekord.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import ru.lizzzi.crossfit_rekord.fragments.TableTL.TL1ParnasFragment;
import ru.lizzzi.crossfit_rekord.fragments.TableTL.TL2MyzhestvoFragment;

public class PageAdapterTable extends FragmentPagerAdapter {

    public PageAdapterTable(FragmentManager fm) {
        super(fm);
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
                return "Парнас"; //mContext.getString(R.string.category_usefulinfo);
            case 1:
                return "Мужество";//mContext.getString(R.string.category_places);
            default:
                return null;
        }
    }
}
