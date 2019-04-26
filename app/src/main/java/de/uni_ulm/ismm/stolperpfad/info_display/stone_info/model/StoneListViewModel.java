package de.uni_ulm.ismm.stolperpfad.info_display.stone_info.model;

import android.annotation.SuppressLint;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import java.util.ArrayList;
import java.util.List;

import de.uni_ulm.ismm.stolperpfad.R;
import de.uni_ulm.ismm.stolperpfad.database.StolperpfadeRepository;
import de.uni_ulm.ismm.stolperpfad.database.data.Person;
import de.uni_ulm.ismm.stolperpfad.info_display.stone_info.StoneInfoMainActivity;
import de.uni_ulm.ismm.stolperpfad.info_display.stone_info.StoneListActivity;
import de.uni_ulm.ismm.stolperpfad.info_display.stone_info.fragments.StoneListFragment;

@SuppressLint("StaticFieldLeak")
public class StoneListViewModel extends AndroidViewModel {

    private List<Person> persons;
    private  ArrayList<Character> initials;
    private ArrayList<Button> index_buttons;
    private ScrollView index_scroll_view;
    private Button last_pressed;
    private VerticalViewPager list_pager;
    private StolperpfadeRepository repo;
    private StoneListActivity parent;
    private static volatile StoneListViewModel INSTANCE;

    public StoneListViewModel(@NonNull Application application, StoneListActivity activity) {
        super(application);
        this.parent = activity;
        repo = new StolperpfadeRepository(application);
    }

    public static StoneListViewModel getInstance(StoneListActivity activity) {
        if(INSTANCE == null || INSTANCE.parent != activity) {
            INSTANCE = new StoneListViewModel(activity.getApplication(), activity);
        }
        return INSTANCE;
    }

    // ++++ Index Methods ++++

    private Button makeIndexButton(Context ctx, int index) {
        Button but = (Button) LayoutInflater.from(ctx).inflate(R.layout.button_index, null);
        but.setOnClickListener(view -> {
            updateIndex(index);
        });
        but.setText(initials.get(index).toString());
        DisplayMetrics dm = ctx.getResources().getDisplayMetrics();
        float dp = 50f;
        float fpixels = dm.density * dp;
        int pixels = (int) (fpixels + 0.5f);
        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(pixels,pixels);
        but.setLayoutParams(params);
        but.setId(index+1000);
        return but;
    }

    public void updateIndex(int index) {
        if(index_buttons == null) {
            setUpIndex();
        }
        list_pager.setCurrentItem(index);
        if(last_pressed != null)
            last_pressed.setBackground(null);
        index_buttons.get(index).setBackgroundResource(R.drawable.ic_index_highlight);
        last_pressed = index_buttons.get(index);
    }

    public void setUpIndex() {
        index_scroll_view = parent.findViewById(R.id.index_scroll_view);
        ConstraintLayout index = index_scroll_view.findViewById(R.id.index_layout);
        index_buttons = new ArrayList<>();
        int count = initials.size();
        if(count == 0) {
            return;
        }
        Button buff;
        for(int i = 0; i < count; i++) {
            index_buttons.add(buff = makeIndexButton(parent, i));
            index.addView(buff);
        }
        index_buttons.get(0).setBackgroundResource(R.drawable.ic_index_highlight);
        Button first = index_buttons.get(0);
        DisplayMetrics dm = parent.getResources().getDisplayMetrics();
        float dp = 16f;
        float fpixels = dm.density * dp;
        int margin = (int) (fpixels + 0.5f);
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
        last_pressed = first;
    }

    @SuppressLint("StaticFieldLeak")
    public void setUpIndex(StoneListActivity parent) {
        list_pager = parent.findViewById(R.id.stone_list_pager);
        list_pager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                updateIndex(position);
            }
        });
        loading(true);
        new LoadIndexTask() {
            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                if(initials == null || initials.size() == 0) {
                    readIndex();
                }
                PagerAdapter lpa = new ListPagerAdapter(parent.getSupportFragmentManager());
                list_pager.setAdapter(lpa);
                setUpIndex();
                loading(false);
            }
        }.execute();
    }

    private AlertDialog loading_info;

    private void loading(boolean start) {
        if(!start) {
            if(loading_info != null) {
                loading_info.dismiss();
            }
            loading_info = null;
        } else{
            if(loading_info != null) {
                loading_info.dismiss();
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(parent);
            builder.setTitle("Bitte haben Sie Geduld");
            builder.setMessage("Daten werden geladen...");
            builder.setCancelable(false);
            loading_info = builder.create();
            loading_info.show();
        }
    }

    private void readIndex() {
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

    public List<Person> getPersonsWithInitial(char initial) {
        List<Person> ret = new ArrayList<>();
        for(Person p : persons) {
            if(p.getFamName() != null && p.getFamName().length() != 0 && p.getFamName().startsWith(initial + "")) {
                ret.add(p);
            }
        }
        return null;
    }

    // ++++ List methods ++++

    public void setUpList(StoneListFragment fragment) {
        new LoadListTask (fragment) {
            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                if(initials == null || initials.size() == 0) {
                    readIndex();
                }
                buildList(fragment);
            }
        }.execute();
    }

    public char getInitial(int position) {
        if(initials == null || initials.size() == 0) {
            return 0;
        }
        return initials.get(position);
    }

    private void buildList(StoneListFragment fragment) {
        Button buff;
        LinearLayout list_layout = fragment.getView().findViewById(R.id.list_layout);
        for(Person person : persons) {
            if(person.getFamName().startsWith(fragment.getInitial() + "")) {
                buff = addButton(fragment, person);
                list_layout.addView(buff);
            }
        }
    }

    private Button addButton(StoneListFragment fragment, Person person) {
        Button but = (Button) LayoutInflater.from(fragment.getContext()).inflate(R.layout.button_person_list, null);
        but.setOnClickListener(view -> {
            Intent intent = new Intent(fragment.getActivity(), StoneInfoMainActivity.class);
            intent.setAction("" + person.getPersId());
            fragment.startActivity(intent);
        });
        String display_name = person.getFormattedListName();
        but.setText(Html.fromHtml(display_name));
        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 42, fragment.getResources().getDisplayMetrics());
        return but;
    }

    private class LoadListTask extends AsyncTask<Void, Void, Void> {

        private final StoneListFragment fragment;

        public LoadListTask(StoneListFragment frag) {
            fragment = frag;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            persons = repo.getAllPersons();
            return null;
        }
    }

    private class LoadIndexTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            persons = repo.getAllPersons();
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
            return StoneListFragment.newInstance(StoneListViewModel.this, getInitial(position));
        }

        @Override
        public int getCount() {
            return initials.size();
        }
    }
}
