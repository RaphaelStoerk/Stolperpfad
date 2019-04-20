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

    public static ArrayList<JSONObject> loadAllJSONFromDirectory(Context context, String dirName) {
        ArrayList<JSONObject> ret = new ArrayList<>();
        try {
            for (String file : context.getAssets().list(dirName)) {
                Log.i("MY_JSON_TAG", file);
                JSONObject person = getDataFromJSON(context, dirName + "/" + file);
                ret.add(person);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ret;
    }

    private static String loadJSONFromAsset(Context context, String filename) {
        String json = null;
        try {

            // see folder main/assets for json files
            // filename is json file, i.e. "person.json"
            InputStream is = context.getAssets().open(filename);

            int size = is.available();

            byte[] buffer = new byte[size];

            is.read(buffer);

            is.close();

            json = new String(buffer, "UTF-8");


        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;

    }

    public static JSONObject getDataFromJSON(Context ctx, String filename) {

        String in = loadJSONFromAsset(ctx, filename);
        JSONObject pers = null;
        try {
            // example call to the json object
            pers = new JSONObject(in);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return pers;
    }

}
