package de.uni_ulm.ismm.stolperpfad.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;


// TABLE 1:
// the most important database table, the persons
@Entity(tableName = "persons")
public class Person {

    // Id - Vorname - Nachname - Geburtsname - Jahr* - Geburtsort - Jahr† - Geschichte - Stolperstein

    // we have to work with Integer instead of int because an 'int' can't be null,
    // but an 'Integer' can and sometimes the table entries are null
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    private int mId;

    @NonNull
    @ColumnInfo(name = "first_name")
    private String mFstName;

    @NonNull
    @ColumnInfo(name = "family_name")
    private String mFamName;

    @ColumnInfo(name = "birth_name")
    private String mBiName;

    @ColumnInfo(name = "birth_year")
    private Integer mBiYear;

    @ColumnInfo(name = "birth_place")
    private Integer mBiPlace;

    @ColumnInfo(name = "death_year")
    private Integer mDeYear;

    @ColumnInfo(name = "historic_term")
    private Integer mHisTerm;

    @ColumnInfo(name = "stolperstein")
    private Integer mStolperstein;

    // constructor
    public Person(@NonNull int id, @NonNull String fstName, @NonNull String famName, String biName,
                  Integer biYear, Integer biPlace, Integer deYear, Integer hisTerm, Integer stolperstein) {
        this.mId = id;
        this.mFstName = fstName;
        this.mFamName = famName;
        this.mBiName = biName;
        this.mBiYear = biYear;
        this.mBiPlace = biPlace;
        this.mDeYear = deYear;
        this.mHisTerm = hisTerm;
        this.mStolperstein = stolperstein;
    }

    // these are the getter-methods;
    // we don't have setter-methods because the persons are set by the database
    public int getId() {
        return this.mId;
    }

    public String getFstName() {
        return this.mFstName;
    }

    public String getFamName() {
        return this.mFamName;
    }

    public String getBiName() {
        return this.mBiName;
    }

    public Integer getBiYear() {
        return this.mBiYear;
    }

    public Integer getBiPlace() {
        return this.mBiPlace;
    }

    public Integer getDeaYear() {
        return this.mDeYear;
    }

    public Integer getHisTerm() {
        return this.mHisTerm;
    }

    public Integer getStolperstein() {
        return this.mStolperstein;
    }


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

    // TABLE 3:
    // children
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

}


