package ru.lizzzi.crossfit_rekord;

import android.content.Context;

public class DocumentFields_Table {
    private Context context;

    public DocumentFields_Table(Context context) {
        this.context = context;
    }

    public enum Fields {
        start_time(R.string.start_time),
        type(R.string.type);

        private int fieldNameId;

        Fields(int fieldNameId) {
            this.fieldNameId = fieldNameId;
        }

        public int getNameId() {
            return fieldNameId;
        }

    }

    public String getStartTimeField() {
        return context.getString(Fields.start_time.getNameId());
    }

    public String getTypeField() {
        return context.getString(Fields.type.getNameId());
    }
}
