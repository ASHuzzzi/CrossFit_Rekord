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
import ru.lizzzi.crossfit_rekord.interfaces.InterfaceChangeTitle;

public class StartScreenFragment extends Fragment {

    private LinearLayout llStartScreenDots;
    private PageAdapterStartScreenSlider vpAdapterStartScrenSlider;
    private int iNumberOfPage = 0;
    private ViewPager vpStartScreenSlider;
    private Handler handlerStartScreen;
    private int iDelay = 2000; //milliseconds

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_start_screen, container, false);

        Button btSchedule = v.findViewById(R.id.button_schedule);
        Button btRecordTraining = v.findViewById(R.id.button_record_training);
        Button btDescription = v.findViewById(R.id.button_definition);
        Button btContactsSS = v.findViewById(R.id.button_contss);
        Button btCalendarWod = v.findViewById(R.id.button_calendar_wod);

        handlerStartScreen = new Handler();
        vpStartScreenSlider = v.findViewById(R.id.vpStart_Screen_Slider);
        llStartScreenDots = v.findViewById(R.id.llStart_Screen_Dots);

        //добавляем точки
        addBottomDots(0);

        vpAdapterStartScrenSlider = new PageAdapterStartScreenSlider(getChildFragmentManager());
        vpStartScreenSlider.setAdapter(vpAdapterStartScrenSlider);
        vpStartScreenSlider.addOnPageChangeListener(viewPagerPageChangeListener);



        btSchedule.setOnClickListener(new View.OnClickListener() {
            //@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                openFragment(TableFragment.class);
            }
        });

        btRecordTraining.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFragment(RecordForTrainingSelectFragment.class);
            }
        });

        btDescription.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                openFragment(CharacterFragment.class);
            }
        });

        btCalendarWod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFragment(CalendarWodFragment.class);
            }
        });

        btContactsSS.setOnClickListener(new View.OnClickListener() {
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
        ft.setCustomAnimations(R.anim.enter_from_down, R.anim.exit_to_up);
        ft.replace(R.id.container, fragment);
        ft.addToBackStack(null);
        ft.commit();

    }


    @Override
    public  void onStart() {
        super.onStart();
        if (getActivity() instanceof InterfaceChangeTitle){
            InterfaceChangeTitle listernerChangeTitle = (InterfaceChangeTitle) getActivity();
            listernerChangeTitle.changeTitle(R.string.app_name, R.string.app_name);
        }

    }
    public void onResume() {
        super.onResume();
            handlerStartScreen.postDelayed(runnable, iDelay);
    }

    @Override
    public void onPause() {
        super.onPause();
        handlerStartScreen.removeCallbacks(runnable);
    }

    //метод рисующий точки
    private void addBottomDots(int iCurrentPage) {
        TextView[] arrDots = new TextView[4];

        llStartScreenDots.removeAllViews();
        for (int i = 0; i < arrDots.length; i++) {
            arrDots[i] = new TextView(getContext());
            arrDots[i].setText(Html.fromHtml("&#8226;"));
            arrDots[i].setTextSize(40);
            arrDots[i].setTextColor(
                    ContextCompat.getColor(Objects.requireNonNull(getContext()), R.color.colorWhite));
            llStartScreenDots.addView(arrDots[i]);
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
            if (vpAdapterStartScrenSlider.getCount() == iNumberOfPage) {
                iNumberOfPage = 0;
            } else {
                iNumberOfPage++;
            }
            vpStartScreenSlider.setCurrentItem(iNumberOfPage, true);
            handlerStartScreen.postDelayed(this, iDelay);
        }
    };

}