package com.example.mayank.iot;

import android.content.pm.PackageManager;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.example.mayank.iot.Adapter.PagerAdapter;
import com.example.mayank.iot.Fragment.ConnectionDialogFragment;
import com.example.mayank.iot.Fragment.Dht11;
import com.example.mayank.iot.Fragment.SocketFragment;
import com.example.mayank.iot.Fragment.TorsionFragment;
import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity implements ActionBar.TabListener {
    public static Menu menu;
    /* access modifiers changed from: private */
    public ActionBar actionBar;
    String currentStatus = "disconnected";
    private PagerAdapter mAdapter;
    int requestId = 0;
    private String[] tabs = {"Socket Connection", "Torsion", "DHT11"};
    private ViewPager viewPager;

    public void setCurrentStatus(String currentStatus2) {
        this.currentStatus = currentStatus2;
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);
        this.viewPager = findViewById(R.id.pager);
        this.viewPager.setOffscreenPageLimit(2);
        this.actionBar = getSupportActionBar();
        this.actionBar.setDisplayShowHomeEnabled(false);
        this.actionBar.setDisplayShowTitleEnabled(false);
        this.mAdapter = new PagerAdapter(getSupportFragmentManager());
        this.viewPager.setAdapter(this.mAdapter);
        this.actionBar.setHomeButtonEnabled(false);
        this.actionBar.setNavigationMode(2);
        for (String tab_name : this.tabs) {
            this.actionBar.addTab(this.actionBar.newTab().setText(tab_name).setTabListener(this));
        }
        this.viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageSelected(int position) {
                MainActivity.this.actionBar.setSelectedNavigationItem(position);
                MainActivity.this.supportInvalidateOptionsMenu();
            }

            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            public void onPageScrollStateChanged(int arg0) {
            }
        });
    }

    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        this.viewPager.setCurrentItem(tab.getPosition());
    }

    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
    }

    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
    }

    public boolean onCreateOptionsMenu(Menu menu2) {
        menu = menu2;
        getMenuInflater().inflate(R.menu.actionbar, menu2);
        return true;
    }

    public boolean onPrepareOptionsMenu(Menu menu2) {
        if (this.viewPager.getCurrentItem() == 0) {
            if (this.currentStatus.equalsIgnoreCase("connecting") || this.currentStatus.equalsIgnoreCase("connected")) {
                menu2.findItem(R.id.action_disconnect).setVisible(true);
                menu2.findItem(R.id.action_connect).setVisible(false);
            } else {
                menu2.findItem(R.id.action_disconnect).setVisible(false);
                menu2.findItem(R.id.action_connect).setVisible(true);
            }
            menu2.findItem(R.id.action_download).setVisible(false);
            menu2.findItem(R.id.action_email).setVisible(false);
            menu2.findItem(R.id.action_details).setVisible(true);
        } else {
            menu2.findItem(R.id.action_details).setVisible(false);
            menu2.findItem(R.id.action_download).setVisible(true);
            menu2.findItem(R.id.action_email).setVisible(true);
        }
        return super.onPrepareOptionsMenu(menu2);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        this.requestId = id;
        if (isStoragePermissionGranted()) {
            performAction(id);
        }
        return super.onOptionsItemSelected(item);
    }

    public void performAction(int id) {
        if (id == R.id.action_download) {
            Fragment page = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.pager + ":" + viewPager.getCurrentItem());
            if (this.viewPager.getCurrentItem() == 1 && page != null) {
                Toast.makeText(this, "Saving File Please Wait...", Toast.LENGTH_LONG).show();
                ((TorsionFragment) page).action(this, "Download");
            }
            if (this.viewPager.getCurrentItem() == 2 && page != null) {
                Toast.makeText(this, "Saving File Please Wait...", Toast.LENGTH_LONG).show();
                ((Dht11) page).action(this, "Download");
            }
        } else if (id == R.id.action_email) {
            Fragment page2 = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.pager + ":" + viewPager.getCurrentItem());
            if (this.viewPager.getCurrentItem() == 1 && page2 != null) {
                ((TorsionFragment) page2).action(this, "Email");
            }
            if (this.viewPager.getCurrentItem() == 2 && page2 != null) {
                ((Dht11) page2).action(this, "Email");
            }
        } else if (id == R.id.action_details) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            Bundle bundle = new Bundle();
            bundle.putString("DialogType", "ServerDetails");
            ConnectionDialogFragment connectionDialogFragment = new ConnectionDialogFragment();
            connectionDialogFragment.setArguments(bundle);
            connectionDialogFragment.show(fragmentManager, "SOCKET");
        } else if (id == R.id.action_connect) {
            Fragment page3 = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.pager + ":" + viewPager.getCurrentItem());
            if (this.viewPager.getCurrentItem() == 0 && page3 != null) {
                ((SocketFragment) page3).connect();
            }
        } else if (id == R.id.action_disconnect) {
            Fragment page4 = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.pager + ":" + viewPager.getCurrentItem());
            if (this.viewPager.getCurrentItem() == 0 && page4 != null) {
                ((SocketFragment) page4).disconnect();
            }
        } else if (id == R.id.action_sound_on) {
            Fragment page5 = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.pager + ":" + viewPager.getCurrentItem());
            if (this.viewPager.getCurrentItem() == 0 && page5 != null) {
                ((SocketFragment) page5).soundCommand(true);
            }
        } else if (id == R.id.action_sound_off) {
            Fragment page6 = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.pager + ":" + viewPager.getCurrentItem());
            if (this.viewPager.getCurrentItem() == 0 && page6 != null) {
                ((SocketFragment) page6).soundCommand(false);
            }
        }
    }

    public boolean isStoragePermissionGranted() {
        if (VERSION.SDK_INT < 23) {
            Log.v("Permission", "Permission is granted");
            return true;
        } else if (checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") == PackageManager.PERMISSION_GRANTED) {
            Log.v("Permission", "Permission is granted");
            return true;
        } else {
            Log.v("Permission", "Permission is revoked");
            ActivityCompat.requestPermissions(this, new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 1);
            return false;
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == 0) {
            Log.v("Permission", "Permission: " + permissions[0] + "was " + grantResults[0]);
            performAction(this.requestId);
        }
    }

    public void pushData(String data) {
        Fragment page = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.pager + ":" + viewPager.getCurrentItem());
        if (this.viewPager.getCurrentItem() == 0 && page != null) {
            ((SocketFragment) page).pushData("PUSHDATA " + data);
        }
    }
}
