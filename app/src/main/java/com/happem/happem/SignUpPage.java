package com.happem.happem;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class SignUpPage extends Activity {

	// Salt
	final byte[] salt = {
	   (byte)0xc7, (byte)0x73, (byte)0x21, (byte)0x8c,
	   (byte)0x7e, (byte)0xc8, (byte)0xee, (byte)0x99
	};	
	final int count = 20;
	final PBEParameterSpec pbeParamSpec = new PBEParameterSpec(salt, count);
	private SecretKeyFactory keyFac;
	private PBEKeySpec pbeKeySpec;
	private SecretKey pbeKey;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.signup_layout);
		
		TextView title =  (TextView) findViewById(R.id.modifica); 
		TextView nome =  (TextView) findViewById(R.id.nomeS);
		TextView cognome =  (TextView) findViewById(R.id.cognomeS); 
		TextView datadinascita =  (TextView) findViewById(R.id.datadinascitaS);
		TextView citta = (TextView) findViewById(R.id.citta);
		TextView indirizzo = (TextView) findViewById(R.id.indirizzoS);
		TextView telefono = (TextView) findViewById(R.id.telefonoS);
		TextView email = (TextView) findViewById(R.id.emailS);
		TextView username = (TextView) findViewById(R.id.username);
		TextView password1 = (TextView) findViewById(R.id.Password1);
		TextView password2 = (TextView) findViewById(R.id.password2S);
		
		final EditText nomeUser =  (EditText) findViewById(R.id.editNomeSUser); //
		final EditText cognomeUser =  (EditText) findViewById(R.id.editCognomeSUser); //
		final EditText datadinascitaUser =  (EditText) findViewById(R.id.dObSUser);
		final EditText cittaUser = (EditText) findViewById(R.id.cittaSUser);
		final EditText indirizzoUser = (EditText) findViewById(R.id.indirizzoSUser);
		final EditText telefonoUser = (EditText) findViewById(R.id.telefonoSUser);
		final EditText emailUser = (EditText) findViewById(R.id.mailSUser); //
		final EditText usernameUser = (EditText) findViewById(R.id.usermameSUser);//
		final EditText password1User = (EditText) findViewById(R.id.password1SUser);//
		final EditText password2User = (EditText) findViewById(R.id.password2SUser);//
		
		Button signupButton = (Button) findViewById(R.id.buttonModifica);

		signupButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {

				 //recuper dato inserito
				String nomeU = nomeUser.getText().toString();
				//se striga � vuota metto errore--> CAMPO OBBLIGATORIO
				if(nomeU.equals("")){
					nomeUser.setError("Inserisci il nome");
				}
				
				String cognomeU = cognomeUser.getText().toString();
				if(cognomeU.equals("")){
					cognomeUser.setError("Inserisci il cognome");
				}

				//campo non obbligatorio --> metto a null se la string a � vuota
				String datadinascitaU = datadinascitaUser.getText().toString();
				if(datadinascitaU.equals("")){
					datadinascitaU=null;
				}
				
				//campo non obbligatorio --> metto a null se la string a � vuota
				String cittaU = cittaUser.getText().toString();
				if(cittaU.equals("")){
					cittaU=null;
				}

				//campo non obbligatorio --> metto a null se la string a � vuota
				String indirizzoU = indirizzoUser.getText().toString();
				if(indirizzoU.equals("")){
					indirizzoU=null;
				}
				
				//campo non obbligatorio --> metto a null se la string a � vuota
				String telefonoU = telefonoUser.getText().toString();
				if(telefonoU.equals("")){
					telefonoU=null;
				}

				boolean emOK=false;
				String emailU = emailUser.getText().toString();
				if(emailU.equals("")){
					emailUser.setError("Inserisci l'indirizzo email");
				}
				else{
					//creo thread per controllare se esiste un utente che ha gia' quella mail, se dovesse esistere stampo un errore e blocco registrazione
					MailCheckThread mct = new MailCheckThread(emailU);
					mct.run();
					mct.start();
					try {
						mct.join();
					} catch (InterruptedException e) {
						Log.e("ERROR-signup-mail", ""+e.toString());
					}
					//controllo risultato thread --> se true tutto ok (non esiste nessun utente con quella mail)
					if(mct.getEmailCheck()){
						emOK=true;
					}else{
						//esiste utente con quella mail --> blocco registrazione metto errore e toast
						emOK=false;
						emailUser.setError("Esiste gia' un utente con questa mail");
						
						//TOAST
						LayoutInflater inflater = getLayoutInflater();
				        View layout = inflater.inflate(R.layout.toast_layout, (ViewGroup) findViewById(R.id.toast_layout_root));
				        ImageView image = (ImageView) layout.findViewById(R.id.immagine);
				        image.setImageDrawable(getResources().getDrawable(R.drawable.icon_17398));
				        TextView text = (TextView) layout.findViewById(R.id.ToastTV);
				        text.setText("Impossibile completare la registrazione!");

				        Toast toast = new Toast(getApplicationContext());
				        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
				        toast.setDuration(Toast.LENGTH_SHORT);
				        toast.setView(layout);
				        toast.show();
					
					}
				}
				
				
				//controllo username
				boolean uOK=false;
				String user = usernameUser.getText().toString();
				//e' un campo obbligatorio quindi, se e' lasciato vuoto --> imposto errore e blocco registrazione
				if(user.equals("")){
					usernameUser.setError("Inserisci l'Username");
				}
				else{
					//creo istanza del thread username 
					UsernameCheckThread uct = new UsernameCheckThread(user);
					uct.run();
					uct.start();
					try {
						uct.join();
					} catch (InterruptedException e) {
						Log.e("ERROR-signup-mail", ""+e.toString());
					}
					//controllo risultato thread --> se true tutto ok (non esiste nessun utente con quel username)
					if(uct.getUsernameCheck()){
						uOK=true;
					}else{
						//esiste utente con quel user --> blocco registrazione metto errore e toast
						uOK=false;
						usernameUser.setError("Esiste gia' un utente con quel Username!");
						//TOAST
						LayoutInflater inflater = getLayoutInflater();
				        View layout = inflater.inflate(R.layout.toast_layout, (ViewGroup) findViewById(R.id.toast_layout_root));
				        ImageView image = (ImageView) layout.findViewById(R.id.immagine);
				        image.setImageDrawable(getResources().getDrawable(R.drawable.icon_17398));
				        TextView text = (TextView) layout.findViewById(R.id.TVData);
				        text.setText("Impossibile completare la registrazione!");

				        Toast toast = new Toast(getApplicationContext());
				        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
				        toast.setDuration(Toast.LENGTH_SHORT);
				        toast.setView(layout);
				        toast.show();
					
					}
				}
			
				
				//faccio inserire due volte la password e poi controllo
				String password1 = password1User.getText().toString();
				if(password1.equals("")){
					password1User.setError("Inserisci la password");
				}
				
				String password2 = password2User.getText().toString();
				if(password2.equals("")){
					password2User.setError("Inserisci la password un'altra volta");
				}
				
				//controllo uguaglianza tra le password
				boolean pOK=false;
					//le password non sono stringe vuote
				if(password1.equals("")==false && password2.equals("")==false){
					if(password1.equals(password2)==true){
						pOK=true;
					}
					else{
						//se le password non coincidono blocco registrazione e metto errore
						password1User.setError("Le password non coincidono");
						password2User.setError("Le password non coincidono");
					}
				}
				
			
				
				/**se controlli eseguiti sono stati passati allora posso registrare
					 - CREO LE CHIAVI PER CIFRARE
					 - CREO ISTANZA DELLA CLASSE "Encryption_Decryption" DOVE HO INSERITO IL CODICE PER CIFRARE E DECIFRARE
					 - CIFRO TUTTI I DATI TRANNE USERNAME E MAIL
					 - SCRIVO SUL DATABASE 
				*/
				
				
				if(pOK==true && emOK==true && uOK==true){ 	
					createKeys(password1);
					
					/*** ENCRYPT NOME ***/
					Encryption_Decryption ed = new Encryption_Decryption();
					String encrypt_nomeU = ed.encrypt(nomeU,pbeKey,pbeParamSpec);
					
					/*** ENCRYPT COGNOME ***/
					String encrypt_cognomeU = ed.encrypt(cognomeU,pbeKey,pbeParamSpec);
					String encrypt_datadinascitaU;

					/*** ENCRYPT DATA NASCITA ***/
					/* PER I CAMPI NON OBBLIGATORI --> SE VALORE E' NULL VADO A SCRIVERE NEL DB NULL
					 * SE INVECE CONTENGONO UN VALORE ALLORA LO CIFRO*/
					if(datadinascitaU!=null){
							encrypt_datadinascitaU = ed.encrypt(datadinascitaU,pbeKey,pbeParamSpec);
					}else{
							encrypt_datadinascitaU=null;
					}
					
					/*** ENCRYPT INDIRIZZO ***/
					String encrypt_indirizzoU;
					if(indirizzoU!=null){
							encrypt_indirizzoU = ed.encrypt(indirizzoU,pbeKey,pbeParamSpec);
					}else{
							encrypt_indirizzoU=null;
					}

					/*** ENCRYPT CITTA ***/
					String encrypt_cittaU;
					if(cittaU!=null){
							encrypt_cittaU = ed.encrypt(cittaU,pbeKey,pbeParamSpec);
					}else{
							encrypt_cittaU=null;
					}
					
					/*** ENCRYPT TELEFONO ***/
					String encrypt_telefonoU;
					if(telefonoU!=null){
						encrypt_telefonoU = ed.encrypt(telefonoU,pbeKey,pbeParamSpec);
					}else{
						encrypt_telefonoU=null;
					}
					
					
					/*** ENCRYPT PASSWORD ***/
					String encrypt_password1 = ed.encrypt(password1,pbeKey,pbeParamSpec);
					
					
					/* ORA POSSO SCRIVERE SUL DATABASE REMOTO --> CREO THREAD E LO AVVIO E ASPETTO CHE FINISCA*/
					SignUpThread t = new SignUpThread(encrypt_nomeU, encrypt_cognomeU, encrypt_datadinascitaU, 
							encrypt_indirizzoU, encrypt_cittaU, encrypt_telefonoU, emailU,user, encrypt_password1  );
					Log.i("THREAD - SIGNUP","REGISTRAZIONE THREAD CREATO");
					t.run();
					t.start();
					try {
						t.join();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					//TOAST
					  LayoutInflater inflater = getLayoutInflater();
				        View layout = inflater.inflate(R.layout.toast_layout, (ViewGroup) findViewById(R.id.toast_layout_root));
				        layout.setBackgroundResource(R.layout.shape2);
				        TextView text = (TextView) layout.findViewById(R.id.ToastTV);
				        text.setText("Registrazione Effettuata con Successo!");
				        Toast toast = new Toast(getApplicationContext());
				        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
				        toast.setDuration(Toast.LENGTH_SHORT);
				        toast.setView(layout);
				        toast.show();
												
						Intent i = new Intent(SignUpPage.this, Login.class);
						startActivity(i);
					}
					
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
