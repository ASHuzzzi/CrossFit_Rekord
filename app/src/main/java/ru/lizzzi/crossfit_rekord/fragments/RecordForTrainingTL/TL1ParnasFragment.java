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
import ru.lizzzi.crossfit_rekord.inspectionСlasses.NetworkCheck;
import ru.lizzzi.crossfit_rekord.interfaces.InterfaceChangeTitle;
import ru.lizzzi.crossfit_rekord.interfaces.ListenerRecordForTrainingSelect;
import ru.lizzzi.crossfit_rekord.loaders.TableFragmentLoader;

public class TL1ParnasFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<List<Map>>> {

    private LinearLayout llErorRfTS;
    private LinearLayout llListTime;
    private RecyclerView rvTreningTime;
    private ProgressBar pbRfTS;

    private RecyclerAdapterRecordForTrainingSelect adapter;

    private Date date; //показывает сегодняшний день
    private GregorianCalendar gcCalendarDay; //нужна для формирования дат для кнопок
    private GregorianCalendar gcNumberDayWeek; // для преобразования выбранного дня в int

    //private String stDateSelectFull; //передает значение по поторому потом идет запрос в базу в следующем фрагменте
    //private String stDateSelectShow; //передает значение которое показывается в Textview следующего фрагмента

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

    private Runnable runnableOpenFragment;

    //private String currentTodayForShow;
    //private String currentTomorrowForShow;
    //private String currentAftertommorowForShow;

    private Date tomorrow;
    private Date aftertomorrow;

    private ImageView iv_RfTS;

    //@SuppressLint("SimpleDateFormat") final SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM");

    @SuppressLint({"HandlerLeak", "ClickableViewAccessibility"})
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
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
        iv_RfTS = v.findViewById(R.id.iv_RfTS);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        rvTreningTime.setLayoutManager(mLayoutManager);
        rvTreningTime.setAdapter(adapter);

        gcNumberDayWeek = new GregorianCalendar();


        @SuppressLint("SimpleDateFormat") final SimpleDateFormat sdf = new SimpleDateFormat("EEE.\n d MMMM");

        //@SuppressLint("SimpleDateFormat") final SimpleDateFormat sdf3 = new SimpleDateFormat("EEEE d MMMM");

        handlerOpenFragment = new Handler() {
            @SuppressLint("ShowToast")
            @Override
            public void handleMessage(Message msg) {
                Bundle bundle = msg.getData();
                String resultCheck;
                resultCheck = bundle.getString("open");
                if (resultCheck != null && resultCheck.equals("false")) {
                    llErorRfTS.setVisibility(View.VISIBLE);
                    pbRfTS.setVisibility(View.INVISIBLE);
                }else {
                    llErorRfTS.setVisibility(View.INVISIBLE);
                    pbRfTS.setVisibility(View.VISIBLE);
                    startAsyncTaskLoader();
                }
            }
        };

        //поток запускаемый при создании экрана (запуск происходит из onStart)
        runnableOpenFragment = new Runnable() {
            @Override
            public void run() {
                networkCheck = new NetworkCheck(getContext());
                boolean resultCheck = networkCheck.checkInternet();
                Bundle bundle = new Bundle();
                if (resultCheck){
                    iNumberOfDay = gcNumberDayWeek.get(Calendar.DAY_OF_WEEK)-1;
                    if (iNumberOfDay == 0){
                        iNumberOfDay = 7;
                    }
                    flagTodayOrNot = true;
                    selectDay = 0;
                    bundle.putString("open", String.valueOf(true));

                }else {

                    bundle.putString("open", String.valueOf(false));
                }
                Message msg = handlerOpenFragment.obtainMessage();
                msg.setData(bundle);
                handlerOpenFragment.sendMessage(msg);
            }
        };

        //получаю значения для кнопок
        date = new Date();
        gcCalendarDay = new GregorianCalendar();
        gcCalendarDay.add(Calendar.DAY_OF_YEAR, 1);
        tomorrow = gcCalendarDay.getTime();

        gcCalendarDay.add(Calendar.DAY_OF_YEAR, 1);
        aftertomorrow = gcCalendarDay.getTime();

        final String currentToday = sdf.format(date);
        final String currentTomorrow = sdf.format(tomorrow);
        final String currentAftertommorow = sdf.format(aftertomorrow);

        //currentTomorrowForShow = sdf3.format(tomorrow);
        //currentTodayForShow = sdf3.format(date);
        //currentAftertommorowForShow = sdf3.format(aftertomorrow);

        btToday.setText(currentToday);
        btTommorow.setText(currentTomorrow);
        btAftertommorow.setText(currentAftertommorow);

        gcCalendarDay.setTime(date);
        //stDateSelectFull = sdf2.format(date);
        //stDateSelectShow = currentTodayForShow;

        buttonError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                llErorRfTS.setVisibility(View.INVISIBLE);
                pbRfTS.setVisibility(View.VISIBLE);
                threadOpenFragment = new Thread(runnableOpenFragment);
                threadOpenFragment.setDaemon(true);
                threadOpenFragment.start();
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
                    //stDateSelectFull = sdf2.format(date);
                    //stDateSelectShow = currentTodayForShow;
                    flagTodayOrNot = true;
                    selectDay = 0;
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
                    //stDateSelectFull = sdf2.format(tomorrow);
                    //stDateSelectShow = currentTomorrowForShow;
                    flagTodayOrNot = false;
                    selectDay = 1;
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
                    //stDateSelectFull = sdf2.format(aftertomorrow);
                    //stDateSelectShow = currentAftertommorowForShow;
                    flagTodayOrNot = false;
                    selectDay = 2;
                    drawList(schedule.get(iNumberOfDay-1));
                }
                return true ;
            }
        });

        return v;
    }

    private void startAsyncTaskLoader(){
        Bundle bundle = new Bundle();
        bundle.putString("SelectedGym", "1");
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

        if (data != null){
            schedule = data;
            drawList(schedule.get(iNumberOfDay-1));

            llErorRfTS.setVisibility(View.INVISIBLE);
            pbRfTS.setVisibility(View.INVISIBLE);
            llListTime.setVisibility(View.VISIBLE);
        }else {
            llErorRfTS.setVisibility(View.VISIBLE);
            pbRfTS.setVisibility(View.INVISIBLE);
            llListTime.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<List<Map>>> loader) {

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
                    int iSelectGym = Objects.requireNonNull(getContext()).getResources().getInteger(R.integer.selectSheduleParnas);
                    ConstructorLinks constructorLinks = new ConstructorLinks();
                    String stOpenURL = constructorLinks.constructorLinks(iSelectGym,selectDay, stStartTime, stTypesItem);
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.addCategory(Intent.CATEGORY_BROWSABLE);
                    intent.setData(Uri.parse(stOpenURL));
                    startActivity(intent);

                    //Оставил эту часть кода на случай возврата к записи через приложение
                    /*
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
                    ft.commit();*/
                }

            }
        });
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        rvTreningTime.setLayoutManager(mLayoutManager);
        rvTreningTime.setAdapter(adapter);
        preSelectionButtonDay(selectDay);
    }

    private void preSelectionButtonDay(int iDaySelect){
        switch (iDaySelect){
            case 0:
                //stDateSelectShow = currentTodayForShow;
                //stDateSelectFull = sdf2.format(date);
                selectButtonDay(true, false, false);

                break;

            case 1:
                //stDateSelectShow = currentTomorrowForShow;
                //stDateSelectFull = sdf2.format(tomorrow);
                selectButtonDay(false, true, false);
                break;

            case 2:
                //stDateSelectShow = currentAftertommorowForShow;
                //stDateSelectFull = sdf2.format(aftertomorrow);
                selectButtonDay(false, false, true);
                break;
        }
    }

    //метод применяющий выбор кнопок
    private void selectButtonDay(boolean tod, boolean tom, boolean aft) {
        btToday.setPressed(tod);
        btTommorow.setPressed(tom);
        btAftertommorow.setPressed(aft);
    }


    //в onStart делаем проверку на наличие данных в адаптаре. При первом запуске адаптер пустой и
    //будет запущен поток.
    //при возврате через кнопку back адаптер будет не пустым поток не запуститься. что сохранит
    //состояние адаптера в положении перед открытием нового фрагмента
    @Override
    public  void onStart() {
        super.onStart();

        if (adapter == null){
            llListTime.setVisibility(View.INVISIBLE);
            llErorRfTS.setVisibility(View.INVISIBLE);

            threadOpenFragment = new Thread(runnableOpenFragment);
            threadOpenFragment.setDaemon(true);
            threadOpenFragment.start();

        }else {
            preSelectionButtonDay(selectDay);
            llListTime.setVisibility(View.VISIBLE);
        }

        if (getActivity() instanceof InterfaceChangeTitle){
            InterfaceChangeTitle listernerChangeTitle = (InterfaceChangeTitle) getActivity();
            listernerChangeTitle.changeTitle(R.string.title_RecordForTraining_Fragment, R.string.title_RecordForTraining_Fragment);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            iv_RfTS.setImageDrawable(getResources().getDrawable(R.drawable.backgroundfotovrtical, getContext().getTheme()));
        } else {
            iv_RfTS.setImageDrawable(getResources().getDrawable(R.drawable.backgroundfotovrtical));
        }

    }


    @Override
    public void onResume() {
        super.onResume();

    }

    public void onStop() {
        super.onStop();
        if (getLoaderManager().hasRunningLoaders()) {
            getLoaderManager().destroyLoader(LOADER_ID);
        }
    }
}
