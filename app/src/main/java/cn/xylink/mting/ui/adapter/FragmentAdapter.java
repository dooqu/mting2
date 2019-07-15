package cn.xylink.mting.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.view.ViewGroup;

import java.util.ArrayList;


public class FragmentAdapter extends FragmentPagerAdapter {
	private ArrayList<Fragment> fragments;
	private FragmentManager fm;
	private String [] titles;

	public FragmentAdapter(FragmentManager fm) {
		super(fm);
		this.fm = fm;

	}

	public FragmentAdapter(FragmentManager fm, ArrayList<Fragment> fragments,String [] title) {
		super(fm);
		this.fm = fm;
		this.fragments = fragments;
		this.titles = title;
	}

	@Override
	public int getCount() {
		return fragments.size();
	}

	@Override
	public Fragment getItem(int position) {
		return fragments.get(position);
	}

	@Override
	public int getItemPosition(Object object) {
		return POSITION_NONE;
	}

	public void setFragments(ArrayList<Fragment> fragments) {
		if (this.fragments != null) {
			FragmentTransaction ft = fm.beginTransaction();
			for (Fragment f : this.fragments) {
				ft.remove(f);
			}

			ft.commit();
			ft = null;
			fm.executePendingTransactions();
		}
		this.fragments = fragments;
		notifyDataSetChanged();
	}

	@Override
	public Object instantiateItem(ViewGroup container, final int position) {
		Fragment fragment = fragments.get(position);
		if (!fragment.isAdded()) { // 如果fragment还没有added
			FragmentTransaction ft = fm.beginTransaction();
			ft.add(fragment, fragment.getClass().getSimpleName());
			ft.commit();
			/**
			 * 在用FragmentTransaction.commit()方法提交FragmentTransaction对象后
			 * 会在进程的主线程中，用异步的方式来执行。 如果想要立即执行这个等待中的操作，就要调用这个方法（只能在主线程中调用）。
			 * 要注意的是，所有的回调和相关的行为都会在这个调用中被执行完成，因此要仔细确认这个方法的调用位置。
			 */
			fm.executePendingTransactions();
		}
		if (fragment.getView() != null && fragment.getView().getParent() == null) {
			container.addView(fragment.getView()); // 为viewpager增加布局
		}

		return fragment;
	}


	@Override
	public CharSequence getPageTitle(int position) {
		return titles[position];
	}
}
