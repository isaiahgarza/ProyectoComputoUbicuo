package com.example.androidtablayout;

import android.app.Activity;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.Toast;
import android.view.View;

public class ConfActivity extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.conf_layout);
    }
    
    public void onRadioButtonClick(View v){
    	RadioButton button = (RadioButton) v;
    	Toast.makeText(ConfActivity.this, button.getText() + " was chosen.", Toast.LENGTH_SHORT).show();
    }
}
