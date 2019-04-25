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

    public String getEntireName() {return mFstName + " " + mFamName;   }


    // TABLE 2:
    @Entity(tableName = "vitas")
    public static class Vita {

        // id - section 1 - section 2 - section 3 - section 4 - section 5 - section 6 - section 7
        // - section 8 - section 9 - section 10

        @PrimaryKey
        @NonNull
        @ColumnInfo(name = "pers_id")
        private int mPersId;

        @ColumnInfo(name = "sectionZero")
        private String mSectionZero;

        @ColumnInfo(name = "sectionOne")
        private String mSectionOne;

        @ColumnInfo(name = "sectionTwo")
        private String mSectionTwo;

        @ColumnInfo(name = "sectionThree")
        private String mSectionThree;

        @ColumnInfo(name = "sectionFour")
        private String mSectionFour;

        @ColumnInfo(name = "sectionFive")
        private String mSectionFive;

        @ColumnInfo(name = "sectionSix")
        private String mSectionSix;

        @ColumnInfo(name = "sectionSeven")
        private String mSectionSeven;

        @ColumnInfo(name = "sectionEight")
        private String mSectionEight;

        @ColumnInfo(name = "sectionNine")
        private String mSectionNine;

        //Constructor
        public Vita(@NonNull int persId, String sectionZero, String sectionOne, String sectionTwo,
                    String sectionThree, String sectionFour, String sectionFive, String sectionSix,
                    String sectionSeven, String sectionEight, String sectionNine) {
            this.mPersId = persId;
            this.mSectionZero = sectionZero;
            this.mSectionOne = sectionOne;
            this.mSectionTwo = sectionTwo;
            this.mSectionThree = sectionThree;
            this.mSectionFour = sectionFour;
            this.mSectionFive = sectionFive;
            this.mSectionSix = sectionSix;
            this.mSectionSeven = sectionSeven;
            this.mSectionEight = sectionEight;
            this.mSectionNine = sectionNine;
        }

        // these are the getter-methods;

        public int getPersId() {
            return mPersId;
        }

        public String getSectionZero() {
            return mSectionZero;
        }

        public String getSectionOne() {
            return mSectionOne;
        }

        public String getSectionTwo() {
            return mSectionTwo;
        }

        public String getSectionThree() {
            return mSectionThree;
        }

        public String getSectionFour() {
            return mSectionFour;
        }

        public String getSectionFive() {
            return mSectionFive;
        }

        public String getSectionSix() {
            return mSectionSix;
        }

        public String getSectionSeven() {
            return mSectionSeven;
        }

        public String getSectionEight() {
            return mSectionEight;
        }

        public String getSectionNine() {
            return mSectionNine;
        }
    }

}


