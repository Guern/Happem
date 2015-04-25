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

public class CheckUserThread extends Thread implements Runnable{
	
	private String user;
	private String pass;
	private boolean CheckUser;
	private boolean CheckPass;
	
	
	public CheckUserThread(String s, String p){
		this.user=s;
		this.pass=p;
	}
	
	@Override
	public void run() {
		//valori passati alla pagina php 
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
    		nameValuePairs.add(new BasicNameValuePair("Username", user));
    	
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
    	    
    	    
    	    if(result.equals("NoResult")==false){
    	       CheckUser=true;
	    	   JSONArray jArray = new JSONArray(result);  
	    	    String passDB="";
	    	    for(int i=0;i<jArray.length();i++){
	    	       JSONObject json_data = jArray.getJSONObject(i);
	    	       
	    	        passDB = json_data.getString("Password");
	    	      
	    	  
	    	    }
	    	    
	    	    //controllo della password
	    	    if(passDB.equals(pass)){
	    	    	CheckPass=true;
				}else{//password diverse
					CheckPass=false;
				}
    	    }
    	    else{//utente non trovato
    	    	Log.i("ERROR-THREAD", "UtenteNonTrovato!");
    	    	CheckUser=false;
    	    }
    	        
	    }catch(Exception e){
	    	e.printStackTrace();
	    	Log.e("log_tag-THREAD", "Error "+e.toString());
	    }			
	}
	
	/*CONTROLLO SE USER/PSW INSERTI COINCIDONO CON QUELLI DEL DB*/
	public boolean getCheck(){
		boolean overallCheck=false;
		
		if(CheckUser==false | CheckPass==false){
			  overallCheck=false;
		}
		if(CheckUser==true && CheckPass==true){
			 overallCheck=true;
		}
		return overallCheck;
	}
	
	
	
	
	public boolean getUserCheck(){
		return CheckUser;
	}
	
	public boolean getPassCheck(){
		return CheckPass;
	}
	
}

	

