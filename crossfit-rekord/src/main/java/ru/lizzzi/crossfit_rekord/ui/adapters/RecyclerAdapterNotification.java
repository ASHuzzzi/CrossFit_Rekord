package ru.lizzzi.crossfit_rekord.ui.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ru.lizzzi.crossfit_rekord.R;
import ru.lizzzi.crossfit_rekord.ui.fragments.NotificationFragment;
import ru.lizzzi.crossfit_rekord.items.NotificationItem;

public class RecyclerAdapterNotification
        extends RecyclerView.Adapter<RecyclerAdapterNotification.ViewHolder> {

    private List<NotificationItem> notifications;
    private NotificationFragment fragment;

    public RecyclerAdapterNotification(NotificationFragment fragment) {
        this.fragment = fragment;
        notifications = new ArrayList<>();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView textDateNote;
        private TextView textHeader;
        private TextView textTime;
        private RelativeLayout notificationLayout;
        private ImageView imageHighPriority;

        ViewHolder(View view) {
            super(view);
            textDateNote = view.findViewById(R.id.tvTimeNotification);
            textHeader = view.findViewById(R.id.tvHeaderNotification);
            textTime = view.findViewById(R.id.tvLongTime);
            notificationLayout = view.findViewById(R.id.layoutNotification);
            imageHighPriority = view.findViewById(R.id.imagePriorityHigh);
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
        final int itemPosition = position;
        String dateNote = String.valueOf(notifications.get(position).getDate());
        String header = notifications.get(position).getHeader();
        boolean isViewed = notifications.get(position).isView();

        long noteTime = Long.valueOf(dateNote);

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
        holder.textTime.setText(String.valueOf(noteTime));
        Animation animation = AnimationUtils.loadAnimation(fragment.getContext(), R.anim.pulse);
        if (isViewed) {
            holder.imageHighPriority.setVisibility(View.GONE);
        } else {
            holder.imageHighPriority.setVisibility(View.VISIBLE);
            holder.imageHighPriority.startAnimation(animation);
        }

        holder.notificationLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragment.openNotificationDataFragment(notifications.get(itemPosition).getDate());
            }
        });

        holder.notificationLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                fragment.showAlertDialog(notifications.get(itemPosition).getDate());
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

    public boolean isEmpty() {
        return notifications.isEmpty();
    }

    public void setNotifications(List<NotificationItem> notifications) {
        this.notifications = notifications;
    }
}
