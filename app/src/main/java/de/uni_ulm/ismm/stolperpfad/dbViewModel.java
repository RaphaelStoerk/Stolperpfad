package de.uni_ulm.ismm.stolperpfad;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

import androidx.lifecycle.LiveData;

import java.util.List;

public class dbViewModel extends AndroidViewModel {

    private PersRepository persRepo;
    private LiveData<List<Person>> allPers;

    public dbViewModel(@NonNull Application application) {
        super(application);
        persRepo = new PersRepository(application);
        allPers = persRepo.getAllPersons();
    }

    /**
     * "getter"-method
     *
     * @return List
     */
    LiveData<List<Person>> getAllPers() {
        return allPers;
    }

    /**
     * sense: hide the implementation of insert() from the UI
     * @param pers
     */
    public void insert(Person pers) {
        persRepo.insert(pers);
    }
}
