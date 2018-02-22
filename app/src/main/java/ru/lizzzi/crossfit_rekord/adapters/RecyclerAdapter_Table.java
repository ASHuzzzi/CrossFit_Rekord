package ru.lizzzi.crossfit_rekord.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

import ru.lizzzi.crossfit_rekord.DocumentFields_Table;
import ru.lizzzi.crossfit_rekord.R;

public class RecyclerAdapter_Table extends BaseAdapter {
    private List<Map> shediletems;
    private int layoutId;
    private LayoutInflater inflater;
    private DocumentFields_Table fields;

    public RecyclerAdapter_Table(Context context, @NonNull List<Map> shediletems, int layoutId) {
        this.shediletems = shediletems;
        this.layoutId = layoutId;
        inflater = LayoutInflater.from(context);
        fields = new DocumentFields_Table(context);
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

    private void customizeView(View view, ViewHolder holder, final Map documentInfo) {

        String start_time = (String) documentInfo.get(fields.getStartTimeField());
        String type = (String) documentInfo.get(fields.getTypeField());

        holder.StartTimeItem.setText(start_time);
        holder.TypesItem.setText(type);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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