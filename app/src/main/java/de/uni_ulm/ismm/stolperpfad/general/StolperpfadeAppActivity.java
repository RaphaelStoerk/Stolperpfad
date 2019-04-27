package de.uni_ulm.ismm.stolperpfad.general;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;

import com.androidquery.AQuery;

import de.uni_ulm.ismm.stolperpfad.R;
import de.uni_ulm.ismm.stolperpfad.StolperpfadeApplication;

public abstract class StolperpfadeAppActivity extends AppCompatActivity {

    // The AQuery framework lets us write short understandable code, see further down
    protected AQuery aq;

    protected MyButtonClickListener<StolperpfadeAppActivity> myClickListener;

    protected int currentLayout;

    public final float HEADER_TRANSLATION_Z = 8;

    protected boolean currently_in_dark_mode = false;

    private AlertDialog dialog;

    public void showQuickAccesMenu() {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(this, R.style.DialogTheme_Light);

        // Get the layout inflater
        LayoutInflater inflater = getLayoutInflater();

        View myDialogView = inflater.inflate(R.layout.dialog_quick_access_menu, null);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(myDialogView);

        toggleDarkMode(myDialogView.findViewById(R.id.dark_mode_switch), false, StolperpfadeApplication.getInstance().isDarkMode());

        myDialogView.findViewById(R.id.quick_access_route_planner).setOnClickListener(myClickListener);
        myDialogView.findViewById(R.id.quick_access_next_stone_button).setOnClickListener(myClickListener);
        myDialogView.findViewById(R.id.quick_access_historical_info).setOnClickListener(myClickListener);
        myDialogView.findViewById(R.id.quick_access_stone_info).setOnClickListener(myClickListener);
        myDialogView.findViewById(R.id.quick_access_privacy).setOnClickListener(myClickListener);
        myDialogView.findViewById(R.id.quick_access_scanner_button).setOnClickListener(myClickListener);
        myDialogView.findViewById(R.id.quick_access_impressum).setOnClickListener(myClickListener);
        myDialogView.findViewById(R.id.dark_mode_switch).setOnClickListener(myClickListener);
        myDialogView.findViewById(R.id.dark_mode_text).setOnClickListener(myClickListener);
        myDialogView.findViewById(R.id.quick_access_project_artist).setOnClickListener(myClickListener);
        myDialogView.setFocusable(true);
        myDialogView.requestFocus();
        // Create the AlertDialog
        dialog = builder.create();
        dialog.show();
    }

    private void toggleDarkMode(Switch mySwitch, boolean toggle, boolean isNowDarkMode) {
        int[] attr = {R.attr.colorAppPrimary, R.attr.colorAppAccent};
        TypedArray ta = this.obtainStyledAttributes(attr);
        if(isNowDarkMode){
            @SuppressLint("ResourceType")
            int color = ta.getResourceId(1, android.R.color.black);
            mySwitch.getThumbDrawable().setTint(getResources().getColor(color, getTheme()));
            mySwitch.getTrackDrawable().setTint(getResources().getColor(color, getTheme()));
            mySwitch.setChecked(true);
            StolperpfadeApplication.getInstance().setDarkMode(true);
            if(toggle) {
                recreate();
            }
        } else {
            int color = ta.getResourceId(0, android.R.color.black);
            mySwitch.getThumbDrawable().setTint(getResources().getColor(color, getTheme()));
            mySwitch.getTrackDrawable().setTint(getResources().getColor(color, getTheme()));
            StolperpfadeApplication.getInstance().setDarkMode(false);
            mySwitch.setChecked(false);
            if(toggle) {
                recreate();
            }
        }
        currently_in_dark_mode = isNowDarkMode;
        ta.recycle();
    }

    public void toggleDarkMode(Switch mySwitch, boolean toggle) {
        toggleDarkMode(mySwitch, toggle, !StolperpfadeApplication.getInstance().isDarkMode());
    }

    public void endQuickAccesDialog() {
        if(dialog != null) {
            dialog.cancel();
            dialog = null;
        }
    }

    public boolean isInDarkMode() {
        return currently_in_dark_mode;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if(StolperpfadeApplication.getInstance().isDarkMode()) {
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
        if(StolperpfadeApplication.getInstance().isDarkMode() != currently_in_dark_mode) {
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
        aq.id(R.id.header_image).visible().clicked(myClickListener);
        aq.id(R.id.header).getView().setTranslationZ(HEADER_TRANSLATION_Z);

        Button back_button = aq.id(R.id.button_back).getButton();
        if(back_button != null) {
            aq.id(R.id.button_back).visible().clicked(myClickListener);
        }
    }

    public void reactToLink(String s) {
        // TODO: Handle on Link click
    }
}
