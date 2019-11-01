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
    private List<Integer> daysWhenRecordingIsPossible;

    public RecyclerAdapterSchedule(GymScheduleFragment fragment) {
        this.fragment = fragment;
        item = new ScheduleItem(fragment.getContext());
        scheduleItems = new ArrayList<>();
        daysWhenRecordingIsPossible = fragment.setDaysWhenRecordingIsPossible();
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
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position){
        final String startTime =
                String.valueOf(scheduleItems.get(position).get(item.getStartTime()));
        final String workoutType = String.valueOf(scheduleItems.get(position).get(item.getType()));

        holder.startTimeItem.setText(startTime);
        holder.typesItem.setText(workoutType);

        BackgroundDrawable backgroundDrawable = new BackgroundDrawable();
        int drawable = backgroundDrawable.getBackgroundDrawable(workoutType);
        holder.layoutItem.setBackgroundResource(drawable);
        holder.layoutItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int selectedDay = fragment.getSelectedDay();
                boolean canSignUp = false;
                String toastText = "Выберите более позднее время";

                if (daysWhenRecordingIsPossible.contains(selectedDay)) {
                    if (selectedToday(selectedDay)) {
                        if (canSignUpToday(startTime)) {
                            canSignUp = true;
                        }
                    } else {
                        canSignUp = true;
                    }
                } else {
                    toastText = getToastText();
                }

                if (canSignUp) {
                    fragment.openBrowserForRecording(
                            daysWhenRecordingIsPossible.indexOf(selectedDay),
                            startTime,
                            workoutType);
                } else {
                    showToast(toastText);
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

    private boolean selectedToday(int selectedDay) {
        return daysWhenRecordingIsPossible.get(0).equals(selectedDay);
    }

    private boolean canSignUpToday(String startTime) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            Calendar calendar = Calendar.getInstance();
            int hourNow = calendar.get(Calendar.HOUR_OF_DAY);
            calendar.setTime(dateFormat.parse(startTime));
            int selectHour = calendar.get(Calendar.HOUR_OF_DAY);

            //проверяем чтобы выбранное время было позже чем сейчас
            return selectHour > hourNow;
        } catch (ParseException e) {
            return false;
        }
    }

    private String getToastText() {
        String nameDayOfWeek = Calendar.getInstance().getDisplayName(
                Calendar.DAY_OF_WEEK,
                Calendar.LONG,
                Locale.getDefault());
        return "Запись возможна на сегодня (" + nameDayOfWeek  + ") и два дня вперед";
    }

    private void showToast(String toastText) {
        Toast toast = Toast.makeText(fragment.getContext(), toastText, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
}