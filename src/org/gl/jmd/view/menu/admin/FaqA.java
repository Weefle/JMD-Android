package org.gl.jmd.view.menu.admin;

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
 * Activit� correspondant � la vue FAQ (visible par un admin).
 * 
 * @author Jordi CHARPENTIER & Yoann VANHOESERLANDE
 */
public class FaqA extends Activity {

	private Activity activity;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.administrateur_faq);
		
		activity = this;
		
		initContent();
	}
	
	public void initContent() {
		File fileFAQ = new File(Environment.getExternalStorageDirectory().getPath() + "/cacheJMD/faqAdmin.php");
		
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
		final ProgressDialog progress = ProgressDialog.show(FaqA.this, "", "Chargement...");
		final AlertDialog alertDialog = new AlertDialog.Builder(this).create();

		final WebView apercu = (WebView) findViewById(R.id.admin_faq);
		apercu.setBackgroundColor(Color.BLACK);

		apercu.setWebViewClient(new WebViewClient() {
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
						FaqA.this.finish();

						return;
					}
				});

				alertDialog.show();
			}
		});	

		apercu.loadUrl("file:///" + Environment.getExternalStorageDirectory().getPath() + "/cacheJMD/faqAdmin.php");
	}
	
	private void initWebview() {
		if (isNetworkAvailable()) {
			final String URL = "http://www.jordi-charpentier.com/jmd/mobile/faq_admin.php";
			
			ProgressDialog progress = new ProgressDialog(activity);
			progress.setMessage("Chargement...");
			new DownloadFromUrl(progress, URL).execute();	
			
			final ProgressDialog progress2 = ProgressDialog.show(FaqA.this, "", "Chargement...");
			final AlertDialog alertDialog = new AlertDialog.Builder(this).create();

			final WebView apercuSujetForum = (WebView) findViewById(R.id.admin_faq);
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
							FaqA.this.finish();

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
					FaqA.this.finish();
				}
			});
			
			errorDia.show();
		}
	}
	
	/**
	 * Classe interne repr�sentant une t�che asynchrone qui sera effectu�e en fond pendant un rond de chargement.
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
			WebUtils.downloadFromUrl(pathUrl, Environment.getExternalStorageDirectory().getPath() + "/cacheJMD/faqAdmin.php");
			
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