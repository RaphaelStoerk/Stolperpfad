package de.uni_ulm.ismm.stolperpfad.map_activities.model;

import android.annotation.SuppressLint;
import android.os.AsyncTask;

import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapboxMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.uni_ulm.ismm.stolperpfad.StolperpfadeApplication;
import de.uni_ulm.ismm.stolperpfad.database.StolperpfadeRepository;
import de.uni_ulm.ismm.stolperpfad.database.data.Person;
import de.uni_ulm.ismm.stolperpfad.database.data.Stolperstein;
import de.uni_ulm.ismm.stolperpfad.map_activities.RoutingUtil;
import de.uni_ulm.ismm.stolperpfad.map_activities.view.MapQuestFragment;

/**
 * This class is responsible for handling the "Stolpersteine". It creates all Stones
 * from the database and keeps track of their locations and markers and calculates routes for
 * the route planner
 */
public class StoneFactory {

    private static final int NEIGHBOURS = 5;
    private static final int MINUTES_AT_STONE = 3;
    public static final int SECONDS_PER_MINUTE = 60;
    public static final int MINUTES_PER_HOUR = 60;
    public static final int HOURS_PER_DAY = 24;

    private MapboxMap map_object;
    private MapQuestFragment map_fragment;
    private ArrayList<StoneOnMap> all_stones;
    private ArrayList<Marker> all_markers;
    private StolperpfadeRepository repo;

    /*
     * For some still unknown reason, certain stones in a certain area crash the route creation,
     * possibly because the routing api can't find a pedestrian path to this area, so for now
     * these stones will not be reachable in the route calculation process
     *
     * Names: Klappholz: id 32
     * the following seemed fine, but will also be put on hold for now,
     * TODO: needs more testing: Einstein: id 11, Levy: id 10, Stark: id 9
     */
    private final int[] unreachable_stone_ids = new int[]{32, 11, 10, 9};

    private StoneFactory(MapQuestFragment map_fragment, MapboxMap map_object) {
        this.map_fragment = map_fragment;
        this.map_object = map_object;
        all_stones = new ArrayList<>();
        all_markers = new ArrayList<>();
        repo = new StolperpfadeRepository(Objects.requireNonNull(map_fragment.getActivity()).getApplication());
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
        return all_markers;
    }

    /**
     * Looks for the stone corresponding to a specific marker
     *
     * @param marker the marker for which a corresponding stone is returned
     * @return the corresponding stone
     */
    public StoneOnMap getStoneFromMarker(Marker marker) {
        for (StoneOnMap s : all_stones) {
            if (s.getMarker(map_object) == marker) {
                return s;
            }
        }
        return null;
    }

    /**
     * Creates a list of all stones with the specified stone ids
     *
     * @param stone_ids the requested stone ids
     * @return a list of all found stones
     */
    ArrayList<StoneOnMap> getStonesFromIds(ArrayList<Integer> stone_ids) {
        ArrayList<StoneOnMap> ret = new ArrayList<>();
        for(int id : stone_ids) {
            for(StoneOnMap s : all_stones) {
                if(s.getStoneId() == id) {
                    ret.add(s);
                    break;
                }
            }
        }
        return ret;
    }

    /**
     * Finds the corresponding Marker object for a specific stone id
     *
     * @param stone_id the requested stone id
     * @return the marker for that id
     */
    public Marker getMarkerFromId(int stone_id) {
        for(StoneOnMap s : all_stones) {
            if(s.getStoneId() == stone_id) {
                return s.getMarker(map_object);
            }
        }
        return null;
    }

    /**
     * Calculates which stone is nearest to a given stone, distance wise. It does not yet
     * account for actual walking distance but rather just straight mathematical distance
     *
     * @param rel_stone the stone from which a nearest stone is looked for
     * @return the stone that is nearest to rel_stone
     */
    public Marker getNearestTo(Marker rel_stone) {
        if (all_markers == null || all_markers.size() == 0 || rel_stone == null) {
            return null;
        }
        int[] avoid = StolperpfadeApplication.getInstance().getVisitedStones();
        Marker best = null;
        double best_dist = -1;
        double curr_dist;
        for (StoneOnMap s : all_stones) {
            if(avoid(s.getStoneId(), avoid) || contains(unreachable_stone_ids, s.getStoneId())) {
                continue;
            }
            Marker m = s.getMarker(map_object);
            m.getPosition().distanceTo(rel_stone.getPosition());
            curr_dist = RoutingUtil.getDist(rel_stone, m);
            if (!m.equals(rel_stone) && (best_dist == -1 || curr_dist < best_dist)) {
                best_dist = curr_dist;
                best = m;
            }
        }
        return best;
    }

    /**
     * Checks if an array of integer-ids contains a specific id
     *
     * @param unreachable_stone_ids the ids to check
     * @param stone_id the id to look for in the set of ids
     * @return true, if the id is contained
     */
    private boolean contains(int[] unreachable_stone_ids, int stone_id) {
        for(int id : unreachable_stone_ids) {
            if(id == stone_id) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if a certain stone id is already in the list of all visited stones and therefor should
     * be avoided
     *
     * @param stone_id the stone id in question
     * @param to_avoid the list of all visited stones
     * @return true, if this id is in the list
     */
    private boolean avoid(int stone_id, int[] to_avoid) {
        for(int i : to_avoid){
            if(i == stone_id) {
                return true;
            }
        }
        return false;
    }

    /**
     * This methods calculates a possible path with the specified inputs from the user
     *
     * @param start_route_from where this next route should start
     * @param end_route_at where this next route should end
     * @param time_in_seconds how much time the user has for the path, corresponds to the distance
     *                        in meters, when we set a standard walking speed of 1 m/s
     * @return a new route for the given parameters
     */
    public Stolperpfad createPathWith(Marker start_route_from, Marker end_route_at, int time_in_seconds) {
        Marker marker_for_curr_position;
        StoneOnMap curr_stone;
        double curr_dist;
        Stolperpfad created_path = Stolperpfad.newInstance();
        if(start_route_from == null) {
            return created_path;
        }
        created_path.setStart(start_route_from);
        created_path.setRequestedTime(time_in_seconds);
        ArrayList<ReachableStone> reachable_stones;
        ArrayList<ReachableStone> valid_reachable_stones;
        // Check if start position is already a stone, else get the nearest one and add it to the route
        if(isNotAStonePosition(start_route_from.getPosition())){
            marker_for_curr_position = getNearestTo(start_route_from);
            curr_dist = start_route_from.getPosition().distanceTo(marker_for_curr_position.getPosition());
            // check if the nearest stone can be reached in time, else no route can be created
            if(curr_dist > time_in_seconds) {
                return created_path;
            }
            curr_stone = getStoneFromMarker(marker_for_curr_position);
            created_path.addStone(curr_stone);
            time_in_seconds -= (curr_dist + SECONDS_PER_MINUTE * MINUTES_AT_STONE);
        }
        // The main Route creation
        while(true) {
            curr_stone = created_path.getLastStone();
            marker_for_curr_position = curr_stone.getMarker(map_object);
            reachable_stones = curr_stone.getReachableStones();
            valid_reachable_stones = getValidReachables(reachable_stones, created_path, end_route_at, time_in_seconds);
            if(valid_reachable_stones.size() == 0) {
                // no more stones can be added
                if(end_route_at != null) {
                    if(end_route_at.getPosition().distanceTo(marker_for_curr_position.getPosition()) <= time_in_seconds) {
                        // the end can be reached in time
                        created_path.addEnd(end_route_at);
                        break;
                    } else if(isNotAStonePosition(end_route_at.getPosition())) {
                        // the end can not be reached in time, but since it is not a stone position,
                        // the user does not need to go all the way there, so it will be set as the end
                        // nonetheless
                        created_path.addEnd(end_route_at);
                    } else {
                        // end is a stone that can not be reached in time, therefore no valid route
                        // can be created
                        created_path.setTimeNotPossible();
                    }
                }
                break;
            } else {
                ReachableStone next = choseNextReachable(valid_reachable_stones);
                created_path.addStone(next.getStone());
                time_in_seconds -= (next.getDist() + SECONDS_PER_MINUTE * MINUTES_AT_STONE);
            }
        }
        return created_path;
    }

    /**
     * Chose a reachable stone from one stone on the path that optimizes the route that will be created
     *
     * @param valid_reachable a list of all reachable stones, checked for validity
     * @return an optimized stone from the list of all valid stones
     */
    private ReachableStone choseNextReachable(ArrayList<ReachableStone> valid_reachable) {
        int size = valid_reachable.size();
        if(Math.random() > 0.3) {
            double dist = -1;
            ReachableStone nearest = null;
            for(ReachableStone n : valid_reachable) {
                if(dist < 0 || n.getDist() < dist) {
                    nearest = n;
                    dist = n.getDist();
                }
            }
            if(nearest != null) {
                return nearest;
            } else {
                int index = (int) (Math.random() * size);
                return valid_reachable.get(index);
            }
        } else {
            int index = (int) (Math.random() * size);
            return valid_reachable.get(index);
        }
    }

    /**
     * Determines which of a list of possible next stones are reachable in the given time
     *
     * @param reachable_stones all neighbours for one stone
     * @param path the current path that is calculated
     * @param end_route_at where the path should end at
     * @param time_in_seconds how much time is left for the route
     * @return a list of all reachable and valid stones
     */
    private ArrayList<ReachableStone> getValidReachables(ArrayList<ReachableStone> reachable_stones, Stolperpfad path, Marker end_route_at, int time_in_seconds) {
        ArrayList<ReachableStone> ret = new ArrayList<>();
        for(ReachableStone n : reachable_stones) {
            // Avoid adding a stone twice to the route
            if(path.getWaypointsLatLng().contains(n.getMarker(map_object).getPosition())){
                continue;
            }
            // check if the stone is reachable
            if(n.getDist() > time_in_seconds) {
                continue;
            }
            // check if the end will be reachable from that ReachableStone in the given time
            if(end_route_at != null && n.getMarker(map_object).getPosition().distanceTo(end_route_at.getPosition()) > time_in_seconds - n.getDist()) {
                continue;
            }
            ret.add(n);
        }
        return ret;
    }

    /**
     * Checks if a position is not the location of a stone
     *
     * @param position the position to check
     * @return true, if the position is not a stone position
     */
    private boolean isNotAStonePosition(LatLng position) {
        for(StoneOnMap s : all_stones) {
            if(s.getMarker(map_object).getPosition().equals(position)) {
                return false;
            }
        }
        return true;
    }

    /**
     * This class is responsible for creating an asynchronous task that creates all the stones
     * from the data in the database, so they can later be displayed on the map_fragment
     */
    @SuppressLint("StaticFieldLeak")
    private class InitializeStonesTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... strings) {

            Objects.requireNonNull(map_fragment.getActivity()).runOnUiThread(() -> {
                // TODO: grab all stone info from the DataBase
                new LoadContentTask() {
                    @Override
                    protected void onPostExecute(Void aVoid) {
                        for(StoneOnMap s : all_stones) {
                            all_markers.add(s.getMarker(map_object));
                        }
                        map_fragment.setStones();
                        new CalculateReachablesInBackgroundTask() {
                            @Override
                            protected void onPostExecute(Boolean aBoolean) {
                                super.onPostExecute(aBoolean);
                            }
                        }.execute();
                    }
                }.execute();
            });
            return "Finished";
        }

        /**
         * Loads the data from the data base
         */
        private class LoadContentTask extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... voids) {
                List<Stolperstein> stones = repo.getAllStones();
                for (Stolperstein s : stones) {
                    List<Person> persons_on_stone = repo.getPersonsOnStone(s.getStoneId());
                    StoneOnMap next = new StoneOnMap(s, persons_on_stone);
                    all_stones.add(next);
                }
                return null;
            }
        }

        /**
         * Determines for every stone what stones are closest to them
         */
        private class CalculateReachablesInBackgroundTask extends AsyncTask<Void, Void, Boolean> {
            @Override
            protected Boolean doInBackground(Void... voids) {
                double dist, shortest_dist;
                StoneOnMap nearest = null;
                for(StoneOnMap s : all_stones) {
                    for(int i = 0; i < NEIGHBOURS; i++ ) {
                        shortest_dist = -1;
                        for (StoneOnMap s_to : all_stones) {
                            if (s.equals(s_to) || s.markedAsReachable(s_to) || contains(unreachable_stone_ids, s_to.getStoneId())) {
                                continue;
                            }
                            dist = s.getMarker(map_object).getPosition().distanceTo(s_to.getMarker(map_object).getPosition());
                            if(shortest_dist == -1 || dist < shortest_dist) {
                                shortest_dist = dist;
                                nearest = s_to;
                            }
                        }
                        if(nearest == null) {
                            return false;
                        }
                        s.addNeighbour(shortest_dist, nearest);
                    }
                    if(s.countNeighbours() != NEIGHBOURS) {
                        return false;
                    }
                }
                return true;
            }
        }
    }
}
