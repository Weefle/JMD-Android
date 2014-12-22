package org.gl.jmd.view.etudiant.listing;

import org.gl.jmd.EtudiantDAO;
import org.gl.jmd.R;
import org.gl.jmd.model.*;
import org.gl.jmd.model.enumeration.*;
import org.gl.jmd.view.etudiant.StatsAnnee;

import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import android.widget.TabHost.OnTabChangeListener;
import android.app.*;
import android.content.*;
import android.content.res.Configuration;
import android.graphics.*;

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

	/**
	 * Méthode déclenchée lors d'un click sur le bouton de simulation.
	 * 
	 * @param view La vue lors du click sur le bouton de simulation.
	 */
	public void simulerObtentionAnnee(View view) {
		Intent i = new Intent(ListeUEETabs.this, StatsAnnee.class);
		i.putExtra("annee", etud.getListeDiplomes().get(positionDip).getListeAnnees().get(positionAnn));

		startActivity(i);
	}

	/**
	 * Méthode déclenchée lors d'un click sur le bouton de simulation.
	 * 
	 * @param view La vue lors du click sur le bouton de simulation.
	 */
	public void back(View view) {
		finish();
	}

	private void initElements() {
		tvTitre = (TextView) findViewById(R.id.etudiant_liste_ue_tabs_titre);
		tvTitre.setText("Semestre 1");

		if (ann.getDecoupage() == DecoupageType.SEMESTRE) {
			tvTitre.setText("Semestre 1");
		} else {
			tvTitre.setText("Trimestre 1");
		}

		// Tabs.
		TextView tv = (TextView) tabHost.getTabWidget().getChildAt(0).findViewById(R.id.tabsText);
		tv.setTextColor(Color.parseColor("#FF5E3A"));
		tv.setTextSize(13);

		SpannableString spanString = null; 

		if (ann.getDecoupage() == DecoupageType.SEMESTRE) {
			spanString = new SpannableString("Semestre 1");
		} else {
			spanString = new SpannableString("Trimestre 1");
		}

		spanString.setSpan(new StyleSpan(Typeface.BOLD), 0, spanString.length(), 0);

		tv.setText(spanString);

		TextView tv2 = (TextView) tabHost.getTabWidget().getChildAt(1).findViewById(R.id.tabsText);
		tv2.setTextColor(Color.parseColor("#FFFFFF"));
		tv2.setTextSize(13);

		if (ann.getDecoupage() == DecoupageType.TRIMESTRE) {
			TextView tv3 = (TextView) tabHost.getTabWidget().getChildAt(2).findViewById(R.id.tabsText);
			tv3.setTextColor(Color.parseColor("#FFFFFF"));
			tv3.setTextSize(13);
		}
	}

	private void initTabHost() {
		tabHost.getTabWidget().getChildAt(0).setBackgroundDrawable(getResources().getDrawable(R.drawable.tab_bg_selector)); 
		tabHost.getTabWidget().getChildAt(0).setSelected(true);

		tabHost.getTabWidget().getChildAt(1).setBackgroundDrawable(getResources().getDrawable(R.drawable.tab_bg_selector)); 

		if (ann.getDecoupage() == DecoupageType.TRIMESTRE) {
			tabHost.getTabWidget().getChildAt(2).setBackgroundDrawable(getResources().getDrawable(R.drawable.tab_bg_selector)); 
		}

		tabHost.setOnTabChangedListener(new OnTabChangeListener(){
			@Override
			public void onTabChanged(String tabId) {	
				TextView tv = (TextView) tabHost.getTabWidget().getChildAt(0).findViewById(R.id.tabsText);
				TextView tv2 = (TextView) tabHost.getTabWidget().getChildAt(1).findViewById(R.id.tabsText);
				TextView tv3 = null;

				SpannableString spanString = null; ;
				SpannableString spanString2 = null;
				SpannableString spanString3 = null;

				if (ann.getDecoupage() == DecoupageType.SEMESTRE) {
					spanString = new SpannableString("Semestre 1");
					spanString2 = new SpannableString("Semestre 2");
				} else {
					spanString = new SpannableString("Trimestre 1");
					spanString2 = new SpannableString("Trimestre 2");
					spanString3 = new SpannableString("Trimestre 3");

					tv3 = (TextView) tabHost.getTabWidget().getChildAt(2).findViewById(R.id.tabsText);
				}

				if (tabId.equals("0")) {
					currentTab = 0;

					if (ann.getDecoupage() == DecoupageType.SEMESTRE) {
						tvTitre.setText("Semestre 1");
					} else {
						tvTitre.setText("Trimestre 1");

						spanString3.setSpan(new StyleSpan(Typeface.NORMAL), 0, spanString3.length(), 0);

						tv3.setTextColor(Color.parseColor("#FFFFFF"));
						tv3.setText(spanString3);
					}

					spanString.setSpan(new StyleSpan(Typeface.BOLD), 0, spanString.length(), 0);

					tv.setTextColor(Color.parseColor("#FF5E3A"));
					tv.setText(spanString);

					spanString2.setSpan(new StyleSpan(Typeface.NORMAL), 0, spanString2.length(), 0);

					tv2.setTextColor(Color.parseColor("#FFFFFF"));
					tv2.setText(spanString2);
				} else if (tabId.equals("1")) {
					currentTab = 1;

					if (ann.getDecoupage() == DecoupageType.SEMESTRE) {
						tvTitre.setText("Semestre 2");
					} else {
						tvTitre.setText("Trimestre 2");

						spanString3.setSpan(new StyleSpan(Typeface.NORMAL), 0, spanString3.length(), 0);

						tv3.setTextColor(Color.parseColor("#FFFFFF"));
						tv3.setText(spanString3);
					}

					spanString.setSpan(new StyleSpan(Typeface.NORMAL), 0, spanString.length(), 0);

					tv.setTextColor(Color.parseColor("#FFFFFF"));
					tv.setText(spanString);

					spanString2.setSpan(new StyleSpan(Typeface.BOLD), 0, spanString2.length(), 0);

					tv2.setTextColor(Color.parseColor("#FF5E3A"));
					tv2.setText(spanString2);
				} else if (tabId.equals("2")) {
					currentTab = 2;

					tvTitre.setText("Trimestre 3");

					spanString3.setSpan(new StyleSpan(Typeface.BOLD), 0, spanString3.length(), 0);

					tv3.setTextColor(Color.parseColor("#FF5E3A"));
					tv3.setText(spanString3);

					spanString.setSpan(new StyleSpan(Typeface.NORMAL), 0, spanString.length(), 0);

					tv.setTextColor(Color.parseColor("#FFFFFF"));
					tv.setText(spanString);

					spanString2.setSpan(new StyleSpan(Typeface.NORMAL), 0, spanString2.length(), 0);

					tv2.setTextColor(Color.parseColor("#FFFFFF"));
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

		Intent act2 = new Intent(ListeUEETabs.this, ListeUEE.class);
		act2.putExtra("positionDip", positionDip);
		act2.putExtra("positionAnn", positionAnn);

		Intent act3 = new Intent(ListeUEETabs.this, ListeUEE.class);
		act3.putExtra("positionDip", positionDip);
		act3.putExtra("positionAnn", positionAnn);

		if (ann.getDecoupage() == DecoupageType.SEMESTRE) {
			act.putExtra("type", DecoupageYearType.SEM1);

			setupTab("Semestre 1", "0", act, 0);

			act2.putExtra("type", DecoupageYearType.SEM2);

			setupTab("Semestre 2", "1", act2, 1);
		} else if (ann.getDecoupage() == DecoupageType.TRIMESTRE) {
			act.putExtra("type", DecoupageYearType.TRI1);

			setupTab("Trimestre 1", "0", act, 0);

			act2.putExtra("type", DecoupageYearType.TRI2);

			setupTab("Trimestre 2", "1", act2, 1);

			act3.putExtra("type", DecoupageYearType.TRI3);

			setupTab("Trimestre 3", "2", act3, 2);
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