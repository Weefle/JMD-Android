package org.gl.jmd.view.etudiant;

import org.gl.jmd.R;
import org.gl.jmd.model.Annee;
import org.gl.jmd.utils.NumberUtils;

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
		tvAvancement.setText(NumberUtils.round(ann.getAvancement(), 2) + " %");
		
		TextView tvMention = (TextView) findViewById(R.id.stats_annee_mention_value);
		tvMention.setText(ann.getMention());
	}
	
	public void export(View view) {
		Intent email = new Intent(Intent.ACTION_SEND);
		email.putExtra(Intent.EXTRA_SUBJECT, "JMD - Moyenne d'une année");
			
		String msg = "Moyenne de l'année '" + ann.getNom() + "' : " + ann.getMoyenne() + "/20";
			
		if (ann.getMoyenne() > 10) {
				msg += "\n\nL'année est validée.";
		} else {
				msg += "\n\nL'année n'est pas validée.";
		}
			
		email.putExtra(Intent.EXTRA_TEXT, msg);
		
		email.setType("message/rfc822");

		startActivity(Intent.createChooser(email, "Choisir un client mail"));
	}
}