package net.givreardent.sam.blogger;

import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

import net.givreardent.sam.blogger.internal.HTTPResult;
import net.givreardent.sam.blogger.internal.Parameters;
import net.givreardent.sam.blogger.internal.Status;
import net.givreardent.sam.blogger.network.BlogAccessor;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by sam on 23/12/15.
 */
public class StatusesFragment extends MasterDetailFragment<Status>
        implements StatusListFragment.OnStatusSelectedListener {
    private StatusListFragment listView;
    private StatusFragment detailView;

    @Override
    protected Fragment getDetailFragment() {
        detailView = new StatusFragment();
        return detailView;
    }

    @Override
    protected ListFragment getListFragment() {
        listView = new StatusListFragment();
        return listView;
    }

    public void createNewStatus() {
        StatusEditFragment dialog = StatusEditFragment.edit(null);
        dialog.setTargetFragment(this, 0);
        dialog.show(getChildFragmentManager(), "StatusEdit");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        listView.refresh();
        detailView.refresh();
    }

    @Override
    public void onStatusSelected(Status status) {
        detailView.setStatus(status);
    }
}
