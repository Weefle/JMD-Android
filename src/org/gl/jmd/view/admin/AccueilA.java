package org.gl.jmd.view.admin;

import java.io.File;

import org.gl.jmd.R;
import org.gl.jmd.view.*;
import org.gl.jmd.view.admin.create.*;
import org.gl.jmd.view.admin.listing.*;
import org.gl.jmd.view.menu.admin.*;

import android.app.*;
import android.content.*;
import android.content.res.Configuration;
import android.graphics.*;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.*;
import android.widget.*;
import android.widget.TabHost.*;

/**
 * Activité correspondant à la vue d'acceuil de l'administrateur.
 * 
 * @author Jordi CHARPENTIER & Yoann VANHOESERLANDE
 */
public class AccueilA extends TabActivity {

	private TabHost tabHost;

	private int currentTab = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.administrateur_accueil);
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
		
		tabHost = getTabHost();
		
		setupTab("Etablissements", "0", new Intent().setClass(this, ListeEtablissementA.class));
        setupTab("Diplômes", "1", new Intent().setClass(this, ListeDiplomeA.class));

		tabHost.getTabWidget().getChildAt(0).getLayoutParams().height = 80;
		tabHost.getTabWidget().getChildAt(0).setBackgroundDrawable(getResources().getDrawable(R.drawable.tab_bg_selector)); 
		tabHost.getTabWidget().getChildAt(0).setSelected(true);
		
		tabHost.getTabWidget().getChildAt(1).getLayoutParams().height = 80;
		tabHost.getTabWidget().getChildAt(1).setBackgroundDrawable(getResources().getDrawable(R.drawable.tab_bg_selector)); 
		
		TextView tv = (TextView) tabHost.getTabWidget().getChildAt(0).findViewById(R.id.tabsText);
		tv.setTextColor(Color.parseColor("#FF5E3A"));
		
		SpannableString spanString = new SpannableString("Etablissements");
		spanString.setSpan(new StyleSpan(Typeface.BOLD), 0, spanString.length(), 0);
		
		tv.setText(spanString);
		
		TextView tv2 = (TextView) tabHost.getTabWidget().getChildAt(1).findViewById(R.id.tabsText);
		tv2.setTextColor(Color.parseColor("#FFFFFF"));

		tabHost.setOnTabChangedListener(new OnTabChangeListener(){
			@Override
			public void onTabChanged(String tabId) {	
				TextView tv = (TextView) tabHost.getTabWidget().getChildAt(0).findViewById(R.id.tabsText);
				TextView tv2 = (TextView) tabHost.getTabWidget().getChildAt(1).findViewById(R.id.tabsText);
				
				SpannableString spanString = new SpannableString("Etablissements");
				SpannableString spanString2 = new SpannableString("Diplômes");
				
				if (tabId.equals("0")) {
					currentTab = 0;
					
					spanString.setSpan(new StyleSpan(Typeface.BOLD), 0, spanString.length(), 0);
					
					tv.setTextColor(Color.parseColor("#FF5E3A"));
					tv.setText(spanString);

					spanString2.setSpan(new StyleSpan(Typeface.NORMAL), 0, spanString2.length(), 0);
					
					tv2.setTextColor(Color.parseColor("#FFFFFF"));
					tv2.setText(spanString2);
				} else if (tabId.equals("1")) {
					currentTab = 1;

					spanString.setSpan(new StyleSpan(Typeface.NORMAL), 0, spanString.length(), 0);

					tv.setTextColor(Color.parseColor("#FFFFFF"));
					tv.setText(spanString);

					spanString2.setSpan(new StyleSpan(Typeface.BOLD), 0, spanString2.length(), 0);

					tv2.setTextColor(Color.parseColor("#FF5E3A"));
					tv2.setText(spanString2);
				}
			}});
	}
	
    private void setupTab(String name, String tag, Intent intent) {
		tabHost.addTab(tabHost.newTabSpec(tag).setIndicator(createTabView(tabHost.getContext(), name)).setContent(intent));
	}
 
	private View createTabView(final Context context, final String text) {
		View view = LayoutInflater.from(context).inflate(R.xml.tab_item, null);
		TextView tv = (TextView) view.findViewById(R.id.tabsText);
		tv.setText(text);
 
		return view;
	}

	/**
	 * Méthode permettant de faire apparaitre le menu lors du click sur le bouton de menu.
	 * 
	 * @param view La vue lors du click sur le bouton de menu.
	 */
	public void openMenu(View view) {
		openOptionsMenu();
	}

	/**
	 * Méthode permettant de faire apparaitre la popup lors du click sur le bouton "plus".
	 * 
	 * @param view La vue lors du click sur le bouton "plus".
	 */
	public void openPopupCreation(View view) {
		if (currentTab == 0) {
			startActivity(new Intent(AccueilA.this, CreationEtablissement.class));	
		} else if (currentTab == 1) {
			startActivity(new Intent(AccueilA.this, CreationDiplome.class));	
		}
	}

	/* Méthodes héritées de la classe Activity. */

	/**
	 * Méthode permettant d'afficher le menu de la vue.
	 * 
	 * @param menu Le menu à afficher.
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_admin_view, menu);

		return true;
	}

	/**
	 * Méthode déclenchée lors du click sur un élément du menu de l'application.
	 * 
	 * @item L'élément du menu sélectionné.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		
		case R.id.menu_accueil:
			finish();
			startActivity(new Intent(AccueilA.this, InitApp.class));		

			return true;

		case R.id.menu_deconnexion:
			File filePseudo = new File("/sdcard/cacheJMD/pseudo.jmd");
			File fileToken = new File("/sdcard/cacheJMD/token.jmd");
			
			filePseudo.delete();
			fileToken.delete();

			finish();
			startActivity(new Intent(AccueilA.this, ConnexionA.class));				

			return true;

		case R.id.menu_nommer_admin:
			startActivity(new Intent(AccueilA.this, AjouterAdminA.class));				

			return true;
		}

		return false;
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