package org.gl.jmd.view.etudiant;

import java.util.*;

import org.gl.jmd.R;
import org.gl.jmd.dao.EtudiantDAO;
import org.gl.jmd.model.Diplome;
import org.gl.jmd.model.user.Etudiant;
import org.gl.jmd.view.*;
import org.gl.jmd.view.etudiant.create.AjouterDiplomeE;
import org.gl.jmd.view.etudiant.listing.ListeAnneesE;

import android.os.Bundle;
import android.view.*;
import android.widget.*;
import android.app.*;
import android.content.*;
import android.content.res.Configuration;

/**
 * Activit� correspondant � la vue d'acceuil de l'�tudiant.
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
			ListView liste = (ListView) findViewById(android.R.id.list);
			ArrayList<HashMap<String, String>> listItem = new ArrayList<HashMap<String, String>>();
			
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("titre", "Aucun dipl�me.");
			
			listItem.add(map);		

			liste.setAdapter(new SimpleAdapter (getBaseContext(), listItem, R.layout.etudiant_simple_list, new String[] {"titre"}, new int[] {R.id.titre}));
			liste.setOnItemClickListener(null);
		} else {
			final ListView liste = (ListView) findViewById(android.R.id.list);
			final ArrayList<HashMap<String, String>> listItem = new ArrayList<HashMap<String, String>>();
			
			HashMap<String, String> map;	

			for(int s = 0; s < etud.getListeDiplomes().size(); s++) {
				map = new HashMap<String, String>();
				map.put("titre", etud.getListeDiplomes().get(s).getNom());
				
				listItem.add(map);		
			}

			liste.setAdapter(new SimpleAdapter (getBaseContext(), listItem, R.layout.etudiant_simple_list, new String[] {"titre"}, new int[] {R.id.titre})); 

			liste.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				public void onItemClick(AdapterView<?> arg0, View arg1, final int position, long arg3) {
					Intent act = new Intent(AccueilE.this, ListeAnneesE.class);
					act.putExtra("idDiplome", etud.getListeDiplomes().get(position).getId());
					act.putExtra("nomDiplome", etud.getListeDiplomes().get(position).getNom());
					
					startActivity(act);
				}
			});

			liste.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
				public boolean onItemLongClick(AdapterView<?> arg0, View arg1, final int arg2, final long arg3) {
					if (listItem.get(arg2).get("img") == null) {
						AlertDialog.Builder confirmQuitter = new AlertDialog.Builder(AccueilE.this);
						confirmQuitter.setTitle("Suppression");
						confirmQuitter.setMessage("Voulez-vous vraiment supprimer ce dipl�me ?");
						confirmQuitter.setCancelable(false);
						confirmQuitter.setPositiveButton("Oui", new AlertDialog.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {								
								ArrayList<Diplome> listeDiplomes = etud.getListeDiplomes();
								listeDiplomes.remove(arg2);
								
								etud.setListeDiplomes(listeDiplomes);
								EtudiantDAO.save(etud);
								
								toast.setText("Le dipl�me a bien �t� supprim�.");
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
	 * M�thode d�clench�e lors d'un click sur le bouton de modification.
	 * 
	 * @param view La vue lors du click sur le bouton de modification.
	 */
	public void modifierListe(View view) {
		startActivity(new Intent(AccueilE.this, AjouterDiplomeE.class));	
	}

	/**
	 * M�thode permettant de faire apparaitre le menu lors du click sur le bouton de menu.
	 * 
	 * @param view La vue lors du click sur le bouton de menu.
	 */
	public void openMenu(View view) {
		openOptionsMenu();
	}

	/* M�thodes h�rit�es de la classe Activity. */

	/**
	 * M�thode d�clench�e lors d'un click sur le bouton virtuel Android de retour.
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
	 * M�thode permettant d'afficher le menu de la vue.
	 * 
	 * @param menu Le menu � afficher.
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_etudiant_view, menu);

		return true;
	}

	/**
	 * M�thode d�clench�e lors du click sur un �l�ment du menu de l'application.
	 * 
	 * @item L'�l�ment du menu s�lectionn�.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		
		case R.id.menu_accueil:
			finish();
			startActivity(new Intent(AccueilE.this, InitApp.class));		

			return true;
		
		case R.id.menu_profil:
			startActivity(new Intent(AccueilE.this, ProfilView.class));				

			return true;
		}

		return false;
	}
	
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
	 * M�thode ex�cut�e lorsque l'activit� est relanc�e.
	 */
	@Override
	public void onRestart() {
		etud = EtudiantDAO.load();
		
		initListe();
		
		super.onRestart();
	} 
}