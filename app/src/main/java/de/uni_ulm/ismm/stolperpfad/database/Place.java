package de.uni_ulm.ismm.stolperpfad.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

/**
 * Overview over the database tables:
 * Table 1: Persons (in Person)
 * Table 2: Marriages (in Person)
 * Table 3: Children (in Person)
 * Table 4: fled to (in Person)
 * Table 5: Places (here)
 * Table 6: moved to (here)
 * Table 7: deported to (here)
 * Table 8: Stolpersteine (in Stolperstein)
 * Table 9: Institutions in Ulm (in Institution)
 * Table 10: moved in Ulm (in Institution)
 * Table 11: Historical Terms (in HistoricTerm)
 */

// TABLE 5: places
@Entity(tableName = "places")
public class Place {
    // id - Ortsname - nach - inDas - inDie - in
    // we need the booleans to identify if the place is a town, an institution or a birth place

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    private int mId;

    @NonNull
    @ColumnInfo(name = "name")
    private String mName;

    @NonNull
    @ColumnInfo(name = "nach")
    private boolean mNach;

    @NonNull
    @ColumnInfo(name = "inDas")
    private boolean mInDas;

    @NonNull
    @ColumnInfo(name = "inDie")
    private boolean mInDie;

    @NonNull
    @ColumnInfo(name = "in")
    private boolean mIn;

    // constructor
    public Place(@NonNull int id, @NonNull String name, @NonNull boolean nach, @NonNull boolean inDas, @NonNull boolean inDie, @NonNull boolean in) {
        this.mId = id;
        this.mName = name;
        this.mNach = nach;
        this.mInDas = inDas;
        this.mInDie = inDie;
        this.mIn = in;
    }

    // getter-methods
    public int getPlaceId() {
        return mId;
    }

    @NonNull
    public String getPlaceName() {
        return mName;
    }

    public boolean toPlace() {
        return mNach;
    }

    public boolean toInstDas() {
        return mInDas;
    }

    public boolean toInstDie() {
        return mInDie;
    }

    public boolean birthPlace() {
        return mIn;
    }


    // TABLE 6: moves to other cities
    @Entity(tableName = "moved_to")
    class Move {
        // id_person - id_place - year

        @NonNull
        @ColumnInfo(name = "id_person")
        private int mPersId;

        @NonNull
        @ColumnInfo(name = "id_place")
        private int mPlaceId;

        @NonNull
        @ColumnInfo(name = "year")
        private int mYear;

        //constructor
        public Move(int persId, int placeId, int year) {
            this.mPersId = persId;
            this.mPlaceId = placeId;
            this.mYear = year;
        }

        //getter
        public int getPersId() {
            return mPersId;
        }

        public int getPlaceId() {
            return mPlaceId;
        }

        public int getYear() {
            return mYear;
        }
    }

    // TABLE 7: deportations
    @Entity(tableName = "deported_to")
    class Deportation {
        // id_person - id_place - year

        @NonNull
        @ColumnInfo(name = "id_person")
        private int mPersId;

        @NonNull
        @ColumnInfo(name = "id_place")
        private int mPlaceId;

        @NonNull
        @ColumnInfo(name = "year")
        private int mYear;

        //constructor
        public Deportation(int persId, int placeId, int year) {
            this.mPersId = persId;
            this.mPlaceId = placeId;
            this.mYear = year;
        }

        //getter
        public int getPersId() {
            return mPersId;
        }

        public int getPlaceId() {
            return mPlaceId;
        }

        public int getYear() {
            return mYear;
        }
    }

}
