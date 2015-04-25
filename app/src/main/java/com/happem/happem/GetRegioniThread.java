package com.happem.happem;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

public class GetRegioniThread extends Thread implements Runnable{
	
	private String[] regioni= new String[20];
	@Override
	public void run() {
	
    	InputStream is;
    	try
    	{

    		HttpClient httpclient = new DefaultHttpClient();
    		HttpPost httppost = new HttpPost("http://10.0.3.2:80/getRegioni.php");
    	        
    	    HttpResponse response = httpclient.execute(httppost);
    	    HttpEntity entity = response.getEntity();
    	        
    	    is = entity.getContent();
    	    BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);

    	    StringBuilder sb = new StringBuilder();
    	    String line = null;
    	    while ((line = reader.readLine()) != null) 
    	    {
    	      sb.append(line);
    	    }
    	    is.close();
    	    
    	    String result="";
    	    result=sb.toString();
    	    
    	    
    	    if(result.equals("NoResult")==false){
	    	   JSONArray jArray = new JSONArray(result);  
	    	    for(int i=0;i<jArray.length();i++){
	    	       JSONObject json_data = jArray.getJSONObject(i);
	    	       	if(i==0){
	    	       		regioni[i]="Tutte le regioni";
	    	       	}else{
	    	       		regioni[i]=json_data.getString("nomeregione");
	    	       	}
	    	    }
	    	    
	    	
    	    }
    	    else{// non trovato
    	    	Log.i("ERROR-THREAD", "Regione/Provincia Non Trovata!");
    	    }
    	        
	    }catch(Exception e){
	    	e.printStackTrace();
	    	Log.e("log_tag-THREAD", "Error: "+e.toString());
	    }			
	}
	
	public String[] getRegioni(){
		return regioni;
	}

	
	
	
	
	
}

	

