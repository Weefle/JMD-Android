package org.gl.jmd.view.etudiant.listing;

import java.util.*;

import org.gl.jmd.*;
import org.gl.jmd.model.*;
import org.gl.jmd.model.enumeration.*;
import org.gl.jmd.view.etudiant.*;
import org.gl.jmd.view.list.*;
import org.gl.jmd.view.list.item.*;

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

	private DecoupageYearType d;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.etudiant_liste_ue);
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

		positionDip = getIntent().getExtras().getInt("positionDip");
		positionAnn = getIntent().getExtras().getInt("positionAnn");

		d = (DecoupageYearType) getIntent().getExtras().getSerializable("type");

		if (d != null) {
			RelativeLayout r = (RelativeLayout) findViewById(R.id.etudiant_liste_ue_bandeau);
			r.setVisibility(View.GONE);

			initListe(d);
		} else {
			initListe();
			initTitleView();
		}
	}
	
	/**
	 * Méthode déclenchée lors d'un click sur le bouton de retour.
	 * 
	 * @param view La vue lors du click sur le bouton de retour.
	 */
	public void back(View view) {
		finish();
	}

	private void initTitleView() {
		TextView tvTitre = (TextView) findViewById(R.id.etudiant_liste_ue_titre);

		if (etud.getListeDiplomes().get(positionDip).getListeAnnees().get(positionAnn).getNom().length() > Constantes.LIMIT_TEXT) {
			tvTitre.setText(etud.getListeDiplomes().get(positionDip).getListeAnnees().get(positionAnn).getNom().substring(0, Constantes.LIMIT_TEXT) + "...");
		} else {
			tvTitre.setText(etud.getListeDiplomes().get(positionDip).getListeAnnees().get(positionAnn).getNom());
		}
	}

	/**
	 * Méthode déclenchée lors d'un click sur le bouton de simulation.
	 * 
	 * @param view La vue lors du click sur le bouton de simulation.
	 */
	public void simulerObtentionAnnee(View view) {
		Intent i = new Intent(ListeUEE.this, StatsAnnee.class);
		i.putExtra("annee", etud.getListeDiplomes().get(positionDip).getListeAnnees().get(positionAnn));

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

			if (etud.getListeDiplomes().get(positionDip).getListeAnnees().get(positionAnn).getDecoupage() == DecoupageType.NULL) {
				for (int s = 0; s < etud.getListeDiplomes().get(positionDip).getListeAnnees().get(positionAnn).getListeUE().size(); s++) {
					items.add(new Header(etud.getListeDiplomes().get(positionDip).getListeAnnees().get(positionAnn).getListeUE().get(s).getNom()));	

					for (int b = 0; b < etud.getListeDiplomes().get(positionDip).getListeAnnees().get(positionAnn).getListeUE().get(s).getListeMatieres().size(); b++) {
						items.add(new ListItemMatiere(etud.getListeDiplomes().get(positionDip).getListeAnnees().get(positionAnn).getListeUE().get(s).getListeMatieres().get(b), s, b));	
					}

					if (etud.getListeDiplomes().get(positionDip).getListeAnnees().get(positionAnn).getListeUE().get(s).getMoyenne(etud.getListeDiplomes().get(positionDip).getListeAnnees().get(positionAnn).getListeRegles()) != -1.0) {
						if (etud.getListeDiplomes().get(positionDip).getListeAnnees().get(positionAnn).getListeUE().get(s).estAjourne(etud.getListeDiplomes().get(positionDip).getListeAnnees().get(positionAnn).getListeRegles())) {
							items.add(new Footer(true, 
												 etud.getListeDiplomes().get(positionDip).getListeAnnees().get(positionAnn).getListeUE().get(s).getMoyenne(etud.getListeDiplomes().get(positionDip).getListeAnnees().get(positionAnn).getListeRegles())));
						} else if (etud.getListeDiplomes().get(positionDip).getListeAnnees().get(positionAnn).getListeUE().get(s).getMoyenne(etud.getListeDiplomes().get(positionDip).getListeAnnees().get(positionAnn).getListeRegles()) < 10) {
							items.add(new Footer(false, 
									 			 etud.getListeDiplomes().get(positionDip).getListeAnnees().get(positionAnn).getListeUE().get(s).getMoyenne(etud.getListeDiplomes().get(positionDip).getListeAnnees().get(positionAnn).getListeRegles())));
						} else {
							items.add(new Footer(false,
									 			 etud.getListeDiplomes().get(positionDip).getListeAnnees().get(positionAnn).getListeUE().get(s).getMoyenne(etud.getListeDiplomes().get(positionDip).getListeAnnees().get(positionAnn).getListeRegles())));
						}
					}
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

					try {
						Footer f = ((Footer) adapter.getItem(position));

						if (f != null) {
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

	private void initListe(DecoupageYearType d) {
		if (etud.getListeDiplomes().get(positionDip).getListeAnnees().get(positionAnn).getListeUE(d).size() == 0) {
			ListView liste = (ListView) findViewById(R.id.listUEEtu);
			ArrayList<HashMap<String, String>> listItem = new ArrayList<HashMap<String, String>>();

			HashMap<String, String> map = new HashMap<String, String>();
			map.put("titre", "Aucune UE.");

			listItem.add(map);		

			liste.setAdapter(new SimpleAdapter (getBaseContext(), listItem, R.layout.simple_list, new String[] {"titre"}, new int[] {R.id.titre}));
			liste.setOnItemClickListener(null);
		} else {
			List<Item> items = new ArrayList<Item>();

			for (int s = 0; s < etud.getListeDiplomes().get(positionDip).getListeAnnees().get(positionAnn).getListeUE(d).size(); s++) {
				items.add(new Header(etud.getListeDiplomes().get(positionDip).getListeAnnees().get(positionAnn).getListeUE(d).get(s).getNom()));	

				if (!etud.getListeDiplomes().get(positionDip).getListeAnnees().get(positionAnn).getListeUE(d).get(s).getListeMatieres().isEmpty()) {
					for (int b = 0; b < etud.getListeDiplomes().get(positionDip).getListeAnnees().get(positionAnn).getListeUE(d).get(s).getListeMatieres().size(); b++) {
						items.add(new ListItemMatiere(etud.getListeDiplomes().get(positionDip).getListeAnnees().get(positionAnn).getListeUE(d).get(s).getListeMatieres().get(b), s, b));	
					}
				} else {
					items.add(new ListItemMatiere());	
				}

				if (etud.getListeDiplomes().get(positionDip).getListeAnnees().get(positionAnn).getListeUE().get(s).getMoyenne(etud.getListeDiplomes().get(positionDip).getListeAnnees().get(positionAnn).getListeRegles()) != -1.0) {
					if (etud.getListeDiplomes().get(positionDip).getListeAnnees().get(positionAnn).getListeUE().get(s).estAjourne(etud.getListeDiplomes().get(positionDip).getListeAnnees().get(positionAnn).getListeRegles())) {
						items.add(new Footer(true, etud.getListeDiplomes().get(positionDip).getListeAnnees().get(positionAnn).getListeUE().get(s).getMoyenne(etud.getListeDiplomes().get(positionDip).getListeAnnees().get(positionAnn).getListeRegles())));
					} else if (etud.getListeDiplomes().get(positionDip).getListeAnnees().get(positionAnn).getListeUE().get(s).getMoyenne(etud.getListeDiplomes().get(positionDip).getListeAnnees().get(positionAnn).getListeRegles()) < 10) {
						items.add(new Footer(false, etud.getListeDiplomes().get(positionDip).getListeAnnees().get(positionAnn).getListeUE().get(s).getMoyenne(etud.getListeDiplomes().get(positionDip).getListeAnnees().get(positionAnn).getListeRegles())));
					} else {
						items.add(new Footer(false, etud.getListeDiplomes().get(positionDip).getListeAnnees().get(positionAnn).getListeUE().get(s).getMoyenne(etud.getListeDiplomes().get(positionDip).getListeAnnees().get(positionAnn).getListeRegles())));
					}
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

					try {
						Footer f = ((Footer) adapter.getItem(position));

						if (f != null) {
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

		if (d != null) {
			RelativeLayout r = (RelativeLayout) findViewById(R.id.etudiant_liste_ue_bandeau);
			r.setVisibility(View.GONE);

			initListe(d);
		} else {
			initListe();
		}

		super.onRestart();
	} 

	/**
	 * Méthode exécutée lorsque l'activité est relancée.
	 */
	@Override
	public void onResume() {
		etud = EtudiantDAO.load();

		if (d != null) {
			RelativeLayout r = (RelativeLayout) findViewById(R.id.etudiant_liste_ue_bandeau);
			r.setVisibility(View.GONE);

			initListe(d);
		} else {
			initListe();
		}

		super.onResume();
	} 
}