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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import ru.lizzzi.crossfit_rekord.R;
import ru.lizzzi.crossfit_rekord.adapters.RecyclerAdapterTable;
import ru.lizzzi.crossfit_rekord.interfaces.ListenerRecordForTrainingSelect;
import ru.lizzzi.crossfit_rekord.loaders.TableFragmentLoader;

public class TableFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Map>> {

    private ProgressBar pbProgressBar;
    private ListView lvItemsInTable;
    private Button buttonMonday;
    private Button buttonTuesday;
    private Button buttonWednesday;
    private Button buttonThursday;
    private Button buttonFriday;
    private Button buttonSaturday;
    private Button buttonSunday;
    private LinearLayout llLayoutError;

    private int iNumberOfDay; // выбранный пользователем день
    private int iPreviousOfDay; // предыдущий выбранный день
    private int LOADERID = 1; //идентефикатор loader'а

    private Handler handlerOpenFragment;
    private Thread threadOpenFragment;

    private Handler handlerClickOnbutton;
    private Thread threadClickOnbutton;

    private NetworkCheck NetworkCheck; //переменная для проврки сети
    private Toast toast; //toast для сообщений

    RecyclerAdapterTable adapter; //адаптер для списка тренировок

    private String dateSelectFull; //передает значение по поторому потом идет запрос в базу в следующем фрагменте
    private String dateSelectShow; //передает значение которое показывается в Textview следующего фрагмента
    private GregorianCalendar calendarday; //нужна для формирования дат для кнопок

    @SuppressLint("HandlerLeak")
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_table, container, false);
        getActivity().setTitle(R.string.title_Table_Fragment);

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
        lvItemsInTable = v.findViewById(R.id.lvTable);

        llLayoutError.setVisibility(View.INVISIBLE);
        lvItemsInTable.setVisibility(View.INVISIBLE);
        pbProgressBar.setVisibility(View.VISIBLE);

        iPreviousOfDay = 1;
        toast = Toast.makeText(getContext(), "Нет подключения", Toast.LENGTH_SHORT);



        //хэндлер для потока runnableOpenFragment
        handlerOpenFragment = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Bundle bundle = msg.getData();
                String result_check = bundle.getString("result");
                if (result_check != null){
                    if (result_check.equals("false")){
                        llLayoutError.setVisibility(View.VISIBLE);
                        pbProgressBar.setVisibility(View.INVISIBLE);
                    }
                }
            }
        };

        //поток запускаемый при создании экрана (запуск происходит из onResume)
        Runnable runnableOpenFragment = new Runnable() {
            @Override
            public void run() {
                NetworkCheck = new NetworkCheck(getContext());
                boolean resultCheck = NetworkCheck.checkInternet();
                if (resultCheck){
                    iNumberOfDay = 1;
                    firstStartAsyncTaskLoader(iNumberOfDay);

                }else {
                    Message msg = handlerOpenFragment.obtainMessage();
                    Bundle bundle = new Bundle();
                    bundle.putString("result", String.valueOf(false));
                    msg.setData(bundle);
                    handlerOpenFragment.sendMessage(msg);
                }
            }
        };
        threadOpenFragment = new Thread(runnableOpenFragment);
        threadOpenFragment.setDaemon(true);

        //хэндлер для потока runnableClickOnbutton
        handlerClickOnbutton = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Bundle bundle = msg.getData();
                String resultCheck = bundle.getString("result");
                if (resultCheck != null){
                    if (resultCheck.equals("false")){
                        if (llLayoutError.getVisibility() == View.INVISIBLE) {
                            preSelectionButtonDay(iPreviousOfDay);
                            toast.show();
                        }
                    }else {
                        lvItemsInTable.setVisibility(View.INVISIBLE);
                        pbProgressBar.setVisibility(View.VISIBLE);
                    }
                }
            }
        };

        //поток запускаемый кнопками выборающими дня недели
        Runnable runnableClickOnbutton = new Runnable() {
            @Override
            public void run() {
                toast.cancel();
                Message msg = handlerClickOnbutton.obtainMessage();
                Bundle bundle = new Bundle();
                NetworkCheck = new NetworkCheck(getContext());
                boolean resultCheck = NetworkCheck.checkInternet();
                if (resultCheck){
                    bundle.putString("result", String.valueOf(true));
                    if (llLayoutError.getVisibility() == View.VISIBLE) {
                        llLayoutError.setVisibility(View.INVISIBLE);
                    }
                    restartAsyncTaskLoader(iNumberOfDay);

                }else {
                    bundle.putString("result", String.valueOf(false));
                }
                msg.setData(bundle);
                handlerClickOnbutton.sendMessage(msg);
            }
        };
        threadClickOnbutton = new Thread(runnableClickOnbutton);
        threadClickOnbutton.setDaemon(true);

        buttonError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pbProgressBar.setVisibility(View.VISIBLE);
                llLayoutError.setVisibility(View.INVISIBLE);
                threadOpenFragment.run();
            }
        });

        buttonMonday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iNumberOfDay = 1;
                threadClickOnbutton.run();
            }
        });

        buttonTuesday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iNumberOfDay = 2;
                threadClickOnbutton.run();
            }
        });

        buttonWednesday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iNumberOfDay = 3;
                threadClickOnbutton.run();
            }
        });

        buttonThursday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iNumberOfDay = 4;
                threadClickOnbutton.run();
            }
        });

        buttonFriday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iNumberOfDay = 5;
                threadClickOnbutton.run();
            }
        });

        buttonSaturday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iNumberOfDay = 6;
                threadClickOnbutton.run();
            }
        });

        buttonSunday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iNumberOfDay = 7;
                threadClickOnbutton.run();
            }
        });

        return v;
    }


    private void firstStartAsyncTaskLoader(int iNumberOfDay){
        preSelectionButtonDay(8); //передаем 8, чтобы сбросить нажатие всех кнопок
        Bundle bundle = new Bundle();
        bundle.putString(String.valueOf(TableFragmentLoader.ARG_WORD), String.valueOf(iNumberOfDay));
        getLoaderManager().initLoader(LOADERID, bundle, this).forceLoad();
    }

    private void restartAsyncTaskLoader(int iNumberOfDay){
        preSelectionButtonDay(8); //передаем 8, чтобы сбросить нажатие всех кнопок
        Bundle bundle = new Bundle();
        bundle.putString(String.valueOf(TableFragmentLoader.ARG_WORD), String.valueOf(iNumberOfDay));
        getLoaderManager().restartLoader(LOADERID, bundle,this).onContentChanged();
    }

    @Override
    public Loader<List<Map>> onCreateLoader(int id, Bundle args) {
        Loader<List<Map>> loader;
        loader = new TableFragmentLoader(getContext(), args);
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<List<Map>> loader, List<Map> data) {
        pbProgressBar.setVisibility(View.INVISIBLE);
        if (data != null){
            adapter = new RecyclerAdapterTable(getContext(), data, R.layout.item_lv_table, new ListenerRecordForTrainingSelect() {
                @Override
                public void selectTime(String stStartTime, String stTypesItem) {

                    Calendar c = Calendar.getInstance();
                    calendarday = new GregorianCalendar();
                    Date today;
                    int numberDayOfWeek;
                    if (c.get(Calendar.DAY_OF_WEEK) == 1){
                        numberDayOfWeek = 7;
                    }else {
                        numberDayOfWeek = (c.get(Calendar.DAY_OF_WEEK)-1);
                    }
                    int dayOfWeek = iNumberOfDay-numberDayOfWeek;
                    if ((dayOfWeek == 0) || (dayOfWeek == 1) || (dayOfWeek == 2) || (dayOfWeek == -5) || (dayOfWeek == -6)){
                        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdfDataShow = new SimpleDateFormat("EEEE dd MMMM");
                        @SuppressLint("SimpleDateFormat") final SimpleDateFormat sdfDataFull = new SimpleDateFormat("dd/MM");
                        calendarday.add(Calendar.DAY_OF_YEAR, dayOfWeek);
                        today = calendarday.getTime();
                        dateSelectShow = sdfDataShow.format(today);
                        dateSelectFull = sdfDataFull.format(today);

                        RecordForTrainingRecordingFragment yfc =  new RecordForTrainingRecordingFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("time", stStartTime);
                        bundle.putString("datefull", dateSelectFull);
                        bundle.putString("dateshow", dateSelectShow);
                        bundle.putString("type", stTypesItem);
                        yfc.setArguments(bundle);
                        FragmentManager fragmentManager = getFragmentManager();
                        FragmentTransaction ft = fragmentManager.beginTransaction();
                        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                        ft.replace(R.id.container, yfc);
                        ft.addToBackStack(null);
                        ft.commit();

                    }else {
                        @SuppressLint("SimpleDateFormat") final SimpleDateFormat sdfToast = new SimpleDateFormat("EEEE");
                        calendarday.add(Calendar.DAY_OF_YEAR, 0);
                        today = calendarday.getTime();
                        String toastToday = sdfToast.format(today);
                        Toast toast = Toast.makeText(getContext(), "Запись возможна на сегодня (" + toastToday  + ") и два дня вперед", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }

                }
            });
            lvItemsInTable.setAdapter(adapter);
            iPreviousOfDay = iNumberOfDay;
            preSelectionButtonDay(iNumberOfDay);

            lvItemsInTable.setVisibility(View.VISIBLE);
        }else {
            Toast.makeText(getContext(), "Нет данных", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Map>> loader) {
    }

    //метод подготоавливающий состояние кнопок в зависимости от выбранного дня
    private void preSelectionButtonDay(int iDayOfWeek){
        if (iDayOfWeek == 1 ){
            selectButtonDay(true, false, false, false, false, false, false);

        }else if (iDayOfWeek == 2){
            selectButtonDay(false, true, false, false, false, false, false);

        }else if (iDayOfWeek == 3){
            selectButtonDay(false, false, true, false, false, false, false);

        }else if (iDayOfWeek == 4){
            selectButtonDay(false, false, false, true, false, false, false);

        }else if (iDayOfWeek == 5){
            selectButtonDay(false, false, false, false, true, false, false);

        }else if (iDayOfWeek == 6){
            selectButtonDay(false, false, false, false, false, true, false);

        }else if (iDayOfWeek == 7){
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

    //в onResume делаем проверку на наличие данных в адаптаре. При первом запуске адаптер пустой и
    //будет запущен поток.
    //при возврате через кнопку back адаптер будет не пустым поток не запуститься. что сохранит
    //состояние адаптера в положении перед открытием нового фрагмента
    @Override
    public void onResume() {
        super.onResume();
        if (adapter == null){
            threadOpenFragment.start();
        }
    }
}
