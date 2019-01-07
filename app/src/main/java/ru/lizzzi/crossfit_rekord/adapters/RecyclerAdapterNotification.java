package ru.lizzzi.crossfit_rekord.adapters;

import android.annotation.SuppressLint;
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
import java.util.Map;

import ru.lizzzi.crossfit_rekord.R;
import ru.lizzzi.crossfit_rekord.documentfields.DocumentFieldsNotification;
import ru.lizzzi.crossfit_rekord.interfaces.ListernerNotification;

public class RecyclerAdapterNotification extends RecyclerView.Adapter<RecyclerAdapterNotification.ViewHolder> {

    private ListernerNotification mListener;
    private List<Map<String, Object>> notifications;
    private DocumentFieldsNotification fields;
    @SuppressLint("SimpleDateFormat")
    private final SimpleDateFormat sdf2 = new SimpleDateFormat("d MMM yyyy");
    @SuppressLint("SimpleDateFormat")
    private final SimpleDateFormat sdf3 = new SimpleDateFormat("HH:mm");


    public RecyclerAdapterNotification(Context context, @NonNull List<Map<String, Object>> notifications, ListernerNotification listener) {
        this.notifications = notifications;
        mListener =listener;
        fields = new DocumentFieldsNotification(context);
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        private TextView stDateNote;
        private TextView stHeader;
        private TextView tvForLong;
        private LinearLayout llNotification;

        ViewHolder(View view) {
            super(view);
            stDateNote = view.findViewById(R.id.tvTimeNotification);
            stHeader = view.findViewById(R.id.tvHeaderNotification);
            tvForLong = view.findViewById(R.id.tvForLong);
            llNotification = view.findViewById(R.id.llNotification);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rv_notification, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position){
        final Map documentInfo = notifications.get(position);
        String stdateNote = (String) documentInfo.get(fields.getDateField());
        String stheader = (String) documentInfo.get(fields.getHeaderField());
        int stviewed = Integer.valueOf((String) documentInfo.get(fields.getViewedField()));

        long ldateNote = Long.valueOf(stdateNote);
        String stLongToString = String.valueOf(ldateNote);

        // set the calendar to start of today
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        Date today = c.getTime();
        Date dDateNote = new Date(ldateNote);

        //проверка на сегодня новость или нет
        if (dDateNote.before(today)) {
            stdateNote = sdf2.format(ldateNote);
        }else{
            stdateNote = sdf3.format(ldateNote);
        }

        holder.stDateNote.setText(stdateNote);
        holder.stHeader.setText(stheader);
        holder.tvForLong.setText(stLongToString);

        if(stviewed == 0){
            holder.stDateNote.setTypeface(null, Typeface.BOLD);
            holder.stHeader.setTypeface(null, Typeface.BOLD);
            holder.llNotification.setBackgroundResource(R.color.colorRedPrimary);
        }else {
            holder.stDateNote.setTypeface(null, Typeface.NORMAL);
            holder.stHeader.setTypeface(null, Typeface.NORMAL);
            holder.llNotification.setBackgroundResource(R.color.colorWhite);
        }

        holder.llNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.selectNotificationInList(
                        (String) documentInfo.get(fields.getDateField()),
                        (String) documentInfo.get(fields.getHeaderField()));
            }
        });

        holder.llNotification.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mListener.deleteNotificationInList(
                        (String) documentInfo.get(fields.getDateField())
                );
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
