package com.taskmanager.horkrux.Authentication;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class LoginAdapter extends FragmentPagerAdapter {

    private Context context;
    int totaltabs;

    public LoginAdapter(@NonNull FragmentManager fm, Context context, int totaltabs) {
        super(fm);
        this.context = context;
        this.totaltabs = totaltabs;
    }

    @Override
    public int getCount() {
        return totaltabs;
    }

    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                LoginTabfragment loginTabfragment = new LoginTabfragment();
                return loginTabfragment;

            case 1:
                sign_up_fragment signUpFragment = new sign_up_fragment();
                return signUpFragment;

            default:
                return null;
        }
    }

}


