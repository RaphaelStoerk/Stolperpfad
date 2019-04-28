package de.uni_ulm.ismm.stolperpfad.info_display.history.model;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;
import android.util.Log;

import de.uni_ulm.ismm.stolperpfad.database.StolperpfadeRepository;
import de.uni_ulm.ismm.stolperpfad.info_display.history.HistoInfoActivity;

public class HistoInfoViewModel extends AndroidViewModel {

    private StolperpfadeRepository repo;
    private HistoInfoActivity parent;
    private static volatile HistoInfoViewModel INSTANCE;

    public HistoInfoViewModel(@NonNull Application application, HistoInfoActivity activity){
        super(application);
        this.parent = activity;
        repo = new StolperpfadeRepository(application);
    }

    public static HistoInfoViewModel getInstance(HistoInfoActivity activity){
        if(INSTANCE == null || INSTANCE.parent != activity) {
            INSTANCE = new HistoInfoViewModel(activity.getApplication(), activity);
        }
        return INSTANCE;
    }

    // methods to get contents for histo_info_view
    //explanation
    public void requestExplanation(String current, HistoInfoActivity activity) {
        repo.getExplanation(current, activity);
    }

    //list of concerned persons
    public void requestConcPers(String curent, HistoInfoActivity activity){
        Log.i("LOG_REQUEST_CONC_PERS", "started in ViewModel");
        repo.getConcernedPersons(curent, activity);
    }
}
