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
import de.uni_ulm.ismm.stolperpfad.map_activities.model.Stolperpfad;
import de.uni_ulm.ismm.stolperpfad.map_activities.view.RouteOptionsDialog;

/**
 * This class represents the Route Planner Activity where the user can interact with a
 * map on which all stones can be found and routes based on these stones can be created
 */
@SuppressLint("InflateParams")
public class RoutePlannerActivity extends StolperpfadAppMapActivity {

    public static final int START_CHOICE_GPS = 0;
    public static final int START_CHOICE_MAP = 1;
    public static final int START_CHOICE_CTR = 2;
    public static final int END_CHOICE_STN = 0;
    public static final int END_CHOICE_MAP = 1;
    public static final int END_CHOICE_CTR = 2;
    public static final int CHOICE_NAN = -1;
    public static final int NO_NEXT_STONE_FLAG = -1;
    private static final float HALF_ROTATION = 180f;
    private static final float NO_ALPHA = 0f;
    private static final float NO_ROTATION = 0;
    private static final float FULL_ALPHA = 1f;
    private boolean menu_up;
    private boolean animating;
    private String current_file_name;
    private ArrayList<Stolperpfad> saved_paths;
    private RouteOptionsDialog route_options_dialog;
    private AlertDialog info_dialog;
    private AlertDialog save_dialog;
    private AlertDialog route_info_dialog;
    private AlertDialog loading_dialog;
    private AlertDialog error_dialog;

    @Override
    protected void onCreate(Bundle saved_state) {
        super.onCreate(saved_state);
        initializeGeneralControls(R.layout.activity_route_planner);
        Bundle b = getIntent().getExtras();
        int id = -1;
        boolean next = false;
        if(b != null) {
            id = b.getInt("id");
            next = b.getBoolean("next");
        }
        initializeMapQuestFragment(id, next);
        // Route Planner specific setups
        aq.id(R.id.save_route_button).visible().clicked(my_click_listener);
        aq.id(R.id.info_map_options_button).visible().clicked(my_click_listener);
        aq.id(R.id.start_guide_button).visible().clicked(my_click_listener);
        aq.id(R.id.menu_open_button).visible().clicked(my_click_listener).getView();
        aq.id(R.id.route_option_button).visible().clicked(my_click_listener).getView();
        menu_up = false;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void onPause() {
        super.onPause();
    }

    /**
     * Create a new route option route_options_dialog
     */
    public void routeOptionDialog() {
        closeDialogs();
        route_options_dialog = RouteOptionsDialog.newInstance();
        route_options_dialog.show(getSupportFragmentManager(), "route_options_dialog");
    }

    /**
     * Display the information route_options_dialog with all necessary map informations
     */
    public void informationDialog() {
        closeDialogs();
        AlertDialog.Builder builder;
        if (StolperpfadeApplication.getInstance().isDarkMode()) {
            builder = new AlertDialog.Builder(this, R.style.DialogTheme_Dark);
        } else {
            builder = new AlertDialog.Builder(this, R.style.DialogTheme_Light);
        }
        LayoutInflater inflater = map_quest.getLayoutInflater();
        View info_dialog_view = inflater.inflate(R.layout.dialog_map_info, null);
        info_dialog_view.findViewById(R.id.info_close).setOnClickListener(view -> {if(info_dialog != null)info_dialog.cancel();});
        builder.setView(info_dialog_view);
        info_dialog = builder.create();
        info_dialog.show();
    }

    /**
     * Display the save and load route_options_dialog for routes
     */
    public void saveOrLoadRouteDialog() {
        closeDialogs();
        AlertDialog.Builder builder;
        current_file_name = "";
        if (StolperpfadeApplication.getInstance().isDarkMode()) {
            builder = new AlertDialog.Builder(this, R.style.DialogTheme_Dark);
        } else {
            builder = new AlertDialog.Builder(this, R.style.DialogTheme_Light);
        }
        LayoutInflater inflater = map_quest.getLayoutInflater();
        View load_dialog_view = inflater.inflate(R.layout.dialog_save_load, null);
        load_dialog_view.findViewById(R.id.save_close).setOnClickListener(view -> {if(save_dialog != null)save_dialog.cancel();});
        load_dialog_view.findViewById(R.id.save_button).setOnClickListener(view -> {
            boolean saved = map_quest.saveRoute(current_file_name); // TODO: inform user
            if(save_dialog != null && saved)save_dialog.cancel();
        });
        EditText time_input = load_dialog_view.findViewById(R.id.path_name_input);
        time_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { /*---*/ }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                current_file_name = charSequence.toString();
                reloadPaths(load_dialog_view, current_file_name, false);
            }
            @Override
            public void afterTextChanged(Editable editable) {
                current_file_name = editable.toString();
                reloadPaths(load_dialog_view, current_file_name, false);
            }
        });
        reloadPaths(load_dialog_view, "", true);
        builder.setView(load_dialog_view);
        save_dialog = builder.create();
        save_dialog.show();
    }

    /**
     * Display an error dialog with specified title
     */
    public void errorDialog(String s) {
        errorDialog(s, "");
    }

    /**
     * Display an error dialog with specified title text
     */
    public void errorDialog(String title, String message) {
        closeDialogs();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setNegativeButton("Okay", (dialogInterface, i) -> dialogInterface.cancel());
        error_dialog = builder.create();
        error_dialog.show();
    }

    /**
     * Checks the external storage if there has been changes in the saved routes
     *
     * @param load_dialog_view the current route_options_dialog view
     * @param current_user_input the currently selected file
     * @param from_storage if the routes in the external memory should be reloaded
     */
    @SuppressLint("StaticFieldLeak")
    private void reloadPaths(View load_dialog_view, String current_user_input, boolean from_storage) {
        LinearLayout path_container = load_dialog_view.findViewById(R.id.container_loaded_paths);
        path_container.removeAllViews();
        if(from_storage) {
            new LoadPathsTask() {
                @Override
                protected void onPostExecute(Void aVoid) {
                    showPaths(path_container, current_user_input);
                }
            }.execute();
        } else {
            showPaths(path_container, current_user_input);
        }
    }

    /**
     * Displays the saved paths that match the input from the search bar
     *
     * @param container the layout that contains the path buttons
     * @param current_user_input the user input from the search bar
     */
    private void showPaths(LinearLayout container, String current_user_input) {
        ArrayList<String> show_paths = new ArrayList<>();
        for(Stolperpfad path : saved_paths) {
            if(path.getName().toLowerCase().contains(current_user_input.toLowerCase())) {
                show_paths.add(path.getName());
            }
        }
        if(show_paths.size() > 0) {
            for(String s : (show_paths)) {
                container.addView(addPathButton(container, s, current_user_input, false));
            }
        } else {
            container.addView(addPathButton(container, "< als neuen Pfad anlegen >", "", true));
        }
    }

    /**
     * Creates a new button for a specific route that was saved in the external storage
     *
     * @param container the layout that contains the buttons
     * @param path_name the name of the saved route
     * @param search_by the user input
     * @param no_pathname_found true, if the input doesn't match any saved route names
     * @return a new button
     */
    private Button addPathButton(LinearLayout container, String path_name, String search_by, boolean no_pathname_found) {
        Button but = (Button) LayoutInflater.from(container.getContext()).inflate(R.layout.button_path_list, null);
        if(!no_pathname_found) {
            but.setOnClickListener(view -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(path_name);
                builder.setMessage(getInfoFor(path_name));
                builder.setPositiveButton("Auswählen", (dialogInterface, i) -> {
                    current_file_name = path_name;
                    closeDialogs();
                    map_quest.loadRoute(getCurrentRoute(path_name));
                });
                builder.setNegativeButton("Zurück", (dialogInterface, i) -> dialogInterface.cancel());
                route_info_dialog = builder.create();
                route_info_dialog.show();
            });
        }
        String show_text;
        if(search_by == null || search_by.equals("") || no_pathname_found) {
            show_text = path_name;
            but.setText(show_text);
        } else {
            show_text = formatText(path_name, search_by);
            but.setText(Html.fromHtml(show_text));
        }
        return but;
    }

    /**
     * Closes all currently open dialogs
     */
    public void closeDialogs() {
        if(route_info_dialog != null) {
            route_info_dialog.cancel();
            route_info_dialog = null;
        }
        if(save_dialog != null) {
            save_dialog.cancel();
            save_dialog = null;
        }
        if(route_options_dialog != null) {
            route_options_dialog.dismiss();
            route_options_dialog = null;
        }
        if(error_dialog != null) {
            error_dialog.dismiss();
            error_dialog = null;
        }
        if(info_dialog != null) {
            info_dialog.dismiss();
            info_dialog = null;
        }
        if(loading_dialog != null) {
            loading_dialog.dismiss();
            loading_dialog = null;
        }
    }

    /**
     * Retrieves the information of a path
     *
     * @param path_name the path to search for
     * @return the information of that path
     */
    private String getInfoFor(String path_name) {
        Stolperpfad road = getCurrentRoute(path_name);
        if(road == null) {
            return "Keine Informationen gefunden...";
        }
        return "Zeit: " + road.getTime() + "\nStart: " + road.getBasicStart() + "\nAnzahl Steine: " + road.getStoneCount();
    }

    /**
     * Highlights the input text in path names that match this string
     *
     * @param path_name the name of a saved route
     * @param search_by the user input
     * @return the highlighted version of the path name
     */
    private String formatText(String path_name, String search_by) {
        int index = path_name.indexOf(search_by);
        String ret;
        if(index >= 0 && index + search_by.length() < path_name.length()){
            ret = path_name.substring(0, index) + "<b>" + path_name.substring(index, index+search_by.length() + 1) + "</b>" + path_name.substring(index+search_by.length() + 1);
        } else {
            ret = path_name;
        }
        return ret;
    }

    /**
     * Finds the saved route with the same name the user has put in the search field
     *
     * @param user_input the user input
     * @return the corresponding path
     */
    private Stolperpfad getCurrentRoute(String user_input) {
        if(saved_paths == null) {
            return null;
        }
        if(user_input == null || user_input.length() == 0) {
            if(current_file_name == null || current_file_name.length() == 0) {
                return null;
            }
            user_input = current_file_name;
        }
        for(Stolperpfad path : saved_paths) {
            if(path.getName().equalsIgnoreCase(user_input)) {
                return path;
            }
        }
        return null;
    }

    /**
     * Starts the route guide on the map
     */
    public void startGuide() {
        map_quest.startGuide();
    }

    /**
     * Prepares the route calculations on the map
     *
     * @param start_choice the chosen start of the next route
     * @param end_choice the chosen end of the next route
     * @param time_choice the time the user has for the next route
     */
    public void calcRoute(String start_choice, String end_choice, String time_choice) {
        closeDialogs();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Route wird berechnet");
        builder.setMessage("Angegebene Zeit: " + time_choice);
        builder.setCancelable(false);
        loading_dialog = builder.create();
        loading_dialog.show();
        map_quest.createRoute(start_choice.startsWith("#") ? "-1" : start_choice,
                end_choice.startsWith("#") ? "-1" : end_choice,
                time_choice.startsWith("#") ? "-1" : time_choice);
    }

    /**
     * Animates the opening and closing of the bottom menu on the map
     */
    public void toggleMenu() {
        View toolbar = aq.id(R.id.map_toolbar).getView();
        View menu_open = aq.id(R.id.menu_open_button).getView();
        if(animating) {
            return;
        }
        if(menu_up) {
            toolbar.animate().translationY(toolbar.getHeight()).alpha(NO_ALPHA).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    toolbar.setVisibility(View.GONE);
                }
            });
            menu_open.animate().rotation(NO_ROTATION).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    animating = false;
                }
            });
        } else {
            menu_open.animate().rotation(HALF_ROTATION).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                }
            });
            toolbar.setAlpha(NO_ALPHA);
            toolbar.setVisibility(View.VISIBLE);
            toolbar.animate().translationY(0).alpha(FULL_ALPHA).setListener(new AnimatorListenerAdapter() {
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

    /**
     * Highlights the guide button and makes it usable
     */
    public void activatePathGuide() {
        aq.id(R.id.start_guide_button).getView().getBackground().setTint(getResources().getColor(R.color.colorAccentLightMode, null));
    }

    /**
     * Deactivates the guide button
     */
    public void deactivateGuide(){
        if(StolperpfadeApplication.getInstance().isDarkMode()) {
            aq.id(R.id.start_guide_button).getView().getBackground().setTint(getResources().getColor(R.color.colorPrimaryDarkMode, null));
            ImageButton guide_button = (ImageButton) aq.id(R.id.start_guide_button).getView();
            guide_button.getDrawable().setTint(getResources().getColor(R.color.colorPrimaryContrastDarkMode, null));
        } else {
            aq.id(R.id.start_guide_button).getView().getBackground().setTint(getResources().getColor(R.color.colorPrimaryLightMode, null));
            ImageButton guide_button = (ImageButton) aq.id(R.id.start_guide_button).getView();
            guide_button.getDrawable().setTint(getResources().getColor(R.color.colorPrimaryContrastLightMode, null));
        }
    }

    /**
     * A helper task that loads the routes saved in the external storage
     */
    @SuppressLint("StaticFieldLeak")
    private class LoadPathsTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            ArrayList<JSONObject> paths_as_json = DataFromJSON.loadAllJSONFromExternalDirectory("paths");
            saved_paths = new ArrayList<>();
            for(JSONObject path : paths_as_json) {
                saved_paths.add(Stolperpfad.newFromJson(path));
            }
            return null;
        }
    }

}
