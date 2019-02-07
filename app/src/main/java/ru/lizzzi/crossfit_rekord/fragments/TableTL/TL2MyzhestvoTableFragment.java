package ru.lizzzi.crossfit_rekord.fragments.TableTL;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import ru.lizzzi.crossfit_rekord.R;
import ru.lizzzi.crossfit_rekord.adapters.RecyclerAdapterTable;
import ru.lizzzi.crossfit_rekord.inspectionСlasses.ConstructorLinks;
import ru.lizzzi.crossfit_rekord.inspectionСlasses.NetworkCheck;
import ru.lizzzi.crossfit_rekord.interfaces.InterfaceChangeTitle;
import ru.lizzzi.crossfit_rekord.interfaces.ListenerRecordForTrainingSelect;
import ru.lizzzi.crossfit_rekord.loaders.TableFragmentLoader;

public class TL2MyzhestvoTableFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<List<Map>>> {
    private ProgressBar pbProgressBar;
    private RecyclerView rvItemsInTable;
    private Button buttonMonday;
    private Button buttonTuesday;
    private Button buttonWednesday;
    private Button buttonThursday;
    private Button buttonFriday;
    private Button buttonSaturday;
    private Button buttonSunday;
    private LinearLayout llLayoutError;

    private int iNumberOfDay; // выбранный пользователем день

    private Handler handlerOpenFragment;
    private Thread threadOpenFragment;

    private ru.lizzzi.crossfit_rekord.inspectionСlasses.NetworkCheck NetworkCheck; //переменная для проврки сети

    private RecyclerAdapterTable adapter; //адаптер для списка тренировок

    //private String dateSelectFull; //передает значение по поторому потом идет запрос в базу в следующем фрагменте
    //private String dateSelectShow; //передает значение которое показывается в Textview следующего фрагмента
    private GregorianCalendar calendarday; //нужна для формирования дат для кнопок

    private List<List<Map>> schedule;
    private Runnable runnableOpenFragment;

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
        llLayoutError = v.findViewById(R.id.Layout_Error);
        pbProgressBar = v.findViewById(R.id.progressBar);
        rvItemsInTable = v.findViewById(R.id.lvTable);

        llLayoutError.setVisibility(View.INVISIBLE);
        rvItemsInTable.setVisibility(View.INVISIBLE);
        pbProgressBar.setVisibility(View.VISIBLE);


        //хэндлер для потока runnableOpenFragment
        handlerOpenFragment = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Bundle bundle = msg.getData();
                String result_check = bundle.getString("result");
                if (result_check != null && result_check.equals("false")) {
                    llLayoutError.setVisibility(View.VISIBLE);
                    pbProgressBar.setVisibility(View.INVISIBLE);
                } else {
                    llLayoutError.setVisibility(View.INVISIBLE);
                    pbProgressBar.setVisibility(View.VISIBLE);
                    firstStartAsyncTaskLoader();
                }
            }
        };

        //поток запускаемый при создании экрана (запуск происходит из onStart)
        runnableOpenFragment = new Runnable() {
            @Override
            public void run() {

                NetworkCheck = new NetworkCheck(getContext());
                boolean resultCheck = NetworkCheck.checkInternet();
                Bundle bundle = new Bundle();
                if (resultCheck) {
                    iNumberOfDay = 1;
                    bundle.putString("result", String.valueOf(true));

                } else {
                    bundle.putString("result", String.valueOf(false));
                }
                Message msg = handlerOpenFragment.obtainMessage();
                msg.setData(bundle);
                handlerOpenFragment.sendMessage(msg);
            }
        };


        buttonError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pbProgressBar.setVisibility(View.VISIBLE);
                llLayoutError.setVisibility(View.INVISIBLE);
                threadOpenFragment = new Thread(runnableOpenFragment);
                threadOpenFragment.setDaemon(true);
                threadOpenFragment.start();
            }
        });

        buttonMonday.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (adapter != null) {
                    iNumberOfDay = 1;
                    createList(schedule.get(iNumberOfDay - 1));
                }
                return true;
            }
        });

        buttonTuesday.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (adapter != null) {
                    iNumberOfDay = 2;
                    createList(schedule.get(iNumberOfDay - 1));
                }

                return true;
            }
        });

        buttonWednesday.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (adapter != null) {
                    iNumberOfDay = 3;
                    createList(schedule.get(iNumberOfDay - 1));
                }
                return true;
            }
        });

        buttonThursday.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (adapter != null) {
                    iNumberOfDay = 4;
                    createList(schedule.get(iNumberOfDay - 1));
                }
                return true;
            }
        });

        buttonFriday.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (adapter != null) {
                    iNumberOfDay = 5;
                    createList(schedule.get(iNumberOfDay - 1));
                }
                return true;
            }
        });

        buttonSaturday.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (adapter != null) {
                    iNumberOfDay = 6;
                    createList(schedule.get(iNumberOfDay - 1));
                }
                return true;
            }
        });

        buttonSunday.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (adapter != null) {
                    iNumberOfDay = 7;
                    createList(schedule.get(iNumberOfDay - 1));
                }
                return true;
            }
        });

        return v;
    }

    private void firstStartAsyncTaskLoader() {
        preSelectionButtonDay(8); //передаем 8, чтобы сбросить нажатие всех кнопок
        Bundle bundle = new Bundle();
        bundle.putString("SelectedGym", "2");
        int loaderid = 1;
        getLoaderManager().initLoader(loaderid, bundle, this).forceLoad();
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
            createList(schedule.get(iNumberOfDay - 1));

            llLayoutError.setVisibility(View.INVISIBLE);
            pbProgressBar.setVisibility(View.INVISIBLE);
            rvItemsInTable.setVisibility(View.VISIBLE);
        } else {
            pbProgressBar.setVisibility(View.INVISIBLE);
            llLayoutError.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<List<Map>>> loader) {
    }

    private void createList(List<Map> dailySchedule) {
        adapter = new RecyclerAdapterTable(getContext(), dailySchedule, new ListenerRecordForTrainingSelect() {
            @Override
            public void selectTime(String stStartTime, String stTypesItem) {

                Date today;
                Calendar c = Calendar.getInstance();
                calendarday = new GregorianCalendar();

                int numberDayOfWeek;
                if (c.get(Calendar.DAY_OF_WEEK) == 1) {
                    numberDayOfWeek = 7;
                } else {
                    numberDayOfWeek = (c.get(Calendar.DAY_OF_WEEK) - 1);
                }
                int dayOfWeek = iNumberOfDay - numberDayOfWeek;
                if ((dayOfWeek == 0) || (dayOfWeek == 1) || (dayOfWeek == 2) || (dayOfWeek == -5) || (dayOfWeek == -6)) {
                    //@SuppressLint("SimpleDateFormat") SimpleDateFormat sdfDataShow = new SimpleDateFormat("EEEE dd MMMM");
                    //@SuppressLint("SimpleDateFormat") SimpleDateFormat sdfDataFull = new SimpleDateFormat("dd/MM");
                    @SuppressLint("SimpleDateFormat") SimpleDateFormat sdfCheckTime = new SimpleDateFormat("HH:mm");

                    //этот цикл нужен для проверки дней недели. Если сегодня сб/вс, то пн/вт должен
                    //быть следующей недели а не текущей
                    if (c.get(Calendar.DAY_OF_WEEK) == 1) { //если день недели вс
                        if (dayOfWeek == -6) { //выбран пн.
                            calendarday.add(Calendar.DAY_OF_YEAR, 1);
                        } else if (dayOfWeek == -5) { // выбран вт.
                            calendarday.add(Calendar.DAY_OF_YEAR, 2);
                        } else {
                            calendarday.add(Calendar.DAY_OF_YEAR, dayOfWeek);
                        }
                    } else if (c.get(Calendar.DAY_OF_WEEK) == 7) { //если день недели сб
                        if (dayOfWeek == -5) { //если пн.
                            calendarday.add(Calendar.DAY_OF_YEAR, 2);
                        } else {
                            calendarday.add(Calendar.DAY_OF_YEAR, dayOfWeek);
                        }
                    } else { //если любой другой день
                        calendarday.add(Calendar.DAY_OF_YEAR, dayOfWeek);
                    }

                    today = calendarday.getTime();
                    //dateSelectShow = sdfDataShow.format(today);
                    //dateSelectFull = sdfDataFull.format(today);
                    boolean checkday = false; //проверка на выбор сегодняшнего дня;
                    if (dayOfWeek == 0) {
                        try {
                            String stTimeNow = sdfCheckTime.format(today);
                            Date dTimeNow = sdfCheckTime.parse(stTimeNow);
                            Date dSelectTime = sdfCheckTime.parse(stStartTime);
                            if (dSelectTime.getTime() > dTimeNow.getTime()) { //проверяем чтобы выбранное время было позже чем сейчас
                                checkday = true;
                            } else {
                                Toast toast = Toast.makeText(getContext(), "Выберите более позднее время.", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                    } else {
                        checkday = true;
                    }

                    if (checkday) {

                        int iSelectGym = Objects.requireNonNull(getContext()).getResources().getInteger(R.integer.intSelectTlMyzhestvo);
                        ConstructorLinks constructorLinks = new ConstructorLinks();
                        String stOpenURL = constructorLinks.constructorLinks(iSelectGym,dayOfWeek, stStartTime, stTypesItem);
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.addCategory(Intent.CATEGORY_BROWSABLE);
                        intent.setData(Uri.parse(stOpenURL));
                        startActivity(intent);

                        //Оставил эту часть кода на случай возврата к записи через приложение
                        /*Bundle bundle = new Bundle();
                        bundle.putString("time", stStartTime);
                        bundle.putString("datefull", dateSelectFull);
                        bundle.putString("dateshow", dateSelectShow);
                        bundle.putString("type", stTypesItem);
                        Fragment fragment =  new RecordForTrainingRecordingFragment();
                        fragment.setArguments(bundle);
                        FragmentManager fragmentManager = getFragmentManager();
                        FragmentTransaction ft = fragmentManager.beginTransaction();
                        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                        ft.replace(R.id.container, fragment);
                        ft.addToBackStack(null);
                        ft.commit();*/
                    }


                } else {
                    @SuppressLint("SimpleDateFormat") final SimpleDateFormat sdfToast = new SimpleDateFormat("EEEE");
                    calendarday.add(Calendar.DAY_OF_YEAR, 0);
                    today = calendarday.getTime();
                    String toastToday = sdfToast.format(today);
                    Toast toast = Toast.makeText(getContext(), "Запись возможна на сегодня (" + toastToday + ") и два дня вперед", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }

            }
        });
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        rvItemsInTable.setLayoutManager(mLayoutManager);
        rvItemsInTable.setAdapter(adapter);
        preSelectionButtonDay(iNumberOfDay);
    }

    //метод подготоавливающий состояние кнопок в зависимости от выбранного дня
    private void preSelectionButtonDay(int iDayOfWeek) {
        if (iDayOfWeek == 1) {
            selectButtonDay(true, false, false, false, false, false, false);

        } else if (iDayOfWeek == 2) {
            selectButtonDay(false, true, false, false, false, false, false);

        } else if (iDayOfWeek == 3) {
            selectButtonDay(false, false, true, false, false, false, false);

        } else if (iDayOfWeek == 4) {
            selectButtonDay(false, false, false, true, false, false, false);

        } else if (iDayOfWeek == 5) {
            selectButtonDay(false, false, false, false, true, false, false);

        } else if (iDayOfWeek == 6) {
            selectButtonDay(false, false, false, false, false, true, false);

        } else if (iDayOfWeek == 7) {
            selectButtonDay(false, false, false, false, false, false, true);

        } else {
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
    public void onStart() {
        super.onStart();

        if (adapter == null) {
            threadOpenFragment = new Thread(runnableOpenFragment);
            threadOpenFragment.setDaemon(true);
            threadOpenFragment.start();

        } else {
            preSelectionButtonDay(iNumberOfDay);
        }

        if (getActivity() instanceof InterfaceChangeTitle) {
            InterfaceChangeTitle listernerChangeTitle = (InterfaceChangeTitle) getActivity();
            listernerChangeTitle.changeTitle(R.string.title_Table_Fragment, R.string.title_Table_Fragment);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();

        if (threadOpenFragment.isAlive()) {
            threadOpenFragment.interrupt();
        }
    }
}