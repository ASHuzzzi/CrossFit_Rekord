package ru.lizzzi.crossfit_rekord.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ru.lizzzi.crossfit_rekord.R;
import ru.lizzzi.crossfit_rekord.fragments.CharacterFragment;

public class RecyclerAdapterCharacter
        extends RecyclerView.Adapter<RecyclerAdapterCharacter.ViewHolder> {

    private List listCharacter;
    private CharacterFragment fragment;

    public RecyclerAdapterCharacter(CharacterFragment fragment) {
        this.fragment = fragment;
        this.listCharacter = new ArrayList();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textCharacter;
        private CardView cardCharacter;

        ViewHolder(View view) {
            super(view);
            textCharacter = view.findViewById(R.id.tvCharacter);
            cardCharacter = view.findViewById(R.id.cvCharacter);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_rv_character,
                parent,
                false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final int itemPosition = position;

        holder.textCharacter.setText(listCharacter.get(itemPosition).toString());
        holder.cardCharacter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment.openDefinitionFragment(listCharacter.get(itemPosition).toString());
            }
        });
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return listCharacter.size();
    }

    public void add(List listCharacter) {
        this.listCharacter = listCharacter;
    }
}
