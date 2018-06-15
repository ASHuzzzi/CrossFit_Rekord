package ru.lizzzi.crossfit_rekord.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import ru.lizzzi.crossfit_rekord.R;


public class RecyclerAdapterDefinition extends BaseAdapter {

    private ArrayList<String> mDataset;
    private ArrayList<String> mPriceset;
    private int layoutId;
    private LayoutInflater inflater;

    public RecyclerAdapterDefinition(Context context, ArrayList<String> dataset, ArrayList<String> priceset, int layoutId) {
        this.mDataset = dataset;
        this.mPriceset = priceset;
        this.layoutId = layoutId;
        inflater = LayoutInflater.from(context);
    }


    @Override
    public int getCount() {
        return mDataset.size();
    }

    @Override
    public Object getItem(int position) {
        return mDataset.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        viewHolder holder;

        if (view != null) {
            holder = (viewHolder) view.getTag();
        } else {
            view = inflater.inflate(layoutId, parent, false);
            holder = new viewHolder(view);
            view.setTag(holder);
        }

        customizeView(view, holder, position);

        return view;
    }

    private void customizeView(View view, viewHolder holder, int position) {

        holder.startTimeItem.setText(mDataset.get(position));
        holder.typesItem.setText(mPriceset.get(position));


        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

    static class viewHolder {
        private TextView startTimeItem;
        private TextView typesItem;

        viewHolder(View view) {
            startTimeItem = view.findViewById(R.id.termin);
            typesItem = view.findViewById(R.id.description);

        }
    }
}


