package de.uni_ulm.ismm.stolperpfad;

import android.support.annotation.NonNull;

/*
This class is the entity class of a person.
 */
public class Person {
    private String pName;

    public Person(@NonNull String name){this.pName = name;}

    public String getName(){return this.pName;}
}
