package com.example.student.evproject;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class ControlPagerAdapter extends FragmentPagerAdapter {

    ArrayList<Fragment> tabs;

    public ControlPagerAdapter(FragmentManager fm) {
        super(fm);

        tabs = new ArrayList<>();
        tabs.add(new CtrlFragment());
        tabs.add(new UserFragment());
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return "차량제어";
            case 1:
                return  "사용자정보";
        }
        return null;
    }

    @Override
    public Fragment getItem(int position) {
        return tabs.get(position);
    }

    @Override
    public int getCount() {
        return tabs.size();
    }
}
