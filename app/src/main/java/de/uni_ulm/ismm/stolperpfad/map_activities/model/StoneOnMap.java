package de.uni_ulm.ismm.stolperpfad.map_activities.model;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapboxMap;

import java.util.ArrayList;
import java.util.List;

import de.uni_ulm.ismm.stolperpfad.R;
import de.uni_ulm.ismm.stolperpfad.StolperpfadeApplication;
import de.uni_ulm.ismm.stolperpfad.database.data.Person;
import de.uni_ulm.ismm.stolperpfad.database.data.Stolperstein;
import de.uni_ulm.ismm.stolperpfad.info_display.stone_info.StoneInfoMainActivity;
import de.uni_ulm.ismm.stolperpfad.map_activities.view.MapQuestFragment;

/**
 * This is a model class to represent a "Stolperstein" on the map activities of this application
 */
@SuppressLint("InflateParams")
public class StoneOnMap {

    private int stoneId;
    private LatLng location;
    private Marker marker;
    private Stolperstein actual_stone;
    private List<Person> persons;
    private ArrayList<ReachableStone> reachable_stones;

    StoneOnMap(Stolperstein actual_stone, List<Person> persons_on_stone) {
        this.actual_stone = actual_stone;
        this.persons = persons_on_stone;
        this.location = new LatLng(actual_stone.getLatitude(), actual_stone.getLongitude());
        this.reachable_stones = new ArrayList<>();
        this.stoneId = actual_stone.getStoneId();

    }

    void addNeighbour(double shorest_dist, StoneOnMap nearest) {
        reachable_stones.add(new ReachableStone(nearest, shorest_dist));
    }

    /* GETTERS */

    /**
     * Creates a new marker for a given MapView with the current values of the StoneOnMap
     * or returns the earlier created Marker
     *
     * @return a Marker representing this StoneOnMap
     */
    public Marker getMarker(MapboxMap mapboxMap) {
        if (marker == null) {
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(location);
            if (persons != null && persons.size() == 1) {
                Person person = persons.get(0);
                markerOptions.title(person.getEntireName());
                markerOptions.snippet(actual_stone.getAddress());
            } else if (persons != null && persons.size() > 1) {
                Person person = persons.get(0);
                markerOptions.title(person.getEntireName() + ", u. w.");
                markerOptions.snippet(actual_stone.getAddress());
            }
            marker = mapboxMap.addMarker(markerOptions);
        }
        return marker;
    }

    int countNeighbours() {
        return reachable_stones.size();
    }

    LatLng getLocation() {
        return location;
    }

    public int getStoneId() {
        return stoneId;
    }

    ArrayList<ReachableStone> getReachableStones() {
        return reachable_stones;
    }

    boolean markedAsReachable(StoneOnMap possible_stone) {
        for (ReachableStone n : reachable_stones) {
            if (n.getStone().equals(possible_stone)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Displays the information dialog for one stone with buttons to get to the person info pages
     * or start a guide to this loaction
     *
     * @param map_fragment the map object
     */
    public void showDialog(MapQuestFragment map_fragment) {
        AlertDialog.Builder builder;
        if (StolperpfadeApplication.getInstance().isDarkMode()) {
            builder = new AlertDialog.Builder(map_fragment.getContext(), R.style.DialogTheme_Dark);
        } else {
            builder = new AlertDialog.Builder(map_fragment.getContext(), R.style.DialogTheme_Light);
        }
        LayoutInflater inflater = map_fragment.getLayoutInflater();
        View stone_dialog_view = inflater.inflate(R.layout.dialog_stone_marker, null);
        LinearLayout list_layout = stone_dialog_view.findViewById(R.id.marker_list_container);
        for (Person person : persons) {
            list_layout.addView(addButton(map_fragment, person, persons.size() == 1));
        }
        TextView title_of_dialog = stone_dialog_view.findViewById(R.id.title_marker);
        title_of_dialog.setText(actual_stone.getAddress());
        builder.setView(stone_dialog_view);
        AlertDialog dialog = builder.create();
        dialog.show();
        stone_dialog_view.findViewById(R.id.marker_info_close).setOnClickListener(view -> dialog.cancel());
        stone_dialog_view.findViewById(R.id.route_marker).setOnClickListener(view -> {
            dialog.cancel();
            map_fragment.createRouteTo(this);
        });
    }

    /**
     * Creates a button for the stone info dialog that can rediredt the app to the info page
     * of a person
     *
     * @param fragment the parent fragment
     * @param person a person from this stone
     * @param only_one true, if the person is the only one from that stone position
     * @return a new button for the stone info dialog
     */
    private Button addButton(MapQuestFragment fragment, Person person, boolean only_one) {
        Button but = (Button) LayoutInflater.from(fragment.getContext()).inflate(R.layout.button_person_list, null);
        but.setOnClickListener(view -> {
            Intent intent = new Intent(fragment.getActivity(), StoneInfoMainActivity.class);
            intent.setAction("" + person.getPersId());
            fragment.startActivity(intent);
        });
        but.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        String display_name;
        if(only_one) {
            display_name = "<b>" + person.getEntireName() + "</b>";
        } else {
            display_name = person.getFormattedListName();
        }
        but.setText(Html.fromHtml(display_name));
        return but;
    }

    public String toString() {
        return marker == null ? "" : marker.getTitle();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof StoneOnMap)) {
            return false;
        }
        StoneOnMap check = (StoneOnMap) o;
        return (this.location.getLatitude() == check.getLocation().getLatitude()) &&
                (this.location.getLongitude() == check.getLocation().getLongitude());
    }
}
