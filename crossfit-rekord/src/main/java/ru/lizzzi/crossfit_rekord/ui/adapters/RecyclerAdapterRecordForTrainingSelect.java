package ru.lizzzi.crossfit_rekord.ui.adapters;

import android.content.res.Resources;
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

import ru.lizzzi.crossfit_rekord.R;
import ru.lizzzi.crossfit_rekord.ui.fragments.RecordForTrainingFragment;
import ru.lizzzi.crossfit_rekord.items.ScheduleItem;
import ru.lizzzi.crossfit_rekord.utils.BackgroundDrawable;

public class RecyclerAdapterRecordForTrainingSelect
        extends RecyclerView.Adapter<RecyclerAdapterRecordForTrainingSelect.ViewHolder> {

    private List<ScheduleItem> scheduleItems;
    private RecordForTrainingFragment fragment;

    public RecyclerAdapterRecordForTrainingSelect(RecordForTrainingFragment fragment) {
        this.fragment = fragment;
        scheduleItems = new ArrayList<>();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView startTimeItem;
        private TextView typesItem;
        private LinearLayout layoutItem;

        ViewHolder(View view) {
            super(view);
            startTimeItem = view.findViewById(R.id.textStartTime);
            typesItem = view.findViewById(R.id.textType);
            layoutItem = view.findViewById(R.id.layoutItemTable);
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
        final String startTime = scheduleItems.get(position).getStartTime();
        final String workoutType = scheduleItems.get(position).getType();

        holder.startTimeItem.setText(startTime);
        holder.typesItem.setText(workoutType);

        try {
            Date today = Calendar.getInstance().getTime();
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            Date timeNow = dateFormat.parse(dateFormat.format(today));
            Date selectedTime = dateFormat.parse(startTime);
            Resources resources = fragment.getResources();
            int resourcesColor, background;
            //проверяем чтобы выбранное время было позже чем сейчас
            if (fragment.isToday() && (selectedTime.getTime() < timeNow.getTime())) {
                background = R.drawable.table_item_out_of_time;
                resourcesColor = resources.getColor(R.color.colorRedPrimary);
                holder.layoutItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showToast(view);
                    }
                });
            } else {
                BackgroundDrawable backgroundDrawable = new BackgroundDrawable();
                background = backgroundDrawable.getBackgroundDrawable(workoutType);
                resourcesColor = resources.getColor(R.color.colorPrimaryDark);
                holder.layoutItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        fragment.openBrowserForRecording(startTime, workoutType);
                    }
                });
            }
            holder.typesItem.setTextColor(resourcesColor);
            holder.startTimeItem.setTextColor(resourcesColor);
            holder.layoutItem.setBackgroundResource(background);
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

    public void setScheduleItems(List<ScheduleItem> scheduleItems) {
        this.scheduleItems = scheduleItems;
    }

    public boolean isEmpty () {
        return scheduleItems.isEmpty();
    }

    private void showToast(View rootView) {
        Toast toast = Toast.makeText(
                rootView.getContext(),
                "Тренировка уже прошла. Выбери более позднее время!",
                Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
}
