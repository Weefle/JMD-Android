package org.gl.jmd.view.etudiant.listing;

import java.util.*;

import org.gl.jmd.Constantes;
import org.gl.jmd.R;
import org.gl.jmd.dao.EtudiantDAO;
import org.gl.jmd.model.*;
import org.gl.jmd.model.enumeration.*;
import org.gl.jmd.view.etudiant.StatsAnnee;
import org.gl.jmd.view.etudiant.create.SaisieNoteE;
import org.gl.jmd.view.list.Header;
import org.gl.jmd.view.list.Item;
import org.gl.jmd.view.list.ListItemMatiere;
import org.gl.jmd.view.list.ListItemUE;
import org.gl.jmd.view.list.TwoTextArrayAdapter;

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

	private Etudiant etud = EtudiantDAO.load();

	private Annee ann;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.etudiant_liste_ue);
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

		positionDip = getIntent().getExtras().getInt("positionDip");
		positionAnn = getIntent().getExtras().getInt("positionAnn");

		ann = etud.getListeDiplomes().get(positionDip).getListeAnnees().get(positionAnn);

		initListe();
		initTitleView();
	}

	private void initTitleView() {
		TextView tvTitre = (TextView) findViewById(R.id.etudiant_liste_ue_titre);
		
		if (ann.getNom().length() > Constantes.LIMIT_TITLE) {
			tvTitre.setText(ann.getNom().substring(0, Constantes.LIMIT_TITLE) + "...");
		} else {
			tvTitre.setText(ann.getNom());
		}
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
		if (etud.getListeDiplomes().get(positionDip).getListeAnnees().get(positionAnn).getListeUE().size() == 0) {
			ListView liste = (ListView) findViewById(R.id.listUEEtu);
			ArrayList<HashMap<String, String>> listItem = new ArrayList<HashMap<String, String>>();

			HashMap<String, String> map = new HashMap<String, String>();
			map.put("titre", "Aucune UE.");

			listItem.add(map);		

			liste.setAdapter(new SimpleAdapter (getBaseContext(), listItem, R.layout.simple_list, new String[] {"titre"}, new int[] {R.id.titre}));
			liste.setOnItemClickListener(null);
		} else {
			List<Item> items = new ArrayList<Item>();

			if (ann.getDecoupage() == DecoupageType.NULL) {
				items.add(new Header("CONTRÔLE CONTINU"));

				for (int s = 0; s < etud.getListeDiplomes().get(positionDip).getListeAnnees().get(positionAnn).getListeUE().size(); s++) {
					items.add(new Header(etud.getListeDiplomes().get(positionDip).getListeAnnees().get(positionAnn).getListeUE().get(s).getNom()));	
					
					for (int b = 0; b < etud.getListeDiplomes().get(positionDip).getListeAnnees().get(positionAnn).getListeUE().get(s).getListeMatieres().size(); b++) {
						items.add(new ListItemMatiere(etud.getListeDiplomes().get(positionDip).getListeAnnees().get(positionAnn).getListeUE().get(s).getListeMatieres().get(b), s, b));	
					}
				}
			} else if (ann.getDecoupage() == DecoupageType.SEMESTRE) {
				items.add(new Header("SEMESTRE 1"));
				boolean hasSEM1 = false;

				for (int s = 0; s < etud.getListeDiplomes().get(positionDip).getListeAnnees().get(positionAnn).getListeUE().size(); s++) {
					if (etud.getListeDiplomes().get(positionDip).getListeAnnees().get(positionAnn).getListeUE().get(s).getDecoupage() == DecoupageYearType.SEM1) {
						hasSEM1 = true;
						items.add(new Header(etud.getListeDiplomes().get(positionDip).getListeAnnees().get(positionAnn).getListeUE().get(s).getNom()));	
						
						for (int b = 0; b < etud.getListeDiplomes().get(positionDip).getListeAnnees().get(positionAnn).getListeUE().get(s).getListeMatieres().size(); b++) {
							items.add(new ListItemMatiere(etud.getListeDiplomes().get(positionDip).getListeAnnees().get(positionAnn).getListeUE().get(s).getListeMatieres().get(b), s, b));	
						}
					}
				}

				if (!hasSEM1) {
					items.add(new ListItemMatiere());	
				}

				items.add(new Header("SEMESTRE 2"));
				boolean hasSEM2 = false;

				for (int s = 0; s < etud.getListeDiplomes().get(positionDip).getListeAnnees().get(positionAnn).getListeUE().size(); s++) {
					if (etud.getListeDiplomes().get(positionDip).getListeAnnees().get(positionAnn).getListeUE().get(s).getDecoupage() == DecoupageYearType.SEM2) {
						hasSEM2 = true;
						items.add(new Header(etud.getListeDiplomes().get(positionDip).getListeAnnees().get(positionAnn).getListeUE().get(s).getNom()));	
						
						for (int b = 0; b < etud.getListeDiplomes().get(positionDip).getListeAnnees().get(positionAnn).getListeUE().get(s).getListeMatieres().size(); b++) {
							items.add(new ListItemMatiere(etud.getListeDiplomes().get(positionDip).getListeAnnees().get(positionAnn).getListeUE().get(s).getListeMatieres().get(b), s, b));	
						}
					}
				}

				if (!hasSEM2) {
					items.add(new ListItemMatiere());	
				}
			} else if (ann.getDecoupage() == DecoupageType.TRIMESTRE) {
				items.add(new Header("TRIMESTRE 1"));
				boolean hasTR1 = false;

				for (int s = 0; s < etud.getListeDiplomes().get(positionDip).getListeAnnees().get(positionAnn).getListeUE().size(); s++) {
					if (etud.getListeDiplomes().get(positionDip).getListeAnnees().get(positionAnn).getListeUE().get(s).getDecoupage() == DecoupageYearType.TRI1) {
						hasTR1 = true;
						items.add(new Header(etud.getListeDiplomes().get(positionDip).getListeAnnees().get(positionAnn).getListeUE().get(s).getNom()));	
						
						for (int b = 0; b < etud.getListeDiplomes().get(positionDip).getListeAnnees().get(positionAnn).getListeUE().get(s).getListeMatieres().size(); b++) {
							items.add(new ListItemMatiere(etud.getListeDiplomes().get(positionDip).getListeAnnees().get(positionAnn).getListeUE().get(s).getListeMatieres().get(b), s, b));	
						}
					}
				}

				if (!hasTR1) {
					items.add(new ListItemMatiere());	
				}

				items.add(new Header("TRIMESTRE 2"));
				boolean hasTR2 = false;

				for (int s = 0; s < etud.getListeDiplomes().get(positionDip).getListeAnnees().get(positionAnn).getListeUE().size(); s++) {
					if (etud.getListeDiplomes().get(positionDip).getListeAnnees().get(positionAnn).getListeUE().get(s).getDecoupage() == DecoupageYearType.TRI2) {
						hasTR2 = true;
						items.add(new Header(etud.getListeDiplomes().get(positionDip).getListeAnnees().get(positionAnn).getListeUE().get(s).getNom()));	
						
						for (int b = 0; b < etud.getListeDiplomes().get(positionDip).getListeAnnees().get(positionAnn).getListeUE().get(s).getListeMatieres().size(); b++) {
							items.add(new ListItemMatiere(etud.getListeDiplomes().get(positionDip).getListeAnnees().get(positionAnn).getListeUE().get(s).getListeMatieres().get(b), s, b));	
						}
					}
				}

				if (!hasTR2) {
					items.add(new ListItemMatiere());	
				}

				items.add(new Header("TRIMESTRE 3"));
				boolean hasTR3 = false;

				for (int s = 0; s < etud.getListeDiplomes().get(positionDip).getListeAnnees().get(positionAnn).getListeUE().size(); s++) {
					if (etud.getListeDiplomes().get(positionDip).getListeAnnees().get(positionAnn).getListeUE().get(s).getDecoupage() == DecoupageYearType.TRI3) {
						hasTR3 = true;
						items.add(new Header(etud.getListeDiplomes().get(positionDip).getListeAnnees().get(positionAnn).getListeUE().get(s).getNom()));	
						
						for (int b = 0; b < etud.getListeDiplomes().get(positionDip).getListeAnnees().get(positionAnn).getListeUE().get(s).getListeMatieres().size(); b++) {
							items.add(new ListItemMatiere(etud.getListeDiplomes().get(positionDip).getListeAnnees().get(positionAnn).getListeUE().get(s).getListeMatieres().get(b), s, b));	
						}
					}
				}

				if (!hasTR3) {
					items.add(new ListItemMatiere());	
				}
			}

			final TwoTextArrayAdapter adapter = new TwoTextArrayAdapter(this, items);

			final ListView liste = (ListView) findViewById(R.id.listUEEtu);

			liste.setAdapter(adapter);

			liste.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				public void onItemClick(AdapterView<?> arg0, View arg1, final int position, long arg3) {
					try {
						Header h = ((Header) adapter.getItem(position));

						if (h != null) {
							return;
						}
					} catch(Exception e) {
						// Do nothing.
					}

					if (((ListItemMatiere) adapter.getItem(position)).getMatiere() == null) {
						// Do nothing.
					} else {
						Intent newIntent = new Intent(ListeUEE.this, SaisieNoteE.class);
						newIntent.putExtra("matiere", ((ListItemMatiere) adapter.getItem(position)).getMatiere());
						
						startActivity(newIntent);
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
	 * Méthode exécutée lorsque l'activité est relancée.
	 */
	@Override
	public void onRestart() {
		etud = EtudiantDAO.load();
		initListe();

		super.onRestart();
	} 

	/**
	 * Méthode exécutée lorsque l'activité est relancée.
	 */
	@Override
	public void onResume() {
		etud = EtudiantDAO.load();
		ann = etud.getListeDiplomes().get(positionDip).getListeAnnees().get(positionAnn);

		initListe();

		super.onResume();
	} 
}