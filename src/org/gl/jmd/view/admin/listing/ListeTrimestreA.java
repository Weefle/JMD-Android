package org.gl.jmd.view.admin.listing;

import java.util.*;

import org.gl.jmd.R;

import android.app.*;
import android.content.Intent;
import android.os.*;
import android.view.View;
import android.widget.*;

/**
 * Activité correspondant à la vue de listing des trimestres d'une année.
 * 
 * @author Jordi CHARPENTIER & Yoann VANHOESERLANDE
 */
public class ListeTrimestreA extends Activity {
	
	private String idAnnee = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.administrateur_liste_semestre);
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
		
		idAnnee = getIntent().getExtras().getString("idAnnee");
		
		initListe();
	}
	
	private void initListe() {
		final ListView liste = (ListView) findViewById(android.R.id.list);

		final ArrayList<HashMap<String, String>> listItem = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> map;

		map = new HashMap<String, String>();
		map.put("titre", "Trimestre 1");
		map.put("decoupage", "TRI1");
		
		listItem.add(map);	
		
		map = new HashMap<String, String>();
		map.put("titre", "Trimestre 2");
		map.put("decoupage", "TRI2");
		
		listItem.add(map);		
		
		map = new HashMap<String, String>();
		map.put("titre", "Trimestre 3");
		map.put("decoupage", "TRI3");
		
		listItem.add(map);	

		final SimpleAdapter mSchedule = new SimpleAdapter (getBaseContext(), listItem, R.layout.admin_simple_list, new String[] {"titre"}, new int[] {R.id.titre});

		liste.setAdapter(mSchedule); 

		liste.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, final int position, long arg3) {
				Intent newIntent = new Intent(ListeTrimestreA.this, ListeUERegleA.class);

				newIntent.putExtra("idAnnee", idAnnee);
				newIntent.putExtra("decoupage", listItem.get(position).get("decoupage"));
				
				startActivity(newIntent);
			}
		});
	}
}
