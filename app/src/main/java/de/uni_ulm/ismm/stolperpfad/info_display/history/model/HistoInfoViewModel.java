package de.uni_ulm.ismm.stolperpfad.info_display.history.model;

import android.annotation.SuppressLint;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.uni_ulm.ismm.stolperpfad.R;
import de.uni_ulm.ismm.stolperpfad.database.StolperpfadeRepository;
import de.uni_ulm.ismm.stolperpfad.database.data.HistoricalTerm;
import de.uni_ulm.ismm.stolperpfad.database.data.Person;
import de.uni_ulm.ismm.stolperpfad.database.data_util.StringCreator;
import de.uni_ulm.ismm.stolperpfad.info_display.history.HistoInfoActivity;

/**
 * This class represents the ViewModel for the Historical term info page
 */
@SuppressLint("StaticFieldLeak")
public class HistoInfoViewModel extends AndroidViewModel {

    private static volatile HistoInfoViewModel INSTANCE;

    private String term_name;
    private String term_content;
    private List<String> historical_term_names;
    private List<String> concerned_person_names;
    private String concerned_person_string;
    private StolperpfadeRepository repo;
    private HistoInfoActivity parent;

    private HistoInfoViewModel(@NonNull Application application, HistoInfoActivity activity) {
        super(application);
        this.parent = activity;
        term_name = activity.getIntent().getStringExtra("term_name");
        concerned_person_names = new ArrayList<>();
        historical_term_names = new ArrayList<>();
        repo = new StolperpfadeRepository(application);
    }

    public static HistoInfoViewModel getInstance(HistoInfoActivity activity) {
        if (INSTANCE == null || INSTANCE.parent != activity) {
            INSTANCE = new HistoInfoViewModel(activity.getApplication(), activity);
        }
        return INSTANCE;
    }

    /**
     * Sets up the background data collection and creates the info page
     */
    @SuppressLint("StaticFieldLeak")
    public void setUpInfoPage() {
        new LoadContentTask(this) {
            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                buildInfoContent();
            }
        }.execute();

    }

    /**
     * Creates the content for the info page and sets up the highlighting of clickable tags
     */
    private void buildInfoContent() {
        //highlight terms in text
        //list with all terms to highlight
        ArrayList<String> all_highlighted_terms = new ArrayList<>();
        all_highlighted_terms.addAll(concerned_person_names);
        all_highlighted_terms.addAll(historical_term_names);

        //set explanation
        TextView title_info_textfield = parent.findViewById(R.id.title_histo_info);
        title_info_textfield.setText(term_name);
        if (term_content != null && !term_content.equals("")) {
            SpannableString newContent = StringCreator.makeSpanWith(term_content, parent, all_highlighted_terms);
            title_info_textfield = parent.findViewById(R.id.histo_info_explanation);
            title_info_textfield.setText(newContent);
            title_info_textfield.setMovementMethod(LinkMovementMethod.getInstance());
            title_info_textfield.setHighlightColor(Color.TRANSPARENT);
        }
        //set concerned persons
        TextView content_info_textfield = parent.findViewById(R.id.histo_info_concerned_persons);
        if (concerned_person_string != null && !concerned_person_string.equals("")) {
            SpannableString newContent = StringCreator.makeSpanWith(concerned_person_string, parent, all_highlighted_terms);
            content_info_textfield.setText(newContent);
            content_info_textfield.setMovementMethod(LinkMovementMethod.getInstance());
            content_info_textfield.setHighlightColor(Color.TRANSPARENT);
        }
    }

    /**
     * A helper background task that collects data from the data base and perpares it for later usage
     */
    private static class LoadContentTask extends AsyncTask<Void, Void, Void> {

        private HistoInfoViewModel model;

        LoadContentTask(HistoInfoViewModel model) {
            this.model = model;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            //get explanation
            model.term_content = model.repo.getExplanation(model.term_name);

            //load data for highlighting
            List<HistoricalTerm> all_historical_terms = model.repo.getAllTerms();
            for (HistoricalTerm term : all_historical_terms) {
                String current = term.getName();
                if (!model.term_name.equals(current)) {
                    model.historical_term_names.add(current);
                }
            }

            //get concerned persons
            List<Person> concerned_person_list = model.repo.getAllConcernedPersons(model.term_name);
            for (Person concerned_person : concerned_person_list) {
                String current = concerned_person.getEntireName();
                model.concerned_person_names.add(current);
            }
            model.concerned_person_string = "";
            for (Person concerned_person : concerned_person_list) {
                String entName = concerned_person.getEntireName();
                if (model.concerned_person_string == null || model.concerned_person_string.equals("")) {
                    model.concerned_person_string = entName;
                } else {
                    model.concerned_person_string += ", " + entName;
                }
            }
            return null;
        }
    }

}