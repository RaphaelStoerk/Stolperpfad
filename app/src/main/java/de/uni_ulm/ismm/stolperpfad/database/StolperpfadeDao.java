package de.uni_ulm.ismm.stolperpfad.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.ArrayList;

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
    ArrayList<Person> getAllPersons();

    @Query("SELECT first_name from persons WHERE pers_id = :persId")
    String getFirstName(int persId);

    @Query("SELECT family_name from persons WHERE pers_id = :persId")
    String getFamilyName(int persId);

    @Query("SELECT birth_name from persons WHERE pers_id = :persId")
    String getBirthName(int persId);

    @Query("SELECT * from persons WHERE historical_terms LIKE '%' || :histoTerm || '%'")
    ArrayList<Person> getAllConcernedPersons(String histoTerm);

    //get a person's Stolperstein id
    @Query("SELECT stolperstein from persons WHERE pers_id = :persId")
    int getStolperstein(int persId);


    //VITA
    @Insert
    void insert(Person.Vita vita);

    @Query("DELETE FROM vitas")
    void deleteAllVitas();

    @Query("SELECT section0 from vitas WHERE pers_id = :persId")
    String getSection0(int persId);

    @Query("SELECT section1 from vitas WHERE pers_id = :persId")
    String getSection1(int persId);

    @Query("SELECT section2 from vitas WHERE pers_id = :persId")
    String getSection2(int persId);

    @Query("SELECT section3 from vitas WHERE pers_id = :persId")
    String getSection3(int persId);

    @Query("SELECT section4 from vitas WHERE pers_id = :persId")
    String getSection4(int persId);

    @Query("SELECT section5 from vitas WHERE pers_id = :persId")
    String getSection5(int persId);

    @Query("SELECT section6 from vitas WHERE pers_id = :persId")
    String getSection6(int persId);

    @Query("SELECT section7 from vitas WHERE pers_id = :persId")
    String getSection7(int persId);

    @Query("SELECT section8 from vitas WHERE pers_id = :persId")
    String getSection8(int persId);

    @Query("SELECT section9 from vitas WHERE pers_id = :persId")
    String getSection9(int persId);


    //STOLPERSTEINE
    @Insert
    void insert(Stolperstein stolperstein);

    @Query("DELETE FROM stolpersteine")
    void deleteAllStolpersteine();

    @Query("SELECT * from stolpersteine")
    ArrayList<Stolperstein> getAllStones();

    @Query("SELECT street_and_number from stolpersteine WHERE stone_id = :stoneId")
    String getAddress(int stoneId);

    @Query("SELECT latitude from stolpersteine WHERE stone_id = :stoneId")
    double getLatitude(int stoneId);

    @Query("SELECT longitude from stolpersteine WHERE stone_id = :stoneId")
    double getLongitude(int stoneId);


    //HISTORICAL TERMS
    @Insert
    void insert(HistoricalTerm histoTerm);

    @Query("DELETE FROM historical_terms")
    void deleteAll();

    @Query("SELECT * from historical_terms ORDER BY name ASC")
    ArrayList<HistoricalTerm> getAllTerms();

    @Query("SELECT explanation from historical_terms WHERE name = :histoTerm")
    String getExplanation(String histoTerm);

}


