package org.gl.jmd.view.admin.listing;

import java.util.ArrayList;
import java.util.HashMap;

import org.gl.jmd.R;
import org.gl.jmd.model.Annee;

import android.app.*;
import android.content.Intent;
import android.os.*;
import android.view.View;
import android.widget.*;

/**
 * Activité correspondant à la vue de listing des semestres d'une année.
 * 
 * @author Jordi CHARPENTIER & Yoann VANHOESERLANDE
 */
public class ListeSemestreA extends Activity {
	
	private Annee a = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.administrateur_liste_semestre);
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
		
		a = (Annee) getIntent().getExtras().getSerializable("annee");
		
		initListe();
	}
	
	private void initListe() {
		final ListView liste = (ListView) findViewById(android.R.id.list);

		final ArrayList<HashMap<String, String>> listItem = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> map;

		map = new HashMap<String, String>();
		map.put("titre", "Semestre 1");
		map.put("decoupage", "SEM1");
		
		listItem.add(map);	
		
		map = new HashMap<String, String>();
		map.put("titre", "Semestre 2");
		map.put("decoupage", "SEM2");
		
		listItem.add(map);		
		
		liste.setAdapter(new SimpleAdapter (getBaseContext(), listItem, R.layout.simple_list, new String[] {"titre"}, new int[] {R.id.titre})); 

		liste.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, final int position, long arg3) {
				Intent newIntent = new Intent(ListeSemestreA.this, ListeUERegleA.class);
				newIntent.putExtra("annee", a);
				newIntent.putExtra("decoupage", listItem.get(position).get("decoupage"));
				
				startActivity(newIntent);
			}
		});
	}
}
