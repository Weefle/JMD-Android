package org.gl.jmd.view.admin.create;

import java.io.*;
import java.net.URLEncoder;
import java.util.regex.*;

import org.apache.http.client.ClientProtocolException;
import org.gl.jmd.R;
import org.gl.jmd.model.Diplome;
import org.gl.jmd.utils.*;

import android.app.*;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.*;
import android.view.View;
import android.widget.*;

/**
 * Activit� correspondant � la vue de cr�ation d'une ann�e d'un dipl�me.
 * 
 * @author Jordi CHARPENTIER & Yoann VANHOESERLANDE
 */
public class CreationDiplome extends Activity {
	
	private Activity activity;

	private Toast toast;
	
	private String contenuPage = "";
	
	private EditText NOM;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.administrateur_creation_diplome);
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
		
		activity = this;
		toast = Toast.makeText(activity, "", Toast.LENGTH_SHORT);
		
		NOM = (EditText) findViewById(R.id.admin_creation_diplome_nom);
	}
	
	/**
	 * M�thode permettant de cr�er un dipl�me (d�clench�e lors d'un click sur le bouton "cr�er").
	 * 
	 * @param view La vue lors du click sur le bouton de cr�ation.
	 */
	public void creerDiplome(View view) {		
		if (NOM.getText().toString().length() != 0) {
			Pattern pattern = Pattern.compile("^[a-zA-Z\\s]+$");
			Matcher matcher = pattern.matcher(NOM.getText().toString());
			
			if (!matcher.matches()) {
				NOM.setBackgroundResource(R.drawable.border_edittext_error);
				
				toast.setText("Le champ \"Nom\" ne peut contenir que des lettres.");
				toast.show();
				
				return;
			}
			
			Diplome d = new Diplome();
			d.setNom(NOM.getText().toString());
			
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
						CreationDiplome.this.finish();
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
							CreationDiplome.this.finish();
							android.os.Process.killProcess(android.os.Process.myPid());
						}
					});
					
					errorDia.show();
				}
			}
			
			String URL = "http://www.jordi-charpentier.com/jmd/mobile/create.php?idAdmin=" + idAdmin + "&type=diplome&nom=" + URLEncoder.encode(d.getNom());			
			
			ProgressDialog progress = new ProgressDialog(activity);
			progress.setMessage("Chargement...");
			new CreerDiplome(progress, URL).execute();	
		} else {
			NOM.setBackgroundResource(R.drawable.border_edittext_error);
			
			toast.setText("Le champ \"Nom\" est vide.");
			toast.show();
		}
	}
	
	/**
	 * Classe interne repr�sentant une t�che asynchrone qui sera effectu�e en fond pendant un rond de chargement.
	 * 
	 * @author Jordi CHARPENTIER & Yoann VANHOESERLANDE
	 */
	private class CreerDiplome extends AsyncTask<Void, Void, Void> {
		private ProgressDialog progress;
		private String pathUrl;

		public CreerDiplome(ProgressDialog progress, String pathUrl) {
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
					CreationDiplome.this.runOnUiThread(new Runnable() {
						public void run() {
							AlertDialog.Builder builder = new AlertDialog.Builder(CreationDiplome.this);
							builder.setMessage("Erreur - V�rifiez votre connexion");
							builder.setCancelable(false);
							builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									CreationDiplome.this.finish();
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
				toast.setText("Dipl�me cr��.");
				toast.show();
				
				finish();
			} else if (contenuPage.equals("error")) {
				toast.setText("Erreur. Veuillez r�essayer.");
				toast.show();	
			} else if (contenuPage.equals("error")) {
				toast.setText("Un dipl�me avec ce nom existe d�j�.");
				toast.show();	
			}else {
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
		final EditText NOM = (EditText) findViewById(R.id.admin_creation_diplome_nom);
		
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
