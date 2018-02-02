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
import android.widget.Toast;

import ru.profit_group.scorocode_sdk.Callbacks.CallbackLoginUser;
import ru.profit_group.scorocode_sdk.Responses.user.ResponseLogin;
import ru.profit_group.scorocode_sdk.ScorocodeSdk;
import ru.profit_group.scorocode_sdk.scorocode_objects.User;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static final String APPLICATION_ID = "24accf90596a4630a107e14d03a6a3a7";
    public static final String CLIENT_KEY = "f539a69f0d5940a38e0ca0e83a394d00";
    public static final String FILE_KEY = "c785108f61304a2680a53e1a44ae15b2";
    private static final String MESSAGE_KEY = "e812ec1547b84b62bc9a5c145d442f77";
    private static final String SCRIPT_KEY = "6920f997815244f2bc77949974e4b215";

    public static final String APP_PREFERENCES = "audata";
    public static final String APP_PREFERENCES_USERNAME = "Username";
    public static final String APP_PREFERENCES_EMAIL = "Email";
    public static final String APP_PREFERENCES_PASSWORD = "Password";
    SharedPreferences mSettings;
    private int counter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ScorocodeSdk.initWith(APPLICATION_ID, CLIENT_KEY, null, FILE_KEY, MESSAGE_KEY, SCRIPT_KEY, null);


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

            boolean containtsusername = mSettings.contains(APP_PREFERENCES_USERNAME);
            boolean containtemail = mSettings.contains(APP_PREFERENCES_EMAIL);
            boolean containtpassword = mSettings.contains(APP_PREFERENCES_PASSWORD);
            if (containtsusername && containtpassword && containtemail) {

                User user = new User();
                user.login(mSettings.getString(APP_PREFERENCES_EMAIL, ""), mSettings.getString(APP_PREFERENCES_PASSWORD, ""), new CallbackLoginUser() {
                    @Override
                    public void onLoginSucceed(ResponseLogin responseLogin) {
                        //Toast.makeText(getContext(), "Авторизация успешно", Toast.LENGTH_SHORT).show();
                        counter = 1;

                    }

                    @Override
                    public void onLoginFailed(String errorCode, String errorMessage) {
                        counter = 2;

                    }
                });

            }else {
                counter = 3;

            }

            if (counter == 1){
                fragmentClass = RecordForTraining_Fragment.class;
            }else if (counter == 2){
                Toast.makeText(this, "Авторизация не прошла. Попробуйте снова.", Toast.LENGTH_SHORT).show();
            }else {
                fragmentClass = Login_Fragment.class;
            }
        }else if (id == R.id.result){
            fragmentClass = Result_Fragment.class;

        }else if (id == R.id.definition){
            fragmentClass = Character_Fragment.class;

        }/*else if (id == R.id.contacts){
            Toast.makeText(this, "В разработке", Toast.LENGTH_SHORT).show();

        }else if (id == R.id.profile){
            Toast.makeText(this, "В разработке", Toast.LENGTH_SHORT).show();
        }*/

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.container, fragment);
        ft.addToBackStack(null);
        ft.commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);


        return true;
    }
}
