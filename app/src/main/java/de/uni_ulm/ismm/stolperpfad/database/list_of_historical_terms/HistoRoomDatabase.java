package de.uni_ulm.ismm.stolperpfad.database.list_of_historical_terms;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import de.uni_ulm.ismm.stolperpfad.database.data.HistoricalTerm;

@Database(entities = {HistoricalTerm.class}, version = 1)
public abstract class HistoRoomDatabase extends RoomDatabase {

    public abstract HistoDao histoDao();

    //make the database a singleton
    private static volatile HistoRoomDatabase INSTANCE;

    static HistoRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (HistoRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            HistoRoomDatabase.class, "history_database").
                            addCallback(sRoomDatabaseCallback).build();
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
                    new HistoRoomDatabase.PopulateDbAsync(INSTANCE).execute();
                }
            };

    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {

        private final HistoDao mDao;

        PopulateDbAsync(HistoRoomDatabase db) {
            mDao = db.histoDao();
        }

        @Override
        protected Void doInBackground(final Void... params) {
            //TODO: add here the reading of the data (parser)
            mDao.deleteAll();
            HistoricalTerm histoTerm = new HistoricalTerm(0, "Polenaktion");
            mDao.insert(histoTerm);
            histoTerm = new HistoricalTerm(1, "Aktion T4/Euthanasie");
            mDao.insert(histoTerm);
            histoTerm = new HistoricalTerm(2, "Pogromnacht");
            mDao.insert(histoTerm);
            histoTerm = new HistoricalTerm(3, "Kindertransport nach Großbritannien");
            mDao.insert(histoTerm);
            histoTerm = new HistoricalTerm(3, "Zeugen Jehovas");
            mDao.insert(histoTerm);
            histoTerm = new HistoricalTerm(3, "Fabrikation");
            mDao.insert(histoTerm);
            return null;
        }
    }


}
