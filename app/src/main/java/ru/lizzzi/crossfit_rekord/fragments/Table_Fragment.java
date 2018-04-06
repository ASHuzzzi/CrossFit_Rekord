package ru.lizzzi.crossfit_rekord.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.List;
import java.util.Map;

import ru.lizzzi.crossfit_rekord.R;
import ru.lizzzi.crossfit_rekord.adapters.RecyclerAdapter_Table;
import ru.lizzzi.crossfit_rekord.loaders.Table_Fragment_Loader;

public class Table_Fragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Map>> {

    private ProgressBar pbProgressBar;
    private ListView lvItemsInTable;
    private Button button_monday;
    private Button button_tuesday;
    private Button button_wednesday;
    private Button button_thursday;
    private Button button_friday;
    private Button button_saturday;
    private Button button_sunday;
    private LinearLayout llLayoutError;

    private int iNumberOfDay; // выбранный пользователем день
    private int iPreviousOfDay; // предыдущий выбранный день
    private int LOADER_ID = 1; //идентефикатор loader'а

    private Handler handler_open_fragment;
    private Thread thread_open_fragment;

    private Handler handler_click_onbutton;
    private Thread thread_click_onbutton;

    private Network_check network_check; //переменная для проврки сети
    private Toast toast; //toast для сообщений

    RecyclerAdapter_Table adapter; //адаптер для списка тренировок

    @SuppressLint("HandlerLeak")
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_table, container, false);
        getActivity().setTitle(R.string.title_Table_Fragment);

        button_monday= v.findViewById(R.id.day_1);
        button_tuesday= v.findViewById(R.id.day_2);
        button_wednesday= v.findViewById(R.id.day_3);
        button_thursday= v.findViewById(R.id.day_4);
        button_friday= v.findViewById(R.id.day_5);
        button_saturday= v.findViewById(R.id.day_6);
        button_sunday= v.findViewById(R.id.day_7);
        Button button_error = v.findViewById(R.id.button5);
        llLayoutError = v.findViewById(R.id.Layout_Error);
        pbProgressBar = v.findViewById(R.id.progressBar);
        lvItemsInTable = v.findViewById(R.id.lvTable);

        llLayoutError.setVisibility(View.INVISIBLE);
        lvItemsInTable.setVisibility(View.INVISIBLE);
        pbProgressBar.setVisibility(View.VISIBLE);

        iPreviousOfDay = 1;
        toast = Toast.makeText(getContext(), "Нет подключения", Toast.LENGTH_SHORT);

        //хэндлер для потока runnable_open_fragment
        handler_open_fragment = new Handler() {
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
        Runnable runnable_open_fragment = new Runnable() {
            @Override
            public void run() {
                network_check = new Network_check(getContext());
                boolean result_check = network_check.checkInternet();
                if (result_check){
                    iNumberOfDay = 1;
                    FirstStartAsyncTaskLoader(iNumberOfDay);

                }else {
                    Message msg = handler_open_fragment.obtainMessage();
                    Bundle bundle = new Bundle();
                    bundle.putString("result", String.valueOf(false));
                    msg.setData(bundle);
                    handler_open_fragment.sendMessage(msg);
                }
            }
        };
        thread_open_fragment = new Thread(runnable_open_fragment);
        thread_open_fragment.setDaemon(true);

        //хэндлер для потока runnable_click_onbutton
        handler_click_onbutton = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Bundle bundle = msg.getData();
                String result_check = bundle.getString("result");
                if (result_check != null){
                    if (result_check.equals("false")){
                        if (llLayoutError.getVisibility() == View.INVISIBLE) {
                            PreSelectionButtonDay(iPreviousOfDay);
                            toast.show();
                        }
                    }else {
                        lvItemsInTable.setVisibility(View.INVISIBLE);
                        pbProgressBar.setVisibility(View.VISIBLE);
                    }
                }
            }
        };

        //поток запускаемыq кнопками выборающими дня недели
        Runnable runnable_click_onbutton = new Runnable() {
            @Override
            public void run() {
                toast.cancel();
                Message msg = handler_click_onbutton.obtainMessage();
                Bundle bundle = new Bundle();
                network_check = new Network_check(getContext());
                boolean result_check = network_check.checkInternet();
                if (result_check){
                    bundle.putString("result", String.valueOf(true));
                    if (llLayoutError.getVisibility() == View.VISIBLE) {
                        llLayoutError.setVisibility(View.INVISIBLE);
                    }
                    RestartAsyncTaskLoader(iNumberOfDay);

                }else {
                    bundle.putString("result", String.valueOf(false));
                }
                msg.setData(bundle);
                handler_click_onbutton.sendMessage(msg);
            }
        };
        thread_click_onbutton = new Thread(runnable_click_onbutton);
        thread_click_onbutton.setDaemon(true);

        button_error.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pbProgressBar.setVisibility(View.VISIBLE);
                llLayoutError.setVisibility(View.INVISIBLE);
                thread_open_fragment.run();
            }
        });

        button_monday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iNumberOfDay = 1;
                thread_click_onbutton.run();
            }
        });

        button_tuesday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iNumberOfDay = 2;
                thread_click_onbutton.run();
            }
        });

        button_wednesday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iNumberOfDay = 3;
                thread_click_onbutton.run();
            }
        });

        button_thursday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iNumberOfDay = 4;
                thread_click_onbutton.run();
            }
        });

        button_friday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iNumberOfDay = 5;
                thread_click_onbutton.run();
            }
        });

        button_saturday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iNumberOfDay = 6;
                thread_click_onbutton.run();
            }
        });

        button_sunday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iNumberOfDay = 7;
                thread_click_onbutton.run();
            }
        });

        return v;
    }


    private void FirstStartAsyncTaskLoader(int iNumberOfDay){
        PreSelectionButtonDay(8); //передаем 8, чтобы сбросить нажатие всех кнопок
        Bundle bundle = new Bundle();
        bundle.putString(String.valueOf(Table_Fragment_Loader.ARG_WORD), String.valueOf(iNumberOfDay));
        getLoaderManager().initLoader(LOADER_ID, bundle, this).forceLoad();
    }

    private void RestartAsyncTaskLoader(int iNumberOfDay){
        PreSelectionButtonDay(8); //передаем 8, чтобы сбросить нажатие всех кнопок
        Bundle bundle = new Bundle();
        bundle.putString(String.valueOf(Table_Fragment_Loader.ARG_WORD), String.valueOf(iNumberOfDay));
        getLoaderManager().restartLoader(LOADER_ID, bundle,this).onContentChanged();
    }

    @Override
    public Loader<List<Map>> onCreateLoader(int id, Bundle args) {
        Loader<List<Map>> loader;
        loader = new Table_Fragment_Loader(getContext(), args);
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<List<Map>> loader, List<Map> data) {
        pbProgressBar.setVisibility(View.INVISIBLE);
        if (data != null){
            adapter = new RecyclerAdapter_Table(getContext(), data, R.layout.item_lv_table);
            lvItemsInTable.setAdapter(adapter);
            iPreviousOfDay = iNumberOfDay;
            PreSelectionButtonDay(iNumberOfDay);

            lvItemsInTable.setVisibility(View.VISIBLE);
        }else {
            Toast.makeText(getContext(), "Нет данных", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Map>> loader) {
    }

    //метод подготоавливающий состояние кнопок в зависимости от выбранного дня
    private void PreSelectionButtonDay(int iDayOfWeek){
        if (iDayOfWeek == 1 ){
            SelectButtonDay(true, false, false, false, false, false, false);

        }else if (iDayOfWeek == 2){
            SelectButtonDay(false, true, false, false, false, false, false);

        }else if (iDayOfWeek == 3){
            SelectButtonDay(false, false, true, false, false, false, false);

        }else if (iDayOfWeek == 4){
            SelectButtonDay(false, false, false, true, false, false, false);

        }else if (iDayOfWeek == 5){
            SelectButtonDay(false, false, false, false, true, false, false);

        }else if (iDayOfWeek == 6){
            SelectButtonDay(false, false, false, false, false, true, false);

        }else if (iDayOfWeek == 7){
            SelectButtonDay(false, false, false, false, false, false, true);

        }else {
            SelectButtonDay(false, false, false, false, false, false, false);
        }
    }

    //метод применяющий выбор кнопок
    private void SelectButtonDay(boolean m, boolean tu, boolean w, boolean th, boolean f, boolean sa, boolean su) {
        button_monday.setPressed(m);
        button_tuesday.setPressed(tu);
        button_wednesday.setPressed(w);
        button_thursday.setPressed(th);
        button_friday.setPressed(f);
        button_saturday.setPressed(sa);
        button_sunday.setPressed(su);
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
}
