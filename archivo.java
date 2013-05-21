package com.example.recordatoriointeligente;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.view.Menu;
import android.widget.Toast;

public class MainActivity extends Activity {
    
    @Override
        protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        new Thread(new Runnable() {
                public void run() {
                    System.out.println("Hilo");
                }
            }).start();
        
        String FILENAME = "prueba";
        String string = "hola mundo";
        String troll = "troll";
        String brinco = "\n";
        FileOutputStream fos = null;
        
        try {
            fos = openFileOutput(FILENAME, Context.MODE_APPEND);
            for(int i = 0 ; i < 10; i++){
                
                fos.write(string.getBytes());
                fos.write(brinco.getBytes());
                
            }
            fos.write(troll.getBytes());
            fos.close();
        } catch (FileNotFoundException e) {
            
            e.printStackTrace();
            
        } catch (IOException e) {
            
            e.printStackTrace();
        }
        
        string  = "Hola";
        try{
            BufferedReader archivo = new BufferedReader(new InputStreamReader(openFileInput(FILENAME)));
            ArrayList<String> arrayList = new ArrayList<String>();
            while( (string = archivo.readLine()) != null ){
                arrayList.add(string);
            }
            for(String value : arrayList){
                System.out.println("Leyendo de archivo: "+value);
            }
            
            if(arrayList.contains(troll)){
                alert("Si lo contiene");
            }
            
        } catch(FileNotFoundException e){
            
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    private void alert(String string) {
        // TODO Auto-generated method stub
        Context context = getApplicationContext();
        CharSequence text = string;
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    @Override
        public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

}
