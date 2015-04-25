package com.happem.happem;

import com.happem.happem.R;
import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
public class PersonalPage extends TabActivity  {
	
	private TabHost tabHost;
	private String usernam;
	/*mi serve come referenza per settare CurrentTabHost in altre activity*/
	public static PersonalPage perspage;
	private Context context;
	
	public TabHost getTab(){
		return tabHost;
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.provalayout);
		perspage=this;
		tabHost = getTabHost();		
		context=this;
		
		
		Intent datipassati = getIntent();
		String pkg=getPackageName();
	    usernam=datipassati.getStringExtra(pkg+".Username");  //prendiamo i dati nell'intent

		//AGGIUNGO I TAB ATTRAVERSO LA FUNZIONE PERSONALIZZATA addTab
	    addTab("Pagina Personale", getResources().getDrawable(R.drawable.icon_home_config), PersonalData.class, usernam);
	    addTab("Ricerca Lavoro",getResources().getDrawable(R.drawable.icon_search_config),RicercaLavoro.class, usernam);
	    addTab("Ricerche Recenti", getResources().getDrawable(R.drawable.icon_recent_config), RicercheRecenti.class, usernam);
	    addTab("Preferiti", getResources().getDrawable(R.drawable.icon_fav_config), Preferiti.class, usernam);
	    
	    tabHost.setCurrentTab(0);
	    //ABILITO IL DISPLAY DELLA ACTION BAR
		getActionBar().setDisplayHomeAsUpEnabled(false);
		
	}
	
	
	private void addTab(String labelId, Drawable drawableId, Class<?> c, String data)
	{
		Intent intent = new Intent(this, c);
		
			String pkg=getPackageName();
			intent.putExtra(pkg+".Username", data);
			
		
		/** TabSpec 
		 * A tab has a tab indicator, content, and a tag that is used to keep track of it. 
		 * This builder helps choose among these options. 
		 * For the tab indicator, your choices are: 
		 * 		1) set a label 
		 * 		2) set a label and an icon 
		 * For the tab content, your choices are:
		 * 		1) the id of a View 
		 *		2) a TabHost.TabContentFactory that creates the View content. 
		 *		3) an Intent that launches an Activity.*/
			
			
		TabHost.TabSpec spec = tabHost.newTabSpec(labelId.trim()+"S");	
		//SPECIFICO LAYOUT DEL TAB
		View tabIndicator = LayoutInflater.from(this).inflate(R.layout.tablayout, getTabWidget(), false);
		//set TITOLO E ICONA
		TextView title = (TextView) tabIndicator.findViewById(R.id.titleTab);
		title.setText(labelId);
		ImageView icon = (ImageView) tabIndicator.findViewById(R.id.iconTab);
		icon.setImageDrawable(drawableId);		
		spec.setIndicator(tabIndicator);
		spec.setContent(intent);
		
		//aggiungo la specifica del TabHost alla varriabile tabHost
		tabHost.addTab(spec);
	}
	
	
	
	
	/** DEFINISCO ELEMENTI DELL'ACTION BAR**/
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// faccio l'inflater del menu
		getMenuInflater().inflate(R.menu.action_bar_test, menu);
		return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
		 
		//ID = LOGOUT --> TORNO SEMPLICEMENTE ALLA PAGINA DEL LOGIN
		if(item.getItemId() ==R.id.logout){
			 
		 		Intent intent = new Intent(this, Login.class);
				startActivity(intent);
				return true;
				
		 }else if(item.getItemId()==R.id.newsearch){ /**NUOVA RICERCA**/
			 
			 /*setto il tab 1 che corrisponde a quello della ricerca*/
			 tabHost.setCurrentTab(1);
			 
			 /* SETTO ESPLICITAMENTE LA VIEW CHE VOGLIO SIA CARICATA COME CONTENT DELL'ACTIVITY CORRENTE 
			   LA VIEW E' QUELLA CHE CARICO ATTRAVERSO IL LOCAL ACTIVITY MANAGER **/
			 
						 /**     LOCAL ACTIVITY MANAGER  
						  * Helper class for managing multiple running embedded activities in the same process. 
						  * This class is not normally used directly, but rather created for you as part of the ActivityGroup 
						  * implementation. **/
						 
						 /**     DECOR VIEW     
						     The DecorView is the view that actually holds the window�s background drawable. **/
						 
						 /**	 FLAG_ACTIVITY_CLEAR_TOP	
						  *  If set, and the activity being launched is already running in the current task, then instead 
						  *  of launching a new instance of that activity, all of the other activities on top of it will be 
						  *  closed and this Intent will be delivered to the (now on top) old activity as a new Intent. **/
			 
			 getCurrentActivity().setContentView(getLocalActivityManager().startActivity("RicercaLavoro", 
					 new Intent(context, RicercaLavoro.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)).getDecorView());
			 
			 return true;
			 
		 }else if(item.getItemId()==R.id.elimricrec){
			 
			 /**  DIALOG 
			   		A dialog is a small window that prompts the user to make a decision or enter additional information. 
			  		A dialog does not fill the screen and is normally used for modal events that require users to take an 
			  		action before they can proceed.  */
			   
			  
			  /** ALERT DIALOG
			  		A subclass of Dialog that can display one, two or three buttons. **/
			 
			 /* CREO UN ALERT **/
			 
			 final AlertDialog.Builder builder = new AlertDialog.Builder(this);
			    //set del messaggio da stampare
			 	builder.setMessage("Eliminare TUTTE le Ricerche Recenti?");
			    //il dialog non puo' esssere cancellato
			 	builder.setCancelable(false)
			 			//set di cosa succede quando viene cliccato il bottone positivo --> cancello dal db le ricerce recenti
			           .setPositiveButton("S�", new DialogInterface.OnClickListener() {
			               public void onClick( final DialogInterface dialog,  final int id) {
			            	//apro il DB in modalita' scrittura
			            	MyDBHelper dbHelper = new MyDBHelper(tabHost.getContext(),"JobAroundU_DB", null, 1);
			           		SQLiteDatabase db=dbHelper.getWritableDatabase();
			           		//scrivo sql
			           		String sqlEliminaRR = "DELETE FROM RicercheRecenti WHERE User='"+usernam+"';";
			           		//eseguo sql
			           		db.execSQL(sqlEliminaRR);
			           		Log.i("DELETE RR", "HO ELIMINATO TUTTE LE RICERCHE RECENTI");
			           		//chiudo il collegamento al DB
			           		db.close();
			               }
			           })
			           .setNegativeButton("No", new DialogInterface.OnClickListener() {
			        	   public void onClick(final DialogInterface dialog, final int id) {
			                    dialog.cancel();
			               }
			           });
			    
	            builder.setTitle("ELIMINAZIONE DI TUTTE LE RICERCHE RECENTI - CONFERMA");
	            // Icona per AlertDialog
	            builder.setIcon(R.drawable.trash_empty);
	            //mostro l'alertDialog
	            builder.show();
	            //setto il tab della pagina iniziale
	            tabHost.setCurrentTab(0);
	            return true;
		 }else{
			 return false;
		 }
		 }
	
	public void RicercaLavoroPage(){
		tabHost.setCurrentTab(1);
		getCurrentActivity().setContentView(getLocalActivityManager().startActivity("RicercaLavoro", new Intent(context, RicercaLavoro.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)).getDecorView());
	}
	
}

