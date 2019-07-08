package cn.xylink.mting.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

import cn.xylink.mting.ui.activity.MainActivity;
import cn.xylink.mting.ui.fragment.CollectFragment;
import cn.xylink.mting.ui.fragment.ReadedFragment;
import cn.xylink.mting.ui.fragment.UnreadFragment;


public class MainFragmentAdapter extends FragmentPagerAdapter {

    List<Fragment> fragments = new ArrayList<>();

    public MainFragmentAdapter(FragmentManager fm) {
        super(fm);
        fragments.add(new UnreadFragment());
        fragments.add(new ReadedFragment());
        fragments.add(new CollectFragment());
    }

    @Override
    public Fragment getItem(int i) {
        return fragments!=null?fragments.get(i):null;
    }

    @Override
    public int getCount() {
        return MainActivity.TAB_ENUM.values().length;
    }
}
