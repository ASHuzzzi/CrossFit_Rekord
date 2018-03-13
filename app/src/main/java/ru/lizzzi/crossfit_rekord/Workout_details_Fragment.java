package ru.lizzzi.crossfit_rekord;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.backendless.Backendless;
import com.backendless.persistence.DataQueryBuilder;

import java.util.List;
import java.util.Map;

/**
 * Created by Liza on 13.03.2018.
 */

public class Workout_details_Fragment extends Fragment {

    List<Map> results;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View v = inflater.inflate(R.layout.fragment_workout_details, container, false);

        final Bundle bundle = getArguments();
        final String ri = bundle.getString("tag");



        @SuppressLint("StaticFieldLeak")
        class DownloadData extends AsyncTask<Void,Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {

                String whereClause = "date_session = '" + ri + "'";
                DataQueryBuilder queryBuilder = DataQueryBuilder.create();
                queryBuilder.setWhereClause(whereClause);
                queryBuilder.setPageSize(100);
                results = Backendless.Data.of("Training_sessions").find(queryBuilder);

                return null;
            }

        }

        DownloadData downloadData = new DownloadData();
        downloadData.execute();

        return v;
    }
}
