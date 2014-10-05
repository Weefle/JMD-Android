package org.gl.jmd.view.admin.create;

import java.io.*;
import java.net.URLEncoder;
import java.util.*;
import java.util.regex.*;

import org.apache.http.client.ClientProtocolException;
import org.gl.jmd.R;
import org.gl.jmd.model.*;
import org.gl.jmd.model.enumeration.DecoupageType;
import org.gl.jmd.utils.*;

import android.app.*;
import android.content.*;
import android.content.res.Configuration;
import android.os.*;
import android.view.View;
import android.widget.AdapterView.*;
import android.widget.*;

/**
 * Activité correspondant à la vue de création d'une année d'un diplôme.
 * 
 * @author Jordi CHARPENTIER & Yoann VANHOESERLANDE
 */
public class CreationAnnee extends Activity {
	
	private Activity activity;

	private Toast toast;
	
	private String contenuPage = "";
	
	private int idDiplome = 0;
	
	private String[] listeEtablissements = null;
	
	private ArrayList<String> idList = new ArrayList<String>();
	
	private String selectedId = "";
	
	private CheckBox LAST_YEAR;
	
	private EditText NOM;
	
	private EditText ETA;
	
	private DecoupageType decoupage;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		
		setContentView(R.layout.administrateur_creation_annee);
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
		
		activity = this;
		toast = Toast.makeText(activity, "", Toast.LENGTH_SHORT);
		
		LAST_YEAR = (CheckBox) findViewById(R.id.admin_creation_annee_is_last_year);		
		NOM = (EditText) findViewById(R.id.admin_creation_annee_nom);		
		ETA = (EditText) findViewById(R.id.admin_creation_annee_nom_eta_auto_complete);
		
		idDiplome = getIntent().getExtras().getInt("idDiplome");
		
		initListeEtablissement();
		initListeDecoupage();
	}
	
	private void initListeEtablissement() {
		String url = "http://www.jordi-charpentier.com/jmd/mobile/getInfos.php?type=etablissement";
		
		ProgressDialog progress = new ProgressDialog(activity);
		progress.setMessage("Chargement...");
		new InitEtablissement(progress, url).execute();	
	}
	
	private void initListeDecoupage() {
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.decoupage_array, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		Spinner spinner = (Spinner) findViewById(R.id.admin_creation_annee_spinner);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
        	@Override
        	public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        		if (parent.getItemAtPosition(pos).toString().equals("Aucun")) {
        			decoupage = DecoupageType.NULL;
        		} else if (parent.getItemAtPosition(pos).toString().equals("Trimestre")) {
        			decoupage = DecoupageType.TRIMESTRE;
        		} else if (parent.getItemAtPosition(pos).toString().equals("Semestre")) {
        			decoupage = DecoupageType.SEMESTRE;
        		} 
        	}
        	
        	@Override
        	public void onNothingSelected(AdapterView<?> parent) {
        		// Empty
        	}
		});
	}
	
	/**
	 * Méthode permettant de créer une année (déclenchée lors d'un click sur le bouton "créer").
	 * 
	 * @param view La vue lors du click sur le bouton de création.
	 */
	public void creerAnnee(View view) {
		if ((NOM.getText().toString().length() != 0) && (ETA.getText().toString().length() != 0)) {
			Pattern pattern = Pattern.compile("^[a-zA-Z0-9\\s]*$");
			Matcher matcher = pattern.matcher(NOM.getText().toString());
			
			if (!matcher.matches()) {
				NOM.setBackgroundResource(R.drawable.border_edittext_error);
				
				toast.setText("Le nom ne peut contenir que des chiffres et des lettres.");
				toast.show();
				
				return;
			} else {
				NOM.setBackgroundResource(R.drawable.border_edittext);
			}
			
			boolean isInList = false;
			
			for (int i = 0; i < listeEtablissements.length; i++) {
				if (listeEtablissements[i].equals(ETA.getText().toString())) {
					isInList = true;
				}
			}
			
			if (!isInList) {
				ETA.setBackgroundResource(R.drawable.border_edittext_error);
				
				toast.setText("L'établissement entré n'est pas valide.");
				toast.show();
				
				return;
			} else {
				ETA.setBackgroundResource(R.drawable.border_edittext);
			}
			
			Annee a = new Annee();
			a.setNom(NOM.getText().toString());
			a.setDecoupage(decoupage);
			
			Etablissement etablissement = new Etablissement();
			
			try {
				etablissement.setId(Integer.parseInt(selectedId));
				a.setEtablissement(etablissement);
				
				ETA.setBackgroundResource(R.drawable.border_edittext);
			} catch (Exception e) {
				ETA.setBackgroundResource(R.drawable.border_edittext_error);
				
				toast.setText("L'établissement entré n'est pas valide.");
				toast.show();
				
				return;
			}
			
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
						CreationAnnee.this.finish();
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
							CreationAnnee.this.finish();
							android.os.Process.killProcess(android.os.Process.myPid());
						}
					});
					
					errorDia.show();
				}
			}
			
			String URL = "http://www.jordi-charpentier.com/jmd/mobile/create.php?idAdmin=" + idAdmin + "&type=annee&nom=" + URLEncoder.encode(a.getNom()) + "&idEtablissement=" + etablissement.getId() + "&idDiplome=" + idDiplome + "&decoupage=" + a.getDecoupage().name() + "&isLastYear=" + (LAST_YEAR.isChecked() ? "1" : "0");
			
			ProgressDialog progress = new ProgressDialog(activity);
			progress.setMessage("Chargement...");
			new CreerAnnee(progress, URL).execute(); 
		} else {
			boolean isNomOK = true;
			boolean isEtaOK = true;
			
			String txtToast = "";
			
			if (NOM.getText().toString().length() == 0) {
				NOM.setBackgroundResource(R.drawable.border_edittext_error);
				isNomOK = false;
			}
			
			if (ETA.getText().toString().length() == 0) {
				ETA.setBackgroundResource(R.drawable.border_edittext_error);
				isEtaOK = false;
			}
			
			if (!isNomOK && !isEtaOK) {
				txtToast = "Les deux champs sont vides.";
			} else if (!isEtaOK) {
				txtToast = "Le champ \"Etablissement\" est vide.";
			} else if (!isNomOK) {
				txtToast = "Le champ \"Nom\" est vide.";
			} 
			
			toast.setText(txtToast);
			toast.show();
		}
	}
	
	/**
	 * Classe interne représentant une tâche asynchrone qui sera effectuée en fond pendant un rond de chargement.
	 * 
	 * @author Jordi CHARPENTIER & Yoann VANHOESERLANDE
	 */
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
			try {
				if((contenuPage = WebUtils.getPage(pathUrl)) != "-1");

				else {
					CreationAnnee.this.runOnUiThread(new Runnable() {
						public void run() {
							AlertDialog.Builder builder = new AlertDialog.Builder(CreationAnnee.this);
							builder.setMessage("Erreur - Vérifiez votre connexion");
							builder.setCancelable(false);
							builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									CreationAnnee.this.finish();
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

			CreationAnnee.this.runOnUiThread(new Runnable() {
				public void run() {
					AutoCompleteTextView textView = (AutoCompleteTextView) findViewById(R.id.admin_creation_annee_nom_eta_auto_complete);
					
					StringTokenizer chaineEclate = new StringTokenizer(contenuPage, ";");
					final String[] temp = new String[chaineEclate.countTokens()];

					if (temp.length != 0) {
						int i = 0;

						while(chaineEclate.hasMoreTokens()) {
							temp[i++] = chaineEclate.nextToken();	
						}

						listeEtablissements = new String[temp.length / 3];
						int a = 0;

						for(int s = 0; s < temp.length; s = s + 3) {							
							listeEtablissements[a++] = temp[s+1] + " - " + temp[s+2];
							
							idList.add(temp[s]);
						}
						
						ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity, R.xml.suggestion_liste, listeEtablissements);
						textView.setAdapter(adapter);
						
						textView.setOnItemClickListener(new OnItemClickListener() {
							@Override
					        public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
								selectedId = idList.get(position);
					        }
					    });
						
						TextView textViewNoAdmin = (TextView) findViewById(R.id.admin_choix_annee_no_etablissement);
						textViewNoAdmin.setText("Il y a " + listeEtablissements.length + " établissement" + ((listeEtablissements.length > 1) ? "s" : "") + " en base.");
					} else {
						textView.setInputType(0);
						
						Button button = (Button) findViewById(R.id.crea_annee_bout_creer);
						button.setEnabled(false);
						
						TextView textViewNoAdmin = (TextView) findViewById(R.id.admin_choix_annee_no_etablissement);
						textViewNoAdmin.setText("Aucun établissement n'est enregistré en base.");
					}
				}
			});

			return null;
		}
	}
	
	/**
	 * Classe interne représentant une tâche asynchrone qui sera effectuée en fond pendant un rond de chargement.
	 * 
	 * @author Jordi CHARPENTIER & Yoann VANHOESERLANDE
	 */
	private class CreerAnnee extends AsyncTask<Void, Void, Void> {
		private ProgressDialog progress;
		private String pathUrl;

		public CreerAnnee(ProgressDialog progress, String pathUrl) {
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
					CreationAnnee.this.runOnUiThread(new Runnable() {
						public void run() {
							AlertDialog.Builder builder = new AlertDialog.Builder(CreationAnnee.this);
							builder.setMessage("Erreur - Vérifiez votre connexion");
							builder.setCancelable(false);
							builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									CreationAnnee.this.finish();
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

			if(contenuPage.equals("ok")) {
				toast.setText("Année créée.");
				toast.show();
				
				finish();
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
	 * Méthode déclenchée lors d'un click sur le bouton virtuel Android de retour.
	 */
	@Override
	public void onBackPressed() {
		final EditText NOM = (EditText) findViewById(R.id.admin_creation_annee_nom);
		
		if (NOM.getText().toString().length() != 0) {
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
