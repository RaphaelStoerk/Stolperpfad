package de.uni_ulm.ismm.stolperpfad.general;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.androidquery.AQuery;

import java.lang.reflect.Field;

import de.uni_ulm.ismm.stolperpfad.R;
import de.uni_ulm.ismm.stolperpfad.StolperpfadApplication;

public abstract class StolperpfadeAppActivity extends AppCompatActivity {

    // The AQuery framework lets us write short understandable code, see further down
    protected AQuery aq;

    protected MyButtonClickListener<StolperpfadeAppActivity> myClickListener;

    protected int currentLayout;

    public final float HEADER_TRANSLATION_Z = 8;

    public void showQuickAccesMenu() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // set the contents of the dialog
        builder.setTitle("Dark Mode? Application will restart");
        builder.setPositiveButton("Ja", (dialogInterface, i) -> {
            SharedPreferences prefs = this.getSharedPreferences(
                    "de.uni_ulm.ismm.stolperpfad", Context.MODE_PRIVATE);
            prefs.edit().putBoolean("de.uni_ulm.ismm.stolperpfad.dark_mode", true).apply();
            StolperpfadApplication.getInstance().setDarkMode(true);
            recreate();
        });

        builder.setNegativeButton("Nein", (dialogInterface, i) -> {
            SharedPreferences prefs = this.getSharedPreferences(
                    "de.uni_ulm.ismm.stolperpfad", Context.MODE_PRIVATE);
            prefs.edit().putBoolean("de.uni_ulm.ismm.stolperpfad.dark_mode", false).apply();
            StolperpfadApplication.getInstance().setDarkMode(false);
            recreate();
        });

        // Create the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if(StolperpfadApplication.getInstance().isDarkMode()) {
            setTheme(R.style.AppTheme_Dark);
        } else {
            setTheme(R.style.AppTheme_Light);
        }
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
        if(StolperpfadApplication.getInstance().modeHasBeenChanged()) {
            recreate();
            StolperpfadApplication.getInstance().setModeChanged(false);
        }
    }

    protected void initializeGeneralControls(@LayoutRes int currentLayout) {
        // Initialize important helper-Objects

        setContentView(this.currentLayout = currentLayout);

        aq = new AQuery(this);

        // Initialize the Listener for the clickable items
        myClickListener = new MyButtonClickListener<>();
        myClickListener.setMyActivity(this);

        // add the listener to the items
        aq.id(R.id.quick_access_button).visible().clicked(myClickListener);
        aq.id(R.id.header).getView().setTranslationZ(HEADER_TRANSLATION_Z);
    }
}
