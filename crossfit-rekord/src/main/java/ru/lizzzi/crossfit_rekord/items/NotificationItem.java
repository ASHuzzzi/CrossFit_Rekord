package ru.lizzzi.crossfit_rekord.items;

import android.content.Context;
import android.support.annotation.NonNull;

import ru.lizzzi.crossfit_rekord.R;

public class NotificationItem {
    private Context context;

    public NotificationItem(Context context) {
        this.context = context;
    }

    public enum Fields {

        dateNote(R.string.dateNote),
        header(R.string.header),
        text(R.string.text),
        viewed(R.string.viewed);

        private  int fieldNameId;

        Fields(int fieldNameId) {
            this.fieldNameId = fieldNameId;
        }

        int getFieldNameId() {
            return fieldNameId;
        }
    }

    public String getDateField() {
        return  context.getString(Fields.dateNote.getFieldNameId());
    }

    public String getHeaderField() {
        return  context.getString(Fields.header.getFieldNameId());
    }

    public String getTextField() {
        return  context.getString(Fields.text.getFieldNameId());
    }

    public String getViewedField() {
        return  context.getString(Fields.viewed.getFieldNameId());
    }
}
