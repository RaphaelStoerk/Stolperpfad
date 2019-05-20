package de.uni_ulm.ismm.stolperpfad.general;

import android.Manifest;
import android.app.AlertDialog;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresPermission;

import com.mapbox.android.core.location.LocationEngineListener;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapboxMap;

import de.uni_ulm.ismm.stolperpfad.map_activities.model.StoneOnMap;
import de.uni_ulm.ismm.stolperpfad.map_activities.view.MapQuestFragment;

/**
 * The interface class for the map listeners
 */
public class MapActionsListener implements MapboxMap.OnInfoWindowClickListener, MapboxMap.OnMapLongClickListener, LocationEngineListener {

    private MapQuestFragment map_fragment;

    public MapActionsListener(MapQuestFragment map_fragment) {
        super();
        this.map_fragment = map_fragment;
    }

    /**
     * From OnInfoWindowClickListener Interface, handles click on the info window
     * of the markers
     *
     * @param marker_of_info_window The marker from which the info window has been clicked on
     * @return true, if the marker represents a stone, else return false
     */
    @Override
    public boolean onInfoWindowClick(@NonNull Marker marker_of_info_window) {
        StoneOnMap possible_stone = map_fragment.getStoneHandler().getStoneFromMarker(marker_of_info_window);
        if (possible_stone == null) {
            if (map_fragment.isStartMarker(marker_of_info_window)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(map_fragment.getContext());
                builder.setTitle("Start Markierung löschen?");
                builder.setPositiveButton("Ja", (dialogInterface, i) -> map_fragment.removeStartMarker());
                builder.setNegativeButton("Nein", (dialogInterface, i) -> { /*---*/ });
                // Create the AlertDialog
                AlertDialog dialog = builder.create();
                dialog.show();
            } else if (map_fragment.isEndMarker(marker_of_info_window)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(map_fragment.getContext());
                builder.setTitle("End Markierung löschen?");
                builder.setPositiveButton("Ja", (dialogInterface, i) -> map_fragment.removeEndMarker());
                builder.setNegativeButton("Nein", (dialogInterface, i) -> { /*---*/ });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
            return false;
        }
        possible_stone.showDialog(map_fragment);
        return true;
    }

    /**
     * From OnMapLongClick Interface, if the user clicks long on the map
     * a dialog will be presented letting the user chose if a new marker should be placed
     * at this position
     *
     * @param clicked_location the position where the user can place a marker
     */
    @Override
    public void onMapLongClick(@NonNull LatLng clicked_location) {
        AlertDialog.Builder builder = new AlertDialog.Builder(map_fragment.getContext());
        String[] choice = new String[]{"Hier Start der Route setzen", "Hier Ende der Route setzen", "Zurück"};
        builder.setTitle("Auswahl festlegen als:")
                .setItems(choice, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            map_fragment.setStartOrEndMarker(clicked_location, true);
                            break;
                        case 1:
                            map_fragment.setStartOrEndMarker(clicked_location, false);
                            break;
                        default:
                    }
                });
        // Create the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * From the LocationEngineListener, gets called when the listener is connected and
     * forces a user location update
     */
    @RequiresPermission(anyOf = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    @Override
    public void onConnected() {
        map_fragment.forceUserLocationUpdate();
    }

    /**
     * From the LocationEngineListener, gets called when the user location changes, updates
     * the user location on the map
     */
    @RequiresPermission(anyOf = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    @Override
    public void onLocationChanged(Location location) {
        map_fragment.setUserLocation(location);
    }
}
