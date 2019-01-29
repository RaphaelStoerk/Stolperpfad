package de.uni_ulm.ismm.stolperpfad;

import android.app.Application;
import android.os.AsyncTask;

import java.util.List;

public class PersRepository {

    private PersDao mPersDao;
    private List<Person> mAllPersons;

    PersRepository(Application application){
        PersRoomDatabase db = PersRoomDatabase.getDatabase(application);
        mPersDao = db.persDao();
        mAllPersons = mPersDao.getAllPersons();
    }

    List<Person> getAllPersons() {
        return mAllPersons;
    }

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
