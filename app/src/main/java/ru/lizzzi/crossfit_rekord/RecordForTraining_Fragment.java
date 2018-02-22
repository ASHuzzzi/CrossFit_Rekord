package ru.lizzzi.crossfit_rekord;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import ru.lizzzi.crossfit_rekord.adapters.RecyclerAdapterRecord;
import ru.profit_group.scorocode_sdk.Callbacks.CallbackDocumentSaved;
import ru.profit_group.scorocode_sdk.Callbacks.CallbackFindDocument;
import ru.profit_group.scorocode_sdk.Callbacks.CallbackGetDocumentById;
import ru.profit_group.scorocode_sdk.Callbacks.CallbackRemoveDocument;
import ru.profit_group.scorocode_sdk.Responses.data.ResponseRemove;
import ru.profit_group.scorocode_sdk.scorocode_objects.Document;
import ru.profit_group.scorocode_sdk.scorocode_objects.DocumentInfo;
import ru.profit_group.scorocode_sdk.scorocode_objects.Query;


public class RecordForTraining_Fragment extends Fragment {

    public static final String APP_PREFERENCES = "audata";
    public static final String APP_PREFERENCES_USERNAME = "Username";
    public static final String APP_PREFERENCES_USERCOUNT = "Usercount";
    SharedPreferences mSettings;

    public static final String COLLECTION_NAME = "recordings_for_training";

    Date date = new Date();
    Calendar calendar = Calendar.getInstance();
    String time_select;
    String date_select;
    String username;
    String userid;

    RecyclerAdapterRecord adapter;
    ListView lvRecord;

    DocumentInfo documentInfo;

    Button btRegister;

    public RecordForTraining_Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_record_for_training_, container, false);

        mSettings = getContext().getSharedPreferences(APP_PREFERENCES, getContext().MODE_PRIVATE);

        Button btToday = ((Button) v.findViewById(R.id.btToday));
        Button btTommorow = ((Button) v.findViewById(R.id.btTommorow));
        Button btAftertommorow = ((Button) v.findViewById(R.id.btAftertommorow));

        lvRecord = (ListView) v.findViewById(R.id.lvRecord);

        Button btt900 = ((Button) v.findViewById(R.id.t900));
        Button btt1000 = ((Button) v.findViewById(R.id.t1000));
        Button btt1100 = ((Button) v.findViewById(R.id.t1100));
        Button btt1200 = ((Button) v.findViewById(R.id.t1200));
        Button btt1300 = ((Button) v.findViewById(R.id.t1300));
        Button btt1400 = ((Button) v.findViewById(R.id.t1400));
        Button btt1500 = ((Button) v.findViewById(R.id.t1500));
        Button btt1600 = ((Button) v.findViewById(R.id.t1600));
        Button btt1700 = ((Button) v.findViewById(R.id.t1700));
        Button btt1800 = ((Button) v.findViewById(R.id.t1800));
        Button btt1900 = ((Button) v.findViewById(R.id.t1900));
        Button btt2000 = ((Button) v.findViewById(R.id.t2000));
        Button btt2100 = ((Button) v.findViewById(R.id.t2100));

        btRegister = ((Button) v.findViewById(R.id.btRecord));

        SimpleDateFormat sdf = new SimpleDateFormat("EEEE dd MMMM");
        final SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM");

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

        DownloadData downloadData = new DownloadData();
        downloadData.execute();

        btToday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                date_select = sdf2.format(date);
                time_select = getContext().getString(R.string.T900);
                DownloadData downloadData = new DownloadData();
                downloadData.execute();
            }
        });

        btTommorow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                date_select = sdf2.format(tomorrow);
                time_select = getContext().getString(R.string.T900);
                DownloadData downloadData = new DownloadData();
                downloadData.execute();
            }
        });

        btAftertommorow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                date_select = sdf2.format(aftertomorrow);
                time_select = getContext().getString(R.string.T900);
                DownloadData downloadData = new DownloadData();
                downloadData.execute();
            }
        });

        btt900.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                time_select = getContext().getString(R.string.T900);
                DownloadData downloadData = new DownloadData();
                downloadData.execute();
            }
        });

        btt1000.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                time_select = getContext().getString(R.string.T1000);
                DownloadData downloadData = new DownloadData();
                downloadData.execute();
            }
        });

        btt1100.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                time_select = getContext().getString(R.string.T1100);
                DownloadData downloadData = new DownloadData();
                downloadData.execute();
            }
        });

        btt1200.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                time_select = getContext().getString(R.string.T1200);
                DownloadData downloadData = new DownloadData();
                downloadData.execute();
            }
        });

        btt1300.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                time_select = getContext().getString(R.string.T1300);
                DownloadData downloadData = new DownloadData();
                downloadData.execute();
            }
        });

        btt1400.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                time_select = getContext().getString(R.string.T1400);
                DownloadData downloadData = new DownloadData();
                downloadData.execute();
            }
        });

        btt1500.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                time_select = getContext().getString(R.string.T1500);
                DownloadData downloadData = new DownloadData();
                downloadData.execute();
            }
        });

        btt1600.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                time_select = getContext().getString(R.string.T1600);
                DownloadData downloadData = new DownloadData();
                downloadData.execute();
            }
        });

        btt1700.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                time_select = getContext().getString(R.string.T1700);
                DownloadData downloadData = new DownloadData();
                downloadData.execute();
            }
        });

        btt1800.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                time_select = getContext().getString(R.string.T1800);
                DownloadData downloadData = new DownloadData();
                downloadData.execute();
            }
        });
        btt1900.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                time_select = getContext().getString(R.string.T1900);
                DownloadData downloadData = new DownloadData();
                downloadData.execute();
            }
        });

        btt2000.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                time_select = getContext().getString(R.string.T2000);
                DownloadData downloadData = new DownloadData();
                downloadData.execute();
            }
        });
        btt2100.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                time_select = getContext().getString(R.string.T2100);
                DownloadData downloadData = new DownloadData();
                downloadData.execute();
            }
        });

        btRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (userid.equals("noId")){
                    Document newDocument = new Document("recordings_for_training");
                    newDocument.setField("data", date_select);
                    newDocument.setField("time", time_select);
                    newDocument.setField("username", username);


                    newDocument.saveDocument(new CallbackDocumentSaved() {
                        @Override
                        public void onDocumentSaved() {
                            Toast.makeText(getContext(), username + " " + date_select + " " + time_select , Toast.LENGTH_SHORT).show();
                            DownloadData downloadData = new DownloadData();
                            downloadData.execute();
                        }

                        @Override
                        public void onDocumentSaveFailed(String errorCode, String errorMessage) {
                            Toast.makeText(getContext(), "Не сохранилось. Попробуйте еще раз." , Toast.LENGTH_SHORT).show();
                        }
                    });
                }else {
                    final Document newDocument = new Document(COLLECTION_NAME);
                    newDocument.getDocumentById(userid, new CallbackGetDocumentById() {
                        @Override
                        public void onDocumentFound(DocumentInfo documentInfo) {
                            newDocument.removeDocument(new CallbackRemoveDocument() {
                                @Override
                                public void onRemoveSucceed(ResponseRemove responseRemove) {
                                    DownloadData downloadData = new DownloadData();
                                    downloadData.execute();
                                }

                                @Override
                                public void onRemoveFailed(String errorCode, String errorMessage) {
                                    Toast.makeText(getContext(), "Не удалилось. Попробуйте еще раз." , Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onDocumentNotFound(String errorCode, String errorMessage) {
                            Toast.makeText(getContext(), "Запись не найдена" , Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });


        return v;
    }

    private class DownloadData extends AsyncTask<Void,Void, Void>{

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            Query query = new Query(COLLECTION_NAME);
            query.equalTo("data", date_select);
            query.equalTo("time", time_select);
            query.findDocuments(new CallbackFindDocument() {
                @Override
                public void onDocumentFound(List<DocumentInfo> documentInfos) {
                    if(documentInfos != null) {

                        lvRecord.setVisibility(View.VISIBLE);
                        final SharedPreferences.Editor editor = mSettings.edit();
                        editor.putString(APP_PREFERENCES_USERCOUNT, "1");
                        editor.apply();
                        adapter = new RecyclerAdapterRecord(getContext(), documentInfos, R.layout.item_lv_record);
                        lvRecord.setAdapter(adapter);

                        Query query2 = new Query(COLLECTION_NAME);
                        query2.equalTo("data", date_select);
                        query2.equalTo("time", time_select);
                        query2.equalTo("username", username);
                        query2.findDocuments(new CallbackFindDocument() {
                            @Override
                            public void onDocumentFound(List<DocumentInfo> documentInfos) {
                                if(documentInfos != null) {
                                    btRegister.setText(R.string.delete_entry);
                                    userid = String.valueOf(documentInfos.get(0).get("_id"));
                                }
                            }

                            @Override
                            public void onDocumentNotFound(String errorCode, String errorMessage) {
                                btRegister.setText(R.string.whrite_entry);
                                userid = "noId";

                            }
                        });
                        return;


                    }
                }

                @Override
                public void onDocumentNotFound(String errorCode, String errorMessage) {
                    Toast.makeText(getContext(), "Нет данных", Toast.LENGTH_SHORT).show();
                    btRegister.setText(R.string.whrite_entry);
                    userid = "noId";
                    lvRecord.setVisibility(View.INVISIBLE);
                }
            });

            return null;
        }

        @Override
        public void onPostExecute(Void result){
            super.onPostExecute(result);
        }


    }

}
