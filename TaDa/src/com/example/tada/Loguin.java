package com.example.tada;

import java.io.File;
import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.http.Httppostaux;

public class Loguin extends ActionBarActivity implements OnClickListener {
	
	Button btn_ingresar, btn_registro;
	EditText et_usuario, et_contrasena;
	Httppostaux post;
    String IP_Server = "192.168.43.152";
    String URL_connect = "http://"+IP_Server+"/TaDa/acces.php";
    boolean result_back;
    private ProgressDialog pDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loguin);
		post = new Httppostaux();
		btn_ingresar=(Button) findViewById(R.id.bingresar);
		btn_registro=(Button) findViewById(R.id.bregistro);
		et_usuario = (EditText) findViewById(R.id.edit_usuario);
		et_contrasena = (EditText) findViewById(R.id.edit_contrasena);
		
		btn_ingresar.setOnClickListener(this);
		btn_registro.setOnClickListener(this);
		crear_folder();
	}

	private void crear_folder() {
		File folder = new File(Environment.getExternalStorageDirectory() + "/TaDa");
	    boolean success = true;
	    if (!folder.exists()) {
//	        Toast.makeText(this, "carpeta creada", Toast.LENGTH_SHORT).show();
	        success = folder.mkdir();
	    }
	    if (success) {
//	        Toast.makeText(this, "La carpeta ya esxiste", Toast.LENGTH_SHORT).show();
	    } else {
//	        Toast.makeText(this, "fallo al crear carpeta", Toast.LENGTH_SHORT).show();
	    }
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bingresar:
			String usuario=et_usuario.getText().toString();
			String passw=et_contrasena.getText().toString();

			if(checklogindata(usuario,passw)==true){
				new asynclogin().execute(usuario,passw);
			}else{
				err_login_vacios();
			}
			break;
			
		case R.id.bregistro:
			Intent i = new Intent(Loguin.this, Registro.class);
			startActivity(i);
			break;
		}
	}
//------------------------------------------------------------------------------------------------------------------
	public void err_login(){
	    Toast toast1 = Toast.makeText(getApplicationContext(),"Usuario o Contraseña incorrectos", Toast.LENGTH_SHORT);
 	    toast1.show();    	
    }
    public void err_login_vacios(){
	    Toast toast1 = Toast.makeText(getApplicationContext(),"Debe llenar todos los campos", Toast.LENGTH_SHORT);
 	    toast1.show();    	
    }
    public boolean loginstatus(String username ,String passwo ) {
    	int logstatus=2;

    	ArrayList<NameValuePair> postparameters2send= new ArrayList<NameValuePair>();
		    		postparameters2send.add(new BasicNameValuePair("usuario",username));
		    		postparameters2send.add(new BasicNameValuePair("password",passwo));
		    		
		    		JSONArray jdata = post.getserverdata(postparameters2send, URL_connect);

		    		if (jdata!=null && jdata.length() > 0){
		    		JSONObject json_data; 
					try {
						 json_data = jdata.getJSONObject(0); 
						 logstatus=json_data.getInt("logstatus");
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
        	pDialog = new ProgressDialog(Loguin.this);
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
				Intent i = new Intent(Loguin.this, Principal.class);
				startActivity(i);
            }else{
            	err_login();
            }
        }
    }
//------------------------------------------------------------------------------------------------------------------
}