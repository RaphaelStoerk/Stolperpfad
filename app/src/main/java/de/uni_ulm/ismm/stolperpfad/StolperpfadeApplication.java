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

public class StolperpfadeApplication extends Application {

    private boolean dark_mode = false;
    private boolean first_call = true;
    private boolean file_tree_ready = false;
    private boolean ocr_language_file_ready = false;
    private boolean image_buffer_ready = false;
    private SharedPreferences prefs;
    private static StolperpfadeApplication instance;

    //private StolperpfadeRepository repo = new StolperpfadeRepository(this);


    public static final String DATA_FILES_PATH = Environment.getExternalStorageDirectory() + "/stolperpfade/data";

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

        prefs = this.getSharedPreferences(
                "de.uni_ulm.ismm.stolperpfad", Context.MODE_PRIVATE);

        if (!prefs.getBoolean("de.uni_ulm.ismm.stolperpfad.dark_mode", false)) {
            prefs.edit().putBoolean("de.uni_ulm.ismm.stolperpfad.dark_mode", false).apply();
        }
        dark_mode = prefs.getBoolean("de.uni_ulm.ismm.stolperpfad.dark_mode", false);

        prefs.edit().putBoolean("de.uni_ulm.ismm.stolperpfad.first_call", true).apply();
        first_call = prefs.getBoolean("de.uni_ulm.ismm.stolperpfad.first_call", false);

        prefs.edit().putBoolean("de.uni_ulm.ismm.stolperpfad.file_tree_ready", false).apply();
    }

    public boolean isDarkMode() {
        return dark_mode = prefs.getBoolean("de.uni_ulm.ismm.stolperpfad.dark_mode", false);
    }

    public void setDarkMode(boolean dark_mode) {
        prefs.edit().putBoolean("de.uni_ulm.ismm.stolperpfad.dark_mode", dark_mode).apply();
        this.dark_mode = prefs.getBoolean("de.uni_ulm.ismm.stolperpfad.dark_mode", false);
    }

    public boolean isFirstCall() {
        return first_call = prefs.getBoolean("de.uni_ulm.ismm.stolperpfad.first_call", false);
    }

    public void setFirstCall(boolean first_call) {
        prefs.edit().putBoolean("de.uni_ulm.ismm.stolperpfad.first_call", first_call).apply();
        this.first_call = prefs.getBoolean("de.uni_ulm.ismm.stolperpfad.first_call", false);
    }

    public boolean setupFileTree() {
        boolean file_tree = prefs.getBoolean("de.uni_ulm.ismm.stolperpfad.file_tree_ready", false);
        if (file_tree) {
            return true;
        }
        File tess = new File(DATA_FILES_PATH + "/tessdata");
        File img = new File(DATA_FILES_PATH + "/img");
        File routes = new File(DATA_FILES_PATH + "/paths");
        if(tess.mkdirs() || tess.exists()){
            if(img.mkdirs() || img.exists()) {
                if (routes.mkdirs() || routes.exists()) {
                    File lang_file = new File(tess, "deu.traineddata");
                    OutputStream out;
                    if (!lang_file.exists() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        try {
                            out = new FileOutputStream(lang_file);
                            InputStream in = getResources().openRawResource(R.raw.traineddata);
                            byte[] buffer = new byte[1024];
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
                    File image_buff = new File(img, "last_scanned_stone.jpg");
                    if (!image_buff.exists()) {
                        try {
                            image_buff.createNewFile();
                            image_buffer_ready = true;
                        } catch (IOException e) {
                            e.printStackTrace();
                            image_buffer_ready = false;
                        }
                    } else {
                        image_buffer_ready = true;
                    }
                    file_tree_ready = ocr_language_file_ready && image_buffer_ready;
                } else {
                    file_tree_ready = false;
                }
            }else {
                file_tree_ready = false;
            }
        } else {
            file_tree_ready = false;
        }
        prefs.edit().putBoolean("de.uni_ulm.ismm.stolperpfad.ocr_ready", ocr_language_file_ready).apply();
        prefs.edit().putBoolean("de.uni_ulm.ismm.stolperpfad.image_buffer_ready", image_buffer_ready).apply();
        prefs.edit().putBoolean("de.uni_ulm.ismm.stolperpfad.file_tree_ready", file_tree_ready).apply();
        return file_tree_ready;
    }

    public static StolperpfadeApplication getInstance() {
        return instance;
    }

    public boolean fileTreeIsReady() {
        return file_tree_ready = prefs.getBoolean("de.uni_ulm.ismm.stolperpfad.file_tree_ready", false);
    }


    public void setUpDatabase() {

    }
}
