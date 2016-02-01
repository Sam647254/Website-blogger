package net.givreardent.sam.blogger;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by sam on 23/12/15.
 */
public class MainActivity extends SingleFragmentActivity {
    MainFragment fragment;

    @Override
    Fragment getFragment() {
        if (fragment == null)
            fragment = new MainFragment();
        return fragment;
    }
}
