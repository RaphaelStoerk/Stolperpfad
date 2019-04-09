package de.uni_ulm.ismm.stolperpfad.database.list_of_persons;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import java.util.List;

import de.uni_ulm.ismm.stolperpfad.database.data.Person;

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

    /**
     * To get the street and number of the Stolperstein of a given person
     * @param stoneId
     * @return street and number
     */
    public String getAddress(int stoneId) {
        return mRepo.getAddress(stoneId);
    }

}
