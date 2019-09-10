package ru.lizzzi.crossfit_rekord.fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ru.lizzzi.crossfit_rekord.R;
import ru.lizzzi.crossfit_rekord.interfaces.TitleChange;
import ru.lizzzi.crossfit_rekord.model.NotificationDataViewModel;

public class NotificationDataFragment extends Fragment {

    private TextView textTime;
    private TextView textHeader;
    private TextView textText;

    private NotificationDataViewModel viewModel;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {
        View view =
                inflater.inflate(R.layout.fragment_notificationdata, container, false);
        viewModel = ViewModelProviders.of(NotificationDataFragment.this)
                .get(NotificationDataViewModel.class);

        textTime = view.findViewById(R.id.tvTimeNotificationND);
        textHeader = view.findViewById(R.id.tvHeaderNotificationND);
        textText = view.findViewById(R.id.tvTextNotificationND);

        viewModel.getNotification(getArguments());
        return view;
    }

    @Override
    public  void onStart() {
        super.onStart();
        TitleChange listenerTitleChange = (TitleChange) getActivity();
        if (listenerTitleChange != null) {
            listenerTitleChange.changeTitle(
                    R.string.title_NotificationData_Fragment,
                    R.string.title_Notification_Fragment);
        }

    }

    public void onResume() {
        super.onResume();
        textHeader.setText(viewModel.getNotificationHeader());
        textText.setText(viewModel.getNotificationText());
        textTime.setText(viewModel.getNotificationTime());
    }
}
