package in.innobins.customer;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by sasuke on 11/9/15.
 */

public class SlidingPagerAdapter extends FragmentStatePagerAdapter {
    final int PAGE_COUNT = 3;
    private String tabTitles[] = new String[] { " UpComing ", " Running ", " Completed " };
    private Context context;

    public SlidingPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        Class fragmentClass = null;
//        Log.d("position", String.valueOf(position));
        switch (position) {
            case 0:
                fragmentClass = UpcomingOrder.class;
                break;
            case 1:
                fragmentClass = RunningOrder.class;
                break;
            case 2:
                fragmentClass = CompletedOrder.class;
                break;
            default:
                fragmentClass = UpcomingOrder.class;
                break;
        }
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fragment;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position

        return tabTitles[position];
    }

}
