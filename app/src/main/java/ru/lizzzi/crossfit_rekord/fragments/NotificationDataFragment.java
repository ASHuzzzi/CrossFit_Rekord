package ru.lizzzi.crossfit_rekord.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ru.lizzzi.crossfit_rekord.R;
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

    private NotificationDBHelper mDBHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_notificationdata, container, false);

        tvTime = v.findViewById(R.id.tvTimeNotification2);
        tvHeader = v.findViewById(R.id.tvHeaderNotification2);
        tvText = v.findViewById(R.id.tvTextNotification);

        mDBHelper = new NotificationDBHelper(getContext());

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
    }
}
