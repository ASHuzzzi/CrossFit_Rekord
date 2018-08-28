package ru.lizzzi.crossfit_rekord.activity;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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

import java.io.IOException;
import java.util.Objects;

import ru.lizzzi.crossfit_rekord.R;
import ru.lizzzi.crossfit_rekord.data.NotificationDBHelper;
import ru.lizzzi.crossfit_rekord.draft.wod_result_fragment.Result_Fragment;
import ru.lizzzi.crossfit_rekord.fragments.AboutMeFragment;
import ru.lizzzi.crossfit_rekord.fragments.CalendarWodFragment;
import ru.lizzzi.crossfit_rekord.fragments.CharacterFragment;
import ru.lizzzi.crossfit_rekord.fragments.CheckAuthData;
import ru.lizzzi.crossfit_rekord.fragments.LoginFragment;
import ru.lizzzi.crossfit_rekord.fragments.RecordForTrainingSelectFragment;
import ru.lizzzi.crossfit_rekord.fragments.StartScreenFragment;
import ru.lizzzi.crossfit_rekord.fragments.TableFragment;
import ru.lizzzi.crossfit_rekord.services.LoadNotificationsService;
import ru.lizzzi.crossfit_rekord.fragments.NotificationFragment;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private CheckAuthData checkAuthData = new CheckAuthData();

    private BroadcastReceiver br;
    private final int LOAD_NOTIFICATION = 1;
    private final int UPDATE_NOTIFICATION = 2;

    public final static int STATUS_FINISH = 200;

    public final static String PARAM_TIME = "time";
    public final static String PARAM_TASK = "task";
    public final static String PARAM_RESULT = "result";
    public final static String PARAM_STATUS = "status";
    public final static String BROADCAST_ACTION = "ru.lizzzi.crossfit_rekord.activity";

    public static final String APPLICATION_IDB = "215CF2B1-C44E-E365-FFB6-9C35DD6A9300";
    public static final String API_KEYB = "8764616E-C5FE-CE43-FF54-17B4A8026F00";

    private NotificationDBHelper mDBHelper;

    private TextView tvNotificationCounter;
    private int lastClicked = 0;
    private  String tag = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        final DrawerLayout drawer = findViewById(R.id.drawer_layout);

        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close){

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
                InputMethodManager inputMethodManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                assert inputMethodManager != null;
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
                InputMethodManager inputMethodManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                assert inputMethodManager != null;
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }



        };
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerview = navigationView.getHeaderView(0);

        LinearLayout llHeader = headerview.findViewById(R.id.llheader);
        llHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenFragment(StartScreenFragment.class, StartScreenFragment.class, null);
                DrawerLayout drawer = findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
            }
        });
        tvNotificationCounter = (TextView) MenuItemCompat.getActionView(navigationView.getMenu().findItem(R.id.notification));



        mDBHelper = new NotificationDBHelper(getContext());
        try {
            mDBHelper.createDataBase();
        } catch (IOException ioe) {
            throw new Error("Unable to create database");
        }
        mDBHelper.openDataBase();
        mDBHelper.close();

        Backendless.initApp(this, APPLICATION_IDB, API_KEYB);

        // создаем BroadcastReceiver
        br = new BroadcastReceiver() {
            // действия при получении сообщений
            public void onReceive(Context context, Intent intent) {
                int status = intent.getIntExtra(PARAM_STATUS, 0);
                int task = intent.getIntExtra(PARAM_TASK, 0);

                // Ловим сообщения об окончании задачи
                if (status == STATUS_FINISH) {
                    switch (task) {
                        case LOAD_NOTIFICATION:
                            int result = intent.getIntExtra(PARAM_RESULT, 0);
                            if (result > 0){
                                initializeCountDrawer();
                                drawer.openDrawer(GravityCompat.START   );
                                Toast toast = Toast.makeText(getContext(), "Появились новые уведомления!", Toast.LENGTH_LONG);
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
        registerReceiver(br, intFilt);




        OpenFragment(StartScreenFragment.class, StartScreenFragment.class, null);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        Class fragmentClass = null;
        Class nextFragmentClass = null;

        int id = item.getItemId();

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        if(lastClicked != id){
            if (id == R.id.shedule) {
                fragmentClass = TableFragment.class;
                tag = "Table";

            } else if (id == R.id.record_training) {

                if (checkAuthData.checkAuthData(getContext())){
                    fragmentClass = RecordForTrainingSelectFragment.class;
                }else {
                    fragmentClass = LoginFragment.class;
                    nextFragmentClass = RecordForTrainingSelectFragment.class;

                }

            } else if (id == R.id.result) {
                fragmentClass = Result_Fragment.class;

            } else if (id == R.id.definition) {
                fragmentClass = CharacterFragment.class;

            } else if (id == R.id.contacts) {


            } else if (id == R.id.profile) {
                if (checkAuthData.checkAuthData(getContext())){
                    fragmentClass = AboutMeFragment.class;
                }else {
                    fragmentClass = LoginFragment.class;
                    nextFragmentClass = AboutMeFragment.class;
                }

            } else if (id == R.id.calendar_wod){
                fragmentClass = CalendarWodFragment.class;
            }else if (id == R.id.notification){
                fragmentClass = NotificationFragment.class;
            }

            OpenFragment(fragmentClass, nextFragmentClass, tag);
            lastClicked = id;
        }

        return true;
    }

    public Context getContext() {
        return this;
    }

    public void OpenFragment(Class fragmentClass, Class nextFragmentClass, String tagFragment){

        Fragment fragment = null;


        try {
            fragment = (Fragment) (fragmentClass != null ? fragmentClass.newInstance() : null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (fragmentClass != null && fragmentClass.equals(LoginFragment.class)) {
            Bundle bundle = new Bundle();

            if (nextFragmentClass.equals(RecordForTrainingSelectFragment.class)) {
                bundle.putString("fragment", String.valueOf(R.string.strRecordFragment));
            }
            if (nextFragmentClass.equals(AboutMeFragment.class)) {
                bundle.putString("fragment", String.valueOf(R.string.strAboutMeFragment));
            }

            if (fragment != null) {
                fragment.setArguments(bundle);
            }
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.replace(R.id.container, fragment, tagFragment);
        ft.addToBackStack(null);
        ft.commit();

    }

    @Override
    public void onBackPressed(){
        lastClicked = 0;
        int count = getSupportFragmentManager().getBackStackEntryCount();
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }
        else{
            if (count == 1) {
                finish();
            } else {
                getSupportFragmentManager().popBackStack();
            }
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
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
    protected void onResume() {
        super.onResume();

        initializeCountDrawer();
        if (!isMyServiceRunning(LoadNotificationsService.class)){
            Intent intent;

            // Создаем Intent для вызова сервиса,
            // кладем туда параметр времени и код задачи
            intent = new Intent(this, LoadNotificationsService.class).putExtra(PARAM_TIME, 7)
                    .putExtra(PARAM_TASK, LOAD_NOTIFICATION);
            // стартуем сервис
            startService(intent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // выключаем BroadcastReceiver
        unregisterReceiver(br);
    }

    private void initializeCountDrawer() {
        new Thread(new Runnable() {
            public void run() {
                int i = mDBHelper.countNotification();
                tvNotificationCounter.setGravity(Gravity.CENTER_VERTICAL);
                tvNotificationCounter.setTypeface(null, Typeface.BOLD);
                tvNotificationCounter.setTextColor(getResources().getColor(R.color.colorAccent));
                tvNotificationCounter.setText(String.valueOf(i));
            }
        }).run();

    }



    }