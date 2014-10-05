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
import android.os.*;

/**
 * Activité correspondant à la vue d'acceuil de l'application.
 * 
 * @author Jordi CHARPENTIER & Yoann VANHOESERLANDE
 */
public class Accueil extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.accueil);
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
		
		/*
		 *  Va dispatcher l'utilisateur sur le bon accueil : si un fichier login est trouvé, c'est un admin. 
		 *  Sinon, un étudiant.
		 */
		
		finish();
		
		File repCache = new File(Environment.getExternalStorageDirectory().getPath() + "/cacheJMD/");
		File fileLogin = new File(repCache.getPath() + "/logins.jmd");
		
		if (!repCache.exists()) {
			repCache.mkdir();
		}
		
		Parametre param = ParametreDAO.load();
		
		if ((param == null)) {
			startActivity(new Intent(Accueil.this, InitApp.class));
		} else if ((param != null) && (param.get(ParamType.IS_ADMIN) != null)) {
			if (param.get(ParamType.IS_ADMIN).equals("false")) {
				startActivity(new Intent(Accueil.this, AccueilE.class));
			} else if (param.get(ParamType.IS_ADMIN).equals("true")) {
				String idAdmin = FileUtils.lireFichier(fileLogin);

				if (idAdmin.length() == 0) {
					startActivity(new Intent(Accueil.this, ConnexionA.class));
				} else {
					if (idAdmin.matches("[+-]?\\d*(\\.\\d+)?")){
						startActivity(new Intent(Accueil.this, AccueilA.class));
					} else {
						fileLogin.delete();

						AlertDialog.Builder errorDia = new AlertDialog.Builder(this);
						errorDia.setTitle("Erreur");
						errorDia.setMessage("Erreur - Veuillez relancer l'application.");
						errorDia.setCancelable(false);
						errorDia.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								Accueil.this.finish();
								android.os.Process.killProcess(android.os.Process.myPid());
							}
						});

						errorDia.show();
					}
				}
			} else {
				// Il y a une erreur dans le fichier. On le supprime et on demande de relancer l'application.
				
				ParametreDAO.getFile().delete();
				
				AlertDialog.Builder errorDia = new AlertDialog.Builder(this);
				errorDia.setTitle("Erreur");
				errorDia.setMessage("Erreur - Veuillez relancer l'application.");
				errorDia.setCancelable(false);
				errorDia.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						Accueil.this.finish();
						android.os.Process.killProcess(android.os.Process.myPid());
					}
				});

				errorDia.show();
			} 
		} 
	}
}