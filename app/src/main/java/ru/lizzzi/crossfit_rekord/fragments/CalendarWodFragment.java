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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

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
        layoutError.setVisibility(View.GONE);

        progressBar = view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

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
        Calendar calendar = GregorianCalendar.getInstance();
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
                String Day = (date.getDay() < 10) ? "0" + date.getDay() : String.valueOf(date.getDay());
                String Month = (date.getMonth() < 10)
                        ? "0" + (date.getMonth() + 1)
                        : String.valueOf((date.getMonth() + 1));

                String selectedDate = Month + "/" + Day + "/" + date.getYear();
                String dateForSave = Month + "/01/" + date.getYear();

                try {
                    SimpleDateFormat dateFormat =
                            new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
                    Date convertSelectDate = dateFormat.parse(selectedDate);
                    Date today = new GregorianCalendar().getTime();
                    if (convertSelectDate.getTime() <= today.getTime()) {
                        viewModel.saveDateInPrefs(selectedDate, dateForSave);
                        Bundle bundle = new Bundle();
                        bundle.putString("tag", selectedDate);
                        WorkoutDetailsFragment fragment = new WorkoutDetailsFragment();
                        fragment.setArguments(bundle);
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
                                Toast.LENGTH_SHORT).show();
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
        calendarView.setOnMonthChangedListener(new OnMonthChangedListener() {
            @Override
            public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
                int swipeMonth = date.getMonth();
                int checkMonth = viewModel.getMonth() - swipeMonth;
                if ((checkMonth == 2) || (checkMonth == -2) || (checkMonth == -10) || (checkMonth == 10)){
                    viewModel.monthChanged(date);
                    calendarView.setVisibility(View.INVISIBLE);
                    layoutError.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
                    getDates();
                }
            }
        });
        calendarView.setSaveEnabled(true);
        calendarView.setVisibility(View.INVISIBLE);
    }

    @Override
    public  void onStart() {
        super.onStart();
        if (layoutError.getVisibility() == View.GONE) {
            Date date = viewModel.getPeriodBoundaries();
            if (date != null) {
                calendarView.setCurrentDate(date);
            }

            calendarView.setVisibility(View.INVISIBLE);
            layoutError.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            getDates();
        }

        if (getActivity() instanceof ChangeTitle) {
            ChangeTitle listernerChangeTitle = (ChangeTitle) getActivity();
            listernerChangeTitle.changeTitle(R.string.title_CalendarWod_Fragment, R.string.title_CalendarWod_Fragment);
        }
    }

    public void getDates() {
        List<Date> selectDates = viewModel.getDates();

        if (selectDates.size() > 0) {
            showSelectedDates(selectDates);
        } else {
            if (viewModel.checkNetwork()) {
                layoutError.setVisibility(View.GONE);
                calendarView.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                LiveData<List<Date>> listLiveData = viewModel.loadDates();
                listLiveData.observe(CalendarWodFragment.this, new Observer<List<Date>>() {
                    @Override
                    public void onChanged(@Nullable List<Date> dates) {
                        if (dates != null) {
                            calendarView.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.INVISIBLE);
                            showSelectedDates(dates);
                        } else {
                            layoutError.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.INVISIBLE);
                            calendarView.setVisibility(View.INVISIBLE);
                            Toast.makeText(getContext(), "Нет данных", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            } else {
                layoutError.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
            }
        }
    }

    public void showSelectedDates(List<Date> dates) {
        viewModel.setSelectDates(dates);
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
