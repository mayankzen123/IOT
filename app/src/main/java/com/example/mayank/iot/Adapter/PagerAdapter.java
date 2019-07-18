package com.example.mayank.iot.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.mayank.iot.Fragment.Dht11;
import com.example.mayank.iot.Fragment.SocketFragment;
import com.example.mayank.iot.Fragment.TorsionFragment;

/**
 * Created by Mayank on 3/16/2017.
 */
public class PagerAdapter extends FragmentPagerAdapter {

    public PagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int index) {

        switch (index) {
            case 0:
                return new SocketFragment();
            case 1:
                return new TorsionFragment();
            case 2:
                return new Dht11();
        }

        return null;
    }

    @Override
    public int getCount() {
        // get item count - equal to number of tabs
        return 3;
    }
}
