package de.uni_ulm.ismm.stolperpfad.info_display.history;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.util.List;

import de.uni_ulm.ismm.stolperpfad.R;
import de.uni_ulm.ismm.stolperpfad.general.StolperpfadeAppActivity;
import de.uni_ulm.ismm.stolperpfad.info_display.history.model.HistoInfoViewModel;

public class HistoInfoActivity extends StolperpfadeAppActivity {

    HistoInfoViewModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initializeGeneralControls(R.layout.activity_histo_info_page);
        model = HistoInfoViewModel.getInstance(this);
        String current = getIntent().getStringExtra("termName");
        model.requestExplanation(current, this);
        model.requestConcPers(current, this);
        Log.i("LOG_REQUEST_CONC_PERS", "started");

    }

    //set name and explanation
    public void setPrimaryContentText(String name, String explanation){
        TextView textView = findViewById(R.id.title_histo_info);
        textView.setText(name);
        if(explanation == null || explanation.equals("")){

        }else {
            textView = findViewById(R.id.histo_info_explanation);
            textView.setText(explanation);
        }
    }

    //set concerned persons
    public void setSecondaryContentText(String persons){
        TextView textView = findViewById(R.id.histo_info_concerned_persons);
        textView.setText(persons);
    }

}
