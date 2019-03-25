package de.uni_ulm.ismm.stolperpfad.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
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

    @NonNull
    @ColumnInfo(name = "id")
    private int mId;

    @NonNull
    @ColumnInfo(name = "name")
    private String mName;


    //constructor
    public HistoricalTerm(int id, @NonNull String name) {
        this.mId = id;
        this.mName = name;
    }

    //getter
    public int getHistId() {
        return mId;
    }

    @NonNull
    public String getHistName() {
        return mName;
    }
}
