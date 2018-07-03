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
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.backendless.Backendless;

import ru.lizzzi.crossfit_rekord.draft.wod_result_fragment.Result_Fragment;
import ru.lizzzi.crossfit_rekord.fragments.AboutMeFragment;
import ru.lizzzi.crossfit_rekord.fragments.CalendarWodFragment;
import ru.lizzzi.crossfit_rekord.fragments.CharacterFragment;
import ru.lizzzi.crossfit_rekord.fragments.CheckAuthData;
import ru.lizzzi.crossfit_rekord.fragments.LoginFragment;
import ru.lizzzi.crossfit_rekord.fragments.RecordForTrainingSelectFragment;
import ru.lizzzi.crossfit_rekord.fragments.StartScreenFragment;
import ru.lizzzi.crossfit_rekord.fragments.TableFragment;
import ru.lizzzi.crossfit_rekord.loaders.AboutMeLoader;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private CheckAuthData checkAuthData = new CheckAuthData();

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

        Backendless.initApp(this, APPLICATION_IDB, API_KEYB);

        OpenFragment(StartScreenFragment.class);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        Class fragmentClass = null;
        Class nextFragmentClass = null;

        int id = item.getItemId();

        if (id == R.id.shedule) {
            fragmentClass = TableFragment.class;

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
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        if (fragmentClass != null && fragmentClass.equals(LoginFragment.class)){
            if (nextFragmentClass != null) {
                LoginFragment(nextFragmentClass);
            }
        }else {
            OpenFragment(fragmentClass);
        }

        return true;
    }

    public Context getContext() {
        return this;
    }

    public void OpenFragment(Class fragmentClass){

        Fragment fragment = null;

        try {
            fragment = (Fragment) (fragmentClass != null ? fragmentClass.newInstance() : null);
        } catch (Exception e) {
            e.printStackTrace();
        }


        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.replace(R.id.container, fragment);
        ft.addToBackStack(null);
        ft.commit();

    }

    private void LoginFragment(Class fragmentClass){
        LoginFragment yfc = new LoginFragment();
        Bundle bundle = new Bundle();

        if(fragmentClass.equals(RecordForTrainingSelectFragment.class)){
            bundle.putString("fragment", String.valueOf(R.string.strRecordFragment));
        }
        if(fragmentClass.equals(AboutMeFragment.class)){
            bundle.putString("fragment", String.valueOf(R.string.strAboutMeFragment));
        }

        yfc.setArguments(bundle);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.replace(R.id.container, yfc);
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
                finish();
            } else {
                getSupportFragmentManager().popBackStack();
            }
        }
    }
}
