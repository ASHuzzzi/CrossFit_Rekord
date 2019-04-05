package ru.lizzzi.crossfit_rekord.fragments.TableTL;

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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import ru.lizzzi.crossfit_rekord.R;
import ru.lizzzi.crossfit_rekord.adapters.RecyclerAdapterTable;
import ru.lizzzi.crossfit_rekord.inspectionСlasses.ConstructorLinks;
import ru.lizzzi.crossfit_rekord.inspectionСlasses.NetworkCheck;
import ru.lizzzi.crossfit_rekord.interfaces.InterfaceChangeTitle;
import ru.lizzzi.crossfit_rekord.interfaces.ListenerRecordForTrainingSelect;
import ru.lizzzi.crossfit_rekord.loaders.TableFragmentLoader;

public class TableFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<List<Map>>> {
    private ProgressBar progressBar;
    private RecyclerView itemsInTable;
    private Button buttonMonday;
    private Button buttonTuesday;
    private Button buttonWednesday;
    private Button buttonThursday;
    private Button buttonFriday;
    private Button buttonSaturday;
    private Button buttonSunday;
    private LinearLayout layoutError;
    private ImageView imageTable;

    private int selectDay; // выбранный пользователем день

    private Handler handlerOpenFragment;
    private Thread threadOpenFragment;

    private NetworkCheck networkCheck; //переменная для проврки сети

    private RecyclerAdapterTable adapter; //адаптер для списка тренировок

    private List<List<Map>> schedule;
    private Runnable runnableOpenFragment;

    private int iSelectGym;

    @SuppressLint({"HandlerLeak", "ClickableViewAccessibility"})
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_table, container, false);

        buttonMonday = v.findViewById(R.id.day_1);
        buttonTuesday = v.findViewById(R.id.day_2);
        buttonWednesday = v.findViewById(R.id.day_3);
        buttonThursday = v.findViewById(R.id.day_4);
        buttonFriday = v.findViewById(R.id.day_5);
        buttonSaturday = v.findViewById(R.id.day_6);
        buttonSunday = v.findViewById(R.id.day_7);
        Button buttonError = v.findViewById(R.id.button5);
        layoutError = v.findViewById(R.id.Layout_Error);
        progressBar = v.findViewById(R.id.progressBar);
        itemsInTable = v.findViewById(R.id.lvTable);
        imageTable = v.findViewById(R.id.ivTable);

        layoutError.setVisibility(View.INVISIBLE);
        itemsInTable.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);

        Bundle bundle = getArguments();
        if (bundle != null) {
            iSelectGym = bundle.getInt("gym");
        } else {
            iSelectGym = Objects.requireNonNull(
                    getContext()).getResources().getInteger(R.integer.selectSheduleParnas);
        }


        //хэндлер для потока runnableOpenFragment
        handlerOpenFragment = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Bundle bundle = msg.getData();
                boolean checkDone = bundle.getBoolean("result");
                if (checkDone) {
                    layoutError.setVisibility(View.INVISIBLE);
                    progressBar.setVisibility(View.VISIBLE);
                    firstStartAsyncTaskLoader();
                } else {
                    layoutError.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }
        };

        //поток запускаемый при создании экрана (запуск происходит из onStart)
        runnableOpenFragment = new Runnable() {
            @Override
            public void run() {

                networkCheck = new NetworkCheck(getContext());
                boolean checkDone = networkCheck.checkInternet();
                Bundle bundle = new Bundle();
                if (checkDone) {
                    selectDay = Calendar.MONDAY;
                    bundle.putBoolean("result", true);
                } else {
                    bundle.putBoolean("result", false);
                }
                Message msg = handlerOpenFragment.obtainMessage();
                msg.setData(bundle);
                handlerOpenFragment.sendMessage(msg);
            }
        };


        buttonError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                layoutError.setVisibility(View.INVISIBLE);
                threadOpenFragment = new Thread(runnableOpenFragment);
                threadOpenFragment.setDaemon(true);
                threadOpenFragment.start();
            }
        });

        buttonMonday.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (adapter != null) {
                    selectDay = Calendar.MONDAY;
                    createList(schedule.get(selectDay -2));
                }
                return true ;
            }
        });

        buttonTuesday.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (adapter != null) {
                    selectDay = Calendar.TUESDAY;
                    createList(schedule.get(selectDay -2));
                }
                return true ;
            }
        });

        buttonWednesday.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (adapter != null) {
                    selectDay = Calendar.WEDNESDAY;
                    createList(schedule.get(selectDay -2));
                }
                return true ;
            }
        });

        buttonThursday.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (adapter != null) {
                    selectDay = Calendar.THURSDAY;
                    createList(schedule.get(selectDay -2));
                }
                return true ;
            }
        });

        buttonFriday.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (adapter != null) {
                    selectDay = Calendar.FRIDAY;
                    createList(schedule.get(selectDay -2));
                }
                return true ;
            }
        });

        buttonSaturday.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (adapter != null) {
                    selectDay = Calendar.SATURDAY;
                    createList(schedule.get(selectDay - 2 ));
                }
                return true ;
            }
        });

        buttonSunday.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (adapter != null) {
                    selectDay = Calendar.SUNDAY;
                    createList(schedule.get(selectDay + 5));
                }
                return true ;
            }
        });
        return v;
    }

    private void firstStartAsyncTaskLoader(){
        preSelectionButtonDay(8); //передаем 8, чтобы сбросить нажатие всех кнопок
        String selectedGym = String.valueOf(iSelectGym);
        Bundle bundle = new Bundle();
        bundle.putString("SelectedGym", selectedGym);
        int loaderId = 1;
        getLoaderManager().initLoader(loaderId, bundle, this).forceLoad();
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
        schedule = data;
        if (schedule != null) {
            createList(schedule.get(selectDay -1));
            layoutError.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
            itemsInTable.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.INVISIBLE);
            layoutError.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<List<Map>>> loader) {
    }

    private void createList(final List<Map> dailySchedule){
        adapter = new RecyclerAdapterTable(getContext(), dailySchedule, new ListenerRecordForTrainingSelect() {
            @Override
            public void selectTime(String stStartTime, String stTypesItem) {
                Calendar calendar = Calendar.getInstance();
                List<Integer> daysWhenRecordingIsPossible = new ArrayList<>();
                for (int i=0; i < 3; i++) {
                    calendar.add(Calendar.DAY_OF_WEEK, i);
                    daysWhenRecordingIsPossible.add(calendar.get(Calendar.DAY_OF_WEEK));
                    calendar.clear();
                    calendar = Calendar.getInstance();
                }
                boolean recordingIsPossible = daysWhenRecordingIsPossible.contains(selectDay);
                if (recordingIsPossible){
                    try {
                        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdfCheckTime = new SimpleDateFormat("HH:mm");
                        calendar = Calendar.getInstance();
                        int hourNow = calendar.get(Calendar.HOUR_OF_DAY);
                        Date selectTime = sdfCheckTime.parse(stStartTime);
                        int selectHour = selectTime.getHours();
                        boolean selectedToday = daysWhenRecordingIsPossible.get(0).equals(selectDay);
                        if (selectedToday && (selectHour <= hourNow)) { //проверяем чтобы выбранное время было позже чем сейчас
                            Toast toast = Toast.makeText(getContext(), "Выберите более позднее время.", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        } else {
                            int numberDayOfWeek =daysWhenRecordingIsPossible.indexOf(selectDay);
                            ConstructorLinks constructorLinks = new ConstructorLinks();
                            String stOpenURL = constructorLinks.constructorLinks(iSelectGym, numberDayOfWeek, stStartTime, stTypesItem);
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_VIEW);
                            intent.addCategory(Intent.CATEGORY_BROWSABLE);
                            intent.setData(Uri.parse(stOpenURL));
                            startActivity(intent);
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } else {
                    String nameDayOfWeek = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());
                    Toast toast = Toast.makeText(getContext(), "Запись возможна на сегодня (" + nameDayOfWeek  + ") и два дня вперед", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            }
        });
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        itemsInTable.setLayoutManager(mLayoutManager);
        itemsInTable.setAdapter(adapter);
        preSelectionButtonDay(selectDay);
    }

    //метод подготоавливающий состояние кнопок в зависимости от выбранного дня
    private void preSelectionButtonDay(int iDayOfWeek){
        if (iDayOfWeek == Calendar.MONDAY){
            selectButtonDay(true, false, false, false, false, false, false);

        }else if (iDayOfWeek == Calendar.TUESDAY){
            selectButtonDay(false, true, false, false, false, false, false);

        }else if (iDayOfWeek == Calendar.WEDNESDAY){
            selectButtonDay(false, false, true, false, false, false, false);

        }else if (iDayOfWeek == Calendar.THURSDAY){
            selectButtonDay(false, false, false, true, false, false, false);

        }else if (iDayOfWeek == Calendar.FRIDAY){
            selectButtonDay(false, false, false, false, true, false, false);

        }else if (iDayOfWeek == Calendar.SATURDAY){
            selectButtonDay(false, false, false, false, false, true, false);

        }else if (iDayOfWeek == Calendar.SUNDAY){
            selectButtonDay(false, false, false, false, false, false, true);

        }else {
            selectButtonDay(false, false, false, false, false, false, false);
        }
    }

    //метод применяющий выбор кнопок
    private void selectButtonDay(boolean m, boolean tu, boolean w, boolean th, boolean f, boolean sa, boolean su) {
        buttonMonday.setPressed(m);
        buttonTuesday.setPressed(tu);
        buttonWednesday.setPressed(w);
        buttonThursday.setPressed(th);
        buttonFriday.setPressed(f);
        buttonSaturday.setPressed(sa);
        buttonSunday.setPressed(su);
    }

    //в onStart делаем проверку на наличие данных в адаптаре. При первом запуске адаптер пустой и
    //будет запущен поток.
    //при возврате через кнопку back адаптер будет не пустым поток не запуститься. что сохранит
    //состояние адаптера в положении перед открытием нового фрагмента
    @Override
    public  void onStart() {
        super.onStart();

        if (adapter == null) {
            threadOpenFragment = new Thread(runnableOpenFragment);
            threadOpenFragment.setDaemon(true);
            threadOpenFragment.start();

        } else {
            preSelectionButtonDay(selectDay);
        }

        if (getActivity() instanceof InterfaceChangeTitle) {
            InterfaceChangeTitle listernerChangeTitle = (InterfaceChangeTitle) getActivity();
            listernerChangeTitle.changeTitle(R.string.title_Table_Fragment, R.string.title_Table_Fragment);
        }

        int backgroungImage;
        if (iSelectGym ==1) {
            backgroungImage = R.drawable.backgroundfotovrtical;
        } else {
            backgroungImage = R.drawable.backgroundfotovrtical2;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            imageTable.setImageDrawable(getResources().getDrawable(
                    backgroungImage, Objects.requireNonNull(getContext()).getTheme()));
        } else {
            imageTable.setImageDrawable(getResources().getDrawable(backgroungImage));
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (threadOpenFragment.isAlive()) {
            threadOpenFragment.interrupt();
        }
    }
}