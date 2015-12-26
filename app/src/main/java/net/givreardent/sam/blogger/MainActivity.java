package net.givreardent.sam.blogger;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by sam on 23/12/15.
 */
public class MainActivity extends SingleFragmentActivity {
    @Override
    Fragment getFragment() {
        return new MainFragment();
    }
}
