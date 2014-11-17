package org.gl.jmd.view.menu.admin;

import java.io.*;
import java.net.SocketTimeoutException;
import java.util.*;

import org.apache.http.HttpResponse;
import org.apache.http.client.*;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.gl.jmd.*;
import org.gl.jmd.model.Admin;
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
 * Activité correspondant à la vue permettant d'ajouter un administrateur.
 * 
 * @author Jordi CHARPENTIER & Yoann VANHOESERLANDE
 */
public class AjouterAdminA extends Activity {

	private Activity activity;

	private Toast toast;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.administrateur_ajout_admin);
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
		
		activity = this;
		toast = Toast.makeText(activity, "", Toast.LENGTH_SHORT);

		ProgressDialog progress = new ProgressDialog(activity);
		progress.setMessage("Chargement...");
		new InitListeComptes(progress, Constantes.URL_SERVER + "admin/getAllAdminInactive").execute();	
	}
	
	private void initListe(final ArrayList<Admin> listeAdmins) {
		final ListView liste = (ListView) findViewById(android.R.id.list);

		if (listeAdmins.size() > 0) {
			final ArrayList<HashMap<String, String>> listItem = new ArrayList<HashMap<String, String>>();
			HashMap<String, String> map;

			for(int s = 0; s < listeAdmins.size(); s++) {
				map = new HashMap<String, String>();

				map.put("id", "" + listeAdmins.get(s).getId());
				map.put("titre", listeAdmins.get(s).getPseudo());

				listItem.add(map);		
			}

			final SimpleAdapter mSchedule = new SimpleAdapter (getBaseContext(), listItem, R.layout.simple_list, new String[] {"titre"}, new int[] {R.id.titre});

			liste.setAdapter(mSchedule); 

			liste.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				public void onItemClick(AdapterView<?> arg0, View arg1, final int position, long arg3) {
					AlertDialog.Builder confirmAjout = new AlertDialog.Builder(AjouterAdminA.this);
					confirmAjout.setTitle("Confirmation");
					confirmAjout.setMessage("Nommer " + listeAdmins.get(position).getPseudo() + " ?");
					confirmAjout.setCancelable(false);
					confirmAjout.setPositiveButton("Oui", new AlertDialog.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {								
							ProgressDialog progress = new ProgressDialog(activity);
							progress.setMessage("Chargement...");
							new NommerAdmin(progress, Constantes.URL_SERVER + "admin/nominateAdmin" +
									"?pseudoToNominate=" + listeAdmins.get(position).getPseudo() +
									"&token=" + FileUtils.lireFichier("/sdcard/cacheJMD/token.jmd") + 
									"&pseudo=" + FileUtils.lireFichier("/sdcard/cacheJMD/pseudo.jmd") +
									"&timestamp=" + new java.util.Date().getTime()
							).execute();	
						}
					});
					
					confirmAjout.setNegativeButton("Non", null);
					confirmAjout.show();
				}
			}); 
		} else {
			final ArrayList<HashMap<String, String>> listItem = new ArrayList<HashMap<String, String>>();
			HashMap<String, String> map;

			map = new HashMap<String, String>();
			map.put("titre", "Aucun compte en attente.");

			listItem.add(map);

			SimpleAdapter mSchedule = new SimpleAdapter (getBaseContext(), listItem, R.layout.simple_list, new String[] {"titre"}, new int[] {R.id.titre});

			liste.setAdapter(mSchedule); 
		}
	}
	
	private class InitListeComptes extends AsyncTask<Void, Void, Void> {
		private ProgressDialog progress;
		private String pathUrl;

		public InitListeComptes(ProgressDialog progress, String pathUrl) {
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
				AjouterAdminA.this.runOnUiThread(new Runnable() {
					public void run() {
						AlertDialog.Builder builder = new AlertDialog.Builder(AjouterAdminA.this);
						builder.setMessage("Erreur - Vérifiez votre connexion");
						builder.setCancelable(false);
						builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								AjouterAdminA.this.finish();
							}
						});

						AlertDialog error = builder.create();
						error.show();
					}
				});
			} catch (ClientProtocolException e1) {
				AjouterAdminA.this.runOnUiThread(new Runnable() {
					public void run() {
						AlertDialog.Builder builder = new AlertDialog.Builder(AjouterAdminA.this);
						builder.setMessage("Erreur - Vérifiez votre connexion");
						builder.setCancelable(false);
						builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								AjouterAdminA.this.finish();
							}
						});

						AlertDialog error = builder.create();
						error.show();
					}
				});
			} catch (IOException e1) {
				AjouterAdminA.this.runOnUiThread(new Runnable() {
					public void run() {
						AlertDialog.Builder builder = new AlertDialog.Builder(AjouterAdminA.this);
						builder.setMessage("Erreur - Vérifiez votre connexion");
						builder.setCancelable(false);
						builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								AjouterAdminA.this.finish();
							}
						});

						AlertDialog error = builder.create();
						error.show();
					}
				});
			} 
            
            final ArrayList<Admin> listeAdmins = new ArrayList<Admin>();
            Admin a = null;
            
            if (jsonStr.length() > 0) {            	
                try {
                    JSONArray admins = new JSONArray(jsonStr);
 
                    for (int i = 0; i < admins.length(); i++) {
                    	JSONObject c = admins.getJSONObject(i);
                        
                        a = new Admin();
                        a.setNom(c.getString("nom"));
                        a.setIsActive(c.getBoolean("estActive"));
                        a.setPrenom(c.getString("prenom"));
                        a.setId(c.getInt("id"));
                        a.setPseudo(c.getString("pseudo"));
                        
                        listeAdmins.add(a);
                    }
                    
                    AjouterAdminA.this.runOnUiThread(new Runnable() {
    					public void run() {
    						initListe(listeAdmins);

    						return;
    					}
    				});
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } 

			return null;
		}
	}

	private class NommerAdmin extends AsyncTask<Void, Void, Void> {
		private ProgressDialog progress;
		private String pathUrl;

		public NommerAdmin(ProgressDialog progress, String pathUrl) {
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
		    HttpGet httppost = new HttpGet(pathUrl);

		    try {
		        HttpResponse response = httpclient.execute(httppost);
		        
		        if (response.getStatusLine().getStatusCode() == 200) {
		        	toast.setText("Le compte a été nommé avec succès.");
		        	toast.show();
		        	
		        	finish();
		        } else if (response.getStatusLine().getStatusCode() == 401) {
		        	File filePseudo = new File("/sdcard/cacheJMD/pseudo.jmd");
					File fileToken = new File("/sdcard/cacheJMD/token.jmd");
					
					filePseudo.delete();
					fileToken.delete();
		        	
					finishAllActivities();
		        	startActivity(new Intent(AjouterAdminA.this, Accueil.class));	
		        } else if (response.getStatusLine().getStatusCode() == 500) {
		        	toast.setText("Une erreur est survenue au niveau de la BDD.");	
					toast.show();
		        } else {
		        	toast.setText("Erreur inconnue. Veuillez réessayer.");	
					toast.show();
		        }
		    } catch (ClientProtocolException e) {
		    	AjouterAdminA.this.runOnUiThread(new Runnable() {
					public void run() {
						AlertDialog.Builder builder = new AlertDialog.Builder(AjouterAdminA.this);
						builder.setMessage("Erreur - Vérifiez votre connexion");
						builder.setCancelable(false);
						builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								AjouterAdminA.this.finish();
							}
						});

						AlertDialog error = builder.create();
						error.show();
					}
				});
		    } catch (IOException e) {
		    	AjouterAdminA.this.runOnUiThread(new Runnable() {
					public void run() {
						AlertDialog.Builder builder = new AlertDialog.Builder(AjouterAdminA.this);
						builder.setMessage("Erreur - Vérifiez votre connexion");
						builder.setCancelable(false);
						builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								AjouterAdminA.this.finish();
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
