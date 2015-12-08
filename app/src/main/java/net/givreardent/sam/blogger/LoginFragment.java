package net.givreardent.sam.blogger;

import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import net.givreardent.sam.blogger.internal.HTTPResult;
import net.givreardent.sam.blogger.internal.Parameters;
import net.givreardent.sam.blogger.network.BlogAccessor;

/**
 * A placeholder fragment containing a simple view.
 */
public class LoginFragment extends Fragment {
    private EditText usernameField, passwordField;
    private Button loginButton;
    private HTTPResult resultHolder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        usernameField = (EditText) view.findViewById(R.id.login_usernameField);
        passwordField = (EditText) view.findViewById(R.id.login_passwordField);
        loginButton = (Button) view.findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show log in progress
                loginButton.setText("Logging in...");
                loginButton.setEnabled(false);
                String username = usernameField.getText().toString();
                String password = passwordField.getText().toString();
                login(username, password);
            }
        });
        return view;
    }

    private void login(String username, String password) {
        new LoginTask().execute(username, password);
    }

    private void proceedToHomeScreen() {

    }

    private class LoginTask extends AsyncTask<String, Void, HTTPResult> {

        @Override
        protected HTTPResult doInBackground(String... params) {
            return BlogAccessor.login(params[0], params[1]);
        }

        @Override
        protected void onPostExecute(HTTPResult result) {
            loginButton.setText(R.string.lit_log_in);
            loginButton.setEnabled(true);
            resultHolder = result;
            if (result == HTTPResult.success) {
                // Login
                // For testing purposes
                Snackbar.make(loginButton, "Login success!", Snackbar.LENGTH_LONG).show();
                Log.i("LoginFragment", Parameters.APIKey);
                Log.i("LoginFragment", Parameters.ID);
            } else if (result == HTTPResult.unauthorized){
                Snackbar.make(loginButton, "Incorrect credentials!", Snackbar.LENGTH_LONG).show();
            } else if (result == HTTPResult.other) {
                Snackbar.make(loginButton, "Unknown error!", Snackbar.LENGTH_LONG).show();
            } else if (result == HTTPResult.connection_failure) {
                Snackbar.make(loginButton, "Connection error!", Snackbar.LENGTH_LONG).show();
            }
        }
    }
}
