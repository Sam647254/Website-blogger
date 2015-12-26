package net.givreardent.sam.blogger.network;

import android.net.Uri;
import android.util.Log;

import net.givreardent.sam.blogger.internal.HTTPResult;
import net.givreardent.sam.blogger.internal.Parameters;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Sam on 2015-12-07.
 */
public class BlogAccessor {
    public static String rootURL = "http://10.0.2.2:3000";
    private static final String interfacePath = "interface";
    private static final String securityPath = "security";
    private static final String tag = "BlogAccessor";
    private static final String APIKeyLabel = "key";
    private static final String IDLabel = "id";
    private static final String EmailLabel = "email";
    private static final String passwordLabel = "password";

    public static HTTPResult login(String username, String password) {
        JSONObject login = new JSONObject();
        String urlString = Uri.parse(rootURL).buildUpon().appendPath(securityPath).appendPath("log in").build().toString();
        HttpURLConnection connection = null;
        try {
            login.put(EmailLabel, username);
            login.put(passwordLabel, password);
            String request = login.toString();
            Log.i("BlogAccessor", request);
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(10000);
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setFixedLengthStreamingMode(request.getBytes().length);
            OutputStream out = connection.getOutputStream();
            out.write(request.getBytes());
            int result = connection.getResponseCode();
            if (result == HttpURLConnection.HTTP_OK) {
                InputStream in = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                StringBuilder builder = new StringBuilder();
                String str;
                while ((str = reader.readLine()) != null) {
                    builder.append(str);
                }
                JSONObject response = (JSONObject) new JSONTokener(builder.toString()).nextValue();
                Parameters.APIKey = response.getString(APIKeyLabel);
                Parameters.ID = response.getString(IDLabel);
                return HTTPResult.success;
            } else if (result == HttpURLConnection.HTTP_UNAUTHORIZED) {
                return HTTPResult.unauthorized;
            } else {
                Log.d("BlogAccessor", "Status code: " + result);
                return HTTPResult.other;
            }
        } catch (java.io.IOException e) {
            Log.e("BlogAccessor", "Error", e);
            return HTTPResult.connection_failure;
        } catch (JSONException e) {
            Log.e("BlogAccessor", "Error", e);
            return HTTPResult.other;
        } finally {
            connection.disconnect();
        }
    }
}
