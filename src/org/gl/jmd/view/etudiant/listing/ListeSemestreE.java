package org.gl.jmd.view.etudiant.listing;

import java.util.ArrayList;
import java.util.HashMap;

import org.gl.jmd.R;

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
public class ListeSemestreE extends Activity {
	
	private int positionDip = 0;
	
	private int positionAnn = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.etudiant_liste_semestre);
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
		
		positionDip = getIntent().getExtras().getInt("positionDip");
		positionAnn = getIntent().getExtras().getInt("positionAnn");
		
		initListe();
	}
	
	private void initListe() {
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
		
		ListView liste = (ListView) findViewById(android.R.id.list);

		liste.setAdapter(new SimpleAdapter (getBaseContext(), listItem, R.layout.etudiant_simple_list, new String[] {"titre"}, new int[] {R.id.titre})); 

		liste.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, final int position, long arg3) {
				Intent newIntent = new Intent(ListeSemestreE.this, ListeUEE.class);
				newIntent.putExtra("positionDip", positionDip);
				newIntent.putExtra("positionDip", positionAnn);
				newIntent.putExtra("decoupage", listItem.get(position).get("decoupage"));
				
				startActivity(newIntent);
			}
		});
	}
}
