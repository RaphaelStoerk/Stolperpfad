package de.uni_ulm.ismm.stolperpfad.database;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.uni_ulm.ismm.stolperpfad.database.data.HistoricalTerm;
import de.uni_ulm.ismm.stolperpfad.database.data.Person;
import de.uni_ulm.ismm.stolperpfad.database.data.Stolperstein;
import de.uni_ulm.ismm.stolperpfad.database.data_util.DataFromJSON;

/**
 * !!! READ ME !!!
 * If you get the following exception
 * "Room cannot verify the data integrity. Looks like you've changed schema but forgot to update the version number.
 * You can simply fix this by increasing the version number."
 * (because you changed something in the data tables),
 * Do NOT change the version number but uninstall the app on your phone, clean the project and rebuild everything.
 * Then it should work again.
 */
@Database(entities = {Person.class, Person.Vita.class, HistoricalTerm.class, Stolperstein.class}, version = 1, exportSchema = false)
public abstract class StolperpfadeRoomDatabase extends RoomDatabase {

    private static Context mContext;
    private static int vitaLength = 10;

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
            ArrayList<JSONObject> persons = DataFromJSON.loadAllJSONFromDirectory(mContext, "person_data");
            int id;
            String firstname;
            String familyname;
            String birthname;
            String history;
            JSONObject stone;
            int stoneId;
            String address;
            double latitude;
            double longitude;
            ArrayList<Integer> stoneIds = new ArrayList<>();

            for (JSONObject json : persons) {
                try {

                    //insert person
                    id = json.getInt("id");
                    firstname = json.getString("firstname");
                    familyname = json.getString("familyname");
                    birthname = json.getString("birthname");
                    history = json.getString("history");
                    stone = json.getJSONObject("stone");
                    stoneId = stone.getInt("id");
                    Log.i("person_found", familyname + ", " + firstname);
                    Person person = new Person(id, firstname, familyname, birthname, history, stoneId);
                    mDao.insert(person);

                    //insert vita
                    JSONArray biography = json.getJSONArray("vita");
                    String[] vitaSections = new String[vitaLength];
                    for (int i = 0; i < biography.length(); i++) {
                        String section = biography.getJSONObject(i).getString("content");
                        vitaSections[i] = section;
                    }
                    Person.Vita vita = new Person.Vita(id, vitaSections[0], vitaSections[1], vitaSections[2],
                            vitaSections[3], vitaSections[4], vitaSections[5], vitaSections[6],
                            vitaSections[7], vitaSections[8], vitaSections[9]);
                    Log.i("LOG_ADDED_PERSON", firstname +" " + familyname + " " + vitaSections[0]);

                    mDao.insert(vita);

                    //insert Stolperstein
                    address = stone.getString("address");
                    latitude = stone.getDouble("latitude");
                    longitude = stone.getDouble("longitude");
                    Stolperstein stostei = new Stolperstein(stoneId, address, latitude, longitude);
                    if (stoneIds.contains(stoneId)) {

                    } else {
                        mDao.insert(stostei);
                        stoneIds.add(stoneId);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            // HISTORICAL TERMS
            ArrayList<JSONObject> histoTerms = DataFromJSON.loadAllJSONFromDirectory(mContext, "history_data");
            String histoName;
            String histoExplanation;

            for (JSONObject json : histoTerms) {
                try {
                    histoName = json.getString("name");
                    histoExplanation = json.getString("explanation");
                    HistoricalTerm histoTerm = new HistoricalTerm(histoName, histoExplanation);
                    mDao.insert(histoTerm);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        private boolean stoneExists(int stoneId) {
            String address = mDao.getAddress(stoneId);
            if (address == null || address.equals("")) {
                return false;
            } else {
                return true;
            }
        }

    }
}
