package de.uni_ulm.ismm.stolperpfad.database;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;

import java.util.List;

public class PersViewModel extends AndroidViewModel {

    private PersRepository mRepo;
    private List<Person> mAllPersons;

    public PersViewModel (Application application){
        super(application);
        mRepo = new PersRepository(application);
        mAllPersons = mRepo.getAllPersons();
    }

    List<Person> getAllPersons(){
        return mAllPersons;
    }

    public void insert(Person person){
        mRepo.insert(person);
    }

}
