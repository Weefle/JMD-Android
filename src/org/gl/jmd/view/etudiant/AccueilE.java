package org.gl.jmd.view.etudiant;

import java.util.*;

import org.gl.jmd.EtudiantDAO;
import org.gl.jmd.R;
import org.gl.jmd.model.*;
import org.gl.jmd.model.enumeration.DecoupageType;
import org.gl.jmd.view.*;
import org.gl.jmd.view.etudiant.listing.*;
import org.gl.jmd.view.list.*;
import org.gl.jmd.view.list.item.Item;
import org.gl.jmd.view.list.item.ListItem;

import android.os.Bundle;
import android.view.*;
import android.widget.*;
import android.app.*;
import android.content.*;
import android.content.res.Configuration;

/**
 * Activité correspondant à la vue d'acceuil de l'étudiant.
 * 
 * @author Jordi CHARPENTIER & Yoann VANHOESERLANDE
 */
public class AccueilE extends Activity {

	private Activity activity;
	
	private Toast toast;
	
	private Etudiant etud = EtudiantDAO.load();
	
	private long back_pressed;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.etudiant_accueil);
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
		
		activity = this;
		toast = Toast.makeText(activity, "", Toast.LENGTH_SHORT);
		
		initListe();
	}

	private void initListe() {			
		if (etud.getListeDiplomes().isEmpty()) {
			ListView liste = (ListView) findViewById(R.id.listAccueilEtud);
			ArrayList<HashMap<String, String>> listItem = new ArrayList<HashMap<String, String>>();
			
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("titre", "Aucun diplôme.");
			
			listItem.add(map);		

			liste.setAdapter(new SimpleAdapter (getBaseContext(), listItem, R.layout.simple_list, new String[] {"titre"}, new int[] {R.id.titre}));
			liste.setOnItemClickListener(null);
		} else {
			List<Item> items = new ArrayList<Item>();
			
			for(int s = 0; s < etud.getListeDiplomes().size(); s++) {
				if (etud.getListeDiplomes().get(s).getListeAnnees().size() > 0) {
					items.add(new Header(etud.getListeDiplomes().get(s).getNom()));
					
					for(int p = 0; p < etud.getListeDiplomes().get(s).getListeAnnees().size(); p++) {
						items.add(new ListItem(etud.getListeDiplomes().get(s).getListeAnnees().get(p), s, p));	
					}
				}
			}

	        final TwoTextArrayAdapter adapter = new TwoTextArrayAdapter(this, items);

	        final ListView liste = (ListView) findViewById(R.id.listAccueilEtud);
	        
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
					
					if (((ListItem) adapter.getItem(position)).getAnnee() == null) {
						// Do nothing.
					} else {	
						Class c = null;
						
						if (((ListItem) adapter.getItem(position)).getAnnee().getDecoupage() == DecoupageType.NULL) {
							c = ListeUEE.class;
						} else if (((ListItem) adapter.getItem(position)).getAnnee().getDecoupage() == DecoupageType.SEMESTRE) {
							c = ListeUEETabs.class;
						} else if (((ListItem) adapter.getItem(position)).getAnnee().getDecoupage() == DecoupageType.TRIMESTRE) {
							c = ListeUEETabs.class;
						}
						
						Intent act = new Intent(AccueilE.this, c);
						act.putExtra("positionDip", ((ListItem) adapter.getItem(position)).getPosDip());
						act.putExtra("positionAnn", ((ListItem) adapter.getItem(position)).getPosAnnee());
						
						startActivity(act);
					}
				}
			});

			liste.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
				public boolean onItemLongClick(AdapterView<?> arg0, View arg1, final int arg2, final long arg3) {
					try {
						Header h = ((Header) adapter.getItem(arg2));
						
						if (h != null) {
							return false;
						}
					} catch(Exception e) {
						// Do nothing.
					}
					
					if (((ListItem) adapter.getItem(arg2)).getAnnee() == null) {
						// Do nothing.
					} else {
						AlertDialog.Builder confirmQuitter = new AlertDialog.Builder(AccueilE.this);
						confirmQuitter.setTitle("Suppression");
						confirmQuitter.setMessage("Voulez-vous vraiment supprimer cette année ?");
						confirmQuitter.setCancelable(false);
						confirmQuitter.setPositiveButton("Oui", new AlertDialog.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {								
								ArrayList<Diplome> listeDiplomes = etud.getListeDiplomes();
								Diplome d = listeDiplomes.get(((ListItem) adapter.getItem(arg2)).getPosDip());
								
								ArrayList<Annee> listeAnnees = d.getListeAnnees();
								listeAnnees.remove(((ListItem) adapter.getItem(arg2)).getPosAnnee());
								
								d.setListeAnnees(listeAnnees);
								
								listeDiplomes.remove(((ListItem) adapter.getItem(arg2)).getPosDip());
								
								if (d.getListeAnnees().size() > 0) {
									listeDiplomes.add(d);
								}
								
								etud.setListeDiplomes(listeDiplomes);
	
								EtudiantDAO.save(etud);
	
								toast.setText("L'année a bien été supprimée.");
								toast.show();
	
								initListe(); 
							}
						});
	
						confirmQuitter.setNegativeButton("Non", null);
						confirmQuitter.show();
					}

					return true;
				}
			}); 
		}
	}

	/**
	 * Méthode déclenchée lors d'un click sur le bouton de modification.
	 * 
	 * @param view La vue lors du click sur le bouton de modification.
	 */
	public void modifierListe(View view) {
		startActivity(new Intent(AccueilE.this, AjouterAnneeE.class));	
	}
	
	public void navigateToAccueil(View view) {
		finish();
		startActivity(new Intent(AccueilE.this, InitApp.class));		
	}

	/* Méthodes héritées de la classe Activity. */

	/**
	 * Méthode déclenchée lors d'un click sur le bouton virtuel Android de retour.
	 */
	@Override
	public void onBackPressed() {
		if (back_pressed + 2000 > System.currentTimeMillis()) {
			android.os.Process.killProcess(android.os.Process.myPid());
		} else {
			Toast.makeText(getBaseContext(), "Appuyez encore une fois sur \"Retour\" pour quitter l'application.", Toast.LENGTH_SHORT).show();
		}
		
        back_pressed = System.currentTimeMillis();
	}
	
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