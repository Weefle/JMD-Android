package org.gl.jmd.view;

import java.io.File;

import org.gl.jmd.Constantes;
import org.gl.jmd.utils.FileUtils;
import org.gl.jmd.view.admin.*;
import org.gl.jmd.view.etudiant.AccueilE;

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
		
		initView();
	}
	
	private void initView() {
		/*
		 *  Va dispatcher l'utilisateur sur le bon accueil : si un fichier login est trouvé, c'est un admin. 
		 *  Sinon, un étudiant.
		 */
		
		finish();
		
		File repCache = new File("/sdcard/cacheJMD/");
		
		if (!repCache.exists()) {
			repCache.mkdir();
		}
		
		String paramHome = FileUtils.readFile(Constantes.FILE_PARAM);
		
		if (paramHome.length() == 0) {
			startActivity(new Intent(Accueil.this, InitApp.class));
		} else if (paramHome.equals("Administrateur")) {
			if (Constantes.FILE_PSEUDO.exists() && Constantes.FILE_TOKEN.exists()) {
				startActivity(new Intent(Accueil.this, AccueilA.class));
			} else {
				startActivity(new Intent(Accueil.this, ConnexionA.class));
			}
		} else if (paramHome.equals("Etudiant")) {
			startActivity(new Intent(Accueil.this, AccueilE.class));
		}
	}
}