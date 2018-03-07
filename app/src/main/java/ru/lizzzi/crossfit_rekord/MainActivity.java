package ru.lizzzi.crossfit_rekord;

import android.content.Context;
import android.content.SharedPreferences;
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
import android.view.MenuItem;

import com.backendless.Backendless;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static final String APP_PREFERENCES = "audata";
    public static final String APP_PREFERENCES_OBJECTID = "ObjectId";
    SharedPreferences mSettings;

    public static final String APPLICATION_IDB = "215CF2B1-C44E-E365-FFB6-9C35DD6A9300";
    public static final String API_KEYB = "8764616E-C5FE-CE43-FF54-17B4A8026F00";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Backendless.initApp(this, APPLICATION_IDB, API_KEYB);


        Fragment fragment = null;
        Class fragmentClass;
        fragmentClass = StartScreen_Fragment.class;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.container, fragment);
        ft.commit();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {


        mSettings = this.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        Fragment fragment = null;
        Class fragmentClass = null;

        int id = item.getItemId();

        if (id == R.id.shedule) {
            fragmentClass = Table_Fragment.class;

        }else if (id == R.id.record_training) {

            boolean containtobjectid = mSettings.contains(APP_PREFERENCES_OBJECTID);
            if (containtobjectid) {

                fragmentClass = RecordForTraining_Fragment.class;

            }else {
                fragmentClass = Login_Fragment.class;
            }
        }else if (id == R.id.result){
            fragmentClass = Result_Fragment.class;

        }else if (id == R.id.definition){
            fragmentClass = Character_Fragment.class;

        }else if (id == R.id.contacts){


        }else if (id == R.id.profile){
            //fragmentClass = AboutMe_Fragment.class;
            fragmentClass = AboutMe_Fragment.class;
        }

        try {
            fragment = (Fragment) (fragmentClass != null ? fragmentClass.newInstance() : null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.container, fragment);
        ft.addToBackStack(null);
        ft.commit();

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);


        return true;
    }

    public Context getContext() {
        return this;
    }
}
