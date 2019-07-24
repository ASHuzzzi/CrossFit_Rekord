package ru.lizzzi.crossfit_rekord.fragments;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import ru.lizzzi.crossfit_rekord.R;
import ru.lizzzi.crossfit_rekord.activity.MainActivity;
import ru.lizzzi.crossfit_rekord.data.SQLiteStorageNotification;
import ru.lizzzi.crossfit_rekord.interfaces.ChangeTitle;

public class NotificationDataFragment extends Fragment {

    private TextView tvTime;
    private TextView tvHeader;
    private TextView tvText;

    private String stHeader;
    private String stText;

    private Long convertTime;

    @SuppressLint("SimpleDateFormat")
    private final SimpleDateFormat sdf2 = new SimpleDateFormat("d MMM yyyy");
    @SuppressLint("SimpleDateFormat")
    private final SimpleDateFormat sdf3 = new SimpleDateFormat("HH:mm");


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_notificationdata, container, false);

        tvTime = v.findViewById(R.id.tvTimeNotificationND);
        tvHeader = v.findViewById(R.id.tvHeaderNotificationND);
        tvText = v.findViewById(R.id.tvTextNotificationND);

        Bundle bundle = getArguments();
        String stTime = bundle.getString("dateNote");
        stHeader = bundle.getString("headerNote");
        convertTime = Long.valueOf(stTime);

        SQLiteStorageNotification mDBHelper = new SQLiteStorageNotification(getContext());
        List<String> listDetailNotification = mDBHelper.selectTextNotification(convertTime);
        if (listDetailNotification.size() > 0){
            stText = listDetailNotification.get(0);
            String stVewed = listDetailNotification.get(1);
            if (stVewed.equals("0")){
                mDBHelper.updateStatusNotification(convertTime, 1);

                Intent intent = new Intent(MainActivity.BROADCAST_ACTION);
                String PARAM_TASK = "task";
                String PARAM_RESULT = "result";
                String PARAM_STATUS = "status";
                intent.putExtra(PARAM_TASK, 2);
                intent.putExtra(PARAM_STATUS, MainActivity.STATUS_FINISH);
                intent.putExtra(PARAM_RESULT, 1);
                getActivity().sendBroadcast(intent);
            }
        }

        return v;
    }

    @Override
    public  void onStart() {
        super.onStart();
        if (getActivity() instanceof ChangeTitle){
            ChangeTitle listernerChangeTitle = (ChangeTitle) getActivity();
            listernerChangeTitle.changeTitle(R.string.title_NotificationData_Fragment, R.string.title_Notification_Fragment);
        }

    }

    public void onResume() {

        super.onResume();
        tvHeader.setText(stHeader);

        tvText.setText(stText);

        // set the calendar to start of today
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        Date today = c.getTime();
        Date dDateNote = new Date(convertTime);

        //проверка на сегодня новость или нет
        if (dDateNote.before(today)) {
            tvTime.setText(sdf2.format(convertTime));
        }else{
            tvTime.setText(sdf3.format(convertTime));
        }
    }
}
