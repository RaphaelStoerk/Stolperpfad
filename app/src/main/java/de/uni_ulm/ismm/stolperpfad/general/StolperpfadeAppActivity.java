package de.uni_ulm.ismm.stolperpfad.general;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;

import com.androidquery.AQuery;

import java.util.List;

import de.uni_ulm.ismm.stolperpfad.R;
import de.uni_ulm.ismm.stolperpfad.StolperpfadeApplication;
import de.uni_ulm.ismm.stolperpfad.database.StolperpfadeRepository;
import de.uni_ulm.ismm.stolperpfad.database.data.HistoricalTerm;
import de.uni_ulm.ismm.stolperpfad.database.data.Person;
import de.uni_ulm.ismm.stolperpfad.info_display.history.HistoInfoActivity;
import de.uni_ulm.ismm.stolperpfad.info_display.stone_info.StoneInfoMainActivity;
import de.uni_ulm.ismm.stolperpfad.scanner.ScannerActivity;

/**
 * This class represents a general activity for this application. It contains methods for creating
 * an activity and handling inputs from the user
 */
public abstract class StolperpfadeAppActivity extends AppCompatActivity {

    public final float HEADER_TRANSLATION_Z = 8;
    protected int current_layout;
    protected boolean currently_in_dark_mode = false;
    protected AQuery aq;
    protected AppClickListener<StolperpfadeAppActivity> my_click_listener;
    protected SearchForTagTask search_tag_and_redirect_task;
    private AlertDialog quick_access_dialog;

    /**
     * Display the quick acces menu that can be accessed from every activity via the header
     */
    @SuppressLint("InflateParams")
    public void showQuickAccesMenu() {
        // setup the quick_access_dialog building process
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.DialogTheme_Light);
        LayoutInflater inflater = getLayoutInflater();
        View quick_access_dialog_view = inflater.inflate(R.layout.dialog_quick_access_menu, null);
        builder.setView(quick_access_dialog_view);

        toggleDarkMode(quick_access_dialog_view.findViewById(R.id.dark_mode_switch), false, StolperpfadeApplication.getInstance().isDarkMode());

        // set the action listener for all buttons
        quick_access_dialog_view.findViewById(R.id.quick_access_route_planner).setOnClickListener(my_click_listener);
        quick_access_dialog_view.findViewById(R.id.quick_access_next_stone_button).setOnClickListener(my_click_listener);
        quick_access_dialog_view.findViewById(R.id.quick_access_historical_info).setOnClickListener(my_click_listener);
        quick_access_dialog_view.findViewById(R.id.quick_access_stone_info).setOnClickListener(my_click_listener);
        quick_access_dialog_view.findViewById(R.id.quick_access_privacy).setOnClickListener(my_click_listener);
        quick_access_dialog_view.findViewById(R.id.quick_access_scanner_button).setOnClickListener(my_click_listener);
        quick_access_dialog_view.findViewById(R.id.quick_access_impressum).setOnClickListener(my_click_listener);
        quick_access_dialog_view.findViewById(R.id.dark_mode_switch).setOnClickListener(my_click_listener);
        quick_access_dialog_view.findViewById(R.id.dark_mode_text).setOnClickListener(my_click_listener);
        quick_access_dialog_view.findViewById(R.id.quick_access_project_artist).setOnClickListener(my_click_listener);
        quick_access_dialog_view.setFocusable(true);
        quick_access_dialog_view.requestFocus();

        // create and display the quick_access_dialog
        quick_access_dialog = builder.create();
        quick_access_dialog_view.findViewById(R.id.quick_acces_close).setOnClickListener(view -> quick_access_dialog.cancel());
        quick_access_dialog.show();
    }

    /**
     * Changes the current theme from or to dark mode
     *
     * @param theme_switch the dark mode switch button
     * @param toggle true, if dark mode should be toggled or set
     * @param is_now_dark_mode true, if the app is currently in dark mode
     */
    @SuppressLint("ResourceType")
    private void toggleDarkMode(Switch theme_switch, boolean toggle, boolean is_now_dark_mode) {
        int[] attr = {R.attr.colorAppPrimary, R.attr.colorAppAccent};
        TypedArray ta = this.obtainStyledAttributes(attr);
        int color;
        if(is_now_dark_mode){
            color = ta.getResourceId(1, android.R.color.black);
        } else {
            color = ta.getResourceId(0, android.R.color.black);
        }
        theme_switch.getThumbDrawable().setTint(getResources().getColor(color, getTheme()));
        theme_switch.getTrackDrawable().setTint(getResources().getColor(color, getTheme()));
        theme_switch.setChecked(is_now_dark_mode);
        StolperpfadeApplication.getInstance().setDarkMode(currently_in_dark_mode = is_now_dark_mode);
        ta.recycle();
        if(toggle) {
            recreate();
        }
    }

    /**
     * Helper method for toggling dark mode
     * @param theme_switch the dark mode switch button
     * @param toggle true, if dark mode should be toggled or set
     */
    public void toggleDarkMode(Switch theme_switch, boolean toggle) {
        if(theme_switch == null) {
            theme_switch = quick_access_dialog.findViewById(R.id.dark_mode_switch);
        }
        toggleDarkMode(theme_switch, toggle, !StolperpfadeApplication.getInstance().isDarkMode());
    }

    /**
     * wrapper method to cancel the current quick access quick_access_dialog
     */
    public void endQuickAccesDialog() {
        if(quick_access_dialog != null) {
            quick_access_dialog.cancel();
            quick_access_dialog = null;
        }
    }

    /**
     * The general onCreate method for all activities, sets the current theme
     *
     * @param saved_state the saved instance state
     */
    @Override
    protected void onCreate(@Nullable Bundle saved_state) {
        if(StolperpfadeApplication.getInstance().isDarkMode()) {
            setTheme(R.style.AppTheme_Dark);
            currently_in_dark_mode = true;
        } else {
            setTheme(R.style.AppTheme_Light);
            currently_in_dark_mode = false;
        }
        super.onCreate(saved_state);
    }

    /**
     * Checks if the theme of the app has changed and recreates this activity if necessary
     */
    @Override
    public void onResume() {
        super.onResume();
        if(StolperpfadeApplication.getInstance().isDarkMode() != currently_in_dark_mode) {
            currently_in_dark_mode = !currently_in_dark_mode;
            recreate();
        }
        setVisible(true);
    }

    /**
     * the general method for setting up the controls of the header of the app, similar for all activities
     *
     * @param current_layout the layout for the current activity
     */
    protected void initializeGeneralControls(@LayoutRes int current_layout) {
        // Initialize important helper-Objects
        setContentView(this.current_layout = current_layout);

        aq = new AQuery(this);

        // Initialize the Listener for the clickable items
        my_click_listener = new AppClickListener<>();
        my_click_listener.setParent_activity(this);

        // add the listener to the items
        aq.id(R.id.quick_access_button).visible().clicked(my_click_listener);
        aq.id(R.id.header_image).visible().clicked(my_click_listener);
        aq.id(R.id.header).getView().setTranslationZ(HEADER_TRANSLATION_Z);

        Button back_button = aq.id(R.id.button_back).getButton();
        if(back_button != null) {
            aq.id(R.id.button_back).visible().clicked(my_click_listener);
        }
    }

    /**
     * This method handels clicks on links in the bio and history information activities, redirects
     * the application to the specified info page, if a known tag can be found in s
     *
     * @param tag the text that has been clicked on
     */
    @SuppressLint("StaticFieldLeak")
    public void reactToLink(String tag) {
        new RedirectToInfoPageTask() {
            @Override
            protected void onPostExecute(Object object) {
                if(object instanceof Person) {
                    // redirect to person info page
                    int id = ((Person) object).getPersId();
                    Intent intent = new Intent(StolperpfadeAppActivity.this, StoneInfoMainActivity.class);
                    intent.setAction("" + id);
                    Bundle transition_options = ActivityOptions.makeSceneTransitionAnimation(StolperpfadeAppActivity.this).toBundle();
                    startActivity(intent, transition_options);
                } else if(object instanceof HistoricalTerm) {
                    // redirect to historical info page
                    String name = ((HistoricalTerm) object).getName();
                    Intent intent = new Intent(StolperpfadeAppActivity.this, HistoInfoActivity.class);
                    intent.putExtra("termName", name);
                    Bundle transition_options = ActivityOptions.makeSceneTransitionAnimation(StolperpfadeAppActivity.this).toBundle();
                    startActivity(intent, transition_options);
                }
            }
        }.execute(tag);
    }

    /**
     * This method is called, when the user has scanned an image from the scanner activity,
     * a task will start that tries to find a persons name in the scanned text and then proceeds
     * to redirect the application to this persons info page
     *
     * @param bulk_text the text that the scanner has found in an image
     */
    @SuppressLint("StaticFieldLeak")
    protected void tryToRedirect(String bulk_text) {
        ScannerActivity scanner_activity;
        if(this instanceof ScannerActivity) {
            scanner_activity = ((ScannerActivity) StolperpfadeAppActivity.this);
        } else {
            return;
        }
        search_tag_and_redirect_task = new SearchForTagTask() {
            @Override
            protected void onPostExecute(Object object) {
                if(object instanceof Person) {
                    // a person has been found, redirect
                    int id = ((Person) object).getPersId();
                    Intent intent = new Intent(scanner_activity, StoneInfoMainActivity.class);
                    intent.setAction("" + id);
                    Bundle transition_options = ActivityOptions.makeSceneTransitionAnimation(scanner_activity).toBundle();
                    scanner_activity.endDialog();
                    startActivity(intent, transition_options);
                } else {
                    scanner_activity.error();
                }
            }
        };
        search_tag_and_redirect_task.execute(bulk_text);
    }

    /**
     * An AsyncTask that tries to redirect the application to an information screen for
     * a person or historical event/place, called when the user clicked on a text link
     */
    @SuppressLint("StaticFieldLeak")
    private class RedirectToInfoPageTask extends AsyncTask<String, Void, Object> {
        @Override
        protected Object doInBackground(String... strings) {
            if(strings == null || strings.length == 0) {
                return null;
            }
            String tag = strings[0];
            StolperpfadeRepository repo = new StolperpfadeRepository(getApplication());
            List<Person> persons = repo.getAllPersons();
            List<HistoricalTerm> terms = repo.getAllTerms();
            // try to find a person
            for(Person p : persons) {
                if(p.getEntireName().equalsIgnoreCase(tag)) {
                    return p;
                }
            }
            // try to find a historical tag
            for(HistoricalTerm h : terms) {
                if(h.getName().equalsIgnoreCase(tag)) {
                    return h;
                }
            }
            return null;
        }
    }

    /**
     * An AsyncTask that tries to find a persons name in a text that a scanner activity has read in
     */
    @SuppressLint("StaticFieldLeak")
    protected class SearchForTagTask extends AsyncTask<String, Void, Object> {
        @Override
        protected Object doInBackground(String... strings) {
            if(strings == null || strings.length == 0) {
                return null;
            }
            String bulk = strings[0].toLowerCase().replace("\n", "");
            StolperpfadeRepository repo = new StolperpfadeRepository(getApplication());
            List<Person> persons = repo.getAllPersons();
            Person best_guess = null;
            // try to find a persons name in the text
            for(Person p : persons) {
                if(bulk.contains(p.getFamName().toLowerCase()) && bulk.contains(p.getFstName().toLowerCase())) {
                    return p;
                }
                if(bulk.contains(p.getFamName().toLowerCase()) || bulk.contains(p.getFstName().toLowerCase())) {
                    best_guess = p;
                }
            }
            return best_guess;
        }
    }
}
