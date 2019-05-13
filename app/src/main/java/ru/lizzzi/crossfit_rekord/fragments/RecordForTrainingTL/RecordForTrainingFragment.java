package ru.lizzzi.crossfit_rekord.fragments.RecordForTrainingTL;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import ru.lizzzi.crossfit_rekord.R;
import ru.lizzzi.crossfit_rekord.adapters.RecyclerAdapterRecordForTrainingSelect;
import ru.lizzzi.crossfit_rekord.inspectionСlasses.ConstructorLinks;
import ru.lizzzi.crossfit_rekord.inspectionСlasses.Network;
import ru.lizzzi.crossfit_rekord.interfaces.ChangeTitle;
import ru.lizzzi.crossfit_rekord.interfaces.ListenerRecordForTrainingSelect;
import ru.lizzzi.crossfit_rekord.loaders.TableFragmentLoader;

public class RecordForTrainingFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<List<Map>>> {

    private LinearLayout linLayoutError;
    private LinearLayout linLauoutShedule;
    private RecyclerView recyclerViewShedule;
    private ProgressBar progressBar;

    private RecyclerAdapterRecordForTrainingSelect adapter;
    private GregorianCalendar numberDayOfWeek; // для преобразования выбранного дня в int

    private int dayOfWeekSelectedDay; // выбранный пользователем день
    private final static int LOADER_ID = 1; //идентефикатор loader'а

    private Handler handlerOpenFragment;
    private Thread threadOpenFragment;
    private Runnable runnableOpenFragment;

    private boolean todayOrNot;
    private List<List<Map>> schedule;
    private int selectDay;

    private Button buttontToday;
    private Button buttontTommorow;
    private Button buttontAftertommorow;

    private ImageView imageBackground;
    private int selectedGym;

    @SuppressLint({"HandlerLeak", "ClickableViewAccessibility"})
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_record_for_training_select, container, false);
        Objects.requireNonNull(getActivity()).setTitle(R.string.title_RecordForTraining_Fragment);

        buttontToday = v.findViewById(R.id.btToday);
        buttontTommorow = v.findViewById(R.id.btTommorow);
        buttontAftertommorow = v.findViewById(R.id.btAftertommorow);
        Button buttonError = v.findViewById(R.id.button6);
        recyclerViewShedule = v.findViewById(R.id.rvTrainingTime);
        linLayoutError = v.findViewById(R.id.llEror_RfTS);
        linLauoutShedule = v.findViewById(R.id.llListTime);
        progressBar = v.findViewById(R.id.pbRfTS);
        imageBackground = v.findViewById(R.id.iv_RfTS);

        numberDayOfWeek = new GregorianCalendar();

        Bundle bundle = getArguments();
        if (bundle != null) {
            selectedGym = bundle.getInt("gym");
        } else {
            selectedGym = Objects.requireNonNull(
                    getContext()).getResources().getInteger(R.integer.selectSheduleParnas);
        }

        handlerOpenFragment = new Handler() {
            @SuppressLint("ShowToast")
            @Override
            public void handleMessage(Message msg) {
                Bundle bundle = msg.getData();
                boolean resultCheck = bundle.getBoolean("open");
                if (resultCheck) {
                    linLayoutError.setVisibility(View.INVISIBLE);
                    progressBar.setVisibility(View.VISIBLE);
                    startAsyncTaskLoader();
                } else {
                    linLayoutError.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }
        };

        //поток запускаемый при создании экрана (запуск происходит из onStart)
        runnableOpenFragment = new Runnable() {
            @Override
            public void run() {
                Network network = new Network(getContext());
                Bundle bundle = new Bundle();
                boolean checkDone = network.checkConnection();
                if (checkDone) {
                    dayOfWeekSelectedDay = numberDayOfWeek.get(Calendar.DAY_OF_WEEK)-1;
                    if (dayOfWeekSelectedDay == 0){
                        dayOfWeekSelectedDay = 7;
                    }
                    todayOrNot = true;
                    selectDay = 0;
                    bundle.putBoolean("open", true);

                } else {
                    bundle.putBoolean("open", false);
                }
                Message msg = handlerOpenFragment.obtainMessage();
                msg.setData(bundle);
                handlerOpenFragment.sendMessage(msg);
            }
        };

        //получаю значения для кнопок
        final Date today = new Date();
        final GregorianCalendar gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.add(Calendar.DAY_OF_YEAR, 1);
        final Date tomorrow = gregorianCalendar.getTime();
        gregorianCalendar.add(Calendar.DAY_OF_YEAR, 1);
        final Date aftertomorrow = gregorianCalendar.getTime();

        @SuppressLint("SimpleDateFormat")
        final SimpleDateFormat sdf = new SimpleDateFormat("EEE.\n d MMMM");
        final String currentToday = sdf.format(today);
        final String currentTomorrow = sdf.format(tomorrow);
        final String currentAftertommorow = sdf.format(aftertomorrow);

        buttontToday.setText(currentToday);
        buttontTommorow.setText(currentTomorrow);
        buttontAftertommorow.setText(currentAftertommorow);

        buttonError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gregorianCalendar.setTime(today);
                linLayoutError.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                threadOpenFragment = new Thread(runnableOpenFragment);
                threadOpenFragment.setDaemon(true);
                threadOpenFragment.start();
            }
        });

        buttontToday.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (adapter != null) {
                    gregorianCalendar.setTime(today);
                    dayOfWeekSelectedDay = numberDayOfWeek.get(Calendar.DAY_OF_WEEK)-1;
                    if (dayOfWeekSelectedDay == 0) {
                        dayOfWeekSelectedDay = 7;
                    }
                    todayOrNot = true;
                    selectDay = 0;
                    drawList(schedule.get(dayOfWeekSelectedDay -1));
                }
                return true ;
            }
        });

        buttontTommorow.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (adapter != null) {
                    gregorianCalendar.setTime(tomorrow);
                    dayOfWeekSelectedDay = numberDayOfWeek.get(Calendar.DAY_OF_WEEK);
                    todayOrNot = false;
                    selectDay = 1;
                    drawList(schedule.get(dayOfWeekSelectedDay -1));
                }
                return true ;
            }
        });

        buttontAftertommorow.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (adapter != null) {
                    gregorianCalendar.setTime(aftertomorrow);
                    dayOfWeekSelectedDay = numberDayOfWeek.get(Calendar.DAY_OF_WEEK)+1;
                    if (dayOfWeekSelectedDay == 8) {
                        dayOfWeekSelectedDay = 1;
                    }
                    todayOrNot = false;
                    selectDay = 2;
                    drawList(schedule.get(dayOfWeekSelectedDay -1));
                }
                return true ;
            }
        });
        return v;
    }

    private void startAsyncTaskLoader() {
        Bundle bundle = new Bundle();
        bundle.putString("SelectedGym", String.valueOf(selectedGym));
        getLoaderManager().restartLoader(LOADER_ID, bundle, this).forceLoad();
    }

    @NonNull
    @Override
    public Loader<List<List<Map>>> onCreateLoader(int id, Bundle args) {
        Loader<List<List<Map>>> loader;
        loader = new TableFragmentLoader(getContext(), args);
        return loader;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<List<Map>>> loader, List<List<Map>> data) {
        if (data != null) {
            schedule = data;
            drawList(schedule.get(dayOfWeekSelectedDay -1));

            linLayoutError.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
            linLauoutShedule.setVisibility(View.VISIBLE);
        } else {
            linLayoutError.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
            linLauoutShedule.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<List<Map>>> loader) {
    }

    private void drawList(List<Map> dailySchedule){
        adapter = new RecyclerAdapterRecordForTrainingSelect(getContext(), dailySchedule,
                todayOrNot, new ListenerRecordForTrainingSelect() {
            @Override
            public void selectTime(String startTime, String typesItem) {
                if (startTime.equals("outTime") && typesItem.equals("outTime")) {
                    Toast toast = Toast.makeText(getContext(), "Тренировка уже прошла. Выбери более позднее время!", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                } else {
                    ConstructorLinks constructorLinks = new ConstructorLinks();
                    String stOpenURL = constructorLinks.constructorLinks(selectedGym,selectDay, startTime, typesItem);
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.addCategory(Intent.CATEGORY_BROWSABLE);
                    intent.setData(Uri.parse(stOpenURL));
                    startActivity(intent);
                    Objects.requireNonNull(getActivity()).overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
                }
            }
        });

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerViewShedule.setLayoutManager(mLayoutManager);
        recyclerViewShedule.setAdapter(adapter);
        preSelectionButtonDay(selectDay);
    }

    private void preSelectionButtonDay(int selectedDay){
        switch (selectedDay) {
            case 0:
                selectButtonDay(true, false, false);
                break;
            case 1:
                selectButtonDay(false, true, false);
                break;
            case 2:
                selectButtonDay(false, false, true);
                break;
        }
    }

    //метод применяющий выбор кнопок
    private void selectButtonDay(boolean today, boolean tommorow, boolean aftertommorow) {
        buttontToday.setPressed(today);
        buttontTommorow.setPressed(tommorow);
        buttontAftertommorow.setPressed(aftertommorow);
    }

    //в onStart делаем проверку на наличие данных в адаптаре. При первом запуске адаптер пустой и
    //будет запущен поток.
    //при возврате через кнопку back адаптер будет не пустым поток не запуститься. что сохранит
    //состояние адаптера в положении перед открытием нового фрагмента
    @Override
    public  void onStart() {
        super.onStart();
        if (adapter == null) {
            linLauoutShedule.setVisibility(View.INVISIBLE);
            linLayoutError.setVisibility(View.INVISIBLE);
            threadOpenFragment = new Thread(runnableOpenFragment);
            threadOpenFragment.setDaemon(true);
            threadOpenFragment.start();
        } else {
            preSelectionButtonDay(selectDay);
            linLauoutShedule.setVisibility(View.VISIBLE);
        }

        if (getActivity() instanceof ChangeTitle) {
            ChangeTitle listernerChangeTitle = (ChangeTitle) getActivity();
            listernerChangeTitle.changeTitle(R.string.title_RecordForTraining_Fragment, R.string.title_RecordForTraining_Fragment);
        }
        int backgroungImage;
        if (selectedGym == getResources().getInteger(R.integer.selectSheduleParnas)) {
            backgroungImage = R.drawable.backgroundfotovrtical;
        } else {
            backgroungImage = R.drawable.backgroundfotovrtical2;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            imageBackground.setImageDrawable(getResources().getDrawable(
                    backgroungImage, Objects.requireNonNull(getContext()).getTheme()));
        } else {
            imageBackground.setImageDrawable(getResources().getDrawable(backgroungImage));
        }
    }

    public void onStop() {
        super.onStop();
        if (getLoaderManager().hasRunningLoaders()) {
            getLoaderManager().destroyLoader(LOADER_ID);
        }
    }
}