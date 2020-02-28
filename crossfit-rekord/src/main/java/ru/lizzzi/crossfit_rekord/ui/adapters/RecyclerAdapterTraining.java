package ru.lizzzi.crossfit_rekord.ui.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import ru.lizzzi.crossfit_rekord.R;
import ru.lizzzi.crossfit_rekord.data.WodStorage;
import ru.lizzzi.crossfit_rekord.ui.fragments.TrainingListFragment;

public class RecyclerAdapterTraining extends RecyclerView.Adapter<RecyclerAdapterTraining.ViewHolder> {

    private List<Map> trainingList;
    private TrainingListFragment fragment;

    public RecyclerAdapterTraining(TrainingListFragment fragment) {
        this.fragment = fragment;
        this.trainingList = new ArrayList<>();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private CardView cardTraining;
        private TextView textDate;
        private TextView textWodLevel;
        private TextView textWod;

        ViewHolder(View view) {
            super(view);
            cardTraining = view.findViewById(R.id.cardTraining);
            textDate = view.findViewById(R.id.textDate);
            textWodLevel = view.findViewById(R.id.textWodLevel);
            textWod = view.findViewById(R.id.textWod);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_rv_training,
                parent,
                false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final int itemPosition = position;
        String dateSession = convertDate(Objects.requireNonNull(
                trainingList.get(itemPosition).get(WodStorage.DATE_SESSION)).toString());
        holder.textDate.setText(dateSession);
        holder.textWodLevel.setText(Objects.requireNonNull(
                trainingList.get(itemPosition).get(WodStorage.WOD_LEVEL)).toString());
        holder.textWod.setText(Objects.requireNonNull(
                trainingList.get(itemPosition).get(WodStorage.WOD)).toString());
        holder.cardTraining.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment.openFragment(Objects.requireNonNull(
                        trainingList.get(itemPosition).get(WodStorage.DATE_SESSION)).toString());
            }
        });
    }

    private String convertDate(String dateSession) {
        Date date = new Date();
        date.setTime(Long.parseLong(dateSession));
        SimpleDateFormat dateFormat = new SimpleDateFormat("d MMMM", Locale.getDefault());
        return dateFormat.format(date);
    }

    @Override
    public int getItemCount() {
        return trainingList.size();
    }

    public void add(List<Map> trainingList) {
        this.trainingList = trainingList;
    }
}
