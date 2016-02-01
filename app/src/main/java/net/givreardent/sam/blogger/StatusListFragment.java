package net.givreardent.sam.blogger;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import net.givreardent.sam.blogger.internal.Parameters;
import net.givreardent.sam.blogger.internal.Status;
import net.givreardent.sam.blogger.network.BlogAccessor;

import java.text.DateFormat;
import java.util.ArrayList;

/**
 * Created by sam on 01/01/16.
 */
public class StatusListFragment extends ListFragment {
    private ArrayList<Status> statuses;
    private OnStatusSelectedListener callback;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        statuses = new ArrayList<>();
        callback = (OnStatusSelectedListener) getParentFragment();
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Status status = statuses.get(position);
        callback.onStatusSelected(status);
    }

    void refresh() {
        new FetchStatusesTask().execute();
    }

    private class FetchStatusesTask extends AsyncTask<Void, Void, ArrayList<Status>> {

        @Override
        protected ArrayList<net.givreardent.sam.blogger.internal.Status> doInBackground(Void... params) {
            return BlogAccessor.getStatuses(Parameters.ID);
        }

        @Override
        protected void onPostExecute(ArrayList<net.givreardent.sam.blogger.internal.Status> statuses) {
            if (statuses != null) {
                Log.i("StatusesFragment", "Fetched " + statuses.size() + " status(es).");
                StatusListFragment.this.statuses = statuses;
                if (getListAdapter() == null)
                    setListAdapter(new StatusesAdapter(statuses));
                else
                    ((StatusesAdapter) getListAdapter()).notifyDataSetChanged();
            } else {
                Log.e("StatusesFragment", "Could not fetch statuses.");
                if (getListAdapter() == null)
                    setListAdapter(new StatusesAdapter(StatusListFragment.this.statuses));
            }
        }
    }

    private class StatusesAdapter extends ArrayAdapter<Status> {
        public StatusesAdapter(ArrayList<Status> statuses) {
            super(getActivity(), 0, statuses);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater()
                        .inflate(R.layout.list_status, parent, false);
                ViewHolder holder = new ViewHolder();
                holder.date = (TextView) convertView.findViewById(R.id.list_status_date);
                holder.content = (TextView) convertView.findViewById(R.id.list_status_content);
                convertView.setTag(holder);
            }
            ViewHolder holder = (ViewHolder) convertView.getTag();
            Status status = getItem(position);
            DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);
            holder.content.setText(status.content_en);
            holder.date.setText(df.format(status.updated));
            return convertView;
        }

        private class ViewHolder {
            TextView date, content;
        }
    }

    public interface OnStatusSelectedListener {
        void onStatusSelected(Status status);
    }
}
