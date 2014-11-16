package org.gl.jmd.view.etudiant.listing;

import java.util.*;

import org.gl.jmd.R;
import org.gl.jmd.dao.EtudiantDAO;
import org.gl.jmd.model.Etudiant;
import org.gl.jmd.view.etudiant.create.SaisieNoteE;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;

/**
 * Activité correspondant à la vue de listing des matières d'une UE (en local).
 * 
 * @author Jordi CHARPENTIER & Yoann VANHOESERLANDE
 */
public class ListeMatieresE extends Activity {

	private int idDiplome = 0;

	private int idAnnee = 0;

	private int idUe = 0;

	private String nomUE = "";

	private Etudiant etud = EtudiantDAO.load();
	
	private int posDip = 0;
	
	private int posAnn = 0;
	
	private int posUe = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.etudiant_liste_matiere);
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

		idDiplome = getIntent().getExtras().getInt("idDiplome");
		idAnnee = getIntent().getExtras().getInt("idAnnee");
		idUe = getIntent().getExtras().getInt("idUE");

		nomUE = getIntent().getExtras().getString("nomUE");

		initListe();
		initTitre();
	}
	
	private void initTitre() {
		// On donne comme titre à la vue le nom de l'UE choisie.
		TextView tvTitre = (TextView) findViewById(R.id.etudiant_liste_matiere_titre);
		tvTitre.setText(nomUE);
		
		// isUEValid
		TextView tvIsUEValid = (TextView) findViewById(R.id.etudiant_isUEValid);
		
		if (etud.getListeDiplomes().get(posDip).getListeAnnees().get(posAnn).getListeUE().get(posUe).getMoyenne() != -1.0) {
			if (etud.getListeDiplomes().get(posDip).getListeAnnees().get(posAnn).getListeUE().get(posUe).isValid(etud.getListeDiplomes().get(posDip).getListeAnnees().get(posAnn).getListeRegles())) {
				tvIsUEValid.setText("L'UE est validée");
			} else if (etud.getListeDiplomes().get(posDip).getListeAnnees().get(posAnn).getListeUE().get(posUe).getMoyenne() != -1.0) {
				tvIsUEValid.setText("L'UE n'est pas validée");
			}
		}
	}

	private void initListe() {
		for (int i = 0; i < this.etud.getListeDiplomes().size(); i++) {
			if (this.etud.getListeDiplomes().get(i).getId() == this.idDiplome) {
				posDip = i;
			}
		}

		for (int i = 0; i < this.etud.getListeDiplomes().size(); i++) {
			for (int j = 0; j < this.etud.getListeDiplomes().get(i).getListeAnnees().size(); j++) {
				if (this.etud.getListeDiplomes().get(i).getListeAnnees().get(j).getId() == this.idAnnee) {
					posAnn = j;
				}
			}
		}

		for (int i = 0; i < this.etud.getListeDiplomes().size(); i++) {
			for (int j = 0; j < this.etud.getListeDiplomes().get(i).getListeAnnees().size(); j++) {
				for (int k = 0; k < this.etud.getListeDiplomes().get(i).getListeAnnees().get(j).getListeUE().size(); k++) {
					if (this.etud.getListeDiplomes().get(i).getListeAnnees().get(j).getListeUE().get(k).getId() == this.idUe) {
						posUe = k;
					}
				}
			}
		}

		final int posDipFin = posDip;
		final int posAnnFin = posAnn;
		final int posUeFin = posUe;

		if (etud.getListeDiplomes().get(posDipFin).getListeAnnees().get(posAnnFin).getListeUE().get(posUeFin).getListeMatieres().isEmpty()) {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("titre", "Aucune matière.");

			ArrayList<HashMap<String, String>> listItem = new ArrayList<HashMap<String, String>>();
			listItem.add(map);		
			
			ListView liste = (ListView) findViewById(android.R.id.list);
			liste.setAdapter(new SimpleAdapter (getBaseContext(), listItem, R.layout.simple_list, new String[] {"titre"}, new int[] {R.id.titre})); 
		} else {
			final ListView liste = (ListView) findViewById(android.R.id.list);

			final ArrayList<HashMap<String, String>> listItem = new ArrayList<HashMap<String, String>>();
			HashMap<String, String> map;	

			for (int s = 0; s < etud.getListeDiplomes().get(posDipFin).getListeAnnees().get(posAnnFin).getListeUE().get(posUeFin).getListeMatieres().size(); s++) {
				map = new HashMap<String, String>();

				map.put("id", "" + etud.getListeDiplomes().get(posDipFin).getListeAnnees().get(posAnnFin).getListeUE().get(posUeFin).getListeMatieres().get(s).getId());

				if (etud.getListeDiplomes().get(posDipFin).getListeAnnees().get(posAnnFin).getListeUE().get(posUeFin).getListeMatieres().get(s).getNom().length() > 20) {
					map.put("titre", etud.getListeDiplomes().get(posDipFin).getListeAnnees().get(posAnnFin).getListeUE().get(posUeFin).getListeMatieres().get(s).getNom().substring(0, 20) + "...");
				} else {
					map.put("titre", etud.getListeDiplomes().get(posDipFin).getListeAnnees().get(posAnnFin).getListeUE().get(posUeFin).getListeMatieres().get(s).getNom());
				}

				map.put("description", "Coefficient : " + etud.getListeDiplomes().get(posDipFin).getListeAnnees().get(posAnnFin).getListeUE().get(posUeFin).getListeMatieres().get(s).getCoefficient());

				if (etud.getListeDiplomes().get(posDipFin).getListeAnnees().get(posAnnFin).getListeUE().get(posUeFin).getListeMatieres().get(s).isOption()) {
					map.put("isOption", "Option");
				} else {
					map.put("isOption", "Obligatoire");
				}

				map.put("note", "" + etud.getListeDiplomes().get(posDipFin).getListeAnnees().get(posAnnFin).getListeUE().get(posUeFin).getListeMatieres().get(s).getNoteFinale());

				listItem.add(map);		
			}

			liste.setAdapter(new SimpleAdapter (getBaseContext(), listItem, R.layout.etudiant_liste_matiere_list, new String[] {"titre", "description", "isOption", "note"}, new int[] {R.id.titre, R.id.description, R.id.isOption, R.id.note})); 

			liste.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				public void onItemClick(AdapterView<?> arg0, View arg1, final int position, long arg3) {
					Intent act = new Intent(ListeMatieresE.this, SaisieNoteE.class);
					act.putExtra("matiere", etud.getListeDiplomes().get(posDipFin).getListeAnnees().get(posAnnFin).getListeUE().get(posUeFin).getListeMatieres().get(position));

					startActivity(act);
				}
			});

			// Cache le badge si aucune note n'a été entrée.
			liste.post(new Runnable() {
				@Override
				public void run() {
					for (int i = 0; i < liste.getCount(); i++) {
						View tempView = (View) liste.getChildAt(i);
						TextView badgeNote = (TextView) tempView.findViewById(R.id.note);

						if (etud.getListeDiplomes().get(posDipFin).getListeAnnees().get(posAnnFin).getListeUE().get(posUeFin).getListeMatieres().get(i).getNoteFinale() >= 10) {							
							badgeNote.setBackgroundResource(R.drawable.badge_moyenne_ok);
						} else if ((etud.getListeDiplomes().get(posDipFin).getListeAnnees().get(posAnnFin).getListeUE().get(posUeFin).getListeMatieres().get(i).getNoteFinale() < 10) && 
								(etud.getListeDiplomes().get(posDipFin).getListeAnnees().get(posAnnFin).getListeUE().get(posUeFin).getListeMatieres().get(i).getNoteFinale() >= 0)) {
							
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
	 * Méthode exécutée lorsque l'activité est relancée.<br />
	 * Ici, ça permet d'actualiser la liste des années lorsqu'une année vient d'être créé et que l'application ramène l'utilisateur sur cette vue de listing.
	 */
	@Override
	public void onRestart() {
		etud = EtudiantDAO.load();
		
		initListe();
		initTitre();

		super.onRestart();
	} 
}