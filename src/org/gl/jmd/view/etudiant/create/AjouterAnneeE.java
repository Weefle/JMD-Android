package org.gl.jmd.view.etudiant.create;

import java.util.*;

import org.gl.jmd.*;
import org.gl.jmd.dao.EtudiantDAO;
import org.gl.jmd.model.*;
import org.gl.jmd.model.enumeration.*;
import org.gl.jmd.model.user.Etudiant;
import org.gl.jmd.utils.ServiceHandler;
import org.json.*;

import android.os.*;
import android.util.Log;
import android.view.View;
import android.widget.*;
import android.app.*;
import android.content.res.Configuration;

/**
 * Activité correspondant à la vue d'ajout des années d'un diplôme (en local).
 * 
 * @author Jordi CHARPENTIER & Yoann VANHOESERLANDE
 */
public class AjouterAnneeE extends Activity {

	private Toast toast;

	private Activity activity;

	private int idDiplome = 0;

	private Etudiant etud = EtudiantDAO.load();

	private Etablissement selectedEta = null;

	private EditText ETA;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.etudiant_ajouter_annees);
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

		activity = this;
		toast = Toast.makeText(activity, "", Toast.LENGTH_SHORT);

		ETA = (EditText) findViewById(R.id.etudiant_ajout_annee_zone_eta);
		idDiplome = getIntent().getExtras().getInt("idDiplome");
	}

	public void openListEta(View view) {
		ProgressDialog progress = new ProgressDialog(activity);
		progress.setMessage("Chargement...");
		new InitEtablissement(progress, Constantes.URL_SERVER + "etablissement/getAll").execute();	
	}

	private void initListe(final ArrayList<Annee> listeAnnees) {
		final ListView liste = (ListView) findViewById(android.R.id.list);

		if (listeAnnees.size() > 0) {
			final ArrayList<HashMap<String, String>> listItem = new ArrayList<HashMap<String, String>>();
			HashMap<String, String> map;

			for(int s = 0; s < listeAnnees.size(); s++) {
				map = new HashMap<String, String>();

				map.put("id", "" + listeAnnees.get(s).getId());
				map.put("titre", listeAnnees.get(s).getNom());

				listItem.add(map);		
			}

			final SimpleAdapter mSchedule = new SimpleAdapter (getBaseContext(), listItem, R.layout.etudiant_ajouter_annees_list, new String[] {"titre"}, new int[] {R.id.titre});

			liste.setAdapter(mSchedule); 

			liste.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				public void onItemClick(AdapterView<?> arg0, View arg1, final int position, long arg3) {
					Log.e("AjouterAnneeE", Constantes.URL_SERVER + "annee/getCompleteYear" +
							"?idAnnee" + listItem.get(position).get("id"));
					
					ProgressDialog progress = new ProgressDialog(activity);
					progress.setMessage("Chargement...");
					new AjouterAnnee(progress, Constantes.URL_SERVER + "annee/getCompleteYear" +
							"?idAnnee=" + listItem.get(position).get("id")).execute();	
				}
			});
		} else {
			ArrayList<HashMap<String, String>> listItem = new ArrayList<HashMap<String, String>>();
			HashMap<String, String> map;

			map = new HashMap<String, String>();
			map.put("titre", "Aucune année.");

			listItem.add(map);

			SimpleAdapter mSchedule = new SimpleAdapter (getBaseContext(), listItem, R.layout.etudiant_ajouter_annees_empty_list, new String[] {"titre"}, new int[] {R.id.titre});

			liste.setAdapter(mSchedule); 
		}
	}

	/* Classes internes. */

	private class InitEtablissement extends AsyncTask<Void, Void, Void> {
		private ProgressDialog progress;
		private String pathUrl;

		public InitEtablissement(ProgressDialog progress, String pathUrl) {
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

			final ArrayList<Etablissement> listeEtablissements = new ArrayList<Etablissement>();
			final ArrayList<String> listeEtablissementsString = new ArrayList<String>();
			Etablissement e = null;

			if (jsonStr != null) {            	
				try {
					JSONArray diplomes = new JSONArray(jsonStr);

					for (int i = 0; i < diplomes.length(); i++) {
						JSONObject c = diplomes.getJSONObject(i);

						e = new Etablissement();
						e.setId(c.getInt("idEtablissement"));
						e.setNom(c.getString("nom"));
						e.setVille(c.getString("ville"));

						listeEtablissementsString.add(e.getNom());
						listeEtablissements.add(e);
					}
				} catch (JSONException ex) {
					ex.printStackTrace();
				}
			} 

			AjouterAnneeE.this.runOnUiThread(new Runnable() {
				public void run() {
					if (listeEtablissements.size() > 0) {
						final Dialog dialog = new Dialog(activity);
						dialog.setTitle("Liste des établissements");
						dialog.setCancelable(true);

						ListView listView = new ListView(activity);
						listView.setAdapter(new ArrayAdapter<String>(activity, android.R.layout.simple_list_item_1, listeEtablissementsString));

						listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
							public void onItemClick(AdapterView<?> arg0, View arg1, final int position, long arg3) {					    		
								ETA.setText(listeEtablissementsString.get(position));
								selectedEta = listeEtablissements.get(position);

								ProgressDialog progress = new ProgressDialog(activity);
								progress.setMessage("Chargement...");
								new RechercherAnnees(progress, Constantes.URL_SERVER + "annee/getAnnees" +
										"?idDiplome=" + idDiplome +
										"&idEtablissement=" + selectedEta.getId()).execute();

								dialog.hide();
							}

						});

						dialog.setContentView(listView);
						dialog.show();
					} else {						
						Button button = (Button) findViewById(R.id.crea_annee_bout_creer);
						button.setEnabled(false);
					}
				}
			});

			return null;
		}
	}

	private class AjouterAnnee extends AsyncTask<Void, Void, Void> {
		private ProgressDialog progress;
		private String pathUrl;

		public AjouterAnnee(ProgressDialog progress, String pathUrl) {
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

			Annee a = new Annee();

			if (jsonStr != null) {            	
				try {
					JSONObject anneeJSON = new JSONObject(jsonStr);

					a.setId(anneeJSON.getInt("idAnnee"));
					a.setNom(anneeJSON.getString("nom"));
					a.setDecoupage(DecoupageType.valueOf(anneeJSON.getString("decoupage")));
					
					Etablissement e = new Etablissement();
					e.setId(anneeJSON.getInt("idEtablissement"));
					e.setNom(anneeJSON.getString("nomEtablissement"));

					a.setEtablissement(e);

					Diplome d = new Diplome();
					d.setId(anneeJSON.getInt("idDiplome"));
					d.setNom(anneeJSON.getString("nomDiplome"));

					a.setDiplome(d);
					
					// Liste des UE.
					JSONArray uesJSON = anneeJSON.getJSONArray("ues");
					ArrayList<UE> listeUE = new ArrayList<UE>();
					UE ue = null;
					
                    for (int i = 0; i < uesJSON.length(); i++) {
                    	JSONObject ueJSON = uesJSON.getJSONObject(i);
                        
                        ue = new UE();
                        ue.setId(ueJSON.getInt("idUE"));
                        ue.setNom(ueJSON.getString("nom"));
                        ue.setDecoupage(DecoupageYearType.valueOf(ueJSON.getString("yearType")));
                        
                        ArrayList<Matiere> listeMatieres = new ArrayList<Matiere>();
                        Matiere m = null;
                        JSONArray matieresJSON = ueJSON.getJSONArray("matieres");
                        
                        for (int j = 0; j < matieresJSON.length(); j++) {
                        	JSONObject matiereJSON = matieresJSON.getJSONObject(j);
                        	
                        	m = new Matiere();
                        	m.setId(matiereJSON.getInt("idMatiere"));
                        	m.setCoefficient(matiereJSON.getLong("coefficient"));
                        	m.setNom(matiereJSON.getString("nom"));
                        	m.setIsOption(matiereJSON.getBoolean("isOption"));
                        	
                        	listeMatieres.add(m);
                        }
                        
                        ue.setListeMatieres(listeMatieres);
                        listeUE.add(ue);
                    }
                    
                    a.setListeUE(listeUE);

					// Sauvegarde de l'année.
					
					int indexDiplome = 0;
					boolean exists = false;
					
					for (int i = 0; i < etud.getListeDiplomes().size(); i++) {
						if (etud.getListeDiplomes().get(i).getId() == idDiplome) {
							indexDiplome = i;
						}
					}
					
					for (int i = 0; i < etud.getListeDiplomes().size(); i++) {
						for (int j = 0; j < etud.getListeDiplomes().get(i).getListeAnnees().size(); j++) {
							if (etud.getListeDiplomes().get(i).getListeAnnees().get(j).getId() == a.getId()) {
								exists = true;
							}
						}
					}

					if (!exists) {
						etud.getListeDiplomes().get(indexDiplome).getListeAnnees().add(a);
						
						if (EtudiantDAO.save(etud)) {
							finish();
						} else {
							toast.setText("Erreur lors de la sauvegarde du diplôme.");
							toast.show();
						}									
					} else {
						toast.setText("L'année est déjà ajoutée.");
						toast.show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			return null;
		}
	}

	private class RechercherAnnees extends AsyncTask<Void, Void, Void> {
		private ProgressDialog progress;
		private String pathUrl;

		public RechercherAnnees(ProgressDialog progress, String pathUrl) {
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

			final ArrayList<Annee> listeAnnees = new ArrayList<Annee>();
			Annee a = null;

			if (jsonStr != null) {            	
				try {
					JSONArray annees = new JSONArray(jsonStr);

					for (int i = 0; i < annees.length(); i++) {
						JSONObject c = annees.getJSONObject(i);

						a = new Annee();
						a.setId(c.getInt("idAnnee"));
						a.setNom(c.getString("nom"));
						a.setIsLast(c.getBoolean("isLastYear"));

						Etablissement e = new Etablissement();
						e.setId(c.getInt("idEtablissement"));

						a.setEtablissement(e);

						Diplome d = new Diplome();
						d.setId(c.getInt("idDiplome"));

						a.setDiplome(d);

						listeAnnees.add(a);
					}

					AjouterAnneeE.this.runOnUiThread(new Runnable() {
						public void run() {    						
							initListe(listeAnnees);
						}
					});
				} catch (JSONException ex) {
					ex.printStackTrace();
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