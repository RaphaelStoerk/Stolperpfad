package de.uni_ulm.ismm.stolperpfad.database.list_of_historical_terms;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import java.util.List;

import de.uni_ulm.ismm.stolperpfad.database.data.HistoricalTerm;

public class HistoViewModel extends AndroidViewModel {

    private HistoRepository mRepository;

    private LiveData<List<HistoricalTerm>> mAllTerms;

    public HistoViewModel (Application application) {
        super(application);
        mRepository = new HistoRepository(application);
        mAllTerms = mRepository.getAllTerms();
    }

    LiveData<List<HistoricalTerm>> getAllTerms() { return mAllTerms; }

    public void insert(HistoricalTerm histoTerm) { mRepository.insert(histoTerm); }

    public int getHistoId(String name){return mRepository.getHistoId(name);}
    public String getExplanation(int termId){return mRepository.getExplanation(termId);}
}
