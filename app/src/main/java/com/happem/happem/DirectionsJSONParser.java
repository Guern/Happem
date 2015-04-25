package com.happem.happem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

public class DirectionsJSONParser {
	/*RICEVE UN JSON OBJECT E RITORNA UNA LITA DI LISTE CONTENENTI LATITUDINE E LONGITUDINE */
	
	public List<List<HashMap<String,String>>> parse(JSONObject jObject){
		
		List<List<HashMap<String, String>>> routes = new ArrayList<List<HashMap<String,String>>>() ;
		JSONArray jRoutes = null;
		JSONArray jLegs = null;
		JSONArray jSteps = null;	
		JSONObject jDistance = null;
		JSONObject jDuration = null;
		
		try {			
			
			jRoutes = jObject.getJSONArray("routes");
			
			/** traverso tutte le strade */
			for(int i=0;i<jRoutes.length();i++){
				Log.i("ROUTES", jRoutes.get(i).toString());	
				/* LEGS 
				  legs[] contains an array which contains information about a leg of the route, 
				  between two locations within the given route. A separate leg will be present 
				  for each waypoint or destination specified. (A route with no waypoints will 
				  contain exactly one leg within the legs array.) Each leg consists of a series of steps.*
				 */
				
				/**    LEGS  
				  Each element in the legs array specifies a single leg of the journey from the origin to the destination 
				  in the calculated route. 
				  For routes that contain no waypoints, the route will consist of a single "leg," but for routes that define
				  one or more waypoints, the route will consist of one or more legs, corresponding to the specific legs of 
				  the journey.
				  
				  Each leg within the legs field(s) may contain the following fields:
				  
				  			- steps[]  --> contains an array of steps denoting information about each separate step of the leg of 	
				  					       the journey. 
				  			- distance --> Indicates the total distance covered by this leg, as a field with the following 
				  							elements:
				  								- value indicates the distance in meters
				  								- text contains a human-readable representation of the distance, 
				  									   displayed in units as used at the origin.
				  						   These fields may be absent if the distance is unknown.
				  			- duration --> Indicates the total duration of this leg, as a field with the following elements:
				  								- value indicates the duration in seconds.
				  								- text contains a human-readable representation of the duration.
				  						   These fields may be absent if the duration is unknown. 
				  **/
				
			
				
				jLegs = ( (JSONObject)jRoutes.get(i)).getJSONArray("legs");				

				List<HashMap<String, String>> path = new ArrayList<HashMap<String, String>>();				
				
				/** SCORRO TUTTI I LEGS */
				for(int j=0;j<jLegs.length();j++){
					Log.i("LEGS", jLegs.get(j).toString());

					/* reupero la distanza del leg j-esimo */
					jDistance = ((JSONObject) jLegs.get(j)).getJSONObject("distance");
					HashMap<String, String> hmDistance = new HashMap<String, String>();
					hmDistance.put("distance", jDistance.getString("text"));
					
					/* recupero la durata del leg j-esimo */
					jDuration = ((JSONObject) jLegs.get(j)).getJSONObject("duration");
					HashMap<String, String> hmDuration = new HashMap<String, String>();
					hmDuration.put("duration", jDuration.getString("text"));
					
					/* aggiungo l'oggetto distanza alla lista */
					path.add(hmDistance);
					
					/* aggiungo l'oggetto durata alla lista*/
					path.add(hmDuration);					
					
					/** STEP
					  Each element in the steps array defines a single step of the calculated directions. 
					  A step is the most atomic unit of a direction's route, containing a single step describing a specific, 
					  single instruction on the journey */
					
					/* per ogni leg scorro gli step */
					jSteps = ( (JSONObject)jLegs.get(j)).getJSONArray("steps");
					
					for(int k=0;k<jSteps.length();k++){
						String polyline = "";
						Log.i("STEPS", jSteps.get(k).toString());
						/*in ogni step e' presente un campo polyline in cui sono presenti i punti*/
						polyline = (String)((JSONObject)((JSONObject)jSteps.get(k)).get("polyline")).get("points");
						//la stringa e' codificata e deve essere decodificata per essere letta, questo crea una lista di elementi
						//LatLng che possono essere disegnati per putni 
						List<LatLng> list = decodePoly(polyline);
						
						/** per disegnare la polyline devo attraversare tutti i punti recuperati e inserirli in una HashMap */
						for(int l=0;l<list.size();l++){
							HashMap<String, String> hm = new HashMap<String, String>();
							hm.put("lat", Double.toString(((LatLng)list.get(l)).latitude) );
							hm.put("lng", Double.toString(((LatLng)list.get(l)).longitude) );
							path.add(hm);						
						}								
					}					
				}
				//aggiungo alla variabile routes il path
				routes.add(path);
			}
			
		} catch (JSONException e) {			
			e.printStackTrace();
		}catch (Exception e){			
		}
		
		return routes;
	}	
	
	
	/**
	  Method to decode polyline points 
	  Courtesy : jeffreysambells.com/2010/05/27/decoding-polylines-from-google-maps-direction-api-with-java 
	 */
	
	/** METODO USATO DA GOOGLE PER CODIFICARE LAT E LNG --> PER DECODIFICARE LO DEVO FARE AL CONTRARIO 
	 * The steps for encoding such a signed value are specified below.
	 * 			1) Take the initial signed value
	  						-179.9832104
	 * 			2) Take the decimal value and multiply it by 1e5, rounding the result:
	  						-17998321
	 * 			3) Convert the decimal value to binary. 
	 * 			   Note that a negative value must be calculated using its two's complement by inverting the binary value 
	 * 			   and adding one to the result:
	 						00000001 00010010 10100001 11110001 --> binary number
	  						11111110 11101101 01011110 00001110 --> it's negative so I've to comupute it's complement
	 						11111110 11101101 01011110 00001111 --> add value 1 to result
	 * 			4) Left-shift the binary value one bit:
	  						11111101 11011010 10111100 00011110
	 * 			5) If the original decimal value is negative, invert this encoding:
	  						00000010 00100101 01000011 11100001
	 * 			6) Break the binary value out into 5-bit chunks (starting from the right hand side):
	  						00001 00010 01010 10000 11111 00001
	 * 			7) Place the 5-bit chunks into reverse order:
	  						00001 11111 10000 01010 00010 00001
	 * 			   OR each value with 0x20 if another bit chunk follows: (add a 1 in position number 6 of each chunk = 32)
	  			            100001 111111 110000 101010 100010 000001
	 * 			8) Convert each value to decimal:
							33 63 48 42 34 1
	 *			9) Add 63 to each value:
							96 126 111 105 97 64
	 *		   10) Convert each value to its ASCII equivalent:
			   				`~oia@ 
	 **/
	
	
    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<LatLng>();
       //creo indice   //creo variabile che mi dice quanto e' lunga la stringa  
        int index = 0, len = encoded.length();
       
        int lat = 0, lng = 0;
        //scorro la stringa finche' indice e' minore del massimo valore
        while (index < len) {
            int b, shift = 0, result = 0;
            /**  primo valore e' la lat **/ 
            do {
            	//PASSAGGIO 10, 9  --> trasformo da char a numero e tolgo 63
                b = encoded.charAt(index++) - 63;
                /*PASSAGGIO 8,7,6,5*/
                /* OPERATION 1)    (b & 0x1f) -->  performs a logical AND operation between b and  0xf1. 
                           		                   This means: return the last 5 bits of b
                 * OPERATION 2)    << shift   -->  shifts to the left an amount of shift bits the result of  A operation. 
                  								   This means: shift the last 5 bits of b an amount of shift bits to the left.
                 * OPERATION 3)   result |= B -->  assigns to result variable the result of perform a logical OR operation
                                                   between result itself and the result of B operation. This means: perform a 
                                                   logical OR between result and the last 5 bits of b shifted to the left an 
                                                   amount of shift bits, and then assign the result to result variable.*/
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            		// PASSAGGIO 4,3: L'OPERATORE ~ FA IL COMPLEMENTO A UNO e FA LO SHIFT DI UNA POSIZONE DELLA VARIABILE 
            		// RESULT SE L'OPERAZIONE DI AND TRA IL RISULTATO E 1 DA' UN RISULTATO DEIVERO DA ZERO, 
            	    // ALTRIMENT FA SOLO LO SHIFT
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;
            /** secondo valore che recuper e' la lng  e devo rifare gli stessi passaggi **/
            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            //creo variabile LatLng e l'aggiungo alla polyline dopo aver diviso ogni valore per 10^5 
            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
            
            //vado avanti a decodificare
        }

        return poly;
    }
}