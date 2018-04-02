package ru.lizzzi.crossfit_rekord.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
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
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.DataQueryBuilder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.lizzzi.crossfit_rekord.R;
import ru.lizzzi.crossfit_rekord.adapters.RecyclerAdapter_Record;
import ru.lizzzi.crossfit_rekord.adapters.RecyclerAdapter_RecordForTrainingSelect;
import ru.lizzzi.crossfit_rekord.interfaces.Listener_RecordForTrainingSelect;
import ru.lizzzi.crossfit_rekord.loaders.Table_Fragment_Loader;


public class RecordForTrainingSelect_Fragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Map>>{

    public static final String APP_PREFERENCES = "audata";
    public static final String APP_PREFERENCES_USERNAME = "Username";
    public static final String APP_PREFERENCES_USERCOUNT = "Usercount";
    SharedPreferences mSettings;

    public static final String Table_name = "recording_on_training";

    Date date = new Date();
    Calendar calendar = Calendar.getInstance();
    String time_select;
    String date_select;
    String username;
    String userid;

    RecyclerAdapter_Record adapter;
    RecyclerAdapter_RecordForTrainingSelect adapter2;
    ListView lvRecord;
    List<Map> results;
    List<Map> result2;
    List<String> list;

    Button btRegister;
    RecyclerView rvTreningTime;

    int iNumberOfDay;

    public int LOADER_ID = 1;
    Network_check network_check;


    public RecordForTrainingSelect_Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_record_for_training_select, container, false);

        mSettings = getContext().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        Button btToday = v.findViewById(R.id.btToday);
        Button btTommorow = v.findViewById(R.id.btTommorow);
        Button btAftertommorow = v.findViewById(R.id.btAftertommorow);
        rvTreningTime = v.findViewById(R.id.rvTrainingTime);


        lvRecord = v.findViewById(R.id.lvRecord);

        Button btt900 = v.findViewById(R.id.t900);
        Button btt1000 = v.findViewById(R.id.t1000);
        Button btt1100 = v.findViewById(R.id.t1100);
        Button btt1200 = v.findViewById(R.id.t1200);
        Button btt1300 = v.findViewById(R.id.t1300);
        Button btt1400 = v.findViewById(R.id.t1400);
        Button btt1500 = v.findViewById(R.id.t1500);
        Button btt1600 = v.findViewById(R.id.t1600);
        Button btt1700 = v.findViewById(R.id.t1700);
        Button btt1800 = v.findViewById(R.id.t1800);
        Button btt1900 = v.findViewById(R.id.t1900);
        Button btt2000 = v.findViewById(R.id.t2000);
        Button btt2100 = v.findViewById(R.id.t2100);

        btRegister = v.findViewById(R.id.btRecord1);

        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("EEEE dd MMMM");
        @SuppressLint("SimpleDateFormat") final SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM");

        calendar.add(Calendar.DAY_OF_YEAR, 1);
        final Date tomorrow = calendar.getTime();

        calendar.add(Calendar.DAY_OF_YEAR, 1);
        final Date aftertomorrow = calendar.getTime();

        final String currentToday = sdf.format(date);
        final String currentTommorow = sdf.format(tomorrow);
        final String currentAftertommorow = sdf.format(aftertomorrow);

        btToday.setText(currentToday);
        btTommorow.setText(currentTommorow);
        btAftertommorow.setText(currentAftertommorow);

        date_select = sdf2.format(date);
        time_select = getContext().getString(R.string.T900);
        username =  mSettings.getString(APP_PREFERENCES_USERNAME, "");

        network_check = new Network_check(getContext());
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        iNumberOfDay = c.get(Calendar.DAY_OF_WEEK)-1;
        Bundle bundle = new Bundle();
        bundle.putString(String.valueOf(Table_Fragment_Loader.ARG_WORD), String.valueOf(iNumberOfDay));
        getLoaderManager().initLoader(LOADER_ID, bundle, this).forceLoad();

        //DownloadData downloadData = new DownloadData();
        //downloadData.execute();

        btToday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                date_select = sdf2.format(date);
                time_select = getContext().getString(R.string.T900);
                StartNewAsyncTask();
            }
        });

        btTommorow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                date_select = sdf2.format(tomorrow);
                time_select = getContext().getString(R.string.T900);
                StartNewAsyncTask();
            }
        });

        btAftertommorow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                date_select = sdf2.format(aftertomorrow);
                time_select = getContext().getString(R.string.T900);
                StartNewAsyncTask();
            }
        });

        btt900.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                time_select = getContext().getString(R.string.T900);
                StartNewAsyncTask();
            }
        });

        btt1000.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                time_select = getContext().getString(R.string.T1000);
                StartNewAsyncTask();
            }
        });

        btt1100.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                time_select = getContext().getString(R.string.T1100);
                StartNewAsyncTask();
            }
        });

        btt1200.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                time_select = getContext().getString(R.string.T1200);
                StartNewAsyncTask();
            }
        });

        btt1300.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                time_select = getContext().getString(R.string.T1300);
                StartNewAsyncTask();
            }
        });

        btt1400.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                time_select = getContext().getString(R.string.T1400);
                StartNewAsyncTask();
            }
        });

        btt1500.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                time_select = getContext().getString(R.string.T1500);
                StartNewAsyncTask();
            }
        });

        btt1600.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                time_select = getContext().getString(R.string.T1600);
                StartNewAsyncTask();
            }
        });

        btt1700.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                time_select = getContext().getString(R.string.T1700);
                StartNewAsyncTask();
            }
        });

        btt1800.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                time_select = getContext().getString(R.string.T1800);
                StartNewAsyncTask();
            }
        });
        btt1900.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                time_select = getContext().getString(R.string.T1900);
                StartNewAsyncTask();
            }
        });

        btt2000.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                time_select = getContext().getString(R.string.T2000);
                StartNewAsyncTask();
            }
        });
        btt2100.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                time_select = getContext().getString(R.string.T2100);
                StartNewAsyncTask();
            }
        });

        btRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (userid.equals("noId")){

                    HashMap<String, String> record = new HashMap<>();
                    record.put( "data", date_select);
                    record.put( "time", time_select);
                    record.put( "username", username);

                    // save object asynchronously
                    Backendless.Persistence.of( Table_name ).save( record, new AsyncCallback<Map>() {
                        public void handleResponse( Map response )
                        {
                            // new Contact instance has been saved
                            Toast.makeText(getContext(), username + " " + date_select + " " + time_select , Toast.LENGTH_SHORT).show();
                            StartNewAsyncTask();
                        }

                        public void handleFault( BackendlessFault fault )
                        {
                            // an error has occurred, the error code can be retrieved with fault.getCode()
                            Toast.makeText(getContext(), "Не сохранилось. Попробуйте еще раз." , Toast.LENGTH_SHORT).show();
                        }
                    });
                }else {

                    HashMap<String, String> record2 = new HashMap<>();
                    record2.put( "objectId", userid);
                    Backendless.Persistence.of( Table_name ).remove(record2, new AsyncCallback<Long>() {
                        @Override
                        public void handleResponse(Long response) {
                            StartNewAsyncTask();
                        }

                        @Override
                        public void handleFault(BackendlessFault fault) {
                            Toast.makeText(getContext(), "Не удалилось. Попробуйте еще раз." , Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });


        return v;
    }

    private void StartNewAsyncTask(){
        DownloadData downloadData = new DownloadData();
        downloadData.execute();
    }

    @Override
    public Loader<List<Map>> onCreateLoader(int id, Bundle args) {
        if (network_check.checkInternet()) {
            Loader<List<Map>> loader;
            loader = new Table_Fragment_Loader(getContext(), args);
            return loader;
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<List<Map>> loader, List<Map> data) {
        if (data != null){
            adapter2 = new RecyclerAdapter_RecordForTrainingSelect(getContext(), data, new Listener_RecordForTrainingSelect(){
                @Override
                public void SelectTime(String start_time) {
                    time_select = start_time;

                    RecordForTrainingRecording_Fragment yfc =  new RecordForTrainingRecording_Fragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("time", time_select);
                    bundle.putString("date", date_select);
                    yfc.setArguments(bundle);
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction ft = fragmentManager.beginTransaction();
                    ft.replace(R.id.container, yfc);
                    ft.addToBackStack(null);
                    ft.commit();
                }

            });
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
            rvTreningTime.setLayoutManager(mLayoutManager);
            rvTreningTime.setAdapter(adapter2);
        }

    }

    @Override
    public void onLoaderReset(Loader<List<Map>> loader) {

    }

    @SuppressLint("StaticFieldLeak")
    private class DownloadData extends AsyncTask<Void,Void, Void>{

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            if (checkInternet()) {
                String whereClause = "data = '" + date_select + "' and time = '" + time_select + "'";
                DataQueryBuilder queryBuilder = DataQueryBuilder.create();
                queryBuilder.setWhereClause(whereClause);
            /*
            setPageSize(20)  - пока использую этот метод. Но в будущем надо бы переделать
            корректно на динамику.
             */
                queryBuilder.setPageSize(20);
                results = Backendless.Data.of("recording_on_training").find(queryBuilder);
                if (results.size() > 0 ){

                    final SharedPreferences.Editor editor = mSettings.edit();
                    editor.putString(APP_PREFERENCES_USERCOUNT, "1");
                    editor.apply();
                    adapter = new RecyclerAdapter_Record(getContext(), results, R.layout.item_lv_record);

                    for (int i = 0; i< results.size(); i++){
                        list = new ArrayList<String>(results.get(i).values());
                    }


                    String whereClause2 = "data = '" + date_select + "' and time = '" + time_select + "' and username = '" + username + "'";
                    DataQueryBuilder queryBuilder2 = DataQueryBuilder.create();
                    queryBuilder2.setWhereClause(whereClause2);
                    queryBuilder2.setPageSize(20);
                    result2 = Backendless.Data.of(Table_name).find(queryBuilder2);
                }

            }
            return null;
        }

        @Override
        public void onPostExecute(Void result){
            super.onPostExecute(result);

            if (adapter != null){
                lvRecord.setVisibility(View.VISIBLE);
                lvRecord.setAdapter(adapter);
            }
            if (results.size() > 0 ){
                if(list.contains(String.valueOf(username))){
                    btRegister.setText(R.string.delete_entry);
                    userid = String.valueOf( result2.get(0).get("objectId"));
                }

            }else {
                btRegister.setText(R.string.whrite_entry);
                userid = "noId";
                lvRecord.setVisibility(View.INVISIBLE);
            }


        }


    }


    public boolean checkInternet() {

        ConnectivityManager cm = (ConnectivityManager)getActivity().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm != null ? cm.getActiveNetworkInfo() : null;
        // проверка подключения
        return activeNetwork != null && activeNetwork.isConnected();

    }
}
