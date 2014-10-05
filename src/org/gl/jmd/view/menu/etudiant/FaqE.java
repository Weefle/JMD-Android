package org.gl.jmd.view.menu.etudiant;

import java.io.*;

import org.gl.jmd.R;
import org.gl.jmd.utils.WebUtils;

import android.net.*;
import android.os.*;
import android.view.View;
import android.webkit.*;
import android.app.*;
import android.content.*;
import android.content.res.Configuration;
import android.graphics.Color;

/**
 * Activité correspondant à la vue FAQ (visible par un étudiant).
 * 
 * @author Jordi CHARPENTIER & Yoann VANHOESERLANDE
 */
public class FaqE extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.etudiant_faq);
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
		
		initContent();
	}
	
	public void initContent() {
		File fileFAQ = new File(Environment.getExternalStorageDirectory().getPath() + "/cacheJMD/faqEtudiant.php");
		
		if (fileFAQ.exists()) {
			initWebviewWithCache();
		} else {
			initWebview();
		}
	}
	
	public void refresh(View view) {
		initWebview();
	}
	
	private boolean isNetworkAvailable() {
	    ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    
	    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
	
	private void initWebviewWithCache() {
		final ProgressDialog progress = ProgressDialog.show(FaqE.this, "", "Chargement...");
		final AlertDialog alertDialog = new AlertDialog.Builder(this).create();

		final WebView apercuSujetForum = (WebView) findViewById(R.id.etudiant_faq);
		apercuSujetForum.setBackgroundColor(Color.BLACK);

		apercuSujetForum.setWebViewClient(new WebViewClient() {
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);

				return true;
			}

			public void onPageFinished(WebView view, String url) {
				if(progress.isShowing()) progress.dismiss();
			}

			public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
				alertDialog.setTitle("Erreur");
				alertDialog.setMessage(description);
				alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						FaqE.this.finish();

						return;
					}
				});

				alertDialog.show();
			}
		});	

		apercuSujetForum.loadUrl("file:///" + Environment.getExternalStorageDirectory().getPath() + "/cacheJMD/faqEtudiant.php");
	}
	
	private void initWebview() {
		if (isNetworkAvailable()) {
			final String URL = "http://www.jordi-charpentier.com/jmd/mobile/faq_etudiant.php";
			
			ProgressDialog progress = new ProgressDialog(this);
			progress.setMessage("Chargement...");
			new DownloadFromUrl(progress, URL).execute();	
			
			final ProgressDialog progress2 = ProgressDialog.show(FaqE.this, "", "Chargement...");
			final AlertDialog alertDialog = new AlertDialog.Builder(this).create();

			final WebView apercuSujetForum = (WebView) findViewById(R.id.etudiant_faq);
			apercuSujetForum.setBackgroundColor(Color.BLACK);

			apercuSujetForum.setWebViewClient(new WebViewClient() {
				public boolean shouldOverrideUrlLoading(WebView view, String url) {
					view.loadUrl(url);
					
					return true;
				}

				public void onPageFinished(WebView view, String url) {
					if(progress2.isShowing()) progress2.dismiss();
				}

				public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
					alertDialog.setTitle("Erreur");
					alertDialog.setMessage(description);
					alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							FaqE.this.finish();

							return;
						}
					});

					alertDialog.show();
				}
			});	

			apercuSujetForum.loadUrl(URL);
		} else {
			AlertDialog.Builder errorDia = new AlertDialog.Builder(this);
			errorDia.setTitle("Erreur");
			errorDia.setMessage("Erreur - Pas de connexion internet.");
			errorDia.setCancelable(false);
			errorDia.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					FaqE.this.finish();
				}
			});
			
			errorDia.show();
		}
	}
	
	/**
	 * Classe interne représentant une tâche asynchrone qui sera effectuée en fond pendant un rond de chargement.
	 * 
	 * @author Jordi CHARPENTIER & Yoann VANHOESERLANDE
	 */
	private class DownloadFromUrl extends AsyncTask<Void, Void, Void> {
		private ProgressDialog progress;
		private String pathUrl;

		public DownloadFromUrl(ProgressDialog progress, String pathUrl) {
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
			WebUtils.downloadFromUrl(pathUrl, Environment.getExternalStorageDirectory().getPath() + "/cacheJMD/faqEtudiant.php");
			
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