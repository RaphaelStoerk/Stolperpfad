package de.uni_ulm.ismm.stolperpfad.info_display.history.model;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

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

    // method to get explanation for histo_info_view
    public void requestExplanation(String current, HistoInfoActivity activity) {
        repo.getExplanation(current, activity);
    }
}
