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

import ru.lizzzi.crossfit_rekord.documentfields.DocumentFields_Record;
import ru.lizzzi.crossfit_rekord.R;

public class RecyclerAdapter_Record extends BaseAdapter {
    private List<Map> storedItems;
    private int layoutId;
    private LayoutInflater inflater;
    private DocumentFields_Record fields;
    private int i = 1;

    public RecyclerAdapter_Record(Context context, @NonNull List<Map> shediletems, int layoutId) {
        this.storedItems = shediletems;
        this.layoutId = layoutId;
        inflater = LayoutInflater.from(context);
        fields = new DocumentFields_Record(context);
    }

    @Override
    public int getCount() {
        return storedItems.size();
    }

    @Override
    public Object getItem(int position) {
        return storedItems.get(position);
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

        customizeView(view, holder, storedItems.get(position));

        return view;
    }

    private void customizeView(View view, ViewHolder holder, final Map documentInfo) {

        String username = (String) documentInfo.get(fields.getUsernameFields());
        String number = String.valueOf(i);
        i++;

        holder.UsernameItem.setText(username);
        holder.UserCountItem.setText(number);


        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

    static class ViewHolder {
        private TextView UsernameItem;
        private TextView UserCountItem;

        ViewHolder(View view) {
            UsernameItem = view.findViewById(R.id.username);
            UserCountItem = view.findViewById(R.id.number);

        }
    }
}