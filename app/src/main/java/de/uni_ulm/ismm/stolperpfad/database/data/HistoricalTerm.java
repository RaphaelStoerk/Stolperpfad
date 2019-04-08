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
    // id - name - explanation

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "histo_id")
    private int mHistoId;

    @NonNull
    @ColumnInfo(name = "name")
    private String mName;

    @NonNull
    @ColumnInfo(name = "explanation")
    private String mExplanation;


    //constructor
    public HistoricalTerm(@NonNull int histoId, @NonNull String name, @NonNull String explanation) {
        this.mHistoId = histoId;
        this.mName = name;
        this.mExplanation = explanation;
    }

    //getter
    public int getHistoId() {
        return mHistoId;
    }

    public String getName() {
        return mName;
    }

    public String getExplanation() {
        return mExplanation;
    }
}
