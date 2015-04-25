package com.happem.happem;


import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import android.app.Activity;
import android.app.ActivityGroup;
import android.app.Notification;
import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RemoteViews;
import android.widget.TextView;

public class MailActivity extends ActivityGroup implements OnClickListener {

	EditText et_subject, et_message;
	String address, subject, message, file_path, sender;
	Button bt_send;
	ImageButton  bt_attach;
	TextView tv_attach, et_address, tvPath;
	private final static String PREF_LOG_PASS="Login_Dati_PASS";
	private final static  String PASSWORD = "";


	private static final int PICK = 100;
	private final static int SOUND_NOTIFICATION_ID = 1;
	private NotificationManager notificationManager;
	Uri URI = null;
	int columnindex;
	private String pos;
	private String az;
	private String username;
	private String regione;
	private String provincia;
	private String[] tag;
	
	// Salt
		final byte[] salt = {
		   (byte)0xc7, (byte)0x73, (byte)0x21, (byte)0x8c,
		   (byte)0x7e, (byte)0xc8, (byte)0xee, (byte)0x99
		};	
		final int count = 20;
		final PBEParameterSpec pbeParamSpec = new PBEParameterSpec(salt, count);
		SecretKeyFactory keyFac;
		PBEKeySpec pbeKeySpec;
		SecretKey pbeKey;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_LEFT_ICON);
		setContentView(R.layout.mail_layout);
		getWindow().setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.email);

		SharedPreferences pref_Pass = getSharedPreferences(PREF_LOG_PASS, Context.MODE_PRIVATE);
		String password = pref_Pass.getString(PASSWORD, "");
		createKeys(password);
		
		Encryption_Decryption ed = new Encryption_Decryption();
		
		Intent datipassati = getIntent();
		String pkg=getPackageName();
	    pos =datipassati.getStringExtra(pkg+".Posizione");
	    az =datipassati.getStringExtra(pkg+".Azienda");
		address=datipassati.getStringExtra(pkg+".EmailReceiver");
		subject= "Candidatura via cellulare per la posizione  **"+pos+   "**  del sig/sig.ra   **"+ed.decrypt(datipassati.getStringExtra(pkg+".CognomeUser"), pbeKey, pbeParamSpec)+"  "+ed.decrypt(datipassati.getStringExtra(pkg+".NomeUser"), pbeKey, pbeParamSpec)+"**";
		sender = datipassati.getStringExtra(pkg+".EmailSender");
			
		username= getIntent().getStringExtra(getPackageName()+".Username");
	    regione=getIntent().getStringExtra(getPackageName()+".Regione");
	    provincia=getIntent().getStringExtra(getPackageName()+".Provincia");
	    tag=getIntent().getStringArrayExtra(getPackageName()+".Tag");
		
		initializeViews();
		bt_send.setOnClickListener(this);
		bt_attach.setOnClickListener(this);

	}

	private void initializeViews() {
		et_address = (TextView) findViewById(R.id.et_address_id);
		et_address.setText(address);
		tvPath = (TextView) findViewById(R.id.write_a_message); 
		et_subject = (EditText) findViewById(R.id.et_subject_id);
		et_subject.setText(subject);
		et_message = (EditText) findViewById(R.id.et_message_id);
		bt_send = (Button) findViewById(R.id.bt_send_id);
		bt_attach = (ImageButton) findViewById(R.id.bt_attach_id);
		

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.bt_attach_id:
			openSearch();
			break;

		case R.id.bt_send_id:
			address = et_address.getText().toString();
			subject = et_subject.getText().toString();
			message = et_message.getText().toString();

			String emailAddresses[] = { address };

			Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
			
			emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, emailAddresses);
			emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
			emailIntent.setType("plain/text");
			emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, message);
			if (URI != null)
				emailIntent.putExtra(Intent.EXTRA_STREAM, URI);
			
				Notification.Builder notification=new Notification.Builder(this);
				notification.setContentTitle("JobAroundU");
		        notification.setSmallIcon(R.drawable.ic_notification);
		        notification.setAutoCancel(true);
		        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_notification);
		        notification.setLargeIcon(bitmap);
		        notification.setDefaults(Notification.DEFAULT_SOUND);
		        notification.setStyle(new Notification.BigTextStyle().bigText("Richiesta di candidatura effettuata con successo per la posizione ***"+pos.toUpperCase()+"*** nell'azienda ***"+az.toUpperCase()+"***"));
				Intent intent = new Intent(this, NotificationActivity.class);
				intent.putExtra("notificationType", "Sound Notification");
				notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
				notificationManager.notify(SOUND_NOTIFICATION_ID, notification.build());
	
				startActivity(emailIntent);
			
			break;

		}

	}

	private void openSearch() {
		Intent intent = new Intent();
		intent.setType("file/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(Intent.createChooser(intent, "Scegli CV"), PICK );
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == PICK && resultCode == RESULT_OK) {
			String FilePath = data.getData().getPath();
			URI = Uri.parse("file://" + FilePath);
			tvPath.setText(""+FilePath);
		}
	}
	
	@Override
	public void onBackPressed(){
		finish();
		RicercaLavoro.groupContact.LastView2(regione, provincia, tag, username);

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
