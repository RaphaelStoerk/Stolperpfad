package de.uni_ulm.ismm.stolperpfad.info_display.stone_info.model;

public class Stolperstein {
    private int id;
    private double lat;
    private double lon;
    private String adress;

    public Stolperstein(int id, String adress, double lat, double lon) {
        this.id = id;
        this.adress = adress;
        this.lat = lat;
        this.lon = lon;
    }

    public double getLatitude() {
        return lat;
    }

    public double getLongitude() {
        return lon;
    }
}
