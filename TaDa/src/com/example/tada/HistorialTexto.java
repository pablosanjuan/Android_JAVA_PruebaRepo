package com.example.tada;

import java.util.ArrayList;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class HistorialTexto extends ListActivity implements OnClickListener, OnItemClickListener{

	String[] arr;
	TextView thora,tfecha,tlatitud,tlongitud,tnota,tcategoria;
	ListView lv;
	Button btn_mapa;
	String variable_hora = "hora";
	String variable_fecha = "fecha";
	String variable_latitud = "latitud";
	String variable_longitud= "longitud";
	String variable_nota = "nota";
	String variable_categoria = "categoria";
	private Context context;
	String IP_Server = "192.168.43.152";
	String URL_connect = "http://"+IP_Server+"/TaDa/extrae.php";
	ArrayList<HashMap<String, String>> jsonlist = new ArrayList<HashMap<String,String>>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.historial_texto);
		
		btn_mapa = (Button) findViewById(R.id.bmapa);
		thora = (TextView) findViewById(R.id.txt_hora_m);
		tfecha = (TextView) findViewById(R.id.txt_fecha_m);
		tlatitud = (TextView) findViewById(R.id.txt_latitud_m);
		tlongitud = (TextView) findViewById(R.id.txt_longitud_m);
		tnota = (TextView) findViewById(R.id.txt_nota_m);
		tcategoria = (TextView) findViewById(R.id.txt_categoria_m);
		lv = (ListView) findViewById(android.R.id.list);
		lv.setOnItemClickListener(this);
		btn_mapa.setOnClickListener(this);
		
		new GetJSONActivity(HistorialTexto.this).execute();
	}
	
	private class GetJSONActivity extends AsyncTask<String, Void, String>{
		@SuppressWarnings("unused")
		private ListActivity activity;
		
		public GetJSONActivity(ListActivity activity){
			this.activity = activity;
			context = activity;
		}
		
		protected String doInBackground(final String... args){
			JSONParser jparser = new JSONParser();
			JSONArray json = jparser.GETJSONfromUrl(URL_connect);
			
			for(int i=0 ; i<json.length(); i++){
			try{
				JSONObject c  = json.getJSONObject(i);
				String val_hora = c.getString(variable_hora);
				String val_fecha = c.getString(variable_fecha);
				String val_latitud = c.getString(variable_latitud);
				String val_longitud = c.getString(variable_longitud);
				String val_nota = c.getString(variable_nota);
				String val_categoria = c.getString(variable_categoria);
				
				HashMap<String, String> map = new HashMap<String, String>();
				
				map.put(variable_hora, val_hora);
				map.put(variable_fecha, val_fecha);
				map.put(variable_latitud, val_latitud);
				map.put(variable_longitud, val_longitud);
				map.put(variable_nota, val_nota);
				map.put(variable_categoria, val_categoria);
				jsonlist.add(map);
			}catch(JSONException e){
				e.printStackTrace();
				return "Error al crear variables";
			}
		  }
			return "Exito creando variables";
		}
		protected void onPostExecute(String success){
			String[] datos = {"hora","fecha","latitud","longitud","nota","categoria"};
			ListAdapter adapter = new SimpleAdapter(context, jsonlist, R.layout.lista, datos , new int[]{R.id.val_hora,R.id.val_fecha,R.id.val_latitud,R.id.val_longitud,R.id.val_nota,R.id.val_categoria});
			setListAdapter(adapter);
			getListView();
		}
	}
	public void onItemClick(AdapterView<?> parent , View view, int position,long id) {
		thora.setText(jsonlist.get(position).get("hora"));
		tfecha.setText(jsonlist.get(position).get("fecha"));
		tlatitud.setText(jsonlist.get(position).get("latitud"));
		tlongitud.setText(jsonlist.get(position).get("longitud"));
		tnota.setText(jsonlist.get(position).get("nota"));
		tcategoria.setText(jsonlist.get(position).get("categoria"));
	}
	@Override
	public void onClick(View v){
		String lat_str = tlatitud.getText().toString();
		String lon_str = tlongitud.getText().toString();
    	String url_coor = "https://www.google.com/maps/@"+lat_str+","+lon_str+",20z";
    	Intent i_coor = new Intent(Intent.ACTION_VIEW);
    	i_coor.setData(Uri.parse(url_coor));
    	lat_str = "";
    	lon_str = "";
    	startActivity(i_coor);
	}
}