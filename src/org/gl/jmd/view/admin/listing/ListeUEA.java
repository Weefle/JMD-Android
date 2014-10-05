package org.gl.jmd.view.admin.listing;

import java.io.*;
import java.util.*;

import org.apache.http.client.ClientProtocolException;
import org.gl.jmd.R;
import org.gl.jmd.utils.*;

import android.app.*;
import android.content.*;
import android.content.res.Configuration;
import android.os.*;
import android.view.View;
import android.widget.*;

/**
 * Activité correspondant à la vue de listing des UE d'une année.
 * 
 * @author Jordi CHARPENTIER & Yoann VANHOESERLANDE
 */
public class ListeUEA extends Activity {
	
	private Activity activity;
	
	private Toast toast;
	
	private String contenuPage = "";
	
	private String idAnnee = "";
	
	private String decoupage = "";
	
	private Intent lastIntent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.administrateur_liste_ue);
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
		
		lastIntent = getIntent();
		
		idAnnee = lastIntent.getExtras().getString("idAnnee");
		decoupage = lastIntent.getExtras().getString("decoupage");
		
		activity = this;
		toast = Toast.makeText(getBaseContext(), "", Toast.LENGTH_SHORT);
		
		actualiserListe();
	}
	
	public void actualiserListe() {
		String URL = "http://www.jordi-charpentier.com/jmd/mobile/getInfos.php?type=ue&idAnnee=" + idAnnee + "&yearType=" + decoupage;
		
		ProgressDialog progress = new ProgressDialog(this);
		progress.setMessage("Chargement...");
		new ListerUE(progress, URL).execute();
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

				s = s + 3;

				listItem.add(map);		
			}
			
			final SimpleAdapter mSchedule = new SimpleAdapter (getBaseContext(), listItem, R.layout.administrateur_liste_ue_list, new String[] {"titre"}, new int[] {R.id.titre});

			liste.setAdapter(mSchedule); 
			
			liste.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				public void onItemClick(AdapterView<?> arg0, View arg1, final int position, long arg3) {
					Intent newIntent = new Intent(ListeUEA.this, ListeMatiereA.class);
					newIntent.putExtra("idAnnee", idAnnee);
					newIntent.putExtra("idUE", "" + listItem.get(position).get("id"));
					newIntent.putExtra("nom", "" + listItem.get(position).get("titre"));
					
					startActivity(newIntent);
				}
			});
			
			liste.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
				public boolean onItemLongClick(AdapterView<?> arg0, View arg1, final int arg2, final long arg3) {
					if (listItem.get(arg2).get("img") == null) {
						AlertDialog.Builder confirmQuitter = new AlertDialog.Builder(ListeUEA.this);
						confirmQuitter.setTitle("Suppression");
						confirmQuitter.setMessage("Voulez-vous vraiment supprimer cet élément ?");
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
											ListeUEA.this.finish();
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
												ListeUEA.this.finish();
												android.os.Process.killProcess(android.os.Process.myPid());
											}
										});
										
										errorDia.show();
									}
								}
								
								String URL = "http://www.jordi-charpentier.com/jmd/mobile/delete.php?idAdmin=" + idAdmin + "&type=ue&idUE=" + listItem.get(arg2).get("id");	

								ProgressDialog progress = new ProgressDialog(activity);
								progress.setMessage("Chargement...");
								new DeleteUE(progress, URL).execute();
								
								actualiserListe();
								
								liste.setSelection(arg2);
							}
						});
						
						confirmQuitter.setNegativeButton("Non", null);
						confirmQuitter.show();
					}
	    			
					return true;
				}
				
			}); 
		} else {
			final ArrayList<HashMap<String, String>> listItem = new ArrayList<HashMap<String, String>>();
			HashMap<String, String> map;
			
			map = new HashMap<String, String>();
			map.put("titre", "Aucune UE.");
			
			listItem.add(map);
			
			SimpleAdapter mSchedule = new SimpleAdapter (getBaseContext(), listItem, R.layout.administrateur_liste_ue_list, new String[] {"titre"}, new int[] {R.id.titre});

			liste.setAdapter(mSchedule); 
		}
	}
	
	/**
	 * Classe interne représentant une tâche asynchrone (lister les UE d'une année en base) qui sera effectuée en fond pendant un rond de chargement.
	 * 
	 * @author Jordi CHARPENTIER & Yoann VANHOESERLANDE
	 */
	private class ListerUE extends AsyncTask<Void, Void, Void> {
		private ProgressDialog progress;
		private String pathUrl;

		public ListerUE(ProgressDialog progress, String pathUrl) {
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
					ListeUEA.this.runOnUiThread(new Runnable() {
						public void run() {
							AlertDialog.Builder builder = new AlertDialog.Builder(ListeUEA.this);
							builder.setMessage("Erreur - Vérifiez votre connexion");
							builder.setCancelable(false);
							builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									ListeUEA.this.finish();
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
				toast.setText("Erreur. Veuillez réessayer.");
				toast.show();	
			} else {
				ListeUEA.this.runOnUiThread(new Runnable() {
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
	 * Classe interne représentant une tâche asynchrone (suppression d'une UE) qui sera effectuée en fond pendant un rond de chargement.
	 * 
	 * @author Jordi CHARPENTIER & Yoann VANHOESERLANDE
	 */
	private class DeleteUE extends AsyncTask<Void, Void, Void> {
		private ProgressDialog progress;
		private String pathUrl;

		public DeleteUE(ProgressDialog progress, String pathUrl) {
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
					ListeUEA.this.runOnUiThread(new Runnable() {
						public void run() {
							AlertDialog.Builder builder = new AlertDialog.Builder(ListeUEA.this);
							builder.setMessage("Erreur - Vérifiez votre connexion");
							builder.setCancelable(false);
							builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									ListeUEA.this.finish();
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
			
			if (contenuPage.equals("ok")) {
				toast.setText("Année supprimée.");
				toast.show();	
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
	 * Méthode exécutée lorsque l'activité est relancée.<br />
	 * Ici, ça permet d'actualiser la liste des UE lorsqu'une UE vient d'être créé et que l'application ramène l'utilisateur sur cette vue de listing.
	 */
	@Override
	public void onRestart() {
		actualiserListe();
		
		super.onRestart();
	} 
}
