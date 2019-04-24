package de.uni_ulm.ismm.stolperpfad.map_activities.model;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;

import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.maps.MapboxMap;

import java.util.ArrayList;
import de.uni_ulm.ismm.stolperpfad.map_activities.RoutingUtil;
import de.uni_ulm.ismm.stolperpfad.map_activities.view.MapQuestFragment;

/**
 * This class is responsible for handling the "Stolpersteine". It creates all Stones
 * from the database and keeps track of their locations and markers
 */
public class StoneFactory {

    private ArrayList<Stone> all_stones;
    private ArrayList<Marker> stone_markers;
    private MapQuestFragment map;
    private MapboxMap mapboxMap;
    private boolean is_ready;

    private StoneFactory(MapQuestFragment map, MapboxMap mapboxMap) {
        this.map = map;
        this.mapboxMap = mapboxMap;
        all_stones = new ArrayList<>();
        stone_markers = new ArrayList<>();

    }

    public static StoneFactory initialize(MapQuestFragment map, MapboxMap mapboxMap) {
        StoneFactory buff = new StoneFactory(map, mapboxMap);
        buff.start_initialization();
        return buff;
    }

    @SuppressLint("StaticFieldLeak")
    private void start_initialization() {

        new InitializeStonesTask() {
            @Override
            public void onPostExecute(String res) {
                is_ready = true;
                map.setStones();
            }
        }.execute();
    }

    public ArrayList<Marker> getMarkers() {
        return stone_markers;
    }

    public ArrayList<Stone> getStones() {
        return all_stones;
    }

    public boolean isReady() {
        return is_ready;
    }

    /**
     * Looks for the stone corresponding to a specific marker
     * TODO: maybe make this more efficient
     *
     * @param marker the marker for which a corresponding stone is returned
     *
     * @return the corresponding stone
     */
    public Stone getStoneFromMarker(Marker marker) {
        for (Stone s : all_stones) {
            if (s.getMarker(mapboxMap) == marker) {
                return s;
            }
        }
        return null;
    }

    /**
     * Calculates which stone is nearest to a given stone, distance wise. It does not yet
     * account for actual walking distance but rather just straight mathematical distance
     *
     * @param rel_stone the stone from which a nearest stone is looked for
     *
     * @return the stone that is nearest to rel_stone
     */
    public Stone getNearestTo(Stone rel_stone) {
        if (all_stones == null || all_stones.size() == 0) {
            return null;
        }
        Stone best = all_stones.get(0);
        double best_dist = -1;
        double curr_dist;
        for (Stone s : all_stones) {
            curr_dist = RoutingUtil.getDist(rel_stone, s);
            if (!s.equals(rel_stone) && (best_dist == -1 || curr_dist < best_dist)) {
                best_dist = curr_dist;
                best = s;
            }
        }
        return best;
    }

    /**
     * Calculates which stone is nearest to a given stone, distance wise. It does not yet
     * account for actual walking distance but rather just straight mathematical distance
     *
     * @param rel_stone the stone from which a nearest stone is looked for
     *
     * @return the stone that is nearest to rel_stone
     */
    public Marker getNearestTo(Marker rel_stone) {
        if (stone_markers == null || stone_markers.size() == 0 || rel_stone == null) {
            return null;
        }
        Marker best = stone_markers.get(0);
        double best_dist = -1;
        double curr_dist;
        for (Marker m : stone_markers) {
            curr_dist = RoutingUtil.getDist(rel_stone, m);
            if (!m.equals(rel_stone) && (best_dist == -1 || curr_dist < best_dist)) {
                best_dist = curr_dist;
                best = m;
            }
        }
        return best;
    }

    /**
     * This class is responsible for creating an asynchronous task that creates all the stones
     * from the data in the database, so they can later be displayed on the map
     */
    @SuppressLint("StaticFieldLeak")
    private class InitializeStonesTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... strings) {

            map.getActivity().runOnUiThread(() -> {
                // TODO: grab all stone info from the DataBase
                Stone s = new Stone(48.4011, 9.9876, "Vorname_1", "Nachname_1", "Bitte ersetzen 1");
                all_stones.add(s);
                stone_markers.add(s.getMarker(mapboxMap));

                s = new Stone( 48.39855, 9.99123, "Vorname_2", "Nachname_2", "Bitte ersetzen 2");
                all_stones.add(s);
                stone_markers.add(s.getMarker(mapboxMap));

                s = new Stone(48.3893, 9.98924, "Vorname_3", "Nachname_3", "Bitte ersetzen 3");
                all_stones.add(s);
                stone_markers.add(s.getMarker(mapboxMap));

                s = new Stone(48.40002, 9.99721, "Vorname_4", "Nachname_4", "Bitte ersetzen 4");
                all_stones.add(s);
                stone_markers.add(s.getMarker(mapboxMap));

                s = new Stone(48.40102, 9.99821, "Vorname_5", "Nachname_5", "Bitte ersetzen 5");
                all_stones.add(s);
                stone_markers.add(s.getMarker(mapboxMap));

            });
            return "Finished";
        }
    }
}
