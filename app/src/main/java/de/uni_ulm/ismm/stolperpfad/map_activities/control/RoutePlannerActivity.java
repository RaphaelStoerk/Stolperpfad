package de.uni_ulm.ismm.stolperpfad.map_activities.control;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import org.json.JSONObject;

import java.util.ArrayList;

import de.uni_ulm.ismm.stolperpfad.R;
import de.uni_ulm.ismm.stolperpfad.StolperpfadeApplication;
import de.uni_ulm.ismm.stolperpfad.database.data_util.DataFromJSON;
import de.uni_ulm.ismm.stolperpfad.map_activities.StolperpfadAppMapActivity;
import de.uni_ulm.ismm.stolperpfad.map_activities.model.MyRoad;
import de.uni_ulm.ismm.stolperpfad.map_activities.view.RouteOptionsDialog;

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
    private boolean loadable;
    private String current_file_name;
    private ArrayList<MyRoad> saved_paths;

    private RouteOptionsDialog dialog;
    private AlertDialog info_dialog;
    private AlertDialog save_dialog;
    private AlertDialog buff_dialog;


    @Override
    protected void onCreate(Bundle saved_state) {
        super.onCreate(saved_state);
        Log.i("MY_DEBUG_TAG","onCreate started");
        if (instance == null) {
            instance = this;
        }
        initializeGeneralControls(R.layout.activity_route_planner);
        Log.i("MY_DEBUG_TAG","General controls done");
        Bundle b = getIntent().getExtras();
        int id = -1;
        boolean next = false;
        if(b != null) {
            id = b.getInt("id");
            next = b.getBoolean("next");
        }
        Log.i("MY_DEBUG_TAG","getExtras done");
        initializeMapQuestFragment(id, next);
        Log.i("MY_DEBUG_TAG","Fragment done");

        // Route Planner specific setups
        //aq.id(R.id.header_route_planner).getView().setTranslationZ(HEADER_TRANSLATION_Z / 2);
        //aq.id(R.id.route_option_button).visible().clicked(my_click_listener);
        aq.id(R.id.save_route_button).visible().clicked(my_click_listener);
        aq.id(R.id.info_map_options_button).visible().clicked(my_click_listener);
        aq.id(R.id.start_guide_button).visible().clicked(my_click_listener);
        aq.id(R.id.menu_open_button).visible().clicked(my_click_listener).getView();
        aq.id(R.id.route_option_button).visible().clicked(my_click_listener).getView();
        menu_up = false;
        Log.i("MY_DEBUG_TAG","setup done");
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
        dialog = RouteOptionsDialog.newInstance(this);
        dialog.show(getSupportFragmentManager(), "dialog");
    }

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

    public void saveOrLoadRouteDialog() {
        AlertDialog.Builder builder;
        current_file_name = "";
        if (StolperpfadeApplication.getInstance().isDarkMode()) {
            builder = new AlertDialog.Builder(this, R.style.DialogTheme_Dark);
        } else {
            builder = new AlertDialog.Builder(this, R.style.DialogTheme_Light);
        }
        // Get the layout inflater
        LayoutInflater inflater = myMapFragment.getLayoutInflater();

        View myDialogView = inflater.inflate(R.layout.dialog_save_load, null);

        myDialogView.findViewById(R.id.save_close).setOnClickListener(view -> {if(save_dialog != null)save_dialog.cancel();});

        myDialogView.findViewById(R.id.save_button).setOnClickListener(view -> {
            if(save_dialog != null)save_dialog.cancel();
            myMapFragment.saveRoute(current_file_name);
        });

        EditText time_input = myDialogView.findViewById(R.id.path_name_input);

        time_input.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                current_file_name = charSequence.toString();
                reloadPaths(myDialogView, current_file_name, false);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                current_file_name = editable.toString();
                reloadPaths(myDialogView, current_file_name, false);
            }
        });

        reloadPaths(myDialogView, "", true);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(myDialogView);
        save_dialog = builder.create();
        save_dialog.show();
    }

    private MyRoad getCurrentRoute(String stub) {
        if(saved_paths == null) {
            return null;
        }
        if(stub == null || stub.length() == 0) {
            if(current_file_name == null || current_file_name.length() == 0) {
                return null;
            }
            stub = current_file_name;
        }
        for(MyRoad path : saved_paths) {
            if(path.getName().equalsIgnoreCase(stub)) {
                return path;
            }
        }
        return null;
    }

    @SuppressLint("StaticFieldLeak")
    private void reloadPaths(View myDialogView, String current_file_name, boolean from_storage) {
        LinearLayout path_container = myDialogView.findViewById(R.id.container_loaded_paths);
        path_container.removeAllViews();
        if(from_storage) {
            new LoadPathsTask() {
                @Override
                protected void onPostExecute(Void aVoid) {
                    showPaths(myDialogView, path_container, current_file_name);
                }
            }.execute();
        } else {
            showPaths(myDialogView, path_container, current_file_name);
        }
    }

    private void showPaths(View myDialogView, LinearLayout container, String current_file_name) {
        ArrayList<String> show_paths = new ArrayList<>();
        for(MyRoad path : saved_paths) {
            if(path.getName().toLowerCase().contains(current_file_name.toLowerCase())) {
                show_paths.add(path.getName());
            }
        }

        if(show_paths.size() > 0) {
            for(String s : (show_paths)) {
                container.addView(addPathButton(myDialogView, container, s, current_file_name, false));
            }
        } else {
            container.addView(addPathButton(myDialogView, container, "< als neuen Pfad anlegen >", "", true));
        }
    }

    private Button addPathButton(View myDialogView, LinearLayout container, String path_name, String search_by, boolean stub) {
        Button but = (Button) LayoutInflater.from(container.getContext()).inflate(R.layout.button_path_list, null);
        if(!stub) {
            but.setOnClickListener(view -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(path_name);
                builder.setMessage(getInfoFor(path_name));
                builder.setPositiveButton("Auswählen", (dialogInterface, i) -> {
                    current_file_name = path_name;
                    closeDialogs();
                    myMapFragment.loadRoute(getCurrentRoute(path_name));
                });
                builder.setNegativeButton("Zurück", (dialogInterface, i) -> dialogInterface.cancel());
                buff_dialog = builder.create();
                buff_dialog.show();
            });
        }
        String show_text;
        if(search_by == null || search_by.equals("") || stub) {
            show_text = path_name;
            but.setText(show_text);
        } else {
            show_text = formatText(path_name, search_by);
            but.setText(Html.fromHtml(show_text));
        }
        //int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 42, getResources().getDisplayMetrics());
        return but;
    }

    private void closeDialogs() {
        if(buff_dialog != null) {
            buff_dialog.cancel();
            buff_dialog = null;
        }
        if(save_dialog != null) {
            save_dialog.cancel();
            save_dialog = null;
        }
    }

    private String getInfoFor(String path_name) {
        MyRoad road = getCurrentRoute(path_name);
        if(road == null) {
            return "Keine Informationen gefunden...";
        }
        return "Zeit: " + road.getTime() + "\nStart: " + road.getBasicStart() + "\nAnzahl Steine: " + road.getStoneCount();
    }

    private String formatText(String path_name, String search_by) {
        int index = path_name.indexOf(search_by);
        String ret = "";
        if(index > 0 && index + search_by.length() < path_name.length()){
            ret = path_name.substring(0, index) + "<b>" + path_name.substring(index, index+search_by.length()) + "</b>" + path_name.substring(index+search_by.length());
        } else {
            ret = path_name;
        }
        return ret;
    }

    private class LoadPathsTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {

                ArrayList<JSONObject> jsons = DataFromJSON.loadAllJSONFromExternalDirectory(RoutePlannerActivity.this, "paths");
                saved_paths = new ArrayList<>();
                for(JSONObject path : jsons) {
                    saved_paths.add(MyRoad.newFromJson(path));
                }
            Log.i("MY_SAVE_TAG", "paths: " + saved_paths.size());
            return null;
        }
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
                }
            });
            menu_open.animate().rotation(0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    animating = false;
                }
            });
        } else {
            menu_open.animate().rotation(180f).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                }
            });
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
        animating = true;
        menu_up = !menu_up;
    }

    public void activatePathPlanner(boolean b1) {
        aq.id(R.id.start_guide_button).getView().getBackground().setTint(getResources().getColor(R.color.colorAccentLightMode, null));
    }

    public void deactivateGuide(){
        if(StolperpfadeApplication.getInstance().isDarkMode()) {
            aq.id(R.id.start_guide_button).getView().getBackground().setTint(getResources().getColor(R.color.colorPrimaryDarkMode, null));
            ImageButton b = (ImageButton) aq.id(R.id.start_guide_button).getView();
            b.getDrawable().setTint(getResources().getColor(R.color.colorPrimaryContrastDarkMode, null));
        } else {
            aq.id(R.id.start_guide_button).getView().getBackground().setTint(getResources().getColor(R.color.colorPrimaryLightMode, null));
            ImageButton b = (ImageButton) aq.id(R.id.start_guide_button).getView();
            b.getDrawable().setTint(getResources().getColor(R.color.colorPrimaryContrastLightMode, null));
        }
    }
}
