package net.givreardent.sam.blogger;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import net.givreardent.sam.blogger.dialogs.URLSettingDialog;
import net.givreardent.sam.blogger.internal.Parameters;
import net.givreardent.sam.blogger.network.BlogAccessor;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        checkURLSetting();
        FloatingActionButton settingsButton = (FloatingActionButton) findViewById(R.id.fab);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                URLSettingDialog dialog = new URLSettingDialog();
                dialog.show(getFragmentManager(), "URL setting");
            }
        });
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void checkURLSetting() {
        String url = Parameters.getPreferences(this).getString(Parameters.URLSetting, null);
        if (url != null) {
            BlogAccessor.rootURL = url;
        }
    }
}
