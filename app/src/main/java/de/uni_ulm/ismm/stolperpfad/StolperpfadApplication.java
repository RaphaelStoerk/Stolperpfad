package de.uni_ulm.ismm.stolperpfad;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

public class StolperpfadApplication extends Application {

    private boolean dark_mode = false;
    private boolean first_call = true;
    private SharedPreferences prefs;
    private static StolperpfadApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

        prefs = this.getSharedPreferences(
                "de.uni_ulm.ismm.stolperpfad", Context.MODE_PRIVATE);

        if(!prefs.getBoolean("de.uni_ulm.ismm.stolperpfad.dark_mode", false)) {
            prefs.edit().putBoolean("de.uni_ulm.ismm.stolperpfad.dark_mode", false).apply();
        }
        dark_mode = prefs.getBoolean("de.uni_ulm.ismm.stolperpfad.dark_mode", false);

        prefs.edit().putBoolean("de.uni_ulm.ismm.stolperpfad.first_call", true).apply();
        first_call = prefs.getBoolean("de.uni_ulm.ismm.stolperpfad.first_call", false);
    }

    public boolean isDarkMode() {
        return dark_mode =  prefs.getBoolean("de.uni_ulm.ismm.stolperpfad.dark_mode", false);
    }

    public void setDarkMode(boolean dark_mode) {
        prefs.edit().putBoolean("de.uni_ulm.ismm.stolperpfad.dark_mode", dark_mode).apply();
        this.dark_mode = prefs.getBoolean("de.uni_ulm.ismm.stolperpfad.dark_mode", false);
    }

    public boolean isFirstCall() {
        return first_call = prefs.getBoolean("de.uni_ulm.ismm.stolperpfad.first_call", false);
    }

    public void setFirstCall(boolean first_call) {
        prefs.edit().putBoolean("de.uni_ulm.ismm.stolperpfad.first_call", first_call).apply();
        this.first_call = prefs.getBoolean("de.uni_ulm.ismm.stolperpfad.first_call", false);
    }

    public static StolperpfadApplication getInstance() {
        return instance;
    }

}
