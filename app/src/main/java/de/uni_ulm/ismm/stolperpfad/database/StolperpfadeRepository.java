package de.uni_ulm.ismm.stolperpfad.database;

import android.app.Application;
import android.os.AsyncTask;


import java.util.List;

import de.uni_ulm.ismm.stolperpfad.database.data.HistoricalTerm;
import de.uni_ulm.ismm.stolperpfad.database.data.Person;
import de.uni_ulm.ismm.stolperpfad.database.data.Stolperstein;

public class StolperpfadeRepository {

    private StolperpfadeDao stolperpfade_dao;
    private List<Person> all_persons;
    private List<Stolperstein> all_stones;
    private List<HistoricalTerm> all_terms;

    public StolperpfadeRepository(Application application) {
        StolperpfadeRoomDatabase db = StolperpfadeRoomDatabase.getDatabase(application);
        stolperpfade_dao = db.mDao();
    }

    //PERSONS

    public void insertPerson(Person person) {
        new InsertPersonAsyncTask(stolperpfade_dao).execute(person);
    }

    private static class InsertPersonAsyncTask extends AsyncTask<Person, Void, Void> {

        private StolperpfadeDao task_dao;

        InsertPersonAsyncTask(StolperpfadeDao dao) {
            task_dao = dao;
        }

        @Override
        protected Void doInBackground(Person... persons) {
            task_dao.insert(persons[0]);
            return null;
        }
    }

    /**
     * Get a list of all persons
     */
    public List<Person> getAllPersons() {
        if (all_persons == null)
            return all_persons = stolperpfade_dao.getAllPersons();
        return all_persons;
    }

    /**
     * Get a list of all persons who are concerned by a given historical term
     *
     * @param historical_term the term to filter the persons by
     * @return all concerned persons
     */
    public List<Person> getAllConcernedPersons(String historical_term){
        return stolperpfade_dao.getAllConcernedPersons(historical_term);
    }

    /**
     * Get all persons with the same stone location
     *
     * @param stone_id the stone to look for
     * @return a list with all persons from that stone position
     */
    public List<Person> getPersonsOnStone(int stone_id) {
        return stolperpfade_dao.getPersonsFromStone(stone_id);
    }


    //VITAS
    // insert vita
    public void insertVita(Person.Vita vita) {
        new InsertVitaAsyncTask(stolperpfade_dao).execute(vita);
    }

    private static class InsertVitaAsyncTask extends AsyncTask<Person.Vita, Void, Void> {

        private StolperpfadeDao task_dao;

        InsertVitaAsyncTask(StolperpfadeDao dao) {
            task_dao = dao;
        }

        @Override
        protected Void doInBackground(Person.Vita... vitas) {
            task_dao.insert(vitas[0]);
            return null;
        }
    }

    /**
     * Get all vitas from an id
     *
     * @param person_id the person to get the vita of
     * @return the vita of that person
     */
    public Person.Vita getVita(int person_id) {
        return stolperpfade_dao.getVita(person_id);
    }

    //STOLPERSTEINE

    // insert stone
    public void insertStone(Stolperstein stone) {
        new InsertStoneAsyncTask(stolperpfade_dao).execute(stone);
    }

    private static class InsertStoneAsyncTask extends AsyncTask<Stolperstein, Void, Void> {

        private StolperpfadeDao task_dao;

        InsertStoneAsyncTask(StolperpfadeDao dao) {
            task_dao = dao;
        }

        @Override
        protected Void doInBackground(Stolperstein... vitas) {
            task_dao.insert(vitas[0]);
            return null;
        }
    }

    /**
     * Get all stones in the data base
     *
     * @return a list of all stones
     */
    public List<Stolperstein> getAllStones() {
        if (all_persons == null)
            return all_stones = stolperpfade_dao.getAllStones();
        return all_stones;
    }

    /**
     * Get the Stolperstein address
     *
     * @param stone_id the stone to look for
     * @return the address of that stone
     */
    public String getAddress(int stone_id) {
        return stolperpfade_dao.getAddress(stone_id);
    }

    //HISTORICAL TERMS

    // insert term
    public void insertTerm(HistoricalTerm term) {
        new InsertTermAsyncTask(stolperpfade_dao).execute(term);
    }

    private static class InsertTermAsyncTask extends AsyncTask<HistoricalTerm, Void, Void> {

        private StolperpfadeDao task_dao;

        InsertTermAsyncTask(StolperpfadeDao dao) {
            task_dao = dao;
        }

        @Override
        protected Void doInBackground(HistoricalTerm... terms) {
            task_dao.insert(terms[0]);
            return null;
        }
    }

    /**
     * Get a list of all historical terms
     *
     * @return a list of all historical terms
     */
    public List<HistoricalTerm> getAllTerms() {
        if (all_terms == null)
            return all_terms = stolperpfade_dao.getAllTerms();
        return all_terms;
    }

    /**
     * Get explanation for a term
     *
     * @param name the term name
     * @return the content of that term
     */
    public String getExplanation(String name){
        return stolperpfade_dao.getExplanation(name);
    }

}
