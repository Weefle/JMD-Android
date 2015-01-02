package org.gl.jmd.view;

import org.gl.jmd.Constantes;
import org.gl.jmd.R;
import org.gl.jmd.utils.FileUtils;
import org.gl.jmd.view.admin.*;
import org.gl.jmd.view.etudiant.AccueilE;

import android.app.*;
import android.content.*;
import android.content.res.Configuration;
import android.os.*;
import android.view.View;
import android.widget.CheckBox;

/**
 * Activité permettant de choisir si l'utilisateur est un administrater ou un étudiant.
 * 
 * @author Jordi CHARPENTIER & Yoann VANHOESERLANDE
 */
public class InitApp extends Activity {

	private CheckBox checkbox;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.ecran_choix_user);
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
		
		initCheckbox(); 
	}
	
	private void initCheckbox() {		
		checkbox = (CheckBox) findViewById(R.id.accueil_choix_user_se_souvenir);

		if (FileUtils.readFile(Constantes.FILE_PARAM).length() > 0) {
			checkbox.setChecked(true);
		}
	}

	private void isCheckBoxEtudiantChecked() {
		if (checkbox.isChecked()) {
			FileUtils.writeFile("Etudiant", Constantes.FILE_PARAM);
		} else {
			Constantes.FILE_PARAM.delete();
		}
	}

	private void isCheckBoxAdminChecked() {
		if (checkbox.isChecked()) {
			FileUtils.writeFile("Administrateur", Constantes.FILE_PARAM);
		} else {
			Constantes.FILE_PARAM.delete();
		}
	}

	/**
	 * Méthode permettant de naviguer vers l'accueil administrateur.
	 * 
	 * @param view La vue lors du click sur le bouton.
	 */
	public void navigateToAdminScreen(View view) {		
		isCheckBoxAdminChecked();

		finish();

		if (Constantes.FILE_PSEUDO.exists() && Constantes.FILE_TOKEN.exists()) {
			startActivity(new Intent(InitApp.this, AccueilA.class));
		} else {
			startActivity(new Intent(InitApp.this, ConnexionA.class));
		}
	}

	/**
	 * Méthode permettant de naviguer vers l'accueil étudiant.
	 * 
	 * @param view La vue lors du click sur le bouton.
	 */
	public void navigateToEtudiantScreen(View view) {		
		isCheckBoxEtudiantChecked();

		finish();

		startActivity(new Intent(InitApp.this, AccueilE.class));
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
	 * Méthode déclenchée lors d'un click sur le bouton virtuel Android de retour.
	 */
	public void onBackPressed() {
		AlertDialog.Builder confirmQuitter = new AlertDialog.Builder(this);
		confirmQuitter.setTitle("Fermeture");
		confirmQuitter.setMessage("Voulez-vous vraiment quitter ?");
		confirmQuitter.setCancelable(false);
		confirmQuitter.setPositiveButton("Oui", new AlertDialog.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				InitApp.this.finish();
				android.os.Process.killProcess(android.os.Process.myPid());
			}
		});

		confirmQuitter.setNegativeButton("Non", null);
		confirmQuitter.show();
	}
}
