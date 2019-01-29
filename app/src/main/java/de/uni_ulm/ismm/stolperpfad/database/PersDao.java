package de.uni_ulm.ismm.stolperpfad.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface PersDao {

    @Insert
    void insert(Person pers);

    @Query("DELETE FROM pers_table")
    void deleteAll();

    @Query("SELECT * from pers_table ORDER BY family_name ASC")
    List<Person> getAllPersons();
}
