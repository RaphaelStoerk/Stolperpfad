package de.uni_ulm.ismm.stolperpfad.map_activities;

import android.content.Context;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;

import com.androidquery.AQuery;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.*;
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay;

import de.uni_ulm.ismm.stolperpfad.R;

public class RoutePlannerActivity extends AppCompatActivity {

    MapView map = null;
    AQuery aq;
    private ScaleBarOverlay mScaleBarOverlay;
    private RotationGestureOverlay mRotationGestureOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        aq = new AQuery(this);

        //handle permissions first, before map is created. not depicted here

        //load/initialize the osmdroid configuration, this can be done
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        final DisplayMetrics dm = ctx.getResources().getDisplayMetrics();
        //setting this before the layout is inflated is a good idea
        //it 'should' ensure that the map has a writable location for the map cache, even without permissions
        //if no tiles are displayed, you can try overriding the cache path using Configuration.getInstance().setCachePath
        //see also StorageUtils
        //note, the load method also sets the HTTP User Agent to your application's package name, abusing osm's tile servers will get you banned based on this string

        //inflate and create the map
        setContentView(R.layout.activity_route_planner);

        map = findViewById(R.id.map);

        // MAPNIK is standard "style" for open street maps, other styles and overlays possible
        map.setTileSource(TileSourceFactory.MAPNIK);

        //deprecated:  map.setBuiltInZoomControls(true);

        IMapController mapController = map.getController();
        GeoPoint startPoint = new GeoPoint(48.4011, 9.9876);
        mapController.setCenter(startPoint);
        mapController.setZoom(14.);

        //support for map rotation
        mRotationGestureOverlay = new RotationGestureOverlay(map);
        mRotationGestureOverlay.setEnabled(true);
        map.getOverlays().add(this.mRotationGestureOverlay);

        //needed for pinch zooms
        map.setMultiTouchControls(true);

        //scales tiles to the current screen's DPI, helps with readability of labels
        map.setTilesScaledToDpi(true);

        map.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.NEVER);
    }

    public void onResume(){
        super.onResume();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        map.onResume(); //needed for compass, my location overlays, v6.0.0 and up
    }

    public void onPause(){
        super.onPause();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        map.onPause();  //needed for compass, my location overlays, v6.0.0 and up
    }
}
