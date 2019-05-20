package de.uni_ulm.ismm.stolperpfad.database.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Overview over the database tables:
 * Table 1: Persons (in Person)
 * Table 2: Vita (in Person)
 * Table 3: Stolpersteine (here)
 * Table 4: Historical Terms (in HistoricalTerm)
 */

// TABLE 3: Stolpersteine
@Entity(tableName = "Stolpersteine")
public class Stolperstein {
    // stone id - address - latitude (Breitangrad) - longitude (Längengrad)

    @PrimaryKey
    @ColumnInfo(name = "stone_id")
    private int mStoneId;

    @ColumnInfo(name = "street_and_number")
    private String mAddress;

    @ColumnInfo(name = "latitude")
    private double mLatitude;

    @ColumnInfo(name = "longitude")
    private double mLongitude;

    //constructor
    public Stolperstein(int stoneId, String address, double latitude, double longitude){
        this.mStoneId = stoneId;
        this.mAddress = address;
        this.mLatitude = latitude;
        this.mLongitude = longitude;
    }

    //getter
    public int getStoneId() {
        return mStoneId;
    }

    public String getAddress() {
        return mAddress;
    }

    public double getLatitude(){
        return mLatitude;
    }

    public double getLongitude(){
        return mLongitude;
    }

}
