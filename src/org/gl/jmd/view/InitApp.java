package org.gl.jmd.view;

import java.io.File;

import org.gl.jmd.R;
import org.gl.jmd.dao.ParametreDAO;
import org.gl.jmd.model.*;
import org.gl.jmd.model.enumeration.ParamType;
import org.gl.jmd.utils.FileUtils;
import org.gl.jmd.view.admin.*;
import org.gl.jmd.view.etudiant.AccueilE;
import org.gl.jmd.view.menu.admin.ConnexionA;

import android.app.*;
import android.content.*;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;

/**
 * Activité permettant de choisir si l'utilisateur est un administrater ou un étudiant.
 * 
 * @author Jordi CHARPENTIER & Yoann VANHOESERLANDE
 */
public class InitApp extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.ecran_choix_user);
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
		
		Parametre p = ParametreDAO.load();
		CheckBox checkbox = (CheckBox) findViewById(R.id.accueil_choix_user_se_souvenir);
		
		if (p != null) {
			if (p.get(ParamType.IS_ADMIN) != null) {
				checkbox.setChecked(true);
			}
		}
		
		// Permet de rediriger l'utilisateur vers le site de JMD lors d'un click sur l'image d'accueil.
		ImageView img = (ImageView)findViewById(R.id.accueil_choix_user_admin_logo);
		img.setOnClickListener(new View.OnClickListener(){
		    public void onClick(View v){
		        Intent intent = new Intent();
		        intent.setAction(Intent.ACTION_VIEW);
		        intent.addCategory(Intent.CATEGORY_BROWSABLE);
		        intent.setData(Uri.parse("http://www.jordi-charpentier.com/jmd/index.php"));
		        
		        startActivity(intent);
		    }
		});
	}
	
	/**
	 * Méthode permettant de naviguer vers l'accueil administrateur.
	 * 
	 * @param view La vue lors du click sur le bouton.
	 */
	public void navigateToAdminScreen(View view) {		
		CheckBox checkbox = (CheckBox) findViewById(R.id.accueil_choix_user_se_souvenir);
		 
		if (checkbox.isChecked()) {
			Parametre p = new Parametre();
			p.addParam(ParamType.IS_ADMIN, "true");
			
			ParametreDAO.save(p);
		}
		
		finish();
		
		File repCache = new File(Environment.getExternalStorageDirectory().getPath() + "/cacheJMD/");
		File fileLogin = new File(repCache.getPath() + "/logins.jmd");
		
		String idAdmin = FileUtils.lireFichier(fileLogin);
		
		if (idAdmin.length() == 0) {
			startActivity(new Intent(InitApp.this, ConnexionA.class));
		} else {
			startActivity(new Intent(InitApp.this, AccueilA.class));
		}
	}
	
	/**
	 * Méthode permettant de naviguer vers l'accueil étudiant.
	 * 
	 * @param view La vue lors du click sur le bouton.
	 */
	public void navigateToEtudiantScreen(View view) {		
		CheckBox checkbox = (CheckBox) findViewById(R.id.accueil_choix_user_se_souvenir);
		 
		if (checkbox.isChecked()) {
			Parametre p = new Parametre();
			p.addParam(ParamType.IS_ADMIN, "false");
			
			ParametreDAO.save(p);
		}
		
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
