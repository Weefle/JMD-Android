package org.gl.jmd.view.admin.create;

import java.io.*;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.client.ClientProtocolException;
import org.gl.jmd.R;
import org.gl.jmd.model.Etablissement;
import org.gl.jmd.utils.*;

import android.app.*;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.*;
import android.view.View;
import android.widget.*;

/**
 * Activit� correspondant � la vue de cr�ation d'un �tablissement.
 * 
 * @author Jordi CHARPENTIER & Yoann VANHOESERLANDE
 */
public class CreationEtablissement extends Activity {
	
	private Activity activity;

	private Toast toast;
	
	private String contenuPage = "";
	
	private EditText NOM;
	
	private EditText VILLE;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.administrateur_creation_etablissement);
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
		
		activity = this;
		toast = Toast.makeText(activity, "", Toast.LENGTH_SHORT);
		
		NOM = (EditText) findViewById(R.id.et_crea_eta_nom);
		VILLE = (EditText) findViewById(R.id.et_crea_eta_ville);
	}
	
	/**
	 * M�thode permettant de cr�er un �tablissement (d�clench�e lors d'un click sur le bouton "cr�er").
	 * 
	 * @param view La vue lors du click sur le bouton de cr�ation.
	 */
	public void creerEtablissement(View view) {		
		if ((NOM.getText().toString().length() != 0) && (VILLE.getText().toString().length() != 0)) {
			Pattern pattern = Pattern.compile("^[a-zA-Z0-9\\s]*$");
			
			Matcher matcher1 = pattern.matcher(NOM.getText().toString());
			Matcher matcher2 = pattern.matcher(VILLE.getText().toString());
			
			if ((!matcher1.matches()) || (!matcher2.matches())) {
				String txtToast = "";
				
				boolean isNomValid = true;
				boolean isVilleValid = true;
				
				if (!matcher1.matches()) {
					NOM.setBackgroundResource(R.drawable.border_edittext_error);
					isNomValid = false;
				} else {
					NOM.setBackgroundResource(R.drawable.border_edittext);
				}

				if (!matcher2.matches()) {
					VILLE.setBackgroundResource(R.drawable.border_edittext_error);
					isVilleValid = false;
				} else {
					VILLE.setBackgroundResource(R.drawable.border_edittext);
				}

				if (!isNomValid && !isVilleValid) {
					txtToast = "Les champs \"Nom\" et \"Ville\" ne peuvent contenir que des chiffres et des lettres.";
				} else if(!isNomValid) {
					txtToast = "Le champ \"Nom\" ne peut contenir que des chiffres et des lettres.";
				} else if(!isVilleValid) {
					txtToast = "Le champ \"Ville\" ne peut contenir que des chiffres et des lettres.";
				}

				toast.setText(txtToast);
				toast.show();
				
				return;
			}
			
			Etablissement e = new Etablissement();
			e.setNom(NOM.getText().toString());
			e.setVille(VILLE.getText().toString());
			
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
						CreationEtablissement.this.finish();
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
							CreationEtablissement.this.finish();
							android.os.Process.killProcess(android.os.Process.myPid());
						}
					});
					
					errorDia.show();
				}
			}
			
			String URL = "http://www.jordi-charpentier.com/jmd/mobile/create.php?idAdmin=" + idAdmin + "&type=etablissement&nom=" + URLEncoder.encode(e.getNom()) + "&ville=" + URLEncoder.encode(e.getVille());
			
			ProgressDialog progress = new ProgressDialog(activity);
			progress.setMessage("Chargement...");
			new CreerEtablissement(progress, URL).execute();	
		} else {
			boolean isNomOK = true;
			boolean isVilleOK = true;
			
			String txtToast = "";
			
			if (NOM.getText().toString().length() == 0) {
				NOM.setBackgroundResource(R.drawable.border_edittext_error);
				isNomOK = false;
			} else {
				NOM.setBackgroundResource(R.drawable.border_edittext);
			}
			
			if (VILLE.getText().toString().length() == 0) {
				VILLE.setBackgroundResource(R.drawable.border_edittext_error);
				isVilleOK = false;
			} else {
				VILLE.setBackgroundResource(R.drawable.border_edittext);
			}
			
			if (!isNomOK && !isVilleOK) {
				txtToast = "Les deux champs sont vides.";
			} else if (!isVilleOK) {
				txtToast = "Le champ \"Ville\" est vide.";
			} else if (!isNomOK) {
				txtToast = "Le champ \"Nom\" est vide.";
			} 
			
			toast.setText(txtToast);
			toast.show();
		}
	}
	
	/**
	 * Classe interne repr�sentant une t�che asynchrone qui sera effectu�e en fond pendant un rond de chargement.
	 * 
	 * @author Jordi CHARPENTIER & Yoann VANHOESERLANDE
	 */
	private class CreerEtablissement extends AsyncTask<Void, Void, Void> {
		private ProgressDialog progress;
		private String pathUrl;

		public CreerEtablissement(ProgressDialog progress, String pathUrl) {
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
					CreationEtablissement.this.runOnUiThread(new Runnable() {
						public void run() {
							AlertDialog.Builder builder = new AlertDialog.Builder(CreationEtablissement.this);
							builder.setMessage("Erreur - V�rifiez votre connexion");
							builder.setCancelable(false);
							builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									CreationEtablissement.this.finish();
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
				toast.setText("Etablissement cr��.");
				toast.show();
				
				finish();
			} else if (contenuPage.equals("error")) {
				toast.setText("Erreur. Veuillez r�essayer.");
				toast.show();	
			} else if (contenuPage.equals("doublon")) {
				toast.setText("Un �tablissement avec ce nom et cette ville existe d�j�.");
				toast.show();
			} else {
				toast.setText("Erreur. Veuillez r�essayer.");
				toast.show();	
			}

			return null;
		}
	}
	
	/* M�thodes h�rit�es de la classe Activity. */
	
	/**
	 * M�thode permettant d'emp�cher la reconstruction de la vue lors de la rotation de l'�cran. 
	 * 
	 * @param newConfig L'�tat de la vue avant la rotation.
	 */
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}
	
	/**
	 * M�thode d�clench�e lors d'un click sur le bouton virtuel Android de retour.
	 */
	@Override
	public void onBackPressed() {
		final EditText NOM = (EditText) findViewById(R.id.et_crea_eta_nom);
		final EditText VILLE = (EditText) findViewById(R.id.et_crea_eta_ville);
		
		if ((NOM.getText().toString().length() != 0) || (VILLE.getText().toString().length() != 0)) {
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
