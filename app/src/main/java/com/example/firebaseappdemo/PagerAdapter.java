package com.example.firebaseappdemo;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class PagerAdapter extends FragmentStatePagerAdapter {

    int numberoftabs;


    public PagerAdapter( FragmentManager fm ,int numberoftabs) {
        super(fm);
        this.numberoftabs= numberoftabs;
    }


    @Override
    public Fragment getItem(int position) {

        switch (position)
        {
            case 0:
                Requestfragment requestfragment = new Requestfragment();
                return requestfragment;

            case 1:
                Chatfrgment chatfrgment = new Chatfrgment();
                return chatfrgment;
            case 2:
                Friendsfragment friendsfragment = new Friendsfragment();
                return  friendsfragment;
            default:
                return null;
        }

    }

    @Override
    public int getCount() {
        return numberoftabs;
    }
}
