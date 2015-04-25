package com.happem.happem;



import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.view.View.OnClickListener;
import android.app.ExpandableListActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class RicercheRecenti extends ExpandableListActivity {
	
	//Initialize variables
		private static final String STR_CHECKED = " has Checked!";
		private static final String STR_UNCHECKED = " has unChecked!";
		private int ParentClickStatus=-1;
		private int ChildClickStatus=-1;
		//aggiustare creando la classe annunci_parent e annunci_child
		private ArrayList<Parent_Pref> parents=new ArrayList<Parent_Pref>();
		private String username;
		@Override
		protected void onResume(){
			super.onResume();

			//RECUPERO RICERCHE RECENTI 
			String sqlRecent="SELECT * FROM RicercheRecenti WHERE User='"+getIntent().getStringExtra(getPackageName()+".Username")+"' ORDER BY Data DESC";
			MyDBHelper dbHelper = new MyDBHelper(this,"JobAroundU_DB", null, 1);
			final SQLiteDatabase db=dbHelper.getReadableDatabase();
			Cursor cursor = db.rawQuery(sqlRecent, null);
			//creo padre e aggiungo ad esso le info
			if(cursor.getCount()<=0){
				 LayoutInflater inflater = getLayoutInflater();
			        View layout = inflater.inflate(R.layout.toast_layout, (ViewGroup) findViewById(R.id.toast_layout_root));
			        ImageView image = (ImageView) layout.findViewById(R.id.immagine);
			        image.setImageDrawable(getResources().getDrawable(R.drawable.icon_25761));
			        TextView text = (TextView) layout.findViewById(R.id.ToastTV);
			        text.setText("Non ci sono ricerche recenti da visualizzare!!!");

			        Toast toast = new Toast(getApplicationContext());
			        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
			        toast.setDuration(Toast.LENGTH_SHORT);
			        toast.setView(layout);
			        toast.show();
			        
			        String n = getIntent().getStringExtra(getPackageName()+".Username");
			        Intent i = new Intent(this, PersonalPage.class);
			        i.putExtra(getPackageName()+".Username", n);
			        startActivity(i);
			}
			while(cursor.moveToNext()){
				Log.i("CICLO ", "STO ANALIZZANDO  DATA   "+cursor.getString(1)+"    "+cursor.getString(3)+"   "+cursor.getString(2) + "   "+cursor.getString(4));
				Parent_Pref p = new Parent_Pref();
				Date d= this.convertiData(cursor.getString(1));
				
				//controllo se esiste un parent gia' con quella data 
				if(parents.size()==0){
					Log.i("", "NON ESISTONO PARENT");
					p.setData(d);
					Child_rr c= new Child_rr();
					c.setProvincia(cursor.getString(3));
					c.setRegione(cursor.getString(2));
					c.setTag(cursor.getString(4));
					p.setChildren(new ArrayList<Child_rr>());
					p.getChildren().add(c);
					parents.add(p);
					Log.i("AGGIUNTO CHILD", "e' stato aggiunto un child al parent (data:  "+p.getData()+")");
					
				}else{
					//faccio controllo perch� esistono gia' dei parent
					//per ogni parent devo controllare se i valori prensenti nel cursore sono gia' stati inseriti oppure no
					boolean stessadata=false;
					
					for(int i=0; i<parents.size(); i++){
						Parent_Pref pp = parents.get(i);
						//controllo se la data e' la stessa
						if(d.equals(pp.getData())){
							Log.i("check", "il parent esiste gia', provo a vedere se non esiste il child");
							stessadata=true;
							boolean esistegia=false;
							//Creo un child che sarebbe quello da aggiungere
							Child_rr c= new Child_rr();
							c.setProvincia(cursor.getString(3));
							c.setRegione(cursor.getString(2));
							c.setTag(cursor.getString(4));
							//scorro tra i vari child del parent e vedo se uno coincide con quelli gia' inseriti
							ArrayList<Child_rr> cp = pp.getChildren();
							for(int j=0; j<cp.size(); j++){
								Child_rr r= cp.get(j);
								if(r.getProvincia().equals(c.getProvincia()) && r.getRegione().equals(c.getRegione()) && r.getTag().equals(c.getTag())){
									esistegia=true;
									Log.i("check child existence", "il child esiste gia', non aggiungo");
								}
							}
							if(esistegia==false){
								pp.getChildren().add(c);
								Log.i("check child existence", "il parent esiste ma non il child, lo agiungo");
							}
						}
					}
					if(stessadata==false){
						//creare e aggiungere padre e child
						p.setData(d);
						p.setChildren(new ArrayList<Child_rr>());
						Child_rr c2= new Child_rr();
						c2.setProvincia(cursor.getString(3));
						c2.setRegione(cursor.getString(2));
						c2.setTag(cursor.getString(4));
						p.getChildren().add(c2);
						parents.add(p);
						Log.i("check", "parent e child non esistono, li aggiungo");
					}
				}
			}
			
			//se parents e' vuoto --> messaggio errore --> non esistono preferiti salvati
			if (parents.isEmpty()) {
				 LayoutInflater inflater = getLayoutInflater();
			        View layout = inflater.inflate(R.layout.toast_layout, (ViewGroup) findViewById(R.id.toast_layout_root));
			        ImageView image = (ImageView) layout.findViewById(R.id.immagine);
			        image.setImageDrawable(getResources().getDrawable(R.drawable.icon_25761));
			        TextView text = (TextView) layout.findViewById(R.id.ToastTV);
			        text.setText("Non ci sono ricerche recenti da visualizzare!!!");

			        Toast toast = new Toast(getApplicationContext());
			        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
			        toast.setDuration(Toast.LENGTH_SHORT);
			        toast.setView(layout);
			        toast.show();
			        
			        String n = getIntent().getStringExtra(getPackageName()+".Username");
			        Intent i = new Intent(this, PersonalPage.class);
			        i.putExtra(getPackageName()+".Username", n);
			        startActivity(i);
			}
			((MyExpandableListAdapter)getExpandableListAdapter()).notifyDataSetChanged();

		}
		
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			
			username = getIntent().getStringExtra(getPackageName()+".Username");
			
			MyDBHelper dbHelper = new MyDBHelper(this,"JobAroundU_DB", null, 1);
			final SQLiteDatabase db=dbHelper.getReadableDatabase();
			
			Resources res = this.getResources();
		    Drawable devider = res.getDrawable(R.drawable.transparent);
		    Drawable group_indicator = res.getDrawable(R.drawable.group_indicator);

		    // Set ExpandableListView values 
		    getExpandableListView().setGroupIndicator(group_indicator);
			getExpandableListView().setDivider(devider);
			getExpandableListView().setChildDivider(devider);
			getExpandableListView().setDividerHeight(15);
			registerForContextMenu(getExpandableListView());
		
			//RECUPERO RICERCHE RECENTI 
			String sqlRecent="SELECT * FROM RicercheRecenti WHERE User='"+getIntent().getStringExtra(getPackageName()+".Username")+"' ORDER BY Data DESC";
			
			Cursor cursor = db.rawQuery(sqlRecent, null);
			//creo padre e aggiungo ad esso le info
			if(cursor.getCount()<=0){
				 LayoutInflater inflater = getLayoutInflater();
			        View layout = inflater.inflate(R.layout.toast_layout, (ViewGroup) findViewById(R.id.toast_layout_root));
			        ImageView image = (ImageView) layout.findViewById(R.id.immagine);
			        image.setImageDrawable(getResources().getDrawable(R.drawable.icon_25761));
			        TextView text = (TextView) layout.findViewById(R.id.ToastTV);
			        text.setText("Non ci sono ricerche recenti da visualizzare!!!");

			        Toast toast = new Toast(getApplicationContext());
			        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
			        toast.setDuration(Toast.LENGTH_SHORT);
			        toast.setView(layout);
			        toast.show();
			        
			        String n = getIntent().getStringExtra(getPackageName()+".Username");
			        Intent i = new Intent(this, PersonalPage.class);
			        i.putExtra(getPackageName()+".Username", n);
			        startActivity(i);
			}
			while(cursor.moveToNext()){
				Log.i("CICLO ", "STO ANALIZZANDO  DATA   "+cursor.getString(1)+"    "+cursor.getString(3)+"   "+cursor.getString(2) + "   "+cursor.getString(4));
				Parent_Pref p = new Parent_Pref();
				Date d= this.convertiData(cursor.getString(1));
				
				//controllo se esiste un parent gia' con quella data 
				if(parents.size()==0){
					Log.i("", "NON ESISTONO PARENT");
					p.setData(d);
					Child_rr c= new Child_rr();
					c.setProvincia(cursor.getString(3));
					c.setRegione(cursor.getString(2));
					c.setTag(cursor.getString(4));
					p.setChildren(new ArrayList<Child_rr>());
					p.getChildren().add(c);
					parents.add(p);
					Log.i("AGGIUNTO CHILD", "e' stato aggiunto un child al parent (data:  "+p.getData()+")");
					
				}else{
					//faccio controllo perch� esistono gia' dei parent
					//per ogni parent devo controllare se i valori prensenti nel cursore sono gia' stati inseriti oppure no
					boolean stessadata=false;
					
					for(int i=0; i<parents.size(); i++){
						Parent_Pref pp = parents.get(i);
						//controllo se la data e' la stessa
						if(d.equals(pp.getData())){
							Log.i("check", "il parent esiste gia', provo a vedere se non esiste il child");
							stessadata=true;
							boolean esistegia=false;
							//Creo un child che sarebbe quello da aggiungere
							Child_rr c= new Child_rr();
							c.setProvincia(cursor.getString(3));
							c.setRegione(cursor.getString(2));
							c.setTag(cursor.getString(4));
							//scorro tra i vari child del parent e vedo se uno coincide con quelli gia' inseriti
							ArrayList<Child_rr> cp = pp.getChildren();
							for(int j=0; j<cp.size(); j++){
								Child_rr r= cp.get(j);
								if(r.getProvincia().equals(c.getProvincia()) && r.getRegione().equals(c.getRegione()) && r.getTag().equals(c.getTag())){
									esistegia=true;
									Log.i("check child existence", "il child esiste gia', non aggiungo");
								}
							}
							if(esistegia==false){
								pp.getChildren().add(c);
								Log.i("check child existence", "il parent esiste ma non il child, lo agiungo");
							}
						}
					}
					if(stessadata==false){
						//creare e aggiungere padre e child
						p.setData(d);
						p.setChildren(new ArrayList<Child_rr>());
						Child_rr c2= new Child_rr();
						c2.setProvincia(cursor.getString(3));
						c2.setRegione(cursor.getString(2));
						c2.setTag(cursor.getString(4));
						p.getChildren().add(c2);
						parents.add(p);
						Log.i("check", "parent e child non esistono, li aggiungo");
					}
				}
			}
			
			//se parents e' vuoto --> messaggio errore --> non esistono preferiti salvati
			if (parents.isEmpty()) {
				 LayoutInflater inflater = getLayoutInflater();
			        View layout = inflater.inflate(R.layout.toast_layout, (ViewGroup) findViewById(R.id.toast_layout_root));
			        ImageView image = (ImageView) layout.findViewById(R.id.immagine);
			        image.setImageDrawable(getResources().getDrawable(R.drawable.icon_25761));
			        TextView text = (TextView) layout.findViewById(R.id.ToastTV);
			        text.setText("Non ci sono ricerche recenti da visualizzare!!!");

			        Toast toast = new Toast(getApplicationContext());
			        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
			        toast.setDuration(Toast.LENGTH_SHORT);
			        toast.setView(layout);
			        toast.show();
			        
			        String n = getIntent().getStringExtra(getPackageName()+".Username");
			        Intent i = new Intent(this, PersonalPage.class);
			        i.putExtra(getPackageName()+".Username", n);
			        startActivity(i);
			}
			
			if (this.getExpandableListAdapter() == null)		{
				//Create ExpandableListAdapter Object
				final MyExpandableListAdapter mAdapter = new MyExpandableListAdapter();
				// Set Adapter to ExpandableList Adapter
				this.setListAdapter(mAdapter);
			}
			else
			{
				 // Refresh ExpandableListView data 
				((MyExpandableListAdapter)getExpandableListAdapter()).notifyDataSetChanged();
		
			}

		}
		
		private Date convertiData(String string) {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
	        try {
				Date d = simpleDateFormat.parse(string);
				return d;
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        
			return null;
		}

		private class MyExpandableListAdapter extends BaseExpandableListAdapter{
				private LayoutInflater inflater;
				//protected SQLiteOpenHelper dbHelper2 =new MyDBHelper(PaginaRisultati.this,"JobAroundU_DB", null, 1);

				public MyExpandableListAdapter(){
					// Create Layout Inflator
					inflater = LayoutInflater.from(RicercheRecenti.this);
				}


				// This Function used to inflate parent rows view

				@Override
				public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parentView){
					
					final Parent_Pref parent = parents.get(groupPosition);
		
					// Inflate grouprow.xml file for parent rows
					convertView = inflater.inflate(R.layout.grouprow_rr, parentView, false); 
		
					// Get grouprow.xml file elements and set values
					int y =(int)parent.getData().getYear()+(int)1900 ;
					((TextView) convertView.findViewById(R.id.TVData)).setText(""+parent.getData().getDate()+" / "+parent.getData().getMonth()+" / "+y);
					return convertView;
				}


				// This Function used to inflate child rows view
				@Override
				public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parentView){
					final Parent_Pref parent = parents.get(groupPosition);
					final Child_rr child = parent.getChildren().get(childPosition);
		
					// Inflate childrow.xml file for child rows
					convertView = inflater.inflate(R.layout.childrow_rr, parentView, false);
		
					// Get childrow.xml file elements and set values
					/***TAGS***/
					((TextView) convertView.findViewById(R.id.TVTags)).setText(child.getTag());
					/***Regione - Provincia***/
					((TextView) convertView.findViewById(R.id.TVLoc_regione)).setText("Regione: "+child.getRegione());
					((TextView) convertView.findViewById(R.id.TVLoc_prov)).setText("Provincia: "+child.getProvincia());
					
					ImageButton ib= (ImageButton) convertView.findViewById(R.id.ImageButton12);
					ib.setOnClickListener(new OnClickListener(){

						@Override
						public void onClick(View v) {
							
							String[] tagRicerca=child.getTag().trim().split(",");
							RicercaLavoro.groupContact.LastView2(child.getRegione(), child.getProvincia(), tagRicerca, username);
						}
						
					});
		
					return convertView;
	}


	@Override
	public Object getChild(int groupPosition, int childPosition)
	{
		//Log.i("Childs", groupPosition+"=  getChild =="+childPosition);
		return parents.get(groupPosition).getChildren().get(childPosition);
	}

	//Call when child row clicked
	@Override
	public long getChildId(int groupPosition, int childPosition)
	{
		/****** When Child row clicked then this function call *******/
		
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
		if(parents.get(groupPosition).getChildren()!=null)
			size = parents.get(groupPosition).getChildren().size();
		return size;
	}


	@Override
	public Object getGroup(int groupPosition)
	{
		Log.i("Parent", groupPosition+"=  getGroup ");
		
		return parents.get(groupPosition);
	}

	@Override
	public int getGroupCount()
	{
		return parents.size();
	}

	//Call when parent row clicked
	@Override
	public long getGroupId(int groupPosition)
	{
		ParentClickStatus=groupPosition;
		if(ParentClickStatus==0)
			ParentClickStatus=-1;
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


	/******************* Checkbox Checked Change Listener ********************/

	private final class CheckUpdateListener implements OnCheckedChangeListener
	{
		private final Parent parent;
		
		private CheckUpdateListener(Parent parent)
		{
			this.parent = parent;
		}
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
		{
			Log.i("onCheckedChanged", "isChecked: "+isChecked);
			parent.setChecked(isChecked);
			
			((MyExpandableListAdapter)getExpandableListAdapter()).notifyDataSetChanged();
			
			final Boolean checked = parent.isChecked();
		}
	}
	/***********************************************************************/


	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return false;
	}

	}

}
