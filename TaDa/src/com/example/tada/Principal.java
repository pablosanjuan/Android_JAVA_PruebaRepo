package com.example.tada;

import java.io.IOException;
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
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.http.Httppostaux;


public class Principal extends ActionBarActivity implements OnClickListener{

	Button btn_record, btn_stop, btn_play, btn_guarda, btn_hist_texto, btn_hist_voz;
	EditText nota_texto;
	TextView informacion;
	private static final String LOG_TAG = "Grabadora";       
	private MediaRecorder mediaRecorder;
	private MediaPlayer mediaPlayer;
	String ruta;
	LocationManager loc_man;
	String torres;
	Double latitud, longitud;
	Httppostaux post;
    String IP_Server = "192.168.43.152";
    String URL_connect = "http://"+IP_Server+"/TaDa/crear_nota.php";
    boolean result_back;
    private ProgressDialog pDialog;
    String text2send;
    String lat_str, lon_str, nombre_voz;
    int i=0;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.principal);
        post = new Httppostaux();
        informacion = (TextView) findViewById(R.id.info);
        btn_record = (Button) findViewById(R.id.bGrabar);
        btn_stop = (Button) findViewById(R.id.bDetener);
        btn_play = (Button) findViewById(R.id.bReproducir);
        btn_guarda = (Button) findViewById(R.id.bGuarda);
        btn_hist_texto = (Button) findViewById(R.id.bhist_notas);
        btn_hist_voz = (Button) findViewById(R.id.bhist_voz);
        nota_texto = (EditText) findViewById(R.id.et_enviar);
        
        btn_record.setOnClickListener(this);
        btn_stop.setOnClickListener(this);
        btn_play.setOnClickListener(this);
        btn_guarda.setOnClickListener(this);
        btn_hist_texto.setOnClickListener(this);
        btn_hist_voz.setOnClickListener(this);
        
        btn_stop.setEnabled(false);
        btn_play.setEnabled(false);
    }
	@SuppressLint("SimpleDateFormat")
	@Override
	public void onClick(View v) {
//---------------------------------------------------NOTAS DE VOZ--------------------------------------------------
		switch (v.getId()) {
		case R.id.bGrabar:
        	Date dt1 = new Date();
        	SimpleDateFormat dh1 = new SimpleDateFormat("HH:mm:ss");
        	String nombre_voz = dh1.format(dt1.getTime());
        	
	        Calendar calendario1 = new GregorianCalendar();
	        Date date1 = calendario1.getTime();
	        SimpleDateFormat df1 = new SimpleDateFormat("dd-MMM-yyyy");
	        String nombre_voz2 = df1.format(date1);
        	
			ruta = Environment.getExternalStorageDirectory().getPath()+"/TaDa/Audio-"+nombre_voz+"-"+nombre_voz2+".3gp";
			mediaRecorder = new MediaRecorder();
		    mediaRecorder.setOutputFile(ruta);
		    mediaRecorder.setAudioChannels(1);
		    mediaRecorder.setAudioSamplingRate(200);
		    mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		    mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		    mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT); 
		    try {
		        mediaRecorder.prepare();
		    } catch (IOException e) {
		        Log.e(LOG_TAG, "Fallo en grabación");
		    }
		    mediaRecorder.start();
		    informacion.setText("grabando");
		    btn_record.setEnabled(false);
		    btn_stop.setEnabled(true);
		    btn_play.setEnabled(false);
			break;
		case R.id.bDetener:
		     mediaRecorder.stop();
		     mediaRecorder.release();
		     informacion.setText("detenido");
			    btn_record.setEnabled(true);
			    btn_stop.setEnabled(false);
			    btn_play.setEnabled(true);
			break;
		case R.id.bReproducir:
			mediaPlayer = new MediaPlayer();
		     try {
				    btn_record.setEnabled(true);
				    btn_stop.setEnabled(false);
				    btn_play.setEnabled(true);
		         mediaPlayer.setDataSource(ruta);
		         mediaPlayer.prepare();
		         mediaPlayer.start();
		         informacion.setText("Reporduciendo");
		     } catch (IOException e) {
		         Log.e(LOG_TAG, "Fallo en reproducción");
		     }
		     break;
//---------------------------------------------------NOTAS DE TEXTO--------------------------------------------------		     
		case R.id.bGuarda:
//--------------------------------CAPTURA DE CATEGORIA-----------------------------------------------------------
			String cate2send = "varias";
//--------------------------------CAPTURA DE TEXTO---------------------------------------------------------------
			if(nota_texto.equals("Introduzca su nota de texto aqui")){
				Toast text = Toast.makeText(this, "Debe Escribir Algo", Toast.LENGTH_SHORT);
				text.show();
		    }else{
		    	text2send = nota_texto.getText().toString();
		    }
//--------------------------------CAPTURA DE FECHA---------------------------------------------------------------
	        Calendar calendario = new GregorianCalendar();
	        Date date = calendario.getTime();
	        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
	        String formatteDate = df.format(date);
//--------------------------------CAPTURA DE HORA----------------------------------------------------------------
        	Date dt = new Date();
        	SimpleDateFormat dh = new SimpleDateFormat("HH:mm:ss");
        	String formatteHour = dh.format(dt.getTime());
    		//informacion.setText(text2send +"/"+ formatteDate +"/"+ formatteHour);
//--------------------------------CAPTURA DE POSICION------------------------------------------------------------
            loc_man = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        	Criteria crit_ubicacion = new Criteria();
        	torres = loc_man.getBestProvider(crit_ubicacion, true);
        	Location ubicacion = loc_man.getLastKnownLocation(torres);
        	
        	if(ubicacion != null){
        		latitud = (Double)(ubicacion.getLatitude());
        		longitud = (Double)(ubicacion.getLongitude());
        		lat_str = latitud.toString();
        		lon_str = longitud.toString();
        	}else{
        		informacion.setText("no llego nada");
        	}
        	
        	new asynclogin().execute(formatteHour,formatteDate,lat_str,lon_str,text2send,cate2send);
			break;
		case R.id.bhist_notas:
			Intent ir_historial = new Intent(this, HistorialTexto.class);
			startActivity(ir_historial);
			break;
		case R.id.bhist_voz:
		Intent ir_historial_voz = new Intent(this, HistorialVoz.class);
		startActivity(ir_historial_voz);
		break;
	   }
	}
//---------------------------------BOTON ENVIAR POR METODO POST--------------------------------------------------
	public void err_nota(){
	    Toast toast1 = Toast.makeText(getApplicationContext(),"No se Pudo Guardar su Nota ", Toast.LENGTH_SHORT);
 	    toast1.show();    	
    }
    public void err_login_vacios(){
	    Toast toast1 = Toast.makeText(getApplicationContext(),"Debe llenar todos los campos", Toast.LENGTH_SHORT);
 	    toast1.show();    	
    }
    public boolean loginstatus(String hora, String fecha, String latitud, String longitud, String nota, String categoria ) {
    	int reg_status=2;

    	ArrayList<NameValuePair> postparameters2send= new ArrayList<NameValuePair>();
		    		postparameters2send.add(new BasicNameValuePair("hora",hora));
		    		postparameters2send.add(new BasicNameValuePair("fecha",fecha));
		    		postparameters2send.add(new BasicNameValuePair("latitud",latitud));
		    		postparameters2send.add(new BasicNameValuePair("longitud",longitud));
		    		postparameters2send.add(new BasicNameValuePair("nota",nota));
		    		postparameters2send.add(new BasicNameValuePair("categoria",categoria));
		    		
		    		JSONArray jdata = post.getserverdata(postparameters2send, URL_connect);

		    		if (jdata!=null && jdata.length() > 0){
		    		JSONObject json_data; 
					try {
						 json_data = jdata.getJSONObject(0); 
						 reg_status=json_data.getInt("logstatus");
						 Log.e("muestra","logstatus= "+reg_status);
					} catch (JSONException e) {
						e.printStackTrace();
					}		            
		    		 if (reg_status==1){ 
		    			 Log.e("loginstatus ", "invalido");
		    			 return false;
		    		 }
		    		 else{
		    			 Log.e("loginstatus ", "valido");
		    			 return true;
		    		 }
		    		 }else{
		    			 Log.e("JSON ", "ERROR");
			    		return false;
			  }
    }
    class asynclogin extends AsyncTask< String, String, String > {
    	String hora, fecha, latitud, longitud, nota, categoria;
        protected void onPreExecute() {
        	pDialog = new ProgressDialog(Principal.this);
            pDialog.setMessage("Guardando Nota...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }
		protected String doInBackground(String... params) {
			hora=params[0];
			fecha=params[1];
			latitud=params[2];
			longitud=params[3];
			nota=params[4];
			categoria=params[5];
			
    		if (loginstatus(hora,fecha,latitud,longitud,nota,categoria)==true){
    			return "ok"; 
    		}else{    		
    			return "err";  
    		}
		}
        protected void onPostExecute(String result) {
           pDialog.dismiss();
           Log.e("onPostExecute=",""+result);
           if (result.equals("ok")){
        	   nota_exitosa();
            }else{
            	err_nota();
            }
        }
    }
	private void nota_exitosa() {
		Toast info = Toast.makeText(this, "Nota Guardada", Toast.LENGTH_SHORT);
		info.show();
	}
}