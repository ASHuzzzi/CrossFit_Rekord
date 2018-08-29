package ru.lizzzi.crossfit_rekord.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import ru.lizzzi.crossfit_rekord.R;
import ru.lizzzi.crossfit_rekord.adapters.RecyclerAdapterRecordForTrainingSelect;
import ru.lizzzi.crossfit_rekord.interfaces.InterfaceChangeTitle;
import ru.lizzzi.crossfit_rekord.interfaces.ListenerRecordForTrainingSelect;
import ru.lizzzi.crossfit_rekord.loaders.TableFragmentLoader;


public class RecordForTrainingSelectFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<List<Map>>>{

    private LinearLayout llErorRfTS;
    private LinearLayout llListTime;
    private RecyclerView rvTreningTime;
    private ProgressBar pbRfTS;

    private RecyclerAdapterRecordForTrainingSelect adapter;

    private Date date; //показывает сегодняшний день
    private GregorianCalendar gcCalendarDay; //нужна для формирования дат для кнопок
    private GregorianCalendar gcNumberDayWeek; // для преобразования выбранного дня в int

    private String stDateSelectFull; //передает значение по поторому потом идет запрос в базу в следующем фрагменте
    private String stDateSelectShow; //передает значение которое показывается в Textview следующего фрагмента

    private int iNumberOfDay; // выбранный пользователем день
    private  int LOADER_ID = 1; //идентефикатор loader'а

    private NetworkCheck networkCheck;//переменная для проврки сети

    private Handler handlerOpenFragment;
    private Thread threadOpenFragment;

    private boolean flagTodayOrNot;
    private List<List<Map>> schedule;
    private int selectDay;

    private Button btToday;
    private Button btTommorow;
    private Button btAftertommorow;

    @SuppressLint({"HandlerLeak", "ClickableViewAccessibility"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_record_for_training_select, container, false);
        getActivity().setTitle(R.string.title_RecordForTraining_Fragment);

        btToday = v.findViewById(R.id.btToday);
        btTommorow = v.findViewById(R.id.btTommorow);
        btAftertommorow = v.findViewById(R.id.btAftertommorow);
        Button buttonError = v.findViewById(R.id.button6);
        rvTreningTime = v.findViewById(R.id.rvTrainingTime);
        llErorRfTS = v.findViewById(R.id.llEror_RfTS);
        llListTime = v.findViewById(R.id.llListTime);
        pbRfTS = v.findViewById(R.id.pbRfTS);

        llListTime.setVisibility(View.INVISIBLE);
        llErorRfTS.setVisibility(View.INVISIBLE);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        rvTreningTime.setLayoutManager(mLayoutManager);
        rvTreningTime.setAdapter(adapter);

        gcNumberDayWeek = new GregorianCalendar();


        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("EEEE dd MMMM");
        @SuppressLint("SimpleDateFormat") final SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM");

        //хэндлер для обоих потоков. Какой именно поток вызвал хэндлер передается в key
        handlerOpenFragment = new Handler() {
            @SuppressLint("ShowToast")
            @Override
            public void handleMessage(Message msg) {
                Bundle bundle = msg.getData();
                String switchs = bundle.getString("switch"); //показывает какой поток вызвал
                String resultCheck;
                if (switchs != null){
                    if (switchs.equals("open")){ //поток при первом запуске экрана
                        resultCheck = bundle.getString("open");
                        if (resultCheck != null) {
                            if (resultCheck.equals("false")) {
                                llErorRfTS.setVisibility(View.VISIBLE);
                                pbRfTS.setVisibility(View.INVISIBLE);
                            }
                        }
                    }
                }
            }
        };

        //поток запускаемый при создании экрана (запуск происходит из onResume)
        Runnable runnable_open_fragment = new Runnable() {
            @Override
            public void run() {
                networkCheck = new NetworkCheck(getContext());
                boolean resultCheck = networkCheck.checkInternet();
                if (resultCheck){
                    iNumberOfDay = gcNumberDayWeek.get(Calendar.DAY_OF_WEEK)-1;
                    if (iNumberOfDay == 0){
                        iNumberOfDay = 7;
                    }
                    flagTodayOrNot = true;
                    selectDay = 1;
                    firstStartAsyncTaskLoader(iNumberOfDay);

                }else {
                    Message msg = handlerOpenFragment.obtainMessage();
                    Bundle bundle = new Bundle();
                    bundle.putString("open", String.valueOf(false));
                    bundle.putString("switch", "open");
                    msg.setData(bundle);
                    handlerOpenFragment.sendMessage(msg);
                }
            }
        };
        threadOpenFragment = new Thread(runnable_open_fragment);
        threadOpenFragment.setDaemon(true);

        //получаю значения для кнопок
        date = new Date();
        gcCalendarDay = new GregorianCalendar();
        gcCalendarDay.add(Calendar.DAY_OF_YEAR, 1);
        final Date tomorrow = gcCalendarDay.getTime();

        gcCalendarDay.add(Calendar.DAY_OF_YEAR, 1);
        final Date aftertomorrow = gcCalendarDay.getTime();

        final String currentToday = sdf.format(date);
        final String currentTomorrow = sdf.format(tomorrow);
        final String currentAftertommorow = sdf.format(aftertomorrow);

        btToday.setText(currentToday);
        btTommorow.setText(currentTomorrow);
        btAftertommorow.setText(currentAftertommorow);

        gcCalendarDay.setTime(date);
        stDateSelectFull = sdf2.format(date);
        stDateSelectShow = currentToday;

        buttonError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                llErorRfTS.setVisibility(View.INVISIBLE);
                pbRfTS.setVisibility(View.VISIBLE);
                threadOpenFragment.run();
            }
        });

        btToday.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (adapter != null){
                    gcCalendarDay.setTime(date);
                    iNumberOfDay = gcNumberDayWeek.get(Calendar.DAY_OF_WEEK)-1;
                    if (iNumberOfDay == 0){
                        iNumberOfDay = 7;
                    }
                    stDateSelectFull = sdf2.format(date);
                    stDateSelectShow = currentToday;
                    flagTodayOrNot = true;
                    selectDay = 1;
                    drawList(schedule.get(iNumberOfDay-1));
                }
                return true ;
            }
        });

        btTommorow.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (adapter != null){
                    gcCalendarDay.setTime(tomorrow);
                    iNumberOfDay = gcNumberDayWeek.get(Calendar.DAY_OF_WEEK);
                    stDateSelectFull = sdf2.format(tomorrow);
                    stDateSelectShow = currentTomorrow;
                    flagTodayOrNot = false;
                    selectDay = 2;
                    drawList(schedule.get(iNumberOfDay-1));
                }
                return true ;
            }
        });

        btAftertommorow.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (adapter != null){
                        gcCalendarDay.setTime(aftertomorrow);
                        iNumberOfDay = gcNumberDayWeek.get(Calendar.DAY_OF_WEEK)+1;
                        if (iNumberOfDay == 8){
                            iNumberOfDay = 1;
                        }
                        stDateSelectFull = sdf2.format(aftertomorrow);
                        stDateSelectShow = currentAftertommorow;
                        flagTodayOrNot = false;
                        selectDay = 3;
                        drawList(schedule.get(iNumberOfDay-1));
                }
                return true ;
            }
        });

        return v;
    }

    private void firstStartAsyncTaskLoader(int day_select){
        Bundle bundle = new Bundle();
        bundle.putString(String.valueOf(TableFragmentLoader.ARG_WORD), String.valueOf(day_select));
        getLoaderManager().initLoader(LOADER_ID, bundle, this).forceLoad();
    }

    @Override
    public Loader<List<List<Map>>> onCreateLoader(int id, Bundle args) {
        if (networkCheck.checkInternet()) {
            Loader<List<List<Map>>> loader;
            loader = new TableFragmentLoader(getContext(), args);
            return loader;
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<List<List<Map>>> loader, List<List<Map>> data) {
        if (data != null){
            schedule = data;
            drawList(schedule.get(iNumberOfDay-1));
        }
    }

    private void preSelectionButtonDay(int iDaySelect){
        if (iDaySelect == 1 ){
            selectButtonDay(true, false, false);

        }else if (iDaySelect == 2){
            selectButtonDay(false, true, false);

        }else if (iDaySelect == 3){
            selectButtonDay(false, false, true);
        }
    }

    //метод применяющий выбор кнопок
    private void selectButtonDay(boolean tod, boolean tom, boolean aft) {
        btToday.setPressed(tod);
        btTommorow.setPressed(tom);
        btAftertommorow.setPressed(aft);
    }

    @Override
    public void onLoaderReset(Loader<List<List<Map>>> loader) {

    }

    @Override
    public  void onStart() {
        super.onStart();
        if (getActivity() instanceof InterfaceChangeTitle){
            InterfaceChangeTitle listernerChangeTitle = (InterfaceChangeTitle) getActivity();
            listernerChangeTitle.changeTitle(R.string.title_RecordForTraining_Fragment, R.string.title_RecordForTraining_Fragment);
        }

    }

    //в onResume делаем проверку на наличие данных в адаптаре. При первом запуске адаптер пустой и
    //будет запущен поток.
    //при возврате через кнопку back адаптер будет не пустым поток не запуститься. что сохранит
    //состояние адаптера в положении перед открытием нового фрагмента
    @Override
    public void onResume() {
        super.onResume();
        if (adapter == null){
            threadOpenFragment.run();
        }else {
            preSelectionButtonDay(selectDay);
        }
    }

    public void onStop() {
        super.onStop();
        if (getLoaderManager().hasRunningLoaders()) {
            getLoaderManager().destroyLoader(LOADER_ID);
        }
    }

    private void drawList(List<Map> dailySchedule){
        adapter = new RecyclerAdapterRecordForTrainingSelect(getContext(), dailySchedule,
                flagTodayOrNot, new ListenerRecordForTrainingSelect(){
            @Override
            public void selectTime(String stStartTime, String stTypesItem) {
                if(stStartTime.equals("outTime") && stTypesItem.equals("outTime")){
                    Toast toast = Toast.makeText(getContext(), "Тренировка уже прошла. Выбери более позднее время!", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }else {
                    RecordForTrainingRecordingFragment yfc =  new RecordForTrainingRecordingFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("time", stStartTime);
                    bundle.putString("datefull", stDateSelectFull);
                    bundle.putString("dateshow", stDateSelectShow);
                    bundle.putString("type", stTypesItem);
                    yfc.setArguments(bundle);
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction ft = fragmentManager.beginTransaction();
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    ft.replace(R.id.container, yfc);
                    ft.addToBackStack(null);
                    ft.commit();
                }

            }
        });
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        rvTreningTime.setLayoutManager(mLayoutManager);
        rvTreningTime.setAdapter(adapter);
        preSelectionButtonDay(selectDay);

        pbRfTS.setVisibility(View.INVISIBLE);
        llListTime.setVisibility(View.VISIBLE);
    }
}
