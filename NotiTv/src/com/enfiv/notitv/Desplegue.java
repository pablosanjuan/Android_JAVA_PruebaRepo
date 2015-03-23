package com.enfiv.notitv;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class Desplegue extends ActionBarActivity implements OnItemClickListener {

	String [] titulares;
	ListView lv_despliegue;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.desplegue);
		SharedPreferences prefs = getSharedPreferences("MisPreferencias",Context.MODE_PRIVATE);
		lv_despliegue = (ListView) findViewById(R.id.lista_despliegue);
		lv_despliegue.setOnItemClickListener(this);
		
		String titul = prefs.getString("enlaces", "");
		String encabezados = prefs.getString("encabezado", "");
		titulares = titul.split("#");
		mostrar(encabezados);
	}
	public void mostrar(String encabezados) {
		String[] encab = encabezados.split("#");
	       for(int i=0; i<encab.length; i++){
	       ArrayAdapter<String> adaptador_encab = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, encab);
	       lv_despliegue.setAdapter(adaptador_encab);
	       }
	}
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		String dato = titulares[position];
		Intent i_web = new Intent(Intent.ACTION_VIEW);
    	i_web.setData(Uri.parse(dato));
    	startActivity(i_web);
	}
}