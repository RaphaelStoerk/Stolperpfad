package de.uni_ulm.ismm.stolperpfad.map_activities.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import de.uni_ulm.ismm.stolperpfad.R;
import de.uni_ulm.ismm.stolperpfad.map_activities.control.RoutePlannerActivity;

public class RouteOptionsFragment extends Fragment {

    private int position;
    private SharedPreferences prefs;

    public static final int START_CHOICE_GPS = 0;
    public static final int START_CHOICE_MAP = 1;
    public static final int START_CHOICE_CTR = 2;
    public static final int START_CHOICE_NAN = -1;

    public static final int END_CHOICE_STN = 0;
    public static final int END_CHOICE_MAP = 1;
    public static final int END_CHOICE_CTR = 2;
    public static final int END_CHOICE_NAN = -1;

    public RouteOptionsFragment() {

    }

    public static RouteOptionsFragment newInstance(int position) {
        RouteOptionsFragment ret =  new RouteOptionsFragment();
        Log.i("MY_DIALOG_TAG", "New fragment at pos: " + position);
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
        ViewGroup root = null;
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

    private int getPosFromId(int i, boolean start) {
        switch(i) {
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
                return start ? START_CHOICE_NAN : END_CHOICE_NAN;
        }
    }

    private View createFirstPage(LayoutInflater inflater) {
        // build the start and end position options
        prefs = this.getActivity().getApplicationContext().getSharedPreferences(
                "de.uni_ulm.ismm.stolperpfad", Context.MODE_PRIVATE);
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.content_options_one, null);

        RadioGroup start_choice = root.findViewById(R.id.start_of_route_choice);
        start_choice.setOnCheckedChangeListener((RadioGroup radioGroup, int i) -> {
            prefs.edit().putString("de.uni_ulm.ismm.stolperpfad.route_start", "" + getPosFromId(i, true)).apply();
        });
        return root;
    }

    private View createSecondPage(LayoutInflater inflater) {
        // build the start and end position options
        prefs = this.getActivity().getApplicationContext().getSharedPreferences(
                "de.uni_ulm.ismm.stolperpfad", Context.MODE_PRIVATE);
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.content_options_two, null);

        RadioGroup end_choice = root.findViewById(R.id.end_of_route_choice);
        end_choice.setOnCheckedChangeListener((RadioGroup radioGroup, int i) -> {
            prefs.edit().putString("de.uni_ulm.ismm.stolperpfad.route_end", "" + getPosFromId(i, false)).apply();
        });
        return root;
    }

    private View createThirdPage(LayoutInflater inflater) {
        prefs = this.getActivity().getApplicationContext().getSharedPreferences(
                "de.uni_ulm.ismm.stolperpfad", Context.MODE_PRIVATE);

        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.content_options_three, null);
        EditText time_input = root.findViewById(R.id.time_input);

        time_input.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.toString().length() > 0) {
                    prefs.edit().putString("de.uni_ulm.ismm.stolperpfad.route_time", editable.toString()).apply();
                } else {
                    prefs.edit().putString("de.uni_ulm.ismm.stolperpfad.route_time", "#").apply();
                }
            }
        });
        return root;
    }

    private View createSummaryPage(LayoutInflater inflater) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.options_summary, null);
        ConstraintLayout summary = root.findViewById(R.id.summary);

        prefs = this.getActivity().getApplicationContext().getSharedPreferences(
                "de.uni_ulm.ismm.stolperpfad", Context.MODE_PRIVATE);
        String start_option = prefs.getString("de.uni_ulm.ismm.stolperpfad.route_start", null);
        String end_option = prefs.getString("de.uni_ulm.ismm.stolperpfad.route_end", null);
        String time_option = prefs.getString("de.uni_ulm.ismm.stolperpfad.route_time", null);
        TextView start_choice = (TextView) summary.findViewById(R.id.summary_start);
        switch(start_option) {
            case "0":
                start_choice.setText("Pfad bei aktueller Position beginnen.");
                break;
            case "1":
                start_choice.setText("Pfad bei gesetzter Markierung beginnen.");
                break;
            case "2":
                start_choice.setText("Pfad am Ulmer Münster beginnen.");
                break;
            default:
                start_choice.setText("Keine Angaben zum Start des Pfades.");
        }
        TextView end_choice = (TextView) summary.findViewById(R.id.summary_end);
        switch(end_option) {
            case "0":
                end_choice.setText("Pfad endet bei einem Stolperstein.");
                break;
            case "1":
                end_choice.setText("Pfad endet bei gesetzter Markierung.");
                break;
            case "2":
                end_choice.setText("Pfad endet am Ulmer Münster.");
                break;
            default:
                end_choice.setText("Keine Angaben zum Ende des Pfades.");
        }

        TextView time_choice = (TextView) summary.findViewById(R.id.summary_time);
        if(!time_option.startsWith("#") && time_option.length() > 0) {
            time_choice.setText("Der Pfad sollte in " + time_option + " Minuten zu schaffen sein.");
        } else {
            time_choice.setText(" ");
        }

        summary.findViewById(R.id.route_option_button_pos).setOnClickListener(
                view -> ((RoutePlannerActivity)getActivity()).calcRoute(start_option, end_option, time_option)
                );

        summary.findViewById(R.id.route_option_button_neg).setOnClickListener(
                view -> ((RoutePlannerActivity)getActivity()).endDialog()
                );

        return root;
    }
}