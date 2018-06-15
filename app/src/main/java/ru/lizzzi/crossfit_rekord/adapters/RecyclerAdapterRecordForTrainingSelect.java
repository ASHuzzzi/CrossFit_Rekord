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
import ru.lizzzi.crossfit_rekord.documentfields.DocumentFields_Table;
import ru.lizzzi.crossfit_rekord.interfaces.Listener_RecordForTrainingSelect;

public class RecyclerAdapterRecordForTrainingSelect extends RecyclerView.Adapter<RecyclerAdapterRecordForTrainingSelect.ViewHolder> {

    private List<Map> shediletems;
    private final ThreadLocal<DocumentFields_Table> fields = new ThreadLocal<>();
    private Listener_RecordForTrainingSelect mlistener;

    public RecyclerAdapterRecordForTrainingSelect(Context context, @NonNull List<Map> shediletems, Listener_RecordForTrainingSelect listener) {
        this.shediletems = shediletems;
        fields.set(new DocumentFields_Table(context));
        mlistener = listener;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView startTimeItem;
        private TextView typesItem;
        private LinearLayout llItemTable;

        ViewHolder(View view) {
            super(view);
            startTimeItem = view.findViewById(R.id.start_time);
            typesItem = view.findViewById(R.id.type);
            llItemTable = view.findViewById(R.id.ll_item_table);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lv_table, parent, false);

        return new ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Map documentInfo = shediletems.get(position);
        final String start_time = (String) documentInfo.get(fields.get().getStartTimeField());
        final String type = (String) documentInfo.get(fields.get().getTypeField());

        holder.startTimeItem.setText(start_time);
        holder.typesItem.setText(type);

        if (type.equals("CrossFit")){
            holder.typesItem.setBackgroundResource(R.drawable.table_item_crossfit);
        }
        if (type.equals("On-Ramp")){
            holder.typesItem.setBackgroundResource(R.drawable.table_item_onramp);
        }
        if (type.equals("Open Gym")){
            holder.typesItem.setBackgroundResource(R.drawable.table_item_opengym);
        }
        if (type.equals("Stretching")){
            holder.typesItem.setBackgroundResource(R.drawable.table_item_stretching);
        }
        if (type.equals("CrossFit Kids")){
            holder.typesItem.setBackgroundResource(R.drawable.table_item_crossfitkids);
        }
        if (type.equals("Weightlifting/Athleticism")){
            holder.typesItem.setBackgroundResource(R.drawable.table_item_weightlifting_and_athleticism);
        }
        if (type.equals("Gymnastics/Defence")){
            holder.typesItem.setBackgroundResource(R.drawable.table_item_gymnastics_and_defence);

        }
        if (type.equals("Rowing/Lady class")){
            holder.typesItem.setBackgroundResource(R.drawable.table_item_rowing_and_ladyclass);
        }
        if (type.equals("Weightlifting")){
            holder.typesItem.setBackgroundResource(R.drawable.table_item_weighlifting);
        }

        LinearLayout ll_item_table = holder.llItemTable;
        ll_item_table.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mlistener.SelectTime((String) documentInfo.get(fields.get().getStartTimeField()),
                        (String) documentInfo.get(fields.get().getTypeField()));
            }
        });
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return shediletems.size();
    }
}
