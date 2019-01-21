package de.uni_ulm.ismm.stolperpfad;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import androidx.lifecycle.LiveData;

@Dao
public interface PersDao {

    @Insert
    void insert(Person person);

    @Query("DELETE FROM persons")
    void deleteAll();

    @Query("SELECT * FROM persons ORDER BY pFamilyName ASC")
    LiveData<List<Person>> getAllPersons();
}
