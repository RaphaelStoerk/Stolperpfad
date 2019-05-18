package de.uni_ulm.ismm.stolperpfad.map_activities.view;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import java.util.Objects;
import de.uni_ulm.ismm.stolperpfad.R;
import de.uni_ulm.ismm.stolperpfad.StolperpfadeApplication;
import de.uni_ulm.ismm.stolperpfad.map_activities.control.RoutePlannerActivity;

/**
 * This class represents the contents of the route options dialog
 */
@SuppressLint("InflateParams")
public class RouteOptionsFragment extends Fragment {

    public static final int START_CHOICE_GPS = 0;
    public static final int START_CHOICE_MAP = 1;
    public static final int START_CHOICE_CTR = 2;
    public static final int END_CHOICE_STN = 0;
    public static final int END_CHOICE_MAP = 1;
    public static final int END_CHOICE_CTR = 2;
    public static final int CHOICE_NAN = -1;

    private int position;

    public RouteOptionsFragment() {
        // required empty constructor
    }

    public static RouteOptionsFragment newInstance(int position) {
        RouteOptionsFragment ret =  new RouteOptionsFragment();
        ret.position = position;
        return ret;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup root;
        switch(position) {
            case 0:
                root = (ViewGroup) createFirstPage(inflater);
                break;
            case 1:
                root = (ViewGroup) createSecondPage(inflater);
                break;
            case 2:
                root = (ViewGroup) createThirdPage(inflater);
                break;
            case 3:
                root = (ViewGroup) createSummaryPage(inflater);
                break;
            default:
                root = (ViewGroup) super.onCreateView(inflater, container, savedInstanceState);
        }
        return root;
    }

    /**
     * Determines what choice id corresponds to which button id
     *
     * @param button_id the checked radio button
     * @return the corresponding choice value
     */
    private int getChoiceFromId(int button_id) {
        switch(button_id) {
            case R.id.start_at_center:
                return START_CHOICE_CTR;
            case R.id.start_at_marker:
                return START_CHOICE_MAP;
            case R.id.start_at_position:
                return START_CHOICE_GPS;
            case R.id.end_at_marker:
                return END_CHOICE_MAP;
            case R.id.end_at_center:
                return END_CHOICE_CTR;
            case R.id.end_at_stone:
                return END_CHOICE_STN;
            default:
                return CHOICE_NAN;
        }
    }

    /**
     * Creates the view content for the first page on the dialog containing the start choice
     *
     * @param inflater the dialog layout inflater
     * @return the view with the created content
     */
    private View createFirstPage(LayoutInflater inflater) {
        // build the start and end position options
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.content_options_one, null);
        RadioGroup start_choice = root.findViewById(R.id.start_of_route_choice);
        start_choice.setOnCheckedChangeListener((RadioGroup radioGroup, int button_id)
                -> StolperpfadeApplication.getInstance().saveStringInPreferences("route_start", "" + getChoiceFromId(button_id)));
        return root;
    }

    /**
     * Creates the view content for the second page on the dialog containing the end choice
     *
     * @param inflater the dialog layout inflater
     * @return the view with the created content
     */
    private View createSecondPage(LayoutInflater inflater) {
        // build the start and end position options
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.content_options_two, null);
        RadioGroup end_choice = root.findViewById(R.id.end_of_route_choice);
        end_choice.setOnCheckedChangeListener((RadioGroup radioGroup, int button_id)
                -> StolperpfadeApplication.getInstance().saveStringInPreferences("route_end", "" + getChoiceFromId(button_id)));
        return root;
    }

    /**
     * Creates the view content for the third page on the dialog containing the time choice
     *
     * @param inflater the dialog layout inflater
     * @return the view with the created content
     */
    private View createThirdPage(LayoutInflater inflater) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.content_options_three, null);
        EditText time_input = root.findViewById(R.id.time_input);
        time_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { /*---*/}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { /*---*/ }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.toString().length() > 0) {
                    StolperpfadeApplication.getInstance().saveStringInPreferences("route_time", editable.toString());
                } else {
                    StolperpfadeApplication.getInstance().saveStringInPreferences("route_time","#");
                }
            }
        });
        return root;
    }

    /**
     * Grabs the chosen values for the next route and creates a summary page for the user
     *
     * @param inflater the inflater used to inflate the layout
     * @return the root view of the summary page
     */
    @SuppressLint("SetTextI18n")
    private View createSummaryPage(LayoutInflater inflater) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.options_summary, null);
        ConstraintLayout summary_container = root.findViewById(R.id.summary);
        String[] chosen_values = StolperpfadeApplication.getInstance().getValuesFromPreferences("route_start", "route_end", "route_time");
        TextView start_choice = summary_container.findViewById(R.id.summary_start);
        switch(chosen_values[0]) {
            case "0":
                start_choice.setText(getResources().getString(R.string.route_dialog_start_pos));
                break;
            case "1":
                start_choice.setText(getResources().getString(R.string.route_dialog_start_mrk));
                break;
            case "2":
                start_choice.setText(getResources().getString(R.string.route_dialog_start_ctr));
                break;
            default:
                start_choice.setText(getResources().getString(R.string.route_dialog_start_def));
        }
        TextView end_choice = summary_container.findViewById(R.id.summary_end);
        switch(chosen_values[1]) {
            case "0":
                end_choice.setText(getResources().getString(R.string.route_dialog_end_pos));
                break;
            case "1":
                end_choice.setText(getResources().getString(R.string.route_dialog_end_mrk));
                break;
            case "2":
                end_choice.setText(getResources().getString(R.string.route_dialog_end_ctr));
                break;
            default:
                end_choice.setText(getResources().getString(R.string.route_dialog_end_def));
        }
        TextView time_choice = summary_container.findViewById(R.id.summary_time);
        if(!chosen_values[2].startsWith("#") && chosen_values[2].length() > 0) {
            time_choice.setText("Der Pfad sollte in " + chosen_values[2] + " Minuten zu schaffen sein.");
        } else {
            time_choice.setText(" ");
        }
        summary_container.findViewById(R.id.route_option_button_pos).setOnClickListener(
                view -> ((RoutePlannerActivity) Objects.requireNonNull(getActivity())).calcRoute(chosen_values[0], chosen_values[1], chosen_values[2])
        );
        summary_container.findViewById(R.id.route_option_button_neg).setOnClickListener(
                view -> ((RoutePlannerActivity) Objects.requireNonNull(getActivity())).closeDialogs()
        );
        return root;
    }
}