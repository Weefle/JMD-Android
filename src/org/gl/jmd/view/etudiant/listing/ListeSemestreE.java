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
	
	private Intent lastIntent;
	
	private String idAnnee = "";
	
	private String idDiplome = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.etudiant_liste_semestre);
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
		
		lastIntent = getIntent();
		
		idAnnee = lastIntent.getExtras().getString("idAnnee");
		idDiplome = lastIntent.getExtras().getString("idDiplome");
		
		initListe();
	}
	
	public void initListe() {
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

		final SimpleAdapter mSchedule = new SimpleAdapter (getBaseContext(), listItem, R.layout.etudiant_liste_semestre_list, new String[] {"titre"}, new int[] {R.id.titre});

		liste.setAdapter(mSchedule); 

		liste.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, final int position, long arg3) {
				Intent newIntent = new Intent(ListeSemestreE.this, ListeUEE.class);
				
				newIntent.putExtra("idDiplome", "" + idDiplome);
				newIntent.putExtra("idAnnee", "" + idAnnee);
				newIntent.putExtra("decoupage", listItem.get(position).get("decoupage"));
				newIntent.putExtra("titre", listItem.get(position).get("titre"));
				
				startActivity(newIntent);
			}
		});
	}
}
