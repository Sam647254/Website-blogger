package net.givreardent.sam.blogger.network;

import android.net.Uri;
import android.util.Log;

import net.givreardent.sam.blogger.internal.HTTPResult;
import net.givreardent.sam.blogger.internal.Parameters;
import net.givreardent.sam.blogger.internal.Post;
import net.givreardent.sam.blogger.internal.Status;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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
    private static final int timeout = 10000;
    private static HTTPResult lastResult = HTTPResult.success;

    /**
     * Logs in with the given username and password.
     * The API key is stored
     * Default URL: POST /security/log in
     * @param username The username to log in with
     * @param password The user's password
     * @return
     */
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
            connection.setConnectTimeout(timeout);
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

    /**
     * Fetches all status updates for the user with the given ID
     * URL: GET /interface/users/&lt;ID&gt;/statuses
     * @param ID the User's ID
     * @return The User's status updates
     */
    public static ArrayList<Status> getStatuses(String ID) {
        ArrayList<Status> statuses = new ArrayList<>();
        String urlString = Uri.parse(rootURL).buildUpon().appendPath(interfacePath)
                .appendPath("users").appendPath(ID).appendPath("statuses").build().toString();
        HttpURLConnection connection = null;
        try {
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(timeout);
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
                JSONArray array = response.getJSONArray("statuses");
                for (int i = 0; i < array.length(); ++i) {
                    Status status = new Status();
                    JSONObject JSONStatus = array.getJSONObject(i);
                    status.ID = JSONStatus.getInt("id");
                    status.content_en = JSONStatus.getString("update_content_en");
                    status.content_fr = JSONStatus.getString("update_content_fr");
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                    status.updated = df.parse(JSONStatus.getString("created_at"));
                    statuses.add(status);
                }
                lastResult = HTTPResult.success;
            } else {
                lastResult = HTTPResult.other;
                Log.e(tag, "Error when fetching statuses: " + result);
                return null;
            }
        } catch (java.io.IOException e) {
            Log.e(tag, "Error", e);
            lastResult = HTTPResult.connection_failure;
            return null;
        } catch (JSONException | ParseException e) {
            Log.e(tag, "Error", e);
            lastResult = HTTPResult.unprocessable;
            return null;
        } finally {
            if (connection != null) connection.disconnect();
        }
        return statuses;
    }

    /**
     * Posts the status. Must be logged in.
     * @param status The status to be posted
     * @return The result of the post
     */
    public static HTTPResult postStatus(Status status) {
        String ID = Parameters.ID;
        String urlString = Uri.parse(rootURL).buildUpon().appendPath(interfacePath)
                .appendPath("users").appendPath(ID).appendPath("statuses").build().toString();
        HttpURLConnection connection = null;
        try {
            JSONObject JSONStatus = toJSON(status);
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", "Token token="+Parameters.APIKey);
            connection.setFixedLengthStreamingMode(JSONStatus.toString().getBytes().length);
            OutputStream out = connection.getOutputStream();
            out.write(JSONStatus.toString().getBytes());
            int result = connection.getResponseCode();
            if (result == HttpURLConnection.HTTP_OK) {
                return HTTPResult.success;
            } else {
                return HTTPResult.other;
            }
        } catch (JSONException e) {
            return HTTPResult.unprocessable;
        } catch (java.io.IOException e) {
            return HTTPResult.connection_failure;
        } finally {
            if (connection != null) connection.disconnect();
        }
    }

    public static HTTPResult updateStatus(Status status) {
        String urlString = Uri.parse(rootURL).buildUpon().appendPath(interfacePath)
                .appendPath("users").appendPath(Parameters.ID).appendPath("statuses")
                .appendPath(Integer.toString(status.ID)).build().toString();
        HttpURLConnection connection = null;
        try {
            JSONObject JSONStatus = toJSON(status);
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("PUT");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", "Token token=" + Parameters.APIKey);
            connection.setFixedLengthStreamingMode(JSONStatus.toString().getBytes().length);
            OutputStream out = connection.getOutputStream();
            out.write(JSONStatus.toString().getBytes());
            int result = connection.getResponseCode();
            if (result == HttpURLConnection.HTTP_OK) {
                return HTTPResult.success;
            } else {
                return HTTPResult.other;
            }
        } catch (JSONException e) {
            return HTTPResult.unprocessable;
        } catch (java.io.IOException e) {
            return HTTPResult.connection_failure;
        } finally {
            if (connection != null) connection.disconnect();
        }
    }

    /**
     * Deletes the status update with the specified ID.
     * Must be admin user to delete.
     * @param ID The ID of the status to be deleted
     * @return the result of the delete request
     */
    public static HTTPResult deleteStatus(int ID) {
        String urlString = Uri.parse(rootURL).buildUpon().appendPath(interfacePath)
                .appendPath("users").appendPath(Parameters.ID).appendPath("statuses")
                .appendPath(Integer.toString(ID)).build().toString();
        HttpURLConnection connection = null;
        try {
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("DELETE");
            int result = connection.getResponseCode();
            if (result == HttpURLConnection.HTTP_NO_CONTENT)
                return HTTPResult.success;
            else if (result == HttpURLConnection.HTTP_UNAUTHORIZED)
                return HTTPResult.unauthorized;
            else
                return HTTPResult.other;
        } catch (java.io.IOException e) {
            return HTTPResult.connection_failure;
        } finally {
            if (connection != null) connection.disconnect();
        }
    }

    public static ArrayList<Post> getPosts(String ID) {
        ArrayList<Post> posts = new ArrayList<>();
        String urlString = Uri.parse(rootURL).buildUpon().appendPath(interfacePath)
                .appendPath("users").appendPath(ID).appendPath("posts").build().toString();
        HttpURLConnection connection = null;
        try {
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(timeout);
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
                JSONArray array = response.getJSONArray("posts");
                for (int i = 0; i < array.length(); ++i) {
                    JSONObject jsonPost = array.getJSONObject(i);
                    Post post = new Post();
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                    post.blurb_en = jsonPost.getString("blurb_en");
                    post.blurb_fr = jsonPost.getString("blurb_fr");
                    post.content_en = jsonPost.getString("content_en");
                    post.content_fr = jsonPost.getString("content_fr");
                    post.title_en = jsonPost.getString("title_en");
                    post.title_fr = jsonPost.getString("title_fr");
                    post.created = df.parse(jsonPost.getString("created_at"));
                    post.updated = df.parse(jsonPost.getString("updated_at"));
                    posts.add(post);
                }
                lastResult = HTTPResult.success;
            } else {
                lastResult = HTTPResult.other;
                Log.e(tag, "Error when fetching statuses: " + result);
                return null;
            }
        } catch (java.io.IOException e) {
            Log.e(tag, "Error", e);
            lastResult = HTTPResult.connection_failure;
            return null;
        } catch (JSONException | ParseException e) {
            Log.e(tag, "Error", e);
            lastResult = HTTPResult.unprocessable;
            return null;
        } finally {
            if (connection != null) connection.disconnect();
        }
        return posts;
    }

    public static HTTPResult post(Post post) {
        String urlString = Uri.parse(rootURL).buildUpon().appendPath(interfacePath)
                .appendPath("users").appendPath(Parameters.ID).appendPath("posts").build().toString();
        HttpURLConnection connection = null;
        try {
            JSONObject JSONPost = toJSON(post);
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", "Token token="+Parameters.APIKey);
            connection.setFixedLengthStreamingMode(JSONPost.toString().getBytes().length);
            OutputStream out = connection.getOutputStream();
            out.write(JSONPost.toString().getBytes());
            int result = connection.getResponseCode();
            if (result == HttpURLConnection.HTTP_OK) {
                return HTTPResult.success;
            } else {
                return HTTPResult.other;
            }
        } catch (JSONException e) {
            return HTTPResult.unprocessable;
        } catch (java.io.IOException e) {
            return HTTPResult.connection_failure;
        } finally {
            if (connection != null) connection.disconnect();
        }
    }

    /**
     * Get the last operation's result
     * @return the last operation's result
     */
    public static HTTPResult getLastResult() {
        return lastResult;
    }

    private static JSONObject toJSON(Status status) throws JSONException {
        JSONObject json = new JSONObject();
        json.put("update_content_en", status.content_en);
        json.put("update_content_fr", status.content_fr);
        return json;
    }

    private static JSONObject toJSON(Post post) throws JSONException {
        JSONObject json = new JSONObject();
        json.put("title_en", post.title_en);
        json.put("title_fr", post.title_fr);
        json.put("content_en", post.content_en);
        json.put("content_fr", post.content_fr);
        json.put("blurb_en", post.blurb_en);
        json.put("blurb_fr", post.blurb_fr);
        return json;
    }
}
