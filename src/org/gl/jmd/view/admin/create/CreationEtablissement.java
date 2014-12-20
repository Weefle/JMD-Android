package org.gl.jmd.view.admin.create;

import java.io.*;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;
import org.gl.jmd.Constantes;
import org.gl.jmd.R;
import org.gl.jmd.model.Etablissement;
import org.gl.jmd.utils.*;
import org.gl.jmd.view.Accueil;

import android.app.*;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.*;
import android.view.View;
import android.widget.*;

/**
 * Activité correspondant à la vue de création d'un établissement.
 * 
 * @author Jordi CHARPENTIER & Yoann VANHOESERLANDE
 */
public class CreationEtablissement extends Activity {
	
	private Activity activity;

	private Toast toast;
	
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
	 * Méthode permettant de créer un établissement (déclenchée lors d'un click sur le bouton "créer").
	 * 
	 * @param view La vue lors du click sur le bouton de création.
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
			
			ProgressDialog progress = new ProgressDialog(activity);
			progress.setMessage("Chargement...");
			new CreerEtablissement(progress, Constantes.URL_SERVER + "etablissement" +
					"?nom=" + URLEncoder.encode(e.getNom()) +
					"&ville=" + URLEncoder.encode(e.getVille()) +
					"&token=" + FileUtils.readFile(Constantes.FILE_TOKEN) + 
					"&pseudo=" + FileUtils.readFile(Constantes.FILE_PSEUDO) +
					"&timestamp=" + new java.util.Date().getTime()).execute();	
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
	
	/* Classe interne. */
	
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
			HttpClient httpclient = new DefaultHttpClient();
		    HttpPut httppost = new HttpPut(pathUrl);

		    try {
		        HttpResponse response = httpclient.execute(httppost);
		        
		        if (response.getStatusLine().getStatusCode() == 200) {
		        	toast.setText("Etablissement créé.");
		        	toast.show();
		        	
		        	finish();
		        } else if (response.getStatusLine().getStatusCode() == 403) {
		        	toast.setText("Un établissement avec ce nom et cette ville existe déjà.");
		        	toast.show();
		        } else if (response.getStatusLine().getStatusCode() == 401) {
		        	Constantes.FILE_PSEUDO.delete();
		        	Constantes.FILE_TOKEN.delete();
		        	
					finishAffinity();
		        	startActivity(new Intent(CreationEtablissement.this, Accueil.class));	
		        	
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
		    	CreationEtablissement.this.runOnUiThread(new Runnable() {
					public void run() {
						AlertDialog.Builder builder = new AlertDialog.Builder(CreationEtablissement.this);
						builder.setMessage("Erreur - Vérifiez votre connexion");
						builder.setCancelable(false);
						builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								CreationEtablissement.this.finish();
							}
						});

						AlertDialog error = builder.create();
						error.show();
					}
				});
		    } catch (IOException e) {
		    	CreationEtablissement.this.runOnUiThread(new Runnable() {
					public void run() {
						AlertDialog.Builder builder = new AlertDialog.Builder(CreationEtablissement.this);
						builder.setMessage("Erreur - Vérifiez votre connexion");
						builder.setCancelable(false);
						builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								CreationEtablissement.this.finish();
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
	 * Méthode déclenchée lors d'un click sur le bouton virtuel Android de retour.
	 */
	@Override
	public void onBackPressed() {
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
