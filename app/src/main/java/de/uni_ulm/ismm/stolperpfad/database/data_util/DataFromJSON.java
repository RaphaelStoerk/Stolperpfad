package de.uni_ulm.ismm.stolperpfad.database.data_util;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

public class DataFromJSON {

    public static String loadJSONFromAsset(Context context, String filename) {
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

    public static String getDataFromJSON(Context ctx, String filename) {

        String in = loadJSONFromAsset(ctx, filename);

        Log.i("Test","got that file" + in);

        String out = "";
        try {

            // example call to the json object
            JSONObject pers = new JSONObject(in);

            String name = pers.getJSONObject("maria").getString("name");

            out += name;

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.i("Test","got that name: " + out);
        return out;
    }

}
