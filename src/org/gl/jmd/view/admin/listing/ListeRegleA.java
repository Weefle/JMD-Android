package org.gl.jmd.view.admin.listing;

import java.io.*;
import java.util.*;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.impl.client.DefaultHttpClient;
import org.gl.jmd.Constantes;
import org.gl.jmd.R;
import org.gl.jmd.model.enumeration.OperateurType;
import org.gl.jmd.utils.*;
import org.gl.jmd.view.Accueil;

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
	
	private String idAnnee = "";
	
	private Toast toast;
	
	private String contenuPage = "";
	
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
	
	public void actualiserListe() {
		String URL = "http://www.jordi-charpentier.com/jmd/mobile/getInfos.php?type=regle&idAnnee=" + idAnnee;
		
		ProgressDialog progress = new ProgressDialog(this);
		progress.setMessage("Chargement...");
		new ListerRegles(progress, URL).execute();	
	}
	
	public void initListe() {
		StringTokenizer chaineEclate = new StringTokenizer(contenuPage, ";");
		
		final String[] temp = new String[chaineEclate.countTokens()];
		final ListView liste = (ListView) findViewById(android.R.id.list);
		
		if (temp.length != 0) {
			int i = 0;

			while(chaineEclate.hasMoreTokens()) {
				temp[i++] = chaineEclate.nextToken();	
			}

			final ArrayList<HashMap<String, String>> listItem = new ArrayList<HashMap<String, String>>();
			HashMap<String, String> map;
			OperateurType o;
			
			for(int s = 0; s < temp.length;) {
				map = new HashMap<String, String>();

				map.put("id", temp[s]);
				
				if (temp[s + 4].equals("0")) {
					map.put("operateur", ">");
					o = OperateurType.SUPERIEUR;
				} else if (temp[s + 4].equals("1")) {
					map.put("operateur", ">=");
					o = OperateurType.SUPERIEUR_EGAL;
				} else if (temp[s + 4].equals("2")) {
					map.put("operateur", "=");
					o = OperateurType.EGAL;
				} else if (temp[s + 4].equals("3")) {
					map.put("operateur", "<=");
					o = OperateurType.INFERIEUR_EGAL;
				} else if (temp[s + 4].equals("4")) {
					map.put("operateur", "<");
					o = OperateurType.INFERIEUR;
				} else {
					Log.i("ListeRegleA", temp[s+4]);
					
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
				
				if (temp[s + 1].equals("1")) {
					map.put("titre", "Nb d'option(s) minimale(s) " + operateurAffiche + " à " + temp[s + 5]);
				} else if (temp[s + 1].equals("2")) {
					map.put("titre", "Note minimale " + operateurAffiche + " à " + temp[s + 5]);
				} else {
					toast.setText("Erreur - Veuillez réessayer");
					toast.show();
					
					return;
				}
				
				map.put("description", "S'applique sur : UE (" + (temp[s+2].equals("1000000000") ? "toutes" : "ID : " + temp[s+2]) + "), matière (" + (temp[s+3].equals("1000000000") ? "toutes" : "ID : " + temp[s+3]) + ")");
				
				map.put("idUE", temp[s + 2]);
				map.put("idMatiere", temp[s + 3]);
				
				map.put("value", temp[s + 5]);
				
				s = s + 6;

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
								String URL = Constantes.URL_SERVER + "regle" +
										"?id=" + listItem.get(arg2).get("id") +
										"&token=" + FileUtils.lireFichier("/sdcard/cacheJMD/token.jmd") + 
										"&pseudo=" + FileUtils.lireFichier("/sdcard/cacheJMD/pseudo.jmd") +
										"&timestamp=" + new java.util.Date().getTime();	
								
								ProgressDialog progress = new ProgressDialog(activity);
								progress.setMessage("Chargement...");
								new DeleteRegle(progress, URL).execute();
								
								actualiserListe();
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
	
	/**
	 * Classe interne représentant une tâche asynchrone qui sera effectuée en fond pendant un rond de chargement.
	 * 
	 * @author Jordi CHARPENTIER & Yoann VANHOESERLANDE
	 */
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
			try {
				if((contenuPage = WebUtils.getPage(pathUrl)) != "-1");

				else {
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
						}});

					return null;
				}
			} catch (ClientProtocolException e) { 
				return null;
			} catch (IOException e) { 
				return null;
			} 	
			
			contenuPage = contenuPage.replaceAll("\n", "");
			contenuPage = contenuPage.replaceAll(" ", "");
			
			if (contenuPage.equals("error")) {
				toast.setText("Erreur. Veuillez réessayer.");
				toast.show();	
			} else {
				ListeRegleA.this.runOnUiThread(new Runnable() {
					public void run() {
						initListe();
						
						return;
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

		        ListeRegleA.this.runOnUiThread(new Runnable() {
					public void run() {    			
				        if (response.getStatusLine().getStatusCode() == 200) {
				        	toast.setText("Règle supprimée.");
				        	toast.show();
				        } else if (response.getStatusLine().getStatusCode() == 401) {
							File filePseudo = new File("/sdcard/cacheJMD/pseudo.jmd");
							File fileToken = new File("/sdcard/cacheJMD/token.jmd");
							
							filePseudo.delete();
							fileToken.delete();
				        	
							finishAllActivities();
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
				        
				        return;
					}
				});
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
	
	public void finishAllActivities(){
		this.finishAffinity();
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
