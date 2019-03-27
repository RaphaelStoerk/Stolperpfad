package de.uni_ulm.ismm.stolperpfad.database.list_of_historical_terms;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

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
                            HistoRoomDatabase.class, "history_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }


}
