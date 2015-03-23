package com.enfiv.notitv;

import http.Httppostaux;
import java.util.ArrayList;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

@SuppressLint("ShowToast")
public class MyAlarmService extends Service 
{
	Httppostaux post;
    String IP_Server = "192.168.1.105";
    String URL_connect = "http://"+IP_Server+"/ProyectoEnfasisIV/notificacion.php";
	private SharedPreferences prefs;
	String usuario,titul,ult_titu;
	@Override
	public IBinder onBind(Intent intent) {
		throw new UnsupportedOperationException("Not yet implemented");
	}
	@Override
	public void onCreate() {
		post = new Httppostaux();
		prefs = getSharedPreferences("MisPreferencias",Context.MODE_PRIVATE);
        usuario = prefs.getString("usuario", "");
//		Toast.makeText(getApplicationContext(), "Service Created", 1).show();
		super.onCreate();
	}
	@Override
	public void onDestroy() {
//		Toast.makeText(getApplicationContext(), "Service Destroy", 1).show();
		super.onDestroy();
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		new asynclogin().execute(usuario);
//		Toast.makeText(getApplicationContext(), "Verficando CAMBIOS", 1).show();
		return START_NOT_STICKY;
	}
	class asynclogin extends AsyncTask< String, String, String > {
    	String user;
		protected String doInBackground(String... params) {
			user=params[0];
    		if (loginstatus(user)==true){  
    			return "ok";
    		}else{    		
    			return "err"; 
    		}
		}
        protected void onPostExecute(String result) {
           Log.e("onPostExecute=",""+result);
           if (result.equals("ok")){
        	   notificar();
            }else{
            	err();
            }
        }
    }
	public void notificar(){
		

		
		NotificationCompat.Builder notificacion = new NotificationCompat.Builder(this);
		
		notificacion.setSmallIcon(R.drawable.ic_launcher);
		notificacion.setTicker("Nuevo Titular");
		notificacion.setWhen(System.currentTimeMillis());
		notificacion.setContentTitle("Nuevo Titular");
		notificacion.setContentText("Extender Informacion");
		
		Uri sonido = RingtoneManager.getDefaultUri(Notification.DEFAULT_SOUND);
		notificacion.setSound(sonido);
		
		Bitmap icono = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher); 
		notificacion.setLargeIcon(icono);
		
		PendingIntent mi_ventana_pendiente;
		Intent ir_notificacion = new Intent();
		Context micontexto = getApplicationContext();
		ir_notificacion.setClass(micontexto, Redireccion.class);
		ir_notificacion.putExtra("ID", 1);
		
		mi_ventana_pendiente = PendingIntent.getActivity(micontexto, 0, ir_notificacion, 0);
		notificacion.setContentIntent(mi_ventana_pendiente);
		Notification n = notificacion.build();
		NotificationManager nn = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		nn.notify(1, n);
	}
	public void err(){
//		Toast.makeText(getApplicationContext(), "NO LLEGA NADA", 1).show();
	}
	public void nopasa(){
//		Toast.makeText(getApplicationContext(), "NO PASA", 1).show();
	}
	
    public boolean loginstatus(String username) {
    	int cambio=2;

    	ArrayList<NameValuePair> postparameters2send= new ArrayList<NameValuePair>();
		    		postparameters2send.add(new BasicNameValuePair("usuario",username));

	    		JSONArray jdata = post.getserverdata(postparameters2send, URL_connect);

		    		if (jdata!=null && jdata.length() > 0){
		    		JSONObject json_data;
					try {
						 json_data = jdata.getJSONObject(0); 
						 cambio=json_data.getInt("cambios");
						 titul=json_data.getString("titulares");
						 SharedPreferences.Editor editor = prefs.edit();
							editor.putString("titular", titul );
							editor.commit();
						 Log.e("muestra","logstatus= "+cambio);
					} catch (JSONException e) {
						e.printStackTrace();
					}		            
		    		 if (cambio==0){ 
		    			 Log.e("loginstatus ", "invalido");
		    			 return false;
		    		 }
		    		 else{
		    			 Log.e("loginstatus ", "valido");
		    			 return true;
		    		 }
		    		}else{
		    			 Log.e("JSON ", "ERROR");
		    			 Log.e("pablo", "ERROR");
		    			 nopasa();
			    		return false;
			  }
    }
}