package ru.lizzzi.crossfit_rekord.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ru.lizzzi.crossfit_rekord.R;
import ru.lizzzi.crossfit_rekord.fragments.MyResultsFragment;
import ru.lizzzi.crossfit_rekord.items.ResultItem;

public class RecyclerAdapterMyResults extends RecyclerView.Adapter<RecyclerAdapterMyResults.ViewHolder> {

    private MyResultsFragment fragment;
    private List<Map<String, String>> exercise;
    private ResultItem item;

    public RecyclerAdapterMyResults(MyResultsFragment fragment) {
        this.fragment = fragment;
        item = new ResultItem(fragment.getContext());
        exercise = new ArrayList<>();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView textCaptionRu;
        private TextView textCaptionEn;
        private EditText editResult;

        ViewHolder(View view) {
            super(view);
            textCaptionRu = view.findViewById(R.id.textCaptionRu);
            textCaptionEn = view.findViewById(R.id.textCaptionEn);
            editResult = view.findViewById(R.id.editResult);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_my_results,
                parent,
                false
        );
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String exercise1 = exercise.get(position).get(item.getExercise());
        String result = exercise.get(position).get(item.getResult());

        holder.textCaptionEn.setText(exercise1);
        holder.editResult.setText(result);
        holder.editResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return exercise.size();
    }

    public void setExercise(List<Map<String, String>> exercise) {
        this.exercise = exercise;
    }

    public boolean isEmpty() {
        return exercise.isEmpty();
    }
}
