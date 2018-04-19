package ru.lizzzi.crossfit_rekord.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

import ru.lizzzi.crossfit_rekord.R;
import ru.lizzzi.crossfit_rekord.documentfields.DocumentFields_Record;

public class RecyclerAdapter_Record extends RecyclerView.Adapter<RecyclerAdapter_Record.ViewHolder>  {
    private List<Map> storedItems;
    private DocumentFields_Record fields;
    private int i = 1;

    public RecyclerAdapter_Record(Context context, @NonNull List<Map> shediletems) {
        this.storedItems = shediletems;
        fields = new DocumentFields_Record(context);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView UsernameItem;
        private TextView UserCountItem;

        ViewHolder(View view) {
            super(view);
            UsernameItem = view.findViewById(R.id.username);
            UserCountItem = view.findViewById(R.id.number);

        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lv_record, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Map documentInfo = storedItems.get(position);
        String username = (String) documentInfo.get(fields.getUsernameFields());
        String number = String.valueOf(i);
        i++;

        holder.UsernameItem.setText(username);
        holder.UserCountItem.setText(number);

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return storedItems.size();
    }
}