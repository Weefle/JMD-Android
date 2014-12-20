package org.gl.jmd.view.etudiant.create;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.*;

import org.apache.http.client.ClientProtocolException;
import org.gl.jmd.*;
import org.gl.jmd.model.*;
import org.gl.jmd.model.enumeration.*;
import org.gl.jmd.utils.WebUtils;

import org.json.*;

import android.os.*;
import android.view.View;
import android.widget.*;
import android.app.*;
import android.content.DialogInterface;
import android.content.res.Configuration;

/**
 * Activité correspondant à la vue de modification de l'accueil d'un étudiant.
 * 
 * @author Jordi CHARPENTIER & Yoann VANHOESERLANDE
 */
public class AjouterAnneeE extends Activity {

	private Activity activity;

	private Toast toast;

	private Etudiant etud = EtudiantDAO.load();
	
	private Etablissement selectedEta = null;

	private EditText ETA;
	
	private Diplome selectedDiplome = null;

	private EditText DIP;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.etudiant_ajouter_annee);
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
		
		DIP = (EditText) findViewById(R.id.etudiant_ajout_annee_nom_dip);
		ETA = (EditText) findViewById(R.id.etudiant_ajout_annee_nom_eta);
		
		activity = this;
		toast = Toast.makeText(activity, "", Toast.LENGTH_SHORT);
	}
	
	public void recherche() {
		if ((selectedDiplome != null) && (selectedEta != null)) {
			ProgressDialog progress = new ProgressDialog(activity);
			progress.setMessage("Chargement...");
			new Recherche(progress, Constantes.URL_SERVER + "annee/getAnnees" +
													"?idDiplome=" + selectedDiplome.getId() +
													"&idEtablissement=" + selectedEta.getId()).execute();	
		}
	}
	
	public void openListEta(View view) {
		ProgressDialog progress = new ProgressDialog(activity);
		progress.setMessage("Chargement...");
		new InitEtablissement(progress, Constantes.URL_SERVER + "etablissement/getAll").execute();	
	}
	
	public void openListDip(View view) {
		ProgressDialog progress = new ProgressDialog(activity);
		progress.setMessage("Chargement...");
		new InitDiplome(progress, Constantes.URL_SERVER + "diplome/getAll").execute();	
	}
	
	private TextView getHeader() {
		TextView headerListe = new TextView(activity);
		headerListe.setText("LISTE DES ANNEES");
		headerListe.setPadding(50, 0, 0, 20);
		headerListe.setTextSize(16);
		
		return headerListe;
	}
	
	private void initListe(final ArrayList<Annee> listeAnnees) {
		final ListView liste = (ListView) findViewById(android.R.id.list);

		liste.removeHeaderView(liste.getChildAt(0));
		liste.addHeaderView(getHeader());
		
		if (listeAnnees.size() > 0) {	
			
			final ArrayList<HashMap<String, String>> listItem = new ArrayList<HashMap<String, String>>();
			HashMap<String, String> map;

			for(int s = 0; s < listeAnnees.size(); s++) {
				map = new HashMap<String, String>();

				map.put("id", "" + listeAnnees.get(s).getId());
				map.put("titre", listeAnnees.get(s).getNom());

				listItem.add(map);		
			}

			final SimpleAdapter mSchedule = new SimpleAdapter (getBaseContext(), listItem, R.layout.simple_list, new String[] {"titre"}, new int[] {R.id.titre});

			liste.setAdapter(mSchedule); 

			liste.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				public void onItemClick(AdapterView<?> arg0, View arg1, final int position, long arg3) {
					if (position != 0) {
						ProgressDialog progress = new ProgressDialog(activity);
						progress.setMessage("Chargement...");
						new AjouterAnnee(progress, Constantes.URL_SERVER + "annee/getCompleteYear" +
								"?idAnnee=" + listItem.get(position - 1).get("id")).execute();
					}
				}
			});
		} else {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("titre", "Aucun résultat.");
			
			ArrayList<HashMap<String, String>> listItem = new ArrayList<HashMap<String, String>>();
			listItem.add(map);		

			liste.setAdapter(new SimpleAdapter (getBaseContext(), listItem, R.layout.simple_list, new String[] {"titre"}, new int[] {R.id.titre})); 
		}
	}

	/* Classes internes. */
	
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
			String jsonStr = "";
            
			try {
				jsonStr = WebUtils.call(pathUrl, WebUtils.GET);
			} catch (SocketTimeoutException e1) {
				AjouterAnneeE.this.runOnUiThread(new Runnable() {
					public void run() {
						AlertDialog.Builder builder = new AlertDialog.Builder(AjouterAnneeE.this);
						builder.setMessage("Erreur - Vérifiez votre connexion");
						builder.setCancelable(false);
						builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								AjouterAnneeE.this.finish();
							}
						});

						AlertDialog error = builder.create();
						error.show();
					}
				});
			} catch (ClientProtocolException e1) {
				AjouterAnneeE.this.runOnUiThread(new Runnable() {
					public void run() {
						AlertDialog.Builder builder = new AlertDialog.Builder(AjouterAnneeE.this);
						builder.setMessage("Erreur - Vérifiez votre connexion");
						builder.setCancelable(false);
						builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								AjouterAnneeE.this.finish();
							}
						});

						AlertDialog error = builder.create();
						error.show();
					}
				});
			} catch (IOException e1) {
				AjouterAnneeE.this.runOnUiThread(new Runnable() {
					public void run() {
						AlertDialog.Builder builder = new AlertDialog.Builder(AjouterAnneeE.this);
						builder.setMessage("Erreur - Vérifiez votre connexion");
						builder.setCancelable(false);
						builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								AjouterAnneeE.this.finish();
							}
						});

						AlertDialog error = builder.create();
						error.show();
					}
				});
			} 
			
			Annee a = new Annee();

			if (jsonStr.length() > 0) {            	
				try {
					JSONObject anneeJSON = new JSONObject(jsonStr);

					a.setId(anneeJSON.getInt("idAnnee"));
					a.setNom(anneeJSON.getString("nom"));
					a.setDecoupage(DecoupageType.valueOf(anneeJSON.getString("decoupage")));
					
					JSONObject etaJSON = anneeJSON.getJSONObject("etablissement");
					Etablissement e = new Etablissement();
					e.setId(etaJSON.getInt("idEtablissement"));
					e.setNom(etaJSON.getString("nom"));
					e.setVille(etaJSON.getString("ville"));
					
					a.setEtablissement(e);

					Diplome d = new Diplome();
					d.setId(anneeJSON.getInt("idDiplome"));
					d.setNom(anneeJSON.getString("nomDiplome"));

					a.setDiplome(d);
					
					// Liste des règles.
					JSONArray reglesJSON = anneeJSON.getJSONArray("regles");
					ArrayList<Regle> listeRegles = new ArrayList<Regle>();
					Regle r = null;
					
                    for (int i = 0; i < reglesJSON.length(); i++) {
                    	JSONObject regleJSON = reglesJSON.getJSONObject(i);
                        
                        r = new Regle();
                        r.setId(regleJSON.getInt("id"));
                        r.setIdAnnee(regleJSON.getInt("idAnnee"));
                        r.setIdMatiere(regleJSON.getInt("idMatiere"));
                        r.setIdUE(regleJSON.getInt("idUE"));
                        r.setOperateur(regleJSON.getInt("operateur"));
                        r.setRegle(regleJSON.getInt("regle"));
                        r.setValeur(regleJSON.getInt("valeur"));

                        listeRegles.add(r);
                    }
                    
                    a.setListeRegles(listeRegles);
					
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
                        
                        try {
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
                        } catch (JSONException ex) {
                        	ex.printStackTrace();
                        }
                        
                        listeUE.add(ue);
                    }
                    
                    a.setListeUE(listeUE);

					// Sauvegarde de l'année.
					
					int indexDiplome = 0;
					boolean exists = false;
					boolean diplomeExists = false;
					
					for (int i = 0; i < etud.getListeDiplomes().size(); i++) {
						if (etud.getListeDiplomes().get(i).getId() == selectedDiplome.getId()) {
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
					
					for (int i = 0; i < etud.getListeDiplomes().size(); i++) {
						if (etud.getListeDiplomes().get(i).getId() == a.getDiplome().getId()) {
							diplomeExists = true;
						}
					}
					
					if (!exists) {
						if (!diplomeExists) {
							etud.getListeDiplomes().add(selectedDiplome);
							etud.getListeDiplomes().get(etud.getListeDiplomes().size() - 1).getListeAnnees().add(a);
						} else {
							etud.getListeDiplomes().get(indexDiplome).getListeAnnees().add(a);
						}
						
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
	
	private class Recherche extends AsyncTask<Void, Void, Void> {
		private ProgressDialog progress;
		private String pathUrl;

		public Recherche(ProgressDialog progress, String pathUrl) {
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
				AjouterAnneeE.this.runOnUiThread(new Runnable() {
					public void run() {
						AlertDialog.Builder builder = new AlertDialog.Builder(AjouterAnneeE.this);
						builder.setMessage("Erreur - Vérifiez votre connexion");
						builder.setCancelable(false);
						builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								AjouterAnneeE.this.finish();
							}
						});

						AlertDialog error = builder.create();
						error.show();
					}
				});
			} catch (ClientProtocolException e1) {
				AjouterAnneeE.this.runOnUiThread(new Runnable() {
					public void run() {
						AlertDialog.Builder builder = new AlertDialog.Builder(AjouterAnneeE.this);
						builder.setMessage("Erreur - Vérifiez votre connexion");
						builder.setCancelable(false);
						builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								AjouterAnneeE.this.finish();
							}
						});

						AlertDialog error = builder.create();
						error.show();
					}
				});
			} catch (IOException e1) {
				AjouterAnneeE.this.runOnUiThread(new Runnable() {
					public void run() {
						AlertDialog.Builder builder = new AlertDialog.Builder(AjouterAnneeE.this);
						builder.setMessage("Erreur - Vérifiez votre connexion");
						builder.setCancelable(false);
						builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								AjouterAnneeE.this.finish();
							}
						});

						AlertDialog error = builder.create();
						error.show();
					}
				});
			} 

			final ArrayList<Annee> listeAnnees = new ArrayList<Annee>();
			Annee a = null;

			if (jsonStr.length() > 0) {            	
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
String jsonStr = "";
            
			try {
				jsonStr = WebUtils.call(pathUrl, WebUtils.GET);
			} catch (SocketTimeoutException e1) {
				AjouterAnneeE.this.runOnUiThread(new Runnable() {
					public void run() {
						AlertDialog.Builder builder = new AlertDialog.Builder(AjouterAnneeE.this);
						builder.setMessage("Erreur - Vérifiez votre connexion");
						builder.setCancelable(false);
						builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								AjouterAnneeE.this.finish();
							}
						});

						AlertDialog error = builder.create();
						error.show();
					}
				});
			} catch (ClientProtocolException e1) {
				AjouterAnneeE.this.runOnUiThread(new Runnable() {
					public void run() {
						AlertDialog.Builder builder = new AlertDialog.Builder(AjouterAnneeE.this);
						builder.setMessage("Erreur - Vérifiez votre connexion");
						builder.setCancelable(false);
						builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								AjouterAnneeE.this.finish();
							}
						});

						AlertDialog error = builder.create();
						error.show();
					}
				});
			} catch (IOException e1) {
				AjouterAnneeE.this.runOnUiThread(new Runnable() {
					public void run() {
						AlertDialog.Builder builder = new AlertDialog.Builder(AjouterAnneeE.this);
						builder.setMessage("Erreur - Vérifiez votre connexion");
						builder.setCancelable(false);
						builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								AjouterAnneeE.this.finish();
							}
						});

						AlertDialog error = builder.create();
						error.show();
					}
				});
			} 

			final ArrayList<Etablissement> listeEtablissements = new ArrayList<Etablissement>();
			final ArrayList<String> listeEtablissementsString = new ArrayList<String>();
			Etablissement e = null;

			if (jsonStr.length() > 0) {            	
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

								recherche();
								
								dialog.dismiss();
							}

						});

						dialog.setContentView(listView);
						dialog.show();
					} 
				}
			});

			return null;
		}
	}
	
	private class InitDiplome extends AsyncTask<Void, Void, Void> {
		private ProgressDialog progress;
		private String pathUrl;

		public InitDiplome(ProgressDialog progress, String pathUrl) {
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
				AjouterAnneeE.this.runOnUiThread(new Runnable() {
					public void run() {
						AlertDialog.Builder builder = new AlertDialog.Builder(AjouterAnneeE.this);
						builder.setMessage("Erreur - Vérifiez votre connexion");
						builder.setCancelable(false);
						builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								AjouterAnneeE.this.finish();
							}
						});

						AlertDialog error = builder.create();
						error.show();
					}
				});
			} catch (ClientProtocolException e1) {
				AjouterAnneeE.this.runOnUiThread(new Runnable() {
					public void run() {
						AlertDialog.Builder builder = new AlertDialog.Builder(AjouterAnneeE.this);
						builder.setMessage("Erreur - Vérifiez votre connexion");
						builder.setCancelable(false);
						builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								AjouterAnneeE.this.finish();
							}
						});

						AlertDialog error = builder.create();
						error.show();
					}
				});
			} catch (IOException e1) {
				AjouterAnneeE.this.runOnUiThread(new Runnable() {
					public void run() {
						AlertDialog.Builder builder = new AlertDialog.Builder(AjouterAnneeE.this);
						builder.setMessage("Erreur - Vérifiez votre connexion");
						builder.setCancelable(false);
						builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								AjouterAnneeE.this.finish();
							}
						});

						AlertDialog error = builder.create();
						error.show();
					}
				});
			} 

			final ArrayList<Diplome> listeDiplomes = new ArrayList<Diplome>();
			final ArrayList<String> listeDiplomesString = new ArrayList<String>();
			Diplome d = null;

			if (jsonStr.length() > 0) {            	
				try {
					JSONArray diplomes = new JSONArray(jsonStr);

					for (int i = 0; i < diplomes.length(); i++) {
						JSONObject c = diplomes.getJSONObject(i);

						d = new Diplome();
						d.setId(c.getInt("idDiplome"));
						d.setNom(c.getString("nom"));

						listeDiplomesString.add(d.getNom());
						listeDiplomes.add(d);
					}
				} catch (JSONException ex) {
					ex.printStackTrace();
				}
			} 

			AjouterAnneeE.this.runOnUiThread(new Runnable() {
				public void run() {
					if (listeDiplomes.size() > 0) {
						final Dialog dialog = new Dialog(activity);
						dialog.setTitle("Liste des établissements");
						dialog.setCancelable(true);

						ListView listView = new ListView(activity);
						listView.setAdapter(new ArrayAdapter<String>(activity, android.R.layout.simple_list_item_1, listeDiplomesString));

						listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
							public void onItemClick(AdapterView<?> arg0, View arg1, final int position, long arg3) {					    		
								DIP.setText(listeDiplomesString.get(position));
								selectedDiplome = listeDiplomes.get(position);

								recherche();
								
								dialog.dismiss();
							}

						});

						dialog.setContentView(listView);
						dialog.show();
					} 
				}
			});

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