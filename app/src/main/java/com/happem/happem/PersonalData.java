package com.happem.happem;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;
import android.view.View;
import android.view.View.OnClickListener;
	
public class PersonalData extends Activity{
	private final static String PREF_LOG_PASS="Login_Dati_PASS";
	private final static  String PASSWORD = "";
	
	// Salt
		final static byte[] salt = {
		   (byte)0xc7, (byte)0x73, (byte)0x21, (byte)0x8c,
		   (byte)0x7e, (byte)0xc8, (byte)0xee, (byte)0x99
		};	
		final static int count = 20;
		final static PBEParameterSpec pbeParamSpec = new PBEParameterSpec(salt, count);
		SecretKeyFactory keyFac;
		PBEKeySpec pbeKeySpec;
		SecretKey pbeKey;
	
	
	@Override
	protected void onResume(){
		super.onResume();
		//quando fa il resume voglio che ricrei la pagina personale --> alcuni dati potrebbero essere cambiati
		this.onCreate(null);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.personaldatapagelayout);
		
		findViewById(R.id.RicercaLavoroTitolo);
		findViewById(R.id.tag);
		findViewById(R.id.provincia);
		findViewById(R.id.cognome);
		findViewById(R.id.indirizzo);
		findViewById(R.id.mail);
		findViewById(R.id.password);
		findViewById(R.id.username);
		findViewById(R.id.datadinascita);
		findViewById(R.id.telefono);
		final TextView nomeUser = (TextView) findViewById(R.id.nomeUser);
		final TextView cognomeUser = (TextView) findViewById(R.id.cognomeUser);
		final TextView indirizzoUser = (TextView) findViewById(R.id.indirizzoUser);
		final TextView passwordUser = (TextView) findViewById(R.id.passwordUser);
		final TextView usernameUser = (TextView) findViewById(R.id.usernameUser);
		final TextView datadinascitaUser = (TextView) findViewById(R.id.DatadinascitaUser);
		final TextView phoneUser = (TextView) findViewById(R.id.telefonoUser);
		final TextView mailUser = (TextView) findViewById(R.id.emailUser);
		
		ImageButton modify =  (ImageButton) findViewById(R.id.imageButton1);
		
		Intent datipassati = getIntent();
		String pkg=getPackageName();
	    String usernameIntent=datipassati.getStringExtra(pkg+".Username");  //prendiamo i dati
	    
	   
		PersonalDataThread t3 = new PersonalDataThread(usernameIntent);
		//t3.run();
		t3.start();
		do{}while(t3.isAlive());


			/**DEVO DECRIPTARE I DATI**/
			SharedPreferences pref_Pass = getSharedPreferences(PREF_LOG_PASS, Context.MODE_PRIVATE);
			String p = pref_Pass.getString(PASSWORD, "");
			createKeys(p);
			
			Encryption_Decryption ed = new Encryption_Decryption();
			
			//non necessario controllo perch� obbligatorio l'inserimento
			nomeUser.setText(ed.decrypt(t3.getNomeDB().toString(), pbeKey, pbeParamSpec).toString());

			cognomeUser.setText(ed.decrypt(t3.getCognomeDB(), pbeKey, pbeParamSpec).toString());
			
			usernameUser.setText(t3.getUserDB());

			passwordUser.setText(ed.decrypt(t3.getPassDB(), pbeKey, pbeParamSpec).toString());
			mailUser.setText(t3.getMailDB());
			
			final String data;
			/*campi non obbligatori--> e' necessario controllo 
			 * 		- se � uguale a null --> dico che il campo non e' stato inserito 
			 * 		- altrimenti, imposto il valore dopo averlo decifrato */
			if(t3.getDatadinascitaDB().equals("null")){
				datadinascitaUser.setText("Dato non inserito");
				datadinascitaUser.setTextColor(getResources().getColor(R.color.red));
				data=null;
			}else{

				datadinascitaUser.setText(ed.decrypt(t3.getDatadinascitaDB().toString(), pbeKey, pbeParamSpec));
				data= datadinascitaUser.getText().toString();
			}
			
			
			final String indirizzo;
			final String citta;
			/* SE L'INDIRIZZO O LA CITTA' NON SONO STATI INDICATI, ALLORA DICO CHE IL DATO NON E' STATO INSERITO*/
			if(t3.getIndirizzoDB().equals("null") | t3.getCittaDB().equals("null")){
				indirizzoUser.setText("Dato non inserito");
				indirizzoUser.setTextColor(getResources().getColor(R.color.red));
				if(t3.getIndirizzoDB().equals("null")){
					indirizzo=null;
				}else{
					indirizzo = ed.decrypt(t3.getIndirizzoDB().toString(), pbeKey, pbeParamSpec);
				}
				if(t3.getCittaDB().equals("null")){
					citta=null;
				}else{
					citta= ed.decrypt(t3.getCittaDB().toString(), pbeKey, pbeParamSpec);
				}
			}else{
				indirizzoUser.setText(ed.decrypt(t3.getIndirizzoDB().toString(), pbeKey, pbeParamSpec)+", "+ed.decrypt(t3.getCittaDB().toString(), pbeKey, pbeParamSpec));
				indirizzo = ed.decrypt(t3.getIndirizzoDB().toString(), pbeKey, pbeParamSpec);
				citta= ed.decrypt(t3.getCittaDB().toString(), pbeKey, pbeParamSpec);
			}
			
			final String telef;
			if(t3.getTelefonoDB().equals("null")){
				phoneUser.setText("Dato non inserito");
				phoneUser.setTextColor(getResources().getColor(R.color.red));
				 telef= null;
			}else{
				phoneUser.setText(ed.decrypt(t3.getTelefonoDB().toString(), pbeKey, pbeParamSpec));
				 telef =ed.decrypt(t3.getTelefonoDB().toString(), pbeKey, pbeParamSpec);
			}
			
			
			/*bottone di modifica --> passo tutti i dati nell'intent in modo da recuperarli nell'activity di destinazione */
			 modify.setOnClickListener(new OnClickListener(){
					final String pkg = getPackageName();

					@Override
					public void onClick(View arg0) {
						Intent i = new Intent(PersonalData.this, ModifyData.class);
						i.putExtra(pkg+".Nome", nomeUser.getText().toString());      //inseriamo i dati nell'intent
						i.putExtra(pkg+".Cognome", cognomeUser.getText().toString());
						i.putExtra(pkg+".Username", usernameUser.getText().toString());
						i.putExtra(pkg+".Password", passwordUser.getText().toString());
						i.putExtra(pkg+".Mail", mailUser.getText().toString());
						i.putExtra(pkg+".DataNascita", data);
						i.putExtra(pkg+".Indirizzo", indirizzo);
						i.putExtra(pkg+".Citta", citta);
						i.putExtra(pkg+".Telefono", telef);
						startActivity(i);
					}
				  
			    });
	}

	
	
public void createKeys(String s){
		
		try {
			char passwordArray[] = s.toCharArray();
			pbeKeySpec = new PBEKeySpec(passwordArray);
			
			keyFac = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
			pbeKey= keyFac.generateSecret(pbeKeySpec);   
			
	    } catch (Exception e) {
	        Log.e("PasswordBasedEncryptionActivity", "PBE secret key error");
	    }
	}
}