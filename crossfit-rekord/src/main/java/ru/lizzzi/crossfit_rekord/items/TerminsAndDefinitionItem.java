package ru.lizzzi.crossfit_rekord.items;

import android.content.Context;

import ru.lizzzi.crossfit_rekord.R;

public class TerminsAndDefinitionItem {
    private Context context;

    public TerminsAndDefinitionItem(Context context) {
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

    public String getDefinitionFields() {
        return context.getString(Fields.description.getId());
    }
}
