package de.uni_ulm.ismm.stolperpfad.info_display.history.model;

import android.annotation.SuppressLint;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import de.uni_ulm.ismm.stolperpfad.R;
import de.uni_ulm.ismm.stolperpfad.database.StolperpfadeRepository;
import de.uni_ulm.ismm.stolperpfad.database.data.HistoricalTerm;
import de.uni_ulm.ismm.stolperpfad.info_display.history.HistoricalListActivity;
import de.uni_ulm.ismm.stolperpfad.info_display.history.HistoInfoActivity;
import de.uni_ulm.ismm.stolperpfad.info_display.history.fragments.HistoricalListFragment;

/**
 * This class represents the ViewModel for the list of historical terms
 */
@SuppressLint("StaticFieldLeak")
public class HistoricalListViewModel extends AndroidViewModel {

    private static volatile HistoricalListViewModel INSTANCE;

    private static final float INDEX_MARGIN_SIZE = 16f;
    private static final float HALF_PIXEL = 0.5f;
    private static final float INDEX_BUTTON_SIZE = 50f;
    private static final int DEFAULT_ID_OFFSET = 1000;

    private List<HistoricalTerm> historical_terms;
    private ArrayList<Character> initials;
    private ArrayList<Button> index_buttons;
    private Button last_pressed;
    private HistoricalTermListPager list_pager;
    private StolperpfadeRepository repo;
    private HistoricalListActivity parent;

    private HistoricalListViewModel(@NonNull Application application, HistoricalListActivity activity){
        super(application);
        this.parent = activity;
        repo = new StolperpfadeRepository(application);
    }

    public static HistoricalListViewModel getInstance(HistoricalListActivity activity) {
        if(INSTANCE == null || INSTANCE.parent != activity) {
            INSTANCE = new HistoricalListViewModel(activity.getApplication(), activity);
        }
        return INSTANCE;
    }

    // ++++ Index Methods ++++

    /**
     * This methods prepares all steps needed to initialize and display the alphabetical index
     * and then the list of terms
     */
    @SuppressLint("StaticFieldLeak")
    public void setUpIndex() {
        list_pager = parent.findViewById(R.id.histo_list_pager);
        list_pager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                updateIndex(position);
            }
        });
        new LoadAllTermsTask() {
            @Override
            protected void onPostExecute(Void aVoid) {
                readIndex();
                buildIndex();
                list_pager.setAdapter(new ListPagerAdapter(parent.getSupportFragmentManager()));
                updateIndex(0);
            }
        }.execute();
    }

    /**
     * Actually builds the elements that will be displayed in the alphabetical index
     */
    private void buildIndex() {
        ConstraintLayout index = parent.findViewById(R.id.index_layout);
        index_buttons = new ArrayList<>();
        int count = initials.size();
        Button buff;
        // create the buttons for the index
        DisplayMetrics dm = parent.getResources().getDisplayMetrics();
        int button_size = (int) (dm.density * INDEX_BUTTON_SIZE + HALF_PIXEL);
        int margin = (int) (dm.density * INDEX_MARGIN_SIZE + HALF_PIXEL);
        for(int i = 0; i < count; i++) {
            index_buttons.add(buff = makeIndexButton(i, button_size));
            index.addView(buff);
        }
        Button first = index_buttons.get(0);
        first.setBackgroundResource(R.drawable.ic_index_highlight);
        // define the constraints for all buttons to one another
        ConstraintSet cs = new ConstraintSet();
        cs.clone(index);
        cs.connect(first.getId(),ConstraintSet.TOP, index.getId(),ConstraintSet.TOP,margin );
        cs.connect(first.getId(),ConstraintSet.START,index.getId(),ConstraintSet.START );
        cs.connect(first.getId(),ConstraintSet.END, index.getId(),ConstraintSet.END);
        for(int i = 1; i < count; i++) {
            cs.connect(index_buttons.get(i).getId(),ConstraintSet.TOP,index_buttons.get(i-1).getId(),ConstraintSet.BOTTOM,margin);
            cs.connect(index_buttons.get(i).getId(),ConstraintSet.START,index.getId(),ConstraintSet.START );
            cs.connect(index_buttons.get(i).getId(),ConstraintSet.END, index.getId(),ConstraintSet.END );
        }
        cs.applyTo(index);
    }

    /**
     * Create a single button for the index at the specified position
     *
     * @param position the position of the button in the list of all buttons, corresponds
     *                 to position of its initial
     * @return the created button
     */
    @SuppressLint("InflateParams")
    private Button makeIndexButton(int position, int button_size) {
        Button new_button = (Button) LayoutInflater.from(parent).inflate(R.layout.button_index, null);
        new_button.setOnClickListener(view -> updateIndex(position));
        new_button.setText(initials.get(position).toString());
        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(button_size,button_size);
        new_button.setLayoutParams(params);
        new_button.setId(position + DEFAULT_ID_OFFSET);
        return new_button;
    }

    /**
     * Update the alphabetical index display, meaning to highlight the current displayed initial
     * and unhighlight the previous initial
     *
     * @param new_position the current page that is displayed
     */
    private void updateIndex(int new_position) {
        if(index_buttons == null) {
            buildIndex();
        }
        list_pager.setCurrentItem(new_position);
        if(last_pressed != null) {
            last_pressed.setBackground(null);
        } else {
            for(Button b : index_buttons) {
                b.setBackground(null);
            }
        }
        index_buttons.get(new_position).setBackgroundResource(R.drawable.ic_index_highlight);
        last_pressed = index_buttons.get(new_position);
    }

    /**
     * Determines the occurring first letters of all term names to calculate the index size
     */
    private void readIndex() {
        if(initials == null || initials.size() == 0) {
            char buff;
            initials = new ArrayList<>();
            for (HistoricalTerm term : historical_terms) {
                if (term.getName() == null || term.getName().length() == 0) {
                    continue;
                }
                buff = term.getName().charAt(0);
                if (!initials.contains(buff)) {
                    initials.add(buff);
                }
            }
        }
    }

    // ++++ List methods ++++

    /**
     * Prepare and build the person list for one specific initial
     *
     * @param root the container view for the person list
     */
    @SuppressLint("StaticFieldLeak")
    public void setUpList(ViewGroup root, char initial) {
        readIndex();
        LinearLayout list_layout = root.findViewById(R.id.list_layout);
        for(HistoricalTerm term : historical_terms) {
            if(term.getName().startsWith(initial + "")) {
                list_layout.addView(makeListButton(term));
            }
        }

    }

    /**
     * Create a single button for the term list with the specified term
     *
     * @param historical_term the term to display
     * @return a new button
     */
    @SuppressLint("InflateParams")
    private Button makeListButton(HistoricalTerm historical_term) {
        Button new_button = (Button) LayoutInflater.from(parent).inflate(R.layout.button_person_list, null);
        new_button.setOnClickListener(view -> {
            Intent intent = new Intent(parent, HistoInfoActivity.class);
            intent.putExtra("term_name", historical_term.getName());
            intent.setAction("" + historical_term.getName());
            parent.startActivity(intent);
        });
        String display_name = historical_term.getName();
        new_button.setText(Html.fromHtml(display_name));
        return new_button;
    }

    /**
     * A simple helper task that retrieves all necessary data from the data base
     */
    private class LoadAllTermsTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            historical_terms = repo.getAllTerms();
            return null;
        }
    }

    /**
     * A simple pager adapter that represents the list pages of the terms
     */
    private class ListPagerAdapter extends FragmentStatePagerAdapter {
        ListPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int current_list_page) {
            return HistoricalListFragment.newInstance(initials.get(current_list_page));
        }

        @Override
        public int getCount() {
            return initials.size();
        }
    }

}
