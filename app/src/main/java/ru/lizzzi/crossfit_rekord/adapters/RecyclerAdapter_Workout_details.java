package ru.lizzzi.crossfit_rekord.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

import ru.lizzzi.crossfit_rekord.R;
import ru.lizzzi.crossfit_rekord.documentfields.DocumentFields_Workout_details;


/**
 * Created by Liza on 15.03.2018.
 */

public class RecyclerAdapter_Workout_details  extends BaseAdapter{

    private List<Map> woditems;
    private int layoutId;
    private LayoutInflater inflater;
    private DocumentFields_Workout_details fields;

    public RecyclerAdapter_Workout_details(Context context, @NonNull List<Map> woditems, int layoutId){
        this.woditems = woditems;
        this.layoutId = layoutId;
        inflater = LayoutInflater.from(context);
        fields = new DocumentFields_Workout_details(context);
    }

    @Override
    public int getCount() {
        return woditems.size();
    }

    @Override
    public Object getItem(int position) {
        return woditems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent){
        ViewHolder holder;

        if (view != null) {
            holder = (ViewHolder) view.getTag();
        } else {
            view = inflater.inflate(layoutId, parent, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        }

        customizeView(view, holder, woditems.get(position));

        return  view;
    }

    private void customizeView(View view, ViewHolder holder, final Map documentInfo) {
        String stName = (String) documentInfo.get(fields.getNameField());
        String stSkill = (String) documentInfo.get(fields.getSkillField());
        String stWod_level = (String) documentInfo.get(fields.getWodLevelField());
        String stWod_Result = (String) documentInfo.get(fields.getWodResultField());

        holder.Name.setText(stName);
        holder.Skill.setText(stSkill);
        holder.Wod_level.setText(stWod_level);
        holder.Wod_result.setText(stWod_Result);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

    }

    static class ViewHolder{
        private TextView Name;
        private TextView Skill;
        private TextView Wod_level;
        private TextView Wod_result;

        ViewHolder(View view){
            Name = view.findViewById(R.id.tvName);
            Skill = view.findViewById(R.id.tvSkill);
            Wod_level = view.findViewById(R.id.tvWod_level);
            Wod_result = view.findViewById(R.id.tvWod_result);
        }
    }
}
