package org.gl.jmd.view.admin.listing;

import java.io.*;
import java.net.SocketTimeoutException;
import java.util.*;

import org.apache.http.HttpResponse;
import org.apache.http.client.*;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.impl.client.DefaultHttpClient;
import org.gl.jmd.*;
import org.gl.jmd.model.Annee;
import org.gl.jmd.model.UE;
import org.gl.jmd.model.enumeration.DecoupageType;
import org.gl.jmd.model.enumeration.DecoupageYearType;
import org.gl.jmd.utils.*;
import org.gl.jmd.view.Accueil;
import org.gl.jmd.view.list.*;
import org.json.*;

import android.app.*;
import android.content.*;
import android.content.res.Configuration;
import android.os.*;
import android.view.View;
import android.widget.*;

/**
 * Activité correspondant à la vue de listing des UE d'une année.
 * 
 * @author Jordi CHARPENTIER & Yoann VANHOESERLANDE
 */
public class ListeUEA extends Activity {
	
	private Activity activity;
	
	private Toast toast;
	
	private Annee annee = null;
	
	private ArrayList<UE> listeUE = new ArrayList<UE>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.administrateur_liste_ue);
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
		
		annee = (Annee) getIntent().getExtras().getSerializable("annee");
		
		activity = this;
		toast = Toast.makeText(getBaseContext(), "", Toast.LENGTH_SHORT);
		
		actualiserListe();
	}

	private void actualiserListe() {		
		listeUE.clear();
		
		ProgressDialog progress = new ProgressDialog(this);
		progress.setMessage("Chargement...");
		new ListerUE(progress, Constantes.URL_SERVER + "ue/getAllUEOfAnnee" +
				"?idAnnee=" + annee.getId()).execute();
	}
	
	private void initListe() {
		if (listeUE.size() == 0) {
			ListView liste = (ListView) findViewById(R.id.listUEAdmin);
			ArrayList<HashMap<String, String>> listItem = new ArrayList<HashMap<String, String>>();
			
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("titre", "Aucune UE.");
			
			listItem.add(map);		
	
			liste.setAdapter(new SimpleAdapter (getBaseContext(), listItem, R.layout.simple_list, new String[] {"titre"}, new int[] {R.id.titre}));
			liste.setOnItemClickListener(null);
		} else {
			List<Item> items = new ArrayList<Item>();
			
			if (annee.getDecoupage() == DecoupageType.NULL) {
				items.add(new Header("CONTRÔLE CONTINU"));
				
				for (int s = 0; s < listeUE.size(); s++) {
					items.add(new ListItemUE(listeUE.get(s), s));	
				}
			} else if (annee.getDecoupage() == DecoupageType.SEMESTRE) {
				items.add(new Header("SEMESTRE 1"));
				
				for (int s = 0; s < listeUE.size(); s++) {
					if (listeUE.get(s).getDecoupage() == DecoupageYearType.SEM1) {
						items.add(new ListItemUE(listeUE.get(s), s));	
					}
				}
				
				items.add(new Header("SEMESTRE 2"));
				
				for (int s = 0; s < listeUE.size(); s++) {
					if (listeUE.get(s).getDecoupage() == DecoupageYearType.SEM2) {
						items.add(new ListItemUE(listeUE.get(s), s));	
					}
				}
			} else if (annee.getDecoupage() == DecoupageType.TRIMESTRE) {
				items.add(new Header("TRIMESTRE 1"));
				
				for (int s = 0; s < listeUE.size(); s++) {
					if (listeUE.get(s).getDecoupage() == DecoupageYearType.TRI1) {
						items.add(new ListItemUE(listeUE.get(s), s));	
					}
				}
				
				items.add(new Header("TRIMESTRE 2"));
				
				for (int s = 0; s < listeUE.size(); s++) {
					if (listeUE.get(s).getDecoupage() == DecoupageYearType.TRI2) {
						items.add(new ListItemUE(listeUE.get(s), s));	
					}
				}
				
				items.add(new Header("TRIMESTRE 3"));
				
				for (int s = 0; s < listeUE.size(); s++) {
					if (listeUE.get(s).getDecoupage() == DecoupageYearType.TRI3) {
						items.add(new ListItemUE(listeUE.get(s), s));	
					}
				}
			}
	
	        final TwoTextArrayAdapter adapter = new TwoTextArrayAdapter(this, items);
	
	        final ListView liste = (ListView) findViewById(R.id.listUEAdmin);
	        
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
					
					if (((ListItemUE) adapter.getItem(position)).getUE() == null) {
						// Do nothing.
					} else {
						Intent newIntent = new Intent(ListeUEA.this, ListeMatiereA.class);
						newIntent.putExtra("ue", listeUE.get(position - 1));
						
						startActivity(newIntent);
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
					
					if (((ListItemUE) adapter.getItem(arg2)).getUE() == null) {
						// Do nothing.
					} else {
						AlertDialog.Builder confirmQuitter = new AlertDialog.Builder(ListeUEA.this);
						confirmQuitter.setTitle("Suppression");
						confirmQuitter.setMessage("Voulez-vous vraiment supprimer cette UE ?");
						confirmQuitter.setCancelable(false);
						confirmQuitter.setPositiveButton("Oui", new AlertDialog.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {								
								ProgressDialog progress = new ProgressDialog(activity);
								progress.setMessage("Chargement...");
								new DeleteUE(progress, Constantes.URL_SERVER + "ue" +
										"?id=" + listeUE.get(arg2).getId() +
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
		}
	}
	
	/* Classes internes. */
	
	private class ListerUE extends AsyncTask<Void, Void, Void> {
		private ProgressDialog progress;
		private String pathUrl;

		public ListerUE(ProgressDialog progress, String pathUrl) {
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
				ListeUEA.this.runOnUiThread(new Runnable() {
					public void run() {
						AlertDialog.Builder builder = new AlertDialog.Builder(ListeUEA.this);
						builder.setMessage("Erreur - Vérifiez votre connexion");
						builder.setCancelable(false);
						builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								ListeUEA.this.finish();
							}
						});

						AlertDialog error = builder.create();
						error.show();
					}
				});
			} catch (ClientProtocolException e1) {
				ListeUEA.this.runOnUiThread(new Runnable() {
					public void run() {
						AlertDialog.Builder builder = new AlertDialog.Builder(ListeUEA.this);
						builder.setMessage("Erreur - Vérifiez votre connexion");
						builder.setCancelable(false);
						builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								ListeUEA.this.finish();
							}
						});

						AlertDialog error = builder.create();
						error.show();
					}
				});
			} catch (IOException e1) {
				ListeUEA.this.runOnUiThread(new Runnable() {
					public void run() {
						AlertDialog.Builder builder = new AlertDialog.Builder(ListeUEA.this);
						builder.setMessage("Erreur - Vérifiez votre connexion");
						builder.setCancelable(false);
						builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								ListeUEA.this.finish();
							}
						});

						AlertDialog error = builder.create();
						error.show();
					}
				});
			} 
            
            UE ue = null;
            
            if (jsonStr.length() > 0) {            	
                try {
                    JSONArray ues = new JSONArray(jsonStr);
 
                    for (int i = 0; i < ues.length(); i++) {
                    	JSONObject c = ues.getJSONObject(i);
                    	
                        ue = new UE();
                        ue.setId(c.getInt("idUE"));
                        ue.setNom(c.getString("nom"));
                        ue.setIdAnnee(c.getInt("idAnnee"));
                        ue.setDecoupage(DecoupageYearType.valueOf(c.getString("yearType")));
                        
                        listeUE.add(ue);
                    }
                    
                    ListeUEA.this.runOnUiThread(new Runnable() {
    					public void run() {    						
    						initListe();
    					}
    				});
                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
            } else {
            	ListeUEA.this.runOnUiThread(new Runnable() {
					public void run() {
						AlertDialog.Builder builder = new AlertDialog.Builder(ListeUEA.this);
						builder.setMessage("Erreur - Vérifiez votre connexion");
						builder.setCancelable(false);
						builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								ListeUEA.this.finish();
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

	private class DeleteUE extends AsyncTask<Void, Void, Void> {
		private ProgressDialog progress;
		private String pathUrl;

		public DeleteUE(ProgressDialog progress, String pathUrl) {
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

		        ListeUEA.this.runOnUiThread(new Runnable() {
					public void run() {    			
				        if (response.getStatusLine().getStatusCode() == 200) {
				        	toast.setText("UE supprimée.");
				        	toast.show();
				        	
				        	ListeUEA.this.runOnUiThread(new Runnable() {
		    					public void run() {    						
		    						actualiserListe();
		    					}
		    				});
				        } else if (response.getStatusLine().getStatusCode() == 401) {
							File filePseudo = new File("/sdcard/cacheJMD/pseudo.jmd");
							File fileToken = new File("/sdcard/cacheJMD/token.jmd");
							
							filePseudo.delete();
							fileToken.delete();
							
							finish();
				        	
							Intent i = new Intent(ListeUEA.this, Accueil.class);
							i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
				        	startActivity(i);	
				        	
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
		    	ListeUEA.this.runOnUiThread(new Runnable() {
					public void run() {
						AlertDialog.Builder builder = new AlertDialog.Builder(ListeUEA.this);
						builder.setMessage("Erreur - Vérifiez votre connexion");
						builder.setCancelable(false);
						builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								ListeUEA.this.finish();
							}
						});

						AlertDialog error = builder.create();
						error.show();
					}
				});
		    } catch (IOException e) {
		    	ListeUEA.this.runOnUiThread(new Runnable() {
					public void run() {
						AlertDialog.Builder builder = new AlertDialog.Builder(ListeUEA.this);
						builder.setMessage("Erreur - Vérifiez votre connexion");
						builder.setCancelable(false);
						builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								ListeUEA.this.finish();
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
	
	/**
	 * Méthode exécutée lorsque l'activité est relancée.<br />
	 * Ici, ça permet d'actualiser la liste des UE lorsqu'une UE vient d'être créé et que l'application ramène l'utilisateur sur cette vue de listing.
	 */
	@Override
	public void onRestart() {
		actualiserListe();
		
		super.onRestart();
	} 
}
