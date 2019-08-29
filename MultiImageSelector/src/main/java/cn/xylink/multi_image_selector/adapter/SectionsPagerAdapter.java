package cn.xylink.multi_image_selector.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

import cn.xylink.multi_image_selector.PlaceholderFragment;
import cn.xylink.multi_image_selector.bean.Image;
import cn.xylink.multi_image_selector.view.CustomViewPager;

public class SectionsPagerAdapter extends FragmentPagerAdapter {

    public List<Image> data;
    public CustomViewPager viewPager;
    public SectionsPagerAdapter(FragmentManager fm, List<Image> data,CustomViewPager viewPager) {
        super(fm);
        this.data = data;
        this.viewPager  = viewPager;
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        return PlaceholderFragment.newInstance(position, data.get(position).name, data.get(position).path,viewPager);
    }

    @Override
    public int getCount() {
        // Show 3 total pages.
        return data.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return data.get(position).name;
    }
}


