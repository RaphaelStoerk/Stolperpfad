package de.uni_ulm.ismm.stolperpfad.database;

import android.annotation.SuppressLint;
import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;


import java.util.ArrayList;
import java.util.List;

import de.uni_ulm.ismm.stolperpfad.database.data.HistoricalTerm;
import de.uni_ulm.ismm.stolperpfad.database.data.Person;
import de.uni_ulm.ismm.stolperpfad.database.data.Stolperstein;

public class StolperpfadeRepository {

    private StolperpfadeDao mDao;
    private List<Person> mAllPersons;
    private List<HistoricalTerm> mAllTerms;

    public StolperpfadeRepository(Application application) {
        StolperpfadeRoomDatabase db = StolperpfadeRoomDatabase.getDatabase(application);
        mDao = db.mDao();
//        mAllPersons = mDao.getAllPersons();
        //       mAllTerms = mDao.getAllTerms();
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

    //get a list of all persons
    public List<Person> getAllPersons() {
        if(mAllPersons == null)
            return mAllPersons = mDao.getAllPersons();
        return mAllPersons;
    }

    //get a person's first name
    /*public String getFstName(int persId, PersInfoPage parent){
        new StolperpfadeRepository.getFstNameAsyncTask(mDao){
            @Override
            protected void onPostExecute(String fstName){
                parent.set
            }
        }
    }*/

    //get a person's family name

    //get a person's birth name (if existent)

    //get all persons who are concerned by the given historical term
    /*public ArrayList<Person> getConcernedPersons(String histoTerm, HistoListActivity parent){
        new StolperpfadeRepository.getConcernedPersonsAsyncTask(mDao){
            @Override
            protected void onPostExecute(ArrayList<Person> concernedPersons){
                parent.setPersList(concernedPersons);
            }
        }.execute(new ArrayList<Person>);
    }

    private static class getConcernedPersonsAsyncTask extends AsyncTask<ArrayList<Person>, Void, Person>{
        private StolperpfadeDao mAsyncTaskDao;
        getConcernedPersonsAsyncTask(StolperpfadeDao dao){
            mAsyncTaskDao = dao;
        }
        @Override
        protected ArrayList<Person> doInBackground(ArrayList<Person>... histoTerm){

        }

    }*/

    //get a person's Stolperstein id




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

    // get all Vitas from an id
    public List<Person.Vita> getVita(int persId) {
        return mDao.getVita(persId);
    }

    //get a particular section of a person's vita



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

    public List<Stolperstein> getStone(int stoneId) {return mDao.getStone(stoneId);}

    //get the Stolperstein's address
    public String getAddress(int stoneId) {
        return mDao.getAddress(stoneId);
    }

    //get get the Stolperstein's latitude

    //get the Stolperstein's longitude



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

    //get a list of all historical terms
    public List<HistoricalTerm> getAllTerms() {
        if(mAllTerms == null)
            return mAllTerms = mDao.getAllTerms();
        return mAllTerms;
    }

    //get historical term explanation
    //THIS CODE WORKED
    /*@SuppressLint("StaticFieldLeak")
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
    }*/
}
