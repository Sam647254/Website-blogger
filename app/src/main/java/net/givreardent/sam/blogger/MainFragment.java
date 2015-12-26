package net.givreardent.sam.blogger;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by sam on 23/12/15.
 */
public class MainFragment extends Fragment {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private FragmentPagerAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main, container, false);
        viewPager = (ViewPager) v.findViewById(R.id.main_ViewPager);
        tabLayout = (TabLayout) v.findViewById(R.id.main_TabLayout);
        adapter = new FragmentPagerAdapter(getActivity().getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    case 0:
                        return new StatusesFragment();
                    case 1:
                        return new PostsFragment();
                    case 2:
                        return new TimelapseFragment();
                    case 3:
                        return new UsersFragment();
                    default:
                        return null;
                }
            }

            @Override
            public int getCount() {
                return 4;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                switch (position) {
                    case 0:
                        return getResources().getString(R.string.lit_statuses);
                    case 1:
                        return getResources().getString(R.string.lit_posts);
                    case 2:
                        return getResources().getString(R.string.lit_users);
                    case 3:
                        return getResources().getString(R.string.lit_timelapse);
                    default:
                        return "";
                }
            }
        };
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        return v;
    }
}
