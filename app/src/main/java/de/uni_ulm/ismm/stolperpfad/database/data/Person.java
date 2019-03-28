package de.uni_ulm.ismm.stolperpfad.database.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

/**
 * Overview over the database tables:
 * Table 1: Persons (here)
 * Table 2: Marriages (here)
 * Table 3: Children (here)
 * Table 4: fled to (here)
 * Table 5: Places (in Place)
 * Table 6: moved to (in Place)
 * Table 7: deported to (in Place)
 * Table 8: Stolpersteine (in Stolperstein)
 * Table 9: Institutions in Ulm (in Institution)
 * Table 10: moved in Ulm (in Institution)
 * Table 11: Historical Terms (in HistoricTerm)
 */


// TABLE 1:
// the most important database table, the persons
@Entity(tableName = "persons")
public class Person {

    // id - first name - last name - birth name - year* - place of birth - year† - historical term - Stolperstein

    // we have to work with Integer instead of int because an 'int' can't be null,
    // but an 'Integer' can and sometimes the table entries are null
    @PrimaryKey
    /*@NonNull
    @ColumnInfo(name = "id")
    private int mId;*/

    @NonNull
    @ColumnInfo(name = "first_name")
    private String mFstName;

    @NonNull
    @ColumnInfo(name = "family_name")
    private String mFamName;

    /*@ColumnInfo(name = "birth_name")
    private String mBiName;

    @ColumnInfo(name = "birth_year")
    private Integer mBiYear;

    @ColumnInfo(name = "birth_place")
    private Integer mBiPlace;

    @ColumnInfo(name = "death_year")
    private Integer mDeYear;

    @ColumnInfo(name = "historical_term")
    private Integer mHisTerm;

    @ColumnInfo(name = "stolperstein")
    private Integer mStolperstein;*/

    // constructor
    public Person(/*@NonNull int id,*/ @NonNull String fstName, @NonNull String famName/*, String biName,
                  Integer biYear, Integer biPlace, Integer deYear, Integer hisTerm, Integer stolperstein*/) {
        //this.mId = id;
        this.mFstName = fstName;
        this.mFamName = famName;
        /*this.mBiName = biName;
        this.mBiYear = biYear;
        this.mBiPlace = biPlace;
        this.mDeYear = deYear;
        this.mHisTerm = hisTerm;
        this.mStolperstein = stolperstein;*/
    }

    // these are the getter-methods;
    // we don't have setter-methods because the persons are set by the database
    /*public int getId() {
        return this.mId;
    }*/

    public String getFstName() {
        return this.mFstName;
    }

    public String getFamName() {
        return this.mFamName;
    }

    /*public String getBiName() {
        return this.mBiName;
    }

    public Integer getBiYear() {
        return this.mBiYear;
    }

    public Integer getBiPlace() {
        return this.mBiPlace;
    }

    public Integer getDeYear() {
        return this.mDeYear;
    }

    public Integer getHisTerm() {
        return this.mHisTerm;
    }

    public Integer getStolperstein() {
        return this.mStolperstein;
    }*/


    // these are the additional tables we need to store the data

    // TABLE 2: marriages
    @Entity(tableName = "married")
    class Marriage {
        // first person - second person - year
        @NonNull
        @ColumnInfo(name = "id_pers1")
        private int mId1;

        @NonNull
        @ColumnInfo(name = "id_pers2")
        private int mId2;

        @NonNull
        @ColumnInfo(name = "year")
        private int mYear;

        //constructor
        public Marriage(int id1, int id2, int year) {
            this.mId1 = id1;
            this.mId2 = id2;
            this.mYear = year;
        }

        //getter
        public int getId1() {
            return mId1;
        }

        public int getId2() {
            return mId2;
        }

        public int getYear() {
            return mYear;
        }
    }

    // TABLE 3: children
    @Entity(tableName = "children")
    class Children{
        // id parent (normally mother) - id first child - id second child - id third child

        @NonNull
        @ColumnInfo(name = "id_parent")
        private int mIdParent;

        @NonNull
        @ColumnInfo(name = "first_child")
        private int mIdFstChild;

        @ColumnInfo(name = "second_child")
        private Integer mIdSndChild;

        @ColumnInfo(name = "third_child")
        private Integer mIdTrdChild;

        //constructor
        public Children(int idParent, int idFstChild, Integer idSndChild, Integer idTrdChild) {
            this.mIdParent = idParent;
            this.mIdFstChild = idFstChild;
            this.mIdSndChild = idSndChild;
            this.mIdTrdChild = idTrdChild;
        }

        //getter-methods
        public int getIdParent() {
            return mIdParent;
        }

        public int getIdFstChild() {
            return mIdFstChild;
        }

        public Integer getIdSndChild() {
            return mIdSndChild;
        }

        public Integer getIdTrdChild() {
            return mIdTrdChild;
        }
    }

    // TABLE 4: flight to the USA, Great Britain or Palestine
    @Entity(tableName = "flight")
    class Flight{
        // id person - country - year

        @NonNull
        @ColumnInfo(name = "id_person")
        private int mId;

        @NonNull
        @ColumnInfo(name = "country")
        private String mCountry;

        @NonNull
        @ColumnInfo(name = "year")
        private int mYear;

        //constructor
        public Flight(int id, String country, int year){
            this.mId = id;
            this.mCountry = country;
            this.mYear = year;
        }

        public int getFlightId() {
            return mId;
        }

        public String getCountry() {
            return mCountry;
        }

        public int getYear() {
            return mYear;
        }
    }


}


