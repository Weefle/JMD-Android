package org.gl.jmd.view.admin.create;

import java.io.*;
import java.util.*;

import org.apache.http.HttpResponse;
import org.apache.http.client.*;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;
import org.gl.jmd.*;
import org.gl.jmd.model.Regle;
import org.gl.jmd.model.enumeration.*;
import org.gl.jmd.utils.*;
import org.gl.jmd.view.Accueil;

import android.app.*;
import android.content.*;
import android.content.res.Configuration;
import android.os.*;
import android.view.View;
import android.widget.*;
import android.widget.AdapterView.OnItemSelectedListener;

/**
 * Activité correspondant à la vue de création d'un établissement.
 * 
 * @author Jordi CHARPENTIER & Yoann VANHOESERLANDE
 */
public class CreationRegle extends Activity {

	private Activity activity;

	private Toast toast;

	private String contenuPage = "";
	
	private Intent lastIntent;
	
	private String idAnnee;
	
	private String decoupage = "";

	private RegleType selectedTypeRegle = RegleType.NOTE_MINIMALE;

	private OperateurType selectedOperateur = OperateurType.EGAL;

	private EditText VALUE;
	
	private ArrayList<String> listeUE_ID = new ArrayList<String>();

	private ArrayList<String> listeUE_NOM = new ArrayList<String>();
	
	private ArrayList<String> listeMatiere_ID = new ArrayList<String>();
	
	private ArrayList<String> listeMatiere_NOM = new ArrayList<String>();
	
	private String selectedUE_ID = "";
	
	private String selectedMatiere_ID = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.administrateur_creation_regle);
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

		activity = this;
		toast = Toast.makeText(activity, "", Toast.LENGTH_SHORT);

		lastIntent = getIntent();
		
		idAnnee = lastIntent.getExtras().getString("idAnnee");
		decoupage = lastIntent.getExtras().getString("decoupage");
		
		VALUE = (EditText) findViewById(R.id.admin_creation_regle_value);

		initListe();
	}

	/**
	 * Méthode permettant de créer une règle (déclenchée lors d'un click sur le bouton "créer").
	 * 
	 * @param view La vue lors du click sur le bouton de création.
	 */
	public void creerRegle(View view) {		
		if (VALUE.getText().toString().length() != 0) {
			Regle r = new Regle();

			if (selectedTypeRegle == RegleType.NB_OPT_MINI) {
				r.setRegle(1);
			} else if (selectedTypeRegle == RegleType.NOTE_MINIMALE) {
				r.setRegle(2);
			}

			if (selectedOperateur == OperateurType.SUPERIEUR) {
				r.setOperateur(0);
			} else if (selectedOperateur == OperateurType.SUPERIEUR_EGAL) {
				r.setOperateur(1);
			} else if (selectedOperateur == OperateurType.EGAL) {
				r.setOperateur(2);
			} else if (selectedOperateur == OperateurType.INFERIEUR) {
				r.setOperateur(3);
			} else if (selectedOperateur == OperateurType.INFERIEUR_EGAL) {
				r.setOperateur(4);
			}

			r.setValeur(Integer.parseInt(VALUE.getText().toString()));
			
			String URL = Constantes.URL_SERVER + "regle" +
					"?regle=" + r.getRegle() +
					"&operateur=" + r.getOperateur() +
					"&valeur=" + r.getValeur() +
					"&idAnnee=" + idAnnee +
					"&idUE=" + selectedUE_ID +
					"&idMatiere=" + selectedMatiere_ID +
					"&token=" + FileUtils.lireFichier("/sdcard/cacheJMD/token.jmd") + 
					"&pseudo=" + FileUtils.lireFichier("/sdcard/cacheJMD/pseudo.jmd") +
					"&timestamp=" + new java.util.Date().getTime();	

			ProgressDialog progress = new ProgressDialog(activity);
			progress.setMessage("Chargement...");
			new CreerRegle(progress, URL).execute();	
		} else {
			toast.setText("Au moins un des champs est vide.");
			toast.show();
		}
	}

	private void initListe() {
		// Type
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.type_regle_array, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		Spinner spinner = (Spinner) findViewById(R.id.admin_creation_regle_spinner);
		spinner.setAdapter(adapter);
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
				if (parent.getItemAtPosition(pos).toString().equals("Note minimale")) {
					selectedTypeRegle = RegleType.NOTE_MINIMALE;
				} else if (parent.getItemAtPosition(pos).toString().equals("Nombre d\'options minimum")) {
					selectedTypeRegle = RegleType.NB_OPT_MINI;
				} 
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// Empty
			}
		});

		// OpŽerateur
		ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this, R.array.operateur_array, android.R.layout.simple_spinner_item);
		adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		Spinner spinner2 = (Spinner) findViewById(R.id.admin_creation_regle_op_spinner);
		spinner2.setAdapter(adapter2);
		spinner2.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
				if (parent.getItemAtPosition(pos).toString().equals("Supérieur")) {
					selectedOperateur = OperateurType.SUPERIEUR;
				} else if (parent.getItemAtPosition(pos).toString().equals("Supérieur ou égal")) {
					selectedOperateur = OperateurType.SUPERIEUR_EGAL;
				} else if (parent.getItemAtPosition(pos).toString().equals("Inférieur")) {
					selectedOperateur = OperateurType.INFERIEUR;
				} else if (parent.getItemAtPosition(pos).toString().equals("Inférieur ou égal")) {
					selectedOperateur = OperateurType.INFERIEUR_EGAL;
				} else if (parent.getItemAtPosition(pos).toString().equals("Egal")) {
					selectedOperateur = OperateurType.EGAL;
				} 
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// Empty
			}
		});
		
		// Liste des UE
		String URL1 = "http://www.jordi-charpentier.com/jmd/mobile/getInfos.php?type=ue&idAnnee=" + idAnnee + "&yearType=" + decoupage;
		
		ProgressDialog progress = new ProgressDialog(activity);
		progress.setMessage("Chargement...");
		new GetUE(progress, URL1).execute(); 
		
		// Liste des matières de l'année
		String URL2 = "http://www.jordi-charpentier.com/jmd/mobile/getInfos.php?type=matiere&idAnnee=" + idAnnee;
		
		ProgressDialog progress2 = new ProgressDialog(activity);
		progress2.setMessage("Chargement...");
		new GetMatieres(progress2, URL2).execute(); 
	}

	/**
	 * Classe interne représentant une tâche asynchrone qui sera effectuée en fond pendant un rond de chargement.
	 * 
	 * @author Jordi CHARPENTIER & Yoann VANHOESERLANDE
	 */
	private class CreerRegle extends AsyncTask<Void, Void, Void> {
		private ProgressDialog progress;
		private String pathUrl;

		public CreerRegle(ProgressDialog progress, String pathUrl) {
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
		    HttpPut httppost = new HttpPut(pathUrl);

		    try {
		        HttpResponse response = httpclient.execute(httppost);
		        
		        if (response.getStatusLine().getStatusCode() == 200) {
		        	toast.setText("Règle créée.");
		        	toast.show();
		        	
		        	finish();
		        } else if (response.getStatusLine().getStatusCode() == 401) {
					File filePseudo = new File("/sdcard/cacheJMD/pseudo.jmd");
					File fileToken = new File("/sdcard/cacheJMD/token.jmd");
					
					filePseudo.delete();
					fileToken.delete();
		        	
					finishAllActivities();
		        	startActivity(new Intent(CreationRegle.this, Accueil.class));	
		        	
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
		    	CreationRegle.this.runOnUiThread(new Runnable() {
					public void run() {
						AlertDialog.Builder builder = new AlertDialog.Builder(CreationRegle.this);
						builder.setMessage("Erreur - Vérifiez votre connexion");
						builder.setCancelable(false);
						builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								CreationRegle.this.finish();
							}
						});

						AlertDialog error = builder.create();
						error.show();
					}
				});
		    } catch (IOException e) {
		    	CreationRegle.this.runOnUiThread(new Runnable() {
					public void run() {
						AlertDialog.Builder builder = new AlertDialog.Builder(CreationRegle.this);
						builder.setMessage("Erreur - Vérifiez votre connexion");
						builder.setCancelable(false);
						builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								CreationRegle.this.finish();
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
	
	/**
	 * Classe interne représentant une tâche asynchrone qui sera effectuée en fond pendant un rond de chargement.
	 * 
	 * @author Jordi CHARPENTIER & Yoann VANHOESERLANDE
	 */
	private class GetMatieres extends AsyncTask<Void, Void, Void> {
		private ProgressDialog progress;
		private String pathUrl;

		public GetMatieres(ProgressDialog progress, String pathUrl) {
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
					CreationRegle.this.runOnUiThread(new Runnable() {
						public void run() {
							AlertDialog.Builder builder = new AlertDialog.Builder(CreationRegle.this);
							builder.setMessage("Erreur - Vérifiez votre connexion");
							builder.setCancelable(false);
							builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									CreationRegle.this.finish();
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

			initListeMatieres(contenuPage);
			
			return null;
		}
	}
	
	/**
	 * Classe interne représentant une tâche asynchrone qui sera effectuée en fond pendant un rond de chargement.
	 * 
	 * @author Jordi CHARPENTIER & Yoann VANHOESERLANDE
	 */
	private class GetUE extends AsyncTask<Void, Void, Void> {
		private ProgressDialog progress;
		private String pathUrl;

		public GetUE(ProgressDialog progress, String pathUrl) {
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
					CreationRegle.this.runOnUiThread(new Runnable() {
						public void run() {
							AlertDialog.Builder builder = new AlertDialog.Builder(CreationRegle.this);
							builder.setMessage("Erreur - Vérifiez votre connexion");
							builder.setCancelable(false);
							builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									CreationRegle.this.finish();
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

			initListeUE(contenuPage);	

			return null;
		}
	}
	
	public void initListeUE(String contenu_page) {
		StringTokenizer chaineEclate = new StringTokenizer(contenu_page, ";");
		
		final String[] temp = new String[chaineEclate.countTokens()];
		
		if (temp.length != 1) {
			int i = 0;

			while(chaineEclate.hasMoreTokens()) {
				temp[i++] = chaineEclate.nextToken();	
			}
			
			listeUE_ID.add("0");
			listeUE_NOM.add("Toutes");
			
			for(int s = 0; s < temp.length; s=s+3) {
				listeUE_ID.add(temp[s]);
				listeUE_NOM.add(temp[s+1]);	
			}
		} else {
			listeUE_ID.add("0");
			listeUE_NOM.add("Aucune UE");
		}
		
		CreationRegle.this.runOnUiThread(new Runnable() {
			public void run() {
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.drawable.spinner_item, listeUE_NOM);

				Spinner spinUE = (Spinner)findViewById(R.id.admin_creation_regle_liste_ue_spinner);
				spinUE.setAdapter(adapter);

				spinUE.setOnItemSelectedListener(new OnItemSelectedListener() {
					@Override
					public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
						if (parent.getItemAtPosition(pos).toString().equals("Toutes")) {
							selectedUE_ID = "1000000000";
						} else if (parent.getItemAtPosition(pos).toString().equals("Aucune UE")) {
							selectedUE_ID = "0";
						} else {
							selectedUE_ID = parent.getItemAtPosition(pos).toString();
						}
					}

					@Override
					public void onNothingSelected(AdapterView<?> parent) {
						// Empty
					}
				});
			}
		});
	}
	
	public void initListeMatieres(String contenu_page) {
		StringTokenizer chaineEclate = new StringTokenizer(contenu_page, ";");
		
		final String[] temp = new String[chaineEclate.countTokens()];
		
		if (temp.length != 1) {
			int i = 0;

			while(chaineEclate.hasMoreTokens()) {
				temp[i++] = chaineEclate.nextToken();	
			}
			
			listeMatiere_ID.add("0");
			listeMatiere_NOM.add("Toutes");
			
			for(int s = 0; s < temp.length; s=s+4) {
				listeMatiere_ID.add(temp[s]);
				listeMatiere_NOM.add(temp[s+1]);	
			}
		} else {
			listeMatiere_ID.add("0");
			listeMatiere_NOM.add("Aucune matière");
		}
		
		CreationRegle.this.runOnUiThread(new Runnable() {
			public void run() {
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.drawable.spinner_item, listeMatiere_NOM);

				Spinner spinMatiere = (Spinner)findViewById(R.id.admin_creation_regle_liste_matieres_spinner);
				spinMatiere.setAdapter(adapter);

				spinMatiere.setOnItemSelectedListener(new OnItemSelectedListener() {
					@Override
					public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
						if (parent.getItemAtPosition(pos).toString().equals("Toutes")) {
							selectedMatiere_ID = "1000000000";
						} else {
							for (int i = 0; i < listeMatiere_NOM.size(); i++) {
								if (parent.getItemAtPosition(pos).toString().equals(listeMatiere_NOM.get(i))) {
									selectedMatiere_ID = listeMatiere_ID.get(i);
								}
							}
							
						}
					}

					@Override
					public void onNothingSelected(AdapterView<?> parent) {
						// Empty
					}
				});
			}
		});
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
	 * Méthode déclenchée lors d'un click sur le bouton virtuel Android de retour.
	 */
	@Override
	public void onBackPressed() {
		if (VALUE.getText().toString().length() != 0) {
			AlertDialog.Builder confirmQuitter = new AlertDialog.Builder(this);
			confirmQuitter.setTitle("Annulation");
			confirmQuitter.setMessage("Voulez-vous vraiment annuler ?");
			confirmQuitter.setCancelable(false);
			confirmQuitter.setPositiveButton("Oui", new AlertDialog.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					finish();
				}
			});

			confirmQuitter.setNegativeButton("Non", null);
			confirmQuitter.show();
		} else {
			finish();
		}
	}
}
