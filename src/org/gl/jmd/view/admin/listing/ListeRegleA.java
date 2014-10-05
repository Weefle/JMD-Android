package org.gl.jmd.view.admin.listing;

import java.io.*;
import java.util.*;

import org.apache.http.client.ClientProtocolException;
import org.gl.jmd.R;
import org.gl.jmd.model.enumeration.OperateurType;
import org.gl.jmd.model.enumeration.RegleType;
import org.gl.jmd.utils.*;

import android.app.*;
import android.content.*;
import android.content.res.Configuration;
import android.os.*;
import android.util.Log;
import android.view.View;
import android.widget.*;

/**
 * Activit� correspondant � la vue de listing des ann�es d'un dipl�me.
 * 
 * @author Jordi CHARPENTIER & Yoann VANHOESERLANDE
 */
public class ListeRegleA extends Activity {
	
	private Activity activity;
	
	private String idAnnee = "";
	
	private Toast toast;
	
	private String contenuPage = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.administrateur_liste_regle);
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
		
		idAnnee = getIntent().getExtras().getString("idAnnee");
		
		activity = this;
		toast = Toast.makeText(getBaseContext(), "", Toast.LENGTH_SHORT);
		
		actualiserListe();
	}
	
	public void actualiserListe() {
		String URL = "http://www.jordi-charpentier.com/jmd/mobile/getInfos.php?type=regle&idAnnee=" + idAnnee;
		
		ProgressDialog progress = new ProgressDialog(this);
		progress.setMessage("Chargement...");
		new ListerRegles(progress, URL).execute();	
	}
	
	public void initListe() {
		StringTokenizer chaineEclate = new StringTokenizer(contenuPage, ";");
		
		final String[] temp = new String[chaineEclate.countTokens()];
		final ListView liste = (ListView) findViewById(android.R.id.list);
		
		if (temp.length != 0) {
			int i = 0;

			while(chaineEclate.hasMoreTokens()) {
				temp[i++] = chaineEclate.nextToken();	
			}

			final ArrayList<HashMap<String, String>> listItem = new ArrayList<HashMap<String, String>>();
			HashMap<String, String> map;
			OperateurType o;
			
			for(int s = 0; s < temp.length;) {
				map = new HashMap<String, String>();

				map.put("id", temp[s]);
				
				if (temp[s + 4].equals("0")) {
					map.put("operateur", ">");
					o = OperateurType.SUPERIEUR;
				} else if (temp[s + 4].equals("1")) {
					map.put("operateur", ">=");
					o = OperateurType.SUPERIEUR_EGAL;
				} else if (temp[s + 4].equals("2")) {
					map.put("operateur", "=");
					o = OperateurType.EGAL;
				} else if (temp[s + 4].equals("3")) {
					map.put("operateur", "<=");
					o = OperateurType.INFERIEUR_EGAL;
				} else if (temp[s + 4].equals("4")) {
					map.put("operateur", "<");
					o = OperateurType.INFERIEUR;
				} else {
					Log.i("ListeRegleA", temp[s+4]);
					
					toast.setText("Erreur - Veuillez r�essayer");
					toast.show();
					
					return;
				}
				
				String operateurAffiche = "";
				
				if (o.equals(OperateurType.SUPERIEUR)) {
					operateurAffiche = "sup�rieure";
				} else if (o.equals(OperateurType.SUPERIEUR_EGAL)) {
					operateurAffiche = "sup�rieure ou �gale";
				} else if (o.equals(OperateurType.EGAL)) {
					operateurAffiche = "�gale";
				} else if (o.equals(OperateurType.INFERIEUR_EGAL)) {
					operateurAffiche = "inf�rieure ou �gale";
				} else if (o.equals(OperateurType.INFERIEUR)) {
					operateurAffiche = "inf�rieure";
				}
				
				if (temp[s + 1].equals("1")) {
					map.put("titre", "Nb d'option(s) minimale(s) " + operateurAffiche + " � " + temp[s + 5]);
				} else if (temp[s + 1].equals("2")) {
					map.put("titre", "Note minimale " + operateurAffiche + " � " + temp[s + 5]);
				} else {
					toast.setText("Erreur - Veuillez r�essayer");
					toast.show();
					
					return;
				}
				
				map.put("description", "S'applique sur : UE (" + (temp[s+2].equals("1000000000") ? "toutes" : "ID : " + temp[s+2]) + "), mati�re (" + (temp[s+3].equals("1000000000") ? "toutes" : "ID : " + temp[s+3]) + ")");
				
				map.put("idUE", temp[s + 2]);
				map.put("idMatiere", temp[s + 3]);
				
				map.put("value", temp[s + 5]);
				
				s = s + 6;

				listItem.add(map);		
			}
			
			final SimpleAdapter mSchedule = new SimpleAdapter (getBaseContext(), listItem, R.layout.administrateur_liste_regle_list, new String[] {"titre", "description"}, new int[] {R.id.titre, R.id.description});

			liste.setAdapter(mSchedule); 
			
			liste.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
				public boolean onItemLongClick(AdapterView<?> arg0, View arg1, final int arg2, final long arg3) {
					if (listItem.get(arg2).get("img") == null) {
						AlertDialog.Builder confirmQuitter = new AlertDialog.Builder(ListeRegleA.this);
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
											ListeRegleA.this.finish();
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
												ListeRegleA.this.finish();
												android.os.Process.killProcess(android.os.Process.myPid());
											}
										});
										
										errorDia.show();
									}
								}
								
								String URL = "http://www.jordi-charpentier.com/jmd/mobile/delete.php?idAdmin=" + idAdmin + "&type=regle&idRegle=" + listItem.get(arg2).get("id");	

								ProgressDialog progress = new ProgressDialog(activity);
								progress.setMessage("Chargement...");
								new DeleteRegle(progress, URL).execute();
								
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
			map.put("titre", "Aucune r�gle.");
			
			listItem.add(map);
			
			SimpleAdapter mSchedule = new SimpleAdapter (getBaseContext(), listItem, R.layout.administrateur_liste_regle_empty_list, new String[] {"titre"}, new int[] {R.id.titre});

			liste.setAdapter(mSchedule); 
		}
	}
	
	/**
	 * Classe interne repr�sentant une t�che asynchrone qui sera effectu�e en fond pendant un rond de chargement.
	 * 
	 * @author Jordi CHARPENTIER & Yoann VANHOESERLANDE
	 */
	private class ListerRegles extends AsyncTask<Void, Void, Void> {
		private ProgressDialog progress;
		private String pathUrl;

		public ListerRegles(ProgressDialog progress, String pathUrl) {
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
					ListeRegleA.this.runOnUiThread(new Runnable() {
						public void run() {
							AlertDialog.Builder builder = new AlertDialog.Builder(ListeRegleA.this);
							builder.setMessage("Erreur - V�rifiez votre connexion");
							builder.setCancelable(false);
							builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									ListeRegleA.this.finish();
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
			
			contenuPage = contenuPage.replaceAll("\n", "");
			contenuPage = contenuPage.replaceAll(" ", "");
			
			if (contenuPage.equals("error")) {
				toast.setText("Erreur. Veuillez r�essayer.");
				toast.show();	
			} else {
				ListeRegleA.this.runOnUiThread(new Runnable() {
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
	 * Classe interne repr�sentant une t�che asynchrone (suppression d'une ann�e) qui sera effectu�e en fond pendant un rond de chargement.
	 * 
	 * @author Jordi CHARPENTIER & Yoann VANHOESERLANDE
	 */
	private class DeleteRegle extends AsyncTask<Void, Void, Void> {
		private ProgressDialog progress;
		private String pathUrl;

		public DeleteRegle(ProgressDialog progress, String pathUrl) {
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
					ListeRegleA.this.runOnUiThread(new Runnable() {
						public void run() {
							AlertDialog.Builder builder = new AlertDialog.Builder(ListeRegleA.this);
							builder.setMessage("Erreur - V�rifiez votre connexion");
							builder.setCancelable(false);
							builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									ListeRegleA.this.finish();
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
				toast.setText("R�gle supprim�e.");
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
	 * M�thode ex�cut�e lorsque l'activit� est relanc�e.<br />
	 * Ici, �a permet d'actualiser la liste des ann�es lorsqu'une ann�e vient d'�tre cr�� et que l'application ram�ne l'utilisateur sur cette vue de listing.
	 */
	@Override
	public void onRestart() {
		actualiserListe();
		
		super.onRestart();
	} 
}
