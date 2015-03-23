package com.example.tada;

import java.util.ArrayList;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.example.http.Httppostaux;
import android.support.v7.app.ActionBarActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Registro extends ActionBarActivity implements OnClickListener{

	Httppostaux post;
	Button btn_guardar_reg;
	String IP_Server = "192.168.43.152";
    String URL_connect_reg = "http://"+IP_Server+"/TaDa/adduser.php";
    private ProgressDialog pDialog;
	EditText et_usuario, et_contrasena;
	String usuario,passw;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.registro);
		post = new Httppostaux();
		et_usuario = (EditText) findViewById(R.id.edit_usuario_reg);
		et_contrasena = (EditText) findViewById(R.id.edit_contrasena_reg);
		btn_guardar_reg = (Button) findViewById(R.id.bregistro_perfil);
		btn_guardar_reg.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		usuario = et_usuario.getText().toString();
		passw = et_contrasena.getText().toString();

		if(checklogindata(usuario,passw)==true){
			new asynclogin().execute(usuario,passw);
		}else{
			err_login_vacios();
		}
	}
//------------------------------------------------------------------------------------------------------------
		public void err_reg(){
		    Toast toast1 = Toast.makeText(getApplicationContext(),"Usuario No Guardado", Toast.LENGTH_SHORT);
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
			    		
			    		JSONArray jdata = post.getserverdata(postparameters2send, URL_connect_reg);

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
	        	pDialog = new ProgressDialog(Registro.this);
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
	        	   err_reg();
	            }else{
	            	reg_exitoso();
	            }
	        }
	    }
			private void reg_exitoso() {
				Toast toast1 = Toast.makeText(getApplicationContext(),"Usuario Guardado Exitosamente", Toast.LENGTH_SHORT);
				toast1.show();
				Intent i = new Intent(Registro.this, Principal.class);
				startActivity(i);
				finish();
			}
}
