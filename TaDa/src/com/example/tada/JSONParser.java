package com.example.tada;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import android.util.Log;

public class JSONParser {
	static JSONArray jarray = null;
	
	public JSONParser(){
	}
	
	public JSONArray GETJSONfromUrl(String url){
		StringBuilder builder = new StringBuilder();
		HttpClient cliente = new DefaultHttpClient();
		HttpGet getdata = new HttpGet(url);
		try{
			HttpResponse response = cliente.execute(getdata);
			StatusLine statusline = response.getStatusLine();
			int statuscode = statusline.getStatusCode();
			if(statuscode == 200){
				HttpEntity entity = response.getEntity();
				InputStream content = entity.getContent();
				BufferedReader reader = new BufferedReader(new InputStreamReader(content));
				String line;
				while((line = reader.readLine()) != null){
					builder.append(line);
				}
			}else{
				Log.e("ERROR", "NO SE PUDO DESCARGAR");
			}
		}catch(ClientProtocolException e){
			e.printStackTrace();
		}catch(IOException e){
			e.printStackTrace();
		}
		
		try{
			jarray = new JSONArray(builder.toString());
		}catch(JSONException e){
			Log.e("JSONparser", "error de traduccion de datos"+ e.toString());
		}
		return jarray;
	}
}	