package de.uni_ulm.ismm.stolperpfad.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.ArrayList;
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

    @Query("SELECT first_name from persons WHERE pers_id = :persId")
    String getFirstName(int persId);

    @Query("SELECT family_name from persons WHERE pers_id = :persId")
    String getFamilyName(int persId);

    @Query("SELECT birth_name from persons WHERE pers_id = :persId")
    String getBirthName(int persId);

    @Query("SELECT * from persons WHERE historical_terms LIKE '%' || :histoTerm || '%'")
    List<Person> getAllConcernedPersons(String histoTerm);

    //get a person's Stolperstein id
    @Query("SELECT stolperstein from persons WHERE pers_id = :persId")
    int getStolperstein(int persId);


    //VITA
    @Insert
    void insert(Person.Vita vita);

    @Query("DELETE FROM vitas")
    void deleteAllVitas();

    @Query("SELECT * FROM vitas WHERE pers_id = :persId")
    List<Person.Vita> getVita(int persId);

    @Query("SELECT sectionZero from vitas WHERE pers_id = :persId")
    String getSectionZero(int persId);

    @Query("SELECT sectionOne from vitas WHERE pers_id = :persId")
    String getSectionOne(int persId);

    @Query("SELECT sectionTwo from vitas WHERE pers_id = :persId")
    String getSectionTwo(int persId);

    @Query("SELECT sectionThree from vitas WHERE pers_id = :persId")
    String getSectionThree(int persId);

    @Query("SELECT sectionFour from vitas WHERE pers_id = :persId")
    String getSectionFour(int persId);

    @Query("SELECT sectionFive from vitas WHERE pers_id = :persId")
    String getSectionFive(int persId);

    @Query("SELECT sectionSix from vitas WHERE pers_id = :persId")
    String getSectionSix(int persId);

    @Query("SELECT sectionSeven from vitas WHERE pers_id = :persId")
    String getSectionSeven(int persId);

    @Query("SELECT sectionEight from vitas WHERE pers_id = :persId")
    String getSectionEight(int persId);

    @Query("SELECT sectionNine from vitas WHERE pers_id = :persId")
    String getSectionNine(int persId);


    //STOLPERSTEINE
    @Insert
    void insert(Stolperstein stolperstein);

    @Query("DELETE FROM stolpersteine")
    void deleteAllStolpersteine();

    @Query("SELECT * from stolpersteine")
    List<Stolperstein> getAllStones();

    @Query("SELECT * from stolpersteine WHERE stone_id = :stoneId")
    List<Stolperstein> getStone(int stoneId);

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
    List<HistoricalTerm> getAllTerms();

    @Query("SELECT explanation from historical_terms WHERE name = :histoTerm")
    String getExplanation(String histoTerm);

}


