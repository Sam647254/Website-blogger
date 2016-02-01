package net.givreardent.sam.blogger;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import net.givreardent.sam.blogger.internal.HTTPResult;
import net.givreardent.sam.blogger.internal.Status;
import net.givreardent.sam.blogger.network.BlogAccessor;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class StatusEditFragment extends DialogFragment {
    private Status status;
    private TextView dateView;
    private EditText enText, frText;
    private AlertDialog dialog;
    private Button OKButton;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View v = getActivity().getLayoutInflater()
                .inflate(R.layout.fragment_status_edit, null, false);
        builder.setView(v);
        enText = (EditText) v.findViewById(R.id.status_English_TextView);
        frText = (EditText) v.findViewById(R.id.status_French_TextView);
        dateView = (TextView) v.findViewById(R.id.status_time_TextView);
        status = (Status) getArguments().getSerializable("status");
        DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);
        if (status != null) {
            enText.setText(status.content_en);
            frText.setText(status.content_fr);
            dateView.setText(df.format(status.updated));
        } else {
            dateView.setText(df.format(Calendar.getInstance().getTime()));
        }
        builder.setPositiveButton(android.R.string.ok, null);
        builder.setNeutralButton(android.R.string.cancel, null);
        dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface d) {
                OKButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                OKButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        v.setEnabled(false);
                        submit();
                    }
                });
            }
        });
        return dialog;
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_create, menu);
    }

    public static StatusEditFragment edit(Status status) {
        StatusEditFragment fragment = new StatusEditFragment();
        Bundle arguments = new Bundle();
        arguments.putSerializable("status", status);
        fragment.setArguments(arguments);
        return fragment;
    }

    private void submit() {
        StatusesFragment fragment = (StatusesFragment) getTargetFragment();
        if (status == null) {
            Status newStatus = new Status();
            newStatus.ID = -1;
            newStatus.content_en = enText.getText().toString();
            newStatus.content_fr = frText.getText().toString();
            newStatus.updated = null;
            new SubmitNewStatusTask().execute(newStatus);
        } else {
            status.content_en = enText.getText().toString();
            status.content_fr = frText.getText().toString();
            new UpdateStatusTask().execute(status);
        }
    }

    private class SubmitNewStatusTask extends AsyncTask<Status, Void, HTTPResult> {

        @Override
        protected HTTPResult doInBackground(net.givreardent.sam.blogger.internal.Status... params) {
            return BlogAccessor.postStatus(params[0]);
        }

        @Override
        protected void onPostExecute(HTTPResult httpResult) {
            if (httpResult == HTTPResult.success) {
                dialog.dismiss();
                Toast.makeText(getActivity(), "Status submitted!", Toast.LENGTH_SHORT).show();
                getTargetFragment().onActivityResult(getTargetRequestCode(), 0, null);
            } else {
                OKButton.setEnabled(true);
                Toast.makeText(getActivity(), "Could not submit status!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class UpdateStatusTask extends AsyncTask<Status, Void, HTTPResult> {

        @Override
        protected HTTPResult doInBackground(net.givreardent.sam.blogger.internal.Status... params) {
            return BlogAccessor.updateStatus(params[0]);
        }

        @Override
        protected void onPostExecute(HTTPResult httpResult) {
            if (httpResult == HTTPResult.success) {
                dialog.dismiss();
                Toast.makeText(getActivity(), "Status updated!", Toast.LENGTH_SHORT).show();
                getTargetFragment().onActivityResult(getTargetRequestCode(), 0, null);
            } else {
                OKButton.setEnabled(true);
                Toast.makeText(getActivity(), "Could not update status!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
