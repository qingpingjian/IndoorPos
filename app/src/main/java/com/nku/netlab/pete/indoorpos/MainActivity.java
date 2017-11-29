package com.nku.netlab.pete.indoorpos;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.nku.netlab.pete.indoorpos.listener.WifiReceiver;

import java.util.concurrent.atomic.AtomicBoolean;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String LOG_TAG = "indoorpos";

    private static MainActivity mainActivity;

    public static class State {
        WifiReceiver wifiReceiver;
        final Fragment[] fragList = new Fragment[2];
        int currentFragIndex;
        AtomicBoolean finishing;
    }
    private State state;
    public static final int WIFI_TAP_POS = 0;
    public static final int PDR_TAP_POS = 1;

    public static final int WIFI_DEFAULT_SCAN_TYPE = 0;
    public static final int WIFI_DEFAULT_SCAN_NUM = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainActivity = this;

        setupMenuDrawer();

//        TODO: remove floating action button
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
        state = new State();
        state.finishing = new AtomicBoolean(false);
        if (savedInstanceState == null) {
            setupFragments();
        }
        state.currentFragIndex = -1; // I want to add fragment in selectFragment method
        // show the wifi training fragment by default
        selectFragment(WIFI_TAP_POS);
    }

    private void setupMenuDrawer() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        //drawer.setDrawerListener(toggle);
        drawer.addDrawerListener(toggle);

        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }
    private void setupFragments() {
        info("Creating WifiFragment");
        state.fragList[WIFI_TAP_POS] = WifiFragment.newInstance(WIFI_DEFAULT_SCAN_TYPE, WIFI_DEFAULT_SCAN_NUM);

        info("Creating PdrFragment");
        state.fragList[PDR_TAP_POS] = PdrFragment.newInstance();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_exit) {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
            finish();
            return true;
        }
        int targetFragIndex = 0;
        if (id == R.id.nav_camara) {
            // Handle the camera action
            targetFragIndex = 0;
        } else if (id == R.id.nav_gallery) {
            targetFragIndex = 0;
        } else if (id == R.id.nav_slideshow) {
            targetFragIndex = 0;
        } else if (id == R.id.nav_manage) {
            targetFragIndex = 0;
        } else if (id == R.id.nav_wifiscan) {
            targetFragIndex = 0;
        } else if (id == R.id.nav_pdr) {
            targetFragIndex = 1;
        }
        selectFragment(targetFragIndex);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    private void selectFragment(int fragIndex) {
        if (fragIndex == state.currentFragIndex)
            return;

        Fragment frag = state.fragList[fragIndex];
        FragmentManager manager = getSupportFragmentManager();
        try {
            manager.beginTransaction().replace(R.id.position_fragment, frag).commit();
        }
        catch (final NullPointerException | IllegalStateException ex) {
            final String message = "exception in fragment switch: " + ex.getMessage();
            error(message, ex);
        }
        state.currentFragIndex = fragIndex;

        final String[] fragmentTitles = new String[]{
                getString(R.string.nav_wifi_title),
                getString(R.string.nav_pdr_title),
        };
        setTitle(fragmentTitles[fragIndex]);
    }

    @Override
    public boolean isFinishing() {
        return state.finishing.get();
    }

    @Override
    public void finish() {
        // TODO: add some clear operation before finish
        final boolean wasFinishing = state.finishing.getAndSet(true);
        if (wasFinishing) {
            info("MAIN: finish called twice!");
        }
        // Give some time to say bye
        sleep(50);
        super.finish();
    }

    public static void sleep(final long sleepInMsc) {
        try {
            Thread.sleep(sleepInMsc);
        } catch (final InterruptedException ex) {
            // No worries
        }
    }

    public static void info(final String value) {
        Log.i(LOG_TAG, String.format("[%s] %s", Thread.currentThread().getName(), value));
    }

    public static void warn(final String value, final Throwable t) {
        Log.w(LOG_TAG, String.format("[%s] %s", Thread.currentThread().getName(), value), t);
    }

    public static void error(final String value, final Throwable t) {
        Log.e(LOG_TAG, String.format("[%s] %s", Thread.currentThread().getName(), value), t);
    }
}
