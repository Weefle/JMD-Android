package org.gl.jmd.view.menu.admin;

import java.io.*;
import java.util.regex.*;

import org.apache.http.client.ClientProtocolException;
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

	private String contenuPage = "";

	private Matcher matcher;

	private static final String EMAIL_PATTERN = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,4}$";
	
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
		
		PSEUDO = (EditText) findViewById(R.id.admin_inscription_ta_pseudo);
		PASSWORD = (EditText) findViewById(R.id.admin_inscription_ta_password);
		PASSWORD_AGAIN = (EditText) findViewById(R.id.admin_inscription_ta_password_again);
		EMAIL = (EditText) findViewById(R.id.admin_inscription_ta_email);
	}

	/**
	 * Méthode permettant de tenter d'inscrire l'utilisateur.
	 * 
	 * @param view La vue lors du click sur le bouton d'inscription.
	 */
	public void inscription(View view) {
		if ((PSEUDO.getText().toString().length() != 0) && (PASSWORD.getText().toString().length() != 0) && 
				(PASSWORD_AGAIN.getText().toString().length() != 0) && (EMAIL.getText().toString().length() != 0)) {

			if (PASSWORD.getText().toString().equals(PASSWORD_AGAIN.getText().toString())) {
				if (validate(EMAIL.getText().toString())) {					
					String URL = "http://www.jordi-charpentier.com/jmd/mobile/inscription.php?pseudo=" + PSEUDO.getText().toString() + "&password=" + SecurityUtils.sha256(PASSWORD.getText().toString()) + "&email=" + EMAIL.getText().toString();

					ProgressDialog progress = new ProgressDialog(activity);
					progress.setMessage("Chargement...");
					new InscriptionAdmin(progress, URL).execute();	
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
			
			if (!isPseudoOK && !isPasswordOK && !isPasswordAgainOK && !isEmailOK) {
				txtToast = "Les quatres champs sont vides.";
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
					txtToast += "Le champ \"Email\" est vide.";
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
		pattern = Pattern.compile(EMAIL_PATTERN);
		matcher = pattern.matcher(email.toUpperCase());

		return matcher.matches();
	}

	/**
	 * Classe interne représentant une tâche asynchrone qui sera effectuée en fond pendant un rond de chargement.
	 * 
	 * @author Jordi CHARPENTIER & Yoann VANHOESERLANDE
	 */
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
			try {
				if((contenuPage = WebUtils.getPage(pathUrl)) != "-1");

				else {
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
						}});

					return null;
				}
			} catch (ClientProtocolException e) { 
				return null;
			} catch (IOException e) { 
				return null;
			} 

			InscriptionA.this.runOnUiThread(new Runnable() {
				public void run() {
					contenuPage = contenuPage.replaceAll(" ", "");

					if (contenuPage.equals("ok")) {
						toast.setText("Inscription effectuée. Votre compte est en attente de validation par un administrateur.");
						toast.show();
					} else if (contenuPage.equals("doublon")) {
						toast.setText("Un utilisateur existe déjà avec ce pseudo.");
						toast.show();
					} else if (contenuPage.equals("error")) {
						toast.setText("Une erreur est arrivée. Veuillez réessayer.");
						toast.show();
					} else {
						toast.setText("Une erreur est arrivée. Veuillez réessayer.");
						toast.show();
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