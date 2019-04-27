package de.uni_ulm.ismm.stolperpfad.database.data_util;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import de.uni_ulm.ismm.stolperpfad.StolperpfadeApplication;

public class DataFromJSON {

    /**
     * Reads in all json files in a given diretory and returns an Arraylist of those
     * JSONObjets
     *
     * @param context the application context
     * @param dirName the directory with all the json files
     * @return a list of JSONObjects
     */
    public static ArrayList<JSONObject> loadAllJSONFromDirectory(Context context, String dirName) {
        ArrayList<JSONObject> ret = new ArrayList<>();
        try {
            for (String file : context.getAssets().list(dirName)) {
                Log.i("MY_JSON_TAG", file);
                JSONObject person = loadJSONFromAssets(context, dirName + "/" + file);
                ret.add(person);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ret;
    }

    private static JSONObject loadJSONFromAssets(Context context, String filename) {
        String json = "";
        JSONObject pers = null;
        try {

            // see folder main/assets for json files
            // filename is json file, i.e. "person.json"
            InputStream is = context.getAssets().open(filename);

            int size = is.available();

            byte[] buffer = new byte[size];

            is.read(buffer);

            is.close();

            json = new String(buffer, "UTF-8");

            pers = new JSONObject(json);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        } catch (JSONException e) {
            Log.i("MY_JSON_TAG", filename);
        }
        return pers;
    }

    public static boolean saveJsonTo(JSONObject json, String filename) {
        try {
            if(filename == null || filename.length() == 0) {
                filename = createPathFileName();
            }
            json.put("name", filename);
            Writer output = null;
            File file = new File(StolperpfadeApplication.DATA_FILES_PATH + "/paths", filename + ".json");
            output = new BufferedWriter(new FileWriter(file));
            output.write(json.toString());
            output.close();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private static String createPathFileName() {
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd_MM_yyyy");
        String formatted = df.format(c);
        long time = System.currentTimeMillis();
        String time_hex = Long.toHexString(time);
        return "Stolperpfad_" + formatted + "_" + time_hex;
    }

    public static ArrayList<JSONObject> loadAllJSONFromExternalDirectory(Context context, String directory) {
        ArrayList<JSONObject> ret = new ArrayList<>();
        try {
            File dir = new File(StolperpfadeApplication.DATA_FILES_PATH, directory);
            if(!dir.exists()) {
                dir.mkdirs();
            }
            for (File file : dir.listFiles()) {
                Log.i("MY_JSON_TAG", file.getAbsolutePath());
                JSONObject person = loadJSONFromExternalStorage(context, file);
                ret.add(person);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    private static JSONObject loadJSONFromExternalStorage(Context context, File file) {
        String json = "";
        JSONObject pers = null;
        try {
            if(!StolperpfadeApplication.getInstance().fileTreeIsReady()) {
                // TODO: inform the user that something is wrong
                StolperpfadeApplication.getInstance().setupFileTree();
            }

            // see folder main/assets for json files
            // filename is json file, i.e. "person.json"
            InputStream is = new FileInputStream(file);

            int size = is.available();

            byte[] buffer = new byte[size];

            is.read(buffer);

            is.close();

            json = new String(buffer, "UTF-8");

            pers = new JSONObject(json);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        } catch (JSONException e) {
            Log.i("MY_JSON_TAG", file.getAbsolutePath());
        }
        return pers;

    }
}
