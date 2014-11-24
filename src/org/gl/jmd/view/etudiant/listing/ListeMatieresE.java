package org.gl.jmd.view.etudiant.listing;

import java.util.*;

import org.gl.jmd.R;
import org.gl.jmd.model.Annee;
import org.gl.jmd.model.UE;
import org.gl.jmd.view.etudiant.create.SaisieNoteE;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;

/**
 * Activit� correspondant � la vue de listing des mati�res d'une UE (en local).
 * 
 * @author Jordi CHARPENTIER & Yoann VANHOESERLANDE
 */
public class ListeMatieresE extends Activity {
	
	private UE ue = null;
	
	private Annee a = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.etudiant_liste_matiere);
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

		ue = (UE) getIntent().getExtras().getSerializable("ue");
		a = (Annee) getIntent().getExtras().getSerializable("annee");
		
		initListe();
		initTitre();
	}
	
	private void initTitre() {
		// On donne comme titre � la vue le nom de l'UE choisie.
		TextView tvTitre = (TextView) findViewById(R.id.etudiant_liste_matiere_titre);
		tvTitre.setText(ue.getNom());
		
		// isUEValid
		TextView tvIsUEValid = (TextView) findViewById(R.id.etudiant_isUEValid);
		
		if (ue.getMoyenne() != -1.0) {
			if (ue.isValid(a.getListeRegles())) {
				tvIsUEValid.setText("L'UE est valid�e");
			} else if (ue.getMoyenne() != -1.0) {
				tvIsUEValid.setText("L'UE n'est pas valid�e");
			}
		}
	}

	private void initListe() {
		if (ue.getListeMatieres().isEmpty()) {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("titre", "Aucune mati�re.");

			ArrayList<HashMap<String, String>> listItem = new ArrayList<HashMap<String, String>>();
			listItem.add(map);		
			
			ListView liste = (ListView) findViewById(android.R.id.list);
			liste.setAdapter(new SimpleAdapter (getBaseContext(), listItem, R.layout.simple_list, new String[] {"titre"}, new int[] {R.id.titre})); 
		} else {
			final ListView liste = (ListView) findViewById(android.R.id.list);

			final ArrayList<HashMap<String, String>> listItem = new ArrayList<HashMap<String, String>>();
			HashMap<String, String> map;	

			for (int s = 0; s < ue.getListeMatieres().size(); s++) {
				map = new HashMap<String, String>();

				map.put("id", "" + ue.getListeMatieres().get(s).getId());

				if (ue.getListeMatieres().get(s).getNom().length() > 20) {
					map.put("titre", ue.getListeMatieres().get(s).getNom().substring(0, 20) + "...");
				} else {
					map.put("titre", ue.getListeMatieres().get(s).getNom());
				}

				map.put("description", "Coefficient : " + ue.getListeMatieres().get(s).getCoefficient());

				if (ue.getListeMatieres().get(s).isOption()) {
					map.put("isOption", "Option");
				} else {
					map.put("isOption", "Obligatoire");
				}

				map.put("note", "" + ue.getListeMatieres().get(s).getNoteFinale());

				listItem.add(map);		
			}

			liste.setAdapter(new SimpleAdapter (getBaseContext(), listItem, R.layout.etudiant_liste_matiere_list, new String[] {"titre", "description", "isOption", "note"}, new int[] {R.id.titre, R.id.description, R.id.isOption, R.id.note})); 

			liste.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				public void onItemClick(AdapterView<?> arg0, View arg1, final int position, long arg3) {
					Intent act = new Intent(ListeMatieresE.this, SaisieNoteE.class);
					act.putExtra("matiere", ue.getListeMatieres().get(position));

					startActivity(act);
				}
			});

			// Cache le badge si aucune note n'a �t� entr�e.
			liste.post(new Runnable() {
				@Override
				public void run() {
					for (int i = 0; i < liste.getCount(); i++) {
						View tempView = (View) liste.getChildAt(i);
						TextView badgeNote = (TextView) tempView.findViewById(R.id.note);

						if (ue.getListeMatieres().get(i).getNoteFinale() >= 10) {							
							badgeNote.setBackgroundResource(R.drawable.badge_moyenne_ok);
						} else if ((ue.getListeMatieres().get(i).getNoteFinale() < 10) && 
								(ue.getListeMatieres().get(i).getNoteFinale() >= 0)) {
							
							badgeNote.setBackgroundResource(R.drawable.badge_moyenne_nok);
						} else {
							badgeNote.setText("0 note");
							badgeNote.setBackgroundResource(R.drawable.badge_note);
						}
					}
				}
			});
		}
	}

	/* M�thodes h�rit�es de la classe Activity. */

	/**
	 * M�thode permettant d'emp�cher la reconstruction de la vue lors de la rotation de l'�cran. 
	 * 
	 * @param newConfig L'�tat de la vue avant la rotation.
	 */
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	/**
	 * M�thode ex�cut�e lorsque l'activit� est relanc�e.<br />
	 * Ici, �a permet d'actualiser la liste des ann�es lorsqu'une ann�e vient d'�tre cr�� et que l'application ram�ne l'utilisateur sur cette vue de listing.
	 */
	@Override
	public void onRestart() {		
		initListe();
		initTitre();

		super.onRestart();
	} 
}