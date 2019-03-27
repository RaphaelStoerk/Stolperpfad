package de.uni_ulm.ismm.stolperpfad.database.list_of_historical_terms;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import de.uni_ulm.ismm.stolperpfad.database.data.HistoricalTerm;

@Dao
public interface HistoDao {
    @Insert
    void insert(HistoricalTerm histoTerm);

    @Query("DELETE FROM historical_terms")
    void deleteAll();

    @Query("SELECT * from historical_terms ORDER BY name ASC")
    LiveData<List<HistoricalTerm>> getAllWords();
}
