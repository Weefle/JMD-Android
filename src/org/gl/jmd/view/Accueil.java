package org.gl.jmd.view;

import java.io.File;

import org.gl.jmd.R;
import org.gl.jmd.dao.ParametreDAO;
import org.gl.jmd.model.*;
import org.gl.jmd.model.enumeration.ParamType;
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
		File filePseudo = new File(repCache.getPath() + "/pseudo.jmd");
		File fileToken = new File(repCache.getPath() + "/token.jmd");
		
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
				if (filePseudo.exists() && fileToken.exists()) {
					startActivity(new Intent(Accueil.this, AccueilA.class));
				} else {
					startActivity(new Intent(Accueil.this, ConnexionA.class));
				}
			}
		} 
	}
}