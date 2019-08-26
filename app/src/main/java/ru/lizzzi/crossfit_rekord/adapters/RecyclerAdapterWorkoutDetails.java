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

import ru.lizzzi.crossfit_rekord.R;
import ru.lizzzi.crossfit_rekord.fragments.WodResultFragment;
import ru.lizzzi.crossfit_rekord.items.WorkoutDetailsItem;


public class RecyclerAdapterWorkoutDetails
        extends RecyclerView.Adapter<RecyclerAdapterWorkoutDetails.ViewHolder> {

    private List<Map> wodItems;
    private WorkoutDetailsItem wordkoutItem;

    public RecyclerAdapterWorkoutDetails(WodResultFragment fragment) {
        wordkoutItem = new WorkoutDetailsItem(fragment.getContext());
        wodItems = new ArrayList<>();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView userName;
        private TextView userSurname;
        private TextView skillResult;
        private TextView wodLevel;
        private TextView wodResult;

        ViewHolder(View view) {
            super(view);
            userName = view.findViewById(R.id.tvName);
            userSurname = view.findViewById(R.id.tvSurname);
            skillResult = view.findViewById(R.id.tvSkill);
            wodLevel = view.findViewById(R.id.tvWod_level);
            wodResult = view.findViewById(R.id.tvWod_result);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_rv_workout_details,
                parent,
                false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position){
        String userName = String.valueOf(wodItems.get(position).get(wordkoutItem.getNameField()));
        String userSurname =
                String.valueOf(wodItems.get(position).get(wordkoutItem.getSurnameField()));
        String skillResult =
                String.valueOf(wodItems.get(position).get(wordkoutItem.getSkillField()));
        String wodLevel =
                String.valueOf(wodItems.get(position).get(wordkoutItem.getWodLevelField()));
        String wodResult =
                String.valueOf(wodItems.get(position).get(wordkoutItem.getWodResultField()));

        holder.userName.setText(userName);
        holder.userSurname.setText(userSurname);
        holder.skillResult.setText(skillResult);
        holder.wodLevel.setText(wodLevel);
        holder.wodResult.setText(wodResult);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return wodItems.size();
    }

    public void setWodItems(List<Map> wodItems) {
        this.wodItems = wodItems;
    }

    public boolean isEmpty() {
        return wodItems.isEmpty();
    }
}
