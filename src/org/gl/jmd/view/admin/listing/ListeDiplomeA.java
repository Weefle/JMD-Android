package org.gl.jmd.view.admin.listing;

import java.io.*;
import java.net.SocketTimeoutException;
import java.util.*;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.impl.client.DefaultHttpClient;
import org.gl.jmd.Constantes;
import org.gl.jmd.R;
import org.gl.jmd.model.Diplome;
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
 * Activité correspondant à la vue de listing des diplômes d'un établissement.
 * 
 * @author Jordi CHARPENTIER & Yoann VANHOESERLANDE
 */
public class ListeDiplomeA extends Activity {

	private Activity activity;

	private Toast toast;
	
	private long back_pressed;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.administrateur_liste_diplome);
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
		
		activity = this;
		toast = Toast.makeText(getBaseContext(), "", Toast.LENGTH_SHORT);

		actualiserListe();
	}

	private void actualiserListe() {
		ProgressDialog progress = new ProgressDialog(activity);
		progress.setMessage("Chargement...");
		new ListerDiplomes(progress, Constantes.URL_SERVER + "diplome/getAll").execute();	
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

			liste.setAdapter(new SimpleAdapter (getBaseContext(), listItem, R.layout.simple_list, new String[] {"titre"}, new int[] {R.id.titre})); 

			liste.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				public void onItemClick(AdapterView<?> arg0, View arg1, final int position, long arg3) {
					Intent newIntent = new Intent(ListeDiplomeA.this, ListeAnneeA.class);
					newIntent.putExtra("diplome", listeDiplomes.get(position));
					
					startActivity(newIntent);				
				}
			});

			liste.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
				public boolean onItemLongClick(AdapterView<?> arg0, View arg1, final int arg2, final long arg3) {
					AlertDialog.Builder confirmQuitter = new AlertDialog.Builder(ListeDiplomeA.this);
					confirmQuitter.setTitle("Suppression");
					confirmQuitter.setMessage("Voulez-vous vraiment supprimer cet élément ?");
					confirmQuitter.setCancelable(false);
					confirmQuitter.setPositiveButton("Oui", new AlertDialog.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							ProgressDialog progress = new ProgressDialog(activity);
							progress.setMessage("Chargement...");
							new DeleteDiplome(progress, Constantes.URL_SERVER + "diplome" +
									"?id=" + listItem.get(arg2).get("id") +
									"&token=" + FileUtils.lireFichier("/sdcard/cacheJMD/token.jmd") + 
									"&pseudo=" + FileUtils.lireFichier("/sdcard/cacheJMD/pseudo.jmd") +
									"&timestamp=" + new java.util.Date().getTime()).execute();
						}
					});
					
					confirmQuitter.setNegativeButton("Non", null);
					confirmQuitter.show();

					return true;
				}
			}); 
		} else {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("titre", "Aucun diplôme.");
			
			ArrayList<HashMap<String, String>> listItem = new ArrayList<HashMap<String, String>>();
			listItem.add(map);
			
			liste.setAdapter(new SimpleAdapter (getBaseContext(), listItem, R.layout.simple_list, new String[] {"titre"}, new int[] {R.id.titre})); 
		}
	}
	
	/* Classes internes. */

	private class ListerDiplomes extends AsyncTask<Void, Void, Void> {
		private ProgressDialog progress;
		private String pathUrl;

		public ListerDiplomes(ProgressDialog progress, String pathUrl) {
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
				ListeDiplomeA.this.runOnUiThread(new Runnable() {
					public void run() {
						AlertDialog.Builder builder = new AlertDialog.Builder(ListeDiplomeA.this);
						builder.setMessage("Erreur - Vérifiez votre connexion");
						builder.setCancelable(false);
						builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								ListeDiplomeA.this.finish();
							}
						});

						AlertDialog error = builder.create();
						error.show();
					}
				});
			} catch (ClientProtocolException e1) {
				ListeDiplomeA.this.runOnUiThread(new Runnable() {
					public void run() {
						AlertDialog.Builder builder = new AlertDialog.Builder(ListeDiplomeA.this);
						builder.setMessage("Erreur - Vérifiez votre connexion");
						builder.setCancelable(false);
						builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								ListeDiplomeA.this.finish();
							}
						});

						AlertDialog error = builder.create();
						error.show();
					}
				});
			} catch (IOException e1) {
				ListeDiplomeA.this.runOnUiThread(new Runnable() {
					public void run() {
						AlertDialog.Builder builder = new AlertDialog.Builder(ListeDiplomeA.this);
						builder.setMessage("Erreur - Vérifiez votre connexion");
						builder.setCancelable(false);
						builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								ListeDiplomeA.this.finish();
							}
						});

						AlertDialog error = builder.create();
						error.show();
					}
				});
			} 
            
            final ArrayList<Diplome> listeDiplomes = new ArrayList<Diplome>();
            Diplome d = null;
            
            if (jsonStr.length() > 0) {            	
                try {
                    JSONArray diplomes = new JSONArray(jsonStr);
 
                    for (int i = 0; i < diplomes.length(); i++) {
                    	JSONObject c = diplomes.getJSONObject(i);
                        
                        d = new Diplome();
                        d.setId(c.getInt("idDiplome"));
                        d.setNom(c.getString("nom"));
                        
                        listeDiplomes.add(d);
                    }
                    
                    ListeDiplomeA.this.runOnUiThread(new Runnable() {
    					public void run() {    						
    						initListe(listeDiplomes);
    					}
    				});
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
            	ListeDiplomeA.this.runOnUiThread(new Runnable() {
					public void run() {
						AlertDialog.Builder builder = new AlertDialog.Builder(ListeDiplomeA.this);
						builder.setMessage("Erreur - Vérifiez votre connexion");
						builder.setCancelable(false);
						builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								ListeDiplomeA.this.finish();
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

	private class DeleteDiplome extends AsyncTask<Void, Void, Void> {
		private ProgressDialog progress;
		private String pathUrl;

		public DeleteDiplome(ProgressDialog progress, String pathUrl) {
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
		        	ListeDiplomeA.this.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							actualiserListe();
						}
					});
		        	
		        	toast.setText("Diplôme supprimé.");
		        	toast.show();
		        } else if (response.getStatusLine().getStatusCode() == 401) {
		        	File filePseudo = new File("/sdcard/cacheJMD/pseudo.jmd");
		        	File fileToken = new File("/sdcard/cacheJMD/token.jmd");

		        	filePseudo.delete();
		        	fileToken.delete();

		        	finish();
		        	startActivity(new Intent(ListeDiplomeA.this, Accueil.class));	

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
		    	ListeDiplomeA.this.runOnUiThread(new Runnable() {
					public void run() {
						AlertDialog.Builder builder = new AlertDialog.Builder(ListeDiplomeA.this);
						builder.setMessage("Erreur - Vérifiez votre connexion");
						builder.setCancelable(false);
						builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								ListeDiplomeA.this.finish();
							}
						});

						AlertDialog error = builder.create();
						error.show();
					}
				});
		    } catch (IOException e) {
		    	ListeDiplomeA.this.runOnUiThread(new Runnable() {
					public void run() {
						AlertDialog.Builder builder = new AlertDialog.Builder(ListeDiplomeA.this);
						builder.setMessage("Erreur - Vérifiez votre connexion");
						builder.setCancelable(false);
						builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								ListeDiplomeA.this.finish();
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
	 * @param savedInstanceState L'état de la vue avant la rotation.
	 */
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	/**
	 * Méthode exécutée lorsque l'activité est relancée.<br />
	 * Ici, ça permet d'actualiser la liste des diplômes lorsqu'un diplôme vient d'être créé et que l'application ramène l'utilisateur sur cette vue de listing.
	 */
	@Override
	public void onRestart() {
		actualiserListe();

		super.onRestart();
	} 
	
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
}