package ru.lizzzi.crossfit_rekord;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;



public class RecyclerAdapter_Definition extends BaseAdapter {

    private Context context;
    private ArrayList<String> mDataset;
    private ArrayList<String> mPriceset;
    private int layoutId;
    private LayoutInflater inflater;

    public RecyclerAdapter_Definition(Context context, ArrayList<String> dataset, ArrayList<String> priceset, int layoutId) {
        this.context = context;
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
        ViewHolder holder;

        if (view != null) {
            holder = (ViewHolder) view.getTag();
        } else {
            view = inflater.inflate(layoutId, parent, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        }

        customizeView(view, holder, position);

        return view;
    }

    private void customizeView(View view, ViewHolder holder, int position) {

        holder.StartTimeItem.setText(mDataset.get(position));
        holder.TypesItem.setText(mPriceset.get(position));


        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

    static class ViewHolder {
        private TextView StartTimeItem;
        private TextView TypesItem;
        //@BindView(R.id.label) TextView tvStoredItemName;;

        public ViewHolder(View view) {
            StartTimeItem = (TextView) view.findViewById(R.id.termin);
            TypesItem = (TextView) view.findViewById(R.id.description);

        }
    }
}


