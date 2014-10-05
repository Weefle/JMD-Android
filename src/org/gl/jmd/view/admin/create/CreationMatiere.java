package org.gl.jmd.view.admin.create;

import java.io.*;
import java.net.URLEncoder;
import java.util.regex.*;

import org.apache.http.client.ClientProtocolException;
import org.gl.jmd.R;
import org.gl.jmd.model.Matiere;
import org.gl.jmd.utils.*;

import android.app.*;
import android.content.*;
import android.content.res.Configuration;
import android.os.*;
import android.view.View;
import android.widget.*;

/**
 * Activité correspondant à la vue de création d'une matière.
 * 
 * @author Jordi CHARPENTIER & Yoann VANHOESERLANDE
 */
public class CreationMatiere extends Activity {
	
	private Activity activity;

	private Toast toast;
	
	private String contenuPage = "";
	
	private int idUE = 0;
	
	private EditText NOM;
	
	private EditText COEFF;
	
	private CheckBox IS_OPTION;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.administrateur_creation_matiere);
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
		
		activity = this;
		toast = Toast.makeText(activity, "", Toast.LENGTH_SHORT);
		
		NOM = (EditText) findViewById(R.id.admin_creation_matiere_nom);
		COEFF = (EditText) findViewById(R.id.admin_creation_matiere_coefficient);
		IS_OPTION = (CheckBox) findViewById(R.id.admin_creation_matiere_checkbox_option);
		
		idUE = getIntent().getExtras().getInt("idUE");
	}
	
	/**
	 * Méthode permettant de créer une matière (déclenchée lors d'un click sur le bouton "créer").
	 * 
	 * @param view La vue lors du click sur le bouton de création.
	 */
	public void creerMatiere(View view) {
		if ((NOM.getText().toString().length() != 0) && (COEFF.getText().toString().length() != 0)) {
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
			
			Pattern pattern2 = Pattern.compile("^[0-9]*$");
			Matcher matcher2 = pattern2.matcher(COEFF.getText().toString());
			
			if (!matcher2.matches()) {
				COEFF.setBackgroundResource(R.drawable.border_edittext_error);
				
				toast.setText("Le coefficient doit être un nombre.");
				toast.show();
				
				return;
			} else {
				COEFF.setBackgroundResource(R.drawable.border_edittext);
			}
			
			Matiere m = new Matiere();
			m.setNom(NOM.getText().toString());
			m.setCoefficient(Integer.parseInt(COEFF.getText().toString()));
			m.setIsOption(IS_OPTION.isChecked());
			
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
						CreationMatiere.this.finish();
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
							CreationMatiere.this.finish();
							android.os.Process.killProcess(android.os.Process.myPid());
						}
					});
					
					errorDia.show();
				}
			}
			
			String URL = "http://www.jordi-charpentier.com/jmd/mobile/create.php?idAdmin=" + idAdmin + "&type=matiere&nom=" + URLEncoder.encode(m.getNom()) + "&coefficient=" + m.getCoefficient() + "&isOption=" + (m.isOption() ? "1" : "0") + "&idUE=" + idUE;
			
			ProgressDialog progress = new ProgressDialog(activity);
			progress.setMessage("Chargement...");
			new CreerMatiere(progress, URL).execute();	
		} else {
			boolean isCoeffOK = true;
			boolean isNomOK = true;
			
			String txtToast = "";
			
			if (COEFF.getText().toString().length() == 0) {
				COEFF.setBackgroundResource(R.drawable.border_edittext_error);
				isCoeffOK = false;
			} else {
				COEFF.setBackgroundResource(R.drawable.border_edittext);
			}
			
			if (NOM.getText().toString().length() == 0) {
				NOM.setBackgroundResource(R.drawable.border_edittext_error);
				isNomOK = false;
			} else {
				NOM.setBackgroundResource(R.drawable.border_edittext);
			}
			
			if (!isNomOK && !isCoeffOK) {
				txtToast = "Les deux champs sont vides.";
			} else if (!isCoeffOK) {
				txtToast = "Le champ \"Coefficient\" est vide.";
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
	private class CreerMatiere extends AsyncTask<Void, Void, Void> {
		private ProgressDialog progress;
		private String pathUrl;

		public CreerMatiere(ProgressDialog progress, String pathUrl) {
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
					CreationMatiere.this.runOnUiThread(new Runnable() {
						public void run() {
							AlertDialog.Builder builder = new AlertDialog.Builder(CreationMatiere.this);
							builder.setMessage("Erreur - Vérifiez votre connexion");
							builder.setCancelable(false);
							builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									CreationMatiere.this.finish();
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
				toast.setText("Matière créée.");
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
		final EditText NOM = (EditText) findViewById(R.id.admin_creation_matiere_nom);
		final EditText COEFF = (EditText) findViewById(R.id.admin_creation_matiere_coefficient);
		
		if ((NOM.getText().toString().length() != 0) && (COEFF.getText().toString().length() != 0)) {
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
