package ru.lizzzi.crossfit_rekord;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

import ru.profit_group.scorocode_sdk.Callbacks.CallbackFindDocument;
import ru.profit_group.scorocode_sdk.scorocode_objects.DocumentInfo;
import ru.profit_group.scorocode_sdk.scorocode_objects.Query;

/**
 * Created by Liza on 11.10.2017.
 */

public class Table_Fragment extends Fragment{

    public static final String COLLECTION_NAME = "table";


    private DocumentFields_Table fields;
    private ProgressBar mProgressBar;
    ListView lvItemsInStorehouse;
    View v;
    List<String> fieldNames = Arrays.asList("start_time","Type");
    RecyclerAdapter_Table adapter;
    List<DocumentInfo> documentInfos;
    int position;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_table, container, false);

        Button button_monday= (Button) v.findViewById(R.id.day_1);
        Button button_tuesday= (Button) v.findViewById(R.id.day_2);
        Button button_wednesday= (Button) v.findViewById(R.id.day_3);
        Button button_thursday= (Button) v.findViewById(R.id.day_4);
        Button button_friday= (Button) v.findViewById(R.id.day_5);
        Button button_saturday= (Button) v.findViewById(R.id.day_6);
        Button button_sunday= (Button) v.findViewById(R.id.day_7);
        fields = new DocumentFields_Table(getContext());
        mProgressBar = (ProgressBar) v.findViewById(R.id.progressBar);
        lvItemsInStorehouse = (ListView) v.findViewById(R.id.lvTable);



        final DownloadTable downloadTable = new DownloadTable();
        downloadTable.execute("1");

        button_monday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DownloadTable downloadTable = new DownloadTable();
                downloadTable.execute("1");
            }
        });

        button_tuesday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DownloadTable downloadTable = new DownloadTable();
                downloadTable.execute("2");
            }
        });

        button_wednesday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DownloadTable downloadTable = new DownloadTable();
                downloadTable.execute("3");
            }
        });

        button_thursday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DownloadTable downloadTable = new DownloadTable();
                downloadTable.execute("4");
            }
        });

        button_friday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DownloadTable downloadTable = new DownloadTable();
                downloadTable.execute("5");
            }
        });

        button_saturday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DownloadTable downloadTable = new DownloadTable();
                downloadTable.execute("6");
            }
        });

        button_sunday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DownloadTable downloadTable = new DownloadTable();
                downloadTable.execute("7");
            }
        });



        return v;
    }

    private class DownloadTable extends AsyncTask<String,Void, Void>{

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            lvItemsInStorehouse.setVisibility(View.INVISIBLE);
            mProgressBar.setVisibility(View.VISIBLE);

        }

        @Override
        protected Void doInBackground(String... params) {

            String dayofweek = params[0];

            //fieldNames.add("start_time");
            //fieldNames.add("Type");

            Query query = new Query(COLLECTION_NAME);
            query.setFieldsForSearch(fieldNames);
            query.ascending("start_time");
            query.equalTo("day_of_week", dayofweek);
            query.findDocuments(new CallbackFindDocument() {
                @Override
                public void onDocumentFound(List<DocumentInfo> documentInfos) {
                    if(documentInfos != null) {
                        adapter = new RecyclerAdapter_Table(getContext(), documentInfos, R.layout.item_lv_table);
                        lvItemsInStorehouse.setAdapter(adapter);

                    }
                }

                @Override
                public void onDocumentNotFound(String errorCode, String errorMessage) {
                    Toast.makeText(getContext(), "Нет данных", Toast.LENGTH_SHORT).show();
                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            super.onPostExecute(result);
            lvItemsInStorehouse.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.INVISIBLE);
        }
    }
}
