package org.gl.jmd.view.admin.listing;

import java.io.*;
import java.util.*;

import org.apache.http.client.ClientProtocolException;
import org.gl.jmd.Constantes;
import org.gl.jmd.R;
import org.gl.jmd.ServiceHandler;
import org.gl.jmd.model.Annee;
import org.gl.jmd.model.Etablissement;
import org.gl.jmd.model.enumeration.DecoupageType;
import org.gl.jmd.utils.*;
import org.gl.jmd.view.admin.create.CreationAnnee;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
public class ListeAnneeA extends Activity {
	
	private Activity activity;
	
	private String idDiplome = "";
	
	private String nomDiplome = "";
	
	private Toast toast;
	
	private String contenuPage = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.administrateur_liste_annee);
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
		
		idDiplome = getIntent().getExtras().getString("idDiplome");
		nomDiplome = getIntent().getExtras().getString("nomDiplome");
		
		activity = this;
		toast = Toast.makeText(getBaseContext(), "", Toast.LENGTH_SHORT);
		
		actualiserListe();
		
		// On donne comme titre à la vue le nom du diplôme choisi.
		TextView tvTitre = (TextView) findViewById(R.id.admin_liste_annee_titre);
		tvTitre.setText(nomDiplome);
	}
	
	public void actualiserListe() {		
		ProgressDialog progress = new ProgressDialog(this);
		progress.setMessage("Chargement...");
		new ListerAnnees(progress, Constantes.URL_SERVER + "annee/getAnneesByDiplome?idDiplome=" + idDiplome).execute();	
	}
	
	public void initListe(final ArrayList<Annee> listeAnnees) {
		final ListView liste = (ListView) findViewById(android.R.id.list);
		
		if (listeAnnees.size() > 0) {
			final ArrayList<HashMap<String, String>> listItem = new ArrayList<HashMap<String, String>>();
			HashMap<String, String> map;

			for(int s = 0; s < listeAnnees.size(); s++) {
				map = new HashMap<String, String>();

				map.put("id", "" + listeAnnees.get(s).getId());
				map.put("titre", listeAnnees.get(s).getNom());
				map.put("decoupage", listeAnnees.get(s).getDecoupage().name());
				map.put("description", listeAnnees.get(s).getEtablissement().getNom() + " - " + listeAnnees.get(s).getEtablissement().getVille());
				map.put("isLastYear", "" + listeAnnees.get(s).isLast());

				listItem.add(map);		
			}
			
			final SimpleAdapter mSchedule = new SimpleAdapter (getBaseContext(), listItem, R.layout.administrateur_liste_annee_list, new String[] {"titre", "description"}, new int[] {R.id.titre, R.id.description});

			liste.setAdapter(mSchedule); 
			
			liste.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				public void onItemClick(AdapterView<?> arg0, View arg1, final int position, long arg3) {
					Intent newIntent = null;
					
					if (listItem.get(position).get("decoupage").equals(DecoupageType.NULL.name())) {
						newIntent = new Intent(ListeAnneeA.this, ListeUERegleA.class);
						newIntent.putExtra("decoupage", "NULL");
					} else if (listItem.get(position).get("decoupage").equals(DecoupageType.SEMESTRE.name())) {
						newIntent = new Intent(ListeAnneeA.this, ListeSemestreA.class);
					} else if (listItem.get(position).get("decoupage").equals(DecoupageType.TRIMESTRE.name())) {
						newIntent = new Intent(ListeAnneeA.this, ListeTrimestreA.class);
					} 
					
					if (newIntent == null) {
						toast.setText("Une erreur est survenue. Veuillez réessayer.");
						toast.show();
					} else {
						newIntent.putExtra("idAnnee", listItem.get(position).get("id"));
						
						startActivity(newIntent);
					}
				}
			});
			
			liste.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
				public boolean onItemLongClick(AdapterView<?> arg0, View arg1, final int arg2, final long arg3) {
					if (listItem.get(arg2).get("img") == null) {
						AlertDialog.Builder confirmQuitter = new AlertDialog.Builder(ListeAnneeA.this);
						confirmQuitter.setTitle("Suppression");
						confirmQuitter.setMessage("Voulez-vous vraiment supprimer cet élément ?");
						confirmQuitter.setCancelable(false);
						confirmQuitter.setPositiveButton("Oui", new AlertDialog.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								File repCache = new File(Environment.getExternalStorageDirectory().getPath() + "/cacheJMD/");
								File fileLogin = new File(repCache.getPath() + "/logins.jmd");
								
								String idAdmin = FileUtils.lireFichier(fileLogin);
								
								if (idAdmin.length() == 0) {
									fileLogin.delete();
									
									AlertDialog.Builder errorDia = new AlertDialog.Builder(activity);
									errorDia.setTitle("Erreur");
									errorDia.setMessage("Erreur - Veuillez relancer l'application.");
									errorDia.setCancelable(false);
									errorDia.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
										public void onClick(DialogInterface dialog, int id) {
											ListeAnneeA.this.finish();
											android.os.Process.killProcess(android.os.Process.myPid());
										}
									});
									
									errorDia.show();
								} else {
									if (idAdmin.matches("[+-]?\\d*(\\.\\d+)?") == false){
										fileLogin.delete();
										
										AlertDialog.Builder errorDia = new AlertDialog.Builder(activity);
										errorDia.setTitle("Erreur");
										errorDia.setMessage("Erreur - Veuillez relancer l'application.");
										errorDia.setCancelable(false);
										errorDia.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
											public void onClick(DialogInterface dialog, int id) {
												ListeAnneeA.this.finish();
												android.os.Process.killProcess(android.os.Process.myPid());
											}
										});
										
										errorDia.show();
									}
								}
								
								String URL = "http://www.jordi-charpentier.com/jmd/mobile/delete.php?idAdmin=" + idAdmin + "&type=annee&idAnnee=" + listItem.get(arg2).get("id");	

								ProgressDialog progress = new ProgressDialog(activity);
								progress.setMessage("Chargement...");
								new DeleteAnnee(progress, URL).execute();
								
								actualiserListe();
								
								liste.setSelection(arg2);
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
			map.put("titre", "Aucune année.");
			
			listItem.add(map);
			
			SimpleAdapter mSchedule = new SimpleAdapter (getBaseContext(), listItem, R.layout.administrateur_liste_annee_empty_list, new String[] {"titre"}, new int[] {R.id.titre});

			liste.setAdapter(mSchedule); 
		}
	}
	
	public void creerAnnee(View view) {
		Intent newIntent = new Intent(ListeAnneeA.this, CreationAnnee.class);
		newIntent.putExtra("idDiplome", Integer.parseInt(idDiplome));
	
		startActivity(newIntent);
	}
	
	/**
	 * Classe interne représentant une tâche asynchrone (lister les années d'un diplôme présents en base) qui sera effectuée en fond pendant un rond de chargement.
	 * 
	 * @author Jordi CHARPENTIER & Yoann VANHOESERLANDE
	 */
	private class ListerAnnees extends AsyncTask<Void, Void, Void> {
		private ProgressDialog progress;
		private String pathUrl;

		public ListerAnnees(ProgressDialog progress, String pathUrl) {
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
                        a.setDecoupage(DecoupageType.valueOf(c.getString("decoupage")));
                        
                        JSONObject etaFromAnnee = c.getJSONObject("etablissement");
                        Etablissement e = new Etablissement();
                        e.setId(etaFromAnnee.getInt("idEtablissement"));
                        e.setNom(etaFromAnnee.getString("nom"));
                        e.setVille(etaFromAnnee.getString("ville"));
                        
                        a.setEtablissement(e);
                        
                        listeAnnees.add(a);
                    }
                    
                    ListeAnneeA.this.runOnUiThread(new Runnable() {
    					public void run() {    						
    						initListe(listeAnnees);

    						return;
    					}
    				});
                } catch (JSONException ex) {
                    ex.printStackTrace();
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
	private class DeleteAnnee extends AsyncTask<Void, Void, Void> {
		private ProgressDialog progress;
		private String pathUrl;

		public DeleteAnnee(ProgressDialog progress, String pathUrl) {
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
					ListeAnneeA.this.runOnUiThread(new Runnable() {
						public void run() {
							AlertDialog.Builder builder = new AlertDialog.Builder(ListeAnneeA.this);
							builder.setMessage("Erreur - Vérifiez votre connexion");
							builder.setCancelable(false);
							builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									ListeAnneeA.this.finish();
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
			
			contenuPage = contenuPage.replaceAll(" ", "");
			
			if (contenuPage.equals("ok")) {
				toast.setText("Année supprimée.");
				toast.show();	
			} else if (contenuPage.equals("error")) {
				toast.setText("Erreur. Veuillez réessayer.");
				toast.show();	
			} else {
				toast.setText("Erreur. Veuillez réessayer.");
				toast.show();	
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
