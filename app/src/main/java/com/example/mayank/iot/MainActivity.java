package com.example.mayank.iot;


import android.Manifest;
import android.app.FragmentTransaction;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.mayank.iot.Adapter.PagerAdapter;
import com.example.mayank.iot.Fragment.ConnectionDialogFragment;
import com.example.mayank.iot.Fragment.Dht11;
import com.example.mayank.iot.Fragment.TorsionFragment;


public class MainActivity extends AppCompatActivity implements ActionBar.TabListener {

    private ViewPager viewPager;
    private PagerAdapter mAdapter;
    private ActionBar actionBar;
    int requestId = 0;
    static Menu menu;
    // Tab titles
    private String[] tabs = {"Socket Connection", "Torsion", "DHT11"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initilization
        viewPager = (ViewPager) findViewById(R.id.pager);
        actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        mAdapter = new PagerAdapter(getSupportFragmentManager());

        viewPager.setAdapter(mAdapter);
        actionBar.setHomeButtonEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Adding Tabs
        for (String tab_name : tabs) {
            actionBar.addTab(actionBar.newTab().setText(tab_name)
                    .setTabListener(this));
        }
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
                supportInvalidateOptionsMenu();
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
    }


    @Override
    public void onTabSelected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction ft) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction ft) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actionbar, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        int pageNum = viewPager.getCurrentItem();
        if (pageNum == 0) {
            menu.findItem(R.id.action_download).setVisible(false);
            menu.findItem(R.id.action_email).setVisible(false);
            menu.findItem(R.id.action_details).setVisible(true);
        } else {
            menu.findItem(R.id.action_details).setVisible(false);
            menu.findItem(R.id.action_download).setVisible(true);
            menu.findItem(R.id.action_email).setVisible(true);
        }
        return super.onPrepareOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        requestId = id;
        if (isStoragePermissionGranted())
            performAction(id);
        return super.onOptionsItemSelected(item);
    }

    public void performAction(int id) {
        if (id == R.id.action_download) {
            Fragment page = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.pager + ":" + viewPager.getCurrentItem());
            if (viewPager.getCurrentItem() == 1 && page != null) {
                Toast.makeText(this, "Saving File Please Wait...", Toast.LENGTH_LONG).show();
                ((TorsionFragment) page).action(this, "Download");
            }
            if (viewPager.getCurrentItem() == 2 && page != null) {
                Toast.makeText(this, "Saving File Please Wait...", Toast.LENGTH_LONG).show();
                ((Dht11) page).action(this, "Download");
            }
        } else if (id == R.id.action_email) {
            Fragment page = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.pager + ":" + viewPager.getCurrentItem());
            if (viewPager.getCurrentItem() == 1 && page != null) {
                ((TorsionFragment) page).action(this, "Email");
            }
            if (viewPager.getCurrentItem() == 2 && page != null) {
                ((Dht11) page).action(this, "Email");
            }
        } else if (id == R.id.action_details) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            ConnectionDialogFragment connectionDialogFragment = new ConnectionDialogFragment();
            connectionDialogFragment.show(fragmentManager,"SOCKET");
        }
    }

    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("Permission", "Permission is granted");
                return true;
            } else {
                Log.v("Permission", "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v("Permission", "Permission is granted");
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.v("Permission", "Permission: " + permissions[0] + "was " + grantResults[0]);
            performAction(requestId);
        }
    }
}
