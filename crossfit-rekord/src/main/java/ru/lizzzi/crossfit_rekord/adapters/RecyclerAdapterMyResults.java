package ru.lizzzi.crossfit_rekord.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ru.lizzzi.crossfit_rekord.R;
import ru.lizzzi.crossfit_rekord.fragments.OneRepeatHighsFragment;
import ru.lizzzi.crossfit_rekord.items.ResultItem;

public class RecyclerAdapterMyResults
        extends RecyclerView.Adapter<RecyclerAdapterMyResults.ViewHolder> {

    private OneRepeatHighsFragment fragment;
    private List<Map<String, String>> exercises;
    private ResultItem item;

    public RecyclerAdapterMyResults(OneRepeatHighsFragment fragment) {
        this.fragment = fragment;
        item = new ResultItem();
        exercises = new ArrayList<>();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView textCaptionRu;
        private TextView textCaptionEn;
        private EditText editResult;
        private TextView textUnit;

        ViewHolder(View view) {
            super(view);
            textCaptionRu = view.findViewById(R.id.textCaptionRu);
            textCaptionEn = view.findViewById(R.id.textCaption);
            editResult = view.findViewById(R.id.editResult);
            textUnit = view.findViewById(R.id.textUnit);
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
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final String exercise = exercises.get(position).get(item.getExercise());
        final String exerciseRu = exercises.get(position).get(item.getExerciseRu());
        final String result = exercises.get(position).get(item.getResult());
        final String unit = exercises.get(position).get(item.getUnit());

        holder.textCaptionEn.setText(exercise);
        holder.textCaptionRu.setText(exerciseRu);
        holder.editResult.setText(!"0".equals(result) ? result : "");
        holder.editResult.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String newResult = holder.editResult.getText().toString();
                if (newResult.startsWith("0")) {
                    holder.editResult.setText("");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                String newResult = holder.editResult.getText().toString();

                if (newResult.startsWith("0")) {
                    newResult = newResult.substring(1);
                }

                if (newResult.length() > 1) {
                    fragment.setExercise(exercise, newResult);
                }
            }
        });
        holder.textUnit.setText(unit);
    }

    @Override
    public int getItemCount() {
        return exercises.size();
    }

    public void setExercises(List<Map<String, String>> exercises) {
        this.exercises = exercises;
    }

    public boolean isEmpty() {
        return exercises.isEmpty();
    }
}
