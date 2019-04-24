package de.uni_ulm.ismm.stolperpfad.map_activities.control;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
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
import de.uni_ulm.ismm.stolperpfad.StolperpfadeApplication;
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

    private boolean menu_up;
    private boolean animating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (instance == null) {
            instance = this;
        }
        initializeGeneralControls(R.layout.activity_route_planner);
        initializeMapQuestFragment(false);

        // Route Planner specific setups
        //aq.id(R.id.header_route_planner).getView().setTranslationZ(HEADER_TRANSLATION_Z / 2);
        aq.id(R.id.route_option_button).visible().clicked(myClickListener);
        aq.id(R.id.save_route_button).visible().clicked(myClickListener);
        aq.id(R.id.info_map_options_button).visible().clicked(myClickListener);
        aq.id(R.id.start_guide_button).visible().clicked(myClickListener);
        aq.id(R.id.menu_open_button).visible().clicked(myClickListener);
        aq.id(R.id.menu_close_button).visible().clicked(myClickListener);

        menu_up = false;
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

    private RouteOptionsDialog dialog;

    public void routeOptionDialog() {
        dialog = RouteOptionsDialog.newInstance(this);
        dialog.show(getSupportFragmentManager(), "dialog");
    }

    private AlertDialog info_dialog;

    public void informationDialog() {
        AlertDialog.Builder builder;
        if (StolperpfadeApplication.getInstance().isDarkMode()) {
            builder = new AlertDialog.Builder(this, R.style.DialogTheme_Dark);
        } else {
            builder = new AlertDialog.Builder(this, R.style.DialogTheme_Light);
        }
        // Get the layout inflater
        LayoutInflater inflater = myMapFragment.getLayoutInflater();

        View myDialogView = inflater.inflate(R.layout.dialog_map_info, null);

        myDialogView.findViewById(R.id.info_close).setOnClickListener(view -> {if(info_dialog != null)info_dialog.cancel();});

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(myDialogView);

        info_dialog = builder.create();
        info_dialog.show();
    }

    private AlertDialog save_dialog;

    public void saveOrLoadRouteDialog() {
        AlertDialog.Builder builder;
        if (StolperpfadeApplication.getInstance().isDarkMode()) {
            builder = new AlertDialog.Builder(this, R.style.DialogTheme_Dark);
        } else {
            builder = new AlertDialog.Builder(this, R.style.DialogTheme_Light);
        }
        // Get the layout inflater
        LayoutInflater inflater = myMapFragment.getLayoutInflater();

        View myDialogView = inflater.inflate(R.layout.dialog_map_save, null);

        //myDialogView.findViewById(R.id.save_close).setOnClickListener(view -> {if(save_dialog != null)save_dialog.cancel();});

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(myDialogView);

        save_dialog = builder.create();
        save_dialog.show();
    }

    public void startGuide() {
        myMapFragment.startGuide();
    }

    public void calcRoute(String start_choice, String end_choice, String time_choice) {
        if(dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        myMapFragment.createRoute(start_choice.startsWith("#") ? "-1" : start_choice,
                end_choice.startsWith("#") ? "-1" : end_choice,
                time_choice.startsWith("#") ? "-1" : time_choice);
    }

    public void endDialog() {
        if(dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }

    public void toggleMenu() {
        View toolbar = aq.id(R.id.map_toolbar).getView();
        View menu_open = aq.id(R.id.menu_open_button).getView();
        if(animating) {
            return;
        }
        if(menu_up) {
            toolbar.animate().translationY(toolbar.getHeight()).alpha(0f).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    toolbar.setVisibility(View.GONE);
                    menu_open.setAlpha(0f);
                    menu_open.setVisibility(View.VISIBLE);
                    menu_open.animate().alpha(1f).setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            animating = false;
                        }
                    });
                }
            });
        } else {
            menu_open.animate().alpha(0f).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    menu_open.setVisibility(View.GONE);
                    toolbar.setAlpha(0f);
                    toolbar.setVisibility(View.VISIBLE);
                    toolbar.animate().translationY(0).alpha(1f).setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            animating = false;
                        }
                    });
                }
            });
        }
        animating = true;
        menu_up = !menu_up;
    }
}
