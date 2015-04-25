package com.happem.happem;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ExpandableListActivity;
import android.app.LocalActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class Preferiti  extends ExpandableListActivity implements AnimationListener{
	//Initialize variables
	private static final String STR_CHECKED = " has Checked!";
	private static final String STR_UNCHECKED = " has unChecked!";
	private int ParentClickStatus=-1;
	private int ChildClickStatus=-1;
	//aggiustare creando la classe annunci_parent e annunci_child
	private ArrayList<Parent> pref=new ArrayList<Parent>();
	Context context;
	private Animation animation;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		context=this;
		MyDBHelper dbHelper = new MyDBHelper(this,"JobAroundU_DB", null, 1);
		
		SQLiteDatabase db=dbHelper.getWritableDatabase();
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
		String sqlSavedPreferences="SELECT * FROM MyPreferences WHERE User='"+getIntent().getStringExtra(getPackageName()+".Username")+"';";
		
		Cursor cursor = db.rawQuery(sqlSavedPreferences, null);
		
		if(cursor.getCount()<=0){
			 LayoutInflater inflater = getLayoutInflater();
		        View layout = inflater.inflate(R.layout.toast_layout, (ViewGroup) findViewById(R.id.toast_layout_root));
		        ImageView image = (ImageView) layout.findViewById(R.id.immagine);
		        image.setImageDrawable(getResources().getDrawable(R.drawable.icon_25761));
		        TextView text = (TextView) layout.findViewById(R.id.ToastTV);
		        text.setText("Non ci sono preferiti da visualizzare!!!");

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
		
		
		
		//creo padre e aggiungo ad esso le info
		while (cursor.moveToNext()){
			Parent p= new Parent();
			p.setPosizione(cursor.getString(1));
			p.setAzienza(cursor.getString(2));
			p.setId(cursor.getString(4));
			Log.i("Parent ID", p.getId());
			Child cp = new Child();
			cp.setDescrizione(cursor.getString(3));
			p.setChildren(new ArrayList<Child>());
 	       	p.getChildren().add(cp);
            
 	       	pref.add(p);		 
		
		}
		db.close();
		cursor.close();
		
		//se parents e' vuoto --> messaggio errore --> non esistono preferiti salvati
		if (pref.isEmpty()) {
			 LayoutInflater inflater = getLayoutInflater();
		        View layout = inflater.inflate(R.layout.toast_layout, (ViewGroup) findViewById(R.id.toast_layout_root));
		        ImageView image = (ImageView) layout.findViewById(R.id.immagine);
		        image.setImageDrawable(getResources().getDrawable(R.drawable.icon_25761));
		        TextView text = (TextView) layout.findViewById(R.id.ToastTV);
		        text.setText("Non ci sono preferiti da visualizzare!!!");

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
	
	@Override
	protected void onResume(){
		super.onResume();
		pref.clear();
		MyDBHelper dbHelper = new MyDBHelper(this,"JobAroundU_DB", null, 1);

        SQLiteDatabase db3=dbHelper.getWritableDatabase();

        String sqlSavedPreferences="SELECT * FROM MyPreferences WHERE User='"+getIntent().getStringExtra(getPackageName()+".Username")+"';";

        Cursor cursor = db3.rawQuery(sqlSavedPreferences, null);

        if(cursor.getCount()<=0){
        	 LayoutInflater inflater = getLayoutInflater();
		        View layout = inflater.inflate(R.layout.toast_layout, (ViewGroup) findViewById(R.id.toast_layout_root));
		        ImageView image = (ImageView) layout.findViewById(R.id.immagine);
		        image.setImageDrawable(getResources().getDrawable(R.drawable.icon_25761));
		        TextView text = (TextView) layout.findViewById(R.id.ToastTV);
		        text.setText("Non ci sono preferiti da visualizzare!!!");

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
        
        
        while (cursor.moveToNext()){

            Parent p= new Parent();
            p.setPosizione(cursor.getString(1));
            p.setAzienza(cursor.getString(2));
            p.setId(cursor.getString(4));
            Log.i("Parent ID", p.getId());
            Child cp = new Child();
            cp.setDescrizione(cursor.getString(3));
            p.setChildren(new ArrayList<Child>());
            p.getChildren().add(cp);

            pref.add(p);         

       }
       db3.close();
       cursor.close();
     

        // Refresh ExpandableListView data 
       ((MyExpandableListAdapter)getExpandableListAdapter()).notifyDataSetChanged();
    	
	}
	
	
	private class MyExpandableListAdapter extends BaseExpandableListAdapter{
			private LayoutInflater inflater;
			//protected SQLiteOpenHelper dbHelper2 =new MyDBHelper(PaginaRisultati.this,"JobAroundU_DB", null, 1);

			public MyExpandableListAdapter(){
				// Create Layout Inflator
				inflater = LayoutInflater.from(Preferiti.this);
			}

			
			// This Function used to inflate parent rows view

			@Override
			public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parentView){
				
				final Parent parent = pref.get(groupPosition);
	
				// Inflate grouprow.xml file for parent rows
				convertView = inflater.inflate(R.layout.grouprow_pref, parentView, false); 
	
				// Get grouprow.xml file elements and set values
				((TextView) convertView.findViewById(R.id.text1)).setText(parent.getPosizione());
				((TextView) convertView.findViewById(R.id.TVAzienda)).setText(parent.getAzienda());
				ImageView image=(ImageView)convertView.findViewById(R.id.image_tag);
				image.setImageResource(R.drawable.yellow_star);
				ImageButton bin = (ImageButton)convertView.findViewById(R.id.imageButton_bin);
				bin.setFocusable(false);
				
				
				bin.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(final View v) {
						
						AlertDialog.Builder alt_bld = new AlertDialog.Builder(Preferiti.this);
	                    alt_bld.setMessage("Sei sicuro di voler eliminare questo annuncio dai preferiti?")
	                    .setCancelable(false)
	                    .setPositiveButton("Si", new DialogInterface.OnClickListener() {
	                    public void onClick(DialogInterface dialog, int id)
	                    {
	                    	MyDBHelper dbHelper2 = new MyDBHelper(Preferiti.this,"JobAroundU_DB", null, 1);
	                    	SQLiteDatabase db2=dbHelper2.getWritableDatabase();
	                    	String sqlDelete = "DELETE FROM MyPreferences WHERE idDBJobs="+parent.getId()+" AND User='"+getIntent().getStringExtra(getPackageName()+".Username")+"';";
	                    	db2.execSQL(sqlDelete);
	                    	db2.close();
	                    	pref.clear();

	                        SQLiteDatabase db3=dbHelper2.getWritableDatabase();

	                        String sqlSavedPreferences="SELECT * FROM MyPreferences WHERE User='"+getIntent().getStringExtra(getPackageName()+".Username")+"';";

	                        Cursor cursor = db3.rawQuery(sqlSavedPreferences, null);

	                        if(cursor.getCount()<=0){
	                        	 LayoutInflater inflater = getLayoutInflater();
	             		        View layout = inflater.inflate(R.layout.toast_layout, (ViewGroup) findViewById(R.id.toast_layout_root));
	             		        ImageView image = (ImageView) layout.findViewById(R.id.immagine);
	             		        image.setImageDrawable(getResources().getDrawable(R.drawable.icon_25761));
	             		        TextView text = (TextView) layout.findViewById(R.id.ToastTV);
	             		        text.setText("Non ci sono preferiti da visualizzare!!!");

	             		        Toast toast = new Toast(getApplicationContext());
	             		        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
	             		        toast.setDuration(Toast.LENGTH_SHORT);
	             		        toast.setView(layout);
	             		        toast.show();
	             		        
	             		        String n = getIntent().getStringExtra(getPackageName()+".Username");
	             		        Intent i = new Intent(context, PersonalPage.class);
	             		        i.putExtra(getPackageName()+".Username", n);
	             		        startActivity(i);
	                        }
	                        while (cursor.moveToNext()){

	                            Parent p= new Parent();
	                            p.setPosizione(cursor.getString(1));
	                            p.setAzienza(cursor.getString(2));
	                            p.setId(cursor.getString(4));
	                            Log.i("Parent ID", p.getId());
	                            Child cp = new Child();
	                            cp.setDescrizione(cursor.getString(3));
	                            p.setChildren(new ArrayList<Child>());
	                            p.getChildren().add(cp);

	                            pref.add(p);         

	                       }
	                       db3.close();
	                       cursor.close();
	                     

	                        // Refresh ExpandableListView data 
	                       ((MyExpandableListAdapter)getExpandableListAdapter()).notifyDataSetChanged();
	                    	
	                    	
	                    	//startActivity(new Intent(Preferiti.this, PersonalPage.class).putExtra(getPackageName()+".Username", "MR")); //aggiustare recuperandp valore
	                    }
	                    })
	                    
	                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
	                    public void onClick(DialogInterface dialog, int id) {
	                    //  Action for 'NO' Button
	                    	dialog.cancel();
	                    }
	                    });
	                    
	                    AlertDialog alert = alt_bld.create();
	                    // Title for AlertDialog
	                    alert.setTitle("CANCELLARE QUESTO PREFERITO?");
	                    // Icon for AlertDialog
	                    alert.setIcon(R.drawable.trash_empty);
	                    alert.show();
	 
	
						//elimino il preferito dal db
						Log.i("DELETING", "I'm deleting   "+parent.getId()+"   Position:  "+parent.getPosizione());
						
					}
					
				});
				 
				return convertView;
			}


			// This Function used to inflate child rows view
			@Override
			public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parentView){
				final Parent parent = pref.get(groupPosition);
				final Child child = parent.getChildren().get(childPosition);
	
				// Inflate childrow.xml file for child rows
				convertView = inflater.inflate(R.layout.childrow_pref, parentView, false);
	
				// Get childrow.xml file elements and set values
				/***DESCRIZIONE***/
				((TextView) convertView.findViewById(R.id.TVDescrizione)).setText(child.getDescrizione());
				ImageView image=(ImageView)convertView.findViewById(R.id.image_tag);
				image.setImageResource(R.drawable.icon_16301);

				
				return convertView;
}


@Override
public Object getChild(int groupPosition, int childPosition)
{
	//Log.i("Childs", groupPosition+"=  getChild =="+childPosition);
	return pref.get(groupPosition).getChildren().get(childPosition);
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
	if(pref.get(groupPosition).getChildren()!=null)
		size = pref.get(groupPosition).getChildren().size();
	return size;
}


@Override
public Object getGroup(int groupPosition)
{
	Log.i("Parent", groupPosition+"=  getGroup ");
	
	return pref.get(groupPosition);
}

@Override
public int getGroupCount()
{
	return pref.size();
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
	return ((pref == null) || pref.isEmpty());
}

@Override
public boolean isChildSelectable(int groupPosition, int childPosition)
{
	return true;
}



@Override
public boolean hasStableIds() {
	// TODO Auto-generated method stub
	return false;
}

}


	@Override
	public void onAnimationEnd(Animation animation) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAnimationRepeat(Animation animation) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAnimationStart(Animation animation) {
		// TODO Auto-generated method stub
		
	}

	
}

