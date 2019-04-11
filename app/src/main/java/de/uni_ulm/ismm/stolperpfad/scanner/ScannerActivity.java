package de.uni_ulm.ismm.stolperpfad.scanner;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.androidquery.AQuery;

import de.uni_ulm.ismm.stolperpfad.R;
import de.uni_ulm.ismm.stolperpfad.info_display.ScrollingInfoActivity;

public class ScannerActivity extends AppCompatActivity implements View.OnClickListener{

    AQuery aq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);
        aq = new AQuery(this);
        aq.id(R.id.scan_button).visible().clicked(this);
        aq.id(R.id.scan_to_info_button).visible().clicked(this);

        // TODO: build the Scanner

        // TODO: call the Camera

        // TODO: make images

        // TODO: process images

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.scan_button:
                // TODO: scan an image
                break;
            case R.id.scan_to_info_button:
                Intent intent = new Intent(ScannerActivity.this, ScrollingInfoActivity.class);
                startActivity(intent);
                break;
        }
    }
}
