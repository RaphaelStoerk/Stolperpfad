package de.uni_ulm.ismm.stolperpfad.database;

import android.annotation.SuppressLint;
import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.uni_ulm.ismm.stolperpfad.database.data.HistoricalTerm;
import de.uni_ulm.ismm.stolperpfad.database.data.Person;
import de.uni_ulm.ismm.stolperpfad.database.data.Stolperstein;
import de.uni_ulm.ismm.stolperpfad.database.data_util.DataFromJSON;

/**
 * This is the Database for this application
 */
@SuppressLint("StaticFieldLeak")
@Database(entities = {Person.class, Person.Vita.class, HistoricalTerm.class, Stolperstein.class}, version = 1, exportSchema = false)
public abstract class StolperpfadeRoomDatabase extends RoomDatabase {

    private static Context mContext;

    public abstract StolperpfadeDao mDao();

    //make the database a singleton
    private static volatile StolperpfadeRoomDatabase INSTANCE;

    static StolperpfadeRoomDatabase getDatabase(final Context context) {
        mContext = context;
        if (INSTANCE == null) {
            synchronized (StolperpfadeRoomDatabase.class) {
                if (INSTANCE == null) {
                    // Create database here
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            StolperpfadeRoomDatabase.class, "stolperpfade_database")
                            .addCallback(sRoomDatabaseCallback).build();
                }
            }
        }
        return INSTANCE;
    }

    private static RoomDatabase.Callback sRoomDatabaseCallback =
            new RoomDatabase.Callback() {
                @Override
                public void onOpen(@NonNull SupportSQLiteDatabase db) {
                    super.onOpen(db);
                    new StolperpfadeRoomDatabase.PopulateDbAsync(INSTANCE).execute();
                }
            };

    /**
     * CLEAR OLDER DATABASE ENTRIES
     */
    private static void clearDatabase(StolperpfadeDao dao) {
        dao.deleteAllPersons();
        dao.deleteAllVitas();
        dao.deleteAllStolpersteine();
        dao.deleteAllTerms();
    }

    /**
     * POPULATE DATABASE
     */
    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {

        private final StolperpfadeDao mDao;

        PopulateDbAsync(StolperpfadeRoomDatabase db) {
            mDao = db.mDao();
        }

        @Override
        protected Void doInBackground(final Void... params) {
            clearDatabase(mDao);

            // PERSONS, VITA, STOLPERSTEINE
            ArrayList<JSONObject> persons_as_json = DataFromJSON.loadAllJSONFromDirectory(mContext, "person_data");
            int id, stoneId;
            double latitude, longitude;
            String first_name, family_name, birth_name, history, address;
            JSONObject stone;
            ArrayList<Integer> stone_ids = new ArrayList<>();
            for (JSONObject single_person : persons_as_json) {
                try {
                    //insert person
                    id = single_person.getInt("id");
                    first_name = single_person.getString("vorname");
                    family_name = single_person.getString("nachname");
                    birth_name = single_person.getString("geburtsname");
                    history = single_person.getString("geschichte");
                    stone = single_person.getJSONObject("stein");
                    stoneId = stone.getInt("id");
                    Person person = new Person(id, first_name, family_name, birth_name, history, stoneId);
                    mDao.insert(person);
                    //insert vita
                    JSONArray biography = single_person.getJSONArray("bio");
                    int vitaLength = 10;
                    String[] vitaSections = new String[vitaLength];
                    for (int i = 0; i < biography.length(); i++) {
                        String section = biography.getJSONObject(i).getString("content");
                        vitaSections[i] = section;
                    }
                    Person.Vita vita = new Person.Vita(id, vitaSections[0], vitaSections[1], vitaSections[2],
                            vitaSections[3], vitaSections[4], vitaSections[5], vitaSections[6],
                            vitaSections[7], vitaSections[8], vitaSections[9]);
                    mDao.insert(vita);
                    //insert Stolperstein
                    address = stone.getString("addresse");
                    latitude = stone.getDouble("latitude");
                    longitude = stone.getDouble("longitude");
                    Stolperstein stostei = new Stolperstein(stoneId, address, latitude, longitude);
                    if (!stone_ids.contains(stoneId)) {
                        mDao.insert(stostei);
                        stone_ids.add(stoneId);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            // HISTORICAL TERMS
            ArrayList<JSONObject> historical_terms_as_json = DataFromJSON.loadAllJSONFromDirectory(mContext, "history_data");
            String historical_title, historical_content;

            for (JSONObject single_term : historical_terms_as_json) {
                try {
                    historical_title = single_term.getString("name");
                    historical_content = single_term.getString("explanation");
                    HistoricalTerm historical_term = new HistoricalTerm(historical_title, historical_content);
                    mDao.insert(historical_term);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }
}
