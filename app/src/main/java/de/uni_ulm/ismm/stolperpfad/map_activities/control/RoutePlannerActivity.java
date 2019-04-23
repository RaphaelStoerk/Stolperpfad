package de.uni_ulm.ismm.stolperpfad.map_activities.control;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import de.uni_ulm.ismm.stolperpfad.R;
import de.uni_ulm.ismm.stolperpfad.map_activities.StolperpfadAppMapActivity;
import de.uni_ulm.ismm.stolperpfad.map_activities.view.RouteOptionsDialog;
import de.uni_ulm.ismm.stolperpfad.map_activities.view.RouteOptionsFragment;

public class RoutePlannerActivity extends StolperpfadAppMapActivity {

    private String[] categories = new String[]{"Nein", "Jüdische Verfolgte", "Politisch Verfolgte", "Andere"};
    private String selected_category;

    private static RoutePlannerActivity instance;

    public static final int START_CHOICE_GPS = 0;
    public static final int START_CHOICE_MAP = 1;
    public static final int START_CHOICE_CTR = 2;
    public static final int START_CHOICE_NAN = -1;

    public static final int END_CHOICE_STN = 0;
    public static final int END_CHOICE_MAP = 1;
    public static final int END_CHOICE_CTR = 2;
    public static final int END_CHOICE_NAN = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (instance == null) {
            instance = this;
        }
        initializeGeneralControls(R.layout.activity_route_planner);
        initializeMapQuestFragment(false);

        // Route Planner specific setups
        aq.id(R.id.header_route_planner).getView().setTranslationZ(HEADER_TRANSLATION_Z / 2);
        aq.id(R.id.route_option_button).visible().clicked(myClickListener);
        aq.id(R.id.save_route_button).visible().clicked(myClickListener);
        aq.id(R.id.info_map_options_button).visible().clicked(myClickListener);
        aq.id(R.id.start_guide_button).invisible().clicked(myClickListener);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void onPause() {
        super.onPause();
    }

    public static RoutePlannerActivity getInstance() {
        if (instance == null) {
            return instance = new RoutePlannerActivity();
        }
        return instance;
    }

    public void routeOptionDialog() {
        RouteOptionsDialog dialog = RouteOptionsDialog.newInstance(this);
        dialog.show(getSupportFragmentManager(), "dialog");
    }

    public void informationDialog() {
        AlertDialog.Builder builder;
        if (currently_in_dark_mode) {
            builder = new AlertDialog.Builder(this, R.style.DialogTheme_Dark);
        } else {
            builder = new AlertDialog.Builder(this, R.style.DialogTheme_Light);
        }
        // Get the layout inflater
        LayoutInflater inflater = myMapFragment.getLayoutInflater();

        View myDialogView = inflater.inflate(R.layout.dialog_map_info, null);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(myDialogView);

        builder.setNegativeButton("Verstanden", (dialogInterface, i) -> {
            dialogInterface.cancel();
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void saveOrLoadRouteDialog() {
        AlertDialog.Builder builder;
        if (currently_in_dark_mode) {
            builder = new AlertDialog.Builder(this, R.style.DialogTheme_Dark);
        } else {
            builder = new AlertDialog.Builder(this, R.style.DialogTheme_Light);
        }
        // Get the layout inflater
        LayoutInflater inflater = myMapFragment.getLayoutInflater();

        View myDialogView = inflater.inflate(R.layout.dialog_save_or_load_route, null);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(myDialogView);

        builder.setNegativeButton("Speichern", (dialogInterface, i) -> {
            dialogInterface.cancel();
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void startGuide() {
        myMapFragment.startGuide();
    }

    public void calcRoute(String start_choice, String end_choice, String time_choice) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        myMapFragment.createRoute(start_choice, end_choice, time_choice);
    }



    class MyDialog extends Dialog {

        public MyDialog(@NonNull Context context) {
            super(context);
        }
    }



}
