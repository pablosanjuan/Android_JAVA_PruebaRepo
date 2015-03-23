package com.example.tada;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class HistorialVoz extends ActionBarActivity implements OnClickListener, OnItemClickListener, OnItemLongClickListener {

	String ruta;
	private static final String LOG_TAG = "Grabadora";
	private MediaPlayer mediaPlayer;
	Button btn_reproducir;
	String [] datoslista;
	ListView lv;
	TextView tvoz, tnvoz, tuvoz;
	File aud[];
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.historial_voz);
		btn_reproducir = (Button) findViewById(R.id.btnreproducir);
		tvoz = (TextView) findViewById(R.id.txt_voz_m);
		tnvoz = (TextView) findViewById(R.id.txt_nota_voz);
		tuvoz = (TextView) findViewById(R.id.txt_url_notz_voz);
		lv = (ListView) findViewById(R.id.lista_nota_texto1);
		lv.setOnItemClickListener(this);
		lv.setOnItemLongClickListener(this);
		datoslista = getResources().getStringArray(R.array.llenar);
		btn_reproducir.setOnClickListener(this);
		
		ruta = Environment.getExternalStorageDirectory().toString();
		File file = new File(ruta+"/TaDa");
		File audios[] = file.listFiles();
		aud = audios;
		ArrayList<String> nombres = new ArrayList<String>();
		for(File audio: audios){
			nombres.add(audio.getName());
		}
		
		for(int i=0; i<audios.length; i++){
		  ArrayAdapter<String> adaptador = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, nombres);
		lv.setAdapter(adaptador);
		}	
	}

	@Override
	public void onClick(View v) {
		ruta = tuvoz.getText().toString();
		mediaPlayer = new MediaPlayer();
	    try {
	        mediaPlayer.setDataSource(ruta);
	        mediaPlayer.prepare();
	        mediaPlayer.start();
	    } catch (IOException e) {
	        Log.e(LOG_TAG, "Fallo en reproducción");
	    }
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		
		return false;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		tnvoz.setText(aud[position].getName().toString());
		tuvoz.setText(aud[position].getPath().toString());
	}
}
