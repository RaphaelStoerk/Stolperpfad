package de.uni_ulm.ismm.stolperpfad.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import de.uni_ulm.ismm.stolperpfad.database.data.HistoricalTerm;
import de.uni_ulm.ismm.stolperpfad.database.data.Person;
import de.uni_ulm.ismm.stolperpfad.database.data.Stolperstein;

/**
 * READ ME:
 * If you want to use given parameters from the methods in the SQL queries,
 * you need to type a ':' right before the variable name.
 */


@Dao
public interface StolperpfadeDao {
    /**
     * Overview over the database tables:
     * Table 1: Persons
     * Table 2: Vita
     * Table 3: Stolpersteine
     * Table 4: Historical Terms
     */

    //PERSONS
    @Insert
    void insert(Person pers);

    @Query("DELETE FROM persons")
    void deleteAllPersons();

    @Query("SELECT * from persons ORDER BY family_name ASC")
    List<Person> getAllPersons();

    @Query("SELECT * from persons WHERE historical_terms LIKE '%' || :histoTerm || '%' ORDER BY family_name ASC")
    List<Person> getAllConcernedPersons(String histoTerm);

    //get all persons with stoneId
    @Query("SELECT * from persons WHERE stolperstein = :stoneId")
    List<Person> getPersonsFromStone(int stoneId);

    //VITA
    @Insert
    void insert(Person.Vita vita);

    @Query("DELETE FROM vitas")
    void deleteAllVitas();

    @Query("SELECT * FROM vitas WHERE pers_id = :persId")
    Person.Vita getVita(int persId);

    //STOLPERSTEINE
    @Insert
    void insert(Stolperstein stolperstein);

    @Query("DELETE FROM stolpersteine")
    void deleteAllStolpersteine();

    @Query("SELECT * from stolpersteine")
    List<Stolperstein> getAllStones();

    @Query("SELECT street_and_number from stolpersteine WHERE stone_id = :stoneId")
    String getAddress(int stoneId);

    //HISTORICAL TERMS
    @Insert
    void insert(HistoricalTerm histoTerm);

    @Query("DELETE FROM historical_terms")
    void deleteAllTerms();

    @Query("SELECT * from historical_terms ORDER BY name ASC")
    List<HistoricalTerm> getAllTerms();

    @Query("SELECT explanation from historical_terms WHERE name = :histoTerm")
    String getExplanation(String histoTerm);
}


