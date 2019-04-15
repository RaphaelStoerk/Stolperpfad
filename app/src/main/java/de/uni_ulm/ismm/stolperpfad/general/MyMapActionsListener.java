package de.uni_ulm.ismm.stolperpfad.general;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresPermission;
import android.support.v4.app.ActivityCompat;

import com.mapbox.android.core.location.LocationEngineListener;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapboxMap;

import de.uni_ulm.ismm.stolperpfad.info_display.ScrollingInfoActivity;
import de.uni_ulm.ismm.stolperpfad.map_activities.model.Stone;
import de.uni_ulm.ismm.stolperpfad.map_activities.view.MapQuestFragment;

public class MyMapActionsListener implements MapboxMap.OnInfoWindowClickListener, MapboxMap.OnMapLongClickListener, LocationEngineListener {

    private MapQuestFragment myMapFragment;

    public MyMapActionsListener(MapQuestFragment myMapFragment) {
        super();
        this.myMapFragment = myMapFragment;
    }

    /**
     * From OnInfoWindowClickListener Interface, handles click on the info window
     * of the markers
     * @param marker The marker from which the info window has been clicked on
     * @return true, if the marker represents a stone, else return false
     */
    @Override
    public boolean onInfoWindowClick(@NonNull Marker marker) {
        if (myMapFragment.isNearestMarkerToUser(marker)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(myMapFragment.getContext());

            builder.setTitle("Zu diesem Stein führen lassen?");

            builder.setPositiveButton("Ja", (dialogInterface, i) -> {

                Context ctx = myMapFragment.getActivity().getApplicationContext();

                if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_COARSE_LOCATION)
                                != PackageManager.PERMISSION_GRANTED) {
                    dialogInterface.cancel();
                }
                myMapFragment.createRouteToNext();
                dialogInterface.cancel();
            });
            builder.setNegativeButton("Nein", (dialogInterface, i) -> {
                dialogInterface.cancel();
                AlertDialog.Builder builder2 = new AlertDialog.Builder(myMapFragment.getContext());

                builder2.setTitle("Informationen zu diesem Stein anzeigen?");

                builder2.setPositiveButton("Ja", (dialogInterface1, i1) -> {
                    myMapFragment.getMapboxMap().deselectMarker(marker);
                    dialogInterface1.cancel();
                    Intent intent = new Intent(myMapFragment.getActivity(), ScrollingInfoActivity.class);
                    intent.setAction(myMapFragment.getStoneHandler().getStoneFromMarker(marker).toString());
                    myMapFragment.startActivity(intent);
                });
                builder2.setNegativeButton("Nein", (dialogInterface1, i1) -> {
                    dialogInterface1.cancel();
                });

                // Create the AlertDialog
                AlertDialog dialog = builder2.create();
                dialog.show();
            });

            // Create the AlertDialog
            AlertDialog dialog = builder.create();
            dialog.show();
            return true;
        }
        Stone check = myMapFragment.getStoneHandler().getStoneFromMarker(marker);
        if (check == null) {
            if (myMapFragment.isStartMarker(marker)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(myMapFragment.getContext());

                builder.setTitle("Start Markierung löschen?");

                builder.setPositiveButton("Ja", (dialogInterface, i) -> {
                    myMapFragment.removeStartMarker();
                });
                builder.setNegativeButton("Nein", (dialogInterface, i) -> {
                });

                // Create the AlertDialog
                AlertDialog dialog = builder.create();
                dialog.show();
            } else if (myMapFragment.isEndMarker(marker)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(myMapFragment.getContext());

                builder.setTitle("End Markierung löschen?");

                builder.setPositiveButton("Ja", (dialogInterface, i) -> {
                    myMapFragment.removeEndMarker();
                });
                builder.setNegativeButton("Nein", (dialogInterface, i) -> {
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
            return false;
        }
        Intent intent = new Intent(myMapFragment.getActivity(), ScrollingInfoActivity.class);
        intent.setAction(check.toString());
        myMapFragment.startActivity(intent);
        return true;
    }

    /**
     * From OnMapLongClick Interface, if the user clicks long on the map
     * a dialog will be presented letting the user chose if a new marker should be placed
     * at this position
     *
     * @param point the position where the user can place a marker
     */
    @Override
    public void onMapLongClick(@NonNull LatLng point) {
        AlertDialog.Builder builder = new AlertDialog.Builder(myMapFragment.getContext());
        String[] choice = new String[]{"Hier Start der Route setzen", "Hier Ende der Route setzen", "Zurück"};
        builder.setTitle("Auswahl festlegen als:")
                .setItems(choice, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            myMapFragment.setStartOrEndMarker(point, true);
                            break;
                        case 1:
                            myMapFragment.setStartOrEndMarker(point, false);
                            break;
                        default:
                    }
                });

        // Create the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }



    @RequiresPermission(anyOf = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    @Override
    public void onConnected() {
        myMapFragment.forceUserLocationUpdate();
    }

    @RequiresPermission(anyOf = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    @Override
    public void onLocationChanged(Location location) {
        myMapFragment.setUserLocation(location);
    }
}
