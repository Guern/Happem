package com.happem.happem;




import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;


public class Navig extends Activity {

	private final static int MAP_MESSAGE_ID = 1;
	private final static int PROGRESS_DIALOG_ID = 1;
	private double currentlat;
	private double currentlong;
	private MapView mMapView;
	private GoogleMap mMap;
	private Marker mark;
	private Marker mark2;
	private LocationManager locationManager;
	private String inputName;
	private String posizione;
	private String azienda;
	private String username;
	private String regione;
	private String provincia;
	private String tag[];
	double lat;
	double lng;
	private LatLng destinazione;
	private TextView tvLungh, tvDur;
	private Context context;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//VOGLIO PERSONALIZZARE L'ICONA IN ALTO A SINISTRA CHE COMPARIRA' QUANDO QUESTA ACTIVITY VERRA' CHIAMATA 
		//RICORDO CHE QUEST ACTIVITY HA COME TEMA APPLICATO "DIALOG" --> apparir� quindi come una finestra di dialogo
		
		requestWindowFeature(Window.FEATURE_LEFT_ICON);
		setContentView(R.layout.navig);
		
		getWindow().setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.directions);
		mMapView = (MapView) findViewById(R.id.mymapview);
		mMapView.onCreate(savedInstanceState);
    	context=this;
    	
    	/***             MapsInitializer.initialize 
    	 * 
    	 * Initializes the Google Maps Android API so that its classes are ready for use. 
    	 * If you are using MapFragment or MapView and have already obtained a (non-null) 
    	 * GoogleMap by calling getMap() on either of these classes, then it is not necessary to call this. ***/
    	MapsInitializer.initialize(this);
    	
		if (mMap == null) {
			mMap = mMapView.getMap();
		             if (mMap != null) {
		        }
		}
    	
        inputName = getIntent().getStringExtra(getPackageName()+".Address");
        posizione = getIntent().getStringExtra(getPackageName()+".Posizione");
        azienda = getIntent().getStringExtra(getPackageName()+".Azienda");
        
        username= getIntent().getStringExtra(getPackageName()+".Username");
        regione=getIntent().getStringExtra(getPackageName()+".Regione");
        provincia=getIntent().getStringExtra(getPackageName()+".Provincia");
        tag=getIntent().getStringArrayExtra(getPackageName()+".Tag");
        
        TextView tv= (TextView) findViewById(R.id.addressName);
        tv.setText("INDIRIZZO DI DESTINAZIONE:  "+inputName);
        tvLungh = (TextView) findViewById(R.id.lenght);
        tvDur = (TextView) findViewById(R.id.duration);
        
        searchPlace();
        
	}

	
	/** A method to download json data from url */
    private String downloadUrl(String strUrl) throws IOException{
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
                URL url = new URL(strUrl);
                //creo una connessione http per comunicare con l'url
                urlConnection = (HttpURLConnection) url.openConnection();

                // mi connetto all' url 
                urlConnection.connect();

                //Leggo i dati dall'url 
                iStream = urlConnection.getInputStream();

                BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

                StringBuffer sb  = new StringBuffer();

                String line = "";
                while( ( line = br.readLine())  != null){
                        sb.append(line);
                }
                
                data = sb.toString();

                br.close();

        }catch(Exception e){
                Log.d("Exception while downloading url", e.toString());
        }finally{
                iStream.close();
                urlConnection.disconnect();
        }
        return data;
     }

    
    /***   ASYNC TASK 
      
      AsyncTask enables proper and easy use of the UI thread. 
      This class allows to perform background operations and publish results on the UI thread without having to 
      manipulate threads and/or handlers.
      AsyncTask is designed to be a helper class around Thread and Handler and does not constitute a generic threading 
      framework. AsyncTasks should ideally be used for short operations (a few seconds at the most.) 
      
      An asynchronous task is defined by a computation that runs on a background thread and whose result is published on 
      the UI thread. An asynchronous task is defined by 3 generic types, called Params, Progress and Result, and 4 steps, 
      called onPreExecute, doInBackground, onProgressUpdate and onPostExecute.
      
     ***/
    
	
	// raccolgo i dati dall'url passata
	private class DownloadTask extends AsyncTask<String, Void, String>{			
					
			//operazioni fatte in background
			@Override
			protected String doInBackground(String... url) {
					
				//STRINGA USATA PER MEMORIZZARE I DATI
				String data = "";
						
				try{
				//RECUPERO DATI DALL'URL 
					data = downloadUrl(url[0]);
				}catch(Exception e){
					Log.d("Background Task",e.toString());
				}
				return data;		
			}
			
			// DOPO ESECUZIONE IN BACKGROUND VIENE ESEGUITO QUESTO METODO
			@Override
			protected void onPostExecute(String result) {			
				super.onPostExecute(result);			
				
				/*  PARSER TASK  
				   A class to parse the Google Places in JSON format*/
				ParserTask parserTask = new ParserTask();
				// invocata dal thread per fare il parsing dei dati 
				parserTask.execute(result);
					
			}		
		}
		
		/** A class to parse the Google Places in JSON format */
	    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >{
	    	
	    	// Parsing the data in non-ui thread    	
			@Override
			protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {
				
				JSONObject jObject;	
				List<List<HashMap<String, String>>> routes = null;			           
	            
	            try{
	            	jObject = new JSONObject(jsonData[0]);
	            	DirectionsJSONParser parser = new DirectionsJSONParser();
	            	Log.i("DIR", jObject.toString());
	            	// passo l'oggetto da trasformare
	            	routes = parser.parse(jObject);    
	            }catch(Exception e){
	            	e.printStackTrace();
	            }
	            return routes;
			}
			
			// thread eseguito dopo il parsing
			@Override
			protected void onPostExecute(List<List<HashMap<String, String>>> result) {
				ArrayList<LatLng> points = null;
				PolylineOptions lineOptions = null;
				MarkerOptions markerOptions = new MarkerOptions();
				String distance = "";
				String duration = "";
				
				if(result.size()<1){
					Toast.makeText(getBaseContext(), "Impossibile disegnare", Toast.LENGTH_SHORT).show();
					return;
				}
					
				
				// passo attraverso tutte le routes
				for(int i=0;i<result.size();i++){
					points = new ArrayList<LatLng>();
					lineOptions = new PolylineOptions();
					
					// recupero la i-esima route
					List<HashMap<String, String>> path = result.get(i);
					
					// Recupero tutti i punti nella i-esima route
					for(int j=0;j<path.size();j++){
						HashMap<String,String> point = path.get(j);	
						
						if(j==0){	// recupero la distanza
							distance = (String)point.get("distance");						
							continue;
						}else if(j==1){ // Recupero la durata
							duration = (String)point.get("duration");
							continue;
						}
						
						//prendo lat e longitudine  e creo variabile che metto nella variabile points
						double lati = Double.parseDouble(point.get("lat"));
						double lngi = Double.parseDouble(point.get("lng"));
						LatLng position = new LatLng(lati, lngi);	
						
						points.add(position);						
					}
					
					// aggiungo tutti i punti 
					lineOptions.addAll(points);
					lineOptions.width(3);
					lineOptions.color(Color.RED);	
					
				}
				
				tvDur.setText("Durata:   "+duration);
				tvLungh.setText("Km:     "+distance);
				
				//disegno la polyline dati i punti
				mMap.addPolyline(lineOptions);							
			}			
	    }   
	
	
	
	private String getDirectionsUrl(LatLng origin,LatLng dest){
		
		// PARTENZA in coordinate
		String str_origin = "origin="+origin.latitude+","+origin.longitude;
		// DESTINAZIONE in coordinate
		String str_dest = "destination="+dest.latitude+","+dest.longitude;		
					
		// Sensor--> ormai e' un parametro che non serve pi� 
		String sensor = "sensor=false";			
					
		// creo i parametri per il web server
		String parameters = str_origin+"&"+str_dest+"&"+sensor;
					
		// Output format
		String output = "json";
		
		// Creo l' url per il web service
		String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;

		return url;
	}

	@Override
	public void onPause() {
    	super.onPause();
    	mMapView.onPause();
    	this.finish();
    	
    	RicercaLavoro.groupContact.LastView2(regione, provincia,tag,username);
    	
	}
	
	
	
	private LatLng mypos(){
		//controllo che GPS sia attivo, se non lo fosse chiedo di attivarlo attraverso una alertDialog che rimanda alle impostazioni
		locationManager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

	    if (locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER )==false ) {
	        buildAlertMessageNoGps();
	    }else{
	    	LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE); 
	    	
	    	Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
	    	
	    	if(lm.getLastKnownLocation(LocationManager.GPS_PROVIDER)!=null){
		    	final LocationListener locationListener = new LocationListener() {
		    	    public void onLocationChanged(Location location) {
		    	        currentlong = location.getLongitude();
		    	        currentlat = location.getLatitude();
		    	    }
	
					@Override
					public void onProviderDisabled(String arg0) {
					}
	
					@Override
					public void onProviderEnabled(String provider) {	
					}
	
					@Override
					public void onStatusChanged(String provider,  int status, Bundle extras) {
		
					}
		    	};
		    	
		    	
		    	lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locationListener);
		    	
		    	currentlong= location.getLongitude();
		    	currentlat= location.getLatitude();
	    	}else{
	    		 LayoutInflater inflater = getLayoutInflater();
			        View layout = inflater.inflate(R.layout.toast_layout, (ViewGroup) findViewById(R.id.toast_layout_root));
			        ImageView image = (ImageView) layout.findViewById(R.id.immagine);
			        image.setImageDrawable(getResources().getDrawable(R.drawable.compass));
			        TextView text = (TextView) layout.findViewById(R.id.ToastTV);
			        text.setText("Impossibile trovare la posizione corrente!!");

			        Toast toast = new Toast(getApplicationContext());
			        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
			        toast.setDuration(Toast.LENGTH_LONG);
			        toast.setView(layout);
			        toast.show();
			        Navig.this.onPause();
	    	}
	    	
	    	
	    }
	    LatLng mypos = new LatLng(currentlat, currentlong);
	    return mypos;
	}
	
	
	//messaggio attivazione GPS
	private void buildAlertMessageNoGps() {
	   		    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
	   		    builder.setMessage("Il tuo GPS sembra essere disattivato, vuoi attivarlo?");
	   		    builder.setCancelable(false)
	   		  
	   		           .setPositiveButton("S�", new DialogInterface.OnClickListener() {
	   		               public void onClick( final DialogInterface dialog,  final int id) {
	   		                   startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
	   		               }
	   		           })
	   		           .setNegativeButton("No", new DialogInterface.OnClickListener() {
	   		        	   public void onClick(final DialogInterface dialog, final int id) {
	   		                    dialog.cancel();
	   		               }
	   		           });
	   		    
	               builder.setTitle("GPS DISATTIVATO");
	               // Icon for AlertDialog
	               builder.setIcon(R.drawable.location2);
	               builder.show();
	   		    
	   		}
	
	@Override
	public void onResume() {
   		super.onResume();
   		mMapView.onResume();
	}

	@Override
	public void onDestroy() {
    	super.onDestroy();
    	mMapView.onDestroy();
	}	
	

	public void searchPlace() {
		//RECUPERO POSIZONE CORRENTE DELL'UTENTE
		LatLng origin=mypos();
		

        MarkerOptions mo= new MarkerOptions();
        mo.position(origin)
        .title(posizione)
        .snippet(azienda)
        .icon(BitmapDescriptorFactory.fromResource(R.drawable.start_marker));
        mark2 = mMap.addMarker(mo);
        
        //REVERSE GEOCODING --> DA INDIRIZZO A COORDINATE LATLNG 
		Thread searchThread = new Thread("SerachThread") {
			@Override
			public void run() {
				
				/***   MESSAGE 
				 * Defines a message containing a description and arbitrary data object that can be sent to a Handler. 
				 * This object contains two extra int fields and an extra object field that allow you to not do allocations 
				 * in many cases.
				 * While the constructor of Message is public, the best way to get one of these is to call Message.obtain() 
				 * or one of the Handler.obtainMessage() methods, which will pull them from a pool of recycled objects  ***/
				Message message = mapHandler.obtainMessage();
				
				String addressToSearch = inputName;
				//elimino eventuali spazi e li sostituisco con il %20
				addressToSearch = addressToSearch.replace(" ", "%20");
				
				
										/*VEDI METODO PIU' SOTTO*/
                JSONObject addressInfo = getAddressInfo(addressToSearch);
						
                try
                {
					//se l'oggetto JSON ha status = OK quindi almeno un risultato e' stato trovato ed e' diverso da null
                	if (addressInfo != null && addressInfo.getString("status").equals("OK")) {
					    message.obj = addressInfo;
						mapHandler.sendMessage(message);
						
					}
					else
						mapHandler.sendEmptyMessage(MAP_MESSAGE_ID);
                }
                catch(JSONException e){
					mapHandler.sendEmptyMessage(MAP_MESSAGE_ID);
                }

				dismissDialog(PROGRESS_DIALOG_ID);			}
		};
		showDialog(PROGRESS_DIALOG_ID);
		//faccio partire il thread
		searchThread.start();
		//ASPETTO CHE FINISCA DI ESSERE "VIVO" PER CONTINUARE
		do{}while(searchThread.isAlive());
        
	}
	
	
    
	private final Handler mapHandler = new Handler() {

	@Override
	public void handleMessage(Message msg) {
			//SE MSG E L'OBJECT DEL MSG SONO DIVERSI DA NULL --> magari il msg contine qualcosa
			if(msg!=null && msg.obj!=null){
				JSONObject addressInfo = (JSONObject)msg.obj;
				//provo a estrarre le informazioni presenti nel message che e' stato passato
				try{
					
					/*	COME RECUPERARA LAT E LNG DA RISULTATO DEL GEOCODING
					 * 
					 * 	prendo l'oggetto JSON, 
					 * 		prendo i "results", 
					 * 			recupero il primo oggetto, 
					 * 				il campo geometry (che e' quello che contiene lat e lng)
					 * 					nel campo geometry prendo la parte che riguarda la "location"
					 * 						recupero i valori lat o lng
					 */
			         lng = ((JSONArray)addressInfo.get("results")).getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getDouble("lng");
			         lat = ((JSONArray)addressInfo.get("results")).getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getDouble("lat");                     
			       
			         MarkerOptions mo2= new MarkerOptions();
			         mo2.position(new LatLng(lat,lng))
			         .title(posizione)
			         .snippet(azienda)
			         .icon(BitmapDescriptorFactory.fromResource(R.drawable.finish_marker));
		        	mark = mMap.addMarker(mo2);
		        	
		        	LatLng destination = mark.getPosition();
		        	//voglio creare una lat e lng a meta' per posizioanre inizialmente la mappa
		        	double mean_lat = (double) ((currentlat+lat)/2);
		        	double mean_lng = (double) ((currentlong+lng)/2);
		            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mean_lat, mean_lng), 13.0f));
		            LatLng origin = mark2.getPosition();
		            
		            /**    DIRECTIONS  API  **/ 
		            String url= getDirectionsUrl(origin,destination);
		            
		            DownloadTask downloadTask = new DownloadTask();
		    		
		    		// Start downloading json data from Google Directions API
		            downloadTask.execute(url);
		        	
				}
				catch(JSONException e){
					Toast.makeText(Navig.this, "Non ho trovato nulla!", 
							   Toast.LENGTH_SHORT).show();
				}
			
			}else{
				Toast.makeText(Navig.this, "Non ho trovato nulla!", 
				   Toast.LENGTH_SHORT).show();
			}
		}

	};
	
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case PROGRESS_DIALOG_ID:
			ProgressDialog progressDialog = new ProgressDialog(this,
					ProgressDialog.STYLE_SPINNER);
			progressDialog.setIndeterminate(true);
			progressDialog.setTitle("Geocoding");
			progressDialog.setMessage("Sto cercando...");
			return progressDialog;
		default:
			return null;
		}
	}
	
	
	public static JSONObject getAddressInfo(String sAddress) {
	    //passo i dati con un GET al seguente indirizzo Http
		HttpGet httpGet = new HttpGet("http://maps.google.com/maps/api/geocode/json?address=" + sAddress + "&sensor=false");
	    HttpClient client = new DefaultHttpClient();
	    HttpResponse response;
	    StringBuilder stringBuilder = new StringBuilder();

	    try {
	    	//eseguo la richiesta httpGET
	        response = client.execute(httpGet);
	        HttpEntity entity = response.getEntity();
	        InputStream stream = entity.getContent();
	        int b;

	     /* creo la stringa dalla quale recupero poi le informazioni*/
	        while ((b = stream.read()) != -1) {
	            stringBuilder.append((char) b);
	        }
	    } catch (ClientProtocolException e) {
	    } catch (IOException e) {
	    }

	    JSONObject jsonObject = new JSONObject();
	    try {
	        jsonObject = new JSONObject(stringBuilder.toString());
	    } catch (JSONException e) {
	        e.printStackTrace();
	    }
	    return jsonObject;
	}	
	
	

}
