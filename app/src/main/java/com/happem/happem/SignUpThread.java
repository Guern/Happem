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
import android.util.Log;

public class SignUpThread extends Thread implements Runnable{
	private String name;
	private String surname;
	private String dOb;
	private String address;
	private String city;
	private String phone;
	private String email;
	private String user;
	private String pass;
	
	
	public SignUpThread(String s, String p, String datadinascitaU, String indirizzoU, String cittaU, String telefonoU, String emailU, String user, String password1){
		name=s;
		surname=p;
		dOb=datadinascitaU;
		address=indirizzoU;
		city=cittaU;
		phone=telefonoU;
		email=emailU;
		this.user=user;
		pass=password1;
		
	}
	
	@Override
	public void run() {
		
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
    		nameValuePairs.add(new BasicNameValuePair("Name", name));
    		nameValuePairs.add(new BasicNameValuePair("Surname", surname));
    		nameValuePairs.add(new BasicNameValuePair("DateOfBirth", dOb));
    		nameValuePairs.add(new BasicNameValuePair("Address", address));
    		nameValuePairs.add(new BasicNameValuePair("City", city));
    		nameValuePairs.add(new BasicNameValuePair("Phone", phone));
    		nameValuePairs.add(new BasicNameValuePair("Email", email));
    		nameValuePairs.add(new BasicNameValuePair("Username", user));
    		nameValuePairs.add(new BasicNameValuePair("Password", pass));

    	InputStream is;
    	try
    	{

    		HttpClient httpclient = new DefaultHttpClient();
    		HttpPost httppost = new HttpPost("http://10.0.3.2:80/provainserimento.php");
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
     	       Log.i("prova-THREAD", "UtenteInseritoCorrettamente");
    	    }
    	    else{
     	       Log.i("prova-THREAD", "UtenteInseritoMALAMENTE");	
    	    }
	    	
    	   
    	        
	    }catch(Exception e){
	    	e.printStackTrace();
	    	Log.e("SIGNUP-THREAD", "Error "+e.toString());
	    }			
	}
	
	
	
}

	

