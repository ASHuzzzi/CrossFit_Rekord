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

import ru.lizzzi.crossfit_rekord.R;
import ru.lizzzi.crossfit_rekord.fragments.OneRepeatHighsFragment;
import ru.lizzzi.crossfit_rekord.items.ExerciseItem;

import static android.text.InputType.TYPE_CLASS_DATETIME;

public class RecyclerAdapterMyResults extends RecyclerView.Adapter<RecyclerAdapterMyResults.ViewHolder> {

    private OneRepeatHighsFragment fragment;
    private List<ExerciseItem> exercises;

    public RecyclerAdapterMyResults(OneRepeatHighsFragment fragment) {
        this.fragment = fragment;
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
        final String exercise = exercises.get(position).getExercise();
        final String exerciseRu = exercises.get(position).getExerciseRu();
        final String result = exercises.get(position).getResult();
        final String unit = exercises.get(position).getUnit();

        holder.textCaptionEn.setText(exercise);
        holder.textCaptionRu.setText(exerciseRu);
        holder.editResult.setText(!"0".equals(result) ? result : "");
        holder.editResult.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                String newResult = charSequence.toString();
                if (newResult.startsWith("0")) {
                    holder.editResult.setText("");
                }

                if (newResult.length() > 1) {
                    ExerciseItem exerciseItem = new ExerciseItem(exercise, exerciseRu, newResult, unit);
                    fragment.setResult(exerciseItem);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        holder.textUnit.setText(unit);
        if (unit != null && unit.equals("время")) {
            holder.editResult.setInputType(TYPE_CLASS_DATETIME);
        }
    }

    @Override
    public int getItemCount() {
        return exercises.size();
    }

    public void setExercises(List<ExerciseItem> exercises) {
        this.exercises = exercises;
    }

    public boolean isEmpty() {
        return exercises.isEmpty();
    }
}
