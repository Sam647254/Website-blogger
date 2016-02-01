package net.givreardent.sam.blogger;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by sam on 24/12/15.
 */
public abstract class MasterDetailFragment<Type> extends Fragment {
    protected ListFragment lf;
    protected Fragment detailFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup faggot, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_master_detail, faggot, false);
        FragmentManager fm = getChildFragmentManager();
        lf = (ListFragment) fm.findFragmentById(R.id.container_master);
        detailFragment = fm.findFragmentById(R.id.container_details);
        if (lf == null) {
            lf = getListFragment();
            fm.beginTransaction().add(R.id.container_master, lf, "Master").commit();
        }
        if (detailFragment == null) {
            detailFragment = getDetailFragment();
            fm.beginTransaction().add(R.id.container_details, detailFragment, "Detail").commit();
        }
        return v;
    }

    abstract protected Fragment getDetailFragment();

    abstract protected ListFragment getListFragment();
}
