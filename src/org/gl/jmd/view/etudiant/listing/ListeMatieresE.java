package org.gl.jmd.view.etudiant.listing;

import java.util.*;

import org.gl.jmd.R;
import org.gl.jmd.dao.EtudiantDAO;
import org.gl.jmd.model.user.Etudiant;
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

	private int idDiplome = 0;

	private int idAnnee = 0;

	private int idUe = 0;

	private String nomUE = "";

	private Etudiant etud = EtudiantDAO.load();

	private Intent lastIntent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.etudiant_liste_matiere);
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

		lastIntent = getIntent();

		idDiplome = lastIntent.getExtras().getInt("idDiplome");
		idAnnee = lastIntent.getExtras().getInt("idAnnee");
		idUe = lastIntent.getExtras().getInt("idUE");

		nomUE = lastIntent.getExtras().getString("nomUE");

		initListe();

		// On donne comme titre � la vue le nom de l'UE choisie.
		TextView tvTitre = (TextView) findViewById(R.id.etudiant_liste_matiere_titre);
		tvTitre.setText(nomUE);
	}

	public void initListe() {
		int posDip = 0;
		int posAnn = 0;
		int posUe = 0;

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

		// On affiche ou non le texte de la validation UE.
		TextView tvValid = (TextView) findViewById(R.id.txt_valid_ue);

		String res = "";

		if (etud.getListeDiplomes().get(posDip).getListeAnnees().get(posAnn).getListeUE().get(posUe).getMoyenne() == -2.0) {
			res = "Il manque une ou plusieurs notes.";
		} 
		
		if ((etud.getListeDiplomes().get(posDip).getListeAnnees().get(posAnn).getListeUE().get(posUe).getMoyenne() < 10) && (etud.getListeDiplomes().get(posDip).getListeAnnees().get(posAnn).getListeUE().get(posUe).getMoyenne() != -2.0)) {
			res += "\nL'UE n'est pas valid�e.\nMoyenne : " + etud.getListeDiplomes().get(posDip).getListeAnnees().get(posAnn).getListeUE().get(posUe).getMoyenne();
		} 

		tvValid.setText(res);

		final int posDipFin = posDip;
		final int posAnnFin = posAnn;
		final int posUeFin = posUe;

		if (etud.getListeDiplomes().get(posDipFin).getListeAnnees().get(posAnnFin).getListeUE().get(posUeFin).getListeMatieres().isEmpty()) {
			final ListView liste = (ListView) findViewById(android.R.id.list);
			final ArrayList<HashMap<String, String>> listItem = new ArrayList<HashMap<String, String>>();

			HashMap<String, String> map = new HashMap<String, String>();

			map.put("titre", "Aucune mati�re.");

			listItem.add(map);		

			final SimpleAdapter mSchedule = new SimpleAdapter (getBaseContext(), listItem, R.layout.etudiant_liste_matiere_empty_list, new String[] {"titre"}, new int[] {R.id.titre});

			liste.setAdapter(mSchedule); 
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

				// map.put("note", "" + etud.getListeDiplomes().get(posDipFin).getListeAnnees().get(posAnnFin).getListeUE().get(posUeFin).getListeMatieres().get(s).getNoteFinale());

				listItem.add(map);		
			}

			final SimpleAdapter mSchedule = new SimpleAdapter (getBaseContext(), listItem, R.layout.etudiant_liste_matiere_list, new String[] {"titre", "description", "isOption", "note"}, new int[] {R.id.titre, R.id.description, R.id.isOption, R.id.note});

			liste.setAdapter(mSchedule); 

			liste.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				public void onItemClick(AdapterView<?> arg0, View arg1, final int position, long arg3) {
					Intent act = new Intent(ListeMatieresE.this, SaisieNoteE.class);
					act.putExtra("idDiplome", idDiplome);
					act.putExtra("idAnnee", idAnnee);
					act.putExtra("idUE", idUe);
					act.putExtra("idMatiere", listItem.get(position).get("id"));
					act.putExtra("matiere", etud.getListeDiplomes().get(posDipFin).getListeAnnees().get(posAnnFin).getListeUE().get(posUeFin).getListeMatieres().get(position));

					startActivity(act);
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
		etud = EtudiantDAO.load();
		initListe();

		super.onRestart();
	} 
}