package ru.lizzzi.crossfit_rekord.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import ru.lizzzi.crossfit_rekord.R;
import ru.lizzzi.crossfit_rekord.documentfields.DocumentFieldsSchedule;
import ru.lizzzi.crossfit_rekord.inspectionСlasses.BackgroundDrawable;
import ru.lizzzi.crossfit_rekord.interfaces.ListenerRecordForTrainingSelect;

public class RecyclerAdapterRecordForTrainingSelect
        extends RecyclerView.Adapter<RecyclerAdapterRecordForTrainingSelect.ViewHolder> {

    private List<Map> scheduleItems;
    private boolean isToday;
    private ListenerRecordForTrainingSelect listener;
    private final ThreadLocal<DocumentFieldsSchedule> scheduleFields = new ThreadLocal<>();

    public RecyclerAdapterRecordForTrainingSelect(
            Context context,
            @NonNull List<Map> scheduleItems,
            boolean isToday,
            ListenerRecordForTrainingSelect listener) {
        this.scheduleItems = scheduleItems;
        this.isToday = isToday;
        this.listener = listener;
        scheduleFields.set(new DocumentFieldsSchedule(context));
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView startTime;
        private TextView workoutTypes;
        private LinearLayout tableLayout;

        ViewHolder(View view) {
            super(view);
            startTime = view.findViewById(R.id.start_time);
            workoutTypes = view.findViewById(R.id.type);
            tableLayout = view.findViewById(R.id.ll_item_table);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_rv_table,
                parent,
                false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Map scheduleItem = scheduleItems.get(position);
        final String startTime = scheduleItem.get(scheduleFields.get().getStartTime()).toString();
        final String workoutType = scheduleItem.get(scheduleFields.get().getType()).toString();

        holder.startTime.setText(startTime);
        holder.workoutTypes.setText(workoutType);

        try {
            Date today = new GregorianCalendar().getTime();
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            Date timeNow = dateFormat.parse(dateFormat.format(today));
            Date selectedTime = dateFormat.parse(startTime);
            if (isToday && (selectedTime.getTime() < timeNow.getTime())) { //проверяем чтобы выбранное время было позже чем сейчас
                holder.workoutTypes.setBackgroundResource(R.drawable.table_item_out_of_time);
                holder.startTime.setTextColor(Color.BLACK);
                holder.tableLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listener.selectTime("outTime",
                                "outTime");
                    }
                });
            } else {
                BackgroundDrawable backgroundDrawable = new BackgroundDrawable();
                int background = backgroundDrawable.getBackgroundDrawable(workoutType);
                holder.workoutTypes.setBackgroundResource(background);
                holder.tableLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listener.selectTime(startTime, workoutType);
                    }
                });
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return scheduleItems.size();
    }
}
