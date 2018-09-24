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
import ru.lizzzi.crossfit_rekord.documentfields.DocumentFieldsWorkoutDetails;


public class RecyclerAdapterWorkoutDetails extends RecyclerView.Adapter<RecyclerAdapterWorkoutDetails.ViewHolder>{

    private List<Map> mapWodItems;
    private DocumentFieldsWorkoutDetails fields;

    public RecyclerAdapterWorkoutDetails(Context context, @NonNull List<Map> wodItems){
        this.mapWodItems = wodItems;
        fields = new DocumentFieldsWorkoutDetails(context);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView Name;
        private TextView Skill;
        private TextView Wod_level;
        private TextView Wod_result;

        ViewHolder(View view){
            super(view);
            Name = view.findViewById(R.id.tvName);
            Skill = view.findViewById(R.id.tvSkill);
            Wod_level = view.findViewById(R.id.tvWod_level);
            Wod_result = view.findViewById(R.id.tvWod_result);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rv_workout_details, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position){
        final Map documentInfo = mapWodItems.get(position);
        String stName = (String) documentInfo.get(fields.getNameField());
        String stSkill = (String) documentInfo.get(fields.getSkillField());
        String stWodLevel = (String) documentInfo.get(fields.getWodLevelField());
        String stWodResult = (String) documentInfo.get(fields.getWodResultField());

        holder.Name.setText(stName);
        holder.Skill.setText(stSkill);
        holder.Wod_level.setText(stWodLevel);
        holder.Wod_result.setText(stWodResult);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mapWodItems.size();
    }
}
