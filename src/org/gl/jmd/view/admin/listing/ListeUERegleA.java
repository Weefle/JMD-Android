package org.gl.jmd.view.admin.listing;

import org.gl.jmd.R;
import org.gl.jmd.model.Annee;
import org.gl.jmd.view.admin.create.*;

import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.*;
import android.widget.*;
import android.widget.TabHost.OnTabChangeListener;
import android.app.*;
import android.content.*;
import android.content.res.Configuration;
import android.graphics.*;

/**
 * Vue ayant 2 onglets : listing des ue d'une année / listing des règles de gestion.
 * 
 * @author Jordi CHARPENTIER & Yoann VANHOESERLANDE
 */
public class ListeUERegleA extends TabActivity {

	private TabHost tabHost;

	private int currentTab = 0;
	
	private Annee annee = null;
	
	private String decoupage = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.administrateur_liste_ue_regle);
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
		
		annee = (Annee) getIntent().getExtras().getSerializable("annee");
		decoupage = getIntent().getExtras().getString("decoupage");
		
		tabHost = getTabHost();
		
		Intent intentListeUE = new Intent(ListeUERegleA.this, ListeUEA.class);
		intentListeUE.putExtra("annee", annee);
		intentListeUE.putExtra("decoupage", decoupage);
		
		Intent intentListeRegle = new Intent(ListeUERegleA.this, ListeRegleA.class);
		intentListeRegle.putExtra("annee", annee);
		intentListeRegle.putExtra("decoupage", decoupage);
		
		setupTab("Liste des UE", "0", intentListeUE);
        setupTab("Règles de gestion", "1", intentListeRegle);

		tabHost.getTabWidget().getChildAt(0).getLayoutParams().height = 80;
		tabHost.getTabWidget().getChildAt(0).setBackgroundDrawable(getResources().getDrawable(R.drawable.tab_bg_selector)); 
		tabHost.getTabWidget().getChildAt(0).setSelected(true);
		
		tabHost.getTabWidget().getChildAt(1).getLayoutParams().height = 80;
		tabHost.getTabWidget().getChildAt(1).setBackgroundDrawable(getResources().getDrawable(R.drawable.tab_bg_selector)); 
		
		TextView tv = (TextView) tabHost.getTabWidget().getChildAt(0).findViewById(R.id.tabsText);
		tv.setTextColor(Color.parseColor("#FF5E3A"));
		
		SpannableString spanString = new SpannableString("Liste des UE");
		spanString.setSpan(new StyleSpan(Typeface.BOLD), 0, spanString.length(), 0);
		
		tv.setText(spanString);
		
		TextView tv2 = (TextView) tabHost.getTabWidget().getChildAt(1).findViewById(R.id.tabsText);
		tv2.setTextColor(Color.parseColor("#FFFFFF"));

		tabHost.setOnTabChangedListener(new OnTabChangeListener(){
			@Override
			public void onTabChanged(String tabId) {	
				TextView tv = (TextView) tabHost.getTabWidget().getChildAt(0).findViewById(R.id.tabsText);
				TextView tv2 = (TextView) tabHost.getTabWidget().getChildAt(1).findViewById(R.id.tabsText);
				
				SpannableString spanString = new SpannableString("Liste des UE");
				SpannableString spanString2 = new SpannableString("Règles de gestion");
				
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
	 * Méthode permettant de faire apparaitre la popup lors du click sur le bouton "plus".
	 * 
	 * @param view La vue lors du click sur le bouton "plus".
	 */
	public void openPopupCreation(View view) {
		Class<?> c = null;
		
		if (currentTab == 0) {
			c = CreationUE.class;
		} else if (currentTab == 1) {
			c = CreationRegle.class;	
		}
		
		Intent intent = new Intent(ListeUERegleA.this, c);
		intent.putExtra("annee", annee);
		intent.putExtra("decoupage", decoupage);
		
		startActivity(intent);	
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