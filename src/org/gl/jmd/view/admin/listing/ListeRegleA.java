package org.gl.jmd.view.admin.listing;

import java.io.*;
import java.net.SocketTimeoutException;
import java.util.*;

import org.apache.http.HttpResponse;
import org.apache.http.client.*;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.impl.client.DefaultHttpClient;
import org.gl.jmd.*;
import org.gl.jmd.model.*;
import org.gl.jmd.utils.*;
import org.gl.jmd.view.Accueil;
import org.gl.jmd.view.list.Header;
import org.gl.jmd.view.list.TwoTextArrayAdapter;
import org.gl.jmd.view.list.item.*;
import org.json.*;

import android.app.*;
import android.content.*;
import android.content.res.Configuration;
import android.os.*;
import android.util.Log;
import android.view.View;
import android.widget.*;

/**
 * Activité correspondant à la vue de listing des années d'un diplôme.
 * 
 * @author Jordi CHARPENTIER & Yoann VANHOESERLANDE
 */
public class ListeRegleA extends Activity {

	private Activity activity;

	private Annee a = null;

	private Toast toast;

	private ArrayList<Regle> listeRegles = new ArrayList<Regle>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.administrateur_liste_regle);
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

		a = (Annee) getIntent().getExtras().getSerializable("annee");

		activity = this;
		toast = Toast.makeText(getBaseContext(), "", Toast.LENGTH_SHORT);

		actualiserListe();
	}

	private void actualiserListe() {
		listeRegles.clear();

		ProgressDialog progress = new ProgressDialog(this);
		progress.setMessage("Chargement...");
		new ListerRegles(progress, Constantes.URL_SERVER + "regle/getAllByAnnee" +
				"?idAnnee=" + a.getId()).execute();	
	}
	
	private boolean contains(String regleToString, ArrayList<Regle> listRegles) {
		boolean res = false;
		
		for (int i = 0; i < listRegles.size(); i++) {
			if (listRegles.get(i).toString().equals(regleToString)) {
				res = true;
				break;
			}
		}
		
		return res;
	}

	private void initListe() {
		if (this.listeRegles.isEmpty()) {
			ListView liste = (ListView) findViewById(android.R.id.list);
			ArrayList<HashMap<String, String>> listItem = new ArrayList<HashMap<String, String>>();

			HashMap<String, String> map = new HashMap<String, String>();
			map.put("titre", "Aucune règle.");

			listItem.add(map);		

			liste.setAdapter(new SimpleAdapter (getBaseContext(), listItem, R.layout.simple_list, new String[] {"titre"}, new int[] {R.id.titre}));
			liste.setOnItemClickListener(null);
		} else {
			List<Item> items = new ArrayList<Item>();
			ArrayList<Regle> listTypeRegle = new ArrayList<Regle>();
			
			for (int s = 0; s < this.listeRegles.size(); s++) {
				if (!contains(this.listeRegles.get(s).toString(), listTypeRegle)) {
					listTypeRegle.add(this.listeRegles.get(s));
				}
			}
			
			for (int s = 0; s < listTypeRegle.size(); s++) {
				items.add(new Header(listTypeRegle.get(s).toString()));
				
				for (int y = 0; y < this.listeRegles.size(); y++) {
					if (this.listeRegles.get(y).toString().equals(listTypeRegle.get(s).toString())) {
						items.add(new ListItemRegle(this.listeRegles.get(y), y));
					}
				}
			}

			final TwoTextArrayAdapter adapter = new TwoTextArrayAdapter(this, items);

			final ListView liste = (ListView) findViewById(android.R.id.list);

			liste.setAdapter(adapter);

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
					
					if (((ListItemRegle) adapter.getItem(arg2)).getRegle() == null) {
						// Do nothing.
					} else {
						AlertDialog.Builder confirmQuitter = new AlertDialog.Builder(ListeRegleA.this);
						confirmQuitter.setTitle("Suppression");
						confirmQuitter.setMessage("Voulez-vous vraiment supprimer cet élément ?");
						confirmQuitter.setCancelable(false);
						confirmQuitter.setPositiveButton("Oui", new AlertDialog.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {								
								ProgressDialog progress = new ProgressDialog(activity);
								progress.setMessage("Chargement...");
								new DeleteRegle(progress, Constantes.URL_SERVER + "regle" +
										"?id=" + ((ListItemRegle) adapter.getItem(arg2)).getRegle().getId() +
										"&token=" + FileUtils.readFile(Constantes.FILE_TOKEN) + 
										"&pseudo=" + FileUtils.readFile(Constantes.FILE_PSEUDO) +
										"&timestamp=" + new java.util.Date().getTime()).execute();
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
			String jsonStr = "";

			try {
				jsonStr = WebUtils.call(pathUrl, WebUtils.GET);
			} catch (SocketTimeoutException e1) {
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
			} catch (ClientProtocolException e1) {
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
			} catch (IOException e1) {
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

			Regle r = null;

			if (jsonStr.length() > 0) {            	
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
						r.setNomUE(c.getString("nomUE"));
						
						listeRegles.add(r);
					}

					ListeRegleA.this.runOnUiThread(new Runnable() {
						public void run() {    						
							initListe();
						}
					});
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else {
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

				Log.e("ListeRegleA", pathUrl);
				
				if (response.getStatusLine().getStatusCode() == 200) {
					toast.setText("Règle supprimée.");
					toast.show();

					ListeRegleA.this.runOnUiThread(new Runnable() {
						public void run() {
							actualiserListe();
						}
					});
				} else if (response.getStatusLine().getStatusCode() == 401) {
					Constantes.FILE_PSEUDO.delete();
					Constantes.FILE_TOKEN.delete();

					activity.finishAffinity();

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
