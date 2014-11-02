package org.gl.jmd.view.etudiant.listing;

import java.util.*;

import org.gl.jmd.R;
import org.gl.jmd.dao.EtudiantDAO;
import org.gl.jmd.model.Annee;
import org.gl.jmd.model.Diplome;
import org.gl.jmd.model.enumeration.DecoupageYearType;
import org.gl.jmd.model.user.Etudiant;
import org.gl.jmd.view.etudiant.StatsAnnee;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import android.app.*;
import android.content.Intent;
import android.content.res.Configuration;

/**
 * Activité correspondant à la vue de listing des UE d'une année (en local).
 * 
 * @author Jordi CHARPENTIER & Yoann VANHOESERLANDE
 */
public class ListeUEE extends Activity {
	
	private int positionDip = 0;
	
	private int positionAnn = 0;
	
	private String decoupage = null;

	private Etudiant etud = EtudiantDAO.load();

	private Annee ann;
	
	private Diplome d;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.etudiant_liste_ue);
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

		positionDip = getIntent().getExtras().getInt("positionDip");
		positionAnn = getIntent().getExtras().getInt("positionAnn");
		decoupage = getIntent().getExtras().getString("decoupage");
		
		d = etud.getListeDiplomes().get(positionDip);
		ann = etud.getListeDiplomes().get(positionDip).getListeAnnees().get(positionAnn);
		
		initListe();
		initTitleView();
	}

	private void initTitleView() {
		TextView tvTitre = (TextView) findViewById(R.id.etudiant_liste_ue_titre);
		tvTitre.setText(ann.getNom().substring(0, 20) + "...");
	}

	/**
	 * Méthode déclenchée lors d'un click sur le bouton de simulation.
	 * 
	 * @param view La vue lors du click sur le bouton de simulation.
	 */
	public void simulerObtentionAnnee(View view) {
		Intent i = new Intent(ListeUEE.this, StatsAnnee.class);
		i.putExtra("annee", ann);
		
		startActivity(i);
	}

	private void initListe() {
		if (etud.getListeDiplomes().get(positionDip).getListeAnnees().get(positionAnn).getListeUE().isEmpty()) {
			final ListView liste = (ListView) findViewById(android.R.id.list);
			final ArrayList<HashMap<String, String>> listItem = new ArrayList<HashMap<String, String>>();

			HashMap<String, String> map = new HashMap<String, String>();

			map.put("titre", "Aucune UE.");

			listItem.add(map);		

			liste.setAdapter(new SimpleAdapter (getBaseContext(), listItem, R.layout.etudiant_simple_list, new String[] {"titre"}, new int[] {R.id.titre})); 
		} else {
			final ListView liste = (ListView) findViewById(android.R.id.list);

			final ArrayList<HashMap<String, String>> listItem = new ArrayList<HashMap<String, String>>();
			HashMap<String, String> map;	

			for(int s = 0; s < etud.getListeDiplomes().get(positionDip).getListeAnnees().get(positionAnn).getListeUE().size(); s++) {
				if (ann.getListeUE().get(s).getDecoupage().equals(DecoupageYearType.valueOf(decoupage))) {
					map = new HashMap<String, String>();
	
					map.put("id", "" + etud.getListeDiplomes().get(positionDip).getListeAnnees().get(positionAnn).getListeUE().get(s).getId());
					map.put("titre", etud.getListeDiplomes().get(positionDip).getListeAnnees().get(positionAnn).getListeUE().get(s).getNom());
	
					listItem.add(map);		
				}
			}

			final SimpleAdapter mSchedule = new SimpleAdapter (getBaseContext(), listItem, R.layout.etudiant_simple_list, new String[] {"titre"}, new int[] {R.id.titre});

			liste.setAdapter(mSchedule); 

			liste.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				public void onItemClick(AdapterView<?> arg0, View arg1, final int position, long arg3) {
					Intent act = new Intent(ListeUEE.this, ListeMatieresE.class);
					act.putExtra("idDiplome", d.getId());
					act.putExtra("idAnnee", ann.getId());
					act.putExtra("idUE", Integer.parseInt(listItem.get(position).get("id")));
					act.putExtra("nomUE", ann.getListeUE().get(position).getNom());

					startActivity(act);
				}
			});
		}
	}

	/* Méthodes héritées de la classe Activity. */

	/**
	 * Méthode permettant d'empécher la reconstruction de la vue lors de la rotation de l'écran. 
	 * 
	 * @param newConfig L'état de la vue avant la rotation.
	 */
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	/**
	 * Méthode exécutée lorsque l'activité est relancée.
	 */
	@Override
	public void onRestart() {
		etud = EtudiantDAO.load();
		initListe();

		super.onRestart();
	} 
}