package org.gl.jmd.view.etudiant.listing;

import java.util.*;

import org.gl.jmd.R;
import org.gl.jmd.dao.EtudiantDAO;
import org.gl.jmd.model.Diplome;
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

	private Diplome d = null;

	private int positionDip = 0;
	
	private Etudiant etud = EtudiantDAO.load();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.etudiant_liste_annees);
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
		
		positionDip = Integer.parseInt(getIntent().getExtras().getString("positionDip"));
		d = etud.getListeDiplomes().get(positionDip);
		
		initListe();
		initTxtTitre();
	}

	private void initListe() {
		if (etud.getListeDiplomes().get(positionDip).getListeAnnees().isEmpty()) {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("titre", "Aucune année.");
			
			ArrayList<HashMap<String, String>> listItem = new ArrayList<HashMap<String, String>>();
			listItem.add(map);
			
			ListView liste = (ListView) findViewById(android.R.id.list);
			liste.setAdapter(new SimpleAdapter (getBaseContext(), listItem, R.layout.etudiant_simple_list, new String[] {"titre"}, new int[] {R.id.titre})); 
		} else {
			final ListView liste = (ListView) findViewById(android.R.id.list);

			final ArrayList<HashMap<String, String>> listItem = new ArrayList<HashMap<String, String>>();
			HashMap<String, String> map;	

			for(int s = 0; s < etud.getListeDiplomes().get(positionDip).getListeAnnees().size(); s++) {
				map = new HashMap<String, String>();

				map.put("titre", etud.getListeDiplomes().get(positionDip).getListeAnnees().get(s).getNom());
				map.put("description", etud.getListeDiplomes().get(positionDip).getListeAnnees().get(s).getEtablissement().getNom());
				map.put("decoupage", etud.getListeDiplomes().get(positionDip).getListeAnnees().get(s).getDecoupage().name());
				map.put("note", "" + etud.getListeDiplomes().get(positionDip).getListeAnnees().get(s).getMoyenne());
				map.put("positionAnn", "" + s);
				
				listItem.add(map);		
			}

			liste.setAdapter(new SimpleAdapter (getBaseContext(), listItem, R.layout.etudiant_liste_annees_list, new String[] {"titre", "description", "note"}, new int[] {R.id.titre, R.id.description, R.id.note})); 

			liste.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				public void onItemClick(AdapterView<?> arg0, View arg1, final int position, long arg3) {
					Class<?> c = null;
					
					if (listItem.get(position).get("decoupage").equals(DecoupageType.NULL.name())) {
						c = ListeUEE.class;
					} else if (listItem.get(position).get("decoupage").equals(DecoupageType.SEMESTRE.name())) {
						c = ListeSemestreE.class;
					} else if (listItem.get(position).get("decoupage").equals(DecoupageType.TRIMESTRE.name())) {
						c = ListeTrimestreE.class;
					} 

					Intent newIntent = new Intent(ListeAnneesE.this, c);
					newIntent.putExtra("positionDip", positionDip);
					newIntent.putExtra("positionAnn", Integer.parseInt(listItem.get(position).get("positionAnn")));
					newIntent.putExtra("decoupage", "NULL");
					
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
						
						if (badgeNote.getText().toString().equals("-1.0")) {							
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
		tvTitre.setText(d.getNom());
	}

	/**
	 * Méthode déclenchée lors d'un click sur le bouton "plus".
	 * 
	 * @param view La vue lors du click sur le bouton "plus".
	 */
	public void modifierListe(View view) {
		Intent act = new Intent(ListeAnneesE.this, AjouterAnneeE.class);
		act.putExtra("idDiplome", d.getId());

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