package ru.lizzzi.crossfit_rekord.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import ru.lizzzi.crossfit_rekord.R;
import ru.lizzzi.crossfit_rekord.fragments.DefinitionFragment;
import ru.lizzzi.crossfit_rekord.items.TerminsAndDefinitionItem;

public class RecyclerAdapterDefinition extends RecyclerView.Adapter<RecyclerAdapterDefinition.ViewHolder> {

    private List<Map<String, String>> termsItems;
    private TerminsAndDefinitionItem terminsAndDefinitionItem;

    public RecyclerAdapterDefinition(DefinitionFragment fragment) {
        termsItems = new ArrayList<>();
        terminsAndDefinitionItem = new TerminsAndDefinitionItem(fragment.getContext());
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textTermin;
        private TextView textDescription;
        ViewHolder(View view) {
            super(view);
            textTermin = view.findViewById(R.id.termin);
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
        final Map mapTermsOfSelectedCharacter = termsItems.get(position);
        String termin = Objects.requireNonNull(mapTermsOfSelectedCharacter.get(
                terminsAndDefinitionItem.getTerminFields())).toString();
        String definition = Objects.requireNonNull(mapTermsOfSelectedCharacter.get(
                terminsAndDefinitionItem.getDefinitionFields())).toString();
        holder.textTermin.setText(termin);
        holder.textDescription.setText(definition);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return termsItems.size();
    }

    public void add(List<Map<String, String>> termsItems) {
        this.termsItems = termsItems;
    }
}