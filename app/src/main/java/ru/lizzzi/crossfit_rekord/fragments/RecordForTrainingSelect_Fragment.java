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
import android.view.LayoutInflater;
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
import ru.lizzzi.crossfit_rekord.interfaces.ListenerRecordForTrainingSelect;
import ru.lizzzi.crossfit_rekord.loaders.Table_Fragment_Loader;


public class RecordForTrainingSelect_Fragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Map>>{

    private LinearLayout llEror_RfTS;
    private LinearLayout llListTime;
    private RecyclerView rvTreningTime;
    private ProgressBar pbRfTS;

    private RecyclerAdapterRecordForTrainingSelect adapter;

    private Date date; //показывает сегодняшний день
    private GregorianCalendar calendarday; //нужна для формирования дат для кнопок
    private GregorianCalendar numberdayweek; // для преобразования выбранного дня в int

    private String date_select_full; //передает значение по поторому потом идет запрос в базу в следующем фрагменте
    private String date_select_show; //передает значение которое показывается в Textview следующего фрагмента

    private int iNumberOfDay; // выбранный пользователем день
    private  int LOADER_ID = 1; //идентефикатор loader'а

    private NetworkCheck NetworkCheck;//переменная для проврки сети

    private Handler handler_open_fragment;
    private Thread thread_open_fragment;

    private Thread thread_click_onbutton;

    @SuppressLint("HandlerLeak")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_record_for_training_select, container, false);
        getActivity().setTitle(R.string.title_RecordForTraining_Fragment);

        Button btToday = v.findViewById(R.id.btToday);
        Button btTommorow = v.findViewById(R.id.btTommorow);
        Button btAftertommorow = v.findViewById(R.id.btAftertommorow);
        Button button_error = v.findViewById(R.id.button6);
        rvTreningTime = v.findViewById(R.id.rvTrainingTime);
        llEror_RfTS= v.findViewById(R.id.llEror_RfTS);
        llListTime = v.findViewById(R.id.llListTime);
        pbRfTS = v.findViewById(R.id.pbRfTS);

        llListTime.setVisibility(View.INVISIBLE);
        llEror_RfTS.setVisibility(View.INVISIBLE);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        rvTreningTime.setLayoutManager(mLayoutManager);
        rvTreningTime.setAdapter(adapter);

        numberdayweek = new GregorianCalendar();

        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("EEEE dd MMMM");
        @SuppressLint("SimpleDateFormat") final SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM");

        //хэндлер для обоих потоков. Какой именно поток вызвал хэндлер передается в key
        handler_open_fragment = new Handler() {
            @SuppressLint("ShowToast")
            @Override
            public void handleMessage(Message msg) {
                Bundle bundle = msg.getData();
                String switchs = bundle.getString("switch"); //показывает какой поток вызвал
                String result_check;
                if (switchs != null){
                    if (switchs.equals("open")){ //поток при первом запуске экрана
                        result_check = bundle.getString("open");
                        if (result_check != null) {
                            if (result_check.equals("false")) {
                                llEror_RfTS.setVisibility(View.VISIBLE);
                                pbRfTS.setVisibility(View.INVISIBLE);
                            }
                        }
                    }else{
                        result_check = bundle.getString("onclick"); //поток от нажатия кнопок
                        if (result_check != null) {
                            if (result_check.equals("false")) {
                                if (llEror_RfTS.getVisibility() == View.INVISIBLE){
                                    Toast.makeText(getContext(), "Нет подключения", Toast.LENGTH_SHORT).show();
                                }
                            }else {
                                llListTime.setVisibility(View.INVISIBLE);
                                pbRfTS.setVisibility(View.VISIBLE);
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
                NetworkCheck = new NetworkCheck(getContext());
                boolean result_check = NetworkCheck.checkInternet();
                if (result_check){
                    iNumberOfDay = numberdayweek.get(Calendar.DAY_OF_WEEK)-1;
                    FirstStartAsyncTaskLoader(iNumberOfDay);

                }else {
                    Message msg = handler_open_fragment.obtainMessage();
                    Bundle bundle = new Bundle();
                    bundle.putString("open", String.valueOf(false));
                    bundle.putString("switch", "open");
                    msg.setData(bundle);
                    handler_open_fragment.sendMessage(msg);
                }
            }
        };
        thread_open_fragment = new Thread(runnable_open_fragment);
        thread_open_fragment.setDaemon(true);

        //поток запускаемый кнопками выборающими дня недели
        Runnable runnable_click_onbutton = new Runnable() {
            @Override
            public void run() {
                Message msg = handler_open_fragment.obtainMessage();
                Bundle bundle = new Bundle();
                NetworkCheck = new NetworkCheck(getContext());
                boolean result_check = NetworkCheck.checkInternet();
                if (result_check){
                    bundle.putString("onclick", String.valueOf(true));
                    if (llEror_RfTS.getVisibility() == View.VISIBLE) {
                        llEror_RfTS.setVisibility(View.INVISIBLE);
                    }
                    RestartAsyncTaskLoader(iNumberOfDay);

                }else {
                    bundle.putString("onclick", String.valueOf(false));
                }
                bundle.putString("switch", "onclick");
                msg.setData(bundle);
                handler_open_fragment.sendMessage(msg);
            }
        };
        thread_click_onbutton = new Thread(runnable_click_onbutton);
        thread_click_onbutton.setDaemon(true);

        //получаю значения для кнопок
        date = new Date();
        calendarday = new GregorianCalendar();
        calendarday.add(Calendar.DAY_OF_YEAR, 1);
        final Date tomorrow = calendarday.getTime();

        calendarday.add(Calendar.DAY_OF_YEAR, 1);
        final Date aftertomorrow = calendarday.getTime();

        final String currentToday = sdf.format(date);
        final String currentTomorrow = sdf.format(tomorrow);
        final String currentAftertommorow = sdf.format(aftertomorrow);

        btToday.setText(currentToday);
        btTommorow.setText(currentTomorrow);
        btAftertommorow.setText(currentAftertommorow);

        calendarday.setTime(date);
        date_select_full = sdf2.format(date);
        date_select_show = currentToday;

        button_error.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                llEror_RfTS.setVisibility(View.INVISIBLE);
                pbRfTS.setVisibility(View.VISIBLE);
                thread_open_fragment.run();
            }
        });

        btToday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendarday.setTime(date);
                iNumberOfDay = numberdayweek.get(Calendar.DAY_OF_WEEK)-1;
                date_select_full = sdf2.format(date);
                date_select_show = currentToday;
                thread_click_onbutton.run();
            }
        });

        btTommorow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendarday.setTime(tomorrow);
                iNumberOfDay = numberdayweek.get(Calendar.DAY_OF_WEEK);
                date_select_full = sdf2.format(tomorrow);
                date_select_show = currentTomorrow;
                thread_click_onbutton.run();
            }
        });

        btAftertommorow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendarday.setTime(aftertomorrow);
                iNumberOfDay = numberdayweek.get(Calendar.DAY_OF_WEEK)+1;
                date_select_full = sdf2.format(aftertomorrow);
                date_select_show = currentAftertommorow;
                thread_click_onbutton.run();
            }
        });

        return v;
    }

    private void FirstStartAsyncTaskLoader(int day_select){
        Bundle bundle = new Bundle();
        bundle.putString(String.valueOf(Table_Fragment_Loader.ARG_WORD), String.valueOf(day_select));
        getLoaderManager().initLoader(LOADER_ID, bundle, this).forceLoad();
    }

    private void RestartAsyncTaskLoader(int day_select){
        Bundle bundle = new Bundle();
        bundle.putString(String.valueOf(Table_Fragment_Loader.ARG_WORD), String.valueOf(day_select));
        getLoaderManager().restartLoader(LOADER_ID, bundle,this).onContentChanged();
    }
    @Override
    public Loader<List<Map>> onCreateLoader(int id, Bundle args) {
        if (NetworkCheck.checkInternet()) {
            Loader<List<Map>> loader;
            loader = new Table_Fragment_Loader(getContext(), args);
            return loader;
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<List<Map>> loader, List<Map> data) {
        if (data != null){
            adapter = new RecyclerAdapterRecordForTrainingSelect(getContext(), data, new ListenerRecordForTrainingSelect(){
                @Override
                public void selectTime(String stStartTime, String stTypesItem) {
                    RecordForTrainingRecording_Fragment yfc =  new RecordForTrainingRecording_Fragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("time", stStartTime);
                    bundle.putString("datefull", date_select_full);
                    bundle.putString("dateshow", date_select_show);
                    bundle.putString("type", stTypesItem);
                    yfc.setArguments(bundle);
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction ft = fragmentManager.beginTransaction();
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    ft.replace(R.id.container, yfc);
                    ft.addToBackStack(null);
                    ft.commit();
                }
            });
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
            rvTreningTime.setLayoutManager(mLayoutManager);
            rvTreningTime.setAdapter(adapter);

            pbRfTS.setVisibility(View.INVISIBLE);
            llListTime.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Map>> loader) {

    }

    //в onResume делаем проверку на наличие данных в адаптаре. При первом запуске адаптер пустой и
    //будет запущен поток.
    //при возврате через кнопку back адаптер будет не пустым поток не запуститься. что сохранит
    //состояние адаптера в положении перед открытием нового фрагмента
    @Override
    public void onResume() {
        super.onResume();
        if (adapter == null){
            thread_open_fragment.start();
        }
    }

    public void onStop() {
        super.onStop();
        if (getLoaderManager().hasRunningLoaders()) {
            getLoaderManager().destroyLoader(LOADER_ID);
        }
    }
}
