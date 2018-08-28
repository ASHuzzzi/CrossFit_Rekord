package ru.lizzzi.crossfit_rekord.fragments;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

import ru.lizzzi.crossfit_rekord.R;
import ru.lizzzi.crossfit_rekord.activity.MainActivity;
import ru.lizzzi.crossfit_rekord.data.NotificationDBHelper;

public class NotificationDataFragment extends Fragment {

    private TextView tvTime;
    private TextView tvHeader;
    private TextView tvText;

    private String stHeader;
    private String stText;

    private Long convertTime;
    List <String> listDetailNotification;

    @SuppressLint("SimpleDateFormat") final SimpleDateFormat sdf2 = new SimpleDateFormat("dd MMM yyyy HH:mm");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_notificationdata, container, false);

        getActivity().setTitle(R.string.title_Notification_Fragment);

        tvTime = v.findViewById(R.id.tvTimeNotificationND);
        tvHeader = v.findViewById(R.id.tvHeaderNotificationND);
        tvText = v.findViewById(R.id.tvTextNotificationND);

        Bundle bundle = getArguments();
        String stTime = bundle.getString("dateNote");
        stHeader = bundle.getString("headerNote");
        convertTime = Long.valueOf(stTime);

        NotificationDBHelper mDBHelper = new NotificationDBHelper(getContext());
        listDetailNotification = mDBHelper.selectTextNotification(convertTime);
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

    public void onResume() {

        super.onResume();
        tvHeader.setText(stHeader);
        tvTime.setText(sdf2.format(convertTime));
        tvText.setText(stText);
    }
}
