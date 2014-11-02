package org.gl.jmd.view.etudiant;

import org.gl.jmd.R;
import org.gl.jmd.dao.EtudiantDAO;
import org.gl.jmd.model.Annee;
import org.gl.jmd.model.user.Etudiant;
import org.gl.jmd.utils.NumberUtils;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.app.*;
import android.content.Intent;

/**
 * Activité correspondant à la vue de stats d'une année.
 * 
 * @author Jordi CHARPENTIER & Yoann VANHOESERLANDE
 */
public class StatsAnnee extends Activity {
	
	private Etudiant etud = EtudiantDAO.load();
	
	private Activity activity;

	private Toast toast;
	
	private Annee ann;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.etudiant_stats_annee);
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
		
		activity = this;
		toast = Toast.makeText(activity, "", Toast.LENGTH_SHORT);
		
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
		if (etud.getMail() != null && etud.getMail().length() > 0) {
			Intent email = new Intent(Intent.ACTION_SEND);
			email.putExtra(Intent.EXTRA_EMAIL, new String[] { etud.getMail() });
			email.putExtra(Intent.EXTRA_SUBJECT, "JMD - Moyenne de l'année");
			
			String msg = "Moyenne de l'année (id : " + ann.getId() + ", nom : " + ann.getNom() + ") : " + ann.getMoyenne();
			
			if (ann.getMoyenne() > 10) {
				msg += "\n\nL'année est validée.";
			} else {
				msg += "\n\nL'année n'est pas validée.";
			}
			
			email.putExtra(Intent.EXTRA_TEXT, msg);

			email.setType("message/rfc822");

			startActivity(Intent.createChooser(email, "Choisir un client mail"));
		} else {
			toast.setText("Il faut définir un mail afin de pouvoir utiliser cette fonctionnalité.");
			toast.show();
		}
	}
}