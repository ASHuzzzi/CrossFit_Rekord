package ru.lizzzi.crossfit_rekord.fragments;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
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
import android.widget.ProgressBar;

import java.util.List;
import java.util.Map;

import ru.lizzzi.crossfit_rekord.R;
import ru.lizzzi.crossfit_rekord.adapters.RecyclerAdapterNotification;
import ru.lizzzi.crossfit_rekord.loaders.NotificationLoader;
import ru.lizzzi.crossfit_rekord.interfaces.InterfaceChangeTitle;
import ru.lizzzi.crossfit_rekord.interfaces.ListernerNotification;

public class NotificationFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Map<String, Object>>> {

    private RecyclerView rvNotoficationList;

    private ProgressBar pbNotification;

    BroadcastReceiver br2;
    public final static String PARAM_TASK = "task";
    public final static String PARAM_RESULT = "result";
    public final static String PARAM_STATUS = "status";
    public final static String BROADCAST_ACTION = "ru.startandroid.develop.p0961servicebackbroadcast";

    final int TASK1_CODE = 1;

    public final static int STATUS_FINISH = 200;
    private IntentFilter intFilt;

    @SuppressLint("HandlerLeak")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_notification, container, false);

        rvNotoficationList = v.findViewById(R.id.rvNotoficationList);
        pbNotification = v.findViewById(R.id.pbNotification);

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

                                rvNotoficationList.setVisibility(View.INVISIBLE);
                                pbNotification.setVisibility(View.VISIBLE);
                                loadNotificationAsyncTaskLoader();
                            }

                            break;
                    }
                }
            }
        };
        // создаем фильтр для BroadcastReceiver
        intFilt = new IntentFilter(BROADCAST_ACTION);

        return  v;
    }

    private void loadNotificationAsyncTaskLoader(){
        int LOADER_ID = 1;
        getLoaderManager().restartLoader(LOADER_ID, null, this).forceLoad();

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
            RecyclerAdapterNotification adapterNotification = new RecyclerAdapterNotification(getContext(), data, new ListernerNotification() {
                @Override
                public void selectNotificationInList(String stdateNote, String stheader) {

                    Bundle bundle = new Bundle();
                    bundle.putString("dateNote", stdateNote);
                    bundle.putString("headerNote", stheader);
                    NotificationDataFragment yfc = new NotificationDataFragment();
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
            rvNotoficationList.setLayoutManager(mLayoutManager);
            rvNotoficationList.setAdapter(adapterNotification);

            rvNotoficationList.setVisibility(View.VISIBLE);
            pbNotification.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Map<String, Object>>> loader) {

    }

    @Override
    public  void onStart() {
        super.onStart();

        if (getActivity() instanceof InterfaceChangeTitle){
            InterfaceChangeTitle listernerChangeTitle = (InterfaceChangeTitle) getActivity();
            listernerChangeTitle.changeTitle(R.string.title_Notification_Fragment, R.string.title_Notification_Fragment);
        }

        // регистрируем (включаем) BroadcastReceiver
        getActivity().registerReceiver(br2, intFilt);

        //TODO добавить позже проверку на новые уведомления, чтобы не грузить весь список каждый раз
        rvNotoficationList.setVisibility(View.INVISIBLE);
        pbNotification.setVisibility(View.VISIBLE);
        loadNotificationAsyncTaskLoader();

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void onPause() {

        super.onPause();
        getActivity().unregisterReceiver(br2);
    }

}
