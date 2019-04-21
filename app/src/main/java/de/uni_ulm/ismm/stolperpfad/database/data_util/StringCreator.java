package de.uni_ulm.ismm.stolperpfad.database.data_util;

import de.uni_ulm.ismm.stolperpfad.info_display.stone_info.model.BioPoint;

public class StringCreator {

    /**
     * An example method for creating the Strings for the bio
     * @return
     */
    public static String makeIntroText(String name, String date, String place) {
        return name + " wurde am " + date + " in " + place + " geboren.";
    }

    public static String makeTextFrom(BioPoint content) {
        switch(content.getType()) {
            case BORN:
                return makeBornText(content);
            case DIED:
                return makeDiedText(content);
            case DEPORTATION:
                return makeDeportedText(content);
            case MARRIED:
                return makeMarriedText(content);
            case MOVED:
                return makeMovedText(content);
            case CUSTOM:
                return makeCustomText(content);
            default:
                return makeDefaultText();
        }
    }

    private static String makeDefaultText() {
        return "";
    }

    private static String makeCustomText(BioPoint content) {
        return content.getCustomDescription();
    }

    private static String makeMovedText(BioPoint content) {
        return content.getName() + " zog am " + content.getDate() + " nach " + content.getPlace() + " um.";
    }

    private static String makeMarriedText(BioPoint content) {
        return "Am " + content.getDate() + " heiratete " + content.getName() + " " + content.getSpouse() + " in " + content.getPlace() + ".";
    }

    private static String makeDeportedText(BioPoint content) {
        return content.getName() + " wurde am " + content.getDate() + " nach " + content.getPlace() + " deportiert.";
    }

    private static String makeDiedText(BioPoint content) {
        return "Am " + content.getDate() + " verstarb " + content.getName() + " in " + content.getPlace() + ".";
    }

    private static String makeBornText(BioPoint content) {
        return content.getName() + " wurde am " + content.getDate() + " in " + content.getPlace() + " geboren";
    }
}
