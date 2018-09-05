package ru.lizzzi.crossfit_rekord.fragments;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.List;
import java.util.Map;

import ru.lizzzi.crossfit_rekord.R;
import ru.lizzzi.crossfit_rekord.adapters.RecyclerAdapterNotification;
import ru.lizzzi.crossfit_rekord.loaders.NotificationLoader;
import ru.lizzzi.crossfit_rekord.interfaces.InterfaceChangeTitle;
import ru.lizzzi.crossfit_rekord.interfaces.ListernerNotification;

public class NotificationFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Map<String, Object>>> {

    private ListView rvNotoficationList;

    private Handler handlerOpenFragment;
    private Thread threadOpenFragment;

    private ProgressBar pbNotification;

    BroadcastReceiver br2;
    public final static String PARAM_TASK = "task";
    public final static String PARAM_RESULT = "result";
    public final static String PARAM_STATUS = "status";
    public final static String BROADCAST_ACTION = "ru.startandroid.develop.p0961servicebackbroadcast";

    final int TASK1_CODE = 1;

    public final static int STATUS_FINISH = 200;
    private IntentFilter intFilt;

    private RecyclerAdapterNotification adapterNotification;
    private Runnable runnableOpenFragment;

    @SuppressLint("HandlerLeak")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_notification, container, false);

        rvNotoficationList = v.findViewById(R.id.rvNotoficationList);
        pbNotification = v.findViewById(R.id.pbNotification);

        handlerOpenFragment = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Bundle bundle = msg.getData();
                String result_check = bundle.getString("result");
                if (result_check != null){
                    if (result_check.equals("false")){
                        pbNotification.setVisibility(View.INVISIBLE);
                    }else {
                        String status = bundle.getString("status");
                        if(status != null && status.equals("start")){
                            rvNotoficationList.setVisibility(View.INVISIBLE);
                            pbNotification.setVisibility(View.VISIBLE);
                        }else {
                            rvNotoficationList.setVisibility(View.VISIBLE);
                            pbNotification.setVisibility(View.INVISIBLE);
                        }
                    }
                }
            }
        };

        //поток запускаемый при создании экрана
        runnableOpenFragment = new Runnable() {
            @Override
            public void run() {
                Message msg = handlerOpenFragment.obtainMessage();
                Bundle bundle = new Bundle();
                bundle.putString("result", String.valueOf(true));
                bundle.putString("status", String.valueOf("start"));
                msg.setData(bundle);
                handlerOpenFragment.sendMessage(msg);
                loadNotificationAsyncTaskLoader();

            }
        };


        br2 = new BroadcastReceiver() {
            // действия при получении сообщений
            public void onReceive(Context context, Intent intent) {
                int task = intent.getIntExtra(PARAM_TASK, 0);
                int status = intent.getIntExtra(PARAM_STATUS, 0);

                // Ловим сообщения об окончании задач
                if (status == STATUS_FINISH) {
                    int result = intent.getIntExtra(PARAM_RESULT, 0);
                    switch (task) {
                        case TASK1_CODE:
                            if (result > 0){

                                threadOpenFragment = new Thread(runnableOpenFragment);
                                threadOpenFragment.setDaemon(true);
                                threadOpenFragment.start();
                            }

                            break;
                    }
                }
            }
        };
        // создаем фильтр для BroadcastReceiver
        intFilt = new IntentFilter(BROADCAST_ACTION);
        // регистрируем (включаем) BroadcastReceiver


        return  v;
    }

    private void loadNotificationAsyncTaskLoader(){

        int LOADER_ID = 1;
        getLoaderManager().initLoader(LOADER_ID, null, this).forceLoad();
    }

    @Override
    public Loader<List<Map<String, Object>>> onCreateLoader(int id, Bundle args) {
        NotificationLoader loader;
        loader = new NotificationLoader(getContext(), args);
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<List<Map<String, Object>>> loader, List<Map<String, Object>> data) {

        if (data != null){
            adapterNotification = new RecyclerAdapterNotification(getContext(), R.layout.item_rv_notification, data, new ListernerNotification() {
                @Override
                public void selectNotificationInList(String stdateNote, String stheader) {

                    Bundle bundle = new Bundle();
                    bundle.putString("dateNote", stdateNote);
                    bundle.putString("headerNote", stheader);
                    NotificationDataFragment yfc =  new NotificationDataFragment();
                    yfc.setArguments(bundle);
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction ft = fragmentManager.beginTransaction();
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    ft.replace(R.id.container, yfc);
                    ft.addToBackStack(null);
                    ft.commit();
                }
            });

            rvNotoficationList.setAdapter(adapterNotification);

            Message msg = handlerOpenFragment.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putString("result", String.valueOf(true));
            bundle.putString("status", String.valueOf("finish"));
            msg.setData(bundle);
            handlerOpenFragment.sendMessage(msg);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Map<String, Object>>> loader) {

    }

    @Override
    public  void onStart() {
        super.onStart();
        if (adapterNotification == null){
            rvNotoficationList.setVisibility(View.INVISIBLE);
            pbNotification.setVisibility(View.VISIBLE);
        }
        if (getActivity() instanceof InterfaceChangeTitle){
            InterfaceChangeTitle listernerChangeTitle = (InterfaceChangeTitle) getActivity();
            listernerChangeTitle.changeTitle(R.string.title_Notification_Fragment, R.string.title_Notification_Fragment);
        }

    }

    @Override
    public void onResume() {
        super.onResume();

        getActivity().registerReceiver(br2, intFilt);
        if (adapterNotification == null){
            threadOpenFragment = new Thread(runnableOpenFragment);
            threadOpenFragment.setDaemon(true);
            threadOpenFragment.start();
        }
    }

    public void onPause() {

        super.onPause();
        getActivity().unregisterReceiver(br2);
    }

}
