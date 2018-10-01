package ru.lizzzi.crossfit_rekord.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import ru.lizzzi.crossfit_rekord.R;


public class RecyclerAdapterDefinition extends RecyclerView.Adapter<RecyclerAdapterDefinition.ViewHolder> {

    private ArrayList<String> mTermin;
    private ArrayList<String> mDescription;

    public RecyclerAdapterDefinition(ArrayList<String> termin, ArrayList<String> description) {
        this.mTermin = termin;
        this.mDescription = description;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTermin;
        private TextView tvDescription;
        ViewHolder(View view) {
            super(view);
            tvTermin = view.findViewById(R.id.termin);
            tvDescription = view.findViewById(R.id.description);

        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rv_description, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position){
        holder.tvTermin.setText(mTermin.get(position));
        holder.tvDescription.setText(mDescription.get(position));
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mTermin.size();
    }


}


