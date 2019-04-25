package de.uni_ulm.ismm.stolperpfad.database.data_util;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;

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
}
