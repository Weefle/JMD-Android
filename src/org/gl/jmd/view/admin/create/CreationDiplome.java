package org.gl.jmd.view.admin.create;

import java.io.*;
import java.net.URLEncoder;
import java.util.regex.*;

import org.apache.http.HttpResponse;
import org.apache.http.client.*;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;
import org.gl.jmd.*;
import org.gl.jmd.utils.*;
import org.gl.jmd.view.Accueil;

import android.app.*;
import android.content.*;
import android.content.res.Configuration;
import android.os.*;
import android.view.View;
import android.widget.*;

/**
 * Activité correspondant à la vue de création d'un diplôme.
 * 
 * @author Jordi CHARPENTIER & Yoann VANHOESERLANDE
 */
public class CreationDiplome extends Activity {
	
	private Activity activity;

	private Toast toast;
	
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
	
	public void back(View view) {
		testBack();
	}
	
	private void testBack() {
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
	
	/**
	 * Méthode permettant de créer un diplôme (déclenchée lors d'un click sur le bouton "créer").
	 * 
	 * @param view La vue lors du click sur le bouton de création.
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
			
			try {
				ProgressDialog progress = new ProgressDialog(activity);
				progress.setMessage("Chargement...");
				new CreerDiplome(progress, Constantes.URL_SERVER + "diplome" +
							"?nom=" + URLEncoder.encode(NOM.getText().toString(), "UTF-8") +
							"&token=" + FileUtils.readFile(Constantes.FILE_TOKEN) + 
							"&pseudo=" + FileUtils.readFile(Constantes.FILE_PSEUDO) +
							"&timestamp=" + new java.util.Date().getTime()).execute();
			} catch (UnsupportedEncodingException e) {
				toast.setText("Erreur d'encodage (UTF-8).");
				toast.show();
			}	
		} else {
			NOM.setBackgroundResource(R.drawable.border_edittext_error);
			
			toast.setText("Le champ \"Nom\" est vide.");
			toast.show();
		}
	}
	
	/* Classe interne. */

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
			HttpClient httpclient = new DefaultHttpClient();
		    HttpPut httpPut = new HttpPut(pathUrl);

		    try {
		        HttpResponse response = httpclient.execute(httpPut);
		        
		        if (response.getStatusLine().getStatusCode() == 200) {
		        	toast.setText("Diplôme créé.");
		        	toast.show();
		        	
		        	finish();
		        } else if (response.getStatusLine().getStatusCode() == 403) {
		        	toast.setText("Un diplôme avec ce nom existe déjà.");
		        	toast.show();
		        } else if (response.getStatusLine().getStatusCode() == 401) {
		        	Constantes.FILE_PSEUDO.delete();
		        	Constantes.FILE_TOKEN.delete();
		        	
					finishAffinity();
		        	startActivity(new Intent(CreationDiplome.this, Accueil.class));	
		        	
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
		    	CreationDiplome.this.runOnUiThread(new Runnable() {
					public void run() {
						AlertDialog.Builder builder = new AlertDialog.Builder(CreationDiplome.this);
						builder.setMessage("Erreur - Vérifiez votre connexion");
						builder.setCancelable(false);
						builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								CreationDiplome.this.finish();
							}
						});

						AlertDialog error = builder.create();
						error.show();
					}
				});
		    } catch (IOException e) {
		    	CreationDiplome.this.runOnUiThread(new Runnable() {
					public void run() {
						AlertDialog.Builder builder = new AlertDialog.Builder(CreationDiplome.this);
						builder.setMessage("Erreur - Vérifiez votre connexion");
						builder.setCancelable(false);
						builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								CreationDiplome.this.finish();
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
		testBack();
	}
}
