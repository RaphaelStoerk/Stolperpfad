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
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Switch;

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

    protected boolean currently_in_dark_mode = false;

    private AlertDialog dialog;

    public void showQuickAccesMenu() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Get the layout inflater
        LayoutInflater inflater = getLayoutInflater();

        View myDialogView = inflater.inflate(R.layout.quick_acces_menu, null);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(myDialogView);

        toggleDarkMode(myDialogView.findViewById(R.id.dark_mode_switch), false, StolperpfadApplication.getInstance().isDarkMode());

        myDialogView.findViewById(R.id.quick_acces_route_planner).setOnClickListener(myClickListener);
        myDialogView.findViewById(R.id.quick_acces_next_stone_button).setOnClickListener(myClickListener);
        myDialogView.findViewById(R.id.quick_access_historical_info).setOnClickListener(myClickListener);
        myDialogView.findViewById(R.id.quick_access_stone_info).setOnClickListener(myClickListener);
        myDialogView.findViewById(R.id.quick_acces_privacy).setOnClickListener(myClickListener);
        myDialogView.findViewById(R.id.quick_acces_scanner_button).setOnClickListener(myClickListener);
        myDialogView.findViewById(R.id.quick_access_impresum).setOnClickListener(myClickListener);
        myDialogView.findViewById(R.id.dark_mode_switch).setOnClickListener(myClickListener);
        myDialogView.findViewById(R.id.quick_acces_project_artist).setOnClickListener(myClickListener);
        myDialogView.setFocusable(true);
        myDialogView.requestFocus();
        // Create the AlertDialog
        dialog = builder.create();
        dialog.show();
    }

    private void toggleDarkMode(Switch mySwitch, boolean toggle, boolean isNowDarkMode) {
        if(isNowDarkMode){
            mySwitch.getThumbDrawable().setTint(getResources().getColor(R.color.colorAccentLightMode, null));
            mySwitch.getTrackDrawable().setTint(getResources().getColor(R.color.colorAccentLightMode, null));
            mySwitch.setChecked(true);
            StolperpfadApplication.getInstance().setDarkMode(true);
            if(toggle) {
                recreate();
            }
        } else {
            mySwitch.getThumbDrawable().setTint(getResources().getColor(R.color.colorPrimaryContrastDarkMode, null));
            mySwitch.getTrackDrawable().setTint(getResources().getColor(R.color.colorPrimaryContrastDarkMode, null));
            StolperpfadApplication.getInstance().setDarkMode(false);
            mySwitch.setChecked(false);
            if(toggle) {
                recreate();
            }
        }
    }

    public void toggleDarkMode(Switch mySwitch, boolean toggle) {
        toggleDarkMode(mySwitch, toggle, !StolperpfadApplication.getInstance().isDarkMode());
    }

    public void endQuickAccesDialog() {
        if(dialog != null) {
            dialog.cancel();
            dialog = null;
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if(StolperpfadApplication.getInstance().isDarkMode()) {
            setTheme(R.style.AppTheme_Dark);
            currently_in_dark_mode = true;
        } else {
            setTheme(R.style.AppTheme_Light);
            currently_in_dark_mode = false;
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(StolperpfadApplication.getInstance().isDarkMode() != currently_in_dark_mode) {
            currently_in_dark_mode = !currently_in_dark_mode;
            recreate();
        }
        setVisible(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        setVisible(false);
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
