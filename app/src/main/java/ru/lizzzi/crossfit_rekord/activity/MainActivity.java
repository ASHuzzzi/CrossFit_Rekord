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

import java.io.IOException;
import java.util.Objects;

import ru.lizzzi.crossfit_rekord.R;
import ru.lizzzi.crossfit_rekord.data.NotificationDBHelper;
import ru.lizzzi.crossfit_rekord.fragments.AboutMeFragment;
import ru.lizzzi.crossfit_rekord.fragments.CalendarWodFragment;
import ru.lizzzi.crossfit_rekord.fragments.CharacterFragment;
import ru.lizzzi.crossfit_rekord.fragments.MyResultsFragment;
import ru.lizzzi.crossfit_rekord.inspectionСlasses.AuthDataCheck;
import ru.lizzzi.crossfit_rekord.fragments.ContactsFragment;
import ru.lizzzi.crossfit_rekord.fragments.LoginFragment;
import ru.lizzzi.crossfit_rekord.fragments.RecordForTrainingSelectFragment;
import ru.lizzzi.crossfit_rekord.fragments.StartScreenFragment;
import ru.lizzzi.crossfit_rekord.fragments.TableFragment;
import ru.lizzzi.crossfit_rekord.interfaces.InterfaceChangeTitle;
import ru.lizzzi.crossfit_rekord.interfaces.InterfaceChangeToggleStatus;
import ru.lizzzi.crossfit_rekord.services.LoadNotificationsService;
import ru.lizzzi.crossfit_rekord.fragments.NotificationFragment;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, InterfaceChangeTitle, InterfaceChangeToggleStatus {

    private AuthDataCheck checkAuthData = new AuthDataCheck();

    private BroadcastReceiver br;
    private final int LOAD_NOTIFICATION = 1;
    private final int UPDATE_NOTIFICATION = 2;

    public final static int STATUS_FINISH = 200;

    private final static String PARAM_TIME = "time";
    public final static String PARAM_TASK = "task";
    public final static String PARAM_RESULT = "result";
    public final static String PARAM_STATUS = "status";
    public final static String BROADCAST_ACTION = "ru.lizzzi.crossfit_rekord.activity";

    private static final String APPLICATION_IDB = "215CF2B1-C44E-E365-FFB6-9C35DD6A9300";
    private static final String API_KEYB = "8764616E-C5FE-CE43-FF54-17B4A8026F00";

    private NotificationDBHelper mDBHelper;

    private TextView tvNotificationCounter;
    private NavigationView navigationView;
    private int iOpenFragment = 1;
    private int iSelectFragment = 0;

    private static final String APP_PREFERENCES = "audata";
    private static final String APP_PREFERENCES_SELECTEDDAY = "SelectedDay";

    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);

        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close){

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
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
        View headerview = navigationView.getHeaderView(0);

        LinearLayout llHeader = headerview.findViewById(R.id.llheader);
        llHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenFragment(StartScreenFragment.class);
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
        registerReceiver(br, intFilt);


    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        Class fragmentClass = null;

        int id = item.getItemId();

        if (id == R.id.shedule) {
            iSelectFragment = R.string.title_Table_Fragment;
            fragmentClass = TableFragment.class;

        } else if (id == R.id.record_training) {
            iSelectFragment = R.string.title_RecordForTraining_Fragment;
            if (checkAuthData.checkAuthData(getContext())){
                fragmentClass = RecordForTrainingSelectFragment.class;
            }else {
                fragmentClass = LoginFragment.class;
            }

        } else if (id == R.id.definition) {
            iSelectFragment = R.string.title_Character_Fragment;
            fragmentClass = CharacterFragment.class;

        } else if (id == R.id.contacts) {
            iSelectFragment = R.string.title_Contacts_Fragment;
            fragmentClass = ContactsFragment.class;

        } else if (id == R.id.profile) {
            iSelectFragment = R.string.title_AboutMe_Fragment;
            if (checkAuthData.checkAuthData(getContext())){
                fragmentClass = AboutMeFragment.class;
            }else {
                fragmentClass = LoginFragment.class;
            }

        } else if (id == R.id.calendar_wod){
            iSelectFragment = R.string.title_CalendarWod_Fragment;
            fragmentClass = CalendarWodFragment.class;

        }else if (id == R.id.notification){
            fragmentClass = NotificationFragment.class;
            iSelectFragment = R.string.title_Notification_Fragment;

        }else if (id == R.id.myResults){
            fragmentClass = MyResultsFragment.class;
            iSelectFragment = R.string.title_MyResults_Fragment;
        }

        if(iSelectFragment != iOpenFragment){
            OpenFragment(fragmentClass);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    private Context getContext() {
        return this;
    }

    private void OpenFragment(Class fragmentClass){

        Fragment fragment = null;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        for(int i = 0; i < (fragmentManager.getBackStackEntryCount()-1); i++) {
            fragmentManager.popBackStack();
        }
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right);
        ft.replace(R.id.container, fragment);
        ft.addToBackStack(null);

        ft.commit();

    }

    @Override
    public void onBackPressed(){

        int count = getSupportFragmentManager().getBackStackEntryCount();
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }
        else{
            if (count == 1) {
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
    protected void  onStart() {
        super.onStart();

        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.container);
        if (checkAuthData.checkAuthData(getContext())){
            if (!isMyServiceRunning(LoadNotificationsService.class)){
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

        }else {
            if (fragment == null) {
                changeToggleStatus(false);
                OpenFragment(LoginFragment.class);
            }

        }

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause(){
        super.onPause();
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
                boolean bCheckTable =  mDBHelper.checkTable();
                if(bCheckTable){
                    int i = mDBHelper.countNotification();
                    String stCounter;
                    if (i > 0 ){
                        stCounter = String.valueOf(i);
                    }else {
                        stCounter = "";
                    }
                    tvNotificationCounter.setGravity(Gravity.CENTER_VERTICAL);
                    tvNotificationCounter.setTypeface(null, Typeface.BOLD);
                    tvNotificationCounter.setTextColor(getResources().getColor(R.color.colorAccent));
                    tvNotificationCounter.setText(stCounter);
                }
            }
        }).run();

    }

    //первый аргумент нужен для смены заголовка, второй для выделения элемента в шторке
    @Override
    public void changeTitle(int intNameFragmentTitle, int intNameFragmentSelectNavDraw) {
        setTitle(intNameFragmentTitle);
        if (iSelectFragment == 0) iSelectFragment=intNameFragmentTitle;
        iOpenFragment = intNameFragmentTitle;

        if (intNameFragmentSelectNavDraw == R.string.title_Notification_Fragment){
            navigationView.getMenu().getItem(0).setChecked(true);
        }else {
            navigationView.getMenu().getItem(0).setChecked(false);
        }

        if (intNameFragmentSelectNavDraw == R.string.title_Table_Fragment){
            navigationView.getMenu().getItem(1).setChecked(true);
        }else {
            navigationView.getMenu().getItem(1).setChecked(false);
        }

        if (intNameFragmentSelectNavDraw == R.string.title_RecordForTraining_Fragment){
            navigationView.getMenu().getItem(2).setChecked(true);
        }else {
            navigationView.getMenu().getItem(2).setChecked(false);
        }

        if (intNameFragmentSelectNavDraw == R.string.title_CalendarWod_Fragment){
            navigationView.getMenu().getItem(3).setChecked(true);
        }else {
            navigationView.getMenu().getItem(3).setChecked(false);
        }

        if (intNameFragmentSelectNavDraw == R.string.title_MyResults_Fragment){
            navigationView.getMenu().getItem(4).setChecked(true);
        }else {
            navigationView.getMenu().getItem(4).setChecked(false);
        }

        if (intNameFragmentSelectNavDraw == R.string.title_Character_Fragment){
            navigationView.getMenu().getItem(5).setChecked(true);
        }else {
            navigationView.getMenu().getItem(5).setChecked(false);
        }

        if (intNameFragmentSelectNavDraw == R.string.title_AboutMe_Fragment){
            navigationView.getMenu().getItem(6).setChecked(true);
        }else {
            navigationView.getMenu().getItem(6).setChecked(false);
        }

        if (intNameFragmentSelectNavDraw == R.string.title_Contacts_Fragment){
            navigationView.getMenu().getItem(7).setChecked(true);
        }else {
            navigationView.getMenu().getItem(7).setChecked(false);
        }
    }

    @Override
    public void changeToggleStatus(boolean toggleVisible) {
        if (toggleVisible){
            drawer.addDrawerListener(toggle);
            toggle.syncState();
        }
    }
}