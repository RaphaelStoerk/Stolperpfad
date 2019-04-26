package de.uni_ulm.ismm.stolperpfad.database;

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
import de.uni_ulm.ismm.stolperpfad.info_display.stone_info.model.PersonInfo;

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
    private static void clearDatabase(StolperpfadeDao dao){
        dao.deleteAllPersons();
        dao.deleteAllVitas();
        dao.deleteAllStolpersteine();
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
            PersonInfo next;
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

            for (JSONObject json : persons) {
                try {
                    //insert person
                    id = json.getInt("id");
                    firstname = json.getString("vorname");
                    familyname = json.getString("nachname");
                    birthname = json.getString("geburtsname");
                    history = json.getString("geschichte");
                    stone = json.getJSONObject("stolperstein");
                    stoneId = stone.getInt("id");
                    Person person = new Person(id, firstname, familyname, birthname, history, stoneId);
                    mDao.insert(person);

                    //insert vita
                    JSONArray biography = json.getJSONArray("bio");
                    String[] vitaSections = new String[vitaLength];
                    for (int i = 0; i < biography.length(); i++) {
                        String section = biography.getString(i);
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
                    mDao.insert(stostei);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            // HISTORICAL TERMS
            ArrayList<JSONObject> histoTerms = DataFromJSON.loadAllJSONFromDirectory(mContext,"history_data");
            String histoName;
            String histoExplanation;

            for (JSONObject json : persons) {
                try {
                    histoName = json.getString("name");
                    histoExplanation = json.getString("explanation");
                    HistoricalTerm histoTerm = new HistoricalTerm(histoName, histoExplanation);
                    mDao.insert(histoTerm);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }



            /*Person person = new Person(0,"Jakob", "Frenkel", null, null, 0);
            mDao.insert(person);
            person = new Person(1,"Ida", "Frenkel", null, null,0);
            mDao.insert(person);
            person = new Person(2,"Karl", "Rueff", null, null,1);
            mDao.insert(person);

            Stolperstein stolperstein = new Stolperstein(0,"Olgastraße 114", 48.402106, 9.994395);
            mDao.insert(stolperstein);
            stolperstein = new Stolperstein(1,"Frauenstraße 28", 48.399455, 9.996718);
            mDao.insert(stolperstein);

            HistoricalTerm histoTerm = new HistoricalTerm("Polenaktion", "@string/info_polenaktion");
            mDao.insert(histoTerm);
            histoTerm = new HistoricalTerm("Aktion T4/Euthanasie", "Aktion T4");
            mDao.insert(histoTerm);
            histoTerm = new HistoricalTerm("Pogromnacht", "Pogromnacht");
            mDao.insert(histoTerm);
            histoTerm = new HistoricalTerm("Kindertransport nach Großbritannien", "Kindertransport nach Großbritannien");
            mDao.insert(histoTerm);
            histoTerm = new HistoricalTerm("Zeugen Jehovas", "Zeugen Jehovas");
            mDao.insert(histoTerm);
            histoTerm = new HistoricalTerm("Fabrikation", "Fabrikation");
            mDao.insert(histoTerm);*/
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

    }
}
