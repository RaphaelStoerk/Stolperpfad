package de.uni_ulm.ismm.stolperpfad;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(entities = {Person.class}, version = 1)
public abstract class PersonsRoomDatabase extends RoomDatabase {
    public abstract PersDao persDao();

    private static volatile PersonsRoomDatabase INSTANCE;

    /**
     * make database a singleton to prevent having
     * multiple instances of the database opened at the same time
     **/
    static PersonsRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (PersonsRoomDatabase.class) {
                if (INSTANCE == null) {
                    // Create database here
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            PersonsRoomDatabase.class, "persons_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
