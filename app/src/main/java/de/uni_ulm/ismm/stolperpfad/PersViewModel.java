package de.uni_ulm.ismm.stolperpfad;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import java.util.List;

public class PersViewModel extends AndroidViewModel {

    private PersRepository mRepo;
    private LiveData<List<Person>> mAllPersons;

    public PersViewModel (Application application){
        super(application);
        mRepo = new PersRepository(application);
        mAllPersons = mRepo.getAllPersons();
    }

    LiveData<List<Person>> getAllPersons(){
        return mAllPersons;
    }

    public void insert(Person person){
        mRepo.insert(person);
    }

}
