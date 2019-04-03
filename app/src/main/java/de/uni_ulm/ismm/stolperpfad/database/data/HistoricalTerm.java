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
 * Table 9: Institutions in Ulm (in Institution)
 * Table 10: moved in Ulm (in Institution)
 * Table 11: Historical Terms (here)
 */

// TABLE 11: historical terms
@Entity(tableName = "historical_terms")
public class HistoricalTerm {
    // id - name

    @PrimaryKey
    /*@NonNull
    @ColumnInfo(name = "id")
    private int mId;*/

    @NonNull
    @ColumnInfo(name = "name")
    private String mName;

    @NonNull
    @ColumnInfo(name = "explication")
    private String mExplication;


    //constructor
    public HistoricalTerm(/*@NonNull int id,*/ @NonNull String name, @NonNull String explication) {
        //this.mId = id;
        this.mName = name;
        this.mExplication = explication;
    }

    //getter
    /*public int getId() {
        return mId;
    }*/

    public String getName() {
        return mName;
    }

    public String getExplication(){return mExplication;}
}
