package de.uni_ulm.ismm.stolperpfad.database.list_of_persons;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import de.uni_ulm.ismm.stolperpfad.database.data.Person;
import de.uni_ulm.ismm.stolperpfad.database.data.Stolperstein;

/**
 * READ ME:
 * If you want to use given parameters from the methods in the SQL queries,
 * you need to type a ':' right before the variable name.
 */

@Dao
public interface PersDao {

    @Insert
    void insert(Person pers);

    @Query("DELETE FROM persons")
    void deleteAll();

    @Query("SELECT * from persons ORDER BY family_name ASC")
    LiveData<List<Person>> getAllPersons();

    @Insert
    void insert(Stolperstein stolperstein);

    @Insert
    void insert(Person.Marriage marriage);

    @Query("SELECT street_and_number from stolpersteine WHERE stone_id = :persId")
    String getAddress(int persId);
}
