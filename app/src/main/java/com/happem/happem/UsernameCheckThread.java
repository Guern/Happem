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

public class UsernameCheckThread extends Thread implements Runnable{
	
	private String username;
	private boolean CheckUsername=false;
	
	
	public UsernameCheckThread(String m){
		this.username=m;
	}
	
	@Override
	public void run() {
		
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
    		nameValuePairs.add(new BasicNameValuePair("Username", username));
    	
    	InputStream is;
    	try
    	{

    		HttpClient httpclient = new DefaultHttpClient();
    		HttpPost httppost = new HttpPost("http://10.0.3.2:80/prova.php");
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
    	    }
    	    is.close();
    	    
    	    String result="";
    	    result=sb.toString();
    	    
    	    //se la stringa result = NoResult non esiste alcun utente con quel USERNAME --> perfetto! 
    	    if(result.equals("NoResult")==true){
    	       CheckUsername=true;
    	    }
    	    else{//esiste utente con quel USERNAME--> impossibile registrare
    	    	Log.i("ERROR-THREAD", "Esiste un utente con quel Username!");
    	    	CheckUsername=false;
    	    }
    	        
	    }catch(Exception e){
	    	e.printStackTrace();
	    	Log.e("log_tag-THREAD", "Error "+e.toString());
	    }			
	}
	
	
	public boolean getUsernameCheck(){
		return CheckUsername;
	}
	

	
}

	

