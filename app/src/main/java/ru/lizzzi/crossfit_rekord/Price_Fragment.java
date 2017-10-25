package ru.lizzzi.crossfit_rekord;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import ru.lizzzi.crossfit_rekord.views.FastScrollRecyclerView;
import ru.profit_group.scorocode_sdk.Callbacks.CallbackCountDocument;
import ru.profit_group.scorocode_sdk.Callbacks.CallbackFindDocument;
import ru.profit_group.scorocode_sdk.Responses.data.ResponseCount;
import ru.profit_group.scorocode_sdk.ScorocodeSdk;
import ru.profit_group.scorocode_sdk.scorocode_objects.DocumentInfo;
import ru.profit_group.scorocode_sdk.scorocode_objects.Query;

/**
 * Created by Liza on 20.09.2017.
 */

public class Price_Fragment extends Fragment{

    public static final String COLLECTION_NAME = "subscription";
    public static final String APPLICATION_ID = "24accf90596a4630a107e14d03a6a3a7";
    public static final String MASTER_KEY = "aee8341a0a22449ebd6a707702689c4e";
    public static final String CLIENT_KEY = "f539a69f0d5940a38e0ca0e83a394d00";
    public static final String FILE_KEY = "c785108f61304a2680a53e1a44ae15b2";
    private static final String MESSAGE_KEY = "e812ec1547b84b62bc9a5c145d442f77";
    private static final String SCRIPT_KEY = "6920f997815244f2bc77949974e4b215";
    private static final String WEBSOCKET_KEY = "6920f997815244f2bc77949974e4b215";

    private FastScrollRecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mAdapter;
    private List<DocumentInfo> documentInfos;
    private DocumentFields fields;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_price, container, false);
        ScorocodeSdk.initWith(APPLICATION_ID, CLIENT_KEY, MASTER_KEY, FILE_KEY, MESSAGE_KEY, SCRIPT_KEY, WEBSOCKET_KEY);
        final ListView lvItemsInStorehouse = (ListView) v.findViewById(R.id.lvItemsInStorehouse);
        fields = new DocumentFields(getContext());

        ExpandableListView elvMain;

        Query query = new Query(COLLECTION_NAME);
        query.findDocuments(new CallbackFindDocument() {
            @Override
            public void onDocumentFound(List<DocumentInfo> documentInfos) {
                if(documentInfos != null) {
                    RecyclerAdapterMenu adapter = new RecyclerAdapterMenu(getContext(), documentInfos, R.layout.item_lv_subscription);
                    lvItemsInStorehouse.setAdapter(adapter);
                }
            }

            @Override
            public void onDocumentNotFound(String errorCode, String errorMessage) {
                Toast.makeText(getContext(), "Нет данных", Toast.LENGTH_SHORT).show();
            }
        });


        //CreateItemList();
        //mRecyclerView = (FastScrollRecyclerView) v.findViewById(R.id.recyclerview);
        //mLayoutManager = new LinearLayoutManager(getContext());
        //mRecyclerView.setLayoutManager(mLayoutManager);


        return v;
    }

    private void CreateItemList() {

        Query query1 = new Query(COLLECTION_NAME);
        query1.countDocuments(new CallbackCountDocument() {
            @Override
            public void onDocumentsCounted(ResponseCount responseCount) {
                Toast.makeText(getContext(), "Есть", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCountFailed(String errorCode, String errorMessage) {
                Toast.makeText(getContext(), "Нет", Toast.LENGTH_SHORT).show();
            }
        });



    }

    private void setAdapter(List<DocumentInfo> documentInfos) {
        //RecyclerAdapterMenu adapter = new RecyclerAdapterMenu(Price_Fragment.this, documentInfos, R.layout.item_menu);
        //lvItemsInStorehouse.setAdapter(adapter);
    }
}
