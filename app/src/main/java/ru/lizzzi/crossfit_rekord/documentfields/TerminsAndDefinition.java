package ru.lizzzi.crossfit_rekord.documentfields;

import android.content.Context;

import ru.lizzzi.crossfit_rekord.R;

public class TerminsAndDefinition {
    private Context context;

    public TerminsAndDefinition(Context context) {
        this.context = context;
    }

    public enum Fields {

        termin(R.string.termin),
        description(R.string.description);

        private int fieldId;

        Fields(int fieldId) {
            this.fieldId = fieldId;
        }

        int getId() {
            return fieldId;
        }

    }
    public String getTerminFields() {
        return context.getString(Fields.termin.getId());
    }

    public String getDescriptionFields() {
        return context.getString(Fields.description.getId());
    }
}
