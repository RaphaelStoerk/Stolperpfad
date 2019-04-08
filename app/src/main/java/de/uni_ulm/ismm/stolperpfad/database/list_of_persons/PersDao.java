package de.uni_ulm.ismm.stolperpfad.database.list_of_persons;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import de.uni_ulm.ismm.stolperpfad.database.data.Institution;
import de.uni_ulm.ismm.stolperpfad.database.data.Person;
import de.uni_ulm.ismm.stolperpfad.database.data.Place;
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
     * Table 2: Marriages
     * Table 3: Children
     * Table 4: fled to
     * Table 5: Places
     * Table 6: moved to
     * Table 7: deported to
     * Table 8: Stolpersteine
     * Table 9: Institutions in Ulm
     * Table 10: moved in Ulm
     * (Table 11: Historical Terms; not treated here!)
     */

    //PERSONS
    @Insert
    void insert(Person pers);

    @Query("DELETE FROM persons")
    void deleteAllPersons();

    @Query("SELECT * from persons ORDER BY family_name ASC")
    LiveData<List<Person>> getAllPersons();


    //MARRIAGES
    @Insert
    void insert(Person.Marriage marriage);

    @Query("DELETE FROM married")
    void deleteAllMarriages();


    //CHILDREN
    @Insert
    void insert(Person.Children children);

    @Query("DELETE FROM children")
    void deleteAllChildren();


    //FLIGHT
    @Insert
    void insert(Person.Flight flight);

    @Query("DELETE FROM flight")
    void deleteAllFlights();


    //PLACES
    @Insert
    void insert(Place place);

    @Query("DELETE FROM places")
    void deleteAllPlaces();


    //MOVES (anywhere)
    @Insert
    void insert(Place.Move movedTo);

    @Query("DELETE FROM moved_to")
    void deleteAllMovesAround();


    //DEPORTATIONS
    @Insert
    void insert(Place.Deportation deportation);

    @Query("DELETE FROM deported_to")
    void deleteAllDeportations();


    //STOLPERSTEINE
    @Insert
    void insert(Stolperstein stolperstein);

    @Query("DELETE FROM stolpersteine")
    void deleteAllStolpersteine();

    @Query("SELECT street_and_number from stolpersteine WHERE stone_id = :persId")
    String getAddress(int persId);


    //INSTITUTIONS
    @Insert
    void insert(Institution institution);

    @Query("DELETE FROM institution_in_Ulm")
    void deleteAllInstitutions();


    //MOVES (Ulm)
    @Insert
    void insert(Institution.MoveInUlm movedInUlm);

    @Query("DELETE FROM moves_in_ulm")
    void deleteAllMovesInUlm();

}
