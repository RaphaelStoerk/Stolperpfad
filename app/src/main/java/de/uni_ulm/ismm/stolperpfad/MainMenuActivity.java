package de.uni_ulm.ismm.stolperpfad;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.androidquery.AQuery;

public class MainMenuActivity extends AppCompatActivity {

    private AQuery aq;
    private MyClickListener myListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);


        aq = new AQuery(this);
        myListener = new MyClickListener();

        aq.id(R.id.exit_button).visible().clicked(myListener);
        aq.id(R.id.info_button).visible().clicked(myListener);
    }

    class MyClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent;
            switch (v.getId()) {
                case R.id.exit_button:
                    finish();
                    System.exit(0);
                    break;
                case R.id.info_button:
                    intent = new Intent(MainMenuActivity.this, ScrollingInfoActivity.class);
                    startActivity(intent);
            }
        }
    }
}
