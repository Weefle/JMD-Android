package org.gl.jmd.view.admin.listing;

import java.io.IOException;
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
import org.gl.jmd.view.admin.create.CreationMatiere;
import org.json.*;

import android.app.*;
import android.content.*;
import android.content.res.Configuration;
import android.os.*;
import android.view.View;
import android.widget.*;

/**
 * Activité correspondant à la vue de listing des matières d'une UE.
 * 
 * @author Jordi CHARPENTIER & Yoann VANHOESERLANDE
 */
public class ListeMatiereA extends Activity {
	
	private Activity activity;
	
	private Toast toast;
	
	private UE ue = null;
	
	private ArrayList<Matiere> listeMatieres = new ArrayList<Matiere>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.administrateur_liste_matiere);
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
		
		ue = (UE) getIntent().getExtras().getSerializable("ue");
		
		activity = this;
		toast = Toast.makeText(getBaseContext(), "", Toast.LENGTH_SHORT);
		
		initTextView();
		actualiserListe();
	}
	
	private void initTextView() {
		TextView tvTitre = (TextView) findViewById(R.id.admin_liste_matiere_titre);
		
		if (ue.getNom().length() > 20) {
			tvTitre.setText(ue.getNom().substring(0, 20) + "...");
		} else {
			tvTitre.setText(ue.getNom());
		}
	}
	
	private void actualiserListe() {	
		listeMatieres.clear();
		
		ProgressDialog progress = new ProgressDialog(this);
		progress.setMessage("Chargement...");
		new ListerMatieres(progress, Constantes.URL_SERVER + "matiere/getAllMatieretOfUE" +
											"?idUE=" + ue.getId()).execute();
	}
	
	private void initListe() {
		final ListView liste = (ListView) findViewById(android.R.id.list);
		
		if (listeMatieres.size() > 0) {
			final ArrayList<HashMap<String, String>> listItem = new ArrayList<HashMap<String, String>>();
			HashMap<String, String> map;

			for(int s = 0; s < listeMatieres.size(); s++) {
				map = new HashMap<String, String>();

				map.put("id", "" + listeMatieres.get(s).getId());
				map.put("titre", ((listeMatieres.get(s).getNom().length() > 20) ? listeMatieres.get(s).getNom().substring(0, 20) + "..." : listeMatieres.get(s).getNom()));
				map.put("isOption", (listeMatieres.get(s).isOption() ? "Option" : "Obligatoire"));
				map.put("description", "Coefficient : " + listeMatieres.get(s).getCoefficient());

				listItem.add(map);		
			}

			liste.setAdapter(new SimpleAdapter (getBaseContext(), listItem, R.layout.administrateur_liste_matiere_list, new String[] {"titre", "description", "isOption"}, new int[] {R.id.titre, R.id.description, R.id.isOption})); 
			
			liste.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
				public boolean onItemLongClick(AdapterView<?> arg0, View arg1, final int arg2, final long arg3) {
						AlertDialog.Builder confirmQuitter = new AlertDialog.Builder(ListeMatiereA.this);
						confirmQuitter.setTitle("Suppression");
						confirmQuitter.setMessage("Voulez-vous vraiment supprimer cet élément ?");
						confirmQuitter.setCancelable(false);
						confirmQuitter.setPositiveButton("Oui", new AlertDialog.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {								
								ProgressDialog progress = new ProgressDialog(activity);
								progress.setMessage("Chargement...");
								new DeleteMatiere(progress, Constantes.URL_SERVER + "matiere" +
										"?id=" + listeMatieres.get(arg2).getId() +
										"&token=" + FileUtils.readFile(Constantes.FILE_TOKEN) + 
										"&pseudo=" + FileUtils.readFile(Constantes.FILE_PSEUDO) +
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
			map.put("titre", "Aucune matière.");
			
			ArrayList<HashMap<String, String>> listItem = new ArrayList<HashMap<String, String>>();
			listItem.add(map);

			liste.setAdapter(new SimpleAdapter (getBaseContext(), listItem, R.layout.simple_list, new String[] {"titre"}, new int[] {R.id.titre})); 
		}
	}
	
	public void back(View view) {
		finish();
	}
	
	public void creerMatiere(View view) {
		Intent newIntent = new Intent(ListeMatiereA.this, CreationMatiere.class);
		newIntent.putExtra("idUE", ue.getId());
	
		startActivity(newIntent);
	}
	
	/* Classes internes. */
	
	private class ListerMatieres extends AsyncTask<Void, Void, Void> {
		private ProgressDialog progress;
		private String pathUrl;

		public ListerMatieres(ProgressDialog progress, String pathUrl) {
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
				ListeMatiereA.this.runOnUiThread(new Runnable() {
					public void run() {
						AlertDialog.Builder builder = new AlertDialog.Builder(ListeMatiereA.this);
						builder.setMessage("Erreur - Vérifiez votre connexion");
						builder.setCancelable(false);
						builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								ListeMatiereA.this.finish();
							}
						});

						AlertDialog error = builder.create();
						error.show();
					}
				});
			} catch (ClientProtocolException e1) {
				ListeMatiereA.this.runOnUiThread(new Runnable() {
					public void run() {
						AlertDialog.Builder builder = new AlertDialog.Builder(ListeMatiereA.this);
						builder.setMessage("Erreur - Vérifiez votre connexion");
						builder.setCancelable(false);
						builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								ListeMatiereA.this.finish();
							}
						});

						AlertDialog error = builder.create();
						error.show();
					}
				});
			} catch (IOException e1) {
				ListeMatiereA.this.runOnUiThread(new Runnable() {
					public void run() {
						AlertDialog.Builder builder = new AlertDialog.Builder(ListeMatiereA.this);
						builder.setMessage("Erreur - Vérifiez votre connexion");
						builder.setCancelable(false);
						builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								ListeMatiereA.this.finish();
							}
						});

						AlertDialog error = builder.create();
						error.show();
					}
				});
			} 
            
            Matiere matiere = null;
            
            if (jsonStr.length() > 0) {            	
                try {
                    JSONArray matieres = new JSONArray(jsonStr);
 
                    for (int i = 0; i < matieres.length(); i++) {
                    	JSONObject c = matieres.getJSONObject(i);
                        
                        matiere = new Matiere();
                        matiere.setCoefficient(c.getLong("coefficient"));
                        matiere.setId(c.getInt("idMatiere"));
                        matiere.setIsOption(c.getBoolean("isOption"));
                        matiere.setNom(c.getString("nom"));
                        
                        listeMatieres.add(matiere);
                    }
                    
                    ListeMatiereA.this.runOnUiThread(new Runnable() {
    					public void run() {    						
    						initListe();
    					}
    				});
                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
            } else {
            	ListeMatiereA.this.runOnUiThread(new Runnable() {
					public void run() {
						AlertDialog.Builder builder = new AlertDialog.Builder(ListeMatiereA.this);
						builder.setMessage("Erreur - Vérifiez votre connexion");
						builder.setCancelable(false);
						builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								ListeMatiereA.this.finish();
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
	
	private class DeleteMatiere extends AsyncTask<Void, Void, Void> {
		private ProgressDialog progress;
		private String pathUrl;

		public DeleteMatiere(ProgressDialog progress, String pathUrl) {
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

		        ListeMatiereA.this.runOnUiThread(new Runnable() {
					public void run() {    			
				        if (response.getStatusLine().getStatusCode() == 200) {
				        	ListeMatiereA.this.runOnUiThread(new Runnable() {
		    					public void run() {    						
		    						actualiserListe();
		    					}
		    				});
				        	
				        	toast.setText("Matière supprimée.");
				        	toast.show();
				        } else if (response.getStatusLine().getStatusCode() == 401) {							
				        	Constantes.FILE_PSEUDO.delete();
				        	Constantes.FILE_TOKEN.delete();
				        	
							activity.finishAffinity();
				        	startActivity(new Intent(ListeMatiereA.this, Accueil.class));	
				        	
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
		    	ListeMatiereA.this.runOnUiThread(new Runnable() {
					public void run() {
						AlertDialog.Builder builder = new AlertDialog.Builder(ListeMatiereA.this);
						builder.setMessage("Erreur - Vérifiez votre connexion");
						builder.setCancelable(false);
						builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								ListeMatiereA.this.finish();
							}
						});

						AlertDialog error = builder.create();
						error.show();
					}
				});
		    } catch (IOException e) {
		    	ListeMatiereA.this.runOnUiThread(new Runnable() {
					public void run() {
						AlertDialog.Builder builder = new AlertDialog.Builder(ListeMatiereA.this);
						builder.setMessage("Erreur - Vérifiez votre connexion");
						builder.setCancelable(false);
						builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								ListeMatiereA.this.finish();
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
	 * Ici, ça permet d'actualiser la liste des matières lorsqu'une matières vient d'être créé et que l'application ramène l'utilisateur sur cette vue de listing.
	 */
	@Override
	public void onRestart() {
		actualiserListe();
		
		super.onRestart();
	} 
}
