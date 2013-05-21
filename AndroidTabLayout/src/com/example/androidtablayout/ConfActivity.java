package com.example.androidtablayout;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;

public class ConfActivity extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.conf_layout);

        TextView textViewChange = (TextView) findViewById(R.id.ConfTab_title);
        textViewChange.setText(textViewChange.getText() + " " + getBTName());
        textViewChange = (TextView) findViewById(R.id.ConfTab_mac);
        textViewChange.setText(textViewChange.getText() + " " + getBTAddres());
    }
    
    public void onRadioButtonClick(View v){
    	RadioButton button = (RadioButton) v;
    	int buttonID = button.getId();
    	String chos = "";
    	switch(buttonID){
    	case R.id.radio_voice:
    		chos = "VOICE";
    		break;
    	case R.id.radio_tone:
    		chos = "TONE";
    		break;
    	case R.id.radio_song:
    		chos = "SONG";
    		break;
    	case R.id.radio_vibr:
    		chos = "VIBRATION";
    		break;
    	default:
    		chos = "ERR";
    		break;
    	}
    	
    	//Toast.makeText(ConfActivity.this, button.getText() + " was chosen.", Toast.LENGTH_SHORT).show();
    	Toast.makeText(ConfActivity.this, chos + " saved to configuration file", Toast.LENGTH_SHORT).show();
    }
    
    public String getBTName(){
    	android.bluetooth.BluetoothAdapter BTadapter = null;
    	if(BTadapter == null){
    		BTadapter = BluetoothAdapter.getDefaultAdapter();
    	}
    	String name = BTadapter.getName();
    	return name;
    }
    
    public String getBTAddres(){
    	BluetoothAdapter BTadapter = BluetoothAdapter.getDefaultAdapter();
    	System.out.println(BTadapter.getAddress());
    	return BTadapter.getAddress();
    }
    
    public void saveAlertPreference(){;}
}