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
import java.util.List;

import de.uni_ulm.ismm.stolperpfad.database.StolperpfadeRepository;
import de.uni_ulm.ismm.stolperpfad.database.data.Person;
import de.uni_ulm.ismm.stolperpfad.database.data.Stolperstein;
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
    private List<Person> persons;
    private List<Stolperstein> stones;
    private boolean neighbours_ready;
    private StolperpfadeRepository repo;

    private StoneFactory(MapQuestFragment map, MapboxMap mapboxMap) {
        this.map = map;
        this.mapboxMap = mapboxMap;
        all_stones = new ArrayList<>();
        stone_markers = new ArrayList<>();
        repo = new StolperpfadeRepository(map.getActivity().getApplication());
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
                // TODO: Stub
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

    public MyRoad createPathWith(Marker start_route_from, Marker end_route_at, int time_in_seconds) {
        Marker curr_pos;
        Stone curr_stone;
        double curr_dist;
        MyRoad created_path = MyRoad.newInstance();
        if(start_route_from == null) {
            Log.i("MY_ROUTE_TAG", "No start");
            return created_path;
        }
        created_path.setStart(start_route_from);
        created_path.setRequestedTime(time_in_seconds);
        ArrayList<Neighbour> neighbours;
        ArrayList<Neighbour> valid_neighs;
        // Check if start position is already a stone, else get the nearest one and add it to the route
        if(!isStonePosition(start_route_from.getPosition())){
            curr_pos = getNearestTo(start_route_from);
            curr_dist = start_route_from.getPosition().distanceTo(curr_pos.getPosition());
            // check if the nearest stone can be reached in time, else no route can be created
            if(curr_dist > time_in_seconds) {
                Log.i("MY_ROUTE_TAG", "Not enough time");
                return created_path;
            }
            curr_stone = getStoneFromMarker(curr_pos);
            created_path.addStone(curr_stone);
            time_in_seconds -= (curr_dist + 60 * MINUTES_AT_STONE);
        }
        // The main Route creation
        while(true) {
            curr_stone = created_path.getLastStone();
            curr_pos = curr_stone.getMarker(mapboxMap);
            neighbours = curr_stone.getNeighbours();
            valid_neighs = getValidNeighbours(neighbours, created_path, end_route_at, time_in_seconds);
            if(valid_neighs.size() == 0) {
                if(end_route_at != null) {
                    if(end_route_at.getPosition().distanceTo(curr_pos.getPosition()) < time_in_seconds) {
                        created_path.addEnd(end_route_at);
                        Log.i("MY_ROUTE_TAG", " The end");
                        break;
                    } else if(!isStonePosition(end_route_at.getPosition())) {
                        created_path.addEnd(end_route_at);
                    } else {
                        created_path.setNotPossible();
                    }
                    Log.i("MY_ROUTE_TAG", "Path not possible: left time: " + time_in_seconds);
                }
                Log.i("MY_ROUTE_TAG", "No valid neighbours");
                break;
            } else {
                Neighbour next = choseGoodNeighbour(valid_neighs, created_path, curr_pos, time_in_seconds);
                created_path.addStone(next.getStone());
                time_in_seconds -= (next.getDist() + 60 * MINUTES_AT_STONE);
                Log.i("MY_ROUTE_TAG", "New stone: " + created_path.getLastStone().getStoneId());
            }
        }
        return created_path;
    }

    private Neighbour choseGoodNeighbour(ArrayList<Neighbour> valid_neighs, MyRoad path, Marker curr_pos, int time_in_seconds) {
        int size = valid_neighs.size();
        int index = (int) (Math.random() * size); // TODO: chose on basis of distances and times
        return valid_neighs.get(index);
    }

    private ArrayList<Neighbour> getValidNeighbours(ArrayList<Neighbour> neighbours, MyRoad path, Marker end_route_at, int time_in_seconds) {
        ArrayList<Neighbour> ret = new ArrayList<>();

        for(Neighbour n : neighbours) {
            // Avoid adding a stone twice to the route
            if(path.getWaypointsLatLng().contains(n.getMarker(mapboxMap).getPosition())){
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

    public ArrayList<Stone> getStonesFromIds(ArrayList<Integer> stoneIds) {
        ArrayList<Stone> ret = new ArrayList<>();
        for(int id : stoneIds) {
            for(Stone s : all_stones) {
                if(s.getStoneId() == id) {
                    ret.add(s);
                    break;
                }
            }
        }
        return ret;
    }

    public Marker getMarkerFromId(int next_id) {
        for(Stone s : all_stones) {
            if(s.getStoneId() == next_id) {
                return s.getMarker(mapboxMap);
            }
        }
        return null;
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
                new LoadContentTask() {
                    @Override
                    protected void onPostExecute(Void aVoid) {
                        for(Stone s : all_stones) {
                            stone_markers.add(s.getMarker(mapboxMap));
                        }
                        is_ready = true;
                        map.setStones();
                        Log.i("MY_ROUTE_TAG", "STARTING");
                        new InitializeNeighboursTask() {
                            @Override
                            protected void onPostExecute(Boolean aBoolean) {
                                super.onPostExecute(aBoolean);
                                neighbours_ready = aBoolean;
                                map.activatePathPlanner(aBoolean);
                                Log.i("MY_DEBUG_TAG","onpost load content");
                            }
                        }.execute();
                    }
                }.execute();
            });
            return "Finished";
        }

        private class LoadContentTask extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... voids) {
                Log.i("MY_DEBUG_TAG","loading repo");
                stones = repo.getAllStones();
                persons = repo.getAllPersons();
                for (Stolperstein s : stones) {
                    List<Person> persons_on_stone = repo.getPersonsOnStone(s.getStoneId());
                    Stone next = new Stone(s, persons_on_stone);
                    all_stones.add(next);
                }
                Log.i("MY_DEBUG_TAG","loading repo done " + persons.size() + ", " + all_stones.size());
                return null;
            }
        }
    }
}
