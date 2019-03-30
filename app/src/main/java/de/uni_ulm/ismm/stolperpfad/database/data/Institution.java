package de.uni_ulm.ismm.stolperpfad.database.data;

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
 * Table 5: Places (in Place)
 * Table 6: moved to (in Place)
 * Table 7: deported to (in Place)
 * Table 8: Stolpersteine (in Stolperstein)
 * Table 9: Institutions in Ulm (here)
 * Table 10: moved in Ulm (here)
 * Table 11: Historical Terms (in HistoricTerm)
 */

// TABLE 9: Institutions in Ulm, such as 'Judenhaus'
@Entity(tableName = "institution_in_Ulm")
public class Institution {
    // id - name

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    private int mId;

    @NonNull
    @ColumnInfo(name = "name")
    private String mName;

    //constructor
    public Institution(int id, @NonNull String name) {
        this.mId = id;
        this.mName = name;
    }

    //getter
    public int getId() {
        return mId;
    }

    @NonNull
    public String getName() {
        return mName;
    }


    // TABLE 10: moved in Ulm
    @Entity(tableName = "moves_in_ulm")
    public static class MoveInUlm{
        // id person - id institution - year

        @PrimaryKey
        @NonNull
        @ColumnInfo(name = "person_id")
        private int mPersId;

        @NonNull
        @ColumnInfo(name = "institution_id")
        private int mInstId;

        @NonNull
        @ColumnInfo(name = "year")
        private int mYear;

        //constructor
        public MoveInUlm(int persId, int instId, int year) {
            this.mPersId = persId;
            this.mInstId = instId;
            this.mYear = year;
        }

        //getter
        public int getPersId() {
            return mPersId;
        }

        public int getInstId() {
            return mInstId;
        }

        public int getYear() {
            return mYear;
        }
    }
}
