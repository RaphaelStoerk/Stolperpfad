package de.uni_ulm.ismm.stolperpfad.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "places")
public class Place {

    // id - Ortsname - nach - inDas - inDie - in
    // we need the booleans to identify if the place is a town, an institution or a birth place

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    private int mId;

    @NonNull
    @ColumnInfo(name = "name")
    private String mName;

    @NonNull
    @ColumnInfo(name = "nach")
    private boolean mNach;

    @NonNull
    @ColumnInfo(name = "inDas")
    private boolean mInDas;

    @NonNull
    @ColumnInfo(name = "inDie")
    private boolean mInDie;

    @NonNull
    @ColumnInfo(name = "in")
    private boolean mIn;

    // constructor
    public Place (@NonNull int id, @NonNull String name, @NonNull boolean nach, @NonNull boolean inDas, @NonNull boolean inDie, @NonNull boolean in){
        this.mId = id;
        this.mName = name;
        this.mNach = nach;
        this.mInDas = inDas;
        this.mInDie = inDie;
        this.mIn = in;
    }

    // getter-methods

    public int getmId() {
        return mId;
    }

    @NonNull
    public String getmName() {
        return mName;
    }

    public boolean ismNach() {
        return mNach;
    }

    public boolean ismInDas() {
        return mInDas;
    }

    public boolean ismInDie() {
        return mInDie;
    }

    public boolean ismIn() {
        return mIn;
    }
}
