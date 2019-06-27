package org.arxing.axutils_android;

import android.content.Context;

import java.util.Set;

public class PreferenceUtils {
    private SecuritySharedPreference pref;

    public PreferenceUtils(Context context, String prefName) {
        this(context, prefName, Context.MODE_PRIVATE);
    }

    public PreferenceUtils(Context context, String prefName, int mode) {
        pref = new SecuritySharedPreference(context, prefName, Context.MODE_PRIVATE);
    }

    public boolean getBool(String key, boolean def) {
        return pref.getBoolean(key, def);
    }

    public int getInt(String key, int def){
        return pref.getInt(key, def);
    }

    public String getString(String key, String def){
        return pref.getString(key, def);
    }

    public float getFloat(String key, float def){
        return pref.getFloat(key, def);
    }

    public long getLong(String key, long def){
        return pref.getLong(key, def);
    }

    public Set<String> getStringSet(String key, Set<String> def){
        return pref.getStringSet(key, def);
    }

    public <T> void set(String key, T value) {
        if (value instanceof Boolean) {
            pref.edit().putBoolean(key, (Boolean) value).commit();
        } else if (value instanceof Integer) {
            pref.edit().putInt(key, (Integer) value).commit();
        } else if (value instanceof String) {
            pref.edit().putString(key, (String) value).commit();
        } else if (value instanceof Set) {
            pref.edit().putStringSet(key, (Set<String>) value).commit();
        }
    }

    public void remove(String key) {
        pref.edit().remove(key).commit();
    }
}
