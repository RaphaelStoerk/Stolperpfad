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

    int getHistoId(String name) {
        return mHistoDao.getHistoId(name);
    }


    //TODO fix this
    public void getExplanation(int termId, HistoInfoPage parent) {
        new getExplantionAsyncTask(mHistoDao) {
            @Override
            protected void onPostExecute(String explanation) {
                parent.setExplanationText(explanation);
            }
        }.execute(new int[]{termId});
    }

    private static class getExplantionAsyncTask extends AsyncTask<int[], Void, String> {

        private HistoDao mAsyncTaskDao;

        getExplantionAsyncTask(HistoDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected String doInBackground(int[]... termId) {
            String explanation = mAsyncTaskDao.getExplanation(termId[0][0]);
            return explanation;
        }
    }


    public void insert(HistoricalTerm histoTerm) {
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
