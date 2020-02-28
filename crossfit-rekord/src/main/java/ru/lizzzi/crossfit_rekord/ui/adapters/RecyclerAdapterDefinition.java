package ru.lizzzi.crossfit_rekord.ui.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ru.lizzzi.crossfit_rekord.R;
import ru.lizzzi.crossfit_rekord.items.TermItem;

public class RecyclerAdapterDefinition extends RecyclerView.Adapter<RecyclerAdapterDefinition.ViewHolder> {

    private List<TermItem> terms;

    public RecyclerAdapterDefinition() {
        terms = new ArrayList<>();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textTerm;
        private TextView textDescription;
        ViewHolder(View view) {
            super(view);
            textTerm = view.findViewById(R.id.termin);
            textDescription = view.findViewById(R.id.description);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_rv_description,
                parent,
                false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String term = terms.get(position).getTerm();
        String definition = terms.get(position).getDefinition();
        holder.textTerm.setText(term);
        holder.textDescription.setText(definition);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return terms.size();
    }

    public void add(List<TermItem> terms) {
        this.terms = terms;
    }
}