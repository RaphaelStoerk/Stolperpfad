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
    // stone id - street/location - latitude (Breitangrad) - longitude (Längengrad)

    @PrimaryKey
    /*@NonNull
    @ColumnInfo(name = "id")
    private int mId;*/

    @NonNull
    @ColumnInfo(name = "street_and_number")
    private String mStreet;

    @NonNull
    @ColumnInfo(name = "latitude")
    private double mLatitude;

    @NonNull
    @ColumnInfo(name = "longitude")
    private double mLongitude;



    //constructor
    public Stolperstein(/*int id,*/ String street, double latitude, double longitude){
        //this.mId = id;
        this.mStreet = street;
        this.mLatitude = latitude;
        this.mLongitude = longitude;
    }

    //getter
    /*public int getId() {
        return mId;
    }*/

    public String getStreet() {
        return mStreet;
    }

    public double getLatitude(){
        return mLatitude;
    }

    public double getLongitude(){
        return mLongitude;
    }

}
