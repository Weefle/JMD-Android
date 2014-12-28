package org.gl.jmd.view.admin.create;

import java.io.*;
import java.net.URLEncoder;

import org.apache.http.HttpResponse;
import org.apache.http.client.*;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;
import org.gl.jmd.*;
import org.gl.jmd.model.*;
import org.gl.jmd.model.enumeration.DecoupageType;
import org.gl.jmd.model.enumeration.DecoupageYearType;
import org.gl.jmd.utils.*;
import org.gl.jmd.view.Accueil;

import android.app.*;
import android.content.*;
import android.content.res.Configuration;
import android.os.*;
import android.view.View;
import android.widget.*;
import android.widget.AdapterView.OnItemSelectedListener;

/**
 * Activité correspondant à la vue de création d'une UE.
 * 
 * @author Jordi CHARPENTIER & Yoann VANHOESERLANDE
 */
public class CreationUE extends Activity {
	
	private Activity activity;

	private Toast toast;
	
	private Annee a = null;
	
	private DecoupageYearType decoupage = null;
	
	private EditText NOM;
	
	private EditText MOYENNE_MINI;
	
	private EditText NB_OPTION_MINI;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.administrateur_creation_ue);
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
		
		activity = this;
		toast = Toast.makeText(activity, "", Toast.LENGTH_SHORT);
		
		NOM = (EditText) findViewById(R.id.admin_creation_ue_nom);
		MOYENNE_MINI = (EditText) findViewById(R.id.admin_creation_ue_moyenne_mini);
		NB_OPTION_MINI = (EditText) findViewById(R.id.admin_creation_ue_nb_option);
		
		a = (Annee) getIntent().getExtras().getSerializable("annee");
		
		initListeDecoupage();
	}
	
	public void back(View view) {
		testBack();
	}
	
	private void testBack() {
		if (NOM.getText().toString().length() != 0) {
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
	
	private void initListeDecoupage() {
		Spinner spinner = (Spinner) findViewById(R.id.admin_creation_ue_spinner);
		
		ArrayAdapter<CharSequence> adapter = null;
		
		if (a.getDecoupage() == DecoupageType.SEMESTRE) {
			adapter = ArrayAdapter.createFromResource(this, R.array.position_ue_array_2, android.R.layout.simple_spinner_item);
		} else if (a.getDecoupage() == DecoupageType.TRIMESTRE) {
			adapter = ArrayAdapter.createFromResource(this, R.array.position_ue_array_1, android.R.layout.simple_spinner_item);
		} else {
			spinner.setVisibility(View.GONE);
		}
		
		if (adapter != null) {
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			
	        spinner.setAdapter(adapter);
	        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
	        	@Override
	        	public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
	        		if (parent.getItemAtPosition(pos).toString().equals("Trimestre 1")) {
	        			decoupage = DecoupageYearType.TRI1;
	        		} else if (parent.getItemAtPosition(pos).toString().equals("Trimestre 2")) {
	        			decoupage = DecoupageYearType.TRI2;
	        		} else if (parent.getItemAtPosition(pos).toString().equals("Trimestre 3")) {
	        			decoupage = DecoupageYearType.TRI3;
	        		} else if (parent.getItemAtPosition(pos).toString().equals("Semestre 1")) {
	        			decoupage = DecoupageYearType.SEM1;
	        		} else if (parent.getItemAtPosition(pos).toString().equals("Semestre 2")) {
	        			decoupage = DecoupageYearType.SEM2;
	        		}
	        	}
	        	
	        	@Override
	        	public void onNothingSelected(AdapterView<?> parent) {
	        		// Empty
	        	}
			});
		}
	}
	
	/**
	 * Méthode permettant de créer une UE (déclenchée lors d'un click sur le bouton "créer").
	 * 
	 * @param view La vue lors du click sur le bouton de création.
	 */
	public void creerUE(View view) {
		final EditText NOM = (EditText) findViewById(R.id.admin_creation_ue_nom);
		
		if (NOM.getText().toString().length() != 0) {			
			UE ue = new UE();
			ue.setNom(NOM.getText().toString());
			
			if (decoupage == null) {
				ue.setDecoupage(DecoupageYearType.NULL);
			} else {
				ue.setDecoupage(decoupage);
			}
			
			try {
				String URL = Constantes.URL_SERVER + "ue" +
						"?nom=" + URLEncoder.encode(ue.getNom(), "UTF-8") +
						"&yearType=" + ue.getDecoupage().name() +
						"&idAnnee=" + a.getId() +
						"&token=" + FileUtils.readFile(Constantes.FILE_TOKEN) + 
						"&pseudo=" + FileUtils.readFile(Constantes.FILE_PSEUDO) +
						"&timestamp=" + new java.util.Date().getTime();
				
				if (NB_OPTION_MINI.getText().toString().length() > 0) {
					URL += "&nbOptMini=" + NB_OPTION_MINI.getText().toString();
				}
				
				if (MOYENNE_MINI.getText().toString().length() > 0) {
					URL += "&noteMinimale=" + MOYENNE_MINI.getText().toString();
				}
				
				ProgressDialog progress = new ProgressDialog(activity);
				progress.setMessage("Chargement...");
				new CreerUE(progress, URL).execute(); 
			} catch (UnsupportedEncodingException ex) {
				toast.setText("Erreur d'encodage (UTF-8).");
				toast.show();
			}	
		} else {
			NOM.setBackgroundResource(R.drawable.border_edittext_error);
			
			toast.setText("Le champ \"Nom\" est vide.");
			toast.show();
		}
	}
	
	/* Classes internes. */

	private class CreerUE extends AsyncTask<Void, Void, Void> {
		private ProgressDialog progress;
		private String pathUrl;

		public CreerUE(ProgressDialog progress, String pathUrl) {
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
		        	toast.setText("UE créée.");
		        	toast.show();
		        	
		        	finish();
		        } else if (response.getStatusLine().getStatusCode() == 401) {
		        	Constantes.FILE_PSEUDO.delete();
		        	Constantes.FILE_TOKEN.delete();
		        	
					finishAffinity();
		        	startActivity(new Intent(CreationUE.this, Accueil.class));	
		        	
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
		    	CreationUE.this.runOnUiThread(new Runnable() {
					public void run() {
						AlertDialog.Builder builder = new AlertDialog.Builder(CreationUE.this);
						builder.setMessage("Erreur - Vérifiez votre connexion");
						builder.setCancelable(false);
						builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								CreationUE.this.finish();
							}
						});

						AlertDialog error = builder.create();
						error.show();
					}
				});
		    } catch (IOException e) {
		    	CreationUE.this.runOnUiThread(new Runnable() {
					public void run() {
						AlertDialog.Builder builder = new AlertDialog.Builder(CreationUE.this);
						builder.setMessage("Erreur - Vérifiez votre connexion");
						builder.setCancelable(false);
						builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								CreationUE.this.finish();
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
		testBack();
	}
}
