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

public class GetProvinceThread extends Thread implements Runnable{
	public String[] province= new String[100];
	private String regione;
	public GetProvinceThread(String regione){
		this.regione=regione;
		//Log.i("", "Regione"+this.regione);
	}
	
	
	
	@Override
	public void run() {
		
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("Regione", regione));
	
    	InputStream is;
    	try
    	{

    		HttpClient httpclient = new DefaultHttpClient();
    		HttpPost httppost = new HttpPost("http://10.0.3.2:80/getProvince.php");
    	    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
    
    	    HttpResponse response = httpclient.execute(httppost);
    	    HttpEntity entity = response.getEntity();
    	        
    	    is = entity.getContent();
    	    BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);

    	    StringBuilder sb = new StringBuilder();
    	    String line = null;
    	    while ((line = reader.readLine()) != null) 
    	    {
    	      sb.append(line);
    	      //Log.i("E", line);
    	    }
    	    is.close();
    	    
    	    String result="";
    	    result=sb.toString();
    	    
    	    
    	    if(result.equals("NoResult")==false){
	    	   JSONArray jArray = new JSONArray(result);  
	    	   String[] province= CreateProvinceArray(jArray.length());
	    	    for(int i=0;i<jArray.length();i++){
	    	       JSONObject json_data = jArray.getJSONObject(i);
	    	       	AddItem(i,json_data.getString("nomeprovincia"));
	    	    }
	    	    
	    	
    	    }
    	    else{// non trovato
    	    	Log.i("ERROR-THREAD", "Provincia Non Trovata!");
    	    }
    	        
	    }catch(Exception e){
	    	e.printStackTrace();
	    	Log.e("log_tag-THREAD", "Error: "+e.toString());
	    }	    	      
			
	}

	private String[] CreateProvinceArray(int dim){
		String[] province = new String[dim];
		return province;
	}
	private void AddItem(int i, String s){
		province[i]=s; 	
	}


	
	
	
	
	
}

	

