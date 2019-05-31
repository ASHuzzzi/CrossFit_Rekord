package ru.lizzzi.crossfit_rekord.activity;

import android.app.ActivityManager;
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
import android.support.v4.view.MenuItemCompat;
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
import ru.lizzzi.crossfit_rekord.data.NotificationDBHelper;
import ru.lizzzi.crossfit_rekord.fragments.AboutMeFragment;
import ru.lizzzi.crossfit_rekord.fragments.CalendarWodFragment;
import ru.lizzzi.crossfit_rekord.fragments.CharacterFragment;
import ru.lizzzi.crossfit_rekord.fragments.MyResultsFragment;
import ru.lizzzi.crossfit_rekord.fragments.NotificationSettingsFragment;
import ru.lizzzi.crossfit_rekord.inspectionСlasses.AuthDataCheck;
import ru.lizzzi.crossfit_rekord.fragments.ContactsFragment;
import ru.lizzzi.crossfit_rekord.fragments.LoginFragment;
import ru.lizzzi.crossfit_rekord.fragments.RecordForTrainingSelectFragment;
import ru.lizzzi.crossfit_rekord.fragments.StartScreenFragment;
import ru.lizzzi.crossfit_rekord.fragments.TableFragment;
import ru.lizzzi.crossfit_rekord.interfaces.ChangeTitle;
import ru.lizzzi.crossfit_rekord.interfaces.ChangeToggleStatus;
import ru.lizzzi.crossfit_rekord.services.LoadNotificationsService;
import ru.lizzzi.crossfit_rekord.fragments.NotificationFragment;


public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        ChangeTitle,
        ChangeToggleStatus {

    private BroadcastReceiver broadcastReceiver;
    private final int LOAD_NOTIFICATION = 1;
    private final int UPDATE_NOTIFICATION = 2;

    public final static int STATUS_FINISH = 200;

    private final static String PARAM_TIME = "time";
    public final static String PARAM_TASK = "task";
    public final static String PARAM_RESULT = "result";
    public final static String PARAM_STATUS = "status";
    public final static String BROADCAST_ACTION = "ru.lizzzi.crossfit_rekord.activity";

    private NotificationDBHelper notificationDBHelper;

    private TextView textNotificationCounter;
    private NavigationView navigationView;
    private int openFragment = 1;
    private int fragmentName = 0;

    private DrawerLayout drawer;
    private ActionBarDrawerToggle actionBarToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initNavigationView();
        initNotificationCounter();
    }

    private void initNavigationView() {
        Toolbar toolbar = findViewById(R.id.appbar);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        actionBarToggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we don't want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
                InputMethodManager inputMethodManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                assert inputMethodManager != null;
                inputMethodManager.hideSoftInputFromWindow(drawerView.getWindowToken(), 0);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
                InputMethodManager inputMethodManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                assert inputMethodManager != null;
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
                OpenFragment(StartScreenFragment.class);
                DrawerLayout drawer = findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
            }
        });
    }

    private void initNotificationCounter() {
        textNotificationCounter = (TextView) MenuItemCompat.getActionView(
                navigationView.getMenu().findItem(R.id.notification));
    }

    @Override
    protected void  onStart() {
        super.onStart();
        checkForAvailabilityDB();
        initBackendlessApi();
        initBroadcastReceiver();
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.container);
        AuthDataCheck authData = new AuthDataCheck();
        boolean checkIsDone = authData.checkAuthData(getContext());
        if (checkIsDone) {
            if (!isServiceLoadNotificationRunning()) {
                Intent intent;

                // Создаем Intent для вызова сервиса,
                // кладем туда параметр времени и код задачи
                intent = new Intent(this, LoadNotificationsService.class).putExtra(PARAM_TIME, 7)
                        .putExtra(PARAM_TASK, LOAD_NOTIFICATION);
                // стартуем сервис
                startService(intent);
            }
            changeToggleStatus(true);
            if (fragment == null) {
                initializeCountDrawer();
                OpenFragment(StartScreenFragment.class);
            }
        } else {
            if (fragment == null) {
                changeToggleStatus(false);
                OpenFragment(LoginFragment.class);
            }

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

    private void checkForAvailabilityDB() {
        notificationDBHelper = new NotificationDBHelper(getContext());
        notificationDBHelper.createDataBase();
    }

    private void initBackendlessApi() {
        final String APPLICATION_IDB = "215CF2B1-C44E-E365-FFB6-9C35DD6A9300";
        final String API_KEYB = "8764616E-C5FE-CE43-FF54-17B4A8026F00";
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
                                Toast toast = Toast.makeText(getContext(), "Появились свежие новости!", Toast.LENGTH_LONG);
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
                boolean bCheckTable =  notificationDBHelper.checkTable();
                if(bCheckTable){
                    int i = notificationDBHelper.countNotification();
                    String stCounter;
                    if (i > 0 ){
                        stCounter = String.valueOf(i);
                    }else {
                        stCounter = "";
                    }
                    textNotificationCounter.setGravity(Gravity.CENTER_VERTICAL);
                    textNotificationCounter.setTypeface(null, Typeface.BOLD);
                    textNotificationCounter.setTextColor(getResources().getColor(R.color.colorAccent));
                    textNotificationCounter.setText(stCounter);
                }
            }
        }).run();
    }

    private void OpenFragment(Class fragmentClass){
        try {
            Fragment fragment = (Fragment) fragmentClass.newInstance();
            FragmentManager fragmentManager = getSupportFragmentManager();
            for(int i = 0; i < (fragmentManager.getBackStackEntryCount()-1); i++) {
                fragmentManager.popBackStack();
            }
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.setCustomAnimations(
                    R.anim.pull_in_right,
                    R.anim.push_out_left,
                    R.anim.pull_in_left,
                    R.anim.push_out_right);
            ft.replace(R.id.container, fragment);
            ft.addToBackStack(null);
            ft.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isServiceLoadNotificationRunning() {
        Class<?> serviceClass = LoadNotificationsService.class;
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            for (ActivityManager.RunningServiceInfo service : Objects.requireNonNull(manager).getRunningServices(Integer.MAX_VALUE)) {
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
        int selectedMenuItem = item.getItemId();
        switch (selectedMenuItem) {
            case (R.id.shedule):
                fragmentName = R.string.title_Table_Fragment;
                fragmentClass = TableFragment.class;
                break;
            case (R.id.record_training):
                fragmentName = R.string.title_RecordForTraining_Fragment;
                fragmentClass = RecordForTrainingSelectFragment.class;
                break;
            case (R.id.definition):
                fragmentName = R.string.title_Character_Fragment;
                fragmentClass = CharacterFragment.class;
                break;
            case (R.id.contacts):
                fragmentName = R.string.title_Contacts_Fragment;
                fragmentClass = ContactsFragment.class;
                break;
            case (R.id.profile):
                fragmentName = R.string.title_AboutMe_Fragment;
                fragmentClass = AboutMeFragment.class;
                break;
            case (R.id.calendar_wod):
                fragmentName = R.string.title_CalendarWod_Fragment;
                fragmentClass = CalendarWodFragment.class;
                break;
            case (R.id.notification):
                fragmentClass = NotificationFragment.class;
                fragmentName = R.string.title_Notification_Fragment;
                break;
            case (R.id.myResults):
                fragmentClass = MyResultsFragment.class;
                fragmentName = R.string.title_MyResults_Fragment;
                break;
            case (R.id.localNotification):
                fragmentClass = NotificationSettingsFragment.class;
                fragmentName = R.string.title_NotificationSettings_Fragment;
                break;
        }

        if(fragmentName != openFragment) {
            OpenFragment(fragmentClass);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private Context getContext() {
        return this;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            int backStackEntryCount = getSupportFragmentManager().getBackStackEntryCount();
            if (backStackEntryCount == 1) {
                final String APP_PREFERENCES = "audata";
                final String APP_PREFERENCES_SELECTEDDAY = "SelectedDay";
                SharedPreferences mSettings = getContext().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = mSettings.edit();
                editor.putString(APP_PREFERENCES_SELECTEDDAY, "0");
                editor.apply();
                finish();
            } else {
                getSupportFragmentManager().popBackStack();
            }
        }
    }

    @Override
    public void changeTitle(int nameFotTitle, int nameForNavigationDraw) {
        setTitle(nameFotTitle);
        if (fragmentName == 0) fragmentName = nameFotTitle;
        openFragment = nameFotTitle;

        for (int index = 0; index < navigationView.getMenu().size(); index++) {
            navigationView.getMenu().getItem(index).setChecked(false);
        }

        int navigationViewIndex = -1;
        switch (nameForNavigationDraw) {
            case (R.string.title_Notification_Fragment):
                navigationViewIndex = 0;
                break;
            case (R.string.title_Table_Fragment):
                navigationViewIndex = 1;
                break;
            case (R.string.title_RecordForTraining_Fragment):
                navigationViewIndex = 2;
                break;
            case (R.string.title_CalendarWod_Fragment):
                navigationViewIndex = 3;
                break;
            case (R.string.title_MyResults_Fragment):
                navigationViewIndex = 4;
                break;
            case (R.string.title_Character_Fragment):
                navigationViewIndex = 5;
                break;
            case (R.string.title_AboutMe_Fragment):
                navigationViewIndex = 6;
                break;
            case (R.string.title_NotificationSettings_Fragment):
                navigationViewIndex = 7;
                break;
            case (R.string.title_Contacts_Fragment):
                navigationViewIndex = 8;
                break;
        }

        if (navigationViewIndex != -1) {
            navigationView.getMenu().getItem(navigationViewIndex).setChecked(true);
        }
    }
}