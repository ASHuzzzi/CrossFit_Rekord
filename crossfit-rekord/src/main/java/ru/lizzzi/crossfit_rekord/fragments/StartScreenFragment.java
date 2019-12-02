package ru.lizzzi.crossfit_rekord.fragments;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Objects;

import ru.lizzzi.crossfit_rekord.R;
import ru.lizzzi.crossfit_rekord.adapters.PageAdapterStartScreenSlider;
import ru.lizzzi.crossfit_rekord.interfaces.TitleChange;

public class StartScreenFragment extends Fragment {

    private LinearLayout linLayStartScreenDots;
    private PageAdapterStartScreenSlider adapterSlider;
    private int numberOfPage;
    private ViewPager viewPager;
    private Handler handlerStartScreen;
    private int DELAY_IN_MICROS = 2000;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((AppCompatActivity) requireActivity()).getSupportActionBar().show();
        View view = inflater.inflate(R.layout.fragment_start_screen, container, false);

        Button buttonSchedule = view.findViewById(R.id.buttonSchedule);
        Button buttonRecordTraining = view.findViewById(R.id.buttonRecordTraining);
        Button buttonDescription = view.findViewById(R.id.buttonDefinition);
        Button buttonContacts = view.findViewById(R.id.buttonContacts);
        Button buttonCalendarWod = view.findViewById(R.id.buttonCalendarWod);

        viewPager = view.findViewById(R.id.viewPagerSlider);
        linLayStartScreenDots = view.findViewById(R.id.linLayoutDots);

        adapterSlider = new PageAdapterStartScreenSlider(getChildFragmentManager());
        viewPager.setAdapter(adapterSlider);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        buttonSchedule.setOnClickListener(new View.OnClickListener() {
            //@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                openFragment(ScheduleFragment.class);
            }
        });

        buttonRecordTraining.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFragment(RecordForTrainingSelectFragment.class);
            }
        });

        buttonDescription.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                openFragment(CharacterFragment.class);
            }
        });

        buttonCalendarWod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFragment(CalendarWodFragment.class);
            }
        });

        buttonContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFragment(ContactsFragment.class);
            }
        });
        handlerStartScreen = new Handler();
        numberOfPage = 0;
        addBottomDots(0);
        return view;
    }

    private void openFragment(Class fragmentClass) {
        try {
            Fragment fragment = (Fragment) fragmentClass.newInstance();
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction;
            if (fragmentManager != null) {
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.setCustomAnimations(
                        R.anim.pull_in_right,
                        R.anim.push_out_left,
                        R.anim.pull_in_left,
                        R.anim.push_out_right);
                fragmentTransaction.replace(R.id.container, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public  void onStart() {
        super.onStart();
        if (getActivity() instanceof TitleChange) {
            TitleChange listernerTitleChange = (TitleChange) getActivity();
            listernerTitleChange.changeTitle(R.string.app_name, R.string.app_name);
        }
    }

    public void onResume() {
        super.onResume();
        handlerStartScreen.postDelayed(runnable, DELAY_IN_MICROS);
    }

    @Override
    public void onPause() {
        super.onPause();
        handlerStartScreen.removeCallbacks(runnable);
    }

    private void addBottomDots(int iCurrentPage) {
        linLayStartScreenDots.removeAllViews();
        TextView[] arrDots = new TextView[4];
        for (int i = 0; i < arrDots.length; i++) {
            arrDots[i] = new TextView(getContext());
            arrDots[i].setText(Html.fromHtml("&#8226;"));
            arrDots[i].setTextSize(40);
            arrDots[i].setTextColor(
                    ContextCompat.getColor(Objects.requireNonNull(getContext()), R.color.colorWhite));
            linLayStartScreenDots.addView(arrDots[i]);
        }
        arrDots[iCurrentPage].setTextColor(
                ContextCompat.getColor(Objects.requireNonNull(getContext()), R.color.colorRedPrimary));
    }

    ViewPager.OnPageChangeListener viewPagerPageChangeListener =
            new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            addBottomDots(position);
            handlerStartScreen.removeCallbacks(runnable);
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }
    };

    Runnable runnable = new Runnable() {
        public void run() {
            if (adapterSlider.getCount() == numberOfPage) {
                numberOfPage = 0;
            } else {
                numberOfPage++;
            }
            viewPager.setCurrentItem(numberOfPage, true);
            handlerStartScreen.postDelayed(this, DELAY_IN_MICROS);
        }
    };
}