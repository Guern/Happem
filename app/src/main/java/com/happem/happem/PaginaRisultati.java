package com.happem.happem;

import java.util.ArrayList;

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.app.ExpandableListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.util.Linkify;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View.OnClickListener;


public class PaginaRisultati extends ExpandableListActivity implements AnimationListener {
	
		private int ChildClickStatus=-1;
		private ArrayList<Parent> parents;
		private String username;
		private String regione;
		private String provincia;
		private String[] tag;
		
		private String posizione;
		private String azienda;
		private Context context;
		private Animation animFade;
		private Animation animRotate;

		private MyExpandableListAdapter mAdapter=null;
		
		@Override
		protected void onPause() {
			super.onPause();
		}



		/**onResume() --> viene chiamata dopo onPause() --> l'activity diventa visibile, nel nostro caso. 
		 * devo "aggiornare" la vista sapendo se e quale parent e' stato aperto. 
		 * nel caso in cui nessun parent sia stato aperto, tutta l'expandable list view e' chiusa 
		 * finisco poi con il recuperare l'adapter dell'expandable list view e aggiornarlo (notifyDataSetChanged()) **/
		
		@Override
		protected void onResume() {
			super.onResume();
			
			MyExpandableListAdapter a = (MyExpandableListAdapter)getExpandableListAdapter();

			if(RicercaLavoro.groupContact.group!=-1 && a.getGroupCount()>0 ){
				
				
					getExpandableListView().expandGroup(RicercaLavoro.groupContact.group);
				
				((MyExpandableListAdapter)getExpandableListAdapter()).notifyDataSetChanged();

			}
			
			if(a.getGroupCount()==0){
				this.onCreate(null);
				((MyExpandableListAdapter)getExpandableListAdapter()).notifyDataSetChanged();

			}
			/** notifyDataSetChanged()
				 * Notifies the attached observers that the underlying data has been changed and any View 
				 * reflecting the data set should refresh itself.**/

		}




	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;

		MyDBHelper dbHelper2 = new MyDBHelper(this,"JobAroundU_DB", null, 1);
		final SQLiteDatabase db2;
		
	    username= getIntent().getStringExtra(getPackageName()+".Username");
	    regione=getIntent().getStringExtra(getPackageName()+".Regione");
	    provincia=getIntent().getStringExtra(getPackageName()+".Provincia");
	    tag=getIntent().getStringArrayExtra(getPackageName()+".Tag");
		
		GetAnnunciThread gat= new GetAnnunciThread(regione, provincia, tag);
		gat.start();
		do{}while(gat.isAlive());
		
		//set dei parametri del layout dell'exp. list view
		Resources res = this.getResources();
	    Drawable devider = res.getDrawable(R.drawable.transparent);
	    Drawable group_indicator = res.getDrawable(R.drawable.group_indicator);
	    // Set ExpandableListView values 
	    getExpandableListView().setGroupIndicator(group_indicator);
		getExpandableListView().setDivider(devider);
		getExpandableListView().setChildDivider(devider);
		getExpandableListView().setDividerHeight(15);//misura in pixel
		/**Registers a context menu to be shown for the given view (multiple views can show the context menu). 
		 * This method will set the View.OnCreateContextMenuListener on the view to this activity, 
		 * so onCreateContextMenu(ContextMenu, View, ContextMenuInfo) will be called when it is time to show the 
		 * context menu.*/
		registerForContextMenu(getExpandableListView());
		
		//controllo se risultati esistono --> se non esistono stampo messaggio di errore
		parents = gat.annuncitrovati;
		
		if (parents.isEmpty()) {
			 LayoutInflater inflater = getLayoutInflater();
		        View layout = inflater.inflate(R.layout.toast_layout, (ViewGroup) findViewById(R.id.toast_layout_root));
		        ImageView image = (ImageView) layout.findViewById(R.id.immagine);
		        image.setImageDrawable(getResources().getDrawable(R.drawable.icon_25761));
		        TextView text = (TextView) layout.findViewById(R.id.ToastTV);
		        text.setText("La ricerca non ha prodotto risultati!!");

		        Toast toast = new Toast(getApplicationContext());
		        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
		        toast.setDuration(Toast.LENGTH_SHORT);
		        toast.setView(layout);
		        toast.show();
			    RicercaLavoro.groupContact.group=-1;

		     
		 }
		
		db2=dbHelper2.getWritableDatabase();
		//metto nella view e stampo a video 
		if (this.getExpandableListAdapter() == null){
					//Creo ExpandableListAdapter Object
					mAdapter=new MyExpandableListAdapter();
					// Set Adapter come ExpandableList Adapter
					this.setListAdapter(mAdapter);
		}
		else
		{
					 // Refresh ExpandableListView data 
					((MyExpandableListAdapter)getExpandableListAdapter()).notifyDataSetChanged();
			
		}
	
		//quando espando vado a chiudere eventuali parent aperti e setto previous grup al valore del parent espanso
		getExpandableListView().setOnGroupExpandListener(new OnGroupExpandListener() {
	        int previousGroup = -1;
	        @Override
	        public void onGroupExpand(int groupPosition) {
	            if(groupPosition != previousGroup)
	                getExpandableListView().collapseGroup(previousGroup);
	            	previousGroup = groupPosition;
	        }
	    });
		
		//quando un parent viene chiuso metto prev.group=-1 in riceraLavoro
		getExpandableListView().setOnGroupCollapseListener(new OnGroupCollapseListener() {		
	        @Override
	        public void onGroupCollapse(int groupPosition) {
				RicercaLavoro.groupContact.setGroup(-1);
	        }
	    });
		
		
		
	}
	
	
	
	
	/***      CLASSE PRIVATA USATA PER PERSONALIZZARE L'EXPANDABLE LIST VIEW BASE  ***/
	
	private class MyExpandableListAdapter extends BaseExpandableListAdapter
	{
		
		private LayoutInflater inflater;
		protected SQLiteOpenHelper dbHelper2 =new MyDBHelper(PaginaRisultati.this,"JobAroundU_DB", null, 1);

		public MyExpandableListAdapter()
		{
			//ottengo inflater dalla classe PaginaRisultati
			inflater = LayoutInflater.from(PaginaRisultati.this);
		}
    
		
		//QUESTO METODO E' USATO PER CREARE LA PARENT ROW
		
		@Override
		public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parentView)
		{
			final Parent parent = parents.get(groupPosition);
			
			// FACCIO L'INFLATE DEL LAYOUT DEL PARENT ROW
			convertView = inflater.inflate(R.layout.grouprow, parentView, false); 
			
			// PRENDO GLI ELEMENTI DEL LAYOUT GROUPROW E SETTO I VALORI
			((TextView) convertView.findViewById(R.id.text1)).setText(parent.getPosizione());
			((TextView) convertView.findViewById(R.id.TVData)).setText(parent.getAzienda());
			ImageView image=(ImageView)convertView.findViewById(R.id.image_tag);
			image.setImageResource(R.drawable.icon_21039);
			
			//BOTTONE DEI PREFERITI --> INIZIALMENTE STELLINA GRIGIA
			final ImageButton imgbtt = (ImageButton)convertView.findViewById(R.id.imageButton1);
			imgbtt.setFocusable(false);
			
			//metto stellina gialla se l'annuncio e' presente nella tabella preferiti e 
			//metto a false la possibilita' di inseririlo tra di essi, evitando cosi' doppioni nella tabella
			
			SQLiteDatabase db2=dbHelper2.getReadableDatabase();
			String sqlPref = "SELECT idDBJobs FROM MyPreferences WHERE User='"+getIntent().getStringExtra(getPackageName()+".Username")+"';";
			Cursor cursor = db2.rawQuery(sqlPref, null);
			boolean prefAnn=false;
			while (cursor.moveToNext()){
				if(cursor.getString(0).equals(parent.getId())){
					imgbtt.setImageResource(R.drawable.yellow_star);
					prefAnn=true;
					//disattivo il clicklistener del bottone --> evito di inserire due volte la preferenza
					imgbtt.setOnClickListener(null);
				}
			}
			db2.close();
			if(prefAnn==false){
				//carico l'animazione
				animRotate = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate);
				animRotate.setAnimationListener(PaginaRisultati.this);
				
				imgbtt.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View v) {
						
							//trasformo id del parent da stringa a intero
							int num = Integer.parseInt(parent.getId());
							
							String sqlMyPreference="INSERT INTO  MyPreferences (Position, Firm, Description, idDBJobs, User ) VALUES ('"+parent.getPosizione()+"', '"+parent.getAzienda()+"', '"+parent.getChildren().get(0).getDescrizione()+"', "+num+", '"+getIntent().getStringExtra(getPackageName()+".Username")+"');";
				
							SQLiteDatabase db3=dbHelper2.getWritableDatabase();
							db3.execSQL(sqlMyPreference);	
							db3.close();
							Log.i("preferred job announce", "Aggiunto " +parent.getPosizione()+" nell'azienda  "+parent.getAzienda()+" ai preferiti");
							
							//parte l'animazione
							imgbtt.startAnimation(animRotate);
							//imposto stellina gialla
							imgbtt.setImageResource(R.drawable.yellow_star);
						}
				});
			}
			
			return convertView;
		}

		
		// QUESTA  FUNZIONE E' USATA PER FARE L'INFLATE DEL CHILD VIEW
		@Override
		public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parentView)
		{
			//prendo il parent dall'ArrayList
			final Parent parent = parents.get(groupPosition);
			//prendo il child  --> ne avro' solo uno perche' ho creato cosi' la view
			final Child child = parent.getChildren().get(childPosition);
			
			//memorizzo la posizione del parent 
			RicercaLavoro.groupContact.setGroup(groupPosition);
			
			// faccio inflate della row view
			convertView = inflater.inflate(R.layout.childrow, parentView, false);
			
			// SET DEI VALORI DELLA VIEW
			/***INDIRIZZO***/
			((TextView) convertView.findViewById(R.id.TVTags)).setText(child.getIndirizzo());
			ImageView image=(ImageView)convertView.findViewById(R.id.image_tag);
			image.setImageResource(R.drawable.icon_14236);
				
			/** DIRECTION-navigator **/
			Button nav = (Button) convertView.findViewById(R.id.DirectionButton);
			
			Button b0 = (Button) convertView.findViewById(R.id.button1);
			if(child.getIndirizzo().equals("Indirizzo NON presente")){
				//faccio "sparire" il bottone per visualizzare posizione del lavoro su maps e per vedere indicazioni stradali
				b0.setVisibility(View.GONE);
				nav.setVisibility(View.GONE);
				
			}
			
			/** BOTTONE PER LA MAPPA */
			b0.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					//recupero indirizzo, azienda e posizione lavorativa che voglio poi far vedere nel marker
					String indirizzodaconvertire= ""+child.getIndirizzo();
					azienda=""+parent.getAzienda();
					posizione = ""+parent.getPosizione();
	        		
					Intent i = new Intent(PaginaRisultati.this, SeeAddress.class);
					i.putExtra(getPackageName()+".Address", indirizzodaconvertire);
					i.putExtra(getPackageName()+".Azienda",azienda);
					i.putExtra(getPackageName()+".Posizione", posizione);
					
					//inserisco anceh questi valori perche' dovr� passarli a RicercaLavoro.LastView2(...) per 
					//ritornare a questa pagina
					i.putExtra(getPackageName()+".Regione", regione);
					i.putExtra(getPackageName()+".Provincia", provincia);
					i.putExtra(getPackageName()+".Tag", tag);
					i.putExtra(getPackageName()+".Username", username);

					startActivity(i);
					
				}
				
			});
			
			/***LUOGO LAVORO ****/
			((TextView) convertView.findViewById(R.id.TVLoc_prov)).setText(child.getLuogoLavoro());
			
			/** DIRECTION-navigator **/
			nav.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					Intent i = new Intent(PaginaRisultati.this, Navig.class);
					i.putExtra(getPackageName()+".Address", child.getIndirizzo());
					i.putExtra(getPackageName()+".Azienda",azienda);
					i.putExtra(getPackageName()+".Posizione", posizione);
					
					i.putExtra(getPackageName()+".Regione", regione);
					i.putExtra(getPackageName()+".Provincia", provincia);
					i.putExtra(getPackageName()+".Tag", tag);
					i.putExtra(getPackageName()+".Username", username);

					startActivity(i);
				}
				
			});
			
			/***DESCRIZIONE ****/
			((TextView) convertView.findViewById(R.id.TVLoc_regione)).setText(child.getDescrizione());
			ImageView image2=(ImageView)convertView.findViewById(R.id.ImageLoc);
			image2.setImageResource(R.drawable.icon_16301);
			
			/***STIPENDIO ****/
			((TextView) convertView.findViewById(R.id.TWStipendio)).setText(child.getStipendio());
			ImageView image3=(ImageView)convertView.findViewById(R.id.ImageStipendio);
			image3.setImageResource(R.drawable.icon_21213);
			
			/***CANDIDATURA CELLULARE ****/
			((TextView) convertView.findViewById(R.id.TWCandCell)).setText(child.getCandidaturaCellulare());
			ImageView image4=(ImageView)convertView.findViewById(R.id.ImageCandCell);
			image4.setImageResource(R.drawable.icon_14583);
			
			/**** EMAIL ****/
			TextView tv= (TextView) convertView.findViewById(R.id.TWMail);
			tv.setText(child.getEmail());
			
			ImageView image5=(ImageView)convertView.findViewById(R.id.ImageMail);
			image5.setImageResource(R.drawable.icon_98);
			
			
			/***             LINKIFY 
			 * Linkify take a piece of text and a regular expression and turns all of the regex matches in the text 
			 * into clickable links. This is particularly useful for matching things like email addresses, web urls, 
			 * etc. and making them actionable. Alone with the pattern that is to be matched, a url scheme prefix is 
			 * also required. Any pattern match that does not begin with the supplied scheme will have the scheme 
			 * prepended to the matched text when the clickable url is created ***/
			
			Linkify.addLinks(tv, Linkify.EMAIL_ADDRESSES);
			
			
			/*** BOTTONE YOUTUBE
			Button b1 = (Button) convertView.findViewById(R.id.Button01);
			
			b1.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					if(child.getYT().equals("Video non presente")){
						LayoutInflater inflater = getLayoutInflater();
				        View layout = inflater.inflate(R.layout.toast_layout, (ViewGroup) findViewById(R.id.toast_layout_root));
				        ImageView image = (ImageView) layout.findViewById(R.id.immagine);
				        image.setImageDrawable(getResources().getDrawable(R.drawable.youtube_notfind));
				        TextView text = (TextView) layout.findViewById(R.id.ToastTV);
				        text.setText("Video non presente!");

				        Toast toast = new Toast(getApplicationContext());
				        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
				        toast.setDuration(Toast.LENGTH_SHORT);
				        toast.setView(layout);
				        toast.show();
					}else{
						Intent i = new Intent(PaginaRisultati.this, YouTubeDialogActivity.class);
						i.putExtra(getPackageName()+".YTV", child.getYT());
						i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						i.putExtra(getPackageName()+".Regione", regione);
						i.putExtra(getPackageName()+".Provincia", provincia);
						i.putExtra(getPackageName()+".Tag", tag);
						i.putExtra(getPackageName()+".Username", username);
						startActivity(i);
					}
				}
				
			});
			
			/***BOTTONE visibile solo se possible inviare candidatura via cell****/
			Button b = (Button) convertView.findViewById(R.id.candidati);
			if(child.getCandidaturaCellulare().equals("Candidatura via cellulare possibile")==false){
				//ELIMINO il bottone se non e' possibile inviare candidatura
				b.setVisibility(View.GONE);
				
			}
			
			
			//CODICE PER INVIARE LA MAIL --> apre  nuova activity con dati dell'annuncio di lavoro 
			//email destinatario gia' inserita, allegato da scegliere e messaggio da editare
			
			b.setOnClickListener(new OnClickListener(){	
				@Override
				public void onClick(View arg0) {
					
					PersonalDataThread t3 = new PersonalDataThread(username);
					t3.run();
					t3.start();
					do{}while(t3.isAlive());
					
					Intent ii= new Intent (PaginaRisultati.this, MailActivity.class);
						ii.putExtra(getPackageName()+".EmailReceiver", child.getEmail());
						ii.putExtra(getPackageName()+".Posizione", parent.getPosizione());
						ii.putExtra(getPackageName()+".NomeUser", t3.getNomeDB());
						ii.putExtra(getPackageName()+".CognomeUser", t3.getCognomeDB());
						ii.putExtra(getPackageName()+".EmailSender", t3.getMailDB());
						ii.putExtra(getPackageName()+".Azienda", parent.getAzienda());
						
						ii.putExtra(getPackageName()+".Regione", regione);
						ii.putExtra(getPackageName()+".Provincia", provincia);
						ii.putExtra(getPackageName()+".Tag", tag);
						ii.putExtra(getPackageName()+".Username", username);
						
					startActivity(ii);
				}
				
			});
			
			
			//animation quando un child viene aperto (la posso inserire qui perche' solo un child per volta viene aperto)
			Animation animation;
			animation = AnimationUtils.loadAnimation(convertView.getContext(),R.anim.scale_wave);
			convertView.startAnimation(animation);
			animation=null;
			
			return convertView;
		}

		//prende i dati associati al child data la posizione del padre e il numero/posizione del child
		@Override
		public Object getChild(int groupPosition, int childPosition)
		{
			return parents.get(groupPosition).getChildren().get(childPosition);
		}

		//chiamato quando una riga viene premuta
		@Override
		public long getChildId(int groupPosition, int childPosition)
		{
		
			if( ChildClickStatus!=childPosition)
			{
			   ChildClickStatus = childPosition;
			}  
			
			return childPosition;
		}

		@Override
		public int getChildrenCount(int groupPosition)
		{
			
			int size=0;
			if(parents.get(groupPosition).getChildren()!=null){
				size = parents.get(groupPosition).getChildren().size();
			}
			return size;
		}
     
		
		@Override
		public Object getGroup(int groupPosition)
		{			
			return parents.get(groupPosition);
		}

		@Override
		public int getGroupCount()
		{
			return parents.size();
		}

		//Chiamato quando la parent row e' cliccata
		@Override
		public long getGroupId(int groupPosition)
		{			
			return groupPosition;
		}

		@Override
		public void notifyDataSetChanged()
		{
			// Refresh List rows
			super.notifyDataSetChanged();
		}

		@Override
		public boolean isEmpty()
		{
			return ((parents == null) || parents.isEmpty());
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition)
		{
			return true;
		}
		@Override
		public boolean hasStableIds() {
			return false;
		}
		
		
	}

	@Override
	public void onAnimationEnd(Animation animation) {
				
	}


	@Override
	public void onAnimationRepeat(Animation animation) {		
	}


	@Override
	public void onAnimationStart(Animation animation) {

	}
}