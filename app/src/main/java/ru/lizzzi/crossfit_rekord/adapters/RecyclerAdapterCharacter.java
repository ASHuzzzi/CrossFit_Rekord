package ru.lizzzi.crossfit_rekord.adapters;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import ru.lizzzi.crossfit_rekord.R;
import ru.lizzzi.crossfit_rekord.interfaces.ListernerCharacter;

public class RecyclerAdapterCharacter extends RecyclerView.Adapter<RecyclerAdapterCharacter.ViewHolder> {

    private List lCharacter;
    private ListernerCharacter listernerCharacter;

    public RecyclerAdapterCharacter(List character, ListernerCharacter listernerCharacter){
        this.lCharacter = character;
        this.listernerCharacter = listernerCharacter;

    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvCharacter;
        private CardView cvCharacter;

        ViewHolder(View view) {
            super(view);
            tvCharacter = view.findViewById(R.id.tvCharacter);
            cvCharacter = view.findViewById(R.id.cvCharacter);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rv_character, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final String stCharacter = (String) lCharacter.get(position);

        holder.tvCharacter.setText(stCharacter);

        holder.cvCharacter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listernerCharacter.SelectCharacter(stCharacter);
            }
        });

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return lCharacter.size();
    }
}
