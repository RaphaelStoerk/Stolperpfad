package de.uni_ulm.ismm.stolperpfad.database.list_of_persons;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import de.uni_ulm.ismm.stolperpfad.database.data.Person;

@Dao
public interface PersDao {

    @Insert
    void insert(Person pers);

    @Query("DELETE FROM persons")
    void deleteAll();

    @Query("SELECT * from persons ORDER BY family_name ASC")
    LiveData<List<Person>> getAllPersons();
}
