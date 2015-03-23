package com.enfiv.notitv;
import http.Httppostaux;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
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
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Identificacion extends ActionBarActivity implements OnClickListener {

	Button btn_ingresar, btn_registro;
	EditText et_nombre, et_usuario, et_contrasena;
	Httppostaux post;
    String IP_Server = "192.168.1.105";
    String URL_connect = "http://"+IP_Server+"/ProyectoEnfasisIV/autenticarUsuario.php";
    boolean result_back;
    private ProgressDialog pDialog;
	private SharedPreferences prefs;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.identificacion);
		post = new Httppostaux();
		prefs = getSharedPreferences("MisPreferencias",Context.MODE_PRIVATE);
		btn_ingresar = (Button) findViewById(R.id.btn_ingresar);
		btn_registro = (Button) findViewById(R.id.btn_registro);
		et_usuario = (EditText) findViewById(R.id.usuario);
		et_contrasena = (EditText) findViewById(R.id.contrasena);
		btn_ingresar.setOnClickListener(this);
		btn_registro.setOnClickListener(this);
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_ingresar:
			
				String usuario=et_usuario.getText().toString();
				String passw=et_contrasena.getText().toString();
				if(checklogindata(usuario,passw)==true){
				new asynclogin().execute(usuario,passw);
    		}else{
    			err_login_vacios();
    		}
    	break;
	    	
		case R.id.btn_registro:
				Intent ir_registro = new Intent().setClass(this, Registro.class);
				startActivity(ir_registro);
    	break;
		}
	}
    public void err_login(){
	    Toast toast1 = Toast.makeText(getApplicationContext(),"Nombre, Usuario o Contraseña incorrectos", Toast.LENGTH_SHORT);
 	    toast1.show();    	
    }
    public void err_login_vacios(){
	    Toast toast1 = Toast.makeText(getApplicationContext(),"Debe llenar todos los campos", Toast.LENGTH_SHORT);
 	    toast1.show();    	
    }
    public boolean loginstatus(String username ,String passwo ) {
    	int logstatus=2, cambio=2;
    	String nomb_usuario,titu,cambioStr,usua;
    	ArrayList<NameValuePair> postparameters2send= new ArrayList<NameValuePair>();
		    		postparameters2send.add(new BasicNameValuePair("usuario",username));
		    		postparameters2send.add(new BasicNameValuePair("password",passwo));
	    		JSONArray jdata = post.getserverdata(postparameters2send, URL_connect);
		    		if (jdata!=null && jdata.length() > 0){
		    		JSONObject json_data;
					try {
						 json_data = jdata.getJSONObject(0);
						 logstatus=json_data.getInt("logstatus");
						 nomb_usuario=json_data.getString("nombre");
						 titu=json_data.getString("titulares");
						 usua=json_data.getString("usuario");
						 cambio = json_data.getInt("cambios");
						 cambioStr = String.valueOf(cambio);
						 SharedPreferences.Editor editor = prefs.edit();
							editor.putString("nombre", nomb_usuario );
							editor.putString("titular", titu );
							editor.putString("cambios", cambioStr );
							editor.putString("usuario", usua );
							editor.commit();
						 Log.e("muestra","logstatus= "+logstatus);
					} catch (JSONException e) {
						e.printStackTrace();
					}		            
		    		 if (logstatus==0){
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
			    		return false;
			  }
    }
    public boolean checklogindata(String username ,String password ){
    if 	(username.equals("") || password.equals("")){
    return false;
    }else{
    	return true;
    }
}           
 
    class asynclogin extends AsyncTask< String, String, String > {
    	String user,pass;
        protected void onPreExecute() {
        	pDialog = new ProgressDialog(Identificacion.this);
            pDialog.setMessage("Autenticando....");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }
		protected String doInBackground(String... params) {
			user=params[0];
			pass=params[1];
    		if (loginstatus(user,pass)==true){  
    			return "ok";
    		}else{    		
    			return "err";  
    		}
		}
        protected void onPostExecute(String result) {
           pDialog.dismiss();
           Log.e("onPostExecute=",""+result);
           if (result.equals("ok")){
				Intent i = new Intent(Identificacion.this, Principal.class);
				startActivity(i);
				finish();
            }else{
            	err_login();
            }
        }
    }
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		  if (keyCode == KeyEvent.KEYCODE_BACK) {new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert).setTitle("Salir")
		      .setMessage("Estás seguro?").setNegativeButton(android.R.string.cancel, null)//sin listener
		      .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {//un listener que al pulsar, cierre la aplicacion
		        @Override
		        public void onClick(DialogInterface dialog, int which){
		        	Identificacion.this.finish();
		        }
		      }).show();
		    return true;
		  }
		  return super.onKeyDown(keyCode, event);
		}
}