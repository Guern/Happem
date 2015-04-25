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

public class ModifyDataThread extends Thread implements Runnable{
	
	//CREATE GETTER METHODS
	
	public String nomeDB;
	public String cognomeDB;
	public String datadinascitaDB;
	    //String d;
	public String indirizzoDB;
	public String cittaDB;
	public String telefonoDB;
	public String mailDB;
	public String userDB;
	public String passDB;
	
	public ModifyDataThread(String nomeU, String cognomeU, String datadinascitaU, String indirizzoU, String cittaU, String telefonoU, String emailU, String user2, String password1){
		this.nomeDB=nomeU;
		this.cognomeDB=cognomeU;
		this.datadinascitaDB=datadinascitaU;
		this.indirizzoDB=indirizzoU;
		this.cittaDB=cittaU;
		this.telefonoDB=telefonoU;
		this.mailDB=emailU;
		this.userDB=user2;
		this.passDB=password1;
		

	}
	
	@Override
	public void run() {
		
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("Name", nomeDB));
			nameValuePairs.add(new BasicNameValuePair("Surname", cognomeDB));
			nameValuePairs.add(new BasicNameValuePair("DateOfBirth", datadinascitaDB));
			nameValuePairs.add(new BasicNameValuePair("Address", indirizzoDB));
			nameValuePairs.add(new BasicNameValuePair("City", cittaDB));
			nameValuePairs.add(new BasicNameValuePair("Phone", telefonoDB));
			nameValuePairs.add(new BasicNameValuePair("Email", mailDB));
			nameValuePairs.add(new BasicNameValuePair("Username", userDB));
			nameValuePairs.add(new BasicNameValuePair("Password", passDB));
    	
		InputStream is;
    	try
    	{

    		HttpClient httpclient = new DefaultHttpClient();
    		HttpPost httppost = new HttpPost("http://10.0.3.2:80/provamodifica.php");
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
    	    
    	    if(result.equals("Query eseguita correttamente")==true){
      	       Log.i("MODIFYTHREAD", "Modifiche apportate correttamente");
 	    	   
     	    }
     	    else{
      	       Log.i("MODIFYTHREAD", "modifiche non apportate correttamente");

     	    }
 	    	
     	   
          
 	    }catch(Exception e){
 	    	e.printStackTrace();
 	    	Log.e("log_tag-THREAD", "Error "+e.toString());
 	    }	
    	
    	
	}	
}

	

