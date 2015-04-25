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

public class GetTagThread extends Thread implements Runnable{
	/**MASSIMO NUMERO DI TAG CHE POSSONO ESSERE PRESENTI*/
	final static int MAX = 10000;
	private String[] tag= new String[MAX]; 
	private int posizione_libera=0;
	
	public GetTagThread() {
	}
	
	@Override
	public void run() {
	
    	InputStream is;
    	try
    	{

    		HttpClient httpclient = new DefaultHttpClient();
    		HttpPost httppost = new HttpPost("http://10.0.3.2:80/getTag.php");
    	        
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
    	    
    	 //   Log.i("prova2- RISULTATO QUERY", result);
    	    
    	    if(result.equals("NoResult")==false){
    	       Log.i("THREAD-TAG", "Tag Trovati");
	    	   JSONArray jArray = new JSONArray(result);  
	    	    for(int i=0;i<jArray.length();i++){
	    	       JSONObject json_data = jArray.getJSONObject(i);
	    	       		// separo tag all'interno della stinga e li metto in un array 
	    	       String[] l = json_data.getString("Tags").trim().split(",");
	    	       		//scorro array appena creato e metto valore nella prima posizione libera dell'array tag (cio� uguale a null e che corrisponde al valore "posizione_libera") 
	    	       for(int k=0; k<l.length; k++){
	    	  			tag[posizione_libera]=l[k].trim();
	         			posizione_libera++;
	   	       		}
	   	       	}
	    	  }
    	    else{// non trovato
    	    	Log.i("ERROR-THREAD-TAG", "Tags non trovati nel DB!");
    	    }
    	        
	    }catch(Exception e){
	    	e.printStackTrace();
	    	Log.e("log_tag-THREAD", "Error: "+e.toString());
	    }			
	}
	
	public String[] getTag(){
		String[] arrayTag= pulisciTagArray(tag);
		return arrayTag;
	}

	private String[] pulisciTagArray(String[] t) {
		int count=0;
		/*CONTO QUANTE POSIZIONI SONO DIVERSE DA NULL*/
		for (int i=0; i<t.length; i++){
			if(t[i]!=null){
				count=count+1;
			}		
		}
		
		String[] risultato= new String[count-1];
		int freeposition=0;
		if(count!=0){
			for(int j=0; j<count; j++){
				//controllo solo per i valori diversi da null
				if(t[j] != null){
					//controllo se all'interno di risultato esiste un tag uguale in tal caso non lo metto 
					String tag_provvisorio=t[j].toString();
					boolean exist_tag=false;
					for(int i=0; i<risultato.length; i++){
							if(i==0 && risultato[0]==null){
								risultato[0]=tag_provvisorio;
							}
							else{
								if(risultato[i]!=null && risultato[i].toString().equals(tag_provvisorio)){
									exist_tag=true;
								}
							}
					}
					//se e' rimasto uguale a falso inserisco in risultato finale in prima posizione not null
					if(exist_tag==false){
						for(int i=0; i<risultato.length; i++){
							if(risultato[i]==null){
								risultato[freeposition]=t[j];
								freeposition++;
								break;
							}
						}
					}
				}
			}
		}
		
		return risultato; //risultato= array la cui dimensione corrisponde al numero di tag inseriti e ogni elementi e' privo di spazi/virgole
	}

	
	
	
	
	
}

	

