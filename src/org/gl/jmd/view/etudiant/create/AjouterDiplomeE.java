package org.gl.jmd.view.etudiant.create;

import java.io.IOException;
import java.util.*;

import org.apache.http.client.ClientProtocolException;
import org.gl.jmd.R;
import org.gl.jmd.dao.EtudiantDAO;
import org.gl.jmd.model.Diplome;
import org.gl.jmd.model.user.Etudiant;
import org.gl.jmd.utils.WebUtils;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import android.app.*;
import android.content.DialogInterface;
import android.content.res.Configuration;

/**
 * Activit� correspondant � la vue de modification de l'accueil d'un �tudiant.
 * 
 * @author Jordi CHARPENTIER & Yoann VANHOESERLANDE
 */
public class AjouterDiplomeE extends Activity {

	private Activity activity;

	private String contenuPage = "";

	private Toast toast;

	private Etudiant etud = EtudiantDAO.load();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.etudiant_modifier_accueil);
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
		
		activity = this;
		toast = Toast.makeText(activity, "", Toast.LENGTH_SHORT);
	}

	public void recherche(View view) {
		EditText et_dip = (EditText) findViewById(R.id.etudiant_modifier_ae_zone_recherche);

		String url = "http://www.jordi-charpentier.com/jmd/mobile/getInfos.php?type=diplome&nom=" + et_dip.getText().toString();

		ProgressDialog progress = new ProgressDialog(activity);
		progress.setMessage("Chargement...");
		new RechercherDiplome(progress, url).execute();	
	}

	/**
	 * Classe interne repr�sentant une t�che asynchrone qui sera effectu�e en fond pendant un rond de chargement.
	 * 
	 * @author Jordi CHARPENTIER & Yoann VANHOESERLANDE
	 */
	private class RechercherDiplome extends AsyncTask<Void, Void, Void> {
		private ProgressDialog progress;
		private String pathUrl;

		public RechercherDiplome(ProgressDialog progress, String pathUrl) {
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
					AjouterDiplomeE.this.runOnUiThread(new Runnable() {
						public void run() {
							AlertDialog.Builder builder = new AlertDialog.Builder(AjouterDiplomeE.this);
							builder.setMessage("Erreur - V�rifiez votre connexion");
							builder.setCancelable(false);
							builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									AjouterDiplomeE.this.finish();
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

			AjouterDiplomeE.this.runOnUiThread(new Runnable() {
				public void run() {
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
							map.put("description", temp[s + 1]);

							s = s + 2;

							listItem.add(map);		
						}

						final SimpleAdapter mSchedule = new SimpleAdapter (getBaseContext(), listItem, R.layout.etudiant_modifier_accueil_list, new String[] {"description"}, new int[] {R.id.description});

						liste.setAdapter(mSchedule); 

						liste.setOnItemClickListener(new AdapterView.OnItemClickListener() {
							public void onItemClick(AdapterView<?> arg0, View arg1, final int position, long arg3) {
								if (etud == null) {
									Etudiant newEtudiant = new Etudiant();
									ArrayList<Diplome> listeDiplomes = new ArrayList<Diplome>();
									newEtudiant.setListeDiplomes(listeDiplomes);

									EtudiantDAO.save(newEtudiant);

									etud = newEtudiant;
								} 

								String nom = listItem.get(position).get("description");
								int id = Integer.parseInt(listItem.get(position).get("id"));
								
								Diplome dip = new Diplome();
								dip.setId(id);
								dip.setNom(nom);

								ArrayList<Diplome> listeDip = etud.getListeDiplomes();
								boolean exists = false;

								for (int i = 0; i < listeDip.size(); i++) {
									if (listeDip.get(i).getId() == id) {
										exists = true;
									}
								}

								if (!exists) {
									listeDip.add(dip);
									etud.setListeDiplomes(listeDip);
									
									if (EtudiantDAO.save(etud)) {
										finish();
									} else {
										toast.setText("Erreur lors de la sauvegarde du dipl�me.");
										toast.show();
									}									
								} else {
									toast.setText("Le dipl�me est d�j� ajout�.");
									toast.show();
								}
							}
						});
					} else {
						final ArrayList<HashMap<String, String>> listItem = new ArrayList<HashMap<String, String>>();
						HashMap<String, String> map;

						map = new HashMap<String, String>();
						
						map.put("description", "Aucun r�sultat.");

						listItem.add(map);		

						final SimpleAdapter mSchedule = new SimpleAdapter (getBaseContext(), listItem, R.layout.etudiant_modifier_accueil_list, new String[] {"description"}, new int[] {R.id.description});

						liste.setAdapter(mSchedule); 
					}
				}});

			return null;
		}
	}

	/* M�thode h�rit�e de la classe Activity. */

	/**
	 * M�thode permettant d'emp�cher la reconstruction de la vue lors de la rotation de l'�cran. 
	 * 
	 * @param newConfig L'�tat de la vue avant la rotation.
	 */
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}
}