package net.givreardent.sam.blogger.internal;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Sam on 2015-12-07.
 */
public class Parameters {
    public static final String PrefsName = "net.givreardent.sam.blogger";
    public static final String URLSetting = "Base URL";

    public static String APIKey = "";
    public static String ID = "";

    public static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(PrefsName, 0);
    }
}
