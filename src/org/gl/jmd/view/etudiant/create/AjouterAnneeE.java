package org.gl.jmd.view.etudiant.create;

import java.io.IOException;
import java.util.*;

import org.apache.http.client.ClientProtocolException;
import org.gl.jmd.R;
import org.gl.jmd.dao.EtudiantDAO;
import org.gl.jmd.model.*;
import org.gl.jmd.model.enumeration.*;
import org.gl.jmd.model.user.Etudiant;
import org.gl.jmd.utils.WebUtils;

import android.os.*;
import android.util.Log;
import android.view.View;
import android.widget.*;
import android.app.*;
import android.content.*;
import android.content.res.Configuration;

/**
 * Activité correspondant à la vue d'ajout des années d'un diplôme (en local).
 * 
 * @author Jordi CHARPENTIER & Yoann VANHOESERLANDE
 */
public class AjouterAnneeE extends Activity {

	private Toast toast;

	private Activity activity;

	private String contenuPage = "";

	private int idDiplome = 0;

	private Etudiant etud = EtudiantDAO.load();

	private Intent lastIntent;

	private ArrayList<UE> listeUE = new ArrayList<UE>();

	private ArrayList<Matiere> listeMatiere = new ArrayList<Matiere>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.etudiant_ajouter_annees);
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

		activity = this;
		toast = Toast.makeText(activity, "", Toast.LENGTH_SHORT);

		lastIntent = getIntent();
		idDiplome = lastIntent.getExtras().getInt("idDiplome");
	}

	public void recherche(View view) {
		final EditText et_dip = (EditText) findViewById(R.id.etudiant_ajouter_annees_zone_recherche);
		String nomAnnee = (et_dip.getText().toString().length() > 0) ? et_dip.getText().toString() : "empty";
		
		String url = "http://www.jordi-charpentier.com/jmd/mobile/getInfos.php?type=annee&nom=" + nomAnnee + "&idDiplome=" + idDiplome;
		
		ProgressDialog progress = new ProgressDialog(activity);
		progress.setMessage("Chargement...");
		new RechercherAnnees(progress, url).execute();	
	}

	/**
	 * Classe interne représentant une tâche asynchrone qui sera effectuée en fond pendant un rond de chargement.
	 * 
	 * @author Jordi CHARPENTIER & Yoann VANHOESERLANDE
	 */
	private class RechercherAnnees extends AsyncTask<Void, Void, Void> {
		private ProgressDialog progress;
		private String pathUrl;

		public RechercherAnnees(ProgressDialog progress, String pathUrl) {
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
					AjouterAnneeE.this.runOnUiThread(new Runnable() {
						public void run() {
							AlertDialog.Builder builder = new AlertDialog.Builder(AjouterAnneeE.this);
							builder.setMessage("Erreur - Vérifiez votre connexion");
							builder.setCancelable(false);
							builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									AjouterAnneeE.this.finish();
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
			
			AjouterAnneeE.this.runOnUiThread(new Runnable() {
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
							map.put("titre", temp[s + 1]);
							map.put("description", temp[s + 4] + " - " + temp[s + 5]);
							map.put("decoupage", temp[s + 2]);
							map.put("isLastYear", temp[s + 3]);
							map.put("idEta", temp[s + 6]);
							
							s = s + 7;							

							listItem.add(map);		
						}

						final SimpleAdapter mSchedule = new SimpleAdapter (getBaseContext(), listItem, R.layout.etudiant_ajouter_annees_list, new String[] {"titre", "description"}, new int[] {R.id.titre, R.id.description});

						liste.setAdapter(mSchedule); 

						liste.setOnItemClickListener(new AdapterView.OnItemClickListener() {
							public void onItemClick(AdapterView<?> arg0, View arg1, final int position, long arg3) {
								final Annee an = new Annee();
								an.setId(Integer.parseInt(listItem.get(position).get("id")));
								an.setNom(listItem.get(position).get("titre"));
								an.setDecoupage(DecoupageType.valueOf(listItem.get(position).get("decoupage")));
								an.setIsLast(listItem.get(position).get("isLastYear").equals("0") ? false : true);
								
								final ArrayList<Regle> listeRegles = new ArrayList<Regle>();
								
								final String urlGetAllRegle = "http://www.jordi-charpentier.com/jmd/mobile/getInfos.php?type=getReglesByID&typeReq=annee&id=" + an.getId();
								
								class GetAllRegleIDAnnee extends AsyncTask<Void, Void, Void> {
									private String pathUrl;
									
									public GetAllRegleIDAnnee(String pathUrl) {
										this.pathUrl = pathUrl;
									}

									protected Void doInBackground(Void... arg0) {
										try {
											if((contenuPage = WebUtils.getPage(pathUrl)) != "-1");

											else {
												AjouterAnneeE.this.runOnUiThread(new Runnable() {
													public void run() {
														AlertDialog.Builder builder = new AlertDialog.Builder(AjouterAnneeE.this);
														builder.setMessage("Erreur - Vérifiez votre connexion");
														builder.setCancelable(false);
														builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
															public void onClick(DialogInterface dialog, int which) {
																AjouterAnneeE.this.finish();
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
										
										StringTokenizer chaineEclate = new StringTokenizer(contenuPage, ";");

										final String[] temp = new String[chaineEclate.countTokens()];

										if (temp.length != 1) {
											int i = 0;

											while(chaineEclate.hasMoreTokens()) {
												temp[i++] = chaineEclate.nextToken();	
											}
											
											Regle r = null;
											
											for(int s = 0; s < temp.length;) {
												r = new Regle();
												r.setId(Integer.parseInt(temp[s].replaceAll(" ", "")));
												r.setRegle(temp[s+1].replaceAll(" ", ""));
												r.setOperateur(temp[s+2].replaceAll(" ", ""));
												r.setValeur(temp[s+3].replaceAll(" ", ""));
													
												listeRegles.add(r);
												
												s=s+4;
											}
										}
										
										an.setListeRegles(listeRegles);

										return null;
									}
								}
								
								new GetAllRegleIDAnnee(urlGetAllRegle).execute();
								
								Etablissement eta = new Etablissement();
								eta.setId(Integer.parseInt(listItem.get(position).get("idEta")));
								eta.setNom(listItem.get(position).get("description").substring(0, listItem.get(position).get("description").indexOf("-") - 1));
								eta.setVille(listItem.get(position).get("description").substring(listItem.get(position).get("description").indexOf("-") + 1, listItem.get(position).get("description").length()));

								an.setEtablissement(eta);

								final String urlGetUEByIdAnnee = "http://www.jordi-charpentier.com/jmd/mobile/getInfos.php?type=getAllInfosAnnee&idAnnee=" + an.getId();

								class GetAllByIDAnnee extends AsyncTask<Void, Void, Void> {
									private String pathUrl;
									private Annee an;
									
									public GetAllByIDAnnee(String pathUrl, Annee an) {
										this.pathUrl = pathUrl;
										this.an = an;
									}

									protected Void doInBackground(Void... arg0) {
										try {
											if((contenuPage = WebUtils.getPage(pathUrl)) != "-1");

											else {
												AjouterAnneeE.this.runOnUiThread(new Runnable() {
													public void run() {
														AlertDialog.Builder builder = new AlertDialog.Builder(AjouterAnneeE.this);
														builder.setMessage("Erreur - Vérifiez votre connexion");
														builder.setCancelable(false);
														builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
															public void onClick(DialogInterface dialog, int which) {
																AjouterAnneeE.this.finish();
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
										
										if (contenuPage.length() == 2) {
											toast.setText("L'année n'est pas complète (0 UE ou 0 matière sur une UE).");
											toast.show();
											
											return null;
										}
										
										listeUE.clear();
										
										StringTokenizer chaineEclate2 = new StringTokenizer(contenuPage, "//");

										final String[] temp2 = new String[chaineEclate2.countTokens()];

										if (temp2.length != 1) {
											int i = 0;

											while(chaineEclate2.hasMoreTokens()) {
												temp2[i++] = chaineEclate2.nextToken();	
											}
											
											for (int n = 0; n < temp2.length; n++) {
												StringTokenizer chaineEclate = new StringTokenizer(temp2[n], ";");

												final String[] temp = new String[chaineEclate.countTokens()];

												if (temp.length != 1) {
													int g = 0;
													boolean isPresent = false;
													
													while(chaineEclate.hasMoreTokens()) {
														temp[g++] = chaineEclate.nextToken();	
													}
													
													final UE ue = new UE();
													ue.setId(Integer.parseInt(temp[0].replaceAll(" ", "")));
													ue.setNom(temp[1]);
													ue.setDecoupage(DecoupageYearType.valueOf(temp[2]));
													
													final String urlGetAllRegleUE = "http://www.jordi-charpentier.com/jmd/mobile/getInfos.php?type=getReglesByID&typeReq=ue&id=" + ue.getId();
													final ArrayList<Regle> listeReglesUE = new ArrayList<Regle>();
													
													class GetAllRegleIDUE extends AsyncTask<Void, Void, Void> {
														private String pathUrl;
														
														public GetAllRegleIDUE(String pathUrl) {
															this.pathUrl = pathUrl;
														}

														protected Void doInBackground(Void... arg0) {
															try {
																if((contenuPage = WebUtils.getPage(pathUrl)) != "-1");

																else {
																	AjouterAnneeE.this.runOnUiThread(new Runnable() {
																		public void run() {
																			AlertDialog.Builder builder = new AlertDialog.Builder(AjouterAnneeE.this);
																			builder.setMessage("Erreur - Vérifiez votre connexion");
																			builder.setCancelable(false);
																			builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
																				public void onClick(DialogInterface dialog, int which) {
																					AjouterAnneeE.this.finish();
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
															
															StringTokenizer chaineEclate = new StringTokenizer(contenuPage, ";");

															final String[] temp = new String[chaineEclate.countTokens()];

															if (temp.length != 1) {
																int i = 0;

																while(chaineEclate.hasMoreTokens()) {
																	temp[i++] = chaineEclate.nextToken();	
																}
																
																Regle r = null;
																
																for(int s = 0; s < temp.length;) {
																	r = new Regle();
																	r.setId(Integer.parseInt(temp[s].replaceAll(" ", "")));
																	r.setRegle(temp[s+1].replaceAll(" ", ""));
																	r.setOperateur(temp[s+2].replaceAll(" ", ""));
																	r.setValeur(temp[s+3].replaceAll(" ", ""));
																		
																	listeReglesUE.add(r);
																	
																	s=s+4;
																}
															}
															
															ue.setListeRegles(listeReglesUE);

															return null;
														}
													}
													
													new GetAllRegleIDUE(urlGetAllRegleUE).execute();
													
													for (int y = 0; y < listeUE.size(); y++) {
														if (ue.getId() == listeUE.get(y).getId()) {
															isPresent = true;
														}	
													}
													
													if (!isPresent) {
														listeUE.add(ue);
													}
												} 
											}
											
											an.setListeUE(listeUE);
											
											if (listeUE.size() == 0) {
												toast.setText("L'année n'est pas complète (0 UE sur l'année).");
												toast.show();
												
												return null;
											}
											
											listeMatiere.clear();
											
											for (int r = 0; r < temp2.length; r++) {
												StringTokenizer chaineEclate = new StringTokenizer(temp2[r], ";");

												final String[] temp = new String[chaineEclate.countTokens()];

												if (temp.length != 1) {
													int g = 0;

													while(chaineEclate.hasMoreTokens()) {
														temp[g++] = chaineEclate.nextToken();	
													}

													final Matiere m = new Matiere();
													m.setId(Integer.parseInt(temp[3].replaceAll(" ", "")));
													m.setNom(temp[4]);
													m.setCoefficient(Integer.parseInt(temp[5].replaceAll(" ", "")));
													m.setIsOption((temp[6].equals("1")) ? true : false);
													
													final String urlGetAllRegleMat = "http://www.jordi-charpentier.com/jmd/mobile/getInfos.php?type=getReglesByID&typeReq=matiere&id=" + m.getId();
													final ArrayList<Regle> listeReglesMat = new ArrayList<Regle>();
													
													class GetAllRegleIDMat extends AsyncTask<Void, Void, Void> {
														private String pathUrl;
														
														public GetAllRegleIDMat(String pathUrl) {
															this.pathUrl = pathUrl;
														}

														protected Void doInBackground(Void... arg0) {
															try {
																if((contenuPage = WebUtils.getPage(pathUrl)) != "-1");

																else {
																	AjouterAnneeE.this.runOnUiThread(new Runnable() {
																		public void run() {
																			AlertDialog.Builder builder = new AlertDialog.Builder(AjouterAnneeE.this);
																			builder.setMessage("Erreur - Vérifiez votre connexion");
																			builder.setCancelable(false);
																			builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
																				public void onClick(DialogInterface dialog, int which) {
																					AjouterAnneeE.this.finish();
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
															
															StringTokenizer chaineEclate = new StringTokenizer(contenuPage, ";");

															final String[] temp = new String[chaineEclate.countTokens()];

															if (temp.length != 1) {
																int i = 0;

																while(chaineEclate.hasMoreTokens()) {
																	temp[i++] = chaineEclate.nextToken();	
																}
																
																Regle r = null;
																
																for(int s = 0; s < temp.length;) {
																	r = new Regle();
																	r.setId(Integer.parseInt(temp[s].replaceAll(" ", "")));
																	r.setRegle(temp[s+1].replaceAll(" ", ""));
																	r.setOperateur(temp[s+2].replaceAll(" ", ""));
																	r.setValeur(temp[s+3].replaceAll(" ", ""));
																		
																	listeReglesMat.add(r);
																	
																	s=s+4;
																}
															}
															
															m.setListeRegles(listeReglesMat);

															return null;
														}
													}
													
													new GetAllRegleIDMat(urlGetAllRegleMat).execute();
													
													for (int n = 0; n < listeUE.size(); n++) {
														if (Integer.parseInt(temp[0].replaceAll(" ", "")) == listeUE.get(n).getId()) {
															ArrayList<Matiere> tmp = listeUE.get(n).getListeMatieres();
															tmp.add(m);
															
															listeUE.get(n).setListeMatieres(tmp);
															
															if (tmp.size() == 0) {
																toast.setText("L'année n'est pas complète (0 matière sur l'UE " + listeUE.get(n).getNom() + ").");
																toast.show();
																
																return null;
															}
														}
													}
												}
											}
										}
										
										int pos = 0;

										for (int b = 0; b < etud.getListeDiplomes().size(); b++) {
											if (etud.getListeDiplomes().get(b).getId() == idDiplome) {
												pos = b;
											}
										}
										
										ArrayList<Diplome> lD = etud.getListeDiplomes();
										ArrayList<Annee> listeAnnees = etud.getListeDiplomes().get(pos).getListeAnnees();
										
										boolean error = false;
										
										for(int o = 0; o < listeAnnees.size(); o++) {
											if (listeAnnees.get(o).getId() == an.getId()) {
												error = true;
											}
										}
										
										if (!error) {
											listeAnnees.add(an);
											
											Diplome d = etud.getListeDiplomes().get(pos);
											d.setListeAnnees(listeAnnees);

											lD.remove(pos);
											lD.add(d);

											etud.setListeDiplomes(lD);
										} else {
											toast.setText("L'année a déjà été ajoutée au diplôme.");
											toast.show();
											
											return null;
										}
										
										if (EtudiantDAO.save(etud)) {
											finish();
										} else {
											toast.setText("Erreur lors de la sauvegarde de l'année.");
											toast.show();
											
											return null;
										}

										return null;
									}
								}

								new GetAllByIDAnnee(urlGetUEByIdAnnee, an).execute();
							}
						});
					} else {
						final ArrayList<HashMap<String, String>> listItem = new ArrayList<HashMap<String, String>>();
						HashMap<String, String> map;

						map = new HashMap<String, String>();

						map.put("titre", "Aucun résultat.");

						listItem.add(map);		

						final SimpleAdapter mSchedule = new SimpleAdapter (getBaseContext(), listItem, R.layout.etudiant_ajouter_annees_empty_list, new String[] {"titre"}, new int[] {R.id.titre});

						liste.setAdapter(mSchedule); 
					}
				}});

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