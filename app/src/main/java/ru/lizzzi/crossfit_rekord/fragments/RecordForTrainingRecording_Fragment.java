package ru.lizzzi.crossfit_rekord.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
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
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import ru.lizzzi.crossfit_rekord.R;
import ru.lizzzi.crossfit_rekord.adapters.RecyclerAdapter_Record;
import ru.lizzzi.crossfit_rekord.loaders.RecordForTrainingRecording_LoadPeople_Loader;


public class RecordForTrainingRecording_Fragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Map>>{

    public static final String APP_PREFERENCES = "audata";
    public static final String APP_PREFERENCES_USERNAME = "Username";
    public static final String APP_PREFERENCES_USERCOUNT = "Usercount";
    SharedPreferences mSettings;

    public static final String Table_name = "recording_on_training";

    ListView lvRecord;
    TextView tvSelectedDay;
    TextView tvSelectedTime;
    TextView tvSelectedType;
    String username;
    String userid;

    String date_select;
    String time_select;

    public int LOADER_SHOW_LIST = 1;
    public int LOADER_WRITE_ITEM = 2;
    public int LOADER_DELETE_ITEM = 3;
    RecyclerAdapter_Record adapter;
    Button btRegister;
    Bundle bundle;

    private Loader<List<Map>> mLoader;

    Network_check network_check;

    LinearLayout llSelectedWorkout;
    ListView lvRecord2;
    Button btRecord;
    ProgressBar progressBar2;
    LinearLayout Layout_Error;
    LinearLayout Layout_emptylist;
    Date convarteDate;
    String date_select_full;
    String date_select_show;


    @SuppressLint("ResourceAsColor")
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_record_for_training_recording, container, false);

        lvRecord = v.findViewById(R.id.lvRecord);
        btRegister = v.findViewById(R.id.btRecord);
        tvSelectedDay = v.findViewById(R.id.tvSelectedDay);
        tvSelectedTime = v.findViewById(R.id.tvSelectedTime);
        tvSelectedType = v.findViewById(R.id.tvSelectedType);

        llSelectedWorkout = v.findViewById(R.id.llSelectedWorkout);
        progressBar2 = v.findViewById(R.id.progressBar2);
        Layout_Error = v.findViewById(R.id.Layout_Error);
        Layout_emptylist = v.findViewById(R.id.Layout_emptylist2);

        lvRecord.setVisibility(View.INVISIBLE);
        btRegister.setVisibility(View.INVISIBLE);
        progressBar2.setVisibility(View.VISIBLE);
        Layout_Error.setVisibility(View.INVISIBLE);
        Layout_emptylist.setVisibility(View.INVISIBLE);

        mSettings = getContext().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        username =  mSettings.getString(APP_PREFERENCES_USERNAME, "");

        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy");
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf2 = new SimpleDateFormat("EEEE, d MMMM");
        bundle = getArguments();
        date_select_show = bundle.getString("dateshow");
        date_select_full = bundle.getString("datefull");
        /*try{
            convarteDate = sdf.parse(tempDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }*/
        //String dsda = sdf2.format(date_select_full);
        date_select = date_select_show;
        time_select = bundle.getString("time");
        network_check = new Network_check(getContext());


        tvSelectedDay.setText(date_select);
        tvSelectedTime.setText(time_select);
        tvSelectedType.setText(bundle.getString("type"));
        if (Objects.equals(bundle.getString("type"), "CrossFit")){
            tvSelectedType.setBackgroundColor(R.color.color_Table_CrossFit);
        }
        if (Objects.equals(bundle.getString("type"), "On-Ramp")){
            tvSelectedType.setTextColor(R.color.color_Table_On_Ramp);
        }
        if (Objects.equals(bundle.getString("type"), "Open Gym")){
            tvSelectedType.setTextColor(R.color.color_Table_Open_Gym);
        }
        if (Objects.equals(bundle.getString("type"), "Stretching")){
            tvSelectedType.setTextColor(R.color.color_Table_Stretching);
        }
        if (Objects.equals(bundle.getString("type"), "CrossFit Kids")){
            tvSelectedType.setTextColor(R.color.color_Table_Crossfit_Kids);
        }
        if (Objects.equals(bundle.getString("type"), "Weightlifting/Athleticism")){
            tvSelectedType.setTextColor(R.color.color_Table_Weighlifting);
        }
        if (Objects.equals(bundle.getString("type"), "Gymnastics/Defence")){
            tvSelectedType.setTextColor(R.drawable.table_item_gymnastics_and_defence);

        }
        if (Objects.equals(bundle.getString("type"), "Rowing/Lady class")){
            tvSelectedType.setTextColor(R.drawable.table_item_rowing_and_ladyclass);
        }
        if (Objects.equals(bundle.getString("type"), "Weightlifting")){
            tvSelectedType.setTextColor(R.color.color_Table_Weighlifting);
        }

        bundle = new Bundle();
        bundle.putString(String.valueOf(RecordForTrainingRecording_LoadPeople_Loader.ARG_DATE), date_select_full);
        bundle.putString(String.valueOf(RecordForTrainingRecording_LoadPeople_Loader.ARG_TIME), time_select);
        mLoader = getLoaderManager().initLoader(LOADER_SHOW_LIST, bundle, this);
        mLoader.forceLoad();

        btRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                lvRecord.setVisibility(View.INVISIBLE);
                btRegister.setVisibility(View.INVISIBLE);
                progressBar2.setVisibility(View.VISIBLE);
                Layout_emptylist.setVisibility(View.INVISIBLE);

                if (network_check.checkInternet()){
                    if (userid.equals("noId")){
                        lvRecord.setVisibility(View.INVISIBLE);
                        StartLoader(2);

                    /*
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
                            //getLoaderManager().restartLoader(LOADER_ID, bundle,this).onContentChanged();
                        }

                        public void handleFault( BackendlessFault fault )
                        {
                            // an error has occurred, the error code can be retrieved with fault.getCode()
                            Toast.makeText(getContext(), "Не сохранилось. Попробуйте еще раз." , Toast.LENGTH_SHORT).show();
                        }
                    });*/
                    }else {
                        lvRecord.setVisibility(View.INVISIBLE);
                        StartLoader(3);

                        //bundle = new Bundle();
                        //bundle.putString(String.valueOf(RecordForTrainingRecording_LoadPeople_Loader.ARG_DATE), date_select);
                        //bundle.putString(String.valueOf(RecordForTrainingRecording_LoadPeople_Loader.ARG_TIME), time_select);
                        //bundle.putString(String.valueOf(RecordForTrainingRecording_LoadPeople_Loader.ARG_USERID), userid);
                        //mLoader3.commitContentChanged();


                        //mLoader3.reset();
                        //mLoader3.forceLoad();

                     /*
                    HashMap<String, String> record2 = new HashMap<>();
                    record2.put( "objectId", userid);
                    Backendless.Persistence.of( Table_name ).remove(record2, new AsyncCallback<Long>() {
                        @Override
                        public void handleResponse(Long response) {
                            //getLoaderManager().restartLoader(LOADER_ID, bundle,this).onContentChanged();
                        }

                        @Override
                        public void handleFault(BackendlessFault fault) {
                            Toast.makeText(getContext(), "Не удалилось. Попробуйте еще раз." , Toast.LENGTH_SHORT).show();
                        }
                    });*/
                    }
                }
            }
        });

        return v;
    }

    private void StartLoader(int loader_id){
        switch (loader_id){
            case 2:
                bundle.putString(String.valueOf(RecordForTrainingRecording_LoadPeople_Loader.ARG_USERNAME), username);
                mLoader = getLoaderManager().restartLoader(LOADER_WRITE_ITEM,bundle, this);
                mLoader.forceLoad();
                break;
            case 3:
                bundle.putString(String.valueOf(RecordForTrainingRecording_LoadPeople_Loader.ARG_USERID), userid);
                mLoader = getLoaderManager().restartLoader(LOADER_DELETE_ITEM,bundle, this);
                mLoader.forceLoad();
                break;
        }

    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        Loader<List<Map>> loader;
        loader = new RecordForTrainingRecording_LoadPeople_Loader(getContext(), args, id);
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<List<Map>> loader, List<Map> data) {

        if (data != null){
            if (CheckUser(data)) {
                btRegister.setText(R.string.delete_entry);

            }else {
                btRegister.setText(R.string.whrite_entry);
                userid = "noId";
            }

            adapter = new RecyclerAdapter_Record(getContext(), data, R.layout.item_lv_record);
            lvRecord.setAdapter(adapter);
            lvRecord.setVisibility(View.VISIBLE);
            btRegister.setVisibility(View.VISIBLE);
            progressBar2.setVisibility(View.INVISIBLE);
        }else {
            btRegister.setText(R.string.whrite_entry);
            userid = "noId";
            lvRecord.setVisibility(View.INVISIBLE);
            btRegister.setVisibility(View.VISIBLE);
            progressBar2.setVisibility(View.INVISIBLE);
            Layout_emptylist.setVisibility(View.VISIBLE);
        }


    }

    @Override
    public void onLoaderReset(Loader loader) {

    }

    private boolean CheckUser (List<Map> data){
        for (int i = 0; i< data.size(); i++){
            if (data.get(i).containsValue(String.valueOf(username))){
                userid = String.valueOf(data.get(i).get("objectId"));
                return true;
            }
        }
        return false;
    }
}
