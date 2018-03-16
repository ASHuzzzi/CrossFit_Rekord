package ru.lizzzi.crossfit_rekord;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.backendless.Backendless;
import com.backendless.persistence.DataQueryBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ru.lizzzi.crossfit_rekord.adapters.RecyclerAdapter_Workout_details;

/**
 * Created by Liza on 13.03.2018.
 */

public class Workout_details_Fragment extends Fragment {

    List<Map> results;
    List<Map> results2;
    RecyclerAdapter_Workout_details adapter;
    ListView lvItemsInWod;
    TextView tvWarmUp;
    TextView tvSkill;
    TextView tvWOD;
    TextView tvLevelA;
    TextView tvLevelB;
    TextView tvLevelC;
    List<String> list;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View v = inflater.inflate(R.layout.fragment_workout_details, container, false);

        final Bundle bundle = getArguments();
        final String ri = bundle.getString("tag");
        lvItemsInWod = v.findViewById(R.id.lvWodResult);
        tvWarmUp = v.findViewById(R.id.tvWarmUp);
        tvSkill = v.findViewById(R.id.tvSkill);
        tvWOD = v.findViewById(R.id.tvWOD);
        tvLevelA = v.findViewById(R.id.tvLevelA);
        tvLevelB = v.findViewById(R.id.tvLevelB);
        tvLevelC = v.findViewById(R.id.tvLevelC);



        @SuppressLint("StaticFieldLeak")
        class DownloadData extends AsyncTask<Void,Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {

                String whereClause = "date_session = '" + ri + "'";
                DataQueryBuilder queryBuilder = DataQueryBuilder.create();
                queryBuilder.setWhereClause(whereClause);
                queryBuilder.setPageSize(100);
                results = Backendless.Data.of("Training_sessions").find(queryBuilder);
                if (results != null){
                    adapter = new RecyclerAdapter_Workout_details(getContext(), results, R.layout.item_lv_workout_details);
                }

                results2 = Backendless.Data.of("Exercise_assignment").find(queryBuilder);
                for (int i = 0; i< results2.size(); i++){
                    list = new ArrayList<String>(results2.get(i).values());
                }

                return null;
            }

            @Override
            protected void  onPostExecute(Void result){
                if(adapter !=null){
                    lvItemsInWod.setAdapter(adapter);
                }
                if (list.size() > 0){
                    tvWarmUp.setText(String.valueOf(list.get(0)));
                    tvSkill.setText(String.valueOf(list.get(5)));
                    tvWOD.setText(String.valueOf(list.get(8)));
                    tvLevelA.setText((String.valueOf(list.get(2))));
                    tvLevelB.setText((String.valueOf(list.get(9))));
                    tvLevelC.setText((String.valueOf(list.get(2))));
                }
            }

        }

        DownloadData downloadData = new DownloadData();
        downloadData.execute();

        return v;
    }
}
