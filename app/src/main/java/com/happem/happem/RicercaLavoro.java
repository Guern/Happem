package com.happem.happem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.app.ActivityGroup;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class RicercaLavoro extends ActivityGroup {
	
	/**  ACTIVITY GROUP  
	 * A screen that contains and runs multiple embedded activities. **/
	
	
	private String regioneSelezionata;
	private String provinciaSelezionata;
	private String p[];
	private LocationManager locationManager;
	private double currentlat;
	private double currentlong;
	private List<String> regioniList = new ArrayList<String>();
	private Spinner spinner2;
	private int posizione_regione;
	private Context context;
	
	/*mi serve per caricare views in questa activity dalle altre activity*/
	public static RicercaLavoro groupContact; 
	public int group=-1;
	private final static String PREF_LOG_USER="Login_Dati_USER";
	private final static  String UTENTE="";
	private String usernameIntent;
	
	
	@Override
 	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ricercalavoro);
        /*METTO REFERENZA A QUESTA SPECIFICA ACTIVITY*/
		groupContact = this;  
		Intent datipassati = getIntent();
		
		/*SE username non arriva nell'intent allora cerco nelle shared preferences*/
		if(datipassati!=null){
			String pkg=getPackageName();
			usernameIntent=datipassati.getStringExtra(pkg+".Username");
		}else{
			SharedPreferences pref_User = getSharedPreferences(PREF_LOG_USER, Context.MODE_PRIVATE);
			usernameIntent = pref_User.getString(UTENTE, "");
		}
	   
	    
	    
		TextView titolo = (TextView) findViewById(R.id.RicercaLavoroTitolo);
		TextView tag = (TextView) findViewById(R.id.tag);
		TextView regioneText = (TextView) findViewById(R.id.regioneText);
		Button buttonEffettuaRicerca = (Button) findViewById(R.id.buttonRicercaLavoro);
		final Spinner spinner1 = (Spinner) findViewById(R.id.spinner1);
		ImageButton imagebutton = (ImageButton) findViewById(R.id.IBlocal);
		spinner2 = (Spinner) findViewById(R.id.spinner2);
		regioniList = new ArrayList<String>();

		
		//popolo spinner1 --> REGIONE
			// 1) recupero elenco regioni
		
		GetRegioniThread grt = new GetRegioniThread();
		grt.start();
		do{}while(grt.isAlive());	
		
		    //2) le inserisco nella lista dal momento che posso usare questa info per recuperare la posizione e
			//la successiva ricerca della provincia sara' quindi piu' agevole
		
		for(int i=0; i<grt.getRegioni().length; i++){
			regioniList.add(grt.getRegioni()[i]);
		}
			//3) popolo lo spinner 
		
				/**ARRAY ADAPTER--> tipo di adapter
				 * An Adapter object acts as a bridge between an AdapterView and the underlying data for that view. 
				 * The Adapter provides access to the data items. 
				 * The Adapter is also responsible for making a View for each item in the data set.*/
		
		ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<String>(getApplicationContext(), R.layout.custom_spinner, grt.getRegioni());
		dataAdapter1.setDropDownViewResource(R.layout.custom_spinner);
		spinner1.setAdapter(dataAdapter1);

		//POPOLARE  LO SPINNER 2 --> PROVINCE -->UNA VOLTA SELEZIONATO ITEM NELLO SPINNER 2 --> PRENDO LE PROVINCE DI QUELLA REGIONE CERCANDOLE NEL DATABASE
		spinner1.setOnItemSelectedListener(new OnItemSelectedListener(){
		
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
				//due query diverse se posizione � 0 (la voce selezionata � "Tutte le regioni")
				//oppure se � stata selezionata una regione 
				posizione_regione=position;
				spinner2 = popoloSpinnerProvince(position, spinner2);
				
				spinner2.setOnItemSelectedListener(new OnItemSelectedListener(){

					@Override
					public void onItemSelected(AdapterView<?> arg0,View arg1, int arg2, long arg3) {
						provinciaSelezionata= p[arg2];
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
						//DECISIONE PRESA DI DEFAULT
						 provinciaSelezionata="Tutte le province";
					}
					
				});
			}

			
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				Log.i("ERRORE", "NON HAI SELEZIONATO NULLA!!!!");
			}
			
		});
		
		/*per creare striga dei tag 
		 * --> recuperare array di stringe da annunci DB
		 * --> per ogni valore dell'array:
		 * 		-> separo i valori con virgola e li metto in un array
		 * 		-> leggo i valori dell'array di tag e inserisco solo quelle strighe che non sono contenute nell'array dei tag
		 * --> aggiungo array a multiautocompletetextview
		 * */
		
		final MultiAutoCompleteTextView tagMACTW = (MultiAutoCompleteTextView) findViewById(R.id.multiAutoCompleteTextView1);
		GetTagThread gtt= new GetTagThread();
		gtt.start();
		do{}while(gtt.isAlive());
		
		String[] tag_recuperati= gtt.getTag();
		/*creo array dei tag*/
		int count2=0;
		for(int i=0; i<tag_recuperati.length;i++){
			if(tag_recuperati[i]!=null){
				count2=count2+1;
			}
		}
		String[] t= new String[count2];
		int free_position=0;
		for(int i=0; i<tag_recuperati.length; i++){
			if(tag_recuperati[i]!=null){
				t[free_position]=tag_recuperati[i].trim();
				free_position++;
			}
		}
		
		
		/** ARRAY ADAPTER 
		 * It's an object that, by accessing differnent kinds of information, allows to "get data out" of an array **/
		
		ArrayAdapter<String> adapter4 = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line, t);
        tagMACTW.setAdapter(adapter4);
        tagMACTW.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
        
        buttonEffettuaRicerca.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				//recupero parametri di ricerca--> devo solo recuperare i tag inseriti perche' regione e provincia gia' li ho
				
				/*creo array dei tag inseriti --> prendo stringa scritta, elimino gli spazi, usando il metodo trim(), 
				 * e faccio lo split definendo come paramentre la virgola perche' ho usato un CommaTokenizer*/
				String[] tagRicerca=tagMACTW.getText().toString().trim().split(",");
				for(int i=0; i<tagRicerca.length; i++){
					tagRicerca[i].trim();
				}
				
				//invio i dati attravero un intent alla nuova activity dove stampero' i risultati
				Intent i = new Intent (v.getContext(), PaginaRisultati.class);
				i.putExtra(getPackageName()+".Regione", regioneSelezionata);
				i.putExtra(getPackageName()+".Provincia", provinciaSelezionata);
				i.putExtra(getPackageName()+".Tag", tagRicerca);
				i.putExtra(getPackageName()+".Username", usernameIntent);
				
				//aggiungo a tabella ricerca recenti i parametri inseriti
				MyDBHelper dbHelper2 = new MyDBHelper(RicercaLavoro.this,"JobAroundU_DB", null, 1);
				final SQLiteDatabase db2;
				
				String sqlMyRecentSearch="INSERT INTO  RicercheRecenti (Regione, Provincia, Tag, User) VALUES ('"+regioneSelezionata+"', '"+provinciaSelezionata+"', '"+tagMACTW.getText().toString().trim()+"', '"+getIntent().getStringExtra(getPackageName()+".Username")+"');";
				
				db2=dbHelper2.getWritableDatabase();
				db2.execSQL(sqlMyRecentSearch);	
				db2.close();				
				
				replaceContentView("RisultatiRicerca", i);
			}
        	
        });
        
        /** AUTOLOCALIZZAZIONE --> impostazione automatica della regione e della provincia**/
        imagebutton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				//controllo che GPS sia attivo --> se non lo creo un alert dialog 
				 locationManager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

			    if (locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER )==false ) {
			        buildAlertMessageNoGps();
			    }else{
			    	
			    	/**  LOCATION MANAGER 
			    	 * It's a system service, which provides API functionalites to determine location and bearing of 
			    	 * the service (if available) 
			    	 * 
			    	 * Once the application has obtained the instance it can 
			    	 * 			- query for the list of all location provides, 
			    	 * 			- register updates 
			    	 * 			- register for a given intent to be fired if the device comes within a given location**/
			    	
			    	/**  LOCATION   
			    	 * A location can consist of a latitude, longitude, timestamp, and other information such as 
			    	 * bearing, altitude and velocity.
			    	 * All locations generated by the LocationManager are guaranteed to have a valid latitude, longitude, 
			    	 * and timestamp (both UTC time and elapsed real-time since boot), all other parameters are optional.**/
			    	
			    	LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE); 
			    	
			    	/** getLastKnownLocation(Provider) 
			    	 * Returns a Location indicating the data from the last known location fix obtained from the given 
			    	 * provider.This can be done without starting the provider. 
			    	 * Note that this location could be out-of-date, for example if the device was turned off and moved 
			    	 * to another location.
			    	 * 
			    	 * If the provider is currently disabled, null is returned*/
			    	
			    	Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			    	if(lm.getLastKnownLocation(LocationManager.GPS_PROVIDER)!=null){
			    			Log.i("LOCATION PRESENT", "location present -->   LAT:"+location.getLatitude()+ "   LNG:   "+location.getLongitude());
					    	final LocationListener locationListener = new LocationListener() {
					    	   /*QUANDO LA LOCALIZZAZIONE VIENE CAMBIATA*/
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
								public void onStatusChanged(String provider, int status, Bundle extras) {									
								}
					    	};
					    	
					    	/** public void requestLocationUpdates (String provider, long minTime, float minDistance, LocationListener listener)
					    	 * Register for location updates using the named provider, and a pending intent.
					    	 * Parameters:
					    	 * 			- provider	    the name of the provider with which to register
					    	 * 			- minTime	    minimum time interval between location updates, in milliseconds
					    	 * 			- minDistance	minimum distance between location updates, in meters
					    	 * 			- listener	    a LocationListener whose onLocationChanged(Location) method will be called for each location update*/
					    	lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locationListener);
					    	
					    	currentlong= location.getLongitude();
					    	currentlat= location.getLatitude();
					    	
					    	/** GEOCODER 
					    	 * A class for handling geocoding and reverse geocoding. 
					    	 * 
					    	 * Geocoding is the process of transforming a street address or other description of a location 
					    	 * into a (latitude, longitude) coordinate. 
					    	 * 
					    	 * Reverse geocoding is the process of transforming a (latitude, longitude) coordinate into a 
					    	 * (partial) address. The amount of detail in a reverse geocoded location description may vary, 
					    	 * for example one might contain the full street address of the closest building, while another 
					    	 * might contain only a city name and postal code. 
					    	 * 
					    	 * The Geocoder class requires a backend service that is not included in the core android framework. 
					    	 * The Geocoder query methods will return an empty list if there no backend service in the platform. 
					    	 * 
					    	 * Use the isPresent() method to determine whether a Geocoder implementation exists. */
					    	Geocoder gc = new Geocoder(v.getContext(), Locale.ITALIAN);
					    	if(gc.isPresent()){
					    		  List<Address> list = null;
								try {
									
									/** getFromLocation(Lat, Lng, maxResults)
									 * 
									 * Returns an array of Addresses that are known to describe the area immediately 
									 * surrounding the given latitude and longitude. 
									 * 
									 * The returned addresses will be localized for the locale provided to this class's 
									 * constructor.
									 * 
									 * The returned values may be obtained by means of a network lookup*/
									//indirizzo da lat e long correnti
									list = gc.getFromLocation(currentlat, currentlong, 1);
								} catch (IOException e) {
									e.printStackTrace();
								}
								  
								  //prendo solo il primo risultato anche ce ne fossero di piu'
					    		  final Address address = list.get(0);
		
					    		  ArrayAdapter myAdap1 = (ArrayAdapter) spinner1.getAdapter(); //cast to an ArrayAdapter
					    		  /*SET LA POSIZIONE DELLO SPINNER CHE CORRISPONDE ALLA REGIONE IN CUI SI TROVA L'UTENTE AVENDOLA RICAVATA
					    		   * CON IL REVERSE GEOCODING*/
					    		                                                 /** address.getAdminArea() --> regione */
					    		  final int spinnerPosition = myAdap1.getPosition(address.getAdminArea());
					    		  Log.i("AUTOLOC", "Regione:  "+address.getAdminArea());
					    		  spinner1.setSelection(spinnerPosition);
					    		  //una volta selezionato un item dello spinner contente le regioni, popolo il secondo spinner 
					    		  spinner1.setOnItemSelectedListener(new OnItemSelectedListener(){
		
									@Override
									public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
										  spinner2 = popoloSpinnerProvince(spinnerPosition,spinner2);
							    		  ArrayAdapter myAdap2 = (ArrayAdapter) spinner2.getAdapter(); //cast to an ArrayAdapter
							    		                                             /** address.getSubAdminArea() --> provincia*/
							    		  int spinnerPosition2 = myAdap2.getPosition(address.getSubAdminArea());
							    		  //set spinner alla posizone corrispondente alla provincia
							    		  spinner2.setSelection(spinnerPosition2, true);   
							    		  Log.i("AUTOLOC", "Posizione:    "+address.getSubAdminArea());
										
									}
		
									@Override
									public void onNothingSelected(AdapterView<?> arg0) {								
									}
					    			   
					    		   });
					    		  
					    		  
					    		}
					    	}else{
					    		/* SE NON E' STATO POSSIBILE RECUPERARE L'INFO SULLA LOCALIZZAZIONE CORRENTE */
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
					    	}	
					    }
			    }
        	
        });
        
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
            // Icona per AlertDialog
            builder.setIcon(R.drawable.location2);
            builder.show();
		    
		}
	
	
	//CARICO LA NUOVA ACTIVITY ALL'INTERNO DEL CORPO DEL TAB
	public void replaceContentView(String id, Intent newIntent) {
		PersonalPage.perspage.getTab().setCurrentTab(1);
		View view = getLocalActivityManager().startActivity(id,newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)).getDecorView();
		this.setContentView(view);
	}
	
    
    
	private Spinner popoloSpinnerProvince(int position, Spinner spinner){
		Spinner spinner2 = (Spinner) findViewById(R.id.spinner2);
		spinner2=spinner;
		if(position != 0){
			regioneSelezionata= regioniList.get(position).toString();
			Log.i("regione selezionata", "Inserita nella query province     "+regioneSelezionata);
			GetProvinceThread gpt = new GetProvinceThread(regioneSelezionata);
			gpt.start();
			do{}while(gpt.isAlive());
			int count=0;
			
			//tolgo valori null e creo un array "pulito"
			for(int i=0; i<gpt.province.length;i++){
				if(gpt.province[i] != null){
					count=count+1;
				}
			}
			if(count!=0){
				p = new String[count];
				for(int i=0; i<count;i++){
					//metto in posizione 0 la ricerca per tutte le province della regione
					//in posizione 1 e successive le province estratte
					if(i==0){
						p[0]="Tutte le province";
					}else{
						p[i]=gpt.province[i-1].toString();
					}
				}
			
			}else{
				count=1;
				p = new String[count];
				p[0] = "Nessuna Provincia Trovata";
				
			}
			
			
			
		}else{
			//--> la voce "Tutte le regioni" � stata selezionata
			//--> unica possibilit� � tutte le province
				p = new String[1];
				p[0] = "Tutte le province";
				regioneSelezionata="Tutte le regioni";
				
		}
		
		ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(getApplicationContext(), R.layout.custom_spinner, p);
		dataAdapter2.setDropDownViewResource(R.layout.custom_spinner);
		
		spinner2.setAdapter(dataAdapter2);
		
		return spinner2;
		
		
	}

	
	public void setGroup(int i){
		group=i;
	}
	

	public void LastView2(String regione, String provincia, String[] tag, String username) {
		
		Intent i = new Intent(this, PaginaRisultati.class);
		i.putExtra(getPackageName()+".Regione", regione);
    	i.putExtra(getPackageName()+".Provincia", provincia);
    	i.putExtra(getPackageName()+".Tag", tag);
    	i.putExtra(getPackageName()+".Username", username);

    	replaceContentView("PagRis", i);
	}
	
	

}
