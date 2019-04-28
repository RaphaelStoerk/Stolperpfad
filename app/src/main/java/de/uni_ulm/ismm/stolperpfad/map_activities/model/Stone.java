package de.uni_ulm.ismm.stolperpfad.map_activities.model;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Intent;
import android.text.Html;
import android.util.TypedValue;
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
import de.uni_ulm.ismm.stolperpfad.info_display.stone_info.StoneListActivity;
import de.uni_ulm.ismm.stolperpfad.info_display.stone_info.fragments.StoneListFragment;
import de.uni_ulm.ismm.stolperpfad.map_activities.view.MapQuestFragment;

/**
 * This is a model class to represent a "Stolperstein" on the map activities of this application
 */

public class Stone {

    private LatLng location;
    private int stoneId;
    private Marker marker;
    private Stolperstein stein;
    List<Person> persons;
    ArrayList<Neighbour> neighbours;


    public Stone(Stolperstein stein) {
        this.stoneId = stein.getStoneId();
        this.location = new LatLng(stein.getLatitude(), stein.getLongitude());
        this.neighbours = new ArrayList<>();
    }

    public Stone(Stolperstein stein, List<Person> persons_on_stone) {
        this.stein = stein;
        this.persons = persons_on_stone;
        this.location = new LatLng(stein.getLatitude(), stein.getLongitude());
        this.neighbours = new ArrayList<>();
        this.stoneId = stein.getStoneId();

    }

    /**
     * Creates a new marker for a given MapView with the current values of the Stone
     * or returns the earlier created Marker
     *
     * @return a Marker representing this Stone
     */
    public Marker getMarker(MapboxMap mapboxMap) {
        if (marker == null) {
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(location);
            if (persons != null && persons.size() == 1) {
                Person person = persons.get(0);
                markerOptions.title(person.getEntireName());
                markerOptions.snippet(stein.getAddress());
            } else if (persons != null && persons.size() > 1) {
                Person person = persons.get(0);
                markerOptions.title(person.getEntireName() + ", u. w.");
                markerOptions.snippet(stein.getAddress());
            }
            marker = mapboxMap.addMarker(markerOptions);
        }
        return marker;
    }

    /**
     * Returns the geographical position of this Stone as a GeoPoint
     *
     * @return the location of this Stone
     */
    public LatLng getLocation() {
        return location;
    }

    public int getStoneId() {
        return stoneId;
    }

    public String toString() {
        return marker == null ? "" : marker.getTitle();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Stone)) {
            return false;
        }
        Stone check = (Stone) o;
        return (this.location.getLatitude() == check.getLocation().getLatitude()) &&
                (this.location.getLongitude() == check.getLocation().getLongitude());
    }

    public boolean hasNeighbour(Stone s_to) {
        for (Neighbour n : neighbours) {
            if (n.getStone().equals(s_to)) {
                return true;
            }
        }
        return false;
    }

    public void addNeighbour(double shorest_dist, Stone nearest) {
        neighbours.add(new Neighbour(nearest, shorest_dist));
    }

    public int countNeighbours() {
        return neighbours.size();
    }

    public ArrayList<Neighbour> getNeighbours() {
        return neighbours;
    }

    public void showDialog(MapQuestFragment myMapFragment) {
        AlertDialog.Builder builder;
        if (StolperpfadeApplication.getInstance().isDarkMode()) {
            builder = new AlertDialog.Builder(myMapFragment.getContext(), R.style.DialogTheme_Dark);
        } else {
            builder = new AlertDialog.Builder(myMapFragment.getContext(), R.style.DialogTheme_Light);
        }
        // Get the layout inflater
        LayoutInflater inflater = myMapFragment.getLayoutInflater();

        View myDialogView = inflater.inflate(R.layout.dialog_stone_marker, null);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        LinearLayout list_layout = myDialogView.findViewById(R.id.marker_list_container);
        for (Person person : persons) {
            list_layout.addView(addButton(myMapFragment, person, persons.size() == 1));
        }
        TextView t = myDialogView.findViewById(R.id.title_marker);
        t.setText(stein.getAddress());
        builder.setView(myDialogView);
        AlertDialog dialog = builder.create();
        dialog.show();
        myDialogView.findViewById(R.id.marker_info_close).setOnClickListener(view -> {
            dialog.cancel();
        });
        myDialogView.findViewById(R.id.route_marker).setOnClickListener(view -> {
            dialog.cancel();
            myMapFragment.createRouteTo(this);
        });
    }


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
}
