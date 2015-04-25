package com.happem.happem;


import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


public class Login extends Activity {
	
	//STRINGE USATE PER LE PREFERENZE
	private final static String PREF_LOG_USER="Login_Dati_USER";
	private final static String PREF_LOG_PASS="Login_Dati_PASS";
	
	private final static  String UTENTE="";
	private final static  String PASSWORD = "";
	private String pass_rec;
	
	
	/** In cryptography, a salt is random data that is used as an additional input to a one-way function that 
	    hashes a password or passphrase. 
	    The primary function of salts is to defend against dictionary attacks versus a list of password hashes 
	    and against pre-computed rainbow table attacks.
	    A salt is simply added to make a common password uncommon.
	    **/
	//Salt
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
		setContentView(R.layout.activity_login);
		
		
		Button loginbutton = (Button) findViewById(R.id.buttonModificaDati);
		loginbutton.setText(R.string.login);
		
		Button signup = (Button) findViewById(R.id.button2);
		signup.setText(R.string.Registrati);
		
		TextView title= (TextView) findViewById(R.id.RicercaLavoroTitolo);
		TextView user= (TextView) findViewById(R.id.paroleChiave);
		TextView pass= (TextView) findViewById(R.id.regioneText);
		
		final Switch remember = (Switch) findViewById(R.id.switch1);
		
		final EditText username = (EditText) findViewById(R.id.usernameLogin);
		final EditText password = (EditText) findViewById(R.id.passwordLogin);
		
		
		//RECUPERO LE PREFERENZE E FACCIO SET DEI CAMPI USERNAME, PASSWORD E STATO DELLO SWITCH
																		//Context.MODE_PRIVATE--> in questo modo solo questa app potra' accedere al valore della preferenza
		SharedPreferences pref_User = getSharedPreferences(PREF_LOG_USER, Context.MODE_PRIVATE);
		username.setText(pref_User.getString(UTENTE, ""));
		
		
		loginbutton.setOnClickListener(new OnClickListener(){
			
			@Override
			public void onClick(View v) {
					
				//recuper username inserito
				String user = username.getText().toString();

				//se striga e' vuota --> errore
				if(user.equals("")){
					username.setError("Inserisci username");
				}
				
				//recupero password inserita
				pass_rec = password.getText().toString();
				
				//se stringa vuota --> errore
				if(pass_rec.equals("")){
					password.setError("Inserisci password");
				}
				
				if(user.equals("")==false & (pass_rec.equals("")==false)){
						
					createKeys(pass_rec);
					
					Encryption_Decryption ed = new Encryption_Decryption();
		
					String encrypt_psw = ed.encrypt(pass_rec,pbeKey,pbeParamSpec);
					
					//recupero il nome del package dell'applicazione
					final String pkg = getPackageName();
					
					//Creo un'istanza del thread 
					CheckUserThread t2 = new CheckUserThread(user, encrypt_psw);
					//avvio il thread--> inizio esecuzione del codice scritto all'interno del metodo run()
					/**run() 
					 * Starts executing the active part of the class' code. 
					 * This method is called when a thread is started that has been created with a class which implements 
					 * Runnable.*/
					//t2.run();
					
					/**start() 
					 * Starts the new Thread of execution. 
					 * The run() method of the receiver will be called by the receiver Thread itself 
					 * (and not the Thread calling start()).*/
					t2.start();
					
					//aspetto che il tread abbia finito di eseguire il codice e nel mentre mostro un dialog per far vedere che sto verificando i dati inseriti
					do{showDialog(1);}while(t2.isAlive());
					dismissDialog(1);
					
					//recupero il valore booleano del risultato del thread
					if(t2.getCheck()==true){
						//creo un intent
						Intent intent= new Intent (Login.this, PersonalPage.class);					
						intent.putExtra(pkg+".Username", user);      //inseriamo i dati nell'intent          
				   
						//creo un toast personalizzato per informare l'utente del login avvenuto correttamente e lo mostro 
				        Toast toast = new Toast(getApplicationContext());
				        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
				        toast.setDuration(Toast.LENGTH_SHORT);
					        LayoutInflater inflater = getLayoutInflater();
					        View layout = inflater.inflate(R.layout.toast_layout, (ViewGroup) findViewById(R.id.toast_layout_root));
					        layout.setBackgroundResource(R.layout.shape2);
					        TextView text = (TextView) layout.findViewById(R.id.ToastTV);
					        text.setText("Carico la tua pagina personale...");
				        toast.setView(layout);
				        toast.show();
				        
				        //recupero le shared preferences
				        SharedPreferences prefers_User = getSharedPreferences(PREF_LOG_USER,Context.MODE_PRIVATE);
				        SharedPreferences prefers_Pass = getSharedPreferences(PREF_LOG_PASS,Context.MODE_PRIVATE);
						 
				        //SALVO VALORE DELL'EDIT-TEXT USER NELLE PREFERENZE SE REMEMBERME (lo switch) =SI  ALTRIMENTI NO
						/**SharedPreferences.Editor 
						 * Interface used for modifying values in a SharedPreferences object.*/
						SharedPreferences.Editor editor_User = prefers_User.edit();
						SharedPreferences.Editor editor_Pass = prefers_Pass.edit();
						
						//per la generazione delle chiavi per decriptare e' necessario che la password sia salvata
						editor_Pass.putString(PASSWORD, pass_rec);
						editor_Pass.commit();

						if(remember.isChecked()){
							editor_User.putString(UTENTE, username.getText().toString());		 
				   			editor_User.commit();
				   		
						}else{
				   			//Rimuovo il valore inserito nella preferenza per lo "username"
				   			editor_User.clear();
				   			editor_User.commit();
				   		}
				        
						startActivity(intent);
					}
					else{
						if(t2.getUserCheck()==false){//utente non trovato
							LayoutInflater inflater = getLayoutInflater();
					        View layout = inflater.inflate(R.layout.toast_layout, (ViewGroup) findViewById(R.id.toast_layout_root));
					        ImageView image = (ImageView) layout.findViewById(R.id.immagine);
					        image.setImageDrawable(getResources().getDrawable(R.drawable.icon_17398));
					        TextView text = (TextView) layout.findViewById(R.id.ToastTV);
					        text.setText("Utente inesistente!");

					        Toast toast = new Toast(getApplicationContext());
					        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
					        toast.setDuration(Toast.LENGTH_SHORT);
					        toast.setView(layout);
					        toast.show();
						}
						
						if(t2.getPassCheck()==false){//entra qui se password e' sbagliata
							username.setError("Wrong Username or Password");
							password.setError("Wrong Username or Password");
							
							 LayoutInflater inflater = getLayoutInflater();
						        View layout = inflater.inflate(R.layout.toast_layout, (ViewGroup) findViewById(R.id.toast_layout_root));
						        ImageView image = (ImageView) layout.findViewById(R.id.immagine);
						        image.setImageDrawable(getResources().getDrawable(R.drawable.icon_17398));
						        TextView text = (TextView) layout.findViewById(R.id.ToastTV);
						        text.setText("Impossibile effettuare il Login!");

						        Toast toast = new Toast(getApplicationContext());
						        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
						        toast.setDuration(Toast.LENGTH_SHORT);
						        toast.setView(layout);
						        toast.show();
							
						}
					}
					
				}
					
			}
	
			
			
		});
		
		//BOTTONE PER LA REGISTRAZIONE
		signup.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Intent intent1 = new Intent (Login.this, SignUpPage.class);
				startActivity(intent1);
			}
			
		});
	}

	
	//METODO PER GENERARE LE CHIAVI PER DECRIPTARE
	public void createKeys(String s){
		/** In order to use Password-Based Encryption (PBE), a salt and an iteration count need to be 
		 defined. The same salt and iteration count that are used for encryption must be used for decryption */
		try {
			char passwordArray[] = s.toCharArray();
			// Convert user�s password into a SecretKey object, using a PBE key factory
			pbeKeySpec = new PBEKeySpec(passwordArray);
			
			/** Better Choices --> 
			 		* 	PBEWithMD5AndTripleDES
			    			Overall, this option is probably more-or-less secure. 
			    			It uses the triple DES algorithm, which gives up to 112-bit security. 
			    			However, this is a very slow algorithm for that level of security and the key is generated 
			    			using the MD5 hash algorithm, now considered insecure
			    	* 	PBEWithSHA1AndRC2_40
							This mode encrypts with 40-bit RC2. **/
			
			
			keyFac = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
			pbeKey= keyFac.generateSecret(pbeKeySpec);   
			
	    } catch (Exception e) {
	        Log.e("PasswordBasedEncryption", "PBE secret key error in Login");
	    }
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case 1:
			ProgressDialog progressDialog = new ProgressDialog(this,
					ProgressDialog.STYLE_SPINNER);
			progressDialog.setIndeterminate(true);
			progressDialog.setTitle("Verifico");
			progressDialog.setMessage("Sto verificando ...");
			return progressDialog;
		default:
			return null;
		}
	}
	
}

