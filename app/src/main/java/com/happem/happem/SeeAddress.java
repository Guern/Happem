package com.happem.happem;


import java.io.IOException;
import java.io.InputStream;

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

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Point;


public class SeeAddress extends Activity {

	private final static int MAP_MESSAGE_ID = 1;
	private final static int PROGRESS_DIALOG_ID = 1;

	MapView mMapView;
	GoogleMap mMap;
	Marker mark;
	
	private String inputName;
	private String posizione;
	private String azienda;
	private String username;
	private String regione;
	private String provincia;
	private String tag[];
	double lat;
	double lng;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_LEFT_ICON);
		setContentView(R.layout.main_mapview);
		getWindow().setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.map);
		
		mMapView = (MapView) findViewById(R.id.mymapview);
		mMapView.onCreate(savedInstanceState);
    	
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
        tv.setText(inputName);
        searchPlace();
        
        
	}

	
	@Override
	public void onPause() {
    	super.onPause();
    	mMapView.onPause();
    	this.finish();
    	
    	RicercaLavoro.groupContact.LastView2(regione, provincia,tag,username);
    
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
		Thread searchThread = new Thread("SerachThread") {
			@Override
			public void run() {
				Message message = mapHandler.obtainMessage();
				
				String addressToSearch = inputName;
				addressToSearch = addressToSearch.replace(" ", "%20");
				
                JSONObject addressInfo = getAddressInfo(addressToSearch);
						
                try
                {
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
		searchThread.start();
	}
    
	private final Handler mapHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if(msg!=null && msg.obj!=null){
				JSONObject addressInfo = (JSONObject)msg.obj;
			
				try{
			         lng = ((JSONArray)addressInfo.get("results")).getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getDouble("lng");
			         lat = ((JSONArray)addressInfo.get("results")).getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getDouble("lat");                     
			         MarkerOptions mo= new MarkerOptions();
			         mo.position(new LatLng(lat,lng))
			         .title(posizione)
			         .snippet(azienda)
			         .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker));
			       
		        	mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat,lng), 16.0f));
		        	mark = mMap.addMarker(mo);
		        	mMap.setOnMarkerClickListener(new OnMarkerClickListener(){

						@Override
						public boolean onMarkerClick(final Marker marker){
							  // Questo fa saltellare il marker sualla sua posizione quando viene clickato
						    if (marker.equals(mark)) {
						        final Handler handler = new Handler();
						        final long start = SystemClock.uptimeMillis(); //Returns milliseconds since boot, not counting time spent in deep sleep.
						        /** A projection is used to translate between on screen location and geographic coordinates 
						            on the surface of the Earth (LatLng). 
						            Screen location is in screen pixels (not display pixels) with respect to the top left 
						            corner of the map (and not necessarily of the whole screen).**/
						        Projection proj = mMap.getProjection();
						        Point startPoint = proj.toScreenLocation(new LatLng(lat,lng));
						        startPoint.offset(0, -100);
						        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
						        final long duration = 2000;
						        /** An interpolator defines the rate of change of an animation.
						            This allows the basic animation effects (alpha, scale, translate, rotate) to be accelerated,
						            decelerated, repeated, etc.*/
						        final Interpolator interpolator = new BounceInterpolator();
						        handler.post(new Runnable() {
						            @Override
						            public void run() {
						                long elapsed = SystemClock.uptimeMillis() - start;
						                /* getInterpolation(float ...) 
						                       Maps a value representing the elapsed fraction of an animation to a value that 
						                       represents the interpolated fraction. 
						                       This interpolated value is then multiplied by the change in value of an 
						                       animation to derive the animated value at the current elapsed animation time.
						                 */
						                float t = interpolator.getInterpolation((float) elapsed / duration);
						                double lng1 = t * lng + (1 - t) * startLatLng.longitude;
						                double lat1 = t * lat + (1 - t) * startLatLng.latitude;
						                marker.setPosition(new LatLng(lat1, lng1));
						                if (t < 1.0) {
						                    // Post again 10ms later.
						                    handler.postDelayed(this, 10);
						                }
						            }
						        });
						    }
						    /* ritorniamo false per indicare che non abbiamo ancora consumeto l'evento e vogliamo che sia il sia
						       il comportamento di default */
						     return false;
						}
		        		
		        	});
		        	
		        	
				}
				catch(JSONException e){
					Toast.makeText(SeeAddress.this, "Found nothing. Retry!", 
							   Toast.LENGTH_SHORT).show();
				}
			
			}else{
				Toast.makeText(SeeAddress.this, "Found nothing. Retry!", 
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
			progressDialog.setMessage("Sto cercando ...");
			return progressDialog;
		default:
			return null;
		}
	}
	
	public static JSONObject getAddressInfo(String sAddress) {
	    HttpGet httpGet = new HttpGet("http://maps.google.com/maps/api/geocode/json?address=" + sAddress + "&sensor=false");
	    HttpClient client = new DefaultHttpClient();
	    HttpResponse response;
	    StringBuilder stringBuilder = new StringBuilder();

	    try {
	        response = client.execute(httpGet);
	        HttpEntity entity = response.getEntity();
	        InputStream stream = entity.getContent();
	        int b;
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
