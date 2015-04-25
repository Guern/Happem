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

public class PersonalDataThread extends Thread implements Runnable{
	
	private String nomeDB;
	private String cognomeDB;
	private String datadinascitaDB;
	private String indirizzoDB;
	private String cittaDB;
	private String telefonoDB;
	private String mailDB;
	private String userDB;
	private String passDB;
	private String user;
	

	
	public PersonalDataThread(String s){
		this.user=s;
	}
	
	@Override
	public void run() {
		
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
    	
    	nameValuePairs.add(new BasicNameValuePair("PersonalDataUser", user));
    	/**INPUT STREAM 
    	 * Most clients will use input streams that read data from the file system (FileInputStream), 
    	 * the network (getInputStream()/getInputStream()), or from an in-memory byte array (ByteArrayInputStream).
    	 * Use InputStreamReader to adapt a byte stream like this one into a character stream.*/
    	InputStream is;
    	try
    	{

    		HttpClient httpclient = new DefaultHttpClient();
    		HttpPost httppost = new HttpPost("http://10.0.3.2:80/prova.php");
    	    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
    	        
    	    HttpResponse response = httpclient.execute(httppost);
    	    HttpEntity entity = response.getEntity();
    	        
    	    is = entity.getContent();
    	    						  /*public BufferedReader (Reader in, int size)
    	    						                           Constructs a new BufferedReader, providing in with size 
    	    						                           characters of buffer.*/
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
    	       Log.i("PersonalData-THREAD", "UtenteTrovato");
	    	   JSONArray jArray = new JSONArray(result);  
	    	    
	    	    for(int i=0;i<jArray.length();i++){
	    	       JSONObject json_data = jArray.getJSONObject(i);
	    	       	 nomeDB=json_data.getString("Name");
		    	     cognomeDB=json_data.getString("Surname");
		    	     datadinascitaDB=json_data.getString("DateOfBirth");
		    	     indirizzoDB=json_data.getString("Address");
		    	     cittaDB=json_data.getString("City");
		    	     telefonoDB=json_data.getString("Phone");
		    	     mailDB=json_data.getString("Mail");
		    	     userDB=json_data.getString("Username");
		    	     passDB=json_data.getString("Password");
	    	
	    	    }
	    	    
    	    }
    	    else{//utente non trovato
    	    	Log.i("ERROR-THREAD", "UtenteNonTrovato!");
    	    }
    	        
	    }catch(Exception e){
	    	e.printStackTrace();
	    	Log.e("log_tag-THREAD", "Error "+e.toString());
	    }			
	}
	
	
	
	//METODI GETTER E SETTER
	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getNomeDB() {
		return nomeDB;
	}

	public void setNomeDB(String nomeDB) {
		this.nomeDB = nomeDB;
	}

	public String getCognomeDB() {
		return cognomeDB;
	}

	public void setCognomeDB(String cognomeDB) {
		this.cognomeDB = cognomeDB;
	}

	public String getDatadinascitaDB() {
		return datadinascitaDB;
	}

	public void setDatadinascitaDB(String datadinascitaDB) {
		this.datadinascitaDB = datadinascitaDB;
	}

	public String getIndirizzoDB() {
		return indirizzoDB;
	}

	public void setIndirizzoDB(String indirizzoDB) {
		this.indirizzoDB = indirizzoDB;
	}

	public String getCittaDB() {
		return cittaDB;
	}

	public void setCittaDB(String cittaDB) {
		this.cittaDB = cittaDB;
	}

	public String getTelefonoDB() {
		return telefonoDB;
	}

	public void setTelefonoDB(String telefonoDB) {
		this.telefonoDB = telefonoDB;
	}

	public String getMailDB() {
		return mailDB;
	}

	public void setMailDB(String mailDB) {
		this.mailDB = mailDB;
	}

	public String getUserDB() {
		return userDB;
	}

	public void setUserDB(String userDB) {
		this.userDB = userDB;
	}

	public String getPassDB() {
		return passDB;
	}

	public void setPassDB(String passDB) {
		this.passDB = passDB;
	}


	
	
	
	
}

	

