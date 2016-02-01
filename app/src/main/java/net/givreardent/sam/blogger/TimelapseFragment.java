package net.givreardent.sam.blogger;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * Created by sam on 23/12/15.
 */
public class TimelapseFragment extends MasterDetailFragment {

    @Override
    protected Fragment getDetailFragment() {
        return null;
    }

    @Override
    protected ListFragment getListFragment() {
        return null;
    }
}
