package net.givreardent.sam.blogger;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

/**
 * Created by sam on 24/12/15.
 */
public abstract class MasterDetailFragment extends Fragment {
    private ListFragment lf;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup faggot, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_master_detail, faggot, false);
        FragmentManager fm = getChildFragmentManager();
        lf = new ListFragment();
        setListAdapter(getListView());
        fm.beginTransaction().add(R.id.container_master, lf, "Master").commit();
        fm.beginTransaction().add(R.id.container_details, getDetailFragment(), "Detail").commit();
        return v;
    }

    protected ListView getListView() {
        return lf.getListView();
    }

    abstract protected Fragment getDetailFragment();

    abstract protected void setListAdapter(ListView view);
}
