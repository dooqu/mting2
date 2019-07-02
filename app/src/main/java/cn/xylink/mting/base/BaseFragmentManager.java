package cn.xylink.mting.base;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by xylink-dev01 on 2018/6/26.
 */

public class BaseFragmentManager {
    private static Map<Integer, BaseFragment> list = new HashMap<Integer, BaseFragment>();
    public final static int FRAGMENT_READINESS_READ = 1;

    public static BaseFragment getBaseFragment(int id) {
        BaseFragment mBaseFragment = null;
        list.get(id);
        if (mBaseFragment != null) {
            return mBaseFragment;
        }
        switch (id) {
            case FRAGMENT_READINESS_READ:
//                mBaseFragment = new ReadinessReadFragment();
                break;
        }
        list.put(id, mBaseFragment);
        return mBaseFragment;

    }

    public static void clear() {
        list = new HashMap<Integer, BaseFragment>();
    }

    public static boolean clear(int id) {
        BaseFragment f = list.remove(id);
        return f != null;
    }

}
