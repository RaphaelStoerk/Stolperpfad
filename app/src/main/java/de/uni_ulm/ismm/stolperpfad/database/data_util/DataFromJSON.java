package de.uni_ulm.ismm.stolperpfad.database.data_util;

import android.content.Context;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import de.uni_ulm.ismm.stolperpfad.StolperpfadeApplication;

/**
 * A helper utility class that organizes the saving and loading of json files in the assets and
 * external storage
 */
public class DataFromJSON {

    /**
     * Reads in all json files in a given directory and returns an Arraylist of those
     * JSONObjects
     *
     * @param context the application context
     * @param directory_name the directory with all the json files
     * @return a list of JSONObjects
     */
    public static ArrayList<JSONObject> loadAllJSONFromDirectory(Context context, String directory_name) {
        ArrayList<JSONObject> ret = new ArrayList<>();
        try {
            for (String file : Objects.requireNonNull(context.getAssets().list(directory_name))) {
                JSONObject person = loadJSONFromAssets(context, directory_name + "/" + file);
                ret.add(person);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ret;
    }

    /**
     * This methods reads in a specific json file from the assets folder
     *
     * @param context the application context
     * @param filename the concerned file name
     * @return the json object read from that file
     */
    private static JSONObject loadJSONFromAssets(Context context, String filename) {
        String json ;
        JSONObject person_as_json;
        try {
            // see folder main/assets for json files
            // filename is json file, i.e. "person.json"
            InputStream is = context.getAssets().open(filename);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, StandardCharsets.UTF_8);
            person_as_json = new JSONObject(json);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        } catch (JSONException e) {
            return null;
        }
        return person_as_json;
    }

    /**
     * Reads in all json files in a given directory and returns an Array-List of those
     * JSONObjects
     *
     * @param directory the directory with all the json files
     * @return a list of JSONObjects
     */
    public static ArrayList<JSONObject> loadAllJSONFromExternalDirectory(String directory) {
        ArrayList<JSONObject> ret = new ArrayList<>();
        try {
            File dir = new File(StolperpfadeApplication.DATA_FILES_PATH, directory);
            if(!dir.exists()) {
                dir.mkdirs();
            }
            for (File file : dir.listFiles()) {
                JSONObject person = loadJSONFromExternalStorage(file);
                ret.add(person);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    /**
     * This methods reads in a specific json file from the external storage
     *
     * @param file the concerned file name
     * @return the json object read from that file
     */
    private static JSONObject loadJSONFromExternalStorage(File file) {
        String json;
        JSONObject pers;
        try {
            if(StolperpfadeApplication.getInstance().fileTreeIsNotReady()) {
                StolperpfadeApplication.getInstance().setupFileTree();
            }
            // see folder main/assets for json files
            // filename is json file, i.e. "person.json"
            InputStream is = new FileInputStream(file);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, StandardCharsets.UTF_8);
            pers = new JSONObject(json);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        } catch (JSONException e) {
            return null;
        }
        return pers;
    }

    /**
     * Writes a json file to the specified file
     *
     * @param json the json object to write
     * @param filename the file name to write into
     * @return true, if the writing was succesful
     */
    public static boolean saveJsonTo(JSONObject json, String filename) {
        try {
            if(filename == null || filename.length() == 0) {
                filename = createPathFileName();
            }
            json.put("name", filename);
            Writer output;
            File file = new File(StolperpfadeApplication.DATA_FILES_PATH + "/paths", filename + ".json");
            output = new BufferedWriter(new FileWriter(file));
            output.write(json.toString());
            output.close();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * Creates a default file name for a route file if none was specified
     *
     * @return an individual file name
     */
    private static String createPathFileName() {
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd_MM_yyyy", Locale.GERMAN);
        String formatted = df.format(c);
        long time = System.currentTimeMillis();
        String time_hex = Long.toHexString(time);
        return "Stolperpfad_" + formatted + "_" + time_hex;
    }

    /**
     * Deletes a file from a given directory
     *
     * @param directory the directory containing the file
     * @param name the name of the file
     * @return true, if the file has been found and deleted
     */
    public static boolean deleteFileFromExternalStorage(String directory, String name) {
        try {
            File dir = new File(StolperpfadeApplication.DATA_FILES_PATH, directory);
            File to_delete = new File(dir.getAbsolutePath(), name + ".json");
            to_delete.delete();
            return true;
        } catch(Exception exc) {
            return false;
        }
    }
}
