package de.uni_ulm.ismm.stolperpfad.map_activities.model;

import android.os.AsyncTask;

import org.osmdroid.views.overlay.Marker;

import java.util.ArrayList;

import de.uni_ulm.ismm.stolperpfad.map_activities.RoutingUtil;
import de.uni_ulm.ismm.stolperpfad.map_activities.fragment.MapFragment;

public class StoneFactory {

    private ArrayList<Stone> all_stones;
    private ArrayList<Marker> stone_markers;
    private MapFragment map;
    private boolean is_ready;

    public StoneFactory(MapFragment map) {
        this.map = map;
        all_stones = new ArrayList<>();
        stone_markers = new ArrayList<>();

    }

    public static StoneFactory initialize(MapFragment map) {
        StoneFactory buff = new StoneFactory(map);
        buff.start_initialization();
        return buff;
    }

    private void start_initialization() {
        new InitializeStonesTask() {
            @Override
            public void onPostExecute(String res) {
                is_ready = true;
                map.setStones();
            }
        }.execute("");
    }

    public ArrayList<Marker> getMarkers() {
        return stone_markers;
    }

    public boolean isReady() {
        return is_ready;
    }

    public Stone getStoneFromMarker(Marker marker) {
        for (Stone s : all_stones) {
            if (s.getMarker(map.getView()) == marker) {
                return s;
            }
        }
        return null;
    }

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

    private class InitializeStonesTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... strings) {
            // TODO: grab all stone info from the DataBase
            Stone s = new Stone(map.getContext(), 48.4011, 9.9876, "Ulm", "Center", "Das ist Ulm");
            all_stones.add(s);
            stone_markers.add(s.getMarker(map.getView()));

            s = new Stone(map.getContext(), 48.398638, 9.993720, "Vorname_1", "Nachname_1", "Bitte ersetzen");
            all_stones.add(s);
            stone_markers.add(s.getMarker(map.getView()));

            s = new Stone(map.getContext(), 48.39855, 9.99123, "Vorname_2", "Nachname_2", "Bitte ersetzen");
            all_stones.add(s);
            stone_markers.add(s.getMarker(map.getView()));

            s = new Stone(map.getContext(), 48.3893, 9.98924, "Vorname_3", "Nachname_3", "Bitte ersetzen");
            all_stones.add(s);
            stone_markers.add(s.getMarker(map.getView()));

            s = new Stone(map.getContext(), 48.40002, 9.99721, "Vorname_3", "Nachname_4", "Bitte ersetzen");
            all_stones.add(s);
            stone_markers.add(s.getMarker(map.getView()));

            return "Finished";
        }
    }
}
