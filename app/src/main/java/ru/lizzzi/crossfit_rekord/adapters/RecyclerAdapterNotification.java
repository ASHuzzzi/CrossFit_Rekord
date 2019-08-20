package ru.lizzzi.crossfit_rekord.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import ru.lizzzi.crossfit_rekord.R;
import ru.lizzzi.crossfit_rekord.items.NotificationItem;
import ru.lizzzi.crossfit_rekord.interfaces.NotificationListener;

public class RecyclerAdapterNotification
        extends RecyclerView.Adapter<RecyclerAdapterNotification.ViewHolder> {

    private NotificationListener listener;
    private List<Map<String, Object>> notifications;
    private NotificationItem fields;

    public RecyclerAdapterNotification(
            Context context,
            @NonNull List<Map<String, Object>> notifications,
            NotificationListener listener) {
        this.notifications = notifications;
        this.listener = listener;
        fields = new NotificationItem(context);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView textDateNote;
        private TextView textHeader;
        private TextView textTime;
        private LinearLayout notificationLayout;

        ViewHolder(View view) {
            super(view);
            textDateNote = view.findViewById(R.id.tvTimeNotification);
            textHeader = view.findViewById(R.id.tvHeaderNotification);
            textTime = view.findViewById(R.id.tvForLong);
            notificationLayout = view.findViewById(R.id.llNotification);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_rv_notification,
                parent,
                false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Map documentInfo = notifications.get(position);
        String dateNote = Objects.requireNonNull(documentInfo.get(fields.getDateField())).toString();
        String header = Objects.requireNonNull(documentInfo.get(fields.getHeaderField())).toString();
        int isViewed = Integer.valueOf(
                Objects.requireNonNull(documentInfo.get(fields.getViewedField())).toString());

        long noteTime = Long.valueOf(dateNote);
        String time = String.valueOf(noteTime);

        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();
        Date dateOfNote = new Date(noteTime);

        //проверка на сегодня новость или нет
        SimpleDateFormat dateFormat = dateOfNote.before(today)
                ? new SimpleDateFormat("d MMM yyyy", Locale.getDefault())
                : new SimpleDateFormat("HH:mm", Locale.getDefault());
        dateNote = dateFormat.format(noteTime);

        holder.textDateNote.setText(dateNote);
        holder.textHeader.setText(header);
        holder.textTime.setText(time);

        if(isViewed == 0) {
            holder.textDateNote.setTypeface(null, Typeface.BOLD);
            holder.textHeader.setTypeface(null, Typeface.BOLD);
            holder.notificationLayout.setBackgroundResource(R.color.colorRedPrimary);
        } else {
            holder.textDateNote.setTypeface(null, Typeface.NORMAL);
            holder.textHeader.setTypeface(null, Typeface.NORMAL);
            holder.notificationLayout.setBackgroundResource(R.color.colorWhite);
        }

        holder.notificationLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.selectNotificationInList(
                        Objects.requireNonNull(documentInfo.get(fields.getDateField())).toString());
            }
        });

        holder.notificationLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                listener.deleteNotificationInList(
                        Objects.requireNonNull(documentInfo.get(fields.getDateField())).toString());
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
