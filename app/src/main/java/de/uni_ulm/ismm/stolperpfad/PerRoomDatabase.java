package de.uni_ulm.ismm.stolperpfad;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(entities = {Person.class}, version = 1)
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
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
