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
import ru.lizzzi.crossfit_rekord.documentfields.DocumentFieldsRecord;

public class RecyclerAdapterRecord extends RecyclerView.Adapter<RecyclerAdapterRecord.ViewHolder>  {
    private List<Map> storedItems;
    private DocumentFieldsRecord fields;
    private int i = 1;

    public RecyclerAdapterRecord(Context context, @NonNull List<Map> shediletems) {
        this.storedItems = shediletems;
        fields = new DocumentFieldsRecord(context);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView usernameItem;
        private TextView userCountItem;

        ViewHolder(View view) {
            super(view);
            usernameItem = view.findViewById(R.id.username);
            userCountItem = view.findViewById(R.id.number);

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
        String userName = (String) documentInfo.get(fields.getUsernameFields());
        String number = String.valueOf(i);
        i++;

        holder.usernameItem.setText(userName);
        holder.userCountItem.setText(number);

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