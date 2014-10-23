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
import org.gl.jmd.ServiceHandler;
import org.gl.jmd.model.Diplome;
import org.gl.jmd.utils.*;
import org.gl.jmd.view.Accueil;
import org.json.*;

import android.app.*;
import android.content.*;
import android.content.res.Configuration;
import android.os.*;
import android.util.Log;
import android.view.View;
import android.widget.*;

/**
 * Activit� correspondant � la vue de listing des dipl�mes d'un �tablissement.
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

	public void actualiserListe() {
		ProgressDialog progress = new ProgressDialog(activity);
		progress.setMessage("Chargement...");
		new ListerDiplomes(progress, Constantes.URL_SERVER + "diplome/getAll").execute();	
	}

	public void initListe(final ArrayList<Diplome> listeDiplomes) {
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

			final SimpleAdapter mSchedule = new SimpleAdapter (getBaseContext(), listItem, R.layout.administrateur_liste_diplome_list, new String[] {"titre"}, new int[] {R.id.titre});

			liste.setAdapter(mSchedule); 

			liste.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				public void onItemClick(AdapterView<?> arg0, View arg1, final int position, long arg3) {
					Intent newIntent = new Intent(ListeDiplomeA.this, ListeAnneeA.class);
					newIntent.putExtra("idDiplome", listItem.get(position).get("id"));
					newIntent.putExtra("nomDiplome", listItem.get(position).get("titre"));
					
					startActivity(newIntent);				
				}
			});

			liste.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
				public boolean onItemLongClick(AdapterView<?> arg0, View arg1, final int arg2, final long arg3) {
					AlertDialog.Builder confirmQuitter = new AlertDialog.Builder(ListeDiplomeA.this);
					confirmQuitter.setTitle("Suppression");
					confirmQuitter.setMessage("Voulez-vous vraiment supprimer cet �l�ment ?");
					confirmQuitter.setCancelable(false);
					confirmQuitter.setPositiveButton("Oui", new AlertDialog.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							String URL = Constantes.URL_SERVER + "diplome" +
									"?id=" + listItem.get(arg2).get("id") +
									"&token=" + FileUtils.lireFichier("/sdcard/cacheJMD/token.jmd") + 
									"&pseudo=" + FileUtils.lireFichier("/sdcard/cacheJMD/pseudo.jmd") +
									"&timestamp=" + new java.util.Date().getTime();	

							ProgressDialog progress = new ProgressDialog(activity);
							progress.setMessage("Chargement...");
							new DeleteDiplome(progress, URL).execute();

							actualiserListe();

							liste.setSelection(arg2);
						}
					});
					
					confirmQuitter.setNegativeButton("Non", null);
					confirmQuitter.show();

					return true;
				}

			}); 
		} else {
			final ArrayList<HashMap<String, String>> listItem = new ArrayList<HashMap<String, String>>();
			HashMap<String, String> map;

			map = new HashMap<String, String>();
			map.put("titre", "Aucun dipl�me.");

			listItem.add(map);

			SimpleAdapter mSchedule = new SimpleAdapter (getBaseContext(), listItem, R.layout.administrateur_liste_diplome_list, new String[] {"titre"}, new int[] {R.id.titre});

			liste.setAdapter(mSchedule); 
		}
	}

	/**
	 * Classe interne repr�sentant une t�che asynchrone (lister les dipl�mes d'un �tablissement pr�sents en base) qui sera effectu�e en fond pendant un rond de chargement.
	 * 
	 * @author Jordi CHARPENTIER & Yoann VANHOESERLANDE
	 */
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
                    
                    ListeDiplomeA.this.runOnUiThread(new Runnable() {
    					public void run() {    						
    						initListe(listeDiplomes);

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

	/**
	 * Classe interne repr�sentant une t�che asynchrone (suppression d'un dipl�me) qui sera effectu�e en fond pendant un rond de chargement.
	 * 
	 * @author Jordi CHARPENTIER & Yoann VANHOESERLANDE
	 */
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

		        ListeDiplomeA.this.runOnUiThread(new Runnable() {
					public void run() {    			
				        if (response.getStatusLine().getStatusCode() == 200) {
				        	toast.setText("Dipl�me supprim�.");
				        	toast.show();
				        } else if (response.getStatusLine().getStatusCode() == 401) {
							File filePseudo = new File("/sdcard/cacheJMD/pseudo.jmd");
							File fileToken = new File("/sdcard/cacheJMD/token.jmd");
							
							filePseudo.delete();
							fileToken.delete();
				        	
							finishAllActivities();
				        	startActivity(new Intent(ListeDiplomeA.this, Accueil.class));	
				        	
				        	toast.setText("Session expir�e.");	
							toast.show();
				        } else if (response.getStatusLine().getStatusCode() == 500) {				        	
				        	toast.setText("Une erreur est survenue au niveau de la BDD.");	
							toast.show();
				        } else {
				        	toast.setText("Erreur inconnue. Veuillez r�essayer.");	
							toast.show();
				        }
				        
				        return;
					}
				});
		    } catch (ClientProtocolException e) {
		    	ListeDiplomeA.this.runOnUiThread(new Runnable() {
					public void run() {
						AlertDialog.Builder builder = new AlertDialog.Builder(ListeDiplomeA.this);
						builder.setMessage("Erreur - V�rifiez votre connexion");
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
						builder.setMessage("Erreur - V�rifiez votre connexion");
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
	
	public void finishAllActivities(){
		this.finishAffinity();
	}

	/* M�thodes h�rit�es de la classe Activity. */

	/**
	 * M�thode permettant d'emp�cher la reconstruction de la vue lors de la rotation de l'�cran. 
	 * 
	 * @param savedInstanceState L'�tat de la vue avant la rotation.
	 */
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	/**
	 * M�thode ex�cut�e lorsque l'activit� est relanc�e.<br />
	 * Ici, �a permet d'actualiser la liste des dipl�mes lorsqu'un dipl�me vient d'�tre cr�� et que l'application ram�ne l'utilisateur sur cette vue de listing.
	 */
	@Override
	public void onRestart() {
		actualiserListe();

		super.onRestart();
	} 
	
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
}