package de.uni_ulm.ismm.stolperpfad.database.data_util;

import android.support.annotation.NonNull;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

import java.util.ArrayList;

import de.uni_ulm.ismm.stolperpfad.general.StolperpfadeAppActivity;

/**
 * A helper utility class for creating Strings with highlighted links
 */
public class StringCreator {

    /**
     * in this method a given text (bio content) is searched for links to histoTerms / other persons
     * which are highlighted in the text
     *
     * @param text the text to highlight
     * @param curr_activity the parent activity
     * @param links the tags to highlight
     * @return a spannable string with the specified highlights
     */
    public static SpannableString makeSpanWith(String text, StolperpfadeAppActivity curr_activity, ArrayList<String> links) {
        SpannableString ret = new SpannableString(text);
        for(String single_tag : links) {
            int start = text.indexOf(single_tag);
            if(start >= 0 && start < text.length()) {
                ClickableSpan clickableSpan = new ClickableSpan() {
                    @Override
                    public void onClick(@NonNull View view) {
                        curr_activity.reactToLink(single_tag);
                    }
                    @Override
                    public void updateDrawState(@NonNull TextPaint ds) {
                        super.updateDrawState(ds);
                        ds.setUnderlineText(false);
                    }
                };
                ret.setSpan(clickableSpan, start, start + single_tag.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        return ret;
    }
}
