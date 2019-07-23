package ru.lizzzi.crossfit_rekord.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
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

import java.lang.ref.WeakReference;
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
import ru.lizzzi.crossfit_rekord.inspectionСlasses.UriParser;
import ru.lizzzi.crossfit_rekord.inspectionСlasses.Network;
import ru.lizzzi.crossfit_rekord.interfaces.ChangeTitle;
import ru.lizzzi.crossfit_rekord.interfaces.ListenerRecordForTrainingSelect;
import ru.lizzzi.crossfit_rekord.loaders.TableFragmentLoader;

public class GymScheduleFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<List<Map>>> {
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

    private int selectedDay; // выбранный пользователем день
    private int selectedGym;
    
    private Thread threadOpenFragment;

    private RecyclerAdapterTable adapter; //адаптер для списка тренировок

    private List<List<Map>> schedule;
    private Runnable runnableOpenFragment;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_table, container, false);

        buttonMonday = view.findViewById(R.id.day_1);
        buttonTuesday = view.findViewById(R.id.day_2);
        buttonWednesday = view.findViewById(R.id.day_3);
        buttonThursday = view.findViewById(R.id.day_4);
        buttonFriday = view.findViewById(R.id.day_5);
        buttonSaturday = view.findViewById(R.id.day_6);
        buttonSunday = view.findViewById(R.id.day_7);
        Button buttonError = view.findViewById(R.id.button5);
        layoutError = view.findViewById(R.id.Layout_Error);
        progressBar = view.findViewById(R.id.progressBar);
        itemsInTable = view.findViewById(R.id.lvTable);
        imageTable = view.findViewById(R.id.ivTable);

        layoutError.setVisibility(View.INVISIBLE);
        itemsInTable.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);

        Bundle bundle = getArguments();
        selectedGym = (bundle != null)
                ? bundle.getInt("gym")
                : Objects.requireNonNull(
                getContext()).getResources().getInteger(R.integer.selectSheduleParnas);

        final Handler handler = new Handler(GymScheduleFragment.this);

        //поток запускаемый при создании экрана (запуск происходит из onStart)
        runnableOpenFragment = new Runnable() {
            @Override
            public void run() {
                Network network = new Network(getContext());
                Bundle bundle = new Bundle();
                boolean checkDone = network.checkConnection();
                if (checkDone) {
                    selectedDay = Calendar.MONDAY;
                }
                bundle.putBoolean("checkConnection", checkDone);
                Message message = handler.obtainMessage();
                message.setData(bundle);
                handler.sendMessage(message);
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
                    selectedDay = Calendar.MONDAY;
                    showSchedule(schedule.get(selectedDay - 1));
                }
                return true ;
            }
        });

        buttonTuesday.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (adapter != null) {
                    selectedDay = Calendar.TUESDAY;
                    showSchedule(schedule.get(selectedDay - 1));
                }
                return true ;
            }
        });

        buttonWednesday.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (adapter != null) {
                    selectedDay = Calendar.WEDNESDAY;
                    showSchedule(schedule.get(selectedDay - 1));
                }
                return true ;
            }
        });

        buttonThursday.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (adapter != null) {
                    selectedDay = Calendar.THURSDAY;
                    showSchedule(schedule.get(selectedDay - 1));
                }
                return true ;
            }
        });

        buttonFriday.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (adapter != null) {
                    selectedDay = Calendar.FRIDAY;
                    showSchedule(schedule.get(selectedDay - 1));
                }
                return true ;
            }
        });

        buttonSaturday.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (adapter != null) {
                    selectedDay = Calendar.SATURDAY;
                    showSchedule(schedule.get(selectedDay - 1));
                }
                return true ;
            }
        });

        buttonSunday.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (adapter != null) {
                    selectedDay = Calendar.SUNDAY;
                    showSchedule(schedule.get(selectedDay - 1));
                }
                return true ;
            }
        });
        return view;
    }

    private void startLoader() {
        int UNPRESS_ALL_BUTTONS = 8;
        setPressedButtons(UNPRESS_ALL_BUTTONS);
        Bundle bundle = new Bundle();
        bundle.putString("SelectedGym", String.valueOf(selectedGym));
        int LOADER_ID = 1;
        getLoaderManager().initLoader(LOADER_ID, bundle, this).forceLoad();
    }

    @NonNull
    @Override
    public Loader<List<List<Map>>> onCreateLoader(int id, Bundle args) {
        return new TableFragmentLoader(getContext(), args);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<List<Map>>> loader, List<List<Map>> data) {
        schedule = data;
        if (schedule != null) {
            showSchedule(schedule.get(selectedDay - 1));
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

    private void showSchedule(final List<Map> dailySchedule){
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
                if (daysWhenRecordingIsPossible.contains(selectedDay)) {
                    try {
                        SimpleDateFormat dateFormat =
                                new SimpleDateFormat("HH:mm", Locale.getDefault());
                        calendar = Calendar.getInstance();
                        int hourNow = calendar.get(Calendar.HOUR_OF_DAY);
                        Date selectTime = dateFormat.parse(stStartTime);
                        int selectHour = selectTime.getHours();
                        boolean selectedToday = daysWhenRecordingIsPossible.get(0).equals(selectedDay);
                        if (selectedToday && (selectHour <= hourNow)) { //проверяем чтобы выбранное время было позже чем сейчас
                            Toast toast = Toast.makeText(
                                    getContext(), 
                                    "Выберите более позднее время.", 
                                    Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        } else {
                            int numberDayOfWeek =daysWhenRecordingIsPossible.indexOf(selectedDay);
                            UriParser uriParser = new UriParser();
                            Uri uri = uriParser.getURI(
                                    selectedGym, 
                                    numberDayOfWeek, 
                                    stStartTime, 
                                    stTypesItem);
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_VIEW);
                            intent.addCategory(Intent.CATEGORY_BROWSABLE);
                            intent.setData(uri);
                            startActivity(intent);
                            Objects.requireNonNull(getActivity()).overridePendingTransition(
                                    R.anim.pull_in_right,
                                    R.anim.push_out_left);
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } else {
                    String nameDayOfWeek = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());
                    Toast toast = Toast.makeText(
                            getContext(), 
                            "Запись возможна на сегодня (" + nameDayOfWeek  + ") и два дня вперед", 
                            Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            }
        });
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        itemsInTable.setLayoutManager(mLayoutManager);
        itemsInTable.setAdapter(adapter);
        setPressedButtons(selectedDay);
    }

    //метод подготоавливающий состояние кнопок в зависимости от выбранного дня
    private void setPressedButtons(int selectedDayOfWeek) {
        unpressedButtons();
        switch (selectedDayOfWeek) {
            case Calendar.MONDAY:
                buttonMonday.setPressed(true);
                break;
            case Calendar.TUESDAY:
                buttonTuesday.setPressed(true);
                break;
            case Calendar.WEDNESDAY:
                buttonWednesday.setPressed(true);
                break;
            case Calendar.THURSDAY:
                buttonThursday.setPressed(true);
                break;
            case Calendar.FRIDAY:
                buttonFriday.setPressed(true);
                break;
            case Calendar.SATURDAY:
                buttonSaturday.setPressed(true);
                break;
            case Calendar.SUNDAY:
                buttonSunday.setPressed(true);
                break;
        }
    }

    //метод применяющий выбор кнопок
    private void unpressedButtons() {
        buttonMonday.setPressed(false);
        buttonTuesday.setPressed(false);
        buttonWednesday.setPressed(false);
        buttonThursday.setPressed(false);
        buttonFriday.setPressed(false);
        buttonSaturday.setPressed(false);
        buttonSunday.setPressed(false);
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
            setPressedButtons(selectedDay);
        }

        if (getActivity() instanceof ChangeTitle) {
            ChangeTitle listernerChangeTitle = (ChangeTitle) getActivity();
            listernerChangeTitle.changeTitle(R.string.title_Table_Fragment, R.string.title_Table_Fragment);
        }

        int backgroundImage;
        if (selectedGym == 1) {
            backgroundImage = R.drawable.background_foto_1;
        } else {
            backgroundImage = R.drawable.background_foto_2;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            imageTable.setImageDrawable(getResources().getDrawable(
                    backgroundImage, Objects.requireNonNull(getContext()).getTheme()));
        } else {
            imageTable.setImageDrawable(getResources().getDrawable(backgroundImage));
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (threadOpenFragment.isAlive()) {
            threadOpenFragment.interrupt();
        }
    }

    private static class Handler extends android.os.Handler {
        private final WeakReference<GymScheduleFragment> fragmentWeakReference;

        Handler(GymScheduleFragment fragmentInstance) {
            fragmentWeakReference = new WeakReference<>(fragmentInstance);
        }

        @Override
        public void handleMessage(Message message) {
            GymScheduleFragment fragment = fragmentWeakReference.get();
            if (fragment != null) {
                boolean resultCheck = message.getData().getBoolean("checkConnection");
                if (resultCheck) {
                    fragment.layoutError.setVisibility(View.INVISIBLE);
                    fragment.progressBar.setVisibility(View.VISIBLE);
                    fragment.startLoader();
                } else {
                    fragment.layoutError.setVisibility(View.VISIBLE);
                    fragment.progressBar.setVisibility(View.INVISIBLE);
                }
            }
        }
    }
}