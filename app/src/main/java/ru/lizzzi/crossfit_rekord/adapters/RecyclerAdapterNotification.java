package ru.lizzzi.crossfit_rekord.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import ru.lizzzi.crossfit_rekord.R;
import ru.lizzzi.crossfit_rekord.documentfields.DocumentFieldsNotification;
import ru.lizzzi.crossfit_rekord.fragments.NotificationFragment;
import ru.lizzzi.crossfit_rekord.interfaces.ListernerNotification;

public class RecyclerAdapterNotification extends BaseAdapter {

    private ListernerNotification mListener;
    private List<Map<String, Object>> notifications;
    private LayoutInflater inflater;
    private int layoutId;
    private final ThreadLocal<DocumentFieldsNotification> fields = new ThreadLocal<>();
    @SuppressLint("SimpleDateFormat") final SimpleDateFormat sdf2 = new SimpleDateFormat("dd MMM yyyy HH:mm");


    public RecyclerAdapterNotification(Context context, int layoutId, @NonNull List<Map<String, Object>> notifications, ListernerNotification listener) {
        this.notifications = notifications;
        inflater = LayoutInflater.from(context);
        mListener =listener;
        fields.set(new DocumentFieldsNotification(context));
        this.layoutId = layoutId;
    }

    @Override
    public int getCount() {
        return notifications.size();
    }

    @Override
    public Object getItem(int position) {
        return notifications.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder;

        if (view != null) {
            holder = (ViewHolder) view.getTag();
        } else {
            view = inflater.inflate(layoutId, parent, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        }

        customizeView(view, holder, notifications.get(position));

        return view;
    }

    @SuppressLint({"ResourceAsColor", "SimpleDateFormat"})
    private void customizeView(View view, final RecyclerAdapterNotification.ViewHolder holder, final Map documentInfo) {

        //final Map documentInfo = shediletems.get(position);
        String stdateNote = (String) documentInfo.get(fields.get().getDateField());
        final long ldateNote;
        ldateNote = Long.valueOf(stdateNote);
        String stLongToString = String.valueOf(ldateNote);
        holder.tvForLong.setText(stLongToString);
        stdateNote = sdf2.format(ldateNote);


        final String stheader = (String) documentInfo.get(fields.get().getHeaderField());
        int stviewed = Integer.valueOf((String) documentInfo.get(fields.get().getViewedField())) ;

        holder.stDateNote.setText(stdateNote);
        holder.stHeader.setText(stheader);

        if(stviewed == 0){
            holder.stDateNote.setTypeface(null, Typeface.BOLD);
            holder.stHeader.setTypeface(null, Typeface.BOLD);
        }

        LinearLayout llNotification = view.findViewById(R.id.llNotification);



        llNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.selectNotificationInList(
                        (String) documentInfo.get(fields.get().getDateField()),
                        (String) documentInfo.get(fields.get().getHeaderField()));
            }
        });
    }

    static class ViewHolder {

        private TextView stDateNote;
        private TextView stHeader;
        private TextView stText;
        private TextView stViewed;
        private TextView tvForLong;

        ViewHolder(View view) {
            stDateNote = view.findViewById(R.id.tvTimeNotification);
            stHeader = view.findViewById(R.id.tvHeaderNotification);
            tvForLong = view.findViewById(R.id.tvForLong);

        }
    }
}