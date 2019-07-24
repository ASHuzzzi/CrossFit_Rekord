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
import ru.lizzzi.crossfit_rekord.documentfields.TerminsAndDefinition;

public class RecyclerAdapterDefinition extends RecyclerView.Adapter<RecyclerAdapterDefinition.ViewHolder> {

    private List<Map<String, String>> termsOfSelectedCharacter;
    private TerminsAndDefinition terminsAndDefinition;

    public RecyclerAdapterDefinition(Context context,
                                     @NonNull List<Map<String, String>> termsOfSelectedCharacter) {
        this.termsOfSelectedCharacter = termsOfSelectedCharacter;
        terminsAndDefinition = new TerminsAndDefinition(context);
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
        final Map mapTermsOfSelectedCharacter = termsOfSelectedCharacter.get(position);
        String termin =
                mapTermsOfSelectedCharacter.get(terminsAndDefinition.getTerminFields()).toString();
        String definition =
                mapTermsOfSelectedCharacter.get(terminsAndDefinition.getDefinitionFields()).toString();
        holder.textTermin.setText(termin);
        holder.textDescription.setText(definition);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return termsOfSelectedCharacter.size();
    }
}