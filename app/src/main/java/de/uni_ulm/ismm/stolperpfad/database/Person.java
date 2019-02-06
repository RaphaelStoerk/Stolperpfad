package de.uni_ulm.ismm.stolperpfad.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "pers_table")
public class Person {

    @PrimaryKey//(autoGenerate = true)
    //private int id;

    @NonNull
    @ColumnInfo(name = "first_name")
    private String mFstName;

    @NonNull
    @ColumnInfo(name = "family_name")
    private String mFamName;

    public Person (@NonNull String fstName, @NonNull String famName) {
        this.mFstName = fstName;
        this.mFamName = famName;
    }

    public String getFstName(){return this.mFstName;}
    public String getFamName(){return this.mFamName;}
    //public int getId(){return this.id;}

}
