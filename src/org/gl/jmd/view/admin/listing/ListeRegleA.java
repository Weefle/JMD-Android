package org.gl.jmd.view.admin.listing;

import java.io.*;
import java.util.*;

import org.apache.http.HttpResponse;
import org.apache.http.client.*;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.impl.client.DefaultHttpClient;
import org.gl.jmd.*;
import org.gl.jmd.model.Regle;
import org.gl.jmd.model.enumeration.OperateurType;
import org.gl.jmd.utils.*;
import org.gl.jmd.view.Accueil;
import org.json.*;

import android.app.*;
import android.content.*;
import android.content.res.Configuration;
import android.os.*;
import android.view.View;
import android.widget.*;

/**
 * Activité correspondant à la vue de listing des années d'un diplôme.
 * 
 * @author Jordi CHARPENTIER & Yoann VANHOESERLANDE
 */
public class ListeRegleA extends Activity {

	private Activity activity;

	private String idAnnee = "";

	private Toast toast;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.administrateur_liste_regle);
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

		idAnnee = getIntent().getExtras().getString("idAnnee");

		activity = this;
		toast = Toast.makeText(getBaseContext(), "", Toast.LENGTH_SHORT);

		actualiserListe();
	}

	private void actualiserListe() {
		ProgressDialog progress = new ProgressDialog(this);
		progress.setMessage("Chargement...");
		new ListerRegles(progress, Constantes.URL_SERVER + "regle/getAllByAnnee?idAnnee=" + idAnnee).execute();	
	}

	private void initListe(final ArrayList<Regle> listeRegles) {
		final ListView liste = (ListView) findViewById(android.R.id.list);

		if (listeRegles.size() > 0) {
			final ArrayList<HashMap<String, String>> listItem = new ArrayList<HashMap<String, String>>();
			HashMap<String, String> map;
			OperateurType o;

			for(int s = 0; s < listeRegles.size(); s++) {
				map = new HashMap<String, String>();

				map.put("id", "" + listeRegles.get(s).getId());

				if (listeRegles.get(s).getOperateur() == 0) {
					map.put("operateur", ">");
					o = OperateurType.SUPERIEUR;
				} else if (listeRegles.get(s).getOperateur() == 1) {
					map.put("operateur", ">=");
					o = OperateurType.SUPERIEUR_EGAL;
				} else if (listeRegles.get(s).getOperateur() == 2) {
					map.put("operateur", "=");
					o = OperateurType.EGAL;
				} else if (listeRegles.get(s).getOperateur() == 3) {
					map.put("operateur", "<=");
					o = OperateurType.INFERIEUR_EGAL;
				} else if (listeRegles.get(s).getOperateur() == 0) {
					map.put("operateur", "<");
					o = OperateurType.INFERIEUR;
				} else {
					toast.setText("Erreur - Veuillez réessayer");
					toast.show();

					return;
				}

				String operateurAffiche = "";

				if (o.equals(OperateurType.SUPERIEUR)) {
					operateurAffiche = "supérieure";
				} else if (o.equals(OperateurType.SUPERIEUR_EGAL)) {
					operateurAffiche = "supérieure ou égale";
				} else if (o.equals(OperateurType.EGAL)) {
					operateurAffiche = "égale";
				} else if (o.equals(OperateurType.INFERIEUR_EGAL)) {
					operateurAffiche = "inférieure ou égale";
				} else if (o.equals(OperateurType.INFERIEUR)) {
					operateurAffiche = "inférieure";
				}

				if (listeRegles.get(s).getRegle() == 1) {
					map.put("titre", "Nb d'option(s) minimale(s) " + operateurAffiche + " à " + listeRegles.get(s).getValeur());
				} else if (listeRegles.get(s).getRegle() == 2) {
					map.put("titre", "Note minimale " + operateurAffiche + " à " + listeRegles.get(s).getValeur());
				} else {
					toast.setText("Erreur - Veuillez réessayer");
					toast.show();

					return;
				}

				map.put("description", 
						"S'applique sur : UE (" + (listeRegles.get(s).getIdUE() == 1000000000 ? "toutes" : "ID : " + listeRegles.get(s).getIdUE()) + "), " +
						"matière (" + (listeRegles.get(s).getIdMatiere() == 1000000000 ? "toutes" : "ID : " + listeRegles.get(s).getIdMatiere() + ")"));

				map.put("idUE", "" + listeRegles.get(s).getIdUE());
				map.put("idMatiere", "" + listeRegles.get(s).getIdMatiere());

				map.put("value", "" + listeRegles.get(s).getValeur());

				listItem.add(map);		
			}

			final SimpleAdapter mSchedule = new SimpleAdapter (getBaseContext(), listItem, R.layout.administrateur_liste_regle_list, new String[] {"titre", "description"}, new int[] {R.id.titre, R.id.description});

			liste.setAdapter(mSchedule); 

			liste.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
				public boolean onItemLongClick(AdapterView<?> arg0, View arg1, final int arg2, final long arg3) {
					if (listItem.get(arg2).get("img") == null) {
						AlertDialog.Builder confirmQuitter = new AlertDialog.Builder(ListeRegleA.this);
						confirmQuitter.setTitle("Suppression");
						confirmQuitter.setMessage("Voulez-vous vraiment supprimer cet élément ?");
						confirmQuitter.setCancelable(false);
						confirmQuitter.setPositiveButton("Oui", new AlertDialog.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {								
								ProgressDialog progress = new ProgressDialog(activity);
								progress.setMessage("Chargement...");
								new DeleteRegle(progress, Constantes.URL_SERVER + "regle" +
										"?id=" + listItem.get(arg2).get("id") +
										"&token=" + FileUtils.lireFichier("/sdcard/cacheJMD/token.jmd") + 
										"&pseudo=" + FileUtils.lireFichier("/sdcard/cacheJMD/pseudo.jmd") +
										"&timestamp=" + new java.util.Date().getTime()).execute();
							}
						});

						confirmQuitter.setNegativeButton("Non", null);
						confirmQuitter.show();
					}

					return true;
				}

			}); 
		} else {
			final ArrayList<HashMap<String, String>> listItem = new ArrayList<HashMap<String, String>>();
			HashMap<String, String> map;

			map = new HashMap<String, String>();
			map.put("titre", "Aucune règle.");

			listItem.add(map);

			SimpleAdapter mSchedule = new SimpleAdapter (getBaseContext(), listItem, R.layout.administrateur_liste_regle_empty_list, new String[] {"titre"}, new int[] {R.id.titre});

			liste.setAdapter(mSchedule); 
		}
	}

	/* Classes internes. */

	private class ListerRegles extends AsyncTask<Void, Void, Void> {
		private ProgressDialog progress;
		private String pathUrl;

		public ListerRegles(ProgressDialog progress, String pathUrl) {
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
            
            final ArrayList<Regle> listeRegles = new ArrayList<Regle>();
            Regle r = null;
            
            if (jsonStr != null) {            	
                try {
                    JSONArray regles = new JSONArray(jsonStr);
 
                    for (int i = 0; i < regles.length(); i++) {
                    	JSONObject c = regles.getJSONObject(i);
                        
                        r = new Regle();
                        r.setId(c.getInt("id"));
                        r.setIdAnnee(c.getInt("idAnnee"));
                        r.setIdMatiere(c.getInt("idMatiere"));
                        r.setIdUE(c.getInt("idUE"));
                        r.setOperateur(c.getInt("operateur"));
                        r.setRegle(c.getInt("regle"));
                        r.setValeur(c.getInt("valeur"));
                        
                        listeRegles.add(r);
                    }
                    
                    ListeRegleA.this.runOnUiThread(new Runnable() {
    					public void run() {    						
    						initListe(listeRegles);
    					}
    				});
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } 

			return null;
		}
	}

	/**
	 * Classe interne représentant une tâche asynchrone (suppression d'une année) qui sera effectuée en fond pendant un rond de chargement.
	 * 
	 * @author Jordi CHARPENTIER & Yoann VANHOESERLANDE
	 */
	private class DeleteRegle extends AsyncTask<Void, Void, Void> {
		private ProgressDialog progress;
		private String pathUrl;

		public DeleteRegle(ProgressDialog progress, String pathUrl) {
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
			HttpClient httpclient = new DefaultHttpClient();
			HttpDelete httpDelete = new HttpDelete(pathUrl);

			try {
				final HttpResponse response = httpclient.execute(httpDelete);

				if (response.getStatusLine().getStatusCode() == 200) {
					toast.setText("Règle supprimée.");
					toast.show();

					ListeRegleA.this.runOnUiThread(new Runnable() {
						public void run() {
							actualiserListe();
						}
					});
				} else if (response.getStatusLine().getStatusCode() == 401) {
					File filePseudo = new File("/sdcard/cacheJMD/pseudo.jmd");
					File fileToken = new File("/sdcard/cacheJMD/token.jmd");

					filePseudo.delete();
					fileToken.delete();

					activity.finishAffinity()
					;
					startActivity(new Intent(ListeRegleA.this, Accueil.class));	

					toast.setText("Session expirée.");	
					toast.show();
				} else if (response.getStatusLine().getStatusCode() == 500) {				        	
					toast.setText("Une erreur est survenue au niveau de la BDD.");	
					toast.show();
				} else {
					toast.setText("Erreur inconnue. Veuillez réessayer.");	
					toast.show();
				}
			} catch (ClientProtocolException e) {
				ListeRegleA.this.runOnUiThread(new Runnable() {
					public void run() {
						AlertDialog.Builder builder = new AlertDialog.Builder(ListeRegleA.this);
						builder.setMessage("Erreur - Vérifiez votre connexion");
						builder.setCancelable(false);
						builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								ListeRegleA.this.finish();
							}
						});

						AlertDialog error = builder.create();
						error.show();
					}
				});
			} catch (IOException e) {
				ListeRegleA.this.runOnUiThread(new Runnable() {
					public void run() {
						AlertDialog.Builder builder = new AlertDialog.Builder(ListeRegleA.this);
						builder.setMessage("Erreur - Vérifiez votre connexion");
						builder.setCancelable(false);
						builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								ListeRegleA.this.finish();
							}
						});

						AlertDialog error = builder.create();
						error.show();
					}
				});
			}

			return null;
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
	 * Méthode exécutée lorsque l'activité est relancée.<br />
	 * Ici, ça permet d'actualiser la liste des années lorsqu'une année vient d'être créé et que l'application ramène l'utilisateur sur cette vue de listing.
	 */
	@Override
	public void onRestart() {
		actualiserListe();

		super.onRestart();
	} 
}
