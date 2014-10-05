package org.gl.jmd.view.admin.listing;

import java.io.File;
import java.io.IOException;
import java.util.*;

import org.apache.http.client.ClientProtocolException;
import org.gl.jmd.R;
import org.gl.jmd.utils.*;

import android.app.*;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.*;
import android.util.Log;
import android.view.View;
import android.widget.*;

/**
 * Activit� correspondant � la vue de listing des �tablissements.
 * 
 * @author Jordi CHARPENTIER & Yoann VANHOESERLANDE
 */
public class ListeEtablissementA extends Activity {

	private Activity activity;

	private Toast toast;

	private String contenuPage = "";
	
	private long back_pressed;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.administrateur_liste_etablissement);
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
		
		activity = this;
		toast = Toast.makeText(getBaseContext(), "", Toast.LENGTH_SHORT);

		actualiserListe();
	}

	public void actualiserListe() {
		String URL = "http://www.jordi-charpentier.com/jmd/mobile/getInfos.php?type=etablissement";

		ProgressDialog progress = new ProgressDialog(this);
		progress.setMessage("Chargement...");
		new ListerEtablissements(progress, URL).execute();	
	}

	public void initListe() {
		StringTokenizer chaineEclate = new StringTokenizer(contenuPage, ";");

		final String[] temp = new String[chaineEclate.countTokens()];
		final ListView liste = (ListView) findViewById(android.R.id.list);

		if (temp.length != 1) {
			int i = 0;

			while(chaineEclate.hasMoreTokens()) {
				temp[i++] = chaineEclate.nextToken();	
			}

			final ArrayList<HashMap<String, String>> listItem = new ArrayList<HashMap<String, String>>();
			HashMap<String, String> map;

			for(int s = 0; s < temp.length;) {
				map = new HashMap<String, String>();

				map.put("id", temp[s].replaceAll(" ", ""));
				map.put("titre", temp[s + 1]);
				map.put("description", temp[s + 2]);

				s = s + 3;

				listItem.add(map);		
			}

			final SimpleAdapter mSchedule = new SimpleAdapter (getBaseContext(), listItem, R.layout.administrateur_liste_etablissement_list, new String[] {"titre", "description"}, new int[] {R.id.titre, R.id.description});

			liste.setAdapter(mSchedule); 

			liste.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
				public boolean onItemLongClick(AdapterView<?> arg0, View arg1, final int arg2, final long arg3) {
					AlertDialog.Builder confirmQuitter = new AlertDialog.Builder(ListeEtablissementA.this);
					confirmQuitter.setTitle("Suppression");
					confirmQuitter.setMessage("Voulez-vous vraiment supprimer cet �l�ment ?");
					confirmQuitter.setCancelable(false);
					confirmQuitter.setPositiveButton("Oui", new AlertDialog.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
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
										ListeEtablissementA.this.finish();
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
											ListeEtablissementA.this.finish();
											android.os.Process.killProcess(android.os.Process.myPid());
										}
									});

									errorDia.show();
								}
							}

							String URL = "http://www.jordi-charpentier.com/jmd/mobile/delete.php?idAdmin=" + idAdmin + "&type=etablissement&idEtablissement=" + listItem.get(arg2).get("id");	
							
							ProgressDialog progress = new ProgressDialog(activity);
							progress.setMessage("Chargement...");
							new DeleteEtablissement(progress, URL).execute();

							actualiserListe();

							liste.setSelection(arg2);
						}
					});
					confirmQuitter.setNegativeButton("Non", null);
					confirmQuitter.show();

					return true;
				}

			});
		} else {						
			final ArrayList<HashMap<String, String>> listItem = new ArrayList<HashMap<String, String>>();
			HashMap<String, String> map;

			map = new HashMap<String, String>();
			map.put("titre", "Aucun �tablissement.");

			listItem.add(map);

			SimpleAdapter mSchedule = new SimpleAdapter (getBaseContext(), listItem, R.layout.administrateur_liste_etablissement_empty_list, new String[] {"titre"}, new int[] {R.id.titre});

			liste.setAdapter(mSchedule); 
		}
	}

	/**
	 * Classe interne repr�sentant une t�che asynchrone (lister les �tablissements pr�sents en base) qui sera effectu�e en fond pendant un rond de chargement.
	 * 
	 * @author Jordi CHARPENTIER & Yoann VANHOESERLANDE
	 */
	private class ListerEtablissements extends AsyncTask<Void, Void, Void> {
		private ProgressDialog progress;
		private String pathUrl;

		public ListerEtablissements(ProgressDialog progress, String pathUrl) {
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
					ListeEtablissementA.this.runOnUiThread(new Runnable() {
						public void run() {
							AlertDialog.Builder builder = new AlertDialog.Builder(ListeEtablissementA.this);
							builder.setMessage("Erreur - V�rifiez votre connexion");
							builder.setCancelable(false);
							builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									ListeEtablissementA.this.finish();
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

			if (contenuPage.equals("error")) {
				toast.setText("Erreur. Veuillez r�essayer.");
				toast.show();	
			} else {
				ListeEtablissementA.this.runOnUiThread(new Runnable() {
					public void run() {
						initListe();

						return;
					}
				});
			} 

			return null;
		}
	}

	/**
	 * Classe interne repr�sentant une t�che asynchrone (suppression d'un �tablissement) qui sera effectu�e en fond pendant un rond de chargement.
	 * 
	 * @author Jordi CHARPENTIER & Yoann VANHOESERLANDE
	 */
	private class DeleteEtablissement extends AsyncTask<Void, Void, Void> {
		private ProgressDialog progress;
		private String pathUrl;

		public DeleteEtablissement(ProgressDialog progress, String pathUrl) {
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
					ListeEtablissementA.this.runOnUiThread(new Runnable() {
						public void run() {
							AlertDialog.Builder builder = new AlertDialog.Builder(ListeEtablissementA.this);
							builder.setMessage("Erreur - V�rifiez votre connexion");
							builder.setCancelable(false);
							builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									ListeEtablissementA.this.finish();
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

			Log.i("ListeEtablissementA", contenuPage);
			
			if (contenuPage.equals("ok")) {
				toast.setText("Etablissement supprim�.");
				toast.show();	
			} else if (contenuPage.equals("error")) {
				toast.setText("Erreur. Veuillez r�essayer.");
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
		if (back_pressed + 2000 > System.currentTimeMillis()) {
			android.os.Process.killProcess(android.os.Process.myPid());
		} else {
			Toast.makeText(getBaseContext(), "Appuyez encore une fois sur \"Retour\" pour quitter l'application.", Toast.LENGTH_SHORT).show();
		}
		
        back_pressed = System.currentTimeMillis();
	}
	
	/**
	 * M�thode ex�cut�e lorsque l'activit� est relanc�e.<br />
	 * Ici, �a permet d'actualiser la liste des �tablissements.
	 */
	@Override
	public void onRestart() {
		actualiserListe();
		
		super.onRestart();
	} 
}