package de.uni_ulm.ismm.stolperpfad;

import android.app.Application;
import android.os.AsyncTask;

import java.util.List;

import androidx.lifecycle.LiveData;

public class PersRepository {
    private PersDao persDao;
    private LiveData<List<Person>> allPers;

    PersRepository(Application application) {
        PersonsRoomDatabase db = PersonsRoomDatabase.getDatabase(application);
        persDao = db.persDao();
        allPers = persDao.getAllPersons();
    }

    LiveData<List<Person>> getAllPersons() {
        return allPers;
    }

    public void insert(Person pers) {
        new insertAsyncTask(persDao).execute(pers);
    }

    /**
     * AsyncTask should avoid an app crash
     * because it blocks the main thread for long-running operations
     */
    private static class insertAsyncTask extends AsyncTask<Person, Void, Void> {

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
