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
<<<<<<< HEAD
        public Vita(@NonNull int persId, String secZero, String secOne, String secTwo,
                    String secThree, String secFour, String secFive, String secSix, String secSeven,
                    String secEight, String secNine) {
            this.mPersId = persId;
            this.mSectionZero = secZero;
            this.mSectionOne = secOne;
            this.mSectionTwo = secTwo;
            this.mSectionThree = secThree;
            this.mSectionFour = secFour;
            this.mSectionFive = secFive;
            this.mSectionSix = secSix;
            this.mSectionSeven = secSeven;
            this.mSectionEight = secEight;
            this.mSectionNine = secNine;
=======
        public Vita(@NonNull int mPersId, String mSection0, String mSection1, String mSection2,
                    String mSection3, String mSection4, String mSection5, String mSection6,
                    String mSection7, String mSection8, String mSection9) {
            this.mPersId = mPersId;
            this.mSection0 = mSection0;
            this.mSection1 = mSection1;
            this.mSection2 = mSection2;
            this.mSection3 = mSection3;
            this.mSection4 = mSection4;
            this.mSection5 = mSection5;
            this.mSection6 = mSection6;
            this.mSection7 = mSection7;
            this.mSection8 = mSection8;
            this.mSection9 = mSection9;
>>>>>>> origin/general_design
        }

        // these are the getter-methods;

<<<<<<< HEAD
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
=======
        public int getMPersId() {
            return mPersId;
        }

        @NonNull
        public String getMSection0() {
            return mSection0;
        }

        @NonNull
        public String getMSection1() {
            return mSection1;
        }

        @NonNull
        public String getMSection2() {
            return mSection2;
        }

        public String getMSection3() {
            return mSection3;
        }

        public String getMSection4() { return mSection4; }

        public String getMSection5() {
            return mSection5;
        }

        public String getMSection6() {
            return mSection6;
        }

        public String getMSection7() {
            return mSection7;
        }

        public String getMSection8() {
            return mSection8;
        }

        public String getMSection9() {
            return mSection9;
>>>>>>> origin/general_design
        }
    }

}


