package de.uni_ulm.ismm.stolperpfad.database.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

/**
 * Overview over the database tables:
 * Table 1: Persons (here)
 * Table 2: Vita (here)
 * Table 3: Stolpersteine (in Stolperstein)
 * Table 4: Historical Terms (in HistoricalTerm)
 */


// TABLE 1:
// the most important database table, the persons
@Entity(tableName = "persons")
public class Person {

    // id - first name - last name - birth name - historical term - Stolperstein

    // if some int-entries in a table can be null, we have to work with Integer instead of int
    // because an 'int' can't be null, but an 'Integer' can
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "pers_id")
    private int mPersId;

    @NonNull
    @ColumnInfo(name = "first_name")
    private String mFstName;

    @NonNull
    @ColumnInfo(name = "family_name")
    private String mFamName;

    @ColumnInfo(name = "birth_name")
    private String mBiName;

    @ColumnInfo(name = "historical_terms")
    private String mHisTerms;

    @ColumnInfo(name = "stolperstein")
    private int mStolperstein;

    // constructor
    public Person(@NonNull int persId, @NonNull String fstName, @NonNull String famName, String biName,
                  String hisTerms, int stolperstein) {
        this.mPersId = persId;
        this.mFstName = fstName;
        this.mFamName = famName;
        this.mBiName = biName;
        this.mHisTerms = hisTerms;
        this.mStolperstein = stolperstein;
    }

    // these are the getter-methods;
    // we don't have setter-methods because the persons are set by the database

    public int getPersId() {
        return this.mPersId;
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

    public String getHisTerms() {
        return this.mHisTerms;
    }

    public int getStolperstein() {
        return this.mStolperstein;
    }


    // TABLE 2:
    @Entity(tableName = "vitas")
    public static class Vita {

        // id - section 1 - section 2 - section 3 - section 4 - section 5 - section 6 - section 7
        // - section 8 - section 9 - section 10

        @PrimaryKey
        @NonNull
        @ColumnInfo(name = "pers_id")
        private int mPersId;

        @ColumnInfo(name = "section0")
        private String mSection0;

        @ColumnInfo(name = "section1")
        private String mSection1;

        @ColumnInfo(name = "section2")
        private String mSection2;

        @ColumnInfo(name = "section3")
        private String mSection3;

        @ColumnInfo(name = "section4")
        private String mSection4;

        @ColumnInfo(name = "section5")
        private String mSection5;

        @ColumnInfo(name = "section6")
        private String mSection6;

        @ColumnInfo(name = "section7")
        private String mSection7;

        @ColumnInfo(name = "section8")
        private String mSection8;

        @ColumnInfo(name = "section9")
        private String mSection9;

        //Constructor
        public Vita(@NonNull int persId, String sec0, String sec1, String sec2,
                    String sec3, String sec4, String sec5, String sec6, String sec7, String sec8, String sec9) {
            this.mPersId = persId;
            this.mSection0 = sec0;
            this.mSection1 = sec1;
            this.mSection2 = sec2;
            this.mSection3 = sec3;
            this.mSection4 = sec4;
            this.mSection5 = sec5;
            this.mSection6 = sec6;
            this.mSection7 = sec7;
            this.mSection8 = sec8;
            this.mSection9 = sec9;
        }

        // these are the getter-methods;

        public int getmPersId() {
            return mPersId;
        }

        @NonNull
        public String getmSection0() {
            return mSection0;
        }

        @NonNull
        public String getmSection1() {
            return mSection1;
        }

        @NonNull
        public String getmSection2() {
            return mSection2;
        }

        public String getmSection3() {
            return mSection3;
        }

        public String getmSection4() {
            return mSection4;
        }

        public String getmSection5() {
            return mSection5;
        }

        public String getmSection6() {
            return mSection6;
        }

        public String getmSection7() {
            return mSection7;
        }

        public String getmSection8() {
            return mSection8;
        }

        public String getmSection9() {
            return mSection9;
        }
    }

}


