package org.gl.jmd.view.etudiant;

import java.io.File;

import org.gl.jmd.R;
import org.gl.jmd.model.Annee;
import org.gl.jmd.utils.PdfUtils;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.app.*;
import android.content.Intent;

/**
 * Activité correspondant à la vue de stats d'une année.
 * 
 * @author Jordi CHARPENTIER & Yoann VANHOESERLANDE
 */
public class StatsAnnee extends Activity {
	
	private Annee ann;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.etudiant_stats_annee);
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
		
		ann = (Annee) getIntent().getSerializableExtra("annee");
		
		initTextViews();
	}
	
	private void initTextViews() {
		TextView tvMoyenne = (TextView) findViewById(R.id.stats_annee_moyenne_value);
		
		if (ann.getMoyenne() == -1.0) {
			tvMoyenne.setText("Aucune note");
		} else {
			tvMoyenne.setText(ann.getMoyenne() +  " / 20");
		}
		
		TextView tvAvancement = (TextView) findViewById(R.id.stats_annee_avancement_value);
		tvAvancement.setText(round(ann.getAvancement(), 2) + " %");
		
		TextView tvMention = (TextView) findViewById(R.id.stats_annee_mention_value);
		tvMention.setText(ann.getMention());
	}
	
	/**
	 * Méthode permettant d'arrondir une valeur.
	 * 
	 * @param value La valeur à arrondir.
	 * @param places Le nombre de décimales à garder.
	 * 
	 * @return La valeur, arrondie.
	 */
	private double round(double value, int places) {
	    long factor = (long) Math.pow(10, places);
	    value = value * factor;
	    long tmp = Math.round(value);
	    
	    return (double) tmp / factor;
	}
	
	public void export(View view) {		
		PdfUtils.generateYearRapport(ann, this.getApplicationContext());
		
		File file = new File("/sdcard/cacheJMD/rapport-" + ann.getId() + ".pdf");
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(file), "application/pdf");
		intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		startActivity(intent); 
	}
}