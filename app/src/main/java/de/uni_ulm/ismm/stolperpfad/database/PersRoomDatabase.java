package de.uni_ulm.ismm.stolperpfad.database;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

@Database(entities = {Person.class}, version = 1, exportSchema = false)
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
            new RoomDatabase.Callback(){

                @Override
                public void onOpen (@NonNull SupportSQLiteDatabase db){
                    super.onOpen(db);
                    new PopulateDbAsync(INSTANCE).execute();
                }
            };

    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {

        private final PersDao mDao;

        PopulateDbAsync(PersRoomDatabase db){
            mDao = db.persDao();
        }

        @Override
        protected Void doInBackground(final Void... params){
            mDao.deleteAll();
            Person person = new Person("Jakob", "Frenkel");
            mDao.insert(person);
            person = new Person("Ida","Frenkel");
            mDao.insert(person);
            person = new Person("Ernst","Dauner");
            mDao.insert(person);
            return null;
        }
    }
}
