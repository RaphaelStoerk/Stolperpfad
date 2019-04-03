package de.uni_ulm.ismm.stolperpfad.database.list_of_historical_terms;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;

import de.uni_ulm.ismm.stolperpfad.database.data.HistoricalTerm;

public class HistoRepository {

    private HistoDao mHistoDao;
    private LiveData<List<HistoricalTerm>> mAllTerms;

    HistoRepository(Application application) {
        HistoRoomDatabase db = HistoRoomDatabase.getDatabase(application);
        mHistoDao = db.histoDao();
        mAllTerms = mHistoDao.getAllTerms();
    }

    LiveData<List<HistoricalTerm>> getAllTerms() {
        return mAllTerms;
    }

    public void insert (HistoricalTerm histoTerm) {
        new insertAsyncTask(mHistoDao).execute(histoTerm);
    }

    private static class insertAsyncTask extends AsyncTask<HistoricalTerm, Void, Void> {

        private HistoDao mAsyncTaskDao;

        insertAsyncTask(HistoDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final HistoricalTerm... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }
}
