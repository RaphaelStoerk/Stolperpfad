package de.uni_ulm.ismm.stolperpfad.database.list_of_persons;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import de.uni_ulm.ismm.stolperpfad.database.data.HistoricalTerm;
import de.uni_ulm.ismm.stolperpfad.database.data.Institution;
import de.uni_ulm.ismm.stolperpfad.database.data.Person;
import de.uni_ulm.ismm.stolperpfad.database.data.Place;
import de.uni_ulm.ismm.stolperpfad.database.data.Stolperstein;

/**
 * !!! READ ME !!!
 * If you get this exception
 * "Room cannot verify the data integrity. Looks like you've changed schema but forgot to update the version number.
 * You can simply fix this by increasing the version number."
 * (because you changed something in the data tables),
 * Do NOT change the version number but uninstall the app on your phone, clean the project and rebuild everything.
 * Then it should work again.
 */
@Database(entities = {Person.class, Person.Marriage.class, Person.Children.class, Person.Flight.class,
        HistoricalTerm.class, Institution.class, Institution.MoveInUlm.class, Place.class, Place.Move.class,
        Place.Deportation.class, Stolperstein.class}, version = 1, exportSchema = false)

public abstract class PersRoomDatabase extends RoomDatabase {

    public abstract PersDao persDao();

    private static volatile PersRoomDatabase INSTANCE;

    static PersRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (PersRoomDatabase.class) {
                if (INSTANCE == null) {

                    // Create database here
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            PersRoomDatabase.class, "persons_database")
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
                    new PopulateDbAsync(INSTANCE).execute();
                }
            };


    /**
     * CLEAR OLDER DATABASE ENTRIES
     */
    private static void clearDatabase(PersDao dao){
        dao.deleteAllPersons();
        dao.deleteAllMarriages();
        dao.deleteAllChildren();
        dao.deleteAllFlights();
        dao.deleteAllPlaces();
        dao.deleteAllMovesAround();
        dao.deleteAllDeportations();
        dao.deleteAllStolpersteine();
        dao.deleteAllInstitutions();
        dao.deleteAllMovesInUlm();
    }


    /**
     * POPULATE DATABASE
     */
    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {

        private final PersDao mDao;

        PopulateDbAsync(PersRoomDatabase db) {
            mDao = db.persDao();
        }

        @Override
        protected Void doInBackground(final Void... params) {
            //TODO: add here the reading of the data (parser)
           clearDatabase(mDao);
            Person person = new Person(0,"Jakob", "Frenkel", 0);
            mDao.insert(person);
            person = new Person(1,"Ida", "Frenkel", 0);
            mDao.insert(person);
            person = new Person(2,"Karl", "Rueff", 1);
            mDao.insert(person);

            Stolperstein stolperstein = new Stolperstein(0,"Olgastraße 114", 48.402106, 9.994395);
            mDao.insert(stolperstein);
            stolperstein = new Stolperstein(1,"Frauenstraße 28", 48.399455, 9.996718);
            mDao.insert(stolperstein);

            Person.Marriage marriage = new Person.Marriage(0, 1, null);
            mDao.insert(marriage);

            /*Person person = new Person(0, "Jakob", "Frenkel", null, null, null, null, null, null);
            mDao.insert(person);
            person = new Person(1, "Ida","Frenkel",  null, null, null, null, null, null);
            mDao.insert(person);
            person = new Person(2, "Ludwig","Hecht",  null, null, null, null, null, null);
            mDao.insert(person);*/
            return null;
        }

    }



}
