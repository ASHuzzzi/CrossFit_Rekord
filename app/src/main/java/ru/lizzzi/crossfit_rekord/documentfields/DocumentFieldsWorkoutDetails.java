package ru.lizzzi.crossfit_rekord.documentfields;

import android.content.Context;

import ru.lizzzi.crossfit_rekord.R;

public class DocumentFieldsWorkoutDetails {
    private Context context;

    public DocumentFieldsWorkoutDetails(Context context){
        this.context = context;
    }

    public enum Fields{
        name(R.string.Name),
        surname(R.string.Surname),
        skill(R.string.skill),
        wodLevel(R.string.wod_level),
        wodResult(R.string.wod_result);

        private  int fieldNameId;

        Fields(int fieldNameId){ this.fieldNameId = fieldNameId; }
        int getFieldNameId() {return fieldNameId; }
    }

    public String getNameField() {return  context.getString(Fields.name.getFieldNameId()); }
    public String getSurnameField(){return context.getString(Fields.surname.getFieldNameId()); }
    public String getSkillField() {return  context.getString(Fields.skill.getFieldNameId()); }
    public String getWodLevelField() {return  context.getString(Fields.wodLevel.getFieldNameId()); }
    public String getWodResultField() {return  context.getString(Fields.wodResult.getFieldNameId()); }
}
