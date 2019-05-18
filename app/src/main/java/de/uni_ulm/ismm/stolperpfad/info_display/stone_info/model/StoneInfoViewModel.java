package de.uni_ulm.ismm.stolperpfad.info_display.stone_info.model;

import android.annotation.SuppressLint;
import android.arch.lifecycle.AndroidViewModel;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidquery.AQuery;

import java.util.ArrayList;
import java.util.List;

import de.uni_ulm.ismm.stolperpfad.R;
import de.uni_ulm.ismm.stolperpfad.StolperpfadeApplication;
import de.uni_ulm.ismm.stolperpfad.database.StolperpfadeRepository;
import de.uni_ulm.ismm.stolperpfad.database.data.HistoricalTerm;
import de.uni_ulm.ismm.stolperpfad.database.data.Person;
import de.uni_ulm.ismm.stolperpfad.database.data_util.StringCreator;
import de.uni_ulm.ismm.stolperpfad.info_display.stone_info.StoneInfoMainActivity;
import de.uni_ulm.ismm.stolperpfad.info_display.stone_info.fragments.StoneInfoBioContentFragment;
import de.uni_ulm.ismm.stolperpfad.info_display.stone_info.fragments.StoneInfoBioFragment;
import de.uni_ulm.ismm.stolperpfad.info_display.stone_info.fragments.StoneInfoPersonFragment;
import de.uni_ulm.ismm.stolperpfad.map_activities.control.RoutePlannerActivity;

/**
 * This class represents the ViewModel for the person info page with their biography and map position
 */
@SuppressLint("StaticFieldLeak")
public class StoneInfoViewModel extends AndroidViewModel {


    private static final int DISPLAY_BIO = 0;
    private static final int DISPLAY_MAP = 1;
    private static final int DEFAULT_OFFSCREEN_PAGE_LIMIT = 1;
    private static final int VITA_POINTS_MIN = 2;
    private static final float VITA_POINT_SIZE = 16f;
    private static final float HALF_PIXEL = 0.5f;
    private static final int DEFAULT_ID_OFFSET = 1000;
    private static StoneInfoViewModel INSTANCE;

    private List<String> person_names;
    private List<String> historical_term_names;
    private List<Person> persons;
    private List<HistoricalTerm> historical_terms;
    private StolperpfadeRepository repo;
    private StoneInfoMainActivity parent;

    @SuppressLint("StaticFieldLeak")
    private StoneInfoViewModel(StoneInfoMainActivity activity) {
        super(activity.getApplication());
        this.parent = activity;
        historical_term_names = new ArrayList<>();
        person_names = new ArrayList<>();
        repo = new StolperpfadeRepository(activity.getApplication());
    }

    public static StoneInfoViewModel getInstance(StoneInfoMainActivity activity) {
        if (INSTANCE == null || INSTANCE.parent != activity) {
            INSTANCE = new StoneInfoViewModel( activity);
        }
        return INSTANCE;
    }

    // ++++ MAIN INFO METHODS ++++

    /**
     * This method initializes the logic and graphic content building for the person info pages
     *
     * @param info_pager the main pager switching between persons
     * @param action the action message that contains the persons id
     */
    @SuppressLint("StaticFieldLeak")
    public void setUpInformationPage(PersonInfoPager info_pager, String action) {
        new LoadContentTask(this) {
            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
                int current_person_index = getIndexFromId(action);
                PagerAdapter pager_adapter = new MainPagerAdapter(parent.getSupportFragmentManager(), persons.size());
                info_pager.setAdapter(pager_adapter);
                info_pager.setCurrentItem(current_person_index);
                parent.setPerson(current_person_index);
            }
        }.execute();
    }

    /**
     * Helper method that reads a persons id from a string and determines their corresponding position
     * in the list of all persons
     *
     * @param action the action message that contains the persons id
     * @return the person_index of this person in the list of all persons
     */
    private int getIndexFromId(String action) {
        if (action == null || action.length() == 0) {
            return 0;
        }
        int id;
        try {
            id = Integer.parseInt(action);
        } catch (NumberFormatException e) {
            return 0;
        }
        int ind = 0;
        for (Person p : persons) {
            if (p.getPersId() == id) { // TODO: Maybe insert query for optimization
                return ind;
            }
            ind++;
        }
        return 0;
    }

    // ++++ INDIVIDUAL PERSON INFO METHODS ++++

    /**
     * Builds the basic info content for a specific person
     *
     * @param root the root view that contains all the specific informations
     * @param person_index the index of the person in the list of all persons
     */
    @SuppressLint("StaticFieldLeak")
    public void showBasicPersonInfo(ViewGroup root, int person_index) {
        ViewPager bio_and_map_pager = root.findViewById(R.id.bio_and_map_pager);
        AQuery aq = new AQuery(root);
        Person current = persons.get(person_index);
        aq.id(R.id.title_stone_info).text(current.getFstName() + " " + current.getFamName());
        String geb_nam = current.getBiName();
        aq.id(R.id.sub_title_stone_info).text(geb_nam.length() == 0 ? "" : ("geb. " + geb_nam));
        aq.id(R.id.stone_info_to_bio_button).clicked(view -> setInfoDisplay(root, bio_and_map_pager, DISPLAY_BIO));
        aq.id(R.id.stone_info_to_map_button).clicked(view -> setInfoDisplay(root, bio_and_map_pager, DISPLAY_MAP));
        bio_and_map_pager.setOffscreenPageLimit(DEFAULT_OFFSCREEN_PAGE_LIMIT); // TODO: test if this breaks the info pages
    }

    /**
     * Update the person info content to show either the biography or the map info
     *
     * @param root the root view that contains the persons info
     * @param bio_and_map_pager the pager that switches between the bio and the map
     * @param info_content_to_display the info content that should now be displayed
     */
    private void setInfoDisplay(@NonNull ViewGroup root, @NonNull ViewPager bio_and_map_pager, int info_content_to_display) {
        bio_and_map_pager.setCurrentItem(info_content_to_display);
        updateContentButtons(root, info_content_to_display);
    }

    /**
     * Updates the buttons that switch between the bio and the map content
     *
     * @param root the root view that contains the persons info
     * @param info_content_to_display the info content that should now be displayed
     */
    public void updateContentButtons(@NonNull ViewGroup root, int info_content_to_display) {
        Button bio_button, map_button;
        bio_button = root.findViewById(R.id.stone_info_to_bio_button);
        map_button = root.findViewById(R.id.stone_info_to_map_button);
        setButtonActive(bio_button, info_content_to_display == DISPLAY_BIO);
        setButtonActive(map_button, info_content_to_display == DISPLAY_MAP);
    }

    /**
     * This method sets the correct design to the buttons that switch between the
     * bio and map content, so that the button corresponding to the displayed page
     * is highlighted
     *
     * @param button the button to change the look of
     * @param active if this button's content is displayed
     */
    @SuppressLint("ResourceType")
    private void setButtonActive(Button button, boolean active) {
        int[] attr;
        if(active) {
            attr = new int[]{R.attr.colorAppAccent, R.attr.colorAppTextButtonAccent};
        } else {
            attr = new int[]{ R.attr.colorAppPrimaryContrast, R.attr.colorAppTextButtonContrast};
        }
        TypedArray ta = parent.obtainStyledAttributes(attr);
        int bg_color = ta.getResourceId(0, android.R.color.black);
        int text_color = ta.getResourceId(1, android.R.color.black);
        button.setBackgroundColor(parent.getResources().getColor(bg_color, parent.getTheme()));
        button.setTextColor(parent.getResources().getColor(text_color, parent.getTheme()));
        ta.recycle();
    }

    // ++++ MAP CONTENT METHODS ++++

    /**
     * Shows the map information for the current person and prepares the redirecting calls to the map
     *
     * @param root the root view that contains the persons map info
     * @param person_index the index of the person in the list of all persons
     */
    @SuppressLint("StaticFieldLeak")
    public void showMapContent(ViewGroup root, int person_index) {
        Person current_person = persons.get(person_index);
        new LoadStoneAddressTask(this) {
            @Override
            protected void onPostExecute(String address) {
                TextView address_text = root.findViewById(R.id.address);
                address_text.setText(address);
                ImageView to_map = root.findViewById(R.id.map_basic);
                to_map.setOnClickListener(view -> {
                    Intent intent = new Intent(parent, RoutePlannerActivity.class);
                    intent.putExtra("id", current_person.getStolperstein());
                    intent.putExtra("next", true);
                    parent.startActivity(intent);
                });
            }
        }.execute(current_person.getStolperstein());
    }

    // ++++ PERSON VITA CONTENT METHODS ++++

    /**
     * Prepares the creation of the vita buttons and the corresponding pager for the vita content
     *
     * @param fragment the parent fragment for the bio content
     * @param root the view root containing the graphical contents
     * @param person_index the current persons index in the list of all persons
     */
    @SuppressLint("StaticFieldLeak")
    public void buildPersonVita(StoneInfoBioFragment fragment, ViewGroup root, int person_index) {
        ConstraintLayout bio_layout = root.findViewById(R.id.bio_layout);
        RotatedViewPager bio_pager = root.findViewById(R.id.bio_view_pager);
        bio_pager.setCurrentItem(0);
        createVitaButtons(fragment, bio_pager, bio_layout, person_index);
    }

    /**
     * Creates the vita buttons for one persons biography and styles them according to our design
     *
     * @param fragment the parent fragment for the bio content
     * @param bio_pager the corresponding pager that shows the vita contents
     * @param bio_layout the layout containing the vita buttons
     * @param person_index the current persons index in the list of all persons
     */
    @SuppressLint("StaticFieldLeak")
    private void createVitaButtons(StoneInfoBioFragment fragment, ViewPager bio_pager, ConstraintLayout bio_layout, int person_index) {
        new LoadVitaTask(this) {
            @Override
            protected void onPostExecute(List<Person.Vita> vitas) {
                // Check if the vita has been properly loaded
                if (vitas == null || vitas.size() == 0) {
                    return;
                }
                Person.Vita current_vita = vitas.get(0);
                int points = current_vita.getSize();
                if (points < VITA_POINTS_MIN) {
                    return;
                }
                // initialize the vita content pager with the size of all vita points
                FragmentManager cfm = fragment.getChildFragmentManager();
                PagerAdapter pagerAdapter = new VitaPagerAdapter(cfm, current_vita.getSize(), person_index);
                bio_pager.setAdapter(pagerAdapter);

                // initialize the buttons
                ArrayList<Button> vita_buttons;
                vita_buttons = new ArrayList<>();
                Button first_button = makeVitaButton(LayoutInflater.from(fragment.getContext()), bio_pager, 0);
                Button last_button = makeVitaButton(LayoutInflater.from(fragment.getContext()), bio_pager, points - 1);
                vita_buttons.add(first_button);
                Button buff;
                for (int i = 0; i < points - 2; i++) {
                    vita_buttons.add(buff = makeVitaButton(LayoutInflater.from(fragment.getContext()), bio_pager, i + 1));
                    bio_layout.addView(buff);
                }
                vita_buttons.add(last_button);
                bio_layout.addView(first_button);
                bio_layout.addView(last_button);

                // constrain the buttons so that they will be spread out symmetrically
                ConstraintSet cs = new ConstraintSet();
                cs.clone(bio_layout);
                cs.connect(first_button.getId(), ConstraintSet.TOP, bio_layout.getId(), ConstraintSet.TOP);
                cs.connect(first_button.getId(), ConstraintSet.START, bio_layout.getId(), ConstraintSet.START);
                cs.connect(first_button.getId(), ConstraintSet.END, bio_pager.getId(), ConstraintSet.START);
                for (int i = 1; i <= points - 2; i++) {
                    cs.connect(vita_buttons.get(i).getId(), ConstraintSet.TOP, vita_buttons.get(i - 1).getId(), ConstraintSet.BOTTOM);
                    cs.connect(vita_buttons.get(i).getId(), ConstraintSet.BOTTOM, vita_buttons.get(i + 1).getId(), ConstraintSet.TOP);
                    cs.connect(vita_buttons.get(i).getId(), ConstraintSet.START, bio_layout.getId(), ConstraintSet.START);
                    cs.connect(vita_buttons.get(i).getId(), ConstraintSet.END, bio_pager.getId(), ConstraintSet.START);
                }
                cs.connect(last_button.getId(), ConstraintSet.BOTTOM, bio_layout.getId(), ConstraintSet.BOTTOM);
                cs.connect(last_button.getId(), ConstraintSet.START, bio_layout.getId(), ConstraintSet.START);
                cs.connect(last_button.getId(), ConstraintSet.END, bio_pager.getId(), ConstraintSet.START);
                cs.applyTo(bio_layout);
                fragment.setVitaButtons(vita_buttons);
                updateVitaButtons(fragment, 0);
            }
        }.execute(persons.get(person_index).getPersId());
    }

    /**
     * Create a single vita button for the specific vita point
     *
     * @param inflater the inflater that will inflate the button layout file
     * @param bio_pager the corresponding pager that shows the vita contents
     * @param bio_index the index of the vita point
     * @return a new vita butotn
     */
    @SuppressLint("InflateParams")
    private Button makeVitaButton(LayoutInflater inflater, ViewPager bio_pager, int bio_index) {
        Button but = (Button) inflater.inflate(R.layout.bio_button_layout, null);
        but.setOnClickListener(view -> showInfo(bio_pager, bio_index));
        but.setBackgroundResource(R.drawable.ic_bio_point_off);
        DisplayMetrics dm = parent.getResources().getDisplayMetrics();
        int pixels = (int) (dm.density * VITA_POINT_SIZE + HALF_PIXEL);
        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(pixels, pixels);
        but.setLayoutParams(params);
        but.setId(bio_index + DEFAULT_ID_OFFSET);
        return but;
    }

    /**
     * Highlights the currently displayed vita contents button
     *
     * @param fragment the parent fragment
     * @param bio_index the index of the vita point
     */
    public void updateVitaButtons(StoneInfoBioFragment fragment, int bio_index) {
        for (Button b : fragment.getVitaButtons()) {
            if (StolperpfadeApplication.getInstance().isDarkMode()) {
                b.setBackgroundResource(R.drawable.ic_bio_off_dark);
            } else {
                b.setBackgroundResource(R.drawable.ic_bio_point_off);
            }
        }
        fragment.getVitaButtons().get(bio_index).setBackgroundResource(R.drawable.ic_bio_point_on);
    }

    /**
     * Update the displayed vita
     *
     * @param bio_pager the corresponding pager that shows the vita contents
     * @param bio_index the index of the vita point
     */
    private void showInfo(ViewPager bio_pager, int bio_index) {
        bio_pager.setCurrentItem(bio_index);
    }

    /**
     * Displays the information for a single vita point with highlighted tags
     *
     * @param root the view root containing the graphical contents
     * @param person_index_in_list the index of the person in the list of all persons
     * @param vita_point the index of the vita point in all vita points
     */
    @SuppressLint("StaticFieldLeak")
    public void showVitaContent(ViewGroup root, int person_index_in_list, int vita_point) {
        new LoadVitaTask(this) {
            @Override
            protected void onPostExecute(List<Person.Vita> found_vitas) {
                if (found_vitas == null || found_vitas.size() == 0) {
                    return;
                }
                String content = found_vitas.get(0).getSection(vita_point);
                AQuery aq = new AQuery(root);
                String[] content_temp = content.split("#");
                String title;
                String text;
                if (content_temp.length > 1) {
                    title = content_temp[0];
                    text = content_temp[1];
                } else {
                    title = "(no title)";
                    text = content_temp[0];
                }
                ArrayList<String> terms_to_highlight = new ArrayList<>();
                for (String person : person_names) {
                    if (!person.equals(persons.get(person_index_in_list).getEntireName())) {
                        terms_to_highlight.add(person);
                    }
                }
                terms_to_highlight.addAll(historical_term_names);
                //call method which identifies in a text the terms to highlight
                SpannableString highlighted_text = StringCreator.makeSpanWith(text, parent, terms_to_highlight);
                if(title != null && title.length()>0){
                    aq.id(R.id.title_bio_point).text(title);
                }
                if(text != null && text.length()>0){
                    TextView text_bio_point = root.findViewById(R.id.text_bio_point);
                    text_bio_point.setText(highlighted_text);
                    text_bio_point.setMovementMethod(LinkMovementMethod.getInstance());
                    text_bio_point.setHighlightColor(Color.TRANSPARENT);
                }
            }
        }.execute(persons.get(person_index_in_list).getPersId());
    }

    // ++++++ IMPORTANT HELPER CLASSES ++++++

    /**
     * Helper Task to load all needed data from the data base
     */
    private static class LoadContentTask extends AsyncTask<Void, Void, Void> {

        private StoneInfoViewModel model;

        LoadContentTask(StoneInfoViewModel model) {
            this.model = model;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            model.persons = model.repo.getAllPersons();
            //load data for highlighting
            model.historical_terms = model.repo.getAllTerms();
            for (HistoricalTerm term : model.historical_terms) {
                String current = term.getName();
                model.historical_term_names.add(current);
            }
            for (Person pers : model.persons) {
                String current = pers.getEntireName();
                model.person_names.add(current);
            }
            return null;
        }
    }

    /**
     * Helper Task to load only a specific stone address from the data base
     */
    private static class LoadStoneAddressTask extends AsyncTask<Integer, Void, String> {

        private StoneInfoViewModel model;

        LoadStoneAddressTask(StoneInfoViewModel model) {
            this.model = model;
        }

        @Override
        protected String doInBackground(Integer... indices) {
            int i = indices[0];
            return model.repo.getAddress(i);
        }
    }

    /**
     * Helper Task to load the vita for a specific person from the data base
     */
    private static class LoadVitaTask extends AsyncTask<Integer, Void, List<Person.Vita>> {

        private StoneInfoViewModel model;

        LoadVitaTask(StoneInfoViewModel model) {
            this.model = model;
        }

        @Override
        protected List<Person.Vita> doInBackground(Integer... indices) {
            int person_id = indices[0];
            return model.repo.getVita(person_id);
        }
    }

    /**
     * The pager adapter for the main pager displaying the complete persons information
     */
    private class MainPagerAdapter extends FragmentStatePagerAdapter {

        private final int PERSONS;

        MainPagerAdapter(FragmentManager fm, int person_count) {
            super(fm);
            PERSONS = person_count;
        }

        @Override
        public Fragment getItem(int person_index) {
            StoneInfoPersonFragment fragment = StoneInfoPersonFragment.newInstance(person_index);
            fragment.setRetainInstance(true);
            return fragment;
        }

        @Override
        public int getCount() {
            return PERSONS;
        }
    }

    /**
     * The pager adapter for the biography contents of the persons
     */
    public class VitaPagerAdapter extends FragmentStatePagerAdapter {

        private int vita_size;
        private int person_index;

        VitaPagerAdapter(FragmentManager fm, int vita_size, int person_index) {
            super(fm);
            this.vita_size = vita_size;
            this.person_index = person_index;
        }

        @Override
        public Fragment getItem(int vita_point) {
            return StoneInfoBioContentFragment.newInstance(person_index, vita_point);
        }

        @Override
        public int getCount() {
            return vita_size;
        }
    }
}
