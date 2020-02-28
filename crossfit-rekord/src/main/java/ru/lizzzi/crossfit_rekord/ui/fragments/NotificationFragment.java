package ru.lizzzi.crossfit_rekord.ui.fragments;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import ru.lizzzi.crossfit_rekord.R;
import ru.lizzzi.crossfit_rekord.ui.activities.MainActivity;
import ru.lizzzi.crossfit_rekord.ui.adapters.RecyclerAdapterNotification;
import ru.lizzzi.crossfit_rekord.interfaces.TitleChange;
import ru.lizzzi.crossfit_rekord.model.NotificationViewModel;

public class NotificationFragment extends Fragment {

    private RecyclerView recyclerViewNotification;
    private ProgressBar progressBar;
    private RecyclerAdapterNotification adapter;

    private BroadcastReceiver broadcastReceiver;
    private NotificationViewModel viewModel;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification, container, false);
        viewModel = ViewModelProviders.of(NotificationFragment.this)
                .get(NotificationViewModel.class);

        recyclerViewNotification = view.findViewById(R.id.rvNotoficationList);
        adapter = new RecyclerAdapterNotification(NotificationFragment.this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerViewNotification.setLayoutManager(layoutManager);
        recyclerViewNotification.setAdapter(adapter);

        progressBar = view.findViewById(R.id.pbNotification);

        return  view;
    }

    @Override
    public  void onStart() {
        super.onStart();
        TitleChange listenerTitleChange = (TitleChange) requireActivity();
        listenerTitleChange.changeTitle(
                R.string.title_Notification_Fragment,
                R.string.title_Notification_Fragment);
        initBroadcastReceiver();
        getNotifications();
    }

    public void onPause() {
        super.onPause();
        requireActivity().unregisterReceiver(broadcastReceiver);
    }

    private void initBroadcastReceiver() {
        broadcastReceiver = new BroadcastReceiver() {
            // действия при получении сообщений
            public void onReceive(Context context, Intent intent) {
                int task = intent.getIntExtra(MainActivity.PARAM_TASK, 0);
                int status = intent.getIntExtra(MainActivity.PARAM_STATUS, 0);
                // Ловим сообщения об окончании задач
                if (status == MainActivity.STATUS_FINISH) {
                    int result = intent.getIntExtra(MainActivity.PARAM_RESULT, 0);
                    if (task == MainActivity.LOAD_NOTIFICATION) {
                        if (result > 0) {
                            getNotifications();
                        }
                    }
                }
            }
        };
        // создаем фильтр для BroadcastReceiver
        IntentFilter intentFilter = new IntentFilter(MainActivity.BROADCAST_ACTION);
        // регистрируем (включаем) BroadcastReceiver
        requireActivity().registerReceiver(broadcastReceiver, intentFilter);
    }

    private void getNotifications() {
        recyclerViewNotification.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        LiveData<Boolean> liveData = viewModel.loadNotification();
        liveData.observe(NotificationFragment.this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean notificationsIsLoaded) {
                if (notificationsIsLoaded) {
                    adapter.setNotifications(viewModel.getNotificationList());
                    adapter.notifyDataSetChanged();
                    recyclerViewNotification.setVisibility(View.VISIBLE);
                }
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

    public void openNotificationDataFragment(long dateNote) {
        Bundle bundle = new Bundle();
        bundle.putLong("dateNote", dateNote);
        NotificationDataFragment notificationDataFragment =
                new NotificationDataFragment();
        notificationDataFragment.setArguments(bundle);
        FragmentManager fragmentManager = getFragmentManager();
        if (fragmentManager != null) {
            FragmentTransaction fragmentTransaction =
                    fragmentManager.beginTransaction();
            fragmentTransaction.setCustomAnimations(
                    R.anim.pull_in_right,
                    R.anim.push_out_left,
                    R.anim.pull_in_left,
                    R.anim.push_out_right);
            fragmentTransaction.replace(R.id.container, notificationDataFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
    }

    public void showAlertDialog(long selectedDateNote) {
        final long dateNoteForDelete = selectedDateNote;
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder
                .setMessage("Удалить новость?")
                .setCancelable(false)
                .setPositiveButton("Да",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(
                                    DialogInterface dialogInterface,
                                    int i) {
                                viewModel.deleteNotification(dateNoteForDelete);
                                getNotifications();
                                sendBroadCast();
                            }
                        })
                .setNegativeButton("Нет",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
        alert.getButton(AlertDialog.BUTTON_POSITIVE)
                .setTextColor(getResources().getColor(R.color.colorAccent));
        alert.getButton(AlertDialog.BUTTON_NEGATIVE)
                .setTextColor(getResources().getColor(R.color.colorPrimary));
    }

    public void sendBroadCast() {
        Intent intent = new Intent(MainActivity.BROADCAST_ACTION);
        intent
                .putExtra(MainActivity.PARAM_TASK, MainActivity.UPDATE_NOTIFICATION)
                .putExtra(MainActivity.PARAM_STATUS, MainActivity.STATUS_FINISH)
                .putExtra(MainActivity.PARAM_RESULT, MainActivity.LOAD_NOTIFICATION);
        requireActivity().sendBroadcast(intent);
    }
}
