package ru.lizzzi.crossfit_rekord.documentfields;

import android.content.Context;

import ru.lizzzi.crossfit_rekord.R;


public class DocumentFields_Record {
    private Context context;

    public DocumentFields_Record(Context context) {
        this.context = context;
    }

    public enum Fields {

        username(R.string.username);

        private int fieldNameId;

        Fields(int fieldNameId) {
            this.fieldNameId = fieldNameId;
        }

        public int getNameId() {
            return fieldNameId;
        }

    }
    public String getUsernameFields() {
        return context.getString(Fields.username.getNameId());
    }
}
