package ru.lizzzi.crossfit_rekord.activity;

import android.app.ActivityManager;
import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;

import java.util.Objects;

import ru.lizzzi.crossfit_rekord.R;
import ru.lizzzi.crossfit_rekord.fragments.AboutMeFragment;
import ru.lizzzi.crossfit_rekord.fragments.CalendarWodFragment;
import ru.lizzzi.crossfit_rekord.fragments.CharacterFragment;
import ru.lizzzi.crossfit_rekord.fragments.MyResultsFragment;
import ru.lizzzi.crossfit_rekord.fragments.AlarmSettingsFragment;
import ru.lizzzi.crossfit_rekord.inspectionСlasses.AuthDataCheck;
import ru.lizzzi.crossfit_rekord.fragments.ContactsFragment;
import ru.lizzzi.crossfit_rekord.fragments.LoginFragment;
import ru.lizzzi.crossfit_rekord.fragments.RecordForTrainingSelectFragment;
import ru.lizzzi.crossfit_rekord.fragments.StartScreenFragment;
import ru.lizzzi.crossfit_rekord.fragments.ScheduleFragment;
import ru.lizzzi.crossfit_rekord.interfaces.TitleChange;
import ru.lizzzi.crossfit_rekord.interfaces.ToggleStatusChange;
import ru.lizzzi.crossfit_rekord.model.MainViewModel;
import ru.lizzzi.crossfit_rekord.services.LoadNotificationsService;
import ru.lizzzi.crossfit_rekord.fragments.NotificationFragment;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        TitleChange,
        ToggleStatusChange {

    private BroadcastReceiver broadcastReceiver;
    private final int LOAD_NOTIFICATION = 1;
    private final int UPDATE_NOTIFICATION = 2;
    public final static int STATUS_FINISH = 200;

    private final static String PARAM_TIME = "time";
    public final static String PARAM_TASK = "task";
    public final static String PARAM_RESULT = "result";
    public final static String PARAM_STATUS = "status";
    public final static String BROADCAST_ACTION = "ru.lizzzi.crossfit_rekord.activity";

    private TextView textNotificationCounter;
    private NavigationView navigationView;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle actionBarToggle;
    private MainViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        viewModel = ViewModelProviders.of(MainActivity.this).get(MainViewModel.class);
        initNavigationView();
        initNotificationCounter();
    }

    private void initNavigationView() {
        Toolbar toolbar = findViewById(R.id.appbar);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        actionBarToggle = new ActionBarDrawerToggle(
                this,
                drawer,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we don't want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
                InputMethodManager inputMethodManager =
                        (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(drawerView.getWindowToken(), 0);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
                InputMethodManager inputMethodManager =
                        (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(drawerView.getWindowToken(), 0);
            }
        };
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        LinearLayout linLayoutNavHeader = headerView.findViewById(R.id.navHeader);
        linLayoutNavHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenFragment(StartScreenFragment.class, null);
                DrawerLayout drawer = findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
            }
        });
    }

    private void initNotificationCounter() {
        textNotificationCounter =
                (TextView) navigationView.getMenu().findItem(R.id.notification).getActionView();
    }

    @Override
    protected void  onStart() {
        super.onStart();
        viewModel.checkForAvailabilityDB();
        initBackendlessApi();
        initBroadcastReceiver();
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.container);
        AuthDataCheck authData = new AuthDataCheck();
        boolean checkIsDone = authData.checkAuthData(this);
        if (checkIsDone) {
            if (!isServiceLoadNotificationRunning()) {
                Intent intent;
                intent = new Intent(this, LoadNotificationsService.class)
                        .putExtra(PARAM_TIME, 7)
                        .putExtra(PARAM_TASK, LOAD_NOTIFICATION);
                startService(intent);
            }
            changeToggleStatus(true);
            String menuFragment = getIntent().getStringExtra("notification");
            if (menuFragment != null 
                    && menuFragment.equalsIgnoreCase("RecordForTrainingSelectFragment")) {
                OpenFragment(RecordForTrainingSelectFragment.class, null);
            } else if (fragment == null) {
                initializeCountDrawer();
                OpenFragment(StartScreenFragment.class, null);
            }
        } else {
            if (fragment == null) {
                changeToggleStatus(false);
                OpenFragment(LoginFragment.class, null);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

    public void initBackendlessApi() {
        String API_KEYB = "8764616E-C5FE-CE43-FF54-17B4A8026F00";
        String APPLICATION_IDB = "215CF2B1-C44E-E365-FFB6-9C35DD6A9300";
        Backendless.initApp(this, APPLICATION_IDB, API_KEYB);
    }

    private void initBroadcastReceiver() {
        // создаем BroadcastReceiver
        broadcastReceiver = new BroadcastReceiver() {
            // действия при получении сообщений
            public void onReceive(Context context, Intent intent) {
                int status = intent.getIntExtra(PARAM_STATUS, 0);
                int task = intent.getIntExtra(PARAM_TASK, 0);

                // Ловим сообщения об окончании задачи
                if (status == STATUS_FINISH) {
                    switch (task) {
                        case LOAD_NOTIFICATION:
                            int result = intent.getIntExtra(PARAM_RESULT, 0);
                            if (result > 0) {
                                initializeCountDrawer();
                                drawer.openDrawer(GravityCompat.START   );
                                Toast toast = Toast.makeText(
                                        MainActivity.this,
                                        "Появились свежие новости!",
                                        Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.BOTTOM, 0, 0);
                                toast.show();
                            }
                            break;
                        case UPDATE_NOTIFICATION:
                            initializeCountDrawer();
                            break;
                    }
                }
            }
        };
        // создаем фильтр для BroadcastReceiver
        IntentFilter intFilt = new IntentFilter(BROADCAST_ACTION);
        // регистрируем (включаем) BroadcastReceiver
        registerReceiver(broadcastReceiver, intFilt);
    }

    private void initializeCountDrawer() {
        new Thread(new Runnable() {
            public void run() {
                boolean dbIsAvailable =  viewModel.dbIsAvailable();
                if (dbIsAvailable) {
                    int unreadNotifications = viewModel.getUnreadNotifications();
                    String stCounter =
                            (unreadNotifications > 0)
                                    ? String.valueOf(unreadNotifications)
                                    : "";
                    textNotificationCounter.setGravity(Gravity.CENTER_VERTICAL);
                    textNotificationCounter.setTypeface(null, Typeface.BOLD);
                    textNotificationCounter.setTextColor(getResources().getColor(R.color.colorAccent));
                    textNotificationCounter.setText(stCounter);
                }
            }
        }).run();
    }

    private void OpenFragment(Class fragmentClass, String tag) {
        try {
            Fragment fragment = (Fragment) fragmentClass.newInstance();
            FragmentManager fragmentManager = getSupportFragmentManager();
            for(int i = 0; i < (fragmentManager.getBackStackEntryCount() - 1); i++) {
                fragmentManager.popBackStack();
            }
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.setCustomAnimations(
                    R.anim.pull_in_right,
                    R.anim.push_out_left,
                    R.anim.pull_in_left,
                    R.anim.push_out_right);
            fragmentTransaction.replace(R.id.container, fragment, tag);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isServiceLoadNotificationRunning() {
        Class<?> serviceClass = LoadNotificationsService.class;
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            for (ActivityManager.RunningServiceInfo service :
                    Objects.requireNonNull(manager).getRunningServices(Integer.MAX_VALUE)) {
                if (serviceClass.getName().equals(service.service.getClassName())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void changeToggleStatus(boolean toggleVisible) {
        if (toggleVisible) {
            drawer.addDrawerListener(actionBarToggle);
            actionBarToggle.syncState();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Class fragmentClass = null;
        String fragmentTag =  null;
        int selectedMenuItem = item.getItemId();
        switch (selectedMenuItem) {
            case (R.id.shedule):
                viewModel.setOpenFragment(R.string.title_Table_Fragment);
                fragmentClass = ScheduleFragment.class;
                break;
            case (R.id.record_training):
                viewModel.setOpenFragment(R.string.title_RecordForTraining_Fragment);
                fragmentClass = RecordForTrainingSelectFragment.class;
                break;
            case (R.id.definition):
                viewModel.setOpenFragment(R.string.title_RecordForTraining_Fragment);
                fragmentClass = CharacterFragment.class;
                break;
            case (R.id.contacts):
                viewModel.setOpenFragment(R.string.title_Contacts_Fragment);
                fragmentClass = ContactsFragment.class;
                break;
            case (R.id.profile):
                viewModel.setOpenFragment(R.string.title_AboutMe_Fragment);
                fragmentClass = AboutMeFragment.class;
                break;
            case (R.id.calendar_wod):
                viewModel.setOpenFragment(R.string.title_CalendarWod_Fragment);
                fragmentClass = CalendarWodFragment.class;
                break;
            case (R.id.notification):
                viewModel.setOpenFragment(R.string.title_Notification_Fragment);
                fragmentClass = NotificationFragment.class;
                break;
            case (R.id.myResults):
                viewModel.setOpenFragment(R.string.title_MyResults_Fragment);
                fragmentClass = MyResultsFragment.class;
                break;
            case (R.id.alarm):
                viewModel.setOpenFragment(R.string.title_AlarmSettings_Fragment);
                fragmentClass = AlarmSettingsFragment.class;
                fragmentTag = getResources().getString(R.string.title_AlarmSettings_Fragment);
                break;
        }

        if(viewModel.getFragmentName() != viewModel.getOpenFragment()) {
            OpenFragment(fragmentClass, fragmentTag);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            int backStackEntryCount = getSupportFragmentManager().getBackStackEntryCount();
            if (backStackEntryCount == 1) {
                String APP_PREFERENCES = "audata";
                String APP_PREFERENCES_SELECTEDDAY = "SelectedDay";
                SharedPreferences sharedPreferences =
                        this.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
                sharedPreferences.edit().putString(APP_PREFERENCES_SELECTEDDAY, "0").apply();
                finish();
            } else {
                getSupportFragmentManager().popBackStack();
            }
        }
    }

    @Override
    public void changeTitle(int nameFotTitle, int nameForNavigationDraw) {
        setTitle(nameFotTitle);
        if (viewModel.getFragmentName() == 0) viewModel.setFragmentName(nameFotTitle);
        viewModel.setOpenFragment(nameFotTitle);

        for (int item = 0; item < navigationView.getMenu().size(); item++) {
            navigationView.getMenu().getItem(item).setChecked(false);
        }

        int NOTHING_SELECTED = -1;
        int item = NOTHING_SELECTED;
        switch (nameForNavigationDraw) {
            case (R.string.title_Notification_Fragment):
                item = 0;
                break;
            case (R.string.title_Table_Fragment):
                item = 1;
                break;
            case (R.string.title_RecordForTraining_Fragment):
                item = 2;
                break;
            case (R.string.title_CalendarWod_Fragment):
                item = 3;
                break;
            case (R.string.title_MyResults_Fragment):
                item = 4;
                break;
            case (R.string.title_Character_Fragment):
                item = 5;
                break;
            case (R.string.title_AboutMe_Fragment):
                item = 6;
                break;
            case (R.string.title_AlarmSettings_Fragment):
                item = 7;
                break;
            case (R.string.title_Contacts_Fragment):
                item = 8;
                break;
        }

        if (item != NOTHING_SELECTED) {
            navigationView.getMenu().getItem(item).setChecked(true);
        }
    }
}