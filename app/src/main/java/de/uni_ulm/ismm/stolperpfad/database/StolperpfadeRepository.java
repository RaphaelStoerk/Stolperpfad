package de.uni_ulm.ismm.stolperpfad.database;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;

import de.uni_ulm.ismm.stolperpfad.database.data.HistoricalTerm;
import de.uni_ulm.ismm.stolperpfad.database.data.Person;
import de.uni_ulm.ismm.stolperpfad.database.data.Stolperstein;
import de.uni_ulm.ismm.stolperpfad.database.list_of_historical_terms.HistoInfoPage;


public class StolperpfadeRepository {


    private StolperpfadeDao mDao;
    private LiveData<List<Person>> mAllPersons;
    private LiveData<List<HistoricalTerm>> mAllTerms;

    public StolperpfadeRepository(Application application) {
        StolperpfadeRoomDatabase db = StolperpfadeRoomDatabase.getDatabase(application);
        mDao = db.mDao();
        mAllPersons = mDao.getAllPersons();
        mAllTerms = mDao.getAllTerms();
    }

    LiveData<List<Person>> getAllPersons() {
        return mAllPersons;
    }

    LiveData<List<HistoricalTerm>> getAllTerms() {
        return mAllTerms;
    }


    //GET DATA FROM THE DATABASE

    //PERSONS
    //insert person
    public void insertPerson(Person person) {
        new de.uni_ulm.ismm.stolperpfad.database.StolperpfadeRepository.insertPersonAsyncTask(mDao).execute(person);
    }

    private static class insertPersonAsyncTask extends AsyncTask<Person, Void, Void> {
        private StolperpfadeDao mAsyncTaskDao;
        insertPersonAsyncTask(StolperpfadeDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Person... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }

    //VITAS
    //insert vita
    public void insertVita(Person.Vita vita) {
        new de.uni_ulm.ismm.stolperpfad.database.StolperpfadeRepository.insertVitaAsyncTask(mDao).execute(vita);
    }

    private static class insertVitaAsyncTask extends AsyncTask<Person.Vita, Void, Void> {
        private StolperpfadeDao mAsyncTaskDao;
        insertVitaAsyncTask(StolperpfadeDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(Person.Vita... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }

    //STOLPERSTEINE
    //insert Stolperstein
    public void insertStone(Stolperstein stostei) {
        new de.uni_ulm.ismm.stolperpfad.database.StolperpfadeRepository.insertStoneAsyncTask(mDao).execute(stostei);
    }

    private static class insertStoneAsyncTask extends AsyncTask<Stolperstein, Void, Void> {
        private StolperpfadeDao mAsyncTaskDao;
        insertStoneAsyncTask(StolperpfadeDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Stolperstein... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }

    //get Stolpersteine address
    public String getAddress(int stoneId) {
        return mDao.getAddress(stoneId);
    }

    //HISTORICAL TERMS
    //insert historical term
    public void insertHisto(HistoricalTerm histoTerm) {
        new StolperpfadeRepository.insertHistoAsyncTask(mDao).execute(histoTerm);
    }

    private static class insertHistoAsyncTask extends AsyncTask<HistoricalTerm, Void, Void> {

        private StolperpfadeDao mAsyncTaskDao;
        insertHistoAsyncTask(StolperpfadeDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final HistoricalTerm... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }

    //get historical term explanation
    public void getExplanation(String termName, HistoInfoPage parent) {
        new StolperpfadeRepository.getExplantionAsyncTask(mDao) {
            @Override
            protected void onPostExecute(String explanation) {
                parent.setExplanationText(explanation);
            }
        }.execute(new String[]{termName});
    }

    private static class getExplantionAsyncTask extends AsyncTask<String[], Void, String> {

        private StolperpfadeDao mAsyncTaskDao;
        getExplantionAsyncTask(StolperpfadeDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected String doInBackground(String[]... termId) {
            String explanation = mAsyncTaskDao.getExplanation(termId[0][0]);
            return explanation;
        }
    }
}
