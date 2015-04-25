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


public class GetAnnunciThread extends Thread implements Runnable {
	String r;
	String p;
	String[] t;
	ArrayList<Parent> annuncitrovati=new ArrayList<Parent>();
	
	public GetAnnunciThread(String regione, String provincia, String[] tag) {
		r=regione.trim();
		p=provincia.trim();
		t=tag;
	
	}
	
	@Override
	public void run(){
		String sql;
		
		/*SQL DIFFERENTE IN BASE ALLA RICERCA CHE L'UTENTE VUOLE EFFETTUARE 
		 * 		- per tutte le regioni e per tutte le province
		 * 		- per una regione specifica ma per tutte le province
		 * 		- per una regione e provincia specifica */
		
		
		if(r.equals("Tutte le regioni") && p.equals("Tutte le province")){
			Log.i("SQL", "Tutte le regioni e tutte le province");
			 sql="SELECT DISTINCT * FROM ANNUNCI, REGIONI, PROVINCE WHERE ANNUNCI.idRegione=REGIONI.idregione AND ANNUNCI.idProvincia=PROVINCE.idprovincia AND ( ";
		}
		else if(r.equals("Tutte le regioni")==false && p.equals("Tutte le province")==true){
			Log.i("SQL", "regione specifica e tutte le province");

			sql="SELECT DISTINCT * FROM ANNUNCI, REGIONI, PROVINCE WHERE ANNUNCI.idRegione=REGIONI.idregione AND ANNUNCI.idProvincia=PROVINCE.idprovincia AND nomeregione LIKE '%"+r+"%' AND ( ";

		}else{
			Log.i("SQL", " regione e  provincia specificati");

			 sql="SELECT DISTINCT * FROM ANNUNCI, REGIONI, PROVINCE WHERE ANNUNCI.idRegione=REGIONI.idregione AND ANNUNCI.idProvincia=PROVINCE.idprovincia AND nomeregione LIKE '%"+r+"%' AND nomeprovincia LIKE '%"+p+"%' AND ( ";

		}
		
		/*APPENDO ALLA STRINGA I TAG INSERITI*/
		for(int i=0; i<t.length;i++){
			if(i!=t.length-1){
				sql=sql+"Tags LIKE '%"+t[i].trim()+"%' OR ";
			}
			else{
				sql=sql+"Tags LIKE '%"+t[i].trim()+"%');";
			}
			
		}
		
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("SQL", sql));
		InputStream is;
    	try
    	{

    		HttpClient httpclient = new DefaultHttpClient();
    		HttpPost httppost = new HttpPost("http://10.0.3.2:80/getAnnunci.php");
    		
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
	    	   JSONArray jArray = new JSONArray(result);  
	    	    for(int i=0;i<jArray.length();i++){
	    	       JSONObject json_data = jArray.getJSONObject(i);
	    	       /*OGGETTO DI CLASSE PARENT ANDRA' A CONTENTERE POSIZIONE E AZIENDA
	    	        * POSIZIONE E AZIENDA SARANNO VISUALIZZATE NELL'EXPANDABLE LIST VIEW NELLA PARTE "PARENT", 
	    	        * QUELLA CIOE' CHE NON SI PUO' COLLASSARE */
	    	       Parent parent= new Parent();
	    	       	String num = json_data.getString("_idA");
	    	       	String pos = json_data.getString("Posizione");
	    	       	String az = json_data.getString("Azienda");
	    	       	parent.setId(num);
	    	       	parent.setPosizione(pos);
	    	       	parent.setAzienza(az);
	    	       
	    	       	/* ESISTE UN SOLO CHILD PER OGNI PARENT NEL CASO DEGLI ANNUNCI*/
	    	       Child child = new Child();
	    	       
	    	       /* Per i campi che possono assumere valore null (quelli optionali quindi), 
	    	        * devo controllare ed eventualmente settare una stringa diversa tipo ".....NON presente" */
	    	       	if(json_data.getString("Indirizzo").toString().equals("null") || json_data.getString("Indirizzo").toString().trim().equals("")){
	    	       		child.setIndirizzo("Indirizzo NON presente");
	    	       	}else{
	    	       		child.setIndirizzo(json_data.getString("Indirizzo").toString());
	    	       	}	
	    	       	    	       
	    	       	if(json_data.getString("Descrizione").toString().equals("null") || json_data.getString("Descrizione").toString().trim().equals("")){
	    	       		child.setDescrizione("Descrizione non presente");
	    	       	}else{
	    	       		child.setDescrizione(json_data.getString("Descrizione").toString());
	    	       	}
	    	       	child.setLuogoLavoro("Regione: "+json_data.getString("nomeregione")+"   Provincia:  "+json_data.getString("nomeprovincia"));
	    	       	if(json_data.getString("Stipendio").toString().equals("null") || json_data.getString("Stipendio").toString().trim().equals("")){
	    	       		child.setStipendio("Informazione non presente");
	    	       	}else{
	    	       		child.setStipendio(json_data.getString("Stipendio").toString());
	    	       	}
	    	       	if(json_data.getString("CandidaturaCellulare").toString().equals("1")){
	    	       		child.setCandidaturaCellulare("Candidatura via cellulare possibile");
	    	       	}else{
	    	       		child.setCandidaturaCellulare("Candidatura via cellulare NON e' possibile!");

	    	       	}
	    	       	if(json_data.getString("email_rif").toString().equals("null") || json_data.getString("email_rif").toString().trim().equals("")){
	    	       		child.setEmail("email non presente");
	    	       		child.setCandidaturaCellulare("Candidatura via cellulare NON e' possibile!");
	    	       	}else{
	    	       		child.setEmail(json_data.getString("email_rif"));
	    	       	}
	    	       	if(json_data.getString("YouTubeVideo").toString().equals("null")||json_data.get("YouTubeVideo").toString().equals("")){
	    	       		child.setYT("Video non presente");
	    	       	}else{
	    	       		child.setYT(json_data.getString("YouTubeVideo"));
	    	       	}
	    	       	
	    	       /*Aggiungo Child al parent
	    	        * 	- creo ArrayList di elementi della classe Child per quel Parent
	    	        * 	- recupero i children e aggiungo il child appena creato*/
	    	       	parent.setChildren(new ArrayList<Child>());
	    	       	parent.getChildren().add(child);
	    	       //aggiungo il parent alla lista che dovro' ritornare	
	    	       annuncitrovati.add(parent);
	    	    }
	    	    
	    	
    	    }
    	    else{// non trovato
    	    	Log.i("ERROR-THREAD", "Annunci Non Trovati!");
    	    }
    	        
	    }catch(Exception e){
	    	e.printStackTrace();
	    	Log.e("log_tag-THREAD", "Error: "+e.toString());
	    }	    	      
			
	}


}
