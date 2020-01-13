package in.innobins.customer;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * Created by sasuke on 7/9/15.
 */
public class MybookingFragment extends Fragment {
    ImageView Togglebutton;
    TextView Title;
    private Toolbar toolbar;
    public static String TAG = "MyBookingFragment";
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        final View Rootview = inflater.inflate(R.layout.mybooking, parent, false);
        Title = (TextView)Rootview.findViewById(R.id.title);
        Title.setText("my booking");
        Togglebutton = (ImageView)Rootview.findViewById(R.id.togglebutton);
        Togglebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.mDrawerLayout.openDrawer(MainActivity.mDrawerPane);
            }
        });
        ViewPager viewPager = (ViewPager) Rootview.findViewById(R.id.viewpager);
        FragmentManager fragmentManager = getChildFragmentManager();
        viewPager.setAdapter(new SlidingPagerAdapter(fragmentManager));
        TabLayout tabLayout = (TabLayout) Rootview.findViewById(R.id.sliding_tabs);

        tabLayout.setupWithViewPager(viewPager);
        return Rootview;
    }

    @Override
    public void onDestroy ()
    {
        super.onDestroy();
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MOOVO", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("running",false);
        editor.putBoolean("upcoming", false);
        editor.putBoolean("completed", false);
        editor.putBoolean("populatedup", false);
        editor.putBoolean("populatedrun",false);
        editor.putBoolean("populatedom", false);
        editor.apply();
    }
}
