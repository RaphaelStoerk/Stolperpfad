package de.uni_ulm.ismm.stolperpfad.info_display.stone_info.model;

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
import de.uni_ulm.ismm.stolperpfad.database.data.Person;
import de.uni_ulm.ismm.stolperpfad.info_display.stone_info.StoneInfoMainActivity;
import de.uni_ulm.ismm.stolperpfad.info_display.stone_info.StoneListActivity;
import de.uni_ulm.ismm.stolperpfad.info_display.stone_info.fragments.StoneListFragment;

/**
 * This class represents the ViewModel for the list of persons
 */
@SuppressLint("StaticFieldLeak")
public class StoneListViewModel extends AndroidViewModel {

    private static volatile StoneListViewModel INSTANCE;

    private List<Person> persons;
    private ArrayList<Character> initials;
    private ArrayList<Button> index_buttons;
    private Button last_pressed;
    private VerticalViewPager list_pager;
    private StoneListActivity parent;

    private StoneListViewModel(@NonNull Application application, StoneListActivity activity) {
        super(application);
        this.parent = activity;
    }

    public static StoneListViewModel getInstance(StoneListActivity activity) {
        if(INSTANCE == null || INSTANCE.parent != activity) {
            INSTANCE = new StoneListViewModel(activity.getApplication(), activity);
        }
        return INSTANCE;
    }

    // ++++ Index Methods ++++

    /**
     * This methods prepares all steps needed to initialize and display the alphabetical index
     * and then the list of persons
     */
    @SuppressLint("StaticFieldLeak")
    public void setUpIndex() {
        list_pager = parent.findViewById(R.id.stone_list_pager);
        list_pager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                updateIndex(position);
            }
        });
        new LoadPersonsTask() {
            @Override
            protected void onPostExecute(Void aVoid) {
                readIndex();
                buildIndex();
                list_pager.setAdapter(new ListPagerAdapter(parent.getSupportFragmentManager()));
            }
        }.execute();
    }

    /**
     * Actually builds the elements that will be displayed in the alphabetical index
     */
    private void buildIndex() {
        ConstraintLayout index_container = parent.findViewById(R.id.index_layout);
        index_buttons = new ArrayList<>();
        int count = initials.size();
        Button buff;
        // create the buttons for the index
        for(int i = 0; i < count; i++) {
            index_buttons.add(buff = makeIndexButton(i));
            index_container.addView(buff);
        }
        // index_buttons.get(0).setBackgroundResource(R.drawable.ic_index_highlight);
        Button first = index_buttons.get(0);
        DisplayMetrics dm = parent.getResources().getDisplayMetrics();
        int margin = (int) (dm.density * 16f + 0.5f);

        // define the constraints for all buttons to one another
        ConstraintSet cs = new ConstraintSet();
        cs.clone(index_container);
        cs.connect(first.getId(),ConstraintSet.TOP, index_container.getId(),ConstraintSet.TOP, margin);
        cs.connect(first.getId(),ConstraintSet.START,index_container.getId(),ConstraintSet.START );
        cs.connect(first.getId(),ConstraintSet.END, index_container.getId(),ConstraintSet.END);
        for(int i = 1; i < count; i++) {
            cs.connect(index_buttons.get(i).getId(),ConstraintSet.TOP,index_buttons.get(i-1).getId(),ConstraintSet.BOTTOM,margin);
            cs.connect(index_buttons.get(i).getId(),ConstraintSet.START,index_container.getId(),ConstraintSet.START );
            cs.connect(index_buttons.get(i).getId(),ConstraintSet.END, index_container.getId(),ConstraintSet.END );
        }
        cs.applyTo(index_container);
    }

    /**
     * Create a single button for the index at the specified position
     *
     * @param position the position of the button in the list of all buttons, corresponds
     *                 to position of its initial
     * @return the created button
     */
    @SuppressLint("InflateParams")
    private Button makeIndexButton(int position) {
        Button but = (Button) LayoutInflater.from(parent).inflate(R.layout.button_index, null);
        but.setOnClickListener(view -> updateIndex(position));
        but.setText(initials.get(position).toString());
        DisplayMetrics dm = parent.getResources().getDisplayMetrics();
        int pixels = (int) (dm.density * 50f + 0.5f);
        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(pixels,pixels);
        but.setLayoutParams(params);
        but.setId(position+1000);
        return but;
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
     * Determines the occurring first letters of all last names to calculate the index size
     */
    private void readIndex() {
        if(initials == null || initials.size() == 0) {
            char buff;
            initials = new ArrayList<>();
            for(Person p : persons) {
                if(p.getFamName() == null || p.getFamName().length() == 0) {
                    continue;
                }
                buff = p.getFamName().charAt(0);
                if(!initials.contains(buff)){
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
    public void setUpList(ViewGroup root, char initial) {
        readIndex();
        Button buff;
        LinearLayout list_layout = root.findViewById(R.id.list_layout);
        for(Person person : persons) {
            if(person.getFamName().startsWith(initial + "")) {
                buff = addButton(person);
                list_layout.addView(buff);
            }
        }
    }

    /**
     * Create a single button for the person list with the specified person
     *
     * @param person the person to display
     * @return a new button
     */
    @SuppressLint("InflateParams")
    private Button addButton(Person person) {
        Button but = (Button) LayoutInflater.from(parent).inflate(R.layout.button_person_list, null);
        but.setOnClickListener(view -> {
            Intent intent = new Intent(parent, StoneInfoMainActivity.class);
            intent.setAction("" + person.getPersId());
            parent.startActivity(intent);
        });
        String display_name = person.getFormattedListName();
        but.setText(Html.fromHtml(display_name));
        return but;
    }

    // ++++ Helper classes ++++

    /**
     * Used to grab the person information from the data base
     */
    private class LoadPersonsTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            if(persons == null) {
                StolperpfadeRepository repo = new StolperpfadeRepository(parent.getApplication());
                persons = repo.getAllPersons();
            }
            return null;
        }
    }

    /**
     * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
     * sequence.
     */
    private class ListPagerAdapter extends FragmentStatePagerAdapter {
        ListPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return StoneListFragment.newInstance(StoneListViewModel.this, initials.get(position));
        }

        @Override
        public int getCount() {
            return initials.size();
        }
    }
}
