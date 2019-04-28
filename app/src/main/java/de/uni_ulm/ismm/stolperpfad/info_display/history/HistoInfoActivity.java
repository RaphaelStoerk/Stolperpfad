package de.uni_ulm.ismm.stolperpfad.info_display.history;

import android.os.Bundle;
import android.widget.TextView;

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

    }

    public void setContentText(String name, String explanation){
        TextView textView = findViewById(R.id.title_histo_info);
        textView.setText(name);
        textView = findViewById(R.id.histo_info_explanation);
        textView.setText(explanation);
    }

}
