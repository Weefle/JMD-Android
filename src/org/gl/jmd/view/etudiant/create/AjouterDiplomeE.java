package org.gl.jmd.view.etudiant.create;

import java.util.*;

import org.gl.jmd.*;
import org.gl.jmd.dao.EtudiantDAO;
import org.gl.jmd.model.Diplome;
import org.gl.jmd.model.user.Etudiant;
import org.gl.jmd.utils.ServiceHandler;

import org.json.*;

import android.os.*;
import android.view.View;
import android.widget.*;
import android.app.*;
import android.content.res.Configuration;

/**
 * Activité correspondant à la vue de modification de l'accueil d'un étudiant.
 * 
 * @author Jordi CHARPENTIER & Yoann VANHOESERLANDE
 */
public class AjouterDiplomeE extends Activity {

	private Activity activity;

	private Toast toast;

	private Etudiant etud = EtudiantDAO.load();
	
	private EditText DIP;
	
	private ImageButton imgB;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.etudiant_ajouter_diplome);
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
		
		activity = this;
		toast = Toast.makeText(activity, "", Toast.LENGTH_SHORT);
		
		imgB = (ImageButton) findViewById(R.id.etudiant_ajout_diplome_img_recherche);
		DIP = (EditText) findViewById(R.id.etudiant_ajout_diplome_zone_nom);
	}

	public void recherche(View view) {
		ProgressDialog progress = new ProgressDialog(activity);
		progress.setMessage("Chargement...");
		new RechercherDiplome(progress, Constantes.URL_SERVER + "diplome/search" +
												"?nom=" + DIP.getText().toString()).execute();	
	}
	
	private void initListe(final ArrayList<Diplome> listeDiplomes) {
		final ListView liste = (ListView) findViewById(android.R.id.list);

		if (listeDiplomes.size() > 0) {
			final ArrayList<HashMap<String, String>> listItem = new ArrayList<HashMap<String, String>>();
			HashMap<String, String> map;

			for(int s = 0; s < listeDiplomes.size(); s++) {
				map = new HashMap<String, String>();

				map.put("id", "" + listeDiplomes.get(s).getId());
				map.put("titre", listeDiplomes.get(s).getNom());

				listItem.add(map);		
			}

			final SimpleAdapter mSchedule = new SimpleAdapter (getBaseContext(), listItem, R.layout.etudiant_simple_list, new String[] {"titre"}, new int[] {R.id.titre});

			liste.setAdapter(mSchedule); 

			liste.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				public void onItemClick(AdapterView<?> arg0, View arg1, final int position, long arg3) {
					String nom = listItem.get(position).get("titre");
					int id = Integer.parseInt(listItem.get(position).get("id"));
					
					Diplome dip = new Diplome();
					dip.setId(id);
					dip.setNom(nom);

					ArrayList<Diplome> listeDip = etud.getListeDiplomes();
					boolean exists = false;

					for (int i = 0; i < listeDip.size(); i++) {
						if (listeDip.get(i).getId() == id) {
							exists = true;
						}
					}

					if (!exists) {
						listeDip.add(dip);
						etud.setListeDiplomes(listeDip);
						
						if (EtudiantDAO.save(etud)) {
							finish();
						} else {
							toast.setText("Erreur lors de la sauvegarde du diplôme.");
							toast.show();
						}									
					} else {
						toast.setText("Le diplôme est déjà ajouté.");
						toast.show();
					}
				}
			});
		} else {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("titre", "Aucun résultat.");
			
			ArrayList<HashMap<String, String>> listItem = new ArrayList<HashMap<String, String>>();
			listItem.add(map);		

			liste.setAdapter(new SimpleAdapter (getBaseContext(), listItem, R.layout.etudiant_simple_list, new String[] {"titre"}, new int[] {R.id.titre})); 
		}
	}

	/**
	 * Classe interne représentant une tâche asynchrone qui sera effectuée en fond pendant un rond de chargement.
	 * 
	 * @author Jordi CHARPENTIER & Yoann VANHOESERLANDE
	 */
	private class RechercherDiplome extends AsyncTask<Void, Void, Void> {
		private ProgressDialog progress;
		private String pathUrl;

		public RechercherDiplome(ProgressDialog progress, String pathUrl) {
			this.progress = progress;
			this.pathUrl = pathUrl;
		}

		public void onPreExecute() {
			progress.show();
		}

		public void onPostExecute(Void unused) {
			progress.dismiss();
		}

		protected Void doInBackground(Void... arg0) {
			ServiceHandler sh = new ServiceHandler();
            String jsonStr = sh.makeServiceCall(pathUrl, ServiceHandler.GET);
            
            final ArrayList<Diplome> listeDiplomes = new ArrayList<Diplome>();
            Diplome d = null;
            
            if (jsonStr != null) {            	
                try {
                    JSONArray diplomes = new JSONArray(jsonStr);
 
                    for (int i = 0; i < diplomes.length(); i++) {
                    	JSONObject c = diplomes.getJSONObject(i);
                        
                        d = new Diplome();
                        d.setId(c.getInt("idDiplome"));
                        d.setNom(c.getString("nom"));
                        
                        listeDiplomes.add(d);
                    }
                    
                    AjouterDiplomeE.this.runOnUiThread(new Runnable() {
    					public void run() {    						
    						initListe(listeDiplomes);
    					}
    				});
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

			return null;
		}
	}

	/* Méthode héritée de la classe Activity. */

	/**
	 * Méthode permettant d'empécher la reconstruction de la vue lors de la rotation de l'écran. 
	 * 
	 * @param newConfig L'état de la vue avant la rotation.
	 */
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}
}