package org.gl.jmd.view.etudiant.listing;

import java.util.*;

import org.gl.jmd.R;
import org.gl.jmd.dao.EtudiantDAO;
import org.gl.jmd.model.Annee;
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

	private int idDiplome = 0;

	private int idAnnee = 0;

	private String titre = "";
	
	private String decoupage = "";

	private Etudiant etud = EtudiantDAO.load();

	private Intent lastIntent;

	private Activity activity;

	private Toast toast;

	private Annee ann;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.etudiant_liste_ue);
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

		activity = this;
		toast = Toast.makeText(activity, "", Toast.LENGTH_SHORT);

		lastIntent = getIntent();

		idDiplome = lastIntent.getExtras().getInt("idDiplome");
		idAnnee = lastIntent.getExtras().getInt("idAnnee");

		titre = lastIntent.getExtras().getString("titre");
		decoupage = lastIntent.getExtras().getString("decoupage");
		
		initListe();
		initMoyenne();

		// On donne comme titre à la vue le nom de l'année choisie.
		TextView tvTitre = (TextView) findViewById(R.id.etudiant_liste_ue_titre);
		tvTitre.setText(titre.substring(0, 20) + "...");
	}

	public void initMoyenne() {
		// Récupération de la moyenne de l'année.
		ann = null;

		int pos = 0;

		for (int i = 0; i < etud.getListeDiplomes().size(); i++) {
			if (etud.getListeDiplomes().get(i).getId() == idDiplome) {
				pos = i;
			}
		}

		for (int j = 0; j < etud.getListeDiplomes().get(pos).getListeAnnees().size(); j++) {
			if (etud.getListeDiplomes().get(pos).getListeAnnees().get(j).getId() == idAnnee) {
				ann = etud.getListeDiplomes().get(pos).getListeAnnees().get(j);
			}
		}
	}

	public void export(View view) {
		if (etud.getMail() != null && etud.getMail().length() > 0) {
			Intent email = new Intent(Intent.ACTION_SEND);
			email.putExtra(Intent.EXTRA_EMAIL, new String[] { etud.getMail() });
			email.putExtra(Intent.EXTRA_SUBJECT, "JMD - Moyenne de l'année");
			
			String msg = "Moyenne de l'année (id : " + idAnnee + ", nom : " + titre + ") : " + ann.getMoyenne();
			
			if (ann.getMoyenne() > 10) {
				msg += "\n\nL'année est validée.";
			} else {
				msg += "\n\nL'année n'est pas validée.";
			}
			
			email.putExtra(Intent.EXTRA_TEXT, msg);

			email.setType("message/rfc822");

			startActivity(Intent.createChooser(email, "Choisir un client mail"));
		} else {
			toast.setText("Il faut définir un mail afin de pouvoir utiliser cette fonctionnalité.");
			toast.show();
		}
	}

	/**
	 * Méthode déclenchée lors d'un click sur le bouton de simulation.
	 * 
	 * @param view La vue lors du click sur le bouton de simulation.
	 */
	public void simulerObtentionAnnee(View view) {
		// Affichage de la moyenne.
		Intent i = new Intent(ListeUEE.this, StatsAnnee.class);
		i.putExtra("moyenne", ann.getMoyenne());
		i.putExtra("avancement", ann.getAvancement());
		i.putExtra("mention", ann.getMention());

		startActivity(i);
	}

	public void initListe() {
		int posDip = 0;
		int posAnn = 0;

		for (int i = 0; i < this.etud.getListeDiplomes().size(); i++) {
			if (this.etud.getListeDiplomes().get(i).getId() == idDiplome) {
				posDip = i;
			}
		}

		for (int i = 0; i < this.etud.getListeDiplomes().size(); i++) {
			for (int j = 0; j < this.etud.getListeDiplomes().get(i).getListeAnnees().size(); j++) {				
				if (this.etud.getListeDiplomes().get(i).getListeAnnees().get(j).getId() == idAnnee) {
					posAnn = j;
				}
			}
		}

		final int posDipFin = posDip;
		final int posAnnFin = posAnn;

		if (etud.getListeDiplomes().get(posDip).getListeAnnees().get(posAnn).getListeUE().isEmpty()) {
			final ListView liste = (ListView) findViewById(android.R.id.list);
			final ArrayList<HashMap<String, String>> listItem = new ArrayList<HashMap<String, String>>();

			HashMap<String, String> map = new HashMap<String, String>();

			map.put("titre", "Aucune UE.");

			listItem.add(map);		

			final SimpleAdapter mSchedule = new SimpleAdapter (getBaseContext(), listItem, R.layout.etudiant_liste_ue_list, new String[] {"titre"}, new int[] {R.id.titre});

			liste.setAdapter(mSchedule); 
		} else {
			final ListView liste = (ListView) findViewById(android.R.id.list);

			final ArrayList<HashMap<String, String>> listItem = new ArrayList<HashMap<String, String>>();
			HashMap<String, String> map;	

			for(int s = 0; s < etud.getListeDiplomes().get(posDipFin).getListeAnnees().get(posAnnFin).getListeUE().size(); s++) {
				if (etud.getListeDiplomes().get(posDipFin).getListeAnnees().get(posAnnFin).getListeUE().get(s).getDecoupage().equals(DecoupageYearType.valueOf(decoupage))) {
					map = new HashMap<String, String>();
	
					map.put("id", "" + etud.getListeDiplomes().get(posDipFin).getListeAnnees().get(posAnnFin).getListeUE().get(s).getId());
					map.put("titre", etud.getListeDiplomes().get(posDipFin).getListeAnnees().get(posAnnFin).getListeUE().get(s).getNom());
	
					listItem.add(map);		
				}
			}

			final SimpleAdapter mSchedule = new SimpleAdapter (getBaseContext(), listItem, R.layout.etudiant_liste_ue_list, new String[] {"titre", "description"}, new int[] {R.id.titre, R.id.description});

			liste.setAdapter(mSchedule); 

			liste.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				public void onItemClick(AdapterView<?> arg0, View arg1, final int position, long arg3) {
					Intent act = new Intent(ListeUEE.this, ListeMatieresE.class);
					act.putExtra("idDiplome", idDiplome);
					act.putExtra("idAnnee", idAnnee);
					act.putExtra("idUE", Integer.parseInt(listItem.get(position).get("id")));
					act.putExtra("nomUE", etud.getListeDiplomes().get(posDipFin).getListeAnnees().get(posAnnFin).getListeUE().get(position).getNom());

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