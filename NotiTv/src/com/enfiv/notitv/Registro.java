package com.enfiv.notitv;

import http.Httppostaux;
import java.util.ArrayList;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Registro extends ActionBarActivity implements OnClickListener {

	EditText et_nom_reg, et_user_reg, et_pass_reg, et_apell_reg;
	Button btn_guardar;
	EditText nom_persona;
	Httppostaux post;
    String IP_Server = "192.168.1.105";
    String URL_connect = "http://"+IP_Server+"/ProyectoEnfasisIV/registrarUsuario.php";
    boolean result_back;
    private ProgressDialog pDialog;
    int check;
    private SharedPreferences prefs;
    String usu_a_guardar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.registro);
		post = new Httppostaux();
		prefs = getSharedPreferences("MisPreferencias",Context.MODE_PRIVATE);
		et_nom_reg = (EditText) findViewById(R.id.et_nombre_reg);
		et_apell_reg = (EditText) findViewById(R.id.et_apellido_reg);
		et_user_reg = (EditText) findViewById(R.id.et_ususario_reg);
		et_pass_reg = (EditText) findViewById(R.id.et_contrasena_reg);
		btn_guardar = (Button) findViewById(R.id.btn_guardar);
		btn_guardar.setOnClickListener(this);
	}

	@Override
	public void onClick(View arg0) {

		switch (arg0.getId()) {
		case R.id.btn_guardar:
			
			String nom_usu=et_nom_reg.getText().toString();
			String nom_apell=et_apell_reg.getText().toString();
			String usuario=et_user_reg.getText().toString();
			String passw=et_pass_reg.getText().toString();
			if(checklogindata(nom_usu,nom_apell,usuario,passw)==true){
					new asynclogin().execute(nom_usu,nom_apell,usuario,passw);
		}else{
			err_login_vacios();
		}
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
    /*Valida el estado del logueo solamente necesita como parametros el usuario y passw*/
    public boolean loginstatus(String nomb, String apellido, String username ,String passwo) {
    	int logstatus=3;
    	
    	/*Creamos un ArrayList del tipo nombre valor para agregar los datos recibidos por los parametros anteriores
    	 * y enviarlo mediante POST a nuestro sistema para relizar la validacion*/ 
    	ArrayList<NameValuePair> postparameters2send= new ArrayList<NameValuePair>();
    				postparameters2send.add(new BasicNameValuePair("nombre",nomb));
    				postparameters2send.add(new BasicNameValuePair("apellido",apellido));
		    		postparameters2send.add(new BasicNameValuePair("username",username));
		    		postparameters2send.add(new BasicNameValuePair("password",passwo));
		    		//realizamos una peticion y como respuesta obtenes un array JSON
	    		JSONArray jdata = post.getserverdata(postparameters2send, URL_connect);
		    		//si lo que obtuvimos no es null
		    		if (jdata!=null && jdata.length() > 0){
		    		JSONObject json_data; //creamos un objeto JSON
					try {
						usu_a_guardar = username;
						 json_data = jdata.getJSONObject(0); //leemos el primer segmento en nuestro caso el unico
						 logstatus=json_data.getInt("logstatus");//accedemos al valor 
						 Log.e("muestra","logstatus= "+logstatus);//muestro por log que obtuvimos
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						}
		    		}
					//validamos el valor obtenido
		    		 if (logstatus==0){// [{"logstatus":"0"}] 
		    			 Log.e("loginstatus ", "invalido");
		    			 Toast.makeText(getApplicationContext(),"Registro No Exitoso", Toast.LENGTH_SHORT).show();
		    			 return false;
		    		 }
		    		 if(logstatus==1){
		    			 Toast.makeText(getApplicationContext(),"Ya existe el Usuario", Toast.LENGTH_SHORT).show();
		    			 return false; 
		    		 }
		    		 if(logstatus==2){
		    				SharedPreferences.Editor editor = prefs.edit();
		    				editor.putString("nombre", usu_a_guardar );
		    				editor.commit();
		    			 return true; 
		    		 }else{
		    			 return false;
		    		 }
    }
    //validamos si no hay ningun campo en blanco
    public boolean checklogindata(String nom_check,String apell_check, String username ,String password ){
    if 	(nom_check.equals("") || apell_check.equals("") || username.equals("") || password.equals("")){
    return false;
    }else{
    	return true;
    }
}           
/*		CLASE ASYNCTASK
 * 
 * usaremos esta para poder mostrar el dialogo de progreso mientras enviamos y obtenemos los datos
 * podria hacerse lo mismo sin usar esto pero si el tiempo de respuesta es demasiado lo que podria ocurrir    
 * si la conexion es lenta o el servidor tarda en responder la aplicacion sera inestable.
 * ademas observariamos el mensaje de que la app no responde.     
 */    
    class asynclogin extends AsyncTask< String, String, String > {
    	String nomb,apell,user,pass;
        protected void onPreExecute() {
//        	para el progress dialog
        	pDialog = new ProgressDialog(Registro.this);
            pDialog.setMessage("Autenticando....");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }
		protected String doInBackground(String... params) {
			nomb=params[0];
			apell=params[1];
			user=params[2];
			pass=params[3];
			//enviamos y recibimos y analizamos los datos en segundo plano.
		
			if (loginstatus(nomb,apell,user,pass)==true){  
    			return "ok"; //login valido
    		}else{
    			return "err"; //login invalido  
    		}
		}
        protected void onPostExecute(String result) {
           pDialog.dismiss();
           Log.e("onPostExecute=",""+result);
           if (result.equals("ok")){
        	    reg_exitoso();
            }else{
            	reg_invalido();
            }
        }
    }
		private void reg_exitoso() {
		    Toast toast1 = Toast.makeText(getApplicationContext(),"Registro Extoso", Toast.LENGTH_SHORT);
	 	    toast1.show();
			Intent i = new Intent(Registro.this, Principal.class);
			startActivity(i);
			finish();
		}
		private void reg_invalido() {
		    Toast.makeText(getApplicationContext(),"No se Pudo Registrar", Toast.LENGTH_SHORT).show();
		    et_nom_reg.setText("");
			et_apell_reg.setText("");
			et_user_reg.setText("");
			et_pass_reg.setText("");
		}
    }