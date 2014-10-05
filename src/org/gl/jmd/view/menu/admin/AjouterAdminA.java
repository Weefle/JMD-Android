package org.gl.jmd.view.menu.admin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import org.apache.http.client.ClientProtocolException;
import org.gl.jmd.R;
import org.gl.jmd.utils.FileUtils;
import org.gl.jmd.utils.WebUtils;

import android.app.*;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.*;
import android.view.View;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;

/**
 * Activité correspondant à la vue permettant d'ajouter un administrateur.
 * 
 * @author Jordi CHARPENTIER & Yoann VANHOESERLANDE
 */
public class AjouterAdminA extends Activity {

	private Activity activity;

	private Toast toast;
	
	private String contenuPage = "";
	
	private String[] listeComptes = null;
	
	private ArrayList<String> idList = new ArrayList<String>();
	
	private String selectedId = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.administrateur_ajout_admin);
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
		
		activity = this;
		toast = Toast.makeText(activity, "", Toast.LENGTH_SHORT);
		
		initListe();
	}
	
	public void initListe() {
		String url = "http://www.jordi-charpentier.com/jmd/mobile/getInfos.php?type=compteAttente";

		ProgressDialog progress = new ProgressDialog(activity);
		progress.setMessage("Chargement...");
		new InitListeComptes(progress, url).execute();	
	}

	/**
	 * Méthode permettant de nommer un utilisateur administrateur.
	 * 
	 * @param view La vue lors du click sur le bouton "ajouter".
	 */
	public void nommer(View view) {
		File loginFile = new File(Environment.getExternalStorageDirectory().getPath() + "/cacheJMD/logins.jmd");
		String url = null;
		
		try {
			url = "http://www.jordi-charpentier.com/jmd/mobile/nommerAdmin.php?idAdminNomme=" + selectedId + "&idAdminNommant=" + FileUtils.lireFichier(loginFile);
		
			ProgressDialog progress = new ProgressDialog(activity);
			progress.setMessage("Chargement...");
			new AjouterAdminC(progress, url).execute();
		} catch (Exception e) {
			toast.setText("L'administrateur entré n'est pas valide.");
			toast.show();
			
			return;
		}
	}
	
	/**
	 * Classe interne représentant une tâche asynchrone qui sera effectuée en fond pendant un rond de chargement.
	 * 
	 * @author Jordi CHARPENTIER & Yoann VANHOESERLANDE
	 */
	private class InitListeComptes extends AsyncTask<Void, Void, Void> {
		private ProgressDialog progress;
		private String pathUrl;

		public InitListeComptes(ProgressDialog progress, String pathUrl) {
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
					AjouterAdminA.this.runOnUiThread(new Runnable() {
						public void run() {
							AlertDialog.Builder builder = new AlertDialog.Builder(AjouterAdminA.this);
							builder.setMessage("Erreur - Vérifiez votre connexion");
							builder.setCancelable(false);
							builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									AjouterAdminA.this.finish();
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

			AjouterAdminA.this.runOnUiThread(new Runnable() {
				public void run() {
					contenuPage = contenuPage.replaceAll(" ", "");

					AutoCompleteTextView textView = (AutoCompleteTextView) findViewById(R.id.admin_ajout_admin_ta_pseudo_auto_complete);
					StringTokenizer chaineEclate = new StringTokenizer(contenuPage, ";");
					final String[] temp = new String[chaineEclate.countTokens()];
					
					if (temp.length != 0) {
						int i = 0;

						while(chaineEclate.hasMoreTokens()) {
							temp[i] = chaineEclate.nextToken();	
							temp[i] = temp[i].replaceAll(" ", "");

							i++;
						}

						listeComptes = new String[temp.length / 2];
						int a = 0;

						for(int s = 0; s < temp.length; s = s+ 2) {
							listeComptes[a++] = temp[s] + " - " + temp[s+1];
							idList.add(temp[s]);
						}
						
						ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity, R.xml.suggestion_liste, listeComptes);
						textView.setAdapter(adapter);
						
						textView.setOnItemClickListener(new OnItemClickListener() {
							@Override
					        public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
								selectedId = idList.get(position);
					        }
					    });
						
						TextView textViewNoAdmin = (TextView) findViewById(R.id.admin_ajout_admin_no_admin);
						textViewNoAdmin.setText("Il y a " + listeComptes.length + " administrateur" + ((listeComptes.length > 1) ? "s" : "") + " en attente de validation.");
					} else {
						textView.setInputType(0);
						
						Button button = (Button) findViewById(R.id.admin_ajout_admin_bout_ajouter);
						button.setEnabled(false);
						
						TextView textViewNoAdmin = (TextView) findViewById(R.id.admin_ajout_admin_no_admin);
						textViewNoAdmin.setText("Aucun compte n'est en attente de validation.");
						textViewNoAdmin.setEnabled(true);
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
	private class AjouterAdminC extends AsyncTask<Void, Void, Void> {
		private ProgressDialog progress;
		private String pathUrl;

		public AjouterAdminC(ProgressDialog progress, String pathUrl) {
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
					AjouterAdminA.this.runOnUiThread(new Runnable() {
						public void run() {
							AlertDialog.Builder builder = new AlertDialog.Builder(AjouterAdminA.this);
							builder.setMessage("Erreur - Vérifiez votre connexion");
							builder.setCancelable(false);
							builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									AjouterAdminA.this.finish();
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
				toast.setText("Administrateur nommé avec succès.");
				toast.show();
				
				finish();
			} else if (contenuPage.equals("error")) {
				toast.setText("Erreur. Vous n'avez pas les droits nécessaires.");
				toast.show();	
			} else {
				toast.setText("Erreur. Veuillez réessayer.");
				toast.show();	
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
}
