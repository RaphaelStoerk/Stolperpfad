package de.uni_ulm.ismm.stolperpfad;

import android.arch.persistence.room.Entity;
import android.support.annotation.NonNull;

/*
This class is the entity class of a person.
 */

@Entity(tableName = "persons")
public class Person {
    private String pFamilyName;
    private String pFirstName;

    public Person(@NonNull String familyName, @NonNull String firstName) {
        this.pFamilyName = familyName;
        this.pFirstName = firstName;
    }

    public String getFamilyName() {
        return this.pFamilyName;
    }

    public String getFirstName() {
        return this.pFamilyName;
    }
}
