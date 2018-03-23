package ru.lizzzi.crossfit_rekord.documentfields;

import android.content.Context;

import ru.lizzzi.crossfit_rekord.R;

public class DocumentFields_Workout_details {
    private Context context;

    public DocumentFields_Workout_details(Context context){
        this.context = context;
    }

    public enum Fields{
        Name(R.string.Name),
        skill(R.string.skill),
        wod_level(R.string.wod_level),
        wod_result(R.string.wod_result);

        private  int fieldNameId;

        Fields(int fieldNameId){ this.fieldNameId = fieldNameId; }
        public  int getFieldNameId() {return fieldNameId; }
    }

    public String getNameField() {return  context.getString(Fields.Name.getFieldNameId()); }
    public String getSkillField() {return  context.getString(Fields.skill.getFieldNameId()); }
    public String getWodLevelField() {return  context.getString(Fields.wod_level.getFieldNameId()); }
    public String getWodResultField() {return  context.getString(Fields.wod_result.getFieldNameId()); }
}
