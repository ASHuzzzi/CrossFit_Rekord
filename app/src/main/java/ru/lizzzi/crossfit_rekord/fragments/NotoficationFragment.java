package ru.lizzzi.crossfit_rekord.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import java.util.List;
import java.util.Map;

import ru.lizzzi.crossfit_rekord.R;
import ru.lizzzi.crossfit_rekord.loaders.NotificationLoader;

public class NotoficationFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Map>>{

    private RecyclerView rvNotoficationList;

    private Handler handlerOpenFragment;
    private Thread threadOpenFragment;

    private NetworkCheck NetworkCheck; //переменная для проврки сети

    private ProgressBar pbNotification;

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
        Runnable runnableOpenFragment = new Runnable() {
            @Override
            public void run() {
                Message msg = handlerOpenFragment.obtainMessage();
                Bundle bundle = new Bundle();
                bundle.putString("result", String.valueOf(true));
                bundle.putString("status", String.valueOf("start"));
                msg.setData(bundle);
                handlerOpenFragment.sendMessage(msg);

                NetworkCheck = new NetworkCheck(getContext());
                boolean resultCheck = NetworkCheck.checkInternet();
                if (resultCheck){
                    loadNotificationAsyncTaskLoader();

                }else {
                    bundle.putString("result", String.valueOf(false));
                    msg.setData(bundle);
                    handlerOpenFragment.sendMessage(msg);
                }

            }
        };
        threadOpenFragment = new Thread(runnableOpenFragment);
        threadOpenFragment.setDaemon(true);

        return  v;
    }

    private void loadNotificationAsyncTaskLoader(){

        int LOADER_ID = 1;
        getLoaderManager().initLoader(LOADER_ID, null, this).forceLoad();
    }

    @Override
    public Loader<List<Map>> onCreateLoader(int id, Bundle args) {
        Loader<List<Map>> loader;
        loader = new NotificationLoader(getContext(), args);
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<List<Map>> loader, List<Map> data) {


        Message msg = handlerOpenFragment.obtainMessage();
        Bundle bundle = new Bundle();
        bundle.putString("result", String.valueOf(true));
        bundle.putString("status", String.valueOf("finish"));
        msg.setData(bundle);
        handlerOpenFragment.sendMessage(msg);
    }

    @Override
    public void onLoaderReset(Loader<List<Map>> loader) {

    }

    @Override
    public  void onStart() {
        super.onStart();
        if (threadOpenFragment.getState() == Thread.State.NEW){
            rvNotoficationList.setVisibility(View.INVISIBLE);
            pbNotification.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onResume() {
        super.onResume();

        if (threadOpenFragment.getState() == Thread.State.NEW){
            threadOpenFragment.start();
        }
    }

}
