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

public class RecyclerAdapter_RecordForTrainingSelect extends RecyclerView.Adapter<RecyclerAdapter_RecordForTrainingSelect.ViewHolder> {

    private List<Map> shediletems;
    private final ThreadLocal<DocumentFields_Table> fields = new ThreadLocal<>();
    private Listener_RecordForTrainingSelect mlistener;

    public RecyclerAdapter_RecordForTrainingSelect(Context context, @NonNull List<Map> shediletems, Listener_RecordForTrainingSelect listener) {
        this.shediletems = shediletems;
        fields.set(new DocumentFields_Table(context));
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

        holder.StartTimeItem.setText(start_time);
        holder.TypesItem.setText(type);

        if (type.equals("CrossFit")){
            holder.TypesItem.setBackgroundResource(R.drawable.table_item_crossfit);
        }
        if (type.equals("On-Ramp")){
            holder.TypesItem.setBackgroundResource(R.drawable.table_item_onramp);
        }
        if (type.equals("Open Gym")){
            holder.TypesItem.setBackgroundResource(R.drawable.table_item_opengym);
        }
        if (type.equals("Stretching")){
            holder.TypesItem.setBackgroundResource(R.drawable.table_item_stretching);
        }
        if (type.equals("CrossFit Kids")){
            holder.TypesItem.setBackgroundResource(R.drawable.table_item_crossfitkids);
        }
        if (type.equals("Weightlifting/Athleticism")){
            holder.TypesItem.setBackgroundResource(R.drawable.table_item_weightlifting_and_athleticism);
        }
        if (type.equals("Gymnastics/Defence")){
            holder.TypesItem.setBackgroundResource(R.drawable.table_item_gymnastics_and_defence);

        }
        if (type.equals("Rowing/Lady class")){
            holder.TypesItem.setBackgroundResource(R.drawable.table_item_rowing_and_ladyclass);
        }
        if (type.equals("Weightlifting")){
            holder.TypesItem.setBackgroundResource(R.drawable.table_item_weighlifting);
        }

        LinearLayout ll_item_table = holder.ll_item_table;
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
