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
    /**
     * Overview over the database tables:
     * Table 1: Persons
     * Table 2: Vita
     * Table 3: Stolpersteine
     * Table 4: Historical Terms, not treated here!)
     */

    //PERSONS
    @Insert
    void insert(Person pers);

    @Query("DELETE FROM persons")
    void deleteAllPersons();

    @Query("SELECT * from persons ORDER BY family_name ASC")
    LiveData<List<Person>> getAllPersons();


    //VITA
    @Insert
    void insert(Person.Vita vita);

    @Query("DELETE FROM vitas")
    void deleteAllVitas();


    //STOLPERSTEINE
    @Insert
    void insert(Stolperstein stolperstein);

    @Query("DELETE FROM stolpersteine")
    void deleteAllStolpersteine();

    @Query("SELECT street_and_number from stolpersteine WHERE stone_id = :stoneId")
    String getAddress(int stoneId);

}
