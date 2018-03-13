package ru.lizzzi.crossfit_rekord;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.backendless.Backendless;
import com.backendless.persistence.DataQueryBuilder;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class Calendar_wod_Fragment extends Fragment implements OnDateSelectedListener {

    List<Map> results;
    List<String> list;
    Date date;
    MaterialCalendarView mcv;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        final View v = inflater.inflate(R.layout.fragment_calendar_wod, container, false);

        mcv = v.findViewById(R.id.calendarView);
        //mcv.setSelectionMode(MaterialCalendarView.SELECTION_MODE_NONE);
        mcv.setOnDateChangedListener(this);

        @SuppressLint("StaticFieldLeak")
        class DownloadData extends AsyncTask<Void,Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {

                DataQueryBuilder queryBuilder = DataQueryBuilder.create();
                queryBuilder.setPageSize(100);
                queryBuilder.addGroupBy("date_session");
                results = Backendless.Data.of("Training_sessions").find(queryBuilder);

                return null;
            }

            public void onPostExecute(Void result){
                super.onPostExecute(result);



                for (int i = 0; i < results.size(); i++){
                    list = new ArrayList<String>(results.get(i).values());
                    String sr = String.valueOf(list.get(3));
                    SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
                    try {
                        date = sdf.parse(sr);

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    mcv.setDateSelected(CalendarDay.from(date), true);
                }




            }
        }

        if (checkInternet()){
            DownloadData downloadData = new DownloadData();
            downloadData.execute();
        }


        return v;
    }

    public boolean checkInternet() {

        ConnectivityManager cm = (ConnectivityManager)getActivity().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm != null ? cm.getActiveNetworkInfo() : null;
        // проверка подключения
        return activeNetwork != null && activeNetwork.isConnected();

    }


    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
        String selectedDate;
        String Day;
        String Month;

        if (date.getDay() <10){
            Day = "0" + date.getDay();
        }else{
            Day = String.valueOf(date.getDay());
        }

         if (date.getMonth() <10){
             Month = "0" + (date.getMonth() + 1);
         }else{
             Month = String.valueOf((date.getMonth() + 1));
         }

        selectedDate = Month + "/" + Day + "/" + date.getYear();
        Fragment fragment = null;
        Class fragmentClass;
        fragmentClass = Workout_details_Fragment.class;
        Workout_details_Fragment yfc = new Workout_details_Fragment();
        Bundle bundle = new Bundle();
        bundle.putString("tag", selectedDate);
        yfc.setArguments(bundle);


        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.container, yfc);
        ft.addToBackStack(null);

        ft.commit();
        //Toast.makeText(getContext(), "Выбран" + date.getDay() + "/" + date.getMonth()+ "/" + date.getYear(), Toast.LENGTH_SHORT).show();
    }
}
