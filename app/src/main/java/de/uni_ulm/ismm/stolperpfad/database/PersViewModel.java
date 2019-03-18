package de.uni_ulm.ismm.stolperpfad.database;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import java.util.ArrayList;
import java.util.List;

public class PersViewModel extends AndroidViewModel {

    private PersRepository mRepo;
    private ArrayList<Person> mAllPersons;

    public PersViewModel (Application application){
        super(application);
        mRepo = new PersRepository(application);
        mAllPersons = mRepo.getAllPersons();
    }

    ArrayList<Person> getAllPersons(){
        return mAllPersons;
    }

}
