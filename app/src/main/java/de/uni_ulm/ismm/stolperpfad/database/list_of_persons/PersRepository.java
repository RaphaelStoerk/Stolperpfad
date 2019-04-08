package de.uni_ulm.ismm.stolperpfad.database.list_of_persons;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;

import de.uni_ulm.ismm.stolperpfad.database.data.Person;

public class PersRepository {

    private PersDao mPersDao;
    private LiveData<List<Person>> mAllPersons;

    PersRepository(Application application){
        PersRoomDatabase db = PersRoomDatabase.getDatabase(application);
        mPersDao = db.persDao();
        mAllPersons = mPersDao.getAllPersons();
    }

    LiveData<List<Person>> getAllPersons() {
        return mAllPersons;
    }

    /**
     * To get the street and number of the Stolperstein of a given person
     * @param stoneId
     * @return street and number
     */
    public String getAddress(int stoneId) {
        return mPersDao.getAddress(stoneId);
    }


    // maybe we won't need these methods later
    public void insert(Person person){
        new insertAsyncTask(mPersDao).execute(person);
    }

    private static class insertAsyncTask extends AsyncTask<Person, Void, Void>{
        private PersDao mAsyncTaskDao;

        insertAsyncTask(PersDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Person... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }
}
