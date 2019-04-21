package de.uni_ulm.ismm.stolperpfad.info_display.stone_info;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androidquery.AQuery;

import de.uni_ulm.ismm.stolperpfad.R;
import de.uni_ulm.ismm.stolperpfad.database.data_util.StringCreator;
import de.uni_ulm.ismm.stolperpfad.info_display.stone_info.model.BioPoint;

/**
 * The lowest Fragment of its group, placeholder fragment for one specific
 * timestep of the biography of one specific person
 */
public class StoneInfoBioContentFragment extends Fragment {

    private BioPoint content;

    public StoneInfoBioContentFragment() {

    }

    public static StoneInfoBioContentFragment newInstance(BioPoint content) {
        StoneInfoBioContentFragment frag  = new StoneInfoBioContentFragment();
        frag.content = content;
        Log.i("BIO_TAG", content.getName() + ", " + content.getType().toString() + ", " + content.getYear());
        return frag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            if (savedInstanceState != null) {
                content = (BioPoint) savedInstanceState.getSerializable("content");
            }
        } catch(NullPointerException ignored) {

        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.content_bio_point, container, false);
        AQuery aq = new AQuery(root);
        aq.id(R.id.title_bio_point).text(content.getTitle());
        aq.id(R.id.text_bio_point).text(StringCreator.makeTextFrom(content));
        return root;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putSerializable("content", content);
        super.onSaveInstanceState(outState);
    }

}
