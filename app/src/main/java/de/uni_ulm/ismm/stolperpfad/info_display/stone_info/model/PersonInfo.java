package de.uni_ulm.ismm.stolperpfad.info_display.stone_info.model;

import java.io.Serializable;
import java.util.ArrayList;

public class PersonInfo implements Serializable {
    private int id;
    private String vorname;
    private String nachname;
    private String geburtsname;
    private Stolperstein stolperstein;
    private ArrayList<BioPoint> biography;
    private BioPoint birth, death;

    public PersonInfo(int id, String vorname, String nachname, String geburtsname) {
        this.vorname = vorname;
        this.nachname = nachname;
        this.geburtsname = geburtsname;
        this.id = id;
        biography = new ArrayList<>();
        biography.add(new BioPoint(BioPoint.TYPE.BORN));
        biography.add(new BioPoint(BioPoint.TYPE.MOVED));
        biography.add(new BioPoint(BioPoint.TYPE.DIED));
    }

    public String getVorname() {
        return vorname;
    }

    public String getNachname() {
        return nachname;
    }

    public String getGeburtsname() {
        return geburtsname;
    }

    public int getId() {
        return id;
    }

    public Stolperstein getStolperstein() {
        return stolperstein;
    }

    public BioPoint getBirth() {
        if(this.birth != null) {
            return birth;
        } else {
            for(BioPoint b : biography) {
                if(b.getType() == BioPoint.TYPE.BORN) {
                    return this.birth = b;
                }
            }
        }
        return null;
    }

    public BioPoint getDeath() {
        if(this.death != null) {
            return death;
        } else {
            for(BioPoint b : biography) {
                if(b.getType() == BioPoint.TYPE.DIED) {
                    return this.death = b;
                }
            }
        }
        return null;
    }

    public ArrayList<BioPoint> getBio() {
        return biography;
    }

    public String getListName() {
        return "<b>" + nachname + "</b>, " + vorname + (geburtsname.length() != 0 ? ("; geb. " + geburtsname) : "");
    }
}
