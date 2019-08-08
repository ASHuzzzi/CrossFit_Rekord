package ru.lizzzi.crossfit_rekord.fragments;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import ru.lizzzi.crossfit_rekord.R;
import ru.lizzzi.crossfit_rekord.interfaces.ChangeTitle;
import ru.lizzzi.crossfit_rekord.model.CalendarWodViewModel;

public class CalendarWodFragment extends Fragment {

    private LinearLayout layoutError;
    private MaterialCalendarView calendarView;
    private ProgressBar progressBar;
    private CalendarWodViewModel viewModel;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_calendar_wod, container, false);
        viewModel = ViewModelProviders.of(CalendarWodFragment.this)
                .get(CalendarWodViewModel.class);

        initCalendarView(view);

        layoutError = view.findViewById(R.id.layout_Error);
        progressBar = view.findViewById(R.id.progressBar);

        Button buttonError = view.findViewById(R.id.button_Error);
        buttonError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layoutError.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                getDates();
            }
        });
        return view;
    }

    private void initCalendarView(View rootView) {
        calendarView = rootView.findViewById(R.id.calendarView);
        Calendar calendar = Calendar.getInstance();
        int maximumDateYear = calendar.get(Calendar.YEAR);
        int maximumDateMonth = calendar.get(Calendar.MONTH);
        int maximumDateDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        calendarView.state().edit()
                .setMinimumDate(CalendarDay.from(2019, 0, 1))
                .setMaximumDate(CalendarDay.from(maximumDateYear, maximumDateMonth, maximumDateDay))
                .commit();
        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(
                    @NonNull MaterialCalendarView widget,
                    @NonNull CalendarDay date,
                    boolean selected) {
                calendarView.setDateSelected(date, false);
                Date selectedDate = date.getDate();
                if (viewModel.earlierThanToday(selectedDate)) {
                    viewModel.saveDateInPrefs(selectedDate);
                    WorkoutDetailsFragment fragment = new WorkoutDetailsFragment();
                    FragmentManager fragmentManager = getFragmentManager();
                    if (fragmentManager != null) {
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.setCustomAnimations(
                                R.anim.pull_in_right,
                                R.anim.push_out_left,
                                R.anim.pull_in_left,
                                R.anim.push_out_right);
                        fragmentTransaction.replace(R.id.container, fragment);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                    }
                } else {
                    for (int i = 0; i < viewModel.getSelectDatesSize(); i++) {
                        calendarView.setDateSelected(CalendarDay.from(
                                viewModel.getSelectDates().get(i)),
                                true);
                    }
                    Toast.makeText(
                            getContext(),
                            "Тренировки еще не было",
                            Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });
        calendarView.setOnMonthChangedListener(new OnMonthChangedListener() {
            @Override
            public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
                viewModel.monthChanged(date);
                getDates();
            }
        });
        calendarView.setSaveEnabled(true);
    }

    @Override
    public  void onStart() {
        super.onStart();
        Date date = viewModel.getDate();
        if (date != null) {
            calendarView.setCurrentDate(date);
        }

        if (viewModel.getSelectDates() == null) {
            getDates();
        }
        if (getActivity() instanceof ChangeTitle) {
            ChangeTitle listernerChangeTitle = (ChangeTitle) getActivity();
            listernerChangeTitle.changeTitle(R.string.title_CalendarWod_Fragment, R.string.title_CalendarWod_Fragment);
        }
    }

    private void getDates() {
        if (viewModel.getDates().isEmpty()) {
            layoutError.setVisibility(View.GONE);
            calendarView.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            if (viewModel.isNetworkConnection()) {
                final LiveData<List<Date>> listLiveData = viewModel.loadDates();
                listLiveData.observe(CalendarWodFragment.this, new Observer<List<Date>>() {
                    @Override
                    public void onChanged(@Nullable List<Date> dates) {
                        if (dates != null) {
                            viewModel.setSelectDates(dates);
                            showSelectedDates();
                        } else {
                            layoutError.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(getContext(), "Нет данных", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else {
                layoutError.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
            }
        } else {
            showSelectedDates();
        }
    }

    private void showSelectedDates() {
        for (int i = 0; i < viewModel.getSelectDatesSize(); i++) {
            calendarView.setDateSelected(
                    CalendarDay.from(viewModel.getSelectDates().get(i)),
                    true);
        }
        calendarView.setVisibility(View.VISIBLE);
        layoutError.setVisibility(View.GONE);
        progressBar.setVisibility(View.INVISIBLE);
    }
}
