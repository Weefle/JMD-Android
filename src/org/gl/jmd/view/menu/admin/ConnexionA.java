package org.gl.jmd.view.menu.admin;

import java.io.IOException;
import java.math.BigInteger;
import java.security.*;

import org.apache.http.client.ClientProtocolException;
import org.gl.jmd.R;
import org.gl.jmd.model.user.Admin;
import org.gl.jmd.utils.*;
import org.gl.jmd.view.InitApp;
import org.gl.jmd.view.admin.*;

import android.app.*;
import android.content.*;
import android.content.res.Configuration;
import android.os.*;
import android.view.View;
import android.widget.*;

/**
 * Activit� correspondant � la vue de connexion � l'application pour les administrateurs.
 * 
 * @author Jordi CHARPENTIER & Yoann VANHOESERLANDE
 */
public class ConnexionA extends Activity {
	
	private Activity activity;
	
	private Toast toast;
	
	private Intent intent;
	
	private String contenuPage = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.administrateur_connexion);
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
		
		activity = this;
		toast = Toast.makeText(activity, "", Toast.LENGTH_SHORT);
		intent = new Intent(ConnexionA.this, AccueilA.class);
	}
	
	public void inscription(View view) {
		startActivity(new Intent(ConnexionA.this, InscriptionA.class));			
	}
	
	/**
	 * M�thode permettant de connecter l'utilisateur.
	 * 
	 * @param view La vue lors du click sur le bouton de connexion.
	 */
	public void connexion(View view) {
		final EditText PSEUDO = (EditText) findViewById(R.id.connex_zone_pseudo);
		final EditText PASSWORD = (EditText) findViewById(R.id.connex_zone_mdp);
		
		if ((PSEUDO.getText().toString().length() != 0) && (PASSWORD.getText().toString().length() != 0)) {
			Admin a = new Admin();
			a.setPseudo(PSEUDO.getText().toString());
			a.setPassword(md5(PASSWORD.getText().toString()));

			String URL = "http://www.jordi-charpentier.com/jmd/mobile/connexion.php?pseudo=" + a.getPseudo() + "&password=" + a.getPassword();

			ProgressDialog progress = new ProgressDialog(activity);
			progress.setMessage("Chargement...");
			new SeConnecter(progress, URL).execute();	
		} else {
			toast.setText("Au moins un des champs est vide.");
			toast.show();
		}
	}
	
	/**
	 * M�thode permettant de rediriger l'utilisateur vers la vue de r�cup�ration du mot de passe.
	 * 
	 * @param view La vue lors du click sur le lien de r�cup�ration du mot de passe.
	 */
	public void resetPassword(View view) {
		startActivity(new Intent(ConnexionA.this, RecupMDPA.class));
	}
	
	/**
	 * M�thode permettant d'hasher une cha�ne de caract�res en MD5.
	 * 
	 * @param s La cha�ne � hasher en MD5.
	 * @return Une cha�ne hash�e en MD5.
	 */
	public String md5(String... strings) {
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("MD5");
			
			for(final String s : strings) {
				md.update(s.getBytes());
			}
		} catch (NoSuchAlgorithmException ex) {
			throw new RuntimeException("Le MD5 n'est pas support�.");
		}
		
		final BigInteger bigInt = new BigInteger(1, md.digest());
		
		return String.format("%032x", bigInt);
	}
	
	/**
	 * Classe interne repr�sentant une t�che asynchrone qui sera effectu�e en fond pendant un rond de chargement.
	 * 
	 * @author Jordi CHARPENTIER & Yoann VANHOESERLANDE
	 */
	private class SeConnecter extends AsyncTask<Void, Void, Void> {
		private ProgressDialog progress;
		private String pathUrl;

		public SeConnecter(ProgressDialog progress, String pathUrl) {
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
					ConnexionA.this.runOnUiThread(new Runnable() {
						public void run() {
							AlertDialog.Builder builder = new AlertDialog.Builder(ConnexionA.this);
							builder.setMessage("Erreur - V�rifiez votre connexion");
							builder.setCancelable(false);
							builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									ConnexionA.this.finish();
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
			
			if(contenuPage.equals("false")) {
				toast.setText("Mauvais identifiants. Veuillez r�essayer.");	
				toast.show();
			} else if (contenuPage.equals("error")) {
				toast.setText("Erreur. Veuillez r�essayer.");	
				toast.show();
			} else if (contenuPage.equals("non_active")) {
				toast.setText("Erreur. Votre compte n'est pas encore activ�.");	
				toast.show();
			} else {
				FileUtils.ecrireTexteFichier(contenuPage, Environment.getExternalStorageDirectory().getPath() + "/cacheJMD/logins.jmd");
				
				finish();
				startActivity(intent);
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
		finish();
		startActivity(new Intent(ConnexionA.this, InitApp.class));	
	}
}
