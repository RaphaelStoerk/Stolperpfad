package de.uni_ulm.ismm.stolperpfad.general;

import android.app.AlertDialog;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;

import com.androidquery.AQuery;

import de.uni_ulm.ismm.stolperpfad.R;

public abstract class StolperpfadeAppActivity extends AppCompatActivity {

    // The AQuery framework lets us write short understandable code, see further down
    protected AQuery aq;

    protected MyButtonClickListener<StolperpfadeAppActivity> myClickListener;

    public void showQuickAccesMenu() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // set the contents of the dialog
        builder.setTitle("Soll das Hauptmenu hierher ziehen?");
        builder.setPositiveButton("Ja", (dialogInterface, i) -> aq.id(R.id.quick_access_button).backgroundColor(Color.RED));
        builder.setNegativeButton("Nein", (dialogInterface, i) -> aq.id(R.id.quick_access_button).backgroundColor(Color.argb(0,0,0,0)));

        // Create the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
