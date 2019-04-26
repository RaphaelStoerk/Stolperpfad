package de.uni_ulm.ismm.stolperpfad.map_activities.model;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;

import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapboxMap;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.uni_ulm.ismm.stolperpfad.database.data_util.DataFromJSON;
import de.uni_ulm.ismm.stolperpfad.map_activities.RoutingUtil;
import de.uni_ulm.ismm.stolperpfad.map_activities.view.MapQuestFragment;

/**
 * This class is responsible for handling the "Stolpersteine". It creates all Stones
 * from the database and keeps track of their locations and markers
 */
public class StoneFactory {

    private static final int NEIGHBOURS = 5;
    private static final int MINUTES_AT_STONE = 3;
    private ArrayList<Stone> all_stones;
    private ArrayList<Marker> stone_markers;
    private MapQuestFragment map;
    private MapboxMap mapboxMap;
    private boolean is_ready;
    private ArrayList<PersonInfo> persons;
    private boolean neighbours_ready;

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
                Log.i("MY_ROUTE_TAG", "STARTING");
                new InitializeNeighboursTask() {
                    @Override
                    protected void onPostExecute(Boolean aBoolean) {
                        super.onPostExecute(aBoolean);
                        neighbours_ready = aBoolean;
                        map.activatePathPlanner(aBoolean);
                    }
                }.execute();
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
            m.getPosition().distanceTo(rel_stone.getPosition());
            curr_dist = RoutingUtil.getDist(rel_stone, m);
            if (!m.equals(rel_stone) && (best_dist == -1 || curr_dist < best_dist)) {
                best_dist = curr_dist;
                best = m;
            }
        }
        return best;
    }

    public boolean createPathWith(ArrayList<Marker> route_points, Marker start_route_from, Marker end_route_at, int time_in_seconds) {
        Marker curr_pos;
        Stone curr_stone;
        double curr_dist;
        ArrayList<Neighbour> neighbours;
        ArrayList<Neighbour> valid_neighs;
        // Check if start position is already a stone, else get the nearest one and add it to the route
        if(!isStonePosition(start_route_from.getPosition())){
            curr_pos = getNearestTo(start_route_from);
            curr_dist = start_route_from.getPosition().distanceTo(curr_pos.getPosition());
            // check if the nearest stone can be reached in time, else no route can be created
            if(curr_dist > time_in_seconds) {
                return false;
            }
            route_points.add(curr_pos);
            curr_stone = getStoneFromMarker(curr_pos);
            Log.i("MY_ROUTE_TAG", "Added a first stone: " + curr_pos.getTitle());
            time_in_seconds -= (curr_dist + 60 * MINUTES_AT_STONE);
        }

        // The main Route creation
        while(true) {
            curr_pos = route_points.get(route_points.size()-1);
            curr_stone = getStoneFromMarker(curr_pos);
            neighbours = curr_stone.getNeighbours();
            valid_neighs = getValidNeighbours(neighbours, route_points, curr_pos, end_route_at, time_in_seconds);
            if(valid_neighs.size() == 0) {
                Log.i("MY_ROUTE_TAG", "No new neighbour found, left time to go to the end: " + (time_in_seconds / 60f) + "min");
                if(end_route_at == null) {
                    if(route_points.size() >= 2) {
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    if(end_route_at.getPosition().distanceTo(curr_pos.getPosition()) < time_in_seconds) {
                        route_points.add(end_route_at);
                        return true;
                    } else {
                        if(!isStonePosition(end_route_at.getPosition())) {
                            route_points.add(end_route_at);
                            Log.i("MY_ROUTE_TAG", "Questionable Route");
                            return true; // TODO: false ???
                        } else {
                            return false;
                        }
                    }
                }
            } else {
                Neighbour next = choseGoodNeighbour(valid_neighs, route_points, curr_pos, time_in_seconds);
                route_points.add(next.getMarker(mapboxMap));
                time_in_seconds -= (next.getDist() + 60 * MINUTES_AT_STONE);
                Log.i("MY_ROUTE_TAG", "Added a new stone, left time " + time_in_seconds);
            }
        }
    }

    private Neighbour choseGoodNeighbour(ArrayList<Neighbour> valid_neighs, ArrayList<Marker> route_points, Marker curr_pos, int time_in_seconds) {
        int size = valid_neighs.size();
        int index = (int) (Math.random() * size); // TODO: chose on basis of distances and times
        return valid_neighs.get(index);
    }

    private ArrayList<Neighbour> getValidNeighbours(ArrayList<Neighbour> neighbours, ArrayList<Marker> route_points, Marker curr_pos, Marker end_route_at, int time_in_seconds) {
        ArrayList<Neighbour> ret = new ArrayList<>();

        for(Neighbour n : neighbours) {
            // Avoid adding a stone twice to the route
            if(route_points.contains(n.getMarker(mapboxMap))){
                continue;
            }
            // check if the stone is reachable
            if(n.getDist() >= time_in_seconds) {
                continue;
            }
            // check if the end will be reachable from that Neighbour in the given time
            if(end_route_at != null && n.getMarker(mapboxMap).getPosition().distanceTo(end_route_at.getPosition()) >= time_in_seconds - n.getDist()) {
                continue;
            }
            ret.add(n);
        }
        return ret;
    }

    private boolean isStonePosition(LatLng position) {
        for(Stone s : all_stones) {
            if(s.getMarker(mapboxMap).getPosition().equals(position)) {
                return true;
            }
        }
        return false;
    }

    private class InitializeNeighboursTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            double dist, shorest_dist = -1;
            Stone nearest = null;
            for(Stone s : all_stones) {
                for(int i = 0; i < NEIGHBOURS; i++ ) {
                    shorest_dist = -1;
                    for (Stone s_to : all_stones) {
                        if (s.equals(s_to) || s.hasNeighbour(s_to)) {
                            continue;
                        }
                        dist = s.getMarker(mapboxMap).getPosition().distanceTo(s_to.getMarker(mapboxMap).getPosition());
                        if(shorest_dist == -1 || dist < shorest_dist) {
                            shorest_dist = dist;
                            nearest = s_to;
                        }
                    }
                    if(nearest == null) {
                        return false;
                    }
                    s.addNeighbour(shorest_dist, nearest);
                }
                if(s.countNeighbours() != NEIGHBOURS) {
                    return false;
                }
            }
            return true;
        }
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

                loadPersons();

                Stone s;

                for(PersonInfo p : persons) {
                    Stolperstein st = p.getStolperstein();
                    s = new Stone(st.getLatitude(),st.getLongitude(), p.getVorname(),p.getNachname(), st.getAdress());
                    all_stones.add(s);
                    stone_markers.add(s.getMarker(mapboxMap));
                }
            });
            return "Finished";
        }

        private void loadPersons() {
            persons = new ArrayList<>();
            ArrayList<JSONObject> personen = DataFromJSON.loadAllJSONFromDirectory(map.getContext(), "personen_daten");
            PersonInfo next;
            for(JSONObject json : personen) {
                try {
                    next = createPersonFromJson(json);
                    persons.add(next);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        private PersonInfo createPersonFromJson(JSONObject json) throws JSONException {
            Stolperstein stolperstein;
            try {
                int id = json.getInt("id");
                String vorname = json.getString("vorname");
                String nachname = json.getString("nachname");
                String geburtsname = json.getString("geburtsname");
                JSONObject stein = json.getJSONObject("stein");
                int id1 = stein.getInt("id");
                String ad = stein.getString("addresse");
                double lat = stein.getDouble("latitude");
                double lon = stein.getDouble("longitude");
                return new PersonInfo(id, vorname, nachname, geburtsname, new Stolperstein(id1, ad, lat, lon));
            } catch(NullPointerException e) {

            }
            return new PersonInfo(-1, "Fehler", "Fehler", "", null);
        }
    }
}
