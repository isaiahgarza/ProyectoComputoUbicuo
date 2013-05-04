package com.example.pruebabt;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Locale;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.app.Activity;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.view.Menu;
import android.view.View.OnClickListener;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

class Corredor implements Runnable {
	private BTTask task;
	public Corredor(BTTask t) {
		this.task = t;
	}
	public void run() {
		this.task.execute();
	}
	
}


class BTTask extends AsyncTask< Void, Void, Void>{
	private MainActivity a;
	
	public BTTask(MainActivity activity){
		super();
		this.a = activity;
	}
	@Override
	protected Void doInBackground(Void ...params) {
		BluetoothAdapter bluetooth = BluetoothAdapter.getDefaultAdapter();
		if(bluetooth == null) return null;
		
		while( bluetooth.getState() != 12 ){
			
			System.out.println("Esperando pro el estado prendido.");
			
		}
		
		bluetooth.startDiscovery();
		try{
			Message msg = new Message();
			msg.obj = "clear";
			MainActivity.mHandler.sendMessage(msg);
		}catch(Exception e){
			System.err.println("Something is wrong. :"+e);
		}
		this.a.intentScan();
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		bluetooth.cancelDiscovery();
		return null;
	}
	
	@Override
	protected void onPostExecute(Void parametros){
		this.a.addTask();
	}
	
}

public class MainActivity extends Activity implements OnInitListener {

private static final int REQUEST_ENABLE_BT = 1;
	
	Button btnScanDevice;
	TextView stateBluetooth;
	EditText retrasoField;
	Button retrasoSave;
	TextToSpeech TTS;
	
	BluetoothAdapter bluetoothAdapter;
	
	static Handler mHandler;
	private static final String FILENAME = "devices.txt";
	private static final String DELAY = "retraso";
    private ListView listDevicesFound;
    private int retraso;
	private ArrayAdapter<String> btArrayAdapter;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TTS = new TextToSpeech(this, this);
        btnScanDevice = (Button)findViewById(R.id.scandevice);
        retrasoSave = (Button)findViewById(R.id.save);
        retrasoField = (EditText)findViewById(R.id.retraso);
        loadRetraso();
        stateBluetooth = (TextView)findViewById(R.id.bluetoothstate);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        
        listDevicesFound = (ListView)findViewById(R.id.devicesfound);
        btArrayAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1);
        listDevicesFound.setAdapter(btArrayAdapter);
        CheckBlueToothState();
        Corredor corr = new Corredor(new BTTask(this));
		MainActivity.mHandler = new Handler(Looper.getMainLooper()) {
			@Override
    		public void handleMessage(Message msg){
	    		if(msg.obj == "clear"){
	    			System.out.println("Supuestamente se limpio la tabla");
	    			btArrayAdapter.clear();
	    		}	
	    		super.handleMessage(msg);
    		}
		};
		this.retraso = 3000;
		MainActivity.mHandler.postDelayed(corr, this.retraso);
        
        btnScanDevice.setOnClickListener(btnScanDeviceOnClickListener);
        retrasoSave.setOnClickListener(retrasoSaveOnClickListener);
       
        listDevicesFound.setOnItemClickListener( new OnItemClickListener(){
        	public void onItemClick(AdapterView<?> parent, View view, int position, long id){
        		Toast.makeText(getApplicationContext(), ((TextView) view).getText(),
        		          Toast.LENGTH_SHORT).show();
        		textToSpeech((String) ((TextView) view).getText());
        	}
        });
    }
    
    public void addTask(){
    	Corredor corr = new Corredor(new BTTask(this));
    	MainActivity.mHandler.postDelayed(corr, this.retraso);
    }
    
    public void listClear(){
    	
    	
    }
    
    public void intentScan(){
    	registerReceiver(ActionFoundReceiver, 
        		new IntentFilter(BluetoothDevice.ACTION_FOUND));
    }
    
    public void textToSpeech(String device){
    	String textToSay = "Haz perdido este dispositivo: "+device+", joder!! ostia tio!";
    	TTS.speak(textToSay, TextToSpeech.QUEUE_FLUSH, null);
    }
    
    public void saveDeviceInFavorites(String device){
    	String brinco = "\n";
		FileOutputStream fos = null;
		try {
			fos = openFileOutput(FILENAME, Context.MODE_APPEND);
			fos.write(device.getBytes());
			fos.write(brinco.getBytes());
			fos.close();
		} catch (FileNotFoundException e) {
			
			e.printStackTrace();
			
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		alert("Se ha guardado en favoritos "+device);
    }
    
    public void verifyDeviceInFavorites(ArrayList<String> devices){
    	String archivoString = null;
    	try{
			BufferedReader archivo = new BufferedReader(new InputStreamReader(openFileInput(FILENAME)));
			ArrayList<String> favorites = new ArrayList<String>();
			while( (archivoString = archivo.readLine()) != null ){
				favorites.add(archivoString);
			}
			for(String value : favorites){
				System.out.println("Leyendo de archivo: "+value);
			}
			for(String device : devices){
				System.out.println("Leyendo de archivo: "+device);
				if(!favorites.contains(device)) alarm(device);
			}
			if(favorites.contains(devices)){
				alert("Si lo contiene");
			}
			
		} catch(FileNotFoundException e){
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public void alarm(String device){
		alert("Se te ha perdido el dispositivo: "+device);
		textToSpeech(device);
    }
    
    @Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(ActionFoundReceiver);
		TTS.shutdown();
	}

	private void CheckBlueToothState(){
    	if (bluetoothAdapter == null){
        	stateBluetooth.setText("Bluetooth es soportado");
        }else{
        	if (bluetoothAdapter.isEnabled()){
        		if(bluetoothAdapter.isDiscovering()){
        			stateBluetooth.setText("Bluetooth is currently in device discovery process.");
        		}else{
        			stateBluetooth.setText("Bluetooth is Enabled.");
        			btnScanDevice.setEnabled(true);
        		}
        	}else{
        		stateBluetooth.setText("Bluetooth is NOT Enabled!");
        		Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        	    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        	}
        }
    }
    
    private Button.OnClickListener btnScanDeviceOnClickListener
    = new Button.OnClickListener(){

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			btArrayAdapter.clear();
			bluetoothAdapter.startDiscovery();
		}};

	private Button.OnClickListener retrasoSaveOnClickListener = new Button.OnClickListener(){
		@Override
		public void onClick(View arg0){
			saveRetraso(DELAY,Integer.valueOf(retrasoField.getText().toString()));
		}
	};
		
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if(requestCode == REQUEST_ENABLE_BT){
			CheckBlueToothState();
		}
	}
    
	private void saveRetraso(String key, int value){
		
		SharedPreferences sp = getPreferences(MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putInt(key, value);
		editor.commit();
		alert("Retraso guardado.");
		
	}
	
	private void alert(String string) {
		// TODO Auto-generated method stub
		Context context = getApplicationContext();
		CharSequence text = string;
		int duration = Toast.LENGTH_SHORT;

		Toast toast = Toast.makeText(context, text, duration);
		toast.show();
	}
	
	private void loadRetraso(){
		SharedPreferences sp = getPreferences(MODE_PRIVATE);
		this.retraso = sp.getInt(DELAY, 10000);
		System.out.println("Este es el retraso: "+this.retraso);
		retrasoField.setText(String.valueOf(this.retraso), TextView.BufferType.EDITABLE);
		
	}
	
	
	
	private final BroadcastReceiver ActionFoundReceiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			if(BluetoothDevice.ACTION_FOUND.equals(action)) {
	            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
	            btArrayAdapter.add(device.getName() + "\n" + device.getAddress());
	            System.out.println("device:"+device.getName()+" address: "+device.getAddress());
	            btArrayAdapter.notifyDataSetChanged();
	        }
		}};

	@Override
	public void onInit(int status) {
		// TODO Auto-generated method stub
		
	}
}
