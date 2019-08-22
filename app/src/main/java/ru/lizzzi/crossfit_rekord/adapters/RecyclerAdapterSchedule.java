package ru.lizzzi.crossfit_rekord.adapters;

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
import java.util.List;
import java.util.Locale;
import java.util.Map;

import ru.lizzzi.crossfit_rekord.R;
import ru.lizzzi.crossfit_rekord.fragments.GymScheduleFragment;
import ru.lizzzi.crossfit_rekord.items.ScheduleItem;
import ru.lizzzi.crossfit_rekord.inspectionСlasses.BackgroundDrawable;

public class RecyclerAdapterSchedule extends RecyclerView.Adapter<RecyclerAdapterSchedule.ViewHolder> {
    private List<Map> scheduleItems;
    private ScheduleItem item;
    private GymScheduleFragment fragment;

    public RecyclerAdapterSchedule(GymScheduleFragment fragment) {
        scheduleItems = new ArrayList<>();
        item = new ScheduleItem(fragment.getContext());
        this.fragment = fragment;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView StartTimeItem;
        private TextView TypesItem;
        private LinearLayout ll_item_table;

        ViewHolder(View view) {
            super(view);
            StartTimeItem = view.findViewById(R.id.start_time);
            TypesItem = view.findViewById(R.id.type);
            ll_item_table = view.findViewById(R.id.ll_item_table);
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
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position){
        final String startTime =
                String.valueOf(scheduleItems.get(position).get(item.getStartTime()));
        final String type = String.valueOf(scheduleItems.get(position).get(item.getType()));

        holder.StartTimeItem.setText(startTime);
        holder.TypesItem.setText(type);

        BackgroundDrawable backgroundDrawable = new BackgroundDrawable();
        int drawable = backgroundDrawable.getBackgroundDrawable(type);
        holder.TypesItem.setBackgroundResource(drawable);

        holder.ll_item_table.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int selectedDay = fragment.getSelectedDay();
                Calendar calendar = Calendar.getInstance();
                List<Integer> daysWhenRecordingIsPossible = new ArrayList<>();
                for (int i = 0; i < 3; i++) {
                    calendar.add(Calendar.DAY_OF_WEEK, i);
                    daysWhenRecordingIsPossible.add(calendar.get(Calendar.DAY_OF_WEEK));
                    calendar.clear();
                    calendar = Calendar.getInstance();
                }
                if (daysWhenRecordingIsPossible.contains(selectedDay)) {
                    try {
                        SimpleDateFormat dateFormat =
                                new SimpleDateFormat("HH:mm", Locale.getDefault());
                        calendar = Calendar.getInstance();
                        int hourNow = calendar.get(Calendar.HOUR_OF_DAY);
                        calendar.setTime(dateFormat.parse(startTime));
                        int selectHour = calendar.get(Calendar.HOUR_OF_DAY);
                        boolean selectedToday =
                                daysWhenRecordingIsPossible.get(0).equals(selectedDay);
                        //проверяем чтобы выбранное время было позже чем сейчас
                        if (selectedToday && (selectHour <= hourNow)) {
                            showToast("Выберите более позднее время.", view);
                        } else {
                            fragment.openBrowserForRecording(
                                    daysWhenRecordingIsPossible.indexOf(selectedDay),
                                    startTime,
                                    type);
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } else {
                    String nameDayOfWeek = calendar.getDisplayName(
                            Calendar.DAY_OF_WEEK,
                            Calendar.LONG, Locale.getDefault());
                    showToast(
                            "Запись возможна на сегодня (" +
                                    nameDayOfWeek  +
                                    ") и два дня вперед",
                            view);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return scheduleItems.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setScheduleItems(List<Map> scheduleItems) {
        this.scheduleItems = scheduleItems;
    }

    public boolean isEmpty() {
        return scheduleItems.isEmpty();
    }

    private void showToast(String toastText, View view) {
        Toast toast = Toast.makeText(view.getContext(), toastText, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
}