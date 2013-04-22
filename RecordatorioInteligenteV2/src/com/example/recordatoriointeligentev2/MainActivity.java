package com.example.recordatoriointeligentev2;

import java.util.ArrayList;
import java.util.Set;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.bluetooth.*;
import android.content.Context;
import android.content.Intent;
import android.app.Activity;
import android.app.AlertDialog;
import android.view.Menu;

class Corredor implements Runnable {
	private BTTask task;
	public Corredor(BTTask t) {
		this.task = t;
	}
	public void run() {
		this.task.execute();
	}
	
}

public class MainActivity extends Activity {
	private static final int REQUEST_ENABLE_BT = 1;
	private Handler mHandler;
    private int retraso;
	
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		verifyAvaibilityOfBluetooth();
		Corredor corr = new Corredor(new BTTask(this));
		this.mHandler = new Handler(Looper.getMainLooper());
		this.retraso = 5000;
		this.mHandler.postDelayed(corr, this.retraso);
		
	}
    
    public void verifyAvaibilityOfBluetooth(){
    	BluetoothAdapter b = BluetoothAdapter.getDefaultAdapter();
		startActivityForResult(new Intent(b.getDefaultAdapter().ACTION_REQUEST_ENABLE), REQUEST_ENABLE_BT);	
    }
    
    public void addTask(){
    	Corredor corr = new Corredor(new BTTask(this));
		this.mHandler.postDelayed(corr, this.retraso);
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}


class BTTask extends AsyncTask< Void, Void, ArrayList<String>>{
	private MainActivity a;
	
	public BTTask(MainActivity activity){
		super();
		this.a = activity;
	}
	
	protected ArrayList<String> doInBackground(Void ...params) {
		BluetoothAdapter bluetooth = BluetoothAdapter.getDefaultAdapter();
		ArrayList<String> listaDeDispositivos = null;
		if(bluetooth == null) return null;
		
		while( bluetooth.getState() != 12 ){
			
			System.out.println("Esperando pro el estado prendido.");
			
		}
		
		bluetooth.startDiscovery();
		
		Set<BluetoothDevice> dispositivos = bluetooth.getBondedDevices();
		
		listaDeDispositivos = new ArrayList<String>();
		
		if( dispositivos.size() > 0 ){
			
			for( BluetoothDevice dispositivo : dispositivos ){
				
				listaDeDispositivos.add(dispositivo.getName()+","+dispositivo.getAddress());
				System.out.println("Nombre: "+dispositivo.getName()+" MAC Address: "+dispositivo.getAddress());
			}
			
		}
		
		bluetooth.cancelDiscovery();
		return listaDeDispositivos;
	}

	@Override
	protected void onPostExecute(ArrayList<String> listaDeDispositivos){
		for(String s : listaDeDispositivos){
			System.out.println(s);
		}
		this.a.addTask();
	}
	
}
