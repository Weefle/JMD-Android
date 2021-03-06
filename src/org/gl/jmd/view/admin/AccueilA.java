package org.gl.jmd.view.admin;

import java.io.*;
import java.util.*;

import org.apache.http.HttpResponse;
import org.apache.http.client.*;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.gl.jmd.Constantes;
import org.gl.jmd.R;
import org.gl.jmd.utils.FileUtils;
import org.gl.jmd.view.*;
import org.gl.jmd.view.admin.create.*;
import org.gl.jmd.view.admin.listing.*;

import com.google.android.gcm.GCMRegistrar;

import android.app.*;
import android.content.*;
import android.content.res.Configuration;
import android.graphics.*;
import android.os.*;
import android.support.v4.widget.DrawerLayout;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TabHost.*;

/**
 * Activité correspondant à la vue d'acceuil de l'administrateur.
 * 
 * @author Jordi CHARPENTIER & Yoann VANHOESERLANDE
 */
public class AccueilA extends TabActivity {

	private TabHost tabHost;

	private int currentTab = 0;
	
	private Activity activity;
	
	private Toast toast;
	
	private ImageView buttonAdd = null;
	
	private TextView tvTitre = null;

	private String regId = "";
	
	private long back_pressed;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.administrateur_accueil);
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
		
		activity = this;
		toast = Toast.makeText(activity, "", Toast.LENGTH_SHORT);
		
		tabHost = getTabHost();
		
		initSlideMenu();
		initTabs();
		initTabHost();
		initElements();
		
		checkIfRegisterDevice();
	}
	
	private void checkIfRegisterDevice() {
		if (!Constantes.FILE_RECEIVE_NOTIF.exists()) {
			AlertDialog.Builder confirmRegister = new AlertDialog.Builder(AccueilA.this);
			confirmRegister.setTitle("Notification");
			confirmRegister.setMessage("Voulez-vous recevoir des notifications de JMD ?");
			confirmRegister.setCancelable(false);
			confirmRegister.setPositiveButton("Oui", new AlertDialog.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					ProgressDialog progress = new ProgressDialog(activity);
					progress.setMessage("Chargement...");
					new RegisterGCM(progress).execute(); 	
				}
			});
			
			confirmRegister.setNegativeButton("Non", new AlertDialog.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					FileUtils.writeFile("0", Constantes.FILE_RECEIVE_NOTIF);
				}
			});
			
			confirmRegister.show();
		}
	}
	
	private void initTabs() {
		setupTab("Favori", "0", new Intent().setClass(this, FavoriA.class), 0);
        setupTab("Etablissements", "1", new Intent().setClass(this, ListeEtablissementA.class), 1);
        setupTab("Diplômes", "2", new Intent().setClass(this, ListeDiplomeA.class), 2);
	}
	
	private void initElements() {
		buttonAdd = (ImageView) findViewById(R.id.admin_accueil_bout_modifier);
		buttonAdd.setVisibility(View.GONE);
		
		tvTitre = (TextView) findViewById(R.id.admin_accueil_bienvenue);
		tvTitre.setText("Favori");
		
		// Tabs.
		TextView tv = (TextView) tabHost.getTabWidget().getChildAt(0).findViewById(R.id.tabsText);
		tv.setTextColor(Color.parseColor("#FF5E3A"));
		tv.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.star_or, 0, 0);
		tv.setTextSize(13);
		
		SpannableString spanString = new SpannableString("Favori");
		spanString.setSpan(new StyleSpan(Typeface.BOLD), 0, spanString.length(), 0);
		
		tv.setText(spanString);
		
		TextView tv2 = (TextView) tabHost.getTabWidget().getChildAt(1).findViewById(R.id.tabsText);
		tv2.setTextColor(Color.parseColor("#FFFFFF"));
		tv2.setTextSize(13);
		
		TextView tv3 = (TextView) tabHost.getTabWidget().getChildAt(2).findViewById(R.id.tabsText);
		tv3.setTextColor(Color.parseColor("#FFFFFF"));
		tv3.setTextSize(13);
	}
	
	private void initTabHost() {
		tabHost.getTabWidget().getChildAt(0).getLayoutParams().height = 125;
		tabHost.getTabWidget().getChildAt(0).setBackgroundDrawable(getResources().getDrawable(R.drawable.tab_bg_selector)); 
		tabHost.getTabWidget().getChildAt(0).setSelected(true);

		tabHost.getTabWidget().getChildAt(1).getLayoutParams().height = 125;
		tabHost.getTabWidget().getChildAt(1).setBackgroundDrawable(getResources().getDrawable(R.drawable.tab_bg_selector)); 
		
		tabHost.getTabWidget().getChildAt(2).getLayoutParams().height = 125;
		tabHost.getTabWidget().getChildAt(2).setBackgroundDrawable(getResources().getDrawable(R.drawable.tab_bg_selector)); 
		
		tabHost.setOnTabChangedListener(new OnTabChangeListener(){
			@Override
			public void onTabChanged(String tabId) {	
				TextView tv = (TextView) tabHost.getTabWidget().getChildAt(0).findViewById(R.id.tabsText);
				TextView tv2 = (TextView) tabHost.getTabWidget().getChildAt(1).findViewById(R.id.tabsText);
				TextView tv3 = (TextView) tabHost.getTabWidget().getChildAt(2).findViewById(R.id.tabsText);
				
				SpannableString spanString = new SpannableString("Favori");
				SpannableString spanString2 = new SpannableString("Etablissements");
				SpannableString spanString3 = new SpannableString("Diplômes");
				
				if (tabId.equals("0")) {
					buttonAdd.setVisibility(View.GONE);
					currentTab = 0;
					tvTitre.setText("Favori");
					
					spanString.setSpan(new StyleSpan(Typeface.BOLD), 0, spanString.length(), 0);
					
					tv.setTextColor(Color.parseColor("#FF5E3A"));
					tv.setText(spanString);
					tv.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.star_or, 0, 0);
					
					spanString2.setSpan(new StyleSpan(Typeface.NORMAL), 0, spanString2.length(), 0);
					
					tv2.setTextColor(Color.parseColor("#FFFFFF"));
					tv2.setText(spanString2);
					tv2.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.university, 0, 0);
					
					spanString3.setSpan(new StyleSpan(Typeface.NORMAL), 0, spanString3.length(), 0);
					
					tv3.setTextColor(Color.parseColor("#FFFFFF"));
					tv3.setText(spanString3);
					tv3.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.diploma, 0, 0);
				} else if (tabId.equals("1")) {
					buttonAdd.setVisibility(View.VISIBLE);
					currentTab = 1;
					tvTitre.setText("Etablissements");

					spanString.setSpan(new StyleSpan(Typeface.NORMAL), 0, spanString.length(), 0);

					tv.setTextColor(Color.parseColor("#FFFFFF"));
					tv.setText(spanString);
					tv.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.star, 0, 0);

					spanString2.setSpan(new StyleSpan(Typeface.BOLD), 0, spanString2.length(), 0);

					tv2.setTextColor(Color.parseColor("#FF5E3A"));
					tv2.setText(spanString2);
					tv2.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.university_or, 0, 0);
					
					spanString3.setSpan(new StyleSpan(Typeface.NORMAL), 0, spanString3.length(), 0);
					
					tv3.setTextColor(Color.parseColor("#FFFFFF"));
					tv3.setText(spanString3);
					tv3.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.diploma, 0, 0);
				} else if (tabId.equals("2")) {
					buttonAdd.setVisibility(View.VISIBLE);
					currentTab = 2;
					tvTitre.setText("Diplômes");
					
					spanString.setSpan(new StyleSpan(Typeface.NORMAL), 0, spanString.length(), 0);

					tv.setTextColor(Color.parseColor("#FFFFFF"));
					tv.setText(spanString);
					tv.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.star, 0, 0);
					
					spanString2.setSpan(new StyleSpan(Typeface.NORMAL), 0, spanString2.length(), 0);
					
					tv2.setTextColor(Color.parseColor("#FFFFFF"));
					tv2.setText(spanString2);
					tv2.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.university, 0, 0);
					
					spanString3.setSpan(new StyleSpan(Typeface.BOLD), 0, spanString3.length(), 0);

					tv3.setTextColor(Color.parseColor("#FF5E3A"));
					tv3.setText(spanString3);
					tv3.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.diploma_or, 0, 0);
				}
			}}
		);
	}
	
	private void initSlideMenu() {		
		final DrawerLayout dLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        final ListView dList = (ListView) findViewById(R.id.left_drawer);

        final ArrayList<HashMap<String, String>> listItem = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> map;
		
		map = new HashMap<String, String>();
		map.put("titre", "Nommer un administrateur");
		listItem.add(map);
		
		map = new HashMap<String, String>();
		map.put("titre", "Clôturer ce compte");
		listItem.add(map);
		
		map = new HashMap<String, String>();
		map.put("titre", "Notifications");
		listItem.add(map);
		
		map = new HashMap<String, String>();
		map.put("titre", "Mails");
		listItem.add(map);
		
		map = new HashMap<String, String>();
		map.put("titre", "Déconnexion");
		listItem.add(map);
        
        dList.setAdapter(new SimpleAdapter(getBaseContext(), listItem, R.layout.slide_menu_simple_list, new String[] {"titre"}, new int[] {R.id.titre}));
		
        dList.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
				dLayout.closeDrawers();	
				
				if (listItem.get(position).get("titre").equals("Déconnexion")) {
					ProgressDialog progress = new ProgressDialog(activity);
					progress.setMessage("Chargement...");
					new SeDeco(progress, Constantes.URL_SERVER + "admin/logout" +
							"?token=" + FileUtils.readFile(Constantes.FILE_TOKEN) + 
							"&pseudo=" + FileUtils.readFile(Constantes.FILE_PSEUDO) +
							"&timestamp=" + new java.util.Date().getTime()).execute(); 
        		} else if (listItem.get(position).get("titre").equals("Nommer un administrateur")) {
        			startActivity(new Intent(AccueilA.this, AjouterAdminA.class));			
        		} else if (listItem.get(position).get("titre").equals("Clôturer ce compte")) {
        			AlertDialog.Builder confirmSuppr = new AlertDialog.Builder(AccueilA.this);
					confirmSuppr.setTitle("Suppression");
					confirmSuppr.setMessage("Voulez-vous vraiment supprimer votre compte ?");
					confirmSuppr.setCancelable(false);
					confirmSuppr.setPositiveButton("Oui", new AlertDialog.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							ProgressDialog progress = new ProgressDialog(activity);
							progress.setMessage("Chargement...");
							new CloturerCompte(progress, Constantes.URL_SERVER + "admin/closeAdminAccount" +
									"?token=" + FileUtils.readFile(Constantes.FILE_TOKEN) + 
									"&pseudo=" + FileUtils.readFile(Constantes.FILE_PSEUDO) +
									"&timestamp=" + new java.util.Date().getTime()).execute(); 		
						}
					});
					
					confirmSuppr.setNegativeButton("Non", null);
					confirmSuppr.show();
        		} else if (listItem.get(position).get("titre").equals("Notifications")) {
        			final CharSequence[] items = {"Recevoir les notifications", "Ne pas recevoir les notifications"};
        			final int pos = (FileUtils.readFile(Constantes.FILE_RECEIVE_NOTIF).equals("0") ? 1 : 0);        			

        			final AlertDialog.Builder notifD = new AlertDialog.Builder(activity);
        			notifD.setTitle("Notifications natives");
        			notifD.setSingleChoiceItems(items, pos, new DialogInterface.OnClickListener() {
        				public void onClick(DialogInterface dialog, int item) {
        					ProgressDialog progress = new ProgressDialog(activity);
        					progress.setMessage("Chargement...");

        					switch(item) {
	        					case 0:
	        						if (FileUtils.readFile(Constantes.FILE_RECEIVE_NOTIF).equals("0")) {
	        							new RegisterGCM(progress).execute(); 	
	        						}
	
	        						break;
	        					case 1:
	        						if (!FileUtils.readFile(Constantes.FILE_RECEIVE_NOTIF).equals("0")) {
	        							new UnregisterGCM(progress).execute(); 		
	        						}
	        						
	        						break;
        					}  
        					
        					dialog.cancel();
        				}
        			});	

        			notifD.show();
        		} else if (listItem.get(position).get("titre").equals("Mails")) {
        			ProgressDialog progress = new ProgressDialog(activity);
					progress.setMessage("Chargement...");
					new GetIfMailAccepted(progress, Constantes.URL_SERVER + "admin/isMailAccepted" +
							"?token=" + FileUtils.readFile(Constantes.FILE_TOKEN) + 
							"&pseudo=" + FileUtils.readFile(Constantes.FILE_PSEUDO) +
							"&timestamp=" + new java.util.Date().getTime()).execute(); 
        		}
			}
        });
	}
	
    private void setupTab(String name, String tag, Intent intent, int i) {
		tabHost.addTab(tabHost.newTabSpec(tag)
				.setIndicator(createTabView(tabHost.getContext(), name, i))
				.setContent(intent));
	}
 
	private View createTabView(final Context context, final String text, int i) {
		View view = LayoutInflater.from(context).inflate(R.layout.tab_item, null);
		
		TextView tv = (TextView) view.findViewById(R.id.tabsText);
		tv.setText(text);
		tv.setCompoundDrawablePadding(2);
		
		if (i == 0) {
			tv.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.star, 0, 0);
		} else if (i == 1) {
			tv.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.university, 0, 0);
		} else if (i == 2) {
			tv.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.diploma, 0, 0);
		}
 
		return view;
	}
	
	public void navigateToAccueil(View view) {
		finish();
		startActivity(new Intent(AccueilA.this, InitApp.class));		
	}

	/**
	 * Méthode permettant de faire apparaitre la popup lors du click sur le bouton "plus".
	 * 
	 * @param view La vue lors du click sur le bouton "plus".
	 */
	public void create(View view) {
		if (currentTab == 1) {
			startActivity(new Intent(AccueilA.this, CreationEtablissement.class));	
		} else if (currentTab == 2) {
			startActivity(new Intent(AccueilA.this, CreationDiplome.class));	
		}
	}
	
	private void setRegId(String regId) {
		this.regId = regId;
		
		ProgressDialog progress = new ProgressDialog(activity);
		progress.setMessage("Chargement...");
		new RegisterDevice(progress, Constantes.URL_SERVER + "admin/registerAndroidDevice" +
				"?idGCM=" + this.regId +
				"&token=" + FileUtils.readFile(Constantes.FILE_TOKEN) + 
				"&pseudo=" + FileUtils.readFile(Constantes.FILE_PSEUDO) +
				"&timestamp=" + new java.util.Date().getTime()).execute(); 
	}
	
	/* Classes internes. */
	
	private class AcceptMail extends AsyncTask<Void, Void, Void> {
		private ProgressDialog progress;
		private String pathUrl;

		public AcceptMail(ProgressDialog progress, String pathUrl) {
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
		        	toast.setText("Mis à jour.");
		        	toast.show();
		        } else if (response.getStatusLine().getStatusCode() == 401) {
		        	Constantes.FILE_PSEUDO.delete();
					Constantes.FILE_TOKEN.delete();
					
					activity.finishAffinity();
		        	startActivity(new Intent(AccueilA.this, Accueil.class));	
		        	
		        	toast.setText("Erreur. Redirection vers l'accueil.");	
					toast.show();
		        } else if (response.getStatusLine().getStatusCode() == 500) {
		        	toast.setText("Une erreur est survenue au niveau de la BDD.");	
					toast.show();
		        } else {
		        	toast.setText("Erreur inconnue. Veuillez réessayer.");	
					toast.show();
		        }
		    } catch (ClientProtocolException e) {
		    	AccueilA.this.runOnUiThread(new Runnable() {
					public void run() {
						AlertDialog.Builder builder = new AlertDialog.Builder(AccueilA.this);
						builder.setMessage("Erreur - Vérifiez votre connexion");
						builder.setCancelable(false);
						builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								AccueilA.this.finish();
							}
						});

						AlertDialog error = builder.create();
						error.show();
					}
				});
		    } catch (IOException e) {
		    	AccueilA.this.runOnUiThread(new Runnable() {
					public void run() {
						AlertDialog.Builder builder = new AlertDialog.Builder(AccueilA.this);
						builder.setMessage("Erreur - Vérifiez votre connexion");
						builder.setCancelable(false);
						builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								AccueilA.this.finish();
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
	
	private class GetIfMailAccepted extends AsyncTask<Void, Void, Void> {
		private ProgressDialog progress;
		private String pathUrl;

		public GetIfMailAccepted(ProgressDialog progress, String pathUrl) {
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
		        final HttpResponse response = httpclient.execute(httpGet);

		        if (response.getStatusLine().getStatusCode() == 200) {
		        	String reponse = EntityUtils.toString(response.getEntity());
		        	
		        	final boolean isMailAccepted = Boolean.parseBoolean(reponse);
		        	
		        	AccueilA.this.runOnUiThread(new Runnable() {
		        		public void run() {    		
		        			final CharSequence[] items = {"Recevoir les mails", "Ne pas recevoir les mails"};
		        			final int pos = (isMailAccepted ? 0 : 1);        			

		        			final AlertDialog.Builder notifD = new AlertDialog.Builder(activity);
		        			notifD.setTitle("Notifications par mail");
		        			notifD.setSingleChoiceItems(items, pos, new DialogInterface.OnClickListener() {
		        				public void onClick(DialogInterface dialog, int item) {
		        					ProgressDialog progress = new ProgressDialog(activity);
		        					progress.setMessage("Chargement...");

		        					switch(item) {
		        						case 0:
		        							new AcceptMail(progress, Constantes.URL_SERVER + "admin/acceptMail" +
		        								"?newValue=" + true +
		        								"&token=" + FileUtils.readFile(Constantes.FILE_TOKEN) + 
		        								"&pseudo=" + FileUtils.readFile(Constantes.FILE_PSEUDO) +
		        								"&timestamp=" + new java.util.Date().getTime()).execute(); 

		        							break;
		        						case 1:
		        							new AcceptMail(progress, Constantes.URL_SERVER + "admin/acceptMail" +
			        							"?newValue=" + false +
			        							"&token=" + FileUtils.readFile(Constantes.FILE_TOKEN) + 
			        							"&pseudo=" + FileUtils.readFile(Constantes.FILE_PSEUDO) +
			        							"&timestamp=" + new java.util.Date().getTime()).execute(); 

		        							break;
		        					}  

		        					dialog.cancel();
		        				}
		        			});	

		        			notifD.show();
		        		}
		        	});
		        } else if (response.getStatusLine().getStatusCode() == 401) {
		        	Constantes.FILE_PSEUDO.delete();
					Constantes.FILE_TOKEN.delete();

		        	activity.finishAffinity();
		        	startActivity(new Intent(AccueilA.this, Accueil.class));	

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
		    	AccueilA.this.runOnUiThread(new Runnable() {
					public void run() {
						AlertDialog.Builder builder = new AlertDialog.Builder(AccueilA.this);
						builder.setMessage("Erreur - Vérifiez votre connexion");
						builder.setCancelable(false);
						builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								AccueilA.this.finish();
							}
						});

						AlertDialog error = builder.create();
						error.show();
					}
				});
		    } catch (IOException e) {
		    	AccueilA.this.runOnUiThread(new Runnable() {
					public void run() {
						AlertDialog.Builder builder = new AlertDialog.Builder(AccueilA.this);
						builder.setMessage("Erreur - Vérifiez votre connexion");
						builder.setCancelable(false);
						builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								AccueilA.this.finish();
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
	
	private class RegisterGCM extends AsyncTask<Void, Void, Void> {
		private ProgressDialog progress;

		public RegisterGCM(ProgressDialog progress) {
			this.progress = progress;
		}

		public void onPreExecute() {
			progress.show();
		}

		public void onPostExecute(Void unused) {
			progress.dismiss();    
		}

		protected Void doInBackground(Void... arg0) {
			GCMRegistrar.checkDevice(getApplicationContext());
			
			GCMRegistrar.checkManifest(getApplicationContext());

			if (GCMRegistrar.getRegistrationId(getApplicationContext()).equals("")) {
				GCMRegistrar.register(getApplicationContext(), "1029438593914");
			} 	
			
			AccueilA.this.runOnUiThread(new Runnable() {
				public void run() {
					Handler handler = new Handler();
					handler.postDelayed(new Runnable() {
						public void run() {
							setRegId(GCMRegistrar.getRegistrationId(getApplicationContext()));
						}
					}, 3000);
				}
			});
			
			return null;
		}
	}
	
	private class UnregisterGCM extends AsyncTask<Void, Void, Void> {
		private ProgressDialog progress;

		public UnregisterGCM(ProgressDialog progress) {
			this.progress = progress;
		}

		public void onPreExecute() {
			progress.show();
		}

		public void onPostExecute(Void unused) {
			progress.dismiss();    
		}

		protected Void doInBackground(Void... arg0) {
			GCMRegistrar.checkDevice(getApplicationContext());
			GCMRegistrar.unregister(getApplicationContext());
			GCMRegistrar.checkManifest(getApplicationContext());

			AccueilA.this.runOnUiThread(new Runnable() {
				public void run() {
					Handler handler = new Handler();
					handler.postDelayed(new Runnable() {
						public void run() {
							ProgressDialog progress = new ProgressDialog(activity);
							progress.setMessage("Chargement...");
							new UnregisterDevice(progress, Constantes.URL_SERVER + "admin/unregisterAndroidDevice" +
									"?idGCM=" + FileUtils.readFile(Constantes.FILE_RECEIVE_NOTIF) +
									"&token=" + FileUtils.readFile(Constantes.FILE_TOKEN) + 
									"&pseudo=" + FileUtils.readFile(Constantes.FILE_PSEUDO) +
									"&timestamp=" + new java.util.Date().getTime()).execute(); 
						}
					}, 3000);
				}
			});
			
			return null;
		}
	}
	
	private class RegisterDevice extends AsyncTask<Void, Void, Void> {
		private ProgressDialog progress;
		private String pathUrl;

		public RegisterDevice(ProgressDialog progress, String pathUrl) {
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
		        	toast.setText("Appareil enregistré.");
		        	toast.show();
		        	
		        	FileUtils.writeFile(regId, Constantes.FILE_RECEIVE_NOTIF);
		        } else if (response.getStatusLine().getStatusCode() == 401) {
					activity.finishAffinity();
		        	startActivity(new Intent(AccueilA.this, Accueil.class));	
		        	
		        	toast.setText("Erreur. Redirection vers l'accueil.");	
					toast.show();
		        } else if (response.getStatusLine().getStatusCode() == 500) {
		        	toast.setText("Une erreur est survenue au niveau de la BDD.");	
					toast.show();
		        } else if (response.getStatusLine().getStatusCode() == 403) {
		        	toast.setText("Erreur. Appareil déjà enregistré.");	
					toast.show();
		        } else {
		        	toast.setText("Erreur inconnue. Veuillez réessayer.");	
		        	toast.show();
		        }
		    } catch (ClientProtocolException e) {
		    	AccueilA.this.runOnUiThread(new Runnable() {
					public void run() {
						AlertDialog.Builder builder = new AlertDialog.Builder(AccueilA.this);
						builder.setMessage("Erreur - Vérifiez votre connexion");
						builder.setCancelable(false);
						builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								AccueilA.this.finish();
							}
						});

						AlertDialog error = builder.create();
						error.show();
					}
				});
		    } catch (IOException e) {
		    	AccueilA.this.runOnUiThread(new Runnable() {
					public void run() {
						AlertDialog.Builder builder = new AlertDialog.Builder(AccueilA.this);
						builder.setMessage("Erreur - Vérifiez votre connexion");
						builder.setCancelable(false);
						builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								AccueilA.this.finish();
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
	
	private class UnregisterDevice extends AsyncTask<Void, Void, Void> {
		private ProgressDialog progress;
		private String pathUrl;

		public UnregisterDevice(ProgressDialog progress, String pathUrl) {
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
		        	AccueilA.this.runOnUiThread(new Runnable() {
						public void run() {
							FileUtils.writeFile("0", Constantes.FILE_RECEIVE_NOTIF);
							
				        	toast.setText("Appareil supprimé.");
				        	toast.show();
						}
					});
		        } else if (response.getStatusLine().getStatusCode() == 401) {
					activity.finishAffinity();
		        	startActivity(new Intent(AccueilA.this, Accueil.class));	
		        	
		        	toast.setText("Erreur. Redirection vers l'accueil.");	
					toast.show();
		        } else if (response.getStatusLine().getStatusCode() == 500) {
		        	toast.setText("Une erreur est survenue au niveau de la BDD.");	
					toast.show();
		        } else {
		        	toast.setText("Erreur inconnue. Veuillez réessayer.");	
					toast.show();
		        }
		    } catch (ClientProtocolException e) {
		    	AccueilA.this.runOnUiThread(new Runnable() {
					public void run() {
						AlertDialog.Builder builder = new AlertDialog.Builder(AccueilA.this);
						builder.setMessage("Erreur - Vérifiez votre connexion");
						builder.setCancelable(false);
						builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								AccueilA.this.finish();
							}
						});

						AlertDialog error = builder.create();
						error.show();
					}
				});
		    } catch (IOException e) {
		    	AccueilA.this.runOnUiThread(new Runnable() {
					public void run() {
						AlertDialog.Builder builder = new AlertDialog.Builder(AccueilA.this);
						builder.setMessage("Erreur - Vérifiez votre connexion");
						builder.setCancelable(false);
						builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								AccueilA.this.finish();
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
	
	private class SeDeco extends AsyncTask<Void, Void, Void> {
		private ProgressDialog progress;
		private String pathUrl;

		public SeDeco(ProgressDialog progress, String pathUrl) {
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
		        	Constantes.FILE_PSEUDO.delete();
		        	Constantes.FILE_PARAM.delete();

					finish();
					startActivity(new Intent(AccueilA.this, ConnexionA.class));		
		        	
		        	toast.setText("Déconnecté.");
		        	toast.show();
		        } else if (response.getStatusLine().getStatusCode() == 401) {
					activity.finishAffinity();
		        	startActivity(new Intent(AccueilA.this, Accueil.class));	
		        	
		        	toast.setText("Erreur. Redirection vers l'accueil.");	
					toast.show();
		        } else if (response.getStatusLine().getStatusCode() == 500) {
		        	toast.setText("Une erreur est survenue au niveau de la BDD.");	
					toast.show();
		        } else {
		        	toast.setText("Erreur inconnue. Veuillez réessayer.");	
					toast.show();
		        }
		    } catch (ClientProtocolException e) {
		    	AccueilA.this.runOnUiThread(new Runnable() {
					public void run() {
						AlertDialog.Builder builder = new AlertDialog.Builder(AccueilA.this);
						builder.setMessage("Erreur - Vérifiez votre connexion");
						builder.setCancelable(false);
						builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								AccueilA.this.finish();
							}
						});

						AlertDialog error = builder.create();
						error.show();
					}
				});
		    } catch (IOException e) {
		    	AccueilA.this.runOnUiThread(new Runnable() {
					public void run() {
						AlertDialog.Builder builder = new AlertDialog.Builder(AccueilA.this);
						builder.setMessage("Erreur - Vérifiez votre connexion");
						builder.setCancelable(false);
						builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								AccueilA.this.finish();
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
	
	private class CloturerCompte extends AsyncTask<Void, Void, Void> {
		private ProgressDialog progress;
		private String pathUrl;

		public CloturerCompte(ProgressDialog progress, String pathUrl) {
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
		        	Constantes.FILE_PSEUDO.delete();
		        	Constantes.FILE_PARAM.delete();

					finish();
					startActivity(new Intent(AccueilA.this, ConnexionA.class));		
		        	
		        	toast.setText("Votre compte a bien été supprimé.");
		        	toast.show();
		        } else if (response.getStatusLine().getStatusCode() == 401) {
					activity.finishAffinity();
		        	startActivity(new Intent(AccueilA.this, Accueil.class));	
		        	
		        	toast.setText("Erreur.");	
					toast.show();
		        } else if (response.getStatusLine().getStatusCode() == 500) {
		        	toast.setText("Une erreur est survenue au niveau de la BDD.");	
					toast.show();
		        } else {
		        	toast.setText("Erreur inconnue. Veuillez réessayer.");	
					toast.show();
		        }
		    } catch (ClientProtocolException e) {
		    	AccueilA.this.runOnUiThread(new Runnable() {
					public void run() {
						AlertDialog.Builder builder = new AlertDialog.Builder(AccueilA.this);
						builder.setMessage("Erreur - Vérifiez votre connexion");
						builder.setCancelable(false);
						builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								AccueilA.this.finish();
							}
						});

						AlertDialog error = builder.create();
						error.show();
					}
				});
		    } catch (IOException e) {
		    	AccueilA.this.runOnUiThread(new Runnable() {
					public void run() {
						AlertDialog.Builder builder = new AlertDialog.Builder(AccueilA.this);
						builder.setMessage("Erreur - Vérifiez votre connexion");
						builder.setCancelable(false);
						builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								AccueilA.this.finish();
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
	 * Méthode déclenchée lors d'un click sur le bouton virtuel Android de retour.
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
	 * Méthode permettant d'empécher la reconstruction de la vue lors de la rotation de l'écran. 
	 * 
	 * @param newConfig L'état de la vue avant la rotation.
	 */
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}
}