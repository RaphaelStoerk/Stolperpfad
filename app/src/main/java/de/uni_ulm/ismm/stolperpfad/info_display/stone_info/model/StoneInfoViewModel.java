package de.uni_ulm.ismm.stolperpfad.info_display.stone_info.model;

import android.annotation.SuppressLint;
import android.app.Application;
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
import android.util.Log;
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

public class StoneInfoViewModel extends AndroidViewModel {

    private List<Person> persons;
    private List<HistoricalTerm> histoTerms;
    private List<String> histoTermNames;
    private List<String> personNames;
    private StolperpfadeRepository repo;
    private StoneInfoMainActivity parent;
    private static StoneInfoViewModel INSTANCE;
    private boolean loaded;

    private final int DISPLAY_BIO = 0;
    private final int DISPLAY_MAP = 1;

    @SuppressLint("StaticFieldLeak")
    public StoneInfoViewModel(@NonNull Application application, StoneInfoMainActivity activity) {
        super(application);
        this.parent = activity;
        histoTermNames = new ArrayList<>();
        personNames = new ArrayList<>();
        repo = new StolperpfadeRepository(application);
    }

    public static StoneInfoViewModel getInstance(StoneInfoMainActivity activity) {
        if (INSTANCE == null || INSTANCE.parent != activity) {
            INSTANCE = new StoneInfoViewModel(activity.getApplication(), activity);
        }
        return INSTANCE;
    }

    // ++++ INFO MAIN METHODS ++++

    public int getIndexFromId(String action) {
        if (action == null || action.length() == 0) {
            Log.i("MY_DATA_TAG", "Empty action");
            return 0;
        }
        int id;
        try {
            id = Integer.parseInt(action);

        } catch (NumberFormatException e) {
            Log.i("MY_DATA_TAG", "String could not be parsed: " + action);
            return 0;
        }
        int ind = 0;
        for (Person p : persons) {
            if (p.getPersId() == id) { // TODO: Maybe insert query for optimization
                return ind;
            }
            ind++;
        }
        Log.i("MY_DATA_TAG", "No person found with id: " + id);
        return 0;
    }

    public int getPersonCount() {
        if (persons == null) {
            return 0;
        } else {
            return persons.size();
        }
    }

    // ++++ INDIVIDUAL PERSON INFO METHODS ++++

    @SuppressLint("StaticFieldLeak")
    public void showBasicPersonInfo(ViewGroup root, @NonNull ViewPager pager, int index) {
        if (!loaded) {
            new LoadContentTask(this) {
                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    loaded = true;
                    showBasicPersonInfoHelper(root, pager, index);
                }
            }.execute();
        } else {
            showBasicPersonInfoHelper(root, pager, index);
        }

    }

    private void showBasicPersonInfoHelper(ViewGroup root, @NonNull ViewPager pager, int index) {
        AQuery aq = new AQuery(root);
        Person current = persons.get(index);
        aq.id(R.id.title_stone_info).text(current.getFstName() + " " + current.getFamName());
        String geb_nam = current.getBiName();
        aq.id(R.id.sub_title_stone_info).text(geb_nam.length() == 0 ? "" : ("geb. " + geb_nam));
        aq.id(R.id.stone_info_to_bio_button).clicked(view -> setInfoDisplay(root, pager, 0));
        aq.id(R.id.stone_info_to_map_button).clicked(view -> setInfoDisplay(root, pager, 1));
        pager.setOffscreenPageLimit(0);
    }

    public void updatePersonInfoContent(@NonNull ViewGroup root, int position) {
        updateButtons(root, position);
    }


    public void setInfoDisplay(@NonNull ViewGroup root, @NonNull ViewPager pager, int position) {
        if (position == DISPLAY_BIO) {
            pager.setCurrentItem(DISPLAY_BIO);
        } else if (position == DISPLAY_MAP) {
            pager.setCurrentItem(DISPLAY_MAP);
        }
        updateButtons(root, position);
    }

    public void updateButtons(@NonNull ViewGroup root, int position) {
        Button bio_button, map_button;
        bio_button = root.findViewById(R.id.stone_info_to_bio_button);
        map_button = root.findViewById(R.id.stone_info_to_map_button);
        if (position == DISPLAY_BIO) {
            setButtonActive(bio_button, true);
            setButtonActive(map_button, false);
        } else if (position == DISPLAY_MAP) {
            setButtonActive(bio_button, false);
            setButtonActive(map_button, true);
        }
    }

    @SuppressLint("ResourceType")
    public void setButtonActive(Button button, boolean active) {
        int[] attr = {R.attr.colorAppAccent, R.attr.colorAppTextButtonAccent, R.attr.colorAppPrimaryContrast, R.attr.colorAppTextButtonContrast};
        TypedArray ta = parent.obtainStyledAttributes(attr);

        int bg_color_active = ta.getResourceId(0, android.R.color.black);
        int text_color_active = ta.getResourceId(1, android.R.color.black);
        int bg_color_inactive = ta.getResourceId(2, android.R.color.black);
        int text_color_inactive = ta.getResourceId(3, android.R.color.black);
        if (active) {
            button.setBackgroundColor(parent.getResources().getColor(bg_color_active, parent.getTheme()));
            button.setTextColor(parent.getResources().getColor(text_color_active, parent.getTheme()));
        } else {
            button.setBackgroundColor(parent.getResources().getColor(bg_color_inactive, parent.getTheme()));
            button.setTextColor(parent.getResources().getColor(text_color_inactive, parent.getTheme()));
        }
        ta.recycle();
    }

    @SuppressLint("StaticFieldLeak")
    public void setUpPersonPage(PersonInfoPager infoPager, String action) {
        new LoadContentTask(this) {
            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                loaded = true;
                int current_person_index = getIndexFromId(action);
                PagerAdapter pagerAdapter = new MainPagerAdapter(parent.getSupportFragmentManager(), persons.size());
                infoPager.setAdapter(pagerAdapter);
                infoPager.setCurrentItem(current_person_index);
                parent.setPerson(current_person_index); // TODO: unnecessary??
            }
        }.execute();
    }

    // ++++ MAP CONTENT METHODS ++++

    @SuppressLint("StaticFieldLeak")
    public void showMapContent(ViewGroup root, int index) {
        if (!loaded) {
            new LoadContentTask(this) {
                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    loaded = true;
                    showMapContent(root, persons.get(index));
                }
            }.execute();
        } else {
            showMapContent(root, persons.get(index));
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void showMapContent(ViewGroup root, Person p) {
        new LoadStoneAddressTask(this) {
            @Override
            protected void onPostExecute(String address) {
                TextView address_text = root.findViewById(R.id.address);
                address_text.setText(address);
                ImageView to_map = root.findViewById(R.id.map_basic);
                to_map.setOnClickListener(view -> {
                    Intent intent = new Intent(parent, RoutePlannerActivity.class);
                    intent.putExtra("id", p.getStolperstein());
                    intent.putExtra("next", true);
                    parent.startActivity(intent);
                });
            }
        }.execute(p.getStolperstein());
    }

    // ++++ PERSON VITA CONTENT METHODS ++++

    @SuppressLint("StaticFieldLeak")
    public void buildPersonVita(StoneInfoBioFragment fragment, FragmentManager cfm, LayoutInflater inflater, ViewGroup root, ViewPager bio_pager, int index) {
        ConstraintLayout bio_layout = root.findViewById(R.id.bio_layout);
        bio_pager.setCurrentItem(0);
        if (!loaded) {
            new LoadContentTask(this) {
                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    loaded = true;
                    createVitaButtons(fragment, cfm, inflater, bio_pager, bio_layout, persons.get(index).getPersId(), index);
                }
            }.execute();
        } else {
            createVitaButtons(fragment, cfm, inflater, bio_pager, bio_layout, persons.get(index).getPersId(), index);
        }
    }


    public void updateVitaButtons(StoneInfoBioFragment fragment, int index) {
        for (Button b : fragment.getVitaButtons()) {
            if (StolperpfadeApplication.getInstance().isDarkMode()) {
                b.setBackgroundResource(R.drawable.ic_bio_off_dark);
            } else {
                b.setBackgroundResource(R.drawable.ic_bio_point_off);
            }
        }
        fragment.getVitaButtons().get(index).setBackgroundResource(R.drawable.ic_bio_point_on);
    }

    @SuppressLint("StaticFieldLeak")
    private void createVitaButtons(StoneInfoBioFragment fragment, FragmentManager cfm, LayoutInflater inflater, ViewPager bio_pager, ConstraintLayout bio_layout, int index, int position) {
        new LoadVitaTask(this) {
            @Override
            protected void onPostExecute(List<Person.Vita> vitas) {
                if (vitas == null || vitas.size() == 0) {
                    return;
                }
                Person.Vita current_vita = vitas.get(0);
                int points = current_vita.getSize();
                if (points < 2) {
                    return;
                }
                PagerAdapter pagerAdapter = new VitaPagerAdapter(cfm, current_vita.getSize(), position);
                bio_pager.setAdapter(pagerAdapter);
                ArrayList<Button> vita_buttons;
                vita_buttons = new ArrayList<>();
                Button birth_button = makeVitaButton(inflater, bio_pager, 0);
                Button death_button = makeVitaButton(inflater, bio_pager, points - 1);
                vita_buttons.add(birth_button);
                Button buff;
                for (int i = 0; i < points - 2; i++) {
                    vita_buttons.add(buff = makeVitaButton(inflater, bio_pager, i + 1));
                    bio_layout.addView(buff);
                }
                vita_buttons.add(death_button);
                bio_layout.addView(birth_button);
                bio_layout.addView(death_button);

                ConstraintSet cs = new ConstraintSet();
                cs.clone(bio_layout);
                cs.connect(birth_button.getId(), ConstraintSet.TOP, bio_layout.getId(), ConstraintSet.TOP);
                // cs.connect(birth_button.getId(),ConstraintSet.BOTTOM, vita_buttons.get(1).getId(),ConstraintSet.TOP );
                cs.connect(birth_button.getId(), ConstraintSet.START, bio_layout.getId(), ConstraintSet.START, 16);
                cs.connect(birth_button.getId(), ConstraintSet.END, bio_pager.getId(), ConstraintSet.START, 16);
                for (int i = 1; i <= points - 2; i++) {
                    cs.connect(vita_buttons.get(i).getId(), ConstraintSet.TOP, vita_buttons.get(i - 1).getId(), ConstraintSet.BOTTOM);
                    cs.connect(vita_buttons.get(i).getId(), ConstraintSet.BOTTOM, vita_buttons.get(i + 1).getId(), ConstraintSet.TOP);
                    cs.connect(vita_buttons.get(i).getId(), ConstraintSet.START, bio_layout.getId(), ConstraintSet.START, 16);
                    cs.connect(vita_buttons.get(i).getId(), ConstraintSet.END, bio_pager.getId(), ConstraintSet.START, 16);
                }
                // cs.connect(death_button.getId(),ConstraintSet.TOP, vita_buttons.get(points-2).getId(),ConstraintSet.BOTTOM );
                cs.connect(death_button.getId(), ConstraintSet.BOTTOM, bio_layout.getId(), ConstraintSet.BOTTOM);
                cs.connect(death_button.getId(), ConstraintSet.START, bio_layout.getId(), ConstraintSet.START, 16);
                cs.connect(death_button.getId(), ConstraintSet.END, bio_pager.getId(), ConstraintSet.START, 16);
                cs.applyTo(bio_layout);
                fragment.setVitaButtons(vita_buttons);
                updateVitaButtons(fragment, 0);
            }
        }.execute(index);
    }

    private Button makeVitaButton(LayoutInflater inflater, ViewPager bio_pager, int bio_index) {
        Button but = (Button) inflater.inflate(R.layout.bio_button_layout, null);
        but.setOnClickListener(view -> showInfo(bio_pager, bio_index));
        but.setBackgroundResource(R.drawable.ic_bio_point_off);
        DisplayMetrics dm = parent.getResources().getDisplayMetrics();
        float dp = 16f;
        float fpixels = dm.density * dp;
        int pixels = (int) (fpixels + 0.5f);
        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(pixels, pixels);
        but.setLayoutParams(params);
        but.setId(bio_index + 1000);
        return but;
    }

    private void showInfo(ViewPager bio_pager, int index) {
        bio_pager.setCurrentItem(index);
    }

    // Interface to Database

    @SuppressLint("StaticFieldLeak")
    private void loadFromDatabase() {
        if (loaded) {
            return;
        }
        new LoadContentTask(this) {
            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                loaded = true;
            }
        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    public void showVitaContent(ViewGroup root, int person, int point) {
        if (!loaded) {
            new LoadContentTask(this) {
                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    loaded = true;
                    showVitaContentHelper(root, persons.get(person).getPersId(), point, persons.get(person).getEntireName());
                }
            }.execute();
        } else {
            showVitaContentHelper(root, persons.get(person).getPersId(), point, persons.get(person).getEntireName());
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void showVitaContentHelper(ViewGroup root, int persId, int point, String currentPersName) {
        new LoadVitaTask(this) {
            @Override
            protected void onPostExecute(List<Person.Vita> vitas) {
                if (vitas == null || vitas.size() == 0) {
                    return;
                }
                String content = vitas.get(0).getSection(point);
                AQuery aq = new AQuery(root);

                String[] contentTemp = content.split("#");
                String title = "";
                String text = "";
                if (contentTemp != null && contentTemp.length > 1) {
                    title = contentTemp[0];
                    text = contentTemp[1];
                }

                //highlight terms in text
                //list with all terms to highlight
                ArrayList<String> allHighlightTerms = new ArrayList<>();
                for (String person : personNames) {
                    if (person.equals(currentPersName)) {

                    } else {
                        allHighlightTerms.add(person);
                    }
                }
                for (String term : histoTermNames) {
                    allHighlightTerms.add(term);
                }
                //call method which identify in a text the terms to highlight
                SpannableString newContent = StringCreator.makeSpanWith(text, parent, allHighlightTerms);
                if(title != null && title.length()>0){
                    aq.id(R.id.title_bio_point).text(title);
                }
                if(text != null && text.length()>0){
                    TextView textContentView = root.findViewById(R.id.text_bio_point);
                    textContentView.setText(newContent);
                    textContentView.setMovementMethod(LinkMovementMethod.getInstance());
                    textContentView.setHighlightColor(Color.TRANSPARENT);
                }
            }
        }.execute(persId);
    }


    private static class LoadContentTask extends AsyncTask<Void, Void, Void> {

        private StoneInfoViewModel model;

        LoadContentTask(StoneInfoViewModel model) {
            this.model = model;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            model.persons = model.repo.getAllPersons();
            //load data for highlighting
            model.histoTerms = model.repo.getAllTerms();
            for (HistoricalTerm term : model.histoTerms) {
                String current = term.getName();
                model.histoTermNames.add(current);
            }
            for (Person pers : model.persons) {
                String current = pers.getEntireName();
                model.personNames.add(current);
            }
            return null;
        }
    }

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

    private static class LoadVitaTask extends AsyncTask<Integer, Void, List<Person.Vita>> {

        private StoneInfoViewModel model;

        LoadVitaTask(StoneInfoViewModel model) {
            this.model = model;
        }

        @Override
        protected List<Person.Vita> doInBackground(Integer... indices) {
            int i = indices[0];
            return model.repo.getVita(i);
        }
    }

    /**
     * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
     * sequence.
     */
    public class VitaPagerAdapter extends FragmentStatePagerAdapter {

        private int vita_size;
        private int index;

        VitaPagerAdapter(FragmentManager fm, int size, int index) {
            super(fm);
            vita_size = size;
            this.index = index;

        }

        @Override
        public Fragment getItem(int position) {
            return StoneInfoBioContentFragment.newInstance(StoneInfoViewModel.this, index, position);
        }

        @Override
        public int getCount() {
            return vita_size;
        }
    }


    /**
     * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
     * sequence.
     */
    private class MainPagerAdapter extends FragmentStatePagerAdapter {

        private final int PERSONS;

        MainPagerAdapter(FragmentManager fm, int person_count) {
            super(fm);
            PERSONS = person_count;
        }

        @Override
        public Fragment getItem(int position) {
            StoneInfoPersonFragment fragment = StoneInfoPersonFragment.newInstance(StoneInfoViewModel.this, position);
            fragment.setRetainInstance(true);
            return fragment;
        }

        @Override
        public int getCount() {
            return PERSONS;
        }
    }
}
