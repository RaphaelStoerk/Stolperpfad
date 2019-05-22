package de.uni_ulm.ismm.stolperpfad;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import de.uni_ulm.ismm.stolperpfad.database.StolperpfadeRepository;

/**
 * This is the heart of the application, it contains logic for storing general information for
 * easier acces and methods for initializing application relevant background content
 */
public class StolperpfadeApplication extends Application {

    private static volatile StolperpfadeApplication INSTANCE;
    public static final String DATA_FILES_PATH = Environment.getExternalStorageDirectory() + "/stolperpfade/data";
    private static final int BUFFER_SIZE = 1024;

    private boolean first_call;
    private boolean file_tree_ready = false;
    private boolean ocr_language_file_ready = false;
    private boolean image_buffer_ready = false;
    private SharedPreferences prefs;

    public static StolperpfadeApplication getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new StolperpfadeApplication();
        }
        return INSTANCE;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = this;
        // initialize general values
        prefs = this.getSharedPreferences("de.uni_ulm.ismm.stolperpfad", Context.MODE_PRIVATE);
        if (!prefs.getBoolean("de.uni_ulm.ismm.stolperpfad.dark_mode", false)) {
            prefs.edit().putBoolean("de.uni_ulm.ismm.stolperpfad.dark_mode", false).apply();
        }
        prefs.edit().putBoolean("de.uni_ulm.ismm.stolperpfad.first_call", true).apply();
        prefs.edit().putBoolean("de.uni_ulm.ismm.stolperpfad.file_tree_ready", false).apply();
        prefs.edit().putString("de.uni_ulm.ismm.stolperpfad.next_stone_mem", "").apply();
    }

    public boolean fileTreeIsNotReady() {
        return !(file_tree_ready = prefs.getBoolean("de.uni_ulm.ismm.stolperpfad.file_tree_ready", false));
    }

    public boolean isDarkMode() {
        return prefs.getBoolean("de.uni_ulm.ismm.stolperpfad.dark_mode", false);
    }

    public void setDarkMode(boolean dark_mode) {
        prefs.edit().putBoolean("de.uni_ulm.ismm.stolperpfad.dark_mode", dark_mode).apply();
    }

    /**
     * This methods checks if the needed file structure is already installed or if not tries
     * to create the neede directories for storing bulky files
     */
    public void setupFileTree() {
        boolean file_tree = prefs.getBoolean("de.uni_ulm.ismm.stolperpfad.file_tree_ready", false);
        if (file_tree) {
            return;
        }
        // create the needed files and directories
        File tess = new File(DATA_FILES_PATH + "/tessdata");
        File img = new File(DATA_FILES_PATH + "/img");
        File routes = new File(DATA_FILES_PATH + "/paths");
        boolean tess_exists = tess.mkdirs() || tess.exists();
        boolean img_exists = img.mkdirs() || img.exists();
        boolean routes_exists = routes.mkdirs() || routes.exists();
        if(tess_exists) {
            // load the trained language file needed for scanning text from an image
            File lang_file = new File(tess, "deu.traineddata");
            OutputStream out;
            if (!lang_file.exists() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                try {
                    out = new FileOutputStream(lang_file);
                    InputStream in = getResources().openRawResource(R.raw.traineddata);
                    byte[] buffer = new byte[BUFFER_SIZE];
                    int len;
                    while ((len = in.read(buffer, 0, buffer.length)) != -1) {
                        out.write(buffer, 0, len);
                    }
                    ocr_language_file_ready = true;
                    in.close();
                    out.close();

                } catch (IOException e) {
                    ocr_language_file_ready = false;
                    e.printStackTrace();
                }
            }
        }
        if(img_exists) {
            // set up the image storage for the scanned image, may be unnecessary
            File image_buff = new File(img, "last_scanned_stone.jpg");
            if (!image_buff.exists()) {
                try {
                    image_buffer_ready = image_buff.createNewFile();
                } catch (IOException e) {
                    image_buffer_ready = false;
                }
            } else {
                image_buffer_ready = true;
            }
        }
        file_tree_ready = ocr_language_file_ready && image_buffer_ready && routes_exists;
        prefs.edit().putBoolean("de.uni_ulm.ismm.stolperpfad.ocr_ready", ocr_language_file_ready).apply();
        prefs.edit().putBoolean("de.uni_ulm.ismm.stolperpfad.image_buffer_ready", image_buffer_ready).apply();
        prefs.edit().putBoolean("de.uni_ulm.ismm.stolperpfad.file_tree_ready", file_tree_ready).apply();
    }

    /**
     * Initializes the data base
     */
    public void setUpDb() {
        prefs.edit().putBoolean("de.uni_ulm.ismm.stolperpfad.db_ready", false).apply();
        StolperpfadeRepository repo = new StolperpfadeRepository(this);
        repo.getAllPersons();
        repo.getAllStones();
        repo.getAllTerms();
        prefs.edit().putBoolean("de.uni_ulm.ismm.stolperpfad.db_ready", true).apply();
    }

    public void saveStringInPreferences(String tag, String content) {
        prefs.edit().putString("de.uni_ulm.ismm.stolperpfad" + "." + tag, content).apply();
    }

    /**
     * Adding a stone id to the list of already viewed stones, so that the user can always be redirected
     * to a new stone if they want to
     *
     * @param stoneId the last viewed stone's id
     */
    public void addStoneToMemory(int stoneId) {
        String curr_mem = prefs.getString("de.uni_ulm.ismm.stolperpfad.next_stone_mem", "");
        if(curr_mem.length() == 0) {
            curr_mem = stoneId + "";
        } else {
            curr_mem += "," + stoneId;
        }
        prefs.edit().putString("de.uni_ulm.ismm.stolperpfad.next_stone_mem", curr_mem).apply();
    }

    /**
     * Creates a list with all the stone ids, that the user has visited in the current session
     *
     * @return the list with all visitied stone ids
     */
    public int[] getVisitedStones() {
        String[] buff = prefs.getString("de.uni_ulm.ismm.stolperpfad.next_stone_mem", "").split(",");
        int[] ret = new int[buff.length];
        int i;
        int j = 0;
        for(String s : buff) {
            try{
                i = Integer.parseInt(s);
            } catch (NumberFormatException e) {
                i = -1;
            }
            ret[j++] = i;
        }
        return ret;
    }

    public String[] getValuesFromPreferences(String... tags) {
        if(tags == null) {
            return new String[0];
        }
        String[] contents = new String[tags.length];
        int i = 0;
        for(String tag : tags) {
            contents[i++] =  prefs.getString("de.uni_ulm.ismm.stolperpfad." + tag, "");
        }
        return contents;
    }
}
