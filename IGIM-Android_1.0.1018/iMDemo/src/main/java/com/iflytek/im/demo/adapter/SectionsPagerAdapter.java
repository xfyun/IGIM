package com.iflytek.im.demo.adapter;

/**
 * 主界面的ViewPager的适配器
 * Created by imxqd on 2016/8/30.
 */

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.iflytek.im.demo.ui.fragment.ConversationFragment;
import com.iflytek.im.demo.ui.fragment.FriendFragment;
import com.iflytek.im.demo.ui.fragment.GroupFragment;
import com.iflytek.im.demo.ui.fragment.SmallGroupFragment;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return ConversationFragment.newInstance();
            case 1:
                return FriendFragment.newInstance();
            case 2:
                return SmallGroupFragment.newInstance();
            case 3:
                return GroupFragment.newInstance();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 4;
    }
}
