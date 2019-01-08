package com.example.admin.miplus.adapter;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.admin.miplus.fragment.SecondFragment;
import com.example.admin.miplus.fragment.FirstFragment;
import com.example.admin.miplus.fragment.ThirdFragment;

public class TabsPagerFragmentAdapter extends FragmentPagerAdapter {

    private String[] tabs;

    public TabsPagerFragmentAdapter(FragmentManager fm) {
        super(fm);
        tabs = new String[]{ "Main", "Map", "Settings", };
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return  tabs[position];
    }

    @Override
    public Fragment getItem(int position){
        switch (position){
            case 0:
                return FirstFragment.getInstance();
            case 1:
                return SecondFragment.getInstance();
            case 2:
                return ThirdFragment.getInstance();
        }
        return  null;

    }

    @Override
    public int getCount(){
        return tabs.length;
    }
}
