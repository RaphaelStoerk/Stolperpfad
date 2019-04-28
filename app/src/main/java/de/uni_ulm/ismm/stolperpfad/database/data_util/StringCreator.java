package de.uni_ulm.ismm.stolperpfad.database.data_util;

import android.support.annotation.NonNull;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

import de.uni_ulm.ismm.stolperpfad.general.StolperpfadeAppActivity;

public class StringCreator {

    /**
     * An example method for creating the Strings for the bio
     * @return
     */
    public static String makeIntroText(String name, String date, String place) {
        return name + " wurde am " + date + " in " + place + " geboren.";
    }
/*
    public static SpannableString makeTextFrom(BioPoint content, StolperpfadeAppActivity curr_activity) {
        switch(content.getType()) {
            case BORN:
                return makeBornText(content,curr_activity);
            case DIED:
                return makeDiedText(content,curr_activity);
            case DEPORTATION:
                return makeDeportedText(content,curr_activity);
            case MARRIED:
                return makeMarriedText(content,curr_activity);
            case MOVED:
                return makeMovedText(content,curr_activity);
            case CUSTOM:
                return makeCustomText(content);
            default:
                return makeDefaultText();
        }
    }

    private static SpannableString makeDefaultText() {
        SpannableString ret = new SpannableString("");
        return ret;
    }

    private static SpannableString makeCustomText(BioPoint content) {
        return new SpannableString(content.getCustomDescription());
    }

    private static SpannableString makeMovedText(BioPoint content, StolperpfadeAppActivity curr_activity) {
        String text = content.getName() + " zog am " + content.getDate() + " nach " + content.getPlace() + " um.";
        SpannableString ret = makeSpanWith(text, curr_activity,content.getYear() + "", content.getPlace());
        return ret;
    }

    private static SpannableString makeMarriedText(BioPoint content, StolperpfadeAppActivity curr_activity) {
        String text = "Am " + content.getDate() + " heiratete " + content.getName() + " " + content.getSpouse() + " in " + content.getPlace() + ".";
        SpannableString ret = makeSpanWith(text, curr_activity,content.getYear() + "", content.getPlace(), content.getSpouse());
        return ret;
    }

    private static SpannableString makeDeportedText(BioPoint content, StolperpfadeAppActivity curr_activity) {
        String text = content.getName() + " wurde am " + content.getDate() + " nach " + content.getPlace() + " deportiert.";
        SpannableString ret = makeSpanWith(text, curr_activity,content.getYear() + "", content.getPlace());
        return ret;
    }

    private static SpannableString makeDiedText(BioPoint content, StolperpfadeAppActivity curr_activity) {
        String text = "Am " + content.getDate() + " verstarb " + content.getName() + " in " + content.getPlace() + ".";
        SpannableString ret = makeSpanWith(text, curr_activity,content.getYear() + "", content.getPlace());
        return ret;
    }

    private static SpannableString makeBornText(BioPoint content, StolperpfadeAppActivity curr_activity) {
        String text = content.getName() + " wurde am " + content.getDate() + " in " + content.getPlace() + " geboren";
        SpannableString ret = makeSpanWith(text, curr_activity,content.getYear() + "", content.getPlace());
        return ret;
    }


    // TODO: @Ulrike

    private static SpannableString makeSpanWith(String text, StolperpfadeAppActivity curr_activity, String... links) {
        SpannableString ret = new SpannableString(text);
        for(String s : links) {
            ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(@NonNull View view) {
                    curr_activity.reactToLink(s);
                }

                @Override
                public void updateDrawState(@NonNull TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setUnderlineText(false);
                }
            };
            int start = text.indexOf(s);
            if(start >= 0 && start < text.length()) {
                ret.setSpan(clickableSpan, start, start + s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        return ret;
    }

    public static SpannableString makeCustomTextWithLinks() {
        return null; // TODO: allow custom texts to use highlighted links
    }

*/
}
