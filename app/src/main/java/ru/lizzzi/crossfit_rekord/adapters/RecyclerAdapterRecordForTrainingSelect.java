package ru.lizzzi.crossfit_rekord.adapters;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import ru.lizzzi.crossfit_rekord.R;
import ru.lizzzi.crossfit_rekord.fragments.RecordForTrainingFragment;
import ru.lizzzi.crossfit_rekord.items.ScheduleItem;
import ru.lizzzi.crossfit_rekord.inspectionСlasses.BackgroundDrawable;

public class RecyclerAdapterRecordForTrainingSelect
        extends RecyclerView.Adapter<RecyclerAdapterRecordForTrainingSelect.ViewHolder> {

    private List<Map> scheduleItems;
    private ScheduleItem item;
    private RecordForTrainingFragment fragment;

    public RecyclerAdapterRecordForTrainingSelect(RecordForTrainingFragment fragment) {
        this.fragment = fragment;
        item = new ScheduleItem(fragment.getContext());
        scheduleItems = new ArrayList<>();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView startTimeItem;
        private TextView typesItem;
        private LinearLayout layoutItem;

        ViewHolder(View view) {
            super(view);
            startTimeItem = view.findViewById(R.id.start_time);
            typesItem = view.findViewById(R.id.type);
            layoutItem = view.findViewById(R.id.ll_item_table);
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
        final String startTime =
                String.valueOf(scheduleItems.get(position).get(item.getStartTime()));
        final String workoutType = String.valueOf(scheduleItems.get(position).get(item.getType()));

        holder.startTimeItem.setText(startTime);
        holder.typesItem.setText(workoutType);

        try {
            Date today = Calendar.getInstance().getTime();
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            Date timeNow = dateFormat.parse(dateFormat.format(today));
            Date selectedTime = dateFormat.parse(startTime);
            //проверяем чтобы выбранное время было позже чем сейчас
            if (fragment.isToday() && (selectedTime.getTime() < timeNow.getTime())) {
                holder.typesItem.setBackgroundResource(R.drawable.table_item_out_of_time);
                holder.startTimeItem.setTextColor(Color.BLACK);
                holder.layoutItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast toast = Toast.makeText(
                                view.getContext(),
                                "Тренировка уже прошла. Выбери более позднее время!",
                                Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                });
            } else {
                BackgroundDrawable backgroundDrawable = new BackgroundDrawable();
                int background = backgroundDrawable.getBackgroundDrawable(workoutType);
                holder.typesItem.setBackgroundResource(background);
                holder.layoutItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        fragment.openBrowserForRecording(startTime, workoutType);
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

    public void setScheduleItems(List<Map> scheduleItems) {
        this.scheduleItems = scheduleItems;
    }

    public boolean isEmpty () {
        return scheduleItems.isEmpty();
    }
}
