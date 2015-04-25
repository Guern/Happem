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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;
import android.view.View.OnClickListener;

	
public class ModifyData extends Activity{
	
	// Salt
		final byte[] salt = {
		   (byte)0xc7, (byte)0x73, (byte)0x21, (byte)0x8c,
		   (byte)0x7e, (byte)0xc8, (byte)0xee, (byte)0x99
		};	
		final int count = 20;
		private final PBEParameterSpec pbeParamSpec = new PBEParameterSpec(salt, count);
		private SecretKeyFactory keyFac;
		private PBEKeySpec pbeKeySpec;
		private SecretKey pbeKey;
		
		
		
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.modify_personal_data_layout);
		
		
		final EditText nomeUser = (EditText) findViewById(R.id.editNomeSUser);
		final EditText cognomeUser = (EditText) findViewById(R.id.editCognomeSUser);
		final EditText indirizzoUser = (EditText) findViewById(R.id.indirizzoSUser);
		final EditText passwordUser = (EditText) findViewById(R.id.password1SUser);
		final EditText passwordUser2= (EditText) findViewById(R.id.password2SUser);
		final EditText usernameUser = (EditText) findViewById(R.id.usermameSUser);
		final EditText datadinascitaUser = (EditText) findViewById(R.id.dObSUser);
		final EditText cittaUser = (EditText) findViewById(R.id.cittaSUser);
		final EditText phoneUser = (EditText) findViewById(R.id.telefonoSUser);
		final EditText mailUser = (EditText) findViewById(R.id.mailSUser);
		
		ImageButton modify=  (ImageButton) findViewById(R.id.imageButton1);
		
		
		Intent datipassati = getIntent();
		final String pkg=getPackageName();
		
		//prendiamo i dati passati dall'intent e set del campo edit corrispondente+controllo se null oppure no
		String usernameIntent=datipassati.getStringExtra(pkg+".Username");  
	    usernameUser.setText(usernameIntent);
	    
	    String nomeIntent = datipassati.getStringExtra(pkg+".Nome");
	    nomeUser.setText(nomeIntent);
	    
	    String cognomeIntent = datipassati.getStringExtra(pkg+".Cognome");
	    cognomeUser.setText(cognomeIntent);
	    
	    String passwordIntent= datipassati.getStringExtra(pkg+".Password");
	    passwordUser.setText(passwordIntent);
	    passwordUser2.setText(passwordIntent);
	    
	    String mailIntent = datipassati.getStringExtra(pkg+".Mail"); 
	    mailUser.setText(mailIntent);
	    
	    String dataIntent = datipassati.getStringExtra(pkg+".DataNascita");// controllo se null oppure no
	    if(dataIntent!=null){
	    	datadinascitaUser.setText(dataIntent);
	    }
	    
	    String indirizzoIntent = datipassati.getStringExtra(pkg+".Indirizzo");// 
	    if(indirizzoIntent!=null){
	    	indirizzoUser.setText(indirizzoIntent);
	    }
	    
	    String cittaIntent= datipassati.getStringExtra(pkg+".Citta");//
	    if(cittaIntent!=null){
	    	cittaUser.setText(cittaIntent);
	    }
	    
	    String telefonoIntent=datipassati.getStringExtra(pkg+".Telefono");//
	    if(telefonoIntent!=null){
	    	phoneUser.setText(telefonoIntent);
	    }
	      
	    //quando clic su bottone salva aggiorno i dati sul database remoto
	    modify.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				//controllo che i campi obbligatori siano riempiti 
				//altrimenti stampo errore (se non obbligatori metto il campo a null)
				boolean nOK=false;
				boolean cOK=false;
				String nomeU = nomeUser.getText().toString();
				if(nomeU.equals("")){
					nomeUser.setError("Inserisci il nome");
				}
				else{
					nOK=true;
				}
				
				String cognomeU = cognomeUser.getText().toString();
				if(cognomeU.equals("")){
					cognomeUser.setError("Inserisci il cognome");
				}else{ 
					cOK=true;
				}
				
				
				String datadinascitaU = datadinascitaUser.getText().toString();
				if(datadinascitaU.equals("")){
					datadinascitaU=null;
				}
				
				String cittaU = cittaUser.getText().toString();
				if(cittaU.equals("")){
					cittaU=null;
				}
				
				String indirizzoU = indirizzoUser.getText().toString();
				if(indirizzoU.equals("")){
					indirizzoU=null;
				}
				
				String telefonoU = phoneUser.getText().toString();
				if(telefonoU.equals("")){
					telefonoU=null;
				}
				
				boolean emOK=false;
				String emailU = mailUser.getText().toString();
				if(emailU.equals("")){
					mailUser.setError("Inserisci l'indirizzo email");
				}else{
					emOK=true;
				}
				
				boolean uOK=false;
				String user = usernameUser.getText().toString();
				if(user.equals("")){
					usernameUser.setError("Inserisci l'Username");
				}else{
					uOK=true;
				}
				
				String password1 = passwordUser.getText().toString();
				if(password1.equals("")){
					passwordUser.setError("Inserisci la password");
				}
				
				String password2 = passwordUser2.getText().toString();
				if(password2.equals("")){
					passwordUser2.setError("Inserisci la password un'altra volta");
				}
				
				//controllo uguaglianza tra le password
				boolean pOK=false;
					//le password non sono stringe vuote
				if(password1.equals("")==false && password2.equals("")==false){
					if(password1.equals(password2)==true){
						pOK=true;
					}
					else{
						passwordUser.setError("Le password non coincidono");
						passwordUser2.setError("Le password non coincidono");
					}
				}
				
				if(pOK==true && uOK==true && emOK==true && nOK==true && cOK==true){
					Encryption_Decryption ed = new Encryption_Decryption();
					//creo chiavi eventualmente con la nuova password e aggiorno le shared preferences
					createKeys(password1);
					nomeU = ed.encrypt(nomeU, pbeKey, pbeParamSpec);
					cognomeU = ed.encrypt(cognomeU, pbeKey, pbeParamSpec);
					
					if(datadinascitaU!=null){
						datadinascitaU = ed.encrypt(datadinascitaU, pbeKey, pbeParamSpec);
					}
					if(cittaU!=null){
						cittaU = ed.encrypt(cittaU, pbeKey, pbeParamSpec);
					}
					if(indirizzoU!=null){
						indirizzoU=ed.encrypt(indirizzoU, pbeKey, pbeParamSpec);
					}
					
					if(telefonoU!=null){
						telefonoU= ed.encrypt(telefonoU, pbeKey, pbeParamSpec);
					}
					password1 = ed.encrypt(password1, pbeKey, pbeParamSpec);
					
			        SharedPreferences prefers_Pass = getSharedPreferences("Login_Dati_PASS",Context.MODE_PRIVATE);
					SharedPreferences.Editor editor_Pass = prefers_Pass.edit();
					editor_Pass.putString("", password1);
					editor_Pass.commit();
					
					ModifyDataThread t1 = new ModifyDataThread(nomeU, cognomeU, datadinascitaU, indirizzoU, cittaU, telefonoU, emailU, user, password1  );
					Log.i("MODYFYDATAPAGE","MODIFICA DATI PERSONALI");
					
					t1.start();
					do{}while(t1.isAlive());
					
					Context context = getApplicationContext();
					CharSequence text = "Modifiche Effettuate Con Successo!";
					int duration = Toast.LENGTH_LONG;
					Toast toast = Toast.makeText(context, text, duration);
					toast.show();
					Intent i = new Intent(ModifyData.this, Login.class);
					i.putExtra(pkg+".Username", user);
					i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(i);
					
				}
			}
	    	
	    });
	    

	}
	
	
	
	//METODO PER GENERARE LE CHIAVI PER DECRIPTARE
		public void createKeys(String s){
			/** In order to use Password-Based Encryption (PBE) as defined in PKCS5, a salt and an iteration count need to be 
			 defined!! The same salt and iteration count that are used for encryption must be used for decryption*/
			try {
				char passwordArray[] = s.toCharArray();
				// Convert user�s password into a SecretKey object, using a PBE key factory
				pbeKeySpec = new PBEKeySpec(passwordArray);
				
				keyFac = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
				pbeKey= keyFac.generateSecret(pbeKeySpec);   
				
		    } catch (Exception e) {
		        Log.e("PasswordBasedEncryptionActivity", "PBE secret key error");
		    }
		}
}