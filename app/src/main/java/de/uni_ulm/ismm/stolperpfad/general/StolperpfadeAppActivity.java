package de.uni_ulm.ismm.stolperpfad.general;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import com.androidquery.AQuery;

import de.uni_ulm.ismm.stolperpfad.R;

public abstract class StolperpfadeAppActivity extends AppCompatActivity {

    // The AQuery framework lets us write short understandable code, see further down
    protected AQuery aq;

    protected MyButtonClickListener<StolperpfadeAppActivity> myClickListener;

    public final float HEADER_TRANSLATION_Z = 8;

    public void showQuickAccesMenu() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // set the contents of the dialog
        builder.setTitle("Soll das Hauptmenu hierher ziehen?");
        builder.setPositiveButton("Ja", (dialogInterface, i) -> aq.id(R.id.quick_access_button).backgroundColor(Color.argb(0,255,0,0)));
        builder.setNegativeButton("Nein", (dialogInterface, i) -> aq.id(R.id.quick_access_button).backgroundColor(Color.argb(0,0,0,0)));

        // Create the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);

    }

    protected void initializeGeneralControls(@LayoutRes int currentLayout) {
        // Initialize important helper-Objects

        setContentView(currentLayout);

        aq = new AQuery(this);

        // Initialize the Listener for the clickable items
        myClickListener = new MyButtonClickListener<>();
        myClickListener.setMyActivity(this);

        // add the listener to the items
        aq.id(R.id.quick_access_button).visible().clicked(myClickListener);
        aq.id(R.id.header).getView().setTranslationZ(HEADER_TRANSLATION_Z);
    }
}
