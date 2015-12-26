package net.givreardent.sam.blogger.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import net.givreardent.sam.blogger.R;
import net.givreardent.sam.blogger.internal.Parameters;
import net.givreardent.sam.blogger.network.BlogAccessor;

/**
 * Created by Sam on 2015-12-16.
 */
public class URLSettingDialog extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.lit_base_url);
        View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_url_setting, null);
        final EditText urlEditText = (EditText) v.findViewById(R.id.url_setting_EditText);
        urlEditText.setText(BlogAccessor.rootURL);
        builder.setView(v);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setURL(urlEditText.getText().toString());
            }
        });
        return builder.create();
    }

    private void setURL(String url) {
        Parameters.getPreferences(getActivity()).edit().putString(Parameters.URLSetting, url).commit();
        BlogAccessor.rootURL = url;
    }
}
