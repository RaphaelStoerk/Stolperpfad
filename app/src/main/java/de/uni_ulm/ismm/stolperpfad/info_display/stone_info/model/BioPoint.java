package de.uni_ulm.ismm.stolperpfad.info_display.stone_info.model;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class BioPoint implements Serializable {

    public enum TYPE {
        BORN, DIED, MOVED, MARRIED, DEPORTATION, CUSTOM, NOT_VALID
    }

    private TYPE type;
    private String place;
    private int year;
    private String date;
    private String spouse;
    private String custom_title;
    private String custom_description;
    private String name;
    private boolean use_custom_desc;

    public BioPoint(TYPE type) {
        this.type = type;
        place = "text";
        year = 0;
        date = "text";
        name = "text";
    }

    public BioPoint(String name, JSONObject bio_as_json) {
        this.name = name;
        this.type = getType(bio_as_json);
    }

    private TYPE getType(JSONObject json) {
        try {
            switch (json.getString("punkt")) {
                case "geboren":
                    return TYPE.BORN;
                case "gestorben":
                    return TYPE.DIED;
                case "umzug":
                    return TYPE.MOVED;
                case "deportiert":
                    return TYPE.DEPORTATION;
                case "heirat":
                    return TYPE.MARRIED;
                case "custom":
                    return TYPE.CUSTOM;
                default:
                    return TYPE.NOT_VALID;
            }
        } catch(JSONException e) {
            return TYPE.NOT_VALID;
        }
    }

    private void createPoint(JSONObject bio_as_json) throws JSONException{
        if(use_custom_desc) {
            custom_title = bio_as_json.getString("title");
            custom_description = bio_as_json.getString("custom");
            return;
        }
        switch(type) {
            case BORN:
                place = bio_as_json.getString("ort");
                year = bio_as_json.getInt("jahr");
                date = bio_as_json.getString("datum");
                break;
            case DIED:
                place = bio_as_json.getString("ort");
                year = bio_as_json.getInt("jahr");
                date = bio_as_json.getString("datum");
                break;
            case MARRIED:
                place = bio_as_json.getString("ort");
                year = bio_as_json.getInt("jahr");
                date = bio_as_json.getString("datum");
                spouse = bio_as_json.getString("ehegatte");
                break;
            case DEPORTATION:
                place = bio_as_json.getString("ort");
                year = bio_as_json.getInt("jahr");
                date = bio_as_json.getString("datum");
                break;
            case MOVED:
                place = bio_as_json.getString("ort");
                year = bio_as_json.getInt("jahr");
                date = bio_as_json.getString("datum");
                break;
            case CUSTOM:
                custom_description = bio_as_json.getString("custom");
                use_custom_desc = true;
                break;
            case NOT_VALID:
            default:
        }
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setSpouse(String spouse) {
        if(type == TYPE.MARRIED) {
            this.spouse = spouse;
        }
    }

    public TYPE getType() {
        return type;
    }

    public String getPlace(){
        return place;
    }

    public int getYear() {
        return year;
    }

    public String getDate() {
        return date;
    }

    public String getName() {
        return name;
    }

    public String getSpouse() {
        if(type == TYPE.MARRIED) {
            return spouse;
        } else {
            return "";
        }
    }

    public boolean hasCustomDescription() {
        return use_custom_desc;
    }

    public String getCustomDescription() {
        if(use_custom_desc) {
            return custom_description;
        } else {
            return "";
        }
    }

    public String getTitle() {
        switch (type) {
            case BORN:
                return "Geburt";
            case DIED:
                return "Tod";
            case DEPORTATION:
                return "Deportation";
            case MARRIED:
                return "Hochzeit";
            case MOVED:
                return "Umzug";
            case CUSTOM:
                return custom_title;
            default:
                return "- kein Titel gefunden -";
        }
    }
}
