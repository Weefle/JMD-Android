package org.gl.jmd.view.etudiant;

import org.gl.jmd.R;
import org.gl.jmd.utils.NumberUtils;

import android.os.Bundle;
import android.widget.TextView;
import android.app.*;
import android.content.Intent;

/**
 * Activité correspondant à la vue de stats d'une année.
 * 
 * @author Jordi CHARPENTIER & Yoann VANHOESERLANDE
 */
public class StatsAnnee extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.etudiant_stats_annee);
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
		
		Intent lastIntent = getIntent();
		
		double moyenne = lastIntent.getExtras().getDouble("moyenne");
		float avancement = lastIntent.getExtras().getFloat("avancement");
		String mention = lastIntent.getExtras().getString("mention");
		
		TextView tvMoyenne = (TextView) findViewById(R.id.stats_annee_moyenne_value);
		
		if (moyenne == -1) {
			tvMoyenne.setText("Aucune note");
		} else {
			tvMoyenne.setText(moyenne +  " / 20");
		}
		
		TextView tvAvancement = (TextView) findViewById(R.id.stats_annee_avancement_value);
		tvAvancement.setText(NumberUtils.round(avancement, 2) + " %");
		
		TextView tvMention = (TextView) findViewById(R.id.stats_annee_mention_value);
		tvMention.setText(mention);
	}
}