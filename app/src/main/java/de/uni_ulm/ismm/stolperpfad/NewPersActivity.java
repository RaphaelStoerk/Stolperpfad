/**
 * we won't use this, it is just here because without it the persons are not displayed
 */

package de.uni_ulm.ismm.stolperpfad;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class NewPersActivity extends AppCompatActivity {

    public static final String EXTRA_REPLY = "com.example.android.wordlistsql.REPLY";

    private EditText mEditPersonView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_person);
        mEditPersonView = findViewById(R.id.edit_person);

        final Button button = findViewById(R.id.button_save);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent replyIntent = new Intent();
                if(TextUtils.isEmpty(mEditPersonView.getText())) {
                    setResult(RESULT_CANCELED, replyIntent);
                } else{
                    String person = mEditPersonView.getText().toString();
                    replyIntent.putExtra(EXTRA_REPLY, person);
                    setResult(RESULT_OK, replyIntent);
                }
                finish();
            }
        });
    }
}
