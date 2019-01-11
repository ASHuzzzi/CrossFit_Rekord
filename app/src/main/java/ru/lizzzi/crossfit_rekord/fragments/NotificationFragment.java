package ru.lizzzi.crossfit_rekord.fragments;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import java.util.List;
import java.util.Map;

import ru.lizzzi.crossfit_rekord.R;
import ru.lizzzi.crossfit_rekord.activity.MainActivity;
import ru.lizzzi.crossfit_rekord.adapters.RecyclerAdapterNotification;
import ru.lizzzi.crossfit_rekord.data.NotificationDBHelper;
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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_notification, container, false);

        rvNotoficationList = v.findViewById(R.id.rvNotoficationList);
        pbNotification = v.findViewById(R.id.pbNotification);

        rvNotoficationList.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                return false;
            }
        });

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

    @NonNull
    @Override
    public Loader<List<Map<String, Object>>> onCreateLoader(int id, Bundle args) {
        NotificationLoader loader;
        loader = new NotificationLoader(getContext());
        return loader;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Map<String, Object>>> loader, List<Map<String, Object>> data) {

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

                @Override
                public void deleteNotificationInList(final String stdateNote) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage("Удалить новость?")
                            .setCancelable(false)
                            .setPositiveButton("Да",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            long lDateNote = Long.valueOf(stdateNote);

                                            NotificationDBHelper mDBHelper = new NotificationDBHelper(getContext());
                                            mDBHelper.deleteNotification(lDateNote);

                                            rvNotoficationList.setVisibility(View.INVISIBLE);
                                            pbNotification.setVisibility(View.VISIBLE);
                                            loadNotificationAsyncTaskLoader();

                                            Intent intent = new Intent(MainActivity.BROADCAST_ACTION);
                                            String PARAM_TASK = "task";
                                            String PARAM_RESULT = "result";
                                            String PARAM_STATUS = "status";
                                            intent.putExtra(PARAM_TASK, 2);
                                            intent.putExtra(PARAM_STATUS, MainActivity.STATUS_FINISH);
                                            intent.putExtra(PARAM_RESULT, 1);
                                            getActivity().sendBroadcast(intent);
                                        }
                                    })
                            .setNegativeButton("Нет",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                    alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorAccent));
                    alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorPrimary));


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
    public void onLoaderReset(@NonNull Loader<List<Map<String, Object>>> loader) {

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
