package org.gl.jmd.view.etudiant.listing;

import java.util.*;

import org.gl.jmd.R;
import org.gl.jmd.dao.EtudiantDAO;
import org.gl.jmd.model.enumeration.DecoupageType;
import org.gl.jmd.model.user.Etudiant;
import org.gl.jmd.view.etudiant.create.AjouterAnneeE;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import android.app.*;
import android.content.*;
import android.content.res.Configuration;

/**
 * Activité correspondant à la vue de listing des années d'un diplôme (en local).
 * 
 * @author Jordi CHARPENTIER & Yoann VANHOESERLANDE
 */
public class ListeAnneesE extends Activity {

	private int idDiplome = 0;

	private String nomDiplome = "";

	private Etudiant etud = EtudiantDAO.load();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.etudiant_liste_annees);
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

		idDiplome = getIntent().getExtras().getInt("idDiplome");
		nomDiplome = getIntent().getExtras().getString("nomDiplome");

		initListe();
		initTxtTitre();
	}

	private void initListe() {
		int pos = 0;

		for (int i = 0; i < this.etud.getListeDiplomes().size(); i++) {
			if (this.etud.getListeDiplomes().get(i).getId() == this.idDiplome) {
				pos = i;
			}
		}

		final int posFin = pos;

		if (etud.getListeDiplomes().get(pos).getListeAnnees().isEmpty()) {
			ListView liste = (ListView) findViewById(android.R.id.list);
			ArrayList<HashMap<String, String>> listItem = new ArrayList<HashMap<String, String>>();

			HashMap<String, String> map = new HashMap<String, String>();
			map.put("titre", "Aucune année.");

			listItem.add(map);		
			liste.setAdapter(new SimpleAdapter (getBaseContext(), listItem, R.layout.etudiant_liste_annee_empty_list, new String[] {"titre"}, new int[] {R.id.titre})); 
		} else {
			final ListView liste = (ListView) findViewById(android.R.id.list);

			final ArrayList<HashMap<String, String>> listItem = new ArrayList<HashMap<String, String>>();
			HashMap<String, String> map;	

			for(int s = 0; s < etud.getListeDiplomes().get(pos).getListeAnnees().size(); s++) {
				map = new HashMap<String, String>();

				map.put("titre", etud.getListeDiplomes().get(pos).getListeAnnees().get(s).getNom());
				map.put("description", etud.getListeDiplomes().get(pos).getListeAnnees().get(s).getEtablissement().getNom());
				map.put("decoupage", etud.getListeDiplomes().get(pos).getListeAnnees().get(s).getDecoupage().name());

				if (etud.getListeDiplomes().get(pos).getListeAnnees().get(s).getMoyenne() != -1) {
					map.put("note", "" + etud.getListeDiplomes().get(pos).getListeAnnees().get(s).getMoyenne());
				} else {
					map.put("note", "-1");
				}

				listItem.add(map);		
			}

			final SimpleAdapter mSchedule = new SimpleAdapter (getBaseContext(), listItem, R.layout.etudiant_liste_annees_list, new String[] {"titre", "description", "note"}, new int[] {R.id.titre, R.id.description, R.id.note});

			liste.setAdapter(mSchedule); 

			liste.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				public void onItemClick(AdapterView<?> arg0, View arg1, final int position, long arg3) {
					Intent newIntent = null;

					if (listItem.get(position).get("decoupage").equals(DecoupageType.NULL.name())) {
						newIntent = new Intent(ListeAnneesE.this, ListeUEE.class);
						newIntent.putExtra("decoupage", "NULL");
						newIntent.putExtra("titre", listItem.get(position).get("titre"));
					} else if (listItem.get(position).get("decoupage").equals(DecoupageType.SEMESTRE.name())) {
						newIntent = new Intent(ListeAnneesE.this, ListeSemestreE.class);
					} else if (listItem.get(position).get("decoupage").equals(DecoupageType.TRIMESTRE.name())) {
						newIntent = new Intent(ListeAnneesE.this, ListeTrimestreE.class);
					} 

					newIntent.putExtra("idDiplome", idDiplome);
					newIntent.putExtra("idAnnee", etud.getListeDiplomes().get(posFin).getListeAnnees().get(position).getId());

					startActivity(newIntent);
				}
			});
			
			// Cache le badge si aucune note n'a été entrée.
			liste.post(new Runnable() {
			    @Override
			    public void run() {
					for (int i = 0; i < liste.getCount(); i++) {
						View tempView = (View) liste.getChildAt(i);
						TextView badgeNote = (TextView) tempView.findViewById(R.id.note);
						
						if (badgeNote.getText().toString().equals("-1")) {							
							badgeNote.setVisibility(View.GONE);
						}
					}
			    }
			});
		}
	}
	
	private void initTxtTitre() {
		// On donne comme titre à la vue le nom du diplôme choisi.
		TextView tvTitre = (TextView) findViewById(R.id.etudiant_liste_annees_titre);
		tvTitre.setText(nomDiplome);
	}

	/**
	 * Méthode déclenchée lors d'un click sur le bouton "plus".
	 * 
	 * @param view La vue lors du click sur le bouton "plus".
	 */
	public void modifierListe(View view) {
		Intent act = new Intent(ListeAnneesE.this, AjouterAnneeE.class);
		act.putExtra("idDiplome", idDiplome);

		startActivity(act);
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

	@Override
	public void onResume() {
		etud = EtudiantDAO.load();

		initListe();
		initTxtTitre();

		super.onRestart();
	} 

	/**
	 * Méthode exécutée lorsque l'activité est relancée.
	 */
	@Override
	public void onRestart() {
		etud = EtudiantDAO.load();

		initListe();
		initTxtTitre();

		super.onRestart();
	} 
}