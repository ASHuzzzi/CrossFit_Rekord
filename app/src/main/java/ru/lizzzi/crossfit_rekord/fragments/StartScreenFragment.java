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
import ru.lizzzi.crossfit_rekord.inspectionСlasses.Notification;
import ru.lizzzi.crossfit_rekord.interfaces.ChangeTitle;

public class StartScreenFragment extends Fragment {

    private LinearLayout linLayStartScreenDots;
    private PageAdapterStartScreenSlider adapterStartScreenSlider;
    private int numberOfPage = 0;
    private ViewPager viewPager;
    private Handler handlerStartScreen;
    private int delay = 2000; //milliseconds

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_start_screen, container, false);

        Button buttonSchedule = v.findViewById(R.id.button_schedule);
        Button buttonRecordTraining = v.findViewById(R.id.button_record_training);
        Button buttonDescription = v.findViewById(R.id.button_definition);
        Button buttonContacts = v.findViewById(R.id.button_contss);
        Button buttonCalendarWod = v.findViewById(R.id.button_calendar_wod);

        handlerStartScreen = new Handler();
        viewPager = v.findViewById(R.id.vpStart_Screen_Slider);
        linLayStartScreenDots = v.findViewById(R.id.llStart_Screen_Dots);

        addBottomDots(0);

        adapterStartScreenSlider = new PageAdapterStartScreenSlider(getChildFragmentManager());
        viewPager.setAdapter(adapterStartScreenSlider);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        buttonSchedule.setOnClickListener(new View.OnClickListener() {
            //@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                openFragment(TableFragment.class);
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
        return v;
    }

    private void openFragment(Class fragmentClass) {
        Fragment fragment = null;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.setCustomAnimations(R.anim.pull_in_right, R.anim.push_out_left, R.anim.pull_in_left, R.anim.push_out_right);
        ft.replace(R.id.container, fragment);
        ft.addToBackStack(null);
        ft.commit();
    }


    @Override
    public  void onStart() {
        super.onStart();
        if (getActivity() instanceof ChangeTitle){
            ChangeTitle listernerChangeTitle = (ChangeTitle) getActivity();
            listernerChangeTitle.changeTitle(R.string.app_name, R.string.app_name);
        }
    }

    public void onResume() {
        super.onResume();
            handlerStartScreen.postDelayed(runnable, delay);
        Notification notification = new Notification();
        notification.sendNotification(getActivity());
    }

    @Override
    public void onPause() {
        super.onPause();
        handlerStartScreen.removeCallbacks(runnable);
    }

    private void addBottomDots(int iCurrentPage) {
        TextView[] arrDots = new TextView[4];

        linLayStartScreenDots.removeAllViews();
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

    //  viewpager change listener
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

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
            if (adapterStartScreenSlider.getCount() == numberOfPage) {
                numberOfPage = 0;
            } else {
                numberOfPage++;
            }
            viewPager.setCurrentItem(numberOfPage, true);
            handlerStartScreen.postDelayed(this, delay);
        }
    };
}