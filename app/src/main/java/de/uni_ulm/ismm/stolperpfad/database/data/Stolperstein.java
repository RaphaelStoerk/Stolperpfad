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
 * Table 8: Stolpersteine (here)
 * Table 9: Institutions in Ulm (in Institution)
 * Table 10: moved in Ulm (in Institution)
 * Table 11: Historical Terms (in HistoricTerm)
 */

// TABLE 8: Stolpersteine
@Entity(tableName = "Stolpersteine")
public class Stolperstein {
    // stone id - street/location - coordinate 1 - coordinate 2 - coordinate 3

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    private int mId;

    @NonNull
    @ColumnInfo(name = "street_and_number")
    private String mStreet;

    /*@NonNull
    @ColumnInfo(name = "first_coordinate")
    private

    @NonNull
    @ColumnInfo(name = "second_coordinate")
    private

    @NonNull
    @ColumnInfo(name = "third_coordinate")
    private*/


    //constructor
    public Stolperstein(int id, String street){ //TODO: add coordinates
        this.mId = id;
        this.mStreet = street;

    }

    //getter
    public int getStoneId() {
        return mId;
    }

    @NonNull
    public String getStreet() {
        return mStreet;
    }
}
