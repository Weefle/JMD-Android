package org.gl.jmd.view.admin;

import java.io.*;
import java.util.regex.*;

import org.apache.http.HttpResponse;
import org.apache.http.client.*;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.gl.jmd.Constantes;
import org.gl.jmd.R;
import org.gl.jmd.utils.*;

import android.app.*;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.*;
import android.view.View;
import android.widget.*;

/**
 * Activité correspondant à la vue d'inscription à l'application.
 * 
 * @author Jordi CHARPENTIER & Yoann VANHOESERLANDE
 */
public class InscriptionA extends Activity {

	private Activity activity;

	private Toast toast;

	private Pattern pattern;

	private Matcher matcher;
	
	private EditText NOM;
	
	private EditText PRENOM;
	
	private EditText PSEUDO;
	
	private EditText PASSWORD;
	
	private EditText PASSWORD_AGAIN;
	
	private EditText EMAIL;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.administrateur_inscription);
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
		
		activity = this;
		toast = Toast.makeText(activity, "", Toast.LENGTH_SHORT);
		
		NOM = (EditText) findViewById(R.id.admin_inscription_ta_nom);
		PRENOM = (EditText) findViewById(R.id.admin_inscription_ta_prenom);
		PSEUDO = (EditText) findViewById(R.id.admin_inscription_ta_pseudo);
		PASSWORD = (EditText) findViewById(R.id.admin_inscription_ta_password);
		PASSWORD_AGAIN = (EditText) findViewById(R.id.admin_inscription_ta_password_again);
		EMAIL = (EditText) findViewById(R.id.admin_inscription_ta_email);
	}
	
	public void back(View view) {
		finish();
	}

	/**
	 * Méthode permettant de tenter d'inscrire l'utilisateur.
	 * 
	 * @param view La vue lors du click sur le bouton d'inscription.
	 */
	public void inscription(View view) {
		if ((PSEUDO.getText().toString().length() != 0) && (PASSWORD.getText().toString().length() != 0) && 
				(PASSWORD_AGAIN.getText().toString().length() != 0) && (EMAIL.getText().toString().length() != 0) &&
				(NOM.getText().toString().length() != 0) && (PRENOM.getText().toString().length() != 0)) {

			if (PASSWORD.getText().toString().equals(PASSWORD_AGAIN.getText().toString())) {
				if (validate(EMAIL.getText().toString())) {					
					ProgressDialog progress = new ProgressDialog(activity);
					progress.setMessage("Chargement...");
					new InscriptionAdmin(progress, Constantes.URL_SERVER + 
								 "admin/subscription" + 
								 "?nom=" + NOM.getText().toString() + 
								 "&prenom=" + PRENOM.getText().toString() + 
								 "&pseudo=" + PSEUDO.getText().toString() + 
								 "&password=" + SecurityUtils.sha256(PASSWORD.getText().toString()) + 
								 "&email=" + EMAIL.getText().toString()).execute();	
				} else {
					EMAIL.setBackgroundResource(R.drawable.border_edittext_error);
					
					toast.setText("L'email entré n'est pas valide.");
					toast.show();
				}
			} else {
				PASSWORD.setBackgroundResource(R.drawable.border_edittext_error);
				PASSWORD_AGAIN.setBackgroundResource(R.drawable.border_edittext_error);
				
				toast.setText("Les mots de passe saisis ne sont pas identiques.");
				toast.show();
			}
		} else {
			boolean isPseudoOK = true;
			boolean isPasswordOK = true;
			boolean isPasswordAgainOK = true;
			boolean isEmailOK = true;
			boolean isNomOK = true;
			boolean isPrenomOK = true;
			
			String txtToast = "";
			
			if (PSEUDO.getText().toString().length() == 0) {
				PSEUDO.setBackgroundResource(R.drawable.border_edittext_error);
				isPseudoOK = false;
			} else {
				PSEUDO.setBackgroundResource(R.drawable.border_edittext);
			}
			
			if (PASSWORD.getText().toString().length() == 0) {
				PASSWORD.setBackgroundResource(R.drawable.border_edittext_error);
				isPasswordOK = false;
			} else {
				PASSWORD.setBackgroundResource(R.drawable.border_edittext);
			}
			
			if (PASSWORD_AGAIN.getText().toString().length() == 0) {
				PASSWORD_AGAIN.setBackgroundResource(R.drawable.border_edittext_error);
				isPasswordAgainOK = false;
			} else {
				PASSWORD_AGAIN.setBackgroundResource(R.drawable.border_edittext);
			}
			
			if (EMAIL.getText().toString().length() == 0) {
				EMAIL.setBackgroundResource(R.drawable.border_edittext_error);
				isEmailOK = false;
			} else {
				EMAIL.setBackgroundResource(R.drawable.border_edittext);
			}
			
			if (NOM.getText().toString().length() == 0) {
				NOM.setBackgroundResource(R.drawable.border_edittext_error);
				isNomOK = false;
			} else {
				NOM.setBackgroundResource(R.drawable.border_edittext);
			}
			
			if (PRENOM.getText().toString().length() == 0) {
				PRENOM.setBackgroundResource(R.drawable.border_edittext_error);
				isPrenomOK = false;
			} else {
				PRENOM.setBackgroundResource(R.drawable.border_edittext);
			}
			
			if (!isPseudoOK && !isPasswordOK && !isPasswordAgainOK && !isEmailOK && !isNomOK && !isPrenomOK) {
				txtToast = "Les six champs sont vides.";
			} else {
				if (!isPseudoOK) {
					txtToast = "Le champ \"Pseudo\" est vide.\n";
				} 
				
				if (!isPasswordOK) {
					txtToast += "Le champ \"Mot de passe\" est vide.\n";
				}
				
				if (!isPasswordAgainOK) {
					txtToast += "Le deuxième champ \"Mot de passe\" est vide.\n";
				} 
				
				if (!isEmailOK) {
					txtToast += "Le champ \"Email\" est vide.\n";
				} 
				
				if (!isNomOK) {
					txtToast += "Le champ \"Nom\" est vide.\n";
				} 
				
				if (!isPrenomOK) {
					txtToast += "Le champ \"Prénom\" est vide.";
				} 
			}
			
			toast.setText(txtToast);
			toast.show();
		}
	}

	/**
	 * Méthode permettant de valider un email.
	 * 
	 * @param email L'email à valider.
	 * 
	 * @return <b>true</b> si l'email est validé.<br /><b>false</b> sinon.
	 */
	private boolean validate(String email) {
		pattern = Pattern.compile(Constantes.EMAIL_PATTERN);
		matcher = pattern.matcher(email.toUpperCase());

		return matcher.matches();
	}

	/* Classe interne. */
	
	private class InscriptionAdmin extends AsyncTask<Void, Void, Void> {
		private ProgressDialog progress;
		private String pathUrl;

		public InscriptionAdmin(ProgressDialog progress, String pathUrl) {
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
		    HttpGet httpGet = new HttpGet(pathUrl);

		    try {
		        HttpResponse response = httpclient.execute(httpGet);
		        
		        if (response.getStatusLine().getStatusCode() == 200) {
		        	toast.setText("Inscription effectuée. Votre compte est en attente de validation par un administrateur.");
		        	toast.show();
		        	
		        	finish();
		        } else if (response.getStatusLine().getStatusCode() == 403) {
		        	toast.setText("Un utilisateur avec ce pseudo existe déjà.");	
					toast.show();
		        } else if (response.getStatusLine().getStatusCode() == 500) {
		        	toast.setText("Une erreur est survenue au niveau de la BDD.");	
					toast.show();
		        } else {
		        	toast.setText("Erreur inconnue. Veuillez réessayer.");	
					toast.show();
		        }
		    } catch (ClientProtocolException e) {
		    	InscriptionA.this.runOnUiThread(new Runnable() {
					public void run() {
						AlertDialog.Builder builder = new AlertDialog.Builder(InscriptionA.this);
						builder.setMessage("Erreur - Vérifiez votre connexion");
						builder.setCancelable(false);
						builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								InscriptionA.this.finish();
							}
						});

						AlertDialog error = builder.create();
						error.show();
					}
				});
		    } catch (IOException e) {
		    	InscriptionA.this.runOnUiThread(new Runnable() {
					public void run() {
						AlertDialog.Builder builder = new AlertDialog.Builder(InscriptionA.this);
						builder.setMessage("Erreur - Vérifiez votre connexion");
						builder.setCancelable(false);
						builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								InscriptionA.this.finish();
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
	
	/**
	 * Méthode déclenchée lors d'un click sur le bouton virtuel Android de retour.
	 */
	@Override
	public void onBackPressed() {
		if ((PSEUDO.getText().toString().length() != 0) || (PASSWORD.getText().toString().length() != 0) || 
				(PASSWORD_AGAIN.getText().toString().length() != 0) || (EMAIL.getText().toString().length() != 0) ||
				(NOM.getText().toString().length() != 0) || (PRENOM.getText().toString().length() != 0)) {
			
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