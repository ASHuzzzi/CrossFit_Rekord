package ru.lizzzi.crossfit_rekord.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

import ru.lizzzi.crossfit_rekord.R;
import ru.lizzzi.crossfit_rekord.documentfields.DocumentFields_Table;
import ru.lizzzi.crossfit_rekord.interfaces.ListenerRecordForTrainingSelect;

public class RecyclerAdapterTable extends BaseAdapter {
    private List<Map> shediletems;
    private int layoutId;
    private LayoutInflater inflater;
    private final ThreadLocal<DocumentFields_Table> fields = new ThreadLocal<>();
    private ListenerRecordForTrainingSelect mlistener;

    public RecyclerAdapterTable(Context context, @NonNull List<Map> shediletems, int layoutId, ListenerRecordForTrainingSelect listener) {
        this.shediletems = shediletems;
        this.layoutId = layoutId;
        inflater = LayoutInflater.from(context);
        fields.set(new DocumentFields_Table(context));
        mlistener = listener;
    }

    @Override
    public int getCount() {
        return shediletems.size();
    }

    @Override
    public Object getItem(int position) {
        return shediletems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder;

        if (view != null) {
            holder = (ViewHolder) view.getTag();
        } else {
            view = inflater.inflate(layoutId, parent, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        }

        customizeView(view, holder, shediletems.get(position));

        return view;
    }

    @SuppressLint("ResourceAsColor")
    private void customizeView(View view, ViewHolder holder, final Map documentInfo) {

        String start_time = (String) documentInfo.get(fields.get().getStartTimeField());
        String type = (String) documentInfo.get(fields.get().getTypeField());

        holder.StartTimeItem.setText(start_time);
        holder.TypesItem.setText(type);

        LinearLayout ll_item_table = view.findViewById(R.id.ll_item_table);


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


        ll_item_table.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mlistener.selectTime((String) documentInfo.get(fields.get().getStartTimeField()),
                        (String) documentInfo.get(fields.get().getTypeField()));
            }
        });
    }

    static class ViewHolder {
        private TextView StartTimeItem;
        private TextView TypesItem;

        ViewHolder(View view) {
            StartTimeItem = view.findViewById(R.id.start_time);
            TypesItem = view.findViewById(R.id.type);

        }
    }
}