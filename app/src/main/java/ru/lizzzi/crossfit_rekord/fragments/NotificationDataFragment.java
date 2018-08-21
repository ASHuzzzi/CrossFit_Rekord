package ru.lizzzi.crossfit_rekord.fragments;


import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ru.lizzzi.crossfit_rekord.R;
import ru.lizzzi.crossfit_rekord.activity.MainActivity;
import ru.lizzzi.crossfit_rekord.data.NotificationDBHelper;

public class NotificationDataFragment extends Fragment {

    private TextView tvTime;
    private TextView tvHeader;
    private TextView tvText;

    private String stTime;
    private String stHeader;
    private String stText;

    private Long convertTime;

    Bundle bundle;

    int time;
    int task;

    final int TASK1_CODE = 1;
    public final static String PARAM_TIME = "time";
    public final static String PARAM_TASK = "task";
    public final static String PARAM_RESULT = "result";
    public final static String PARAM_STATUS = "status";
    public final static int STATUS_START = 100;
    public final static int STATUS_FINISH = 200;
    IntentFilter intFilt;
    Intent intent;
    private NotificationDBHelper mDBHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_notificationdata, container, false);

        tvTime = v.findViewById(R.id.tvTimeNotification2);
        tvHeader = v.findViewById(R.id.tvHeaderNotification2);
        tvText = v.findViewById(R.id.tvTextNotification);

        mDBHelper = new NotificationDBHelper(getContext());
        intent = new Intent(MainActivity.BROADCAST_ACTION);
        time = intent.getIntExtra(PARAM_TIME, 1);
        task = intent.getIntExtra(PARAM_TASK, 0);




        return v;
    }

    public void onResume() {

        super.onResume();
        bundle = getArguments();
        stText = bundle.getString("dateNote");
        stHeader = bundle.getString("headerNote");

        convertTime = Long.valueOf(stText);
        String ttt = mDBHelper.selectTextNotification(convertTime);
        tvText.setText(ttt);
        mDBHelper.updateStatusNotification(convertTime);

        intent.putExtra(PARAM_TASK, 1);
        intent.putExtra(PARAM_STATUS, MainActivity.STATUS_FINISH);
        intent.putExtra(PARAM_RESULT, "1");
        getActivity().sendBroadcast(intent);
    }
}
