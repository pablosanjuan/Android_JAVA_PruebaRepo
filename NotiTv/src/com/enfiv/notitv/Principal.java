package com.enfiv.notitv;

import http.Httppostaux;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Principal extends ActionBarActivity implements OnItemClickListener{

	private ListView lista_titu;
	private TextView nombre_presona;
	private SharedPreferences prefs;
	String cambios,usuario;
	private TextView fecha;
	String [] titulares;
	Httppostaux post;
	ProgressDialog pDialog;
	boolean result_back;
	String IP_Server = "192.168.1.105";
	String URL_connect = "http://"+IP_Server+"/ProyectoEnfasisIV/buscarenlaces.php";
	    
	
	@SuppressLint({ "InlinedApi", "SimpleDateFormat" })
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.principal);
        	post = new Httppostaux();
			prefs = getSharedPreferences("MisPreferencias",Context.MODE_PRIVATE);
			nombre_presona = (TextView) findViewById(R.id.nombre_persona);
			fecha = (TextView) findViewById(R.id.fecha);
	        lista_titu = (ListView) findViewById(R.id.lv_titulares);
	        lista_titu.setOnItemClickListener(this);
//-----------------------------inicio servicio------------------------------------

			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.SECOND, 10);
			Intent intent = new Intent(this, MyAlarmService.class);
			PendingIntent pintent = PendingIntent.getService(this, 0, intent, 0);
			AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
			int j;
			j=15;
			alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),j* 1000, pintent);
			startService(intent);
////-----------------------------final servicio------------------------------------
	        nombre_presona.setText(prefs.getString("nombre", ""));
	        String titul = prefs.getString("titular", "");
	        
	        Calendar calendario = new GregorianCalendar();
	        Date date = calendario.getTime();
	        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
	        String formatteDate = df.format(date);
	        fecha.setText("Los Titulares para hoy "+formatteDate+" son:");
	        
			titulares = titul.split(",");
			for(int i=0; i<titulares.length; i++){
	        ArrayAdapter<String> adaptador = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, titulares);
	        lista_titu.setAdapter(adaptador);
			}
	}
	
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		String dato = titulares[position];
		new asynclogin().execute(dato);
	}
//-------------------------------------------------------------------------------------------------------------------------------
	public boolean loginstatus(String dato) {
    	String enlac,encabe;
    	ArrayList<NameValuePair> postparameters2send= new ArrayList<NameValuePair>();
		    		postparameters2send.add(new BasicNameValuePair("titular",dato));
	    		JSONArray jdata = post.getserverdata(postparameters2send, URL_connect);
		    		if (jdata!=null && jdata.length() > 0){
		    		JSONObject json_data;
					try {
						 json_data = jdata.getJSONObject(0);
						 enlac=json_data.getString("urls");
						 encabe=json_data.getString("encabezados");
						 SharedPreferences.Editor editor = prefs.edit();
							editor.putString("enlaces", enlac );
							editor.putString("encabezado", encabe );
							editor.commit();
					} catch (JSONException e) {
						e.printStackTrace();
						}
		    		}
		    		return true;
	}
	class asynclogin extends AsyncTask< String, String, String > {
    	String dato;
        protected void onPreExecute() {
        	pDialog = new ProgressDialog(Principal.this);
            pDialog.setMessage("Buscando Enlaces....");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }
		protected String doInBackground(String... params) {
			dato=params[0];
    		if (loginstatus(dato)==true){  
    			return "ok"; 
    		}else{    		
    			return "err"; 
    		}
		}
        protected void onPostExecute(String result) {
           pDialog.dismiss();
           Log.e("onPostExecute=",""+result);
           if (result.equals("ok")){
				exito();
            }else{
            	error();
            }
        }
    }
//-------------------------------------------------------------------------------------------------------------------------------
	private void exito() {
		Intent i = new Intent(Principal.this, Desplegue.class);
		startActivity(i);
	}
	private void error() {
		Toast.makeText(this, "chao", Toast.LENGTH_SHORT).show();
	}
//---------------------------------controlador del boton menu----------------------
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.btn_cerrarseion:
			SharedPreferences.Editor editor = prefs.edit();
			editor.putString("nombre", "" );
			editor.putString("cambios", "" );
			editor.putString("usuario", "" );
			editor.commit();
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.SECOND,0);
			Intent intent = new Intent(this, MyAlarmService.class);
			stopService(intent);
			Intent ir_principal = new Intent().setClass(this, Identificacion.class);
			startActivity(ir_principal);
	        finish();
			break;
		}
		return super.onOptionsItemSelected(item);
	}
//---------------------------------contorlador del boton atras---------------------	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		  if (keyCode == KeyEvent.KEYCODE_BACK) {
		    new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert).setTitle("Salir")
		      .setMessage("Estás seguro?").setNegativeButton(android.R.string.cancel, null)//sin listener
		      .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {//un listener que al pulsar, cierre la aplicacion
		        @Override
		        public void onClick(DialogInterface dialog, int which){
		        	Principal.this.finish();
		        }
		      }).show();
		    return true;
		  }
		  return super.onKeyDown(keyCode, event);
		}
}