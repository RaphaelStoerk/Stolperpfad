package de.uni_ulm.ismm.stolperpfad.database.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

/**
 * Overview over the database tables:
 * Table 1: Persons (in Person)
 * Table 2: Vita (in Person)
 * Table 3: Stolpersteine (in Stolperstein)
 * Table 4: Historical Terms (here)
 */

// TABLE 11: historical terms
@Entity(tableName = "historical_terms")
public class HistoricalTerm {
    // name - explanation

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "name")
    private String mName;

    @NonNull
    @ColumnInfo(name = "explanation")
    private String mExplanation;


    //constructor
    public HistoricalTerm(@NonNull String name, @NonNull String explanation) {
        this.mName = name;
        this.mExplanation = explanation;
    }

    //getter
    public String getName() {
        return mName;
    }

    public String getExplanation() {
        return mExplanation;
    }
}
