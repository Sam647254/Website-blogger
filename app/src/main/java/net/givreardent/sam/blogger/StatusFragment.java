package net.givreardent.sam.blogger;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.givreardent.sam.blogger.internal.Status;

import java.text.DateFormat;

/**
 * Created by sam on 27/12/15.
 */
public class StatusFragment extends Fragment {
    private TextView dateView, content_enView, content_frView;
    private LinearLayout llayout;
    private TextView noStatusTextView;
    private Status status;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        status = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_status_view, parent, false);
        dateView = (TextView) v.findViewById(R.id.status_time_TextView);
        content_enView = (TextView) v.findViewById(R.id.status_English_TextView);
        content_frView = (TextView) v.findViewById(R.id.status_French_TextView);
        llayout = (LinearLayout) v.findViewById(R.id.status_LinearLayout);
        noStatusTextView = (TextView) v.findViewById(R.id.status_no_status_TextView);
        FloatingActionButton createButton = (FloatingActionButton) v.findViewById(R.id.status_new_FAB);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((StatusesFragment) getParentFragment()).createNewStatus();
            }
        });
        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_status, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_status_edit:
                if (status != null) {
                    StatusEditFragment dialog = StatusEditFragment.edit(status);
                    dialog.setTargetFragment(getParentFragment(), 0);
                    dialog.show(getParentFragment().getChildFragmentManager(), "Edit");
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void refresh() {
        setStatus(status);
    }

    public void setStatus(Status status) {
        this.status = status;
        noStatusTextView.setVisibility(View.GONE);
        llayout.setVisibility(View.VISIBLE);
        DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);
        dateView.setText(df.format(status.updated));
        content_enView.setText(status.content_en);
        content_frView.setText(status.content_fr);
    }
}
