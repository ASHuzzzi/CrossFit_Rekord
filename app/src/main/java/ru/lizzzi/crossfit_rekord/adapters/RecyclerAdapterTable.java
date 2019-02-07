package ru.lizzzi.crossfit_rekord.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

import ru.lizzzi.crossfit_rekord.R;
import ru.lizzzi.crossfit_rekord.documentfields.DocumentFieldsTable;
import ru.lizzzi.crossfit_rekord.inspectionСlasses.TakeBackgroungResourceForAdapter;
import ru.lizzzi.crossfit_rekord.interfaces.ListenerRecordForTrainingSelect;

public class RecyclerAdapterTable extends RecyclerView.Adapter<RecyclerAdapterTable.ViewHolder> {
    private List<Map> shediletems;
    private DocumentFieldsTable fields;
    private ListenerRecordForTrainingSelect mlistener;

    public RecyclerAdapterTable(Context context, @NonNull List<Map> shediletems, ListenerRecordForTrainingSelect listener) {
        this.shediletems = shediletems;
        fields = new DocumentFieldsTable(context);
        mlistener = listener;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView StartTimeItem;
        private TextView TypesItem;
        private LinearLayout ll_item_table;

        ViewHolder(View view) {
            super(view);
            StartTimeItem = view.findViewById(R.id.start_time);
            TypesItem = view.findViewById(R.id.type);
            ll_item_table = view.findViewById(R.id.ll_item_table);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rv_table, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position){
        final Map documentInfo = shediletems.get(position);
        String start_time = (String) documentInfo.get(fields.getStartTimeField());
        String type = (String) documentInfo.get(fields.getTypeField());

        holder.StartTimeItem.setText(start_time);
        holder.TypesItem.setText(type);

        TakeBackgroungResourceForAdapter takeBackgroungResourceForAdapter = new TakeBackgroungResourceForAdapter();
        int iResource = takeBackgroungResourceForAdapter.takeBackgroungResourceForAdapter(type);
        holder.TypesItem.setBackgroundResource(iResource);

        holder.ll_item_table.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mlistener.selectTime((String) documentInfo.get(fields.getStartTimeField()),
                        (String) documentInfo.get(fields.getTypeField()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return shediletems.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}