package de.uni_ulm.ismm.stolperpfad.database;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import de.uni_ulm.ismm.stolperpfad.database.data.HistoricalTerm;
import de.uni_ulm.ismm.stolperpfad.database.data.Person;
import de.uni_ulm.ismm.stolperpfad.database.data.Stolperstein;

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

    public abstract StolperpfadeDao mDao();
    //make the database a singleton
    private static volatile StolperpfadeRoomDatabase INSTANCE;

    static StolperpfadeRoomDatabase getDatabase(final Context context) {
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
            //TODO: add here the reading of the data (parser)?
            clearDatabase(mDao);
            Person person = new Person(0,"Jakob", "Frenkel", null, null, 0);
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
            mDao.insert(histoTerm);
            return null;
        }

    }
}
