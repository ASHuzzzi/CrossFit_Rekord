package ru.lizzzi.crossfit_rekord.items;

import android.content.Context;
import android.support.annotation.NonNull;

import ru.lizzzi.crossfit_rekord.R;

public class ScheduleItem {
    private Context context;

    public ScheduleItem(Context context) {
        this.context = context;
    }

    public enum Fields {

        start_time(R.string.start_time),
        type(R.string.type);

        private int fieldNameId;

        Fields(int fieldNameId) {
            this.fieldNameId = fieldNameId;
        }

        int getNameId() {
            return fieldNameId;
        }

    }

    @NonNull
    public String getStartTime() {
        return context.getString(Fields.start_time.getNameId());
    }

    @NonNull
    public String getType() {
        return context.getString(Fields.type.getNameId());
    }
}
