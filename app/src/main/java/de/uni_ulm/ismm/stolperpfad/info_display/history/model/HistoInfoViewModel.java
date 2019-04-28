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

public class HistoInfoViewModel extends AndroidViewModel {

    private StolperpfadeRepository repo;
    private HistoInfoActivity parent;
    private static volatile HistoInfoViewModel INSTANCE;
    private String termName;
    private String termExplanation;
    private List<Person> allPersons;
    private List<HistoricalTerm> allHistoTerms;
    private List<String> personNames;
    private List<String> histoTermNames;
    private List<Person> concPersList;
    private String concernedPersonsString;

    public HistoInfoViewModel(@NonNull Application application, HistoInfoActivity activity) {
        super(application);
        this.parent = activity;
        termName = activity.getIntent().getStringExtra("termName");
        allPersons = new ArrayList<>();
        allHistoTerms = new ArrayList<>();
        personNames = new ArrayList<>();
        histoTermNames = new ArrayList<>();
        concPersList = new ArrayList<>();
        repo = new StolperpfadeRepository(application);
    }

    public static HistoInfoViewModel getInstance(HistoInfoActivity activity) {
        if (INSTANCE == null || INSTANCE.parent != activity) {
            INSTANCE = new HistoInfoViewModel(activity.getApplication(), activity);
        }
        return INSTANCE;
    }

    //get histoInfoPage content
    @SuppressLint("StaticFieldLeak")
    public void requestContent(HistoInfoActivity activity) {
        new LoadContentTask(this) {
            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                setContent(activity);
            }
        }.execute();

    }


    //set histoInfoPage content
    public void setContent(HistoInfoActivity activity) {
        //highlight terms in text
        //list with all terms to highlight
        ArrayList<String> allHighlightTerms = new ArrayList<>();
        for (String person : personNames) {
            allHighlightTerms.add(person);
        }
        for (String term : histoTermNames) {
            allHighlightTerms.add(term);
        }

        //set explanation
        TextView textView = activity.findViewById(R.id.title_histo_info);
        textView.setText(termName);
        if (termExplanation == null || termExplanation.equals("")) {

        } else {
            SpannableString newContent = StringCreator.makeSpanWith(termExplanation, activity, allHighlightTerms);
            textView = activity.findViewById(R.id.histo_info_explanation);
            textView.setText(newContent);
            textView.setMovementMethod(LinkMovementMethod.getInstance());
            textView.setHighlightColor(Color.TRANSPARENT);
        }

        //set concerned persons
        if (concernedPersonsString == null || concernedPersonsString.equals("")) {

        } else {
            SpannableString newContent = StringCreator.makeSpanWith(concernedPersonsString, activity, allHighlightTerms);
            textView = activity.findViewById(R.id.histo_info_concerned_persons);
            textView.setText(newContent);
            textView.setMovementMethod(LinkMovementMethod.getInstance());
            textView.setHighlightColor(Color.TRANSPARENT);
        }

    }


    private static class LoadContentTask extends AsyncTask<Void, Void, Void> {

        private HistoInfoViewModel model;

        LoadContentTask(HistoInfoViewModel model) {
            this.model = model;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            //get explanation
            model.termExplanation = model.repo.getExplanation(model.termName);

            //load data for highlighting
            model.allPersons = model.repo.getAllPersons();
            model.allHistoTerms = model.repo.getAllTerms();
            for (HistoricalTerm term : model.allHistoTerms) {
                String current = term.getName();
                model.histoTermNames.add(current);
            }
            for (Person pers : model.allPersons) {
                String current = pers.getEntireName();
                model.personNames.add(current);
            }

            //get concerned persons
            model.concPersList = model.repo.getAllConcernedPersons(model.termName);
            model.concernedPersonsString = "";
            for (Person pers : model.concPersList) {
                String entName = pers.getEntireName();
                if (model.concernedPersonsString == null || model.concernedPersonsString.equals("")) {
                    model.concernedPersonsString = entName;
                } else {
                    model.concernedPersonsString = model.concernedPersonsString + ", " + entName;
                }
            }
            return null;
        }
    }

}