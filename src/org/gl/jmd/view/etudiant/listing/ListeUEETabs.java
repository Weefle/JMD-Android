package org.gl.jmd.view.etudiant.listing;

import org.gl.jmd.R;
import org.gl.jmd.dao.EtudiantDAO;
import org.gl.jmd.model.*;
import org.gl.jmd.model.enumeration.DecoupageType;
import org.gl.jmd.model.enumeration.DecoupageYearType;

import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import android.widget.TabHost.OnTabChangeListener;
import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;

public class ListeUEETabs extends TabActivity {

	private TabHost tabHost;

	private int currentTab = 0;
	
	private int positionDip = 0;

	private int positionAnn = 0;

	private Etudiant etud = EtudiantDAO.load();

	private Annee ann;
	
	private TextView tvTitre = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.etudiant_liste_ue_tabs);
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

		positionDip = getIntent().getExtras().getInt("positionDip");
		positionAnn = getIntent().getExtras().getInt("positionAnn");

		ann = etud.getListeDiplomes().get(positionDip).getListeAnnees().get(positionAnn);

		tabHost = getTabHost();
		
		initTabs();
		initTabHost();
		initElements();
	}
	
	private void initElements() {
		tvTitre = (TextView) findViewById(R.id.etudiant_liste_ue_tabs_titre);
		tvTitre.setText("Semestre 1");
		
		// Tabs.
		TextView tv = (TextView) tabHost.getTabWidget().getChildAt(0).findViewById(R.id.tabsText);
		tv.setTextColor(Color.parseColor("#FF5E3A"));
		tv.setTextSize(13);
		
		SpannableString spanString = new SpannableString("Semestre 1");
		spanString.setSpan(new StyleSpan(Typeface.BOLD), 0, spanString.length(), 0);
		
		tv.setText(spanString);
		
		TextView tv2 = (TextView) tabHost.getTabWidget().getChildAt(1).findViewById(R.id.tabsText);
		tv2.setTextColor(Color.parseColor("#FFFFFF"));
		tv2.setTextSize(13);
	}
	
	private void initTabHost() {
		tabHost.getTabWidget().getChildAt(0).setBackgroundDrawable(getResources().getDrawable(R.drawable.tab_bg_selector)); 
		tabHost.getTabWidget().getChildAt(0).setSelected(true);

		tabHost.getTabWidget().getChildAt(1).setBackgroundDrawable(getResources().getDrawable(R.drawable.tab_bg_selector)); 
		
		tabHost.setOnTabChangedListener(new OnTabChangeListener(){
			@Override
			public void onTabChanged(String tabId) {	
				TextView tv = (TextView) tabHost.getTabWidget().getChildAt(0).findViewById(R.id.tabsText);
				TextView tv2 = (TextView) tabHost.getTabWidget().getChildAt(1).findViewById(R.id.tabsText);
				
				SpannableString spanString = new SpannableString("Semestre 1");
				SpannableString spanString2 = new SpannableString("Semestre 2");
				
				if (tabId.equals("0")) {
					currentTab = 0;
					tvTitre.setText("Semestre 1");
					
					spanString.setSpan(new StyleSpan(Typeface.BOLD), 0, spanString.length(), 0);
					
					tv.setTextColor(Color.parseColor("#FF5E3A"));
					tv.setText(spanString);
					
					spanString2.setSpan(new StyleSpan(Typeface.NORMAL), 0, spanString2.length(), 0);
					
					tv2.setTextColor(Color.parseColor("#FFFFFF"));
					tv2.setText(spanString2);
				} else if (tabId.equals("1")) {
					currentTab = 1;
					tvTitre.setText("Semestre 2");

					spanString.setSpan(new StyleSpan(Typeface.NORMAL), 0, spanString.length(), 0);

					tv.setTextColor(Color.parseColor("#FFFFFF"));
					tv.setText(spanString);

					spanString2.setSpan(new StyleSpan(Typeface.BOLD), 0, spanString2.length(), 0);

					tv2.setTextColor(Color.parseColor("#FF5E3A"));
					tv2.setText(spanString2);
				} 
			}}
		);
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
 
		return view;
	}
	
	private void initTabs() {
		Intent act = new Intent(ListeUEETabs.this, ListeUEE.class);
		act.putExtra("positionDip", positionDip);
		act.putExtra("positionAnn", positionAnn);
		
		Intent act2 = act;
		
		if (ann.getDecoupage() == DecoupageType.SEMESTRE) {
			act.putExtra("type", DecoupageYearType.SEM1);
			
			setupTab("Semestre 1", "0", act, 0);
			
			act2.putExtra("type", DecoupageYearType.SEM2);
			
	        setupTab("Semestre 2", "1", act2, 1);
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
	 * Méthode exécutée lorsque l'activité est relancée.
	 */
	@Override
	public void onRestart() {
		etud = EtudiantDAO.load();
		
		ann = etud.getListeDiplomes().get(positionDip).getListeAnnees().get(positionAnn);

		super.onRestart();
	} 

	/**
	 * Méthode exécutée lorsque l'activité est relancée.
	 */
	@Override
	public void onResume() {
		etud = EtudiantDAO.load();
		
		ann = etud.getListeDiplomes().get(positionDip).getListeAnnees().get(positionAnn);

		super.onResume();
	} 
}