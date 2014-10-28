package org.gl.jmd.view.etudiant;

import java.io.File;

import org.gl.jmd.R;
import org.gl.jmd.dao.EtudiantDAO;
import org.gl.jmd.model.user.Etudiant;

import android.app.*;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class ProfilView extends Activity {
	
	private Activity activity;
	
	private Toast toast;
	
	private EditText PRENOM;
	
	private EditText NOM;
	
	private EditText EMAIL;	
	
	private Etudiant e;
	
	private File fileProfil = new File(Environment.getExternalStorageDirectory().getPath() + "/cacheJMD/etudiant.cfg");

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.etudiant_profil_view);
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
		
		activity = this;
		toast = Toast.makeText(activity, "", Toast.LENGTH_SHORT);
		
		e = EtudiantDAO.load();
		
		PRENOM = (EditText) findViewById(R.id.etudiant_profil_zone_prenom);
		NOM = (EditText) findViewById(R.id.etudiant_profil_nom);
		EMAIL = (EditText) findViewById(R.id.etudiant_profil_email);	
		
		initTxt();
	}
	
	public void initTxt() {
		if (e.getMail() != null && e.getMail().length() > 0) {
			EMAIL.setText(e.getMail());
		} else {
			EMAIL.setText("");
		}
		
		if (e.getNom() != null && e.getNom().length() > 0) {
			NOM.setText(e.getNom());
		} else {
			NOM.setText("");
		}
		
		if (e.getPrenom() != null && e.getPrenom().length() > 0) {
			PRENOM.setText(e.getPrenom());
		} else {
			PRENOM.setText("");
		}
	}
	
	public void supprimerProfil(View view) {
		AlertDialog.Builder confirmQuitter = new AlertDialog.Builder(this);
		confirmQuitter.setTitle("Annulation");
		confirmQuitter.setMessage("Voulez-vous vraiment supprimer votre profil ?");
		confirmQuitter.setCancelable(false);
		confirmQuitter.setPositiveButton("Oui", new AlertDialog.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				if (fileProfil.delete()) {
					toast.setText("Suppression effectuée");
					toast.show();
					
					e = EtudiantDAO.load();
					initTxt();
				} else {
					toast.setText("Erreur - Suppression impossible");
					toast.show();
				}
			}
		});

		confirmQuitter.setNegativeButton("Non", null);
		confirmQuitter.show();
	}
	
	public void sauvegarderProfil(View view) {	
		if ((NOM.getText().toString().length() != 0) && (PRENOM.getText().toString().length() != 0) && (EMAIL.getText().toString().length() != 0)) {
			e = EtudiantDAO.load();
			e.setPrenom(PRENOM.getText().toString());
			e.setNom(NOM.getText().toString());
			e.setMail(EMAIL.getText().toString());
			
			EtudiantDAO.save(e);
			
			finish();
		} else {
			boolean isNomOK = true;
			boolean isPrenomOK = true;
			boolean isEmailOK = true;
			
			String txtToast = "";
			
			if (NOM.getText().toString().length() == 0) {
				NOM.setBackgroundResource(R.drawable.border_edittext_error);
				isNomOK = false;
			} else {
				NOM.setBackgroundResource(R.drawable.border_edittext);
			}
			
			if (PRENOM.getText().toString().length() == 0) {
				PRENOM.setBackgroundResource(R.drawable.border_edittext_error);
				isPrenomOK = false;
			} else {
				PRENOM.setBackgroundResource(R.drawable.border_edittext);
			}
			
			if (EMAIL.getText().toString().length() == 0) {
				EMAIL.setBackgroundResource(R.drawable.border_edittext_error);
				isEmailOK = false;
			} else {
				EMAIL.setBackgroundResource(R.drawable.border_edittext);
			}
			
			txtToast = "Champ(s) vide(s) : ";
			
			if (!isNomOK && !isPrenomOK && !isEmailOK) {
				txtToast = "Les trois champs sont vides.";
			}
			
			if (!isNomOK) {
				txtToast += "\"Nom\" ";
			} 
			
			if (!isPrenomOK) {
				txtToast += "\"Prénom\" ";
			} 
			
			if (!isEmailOK) {
				txtToast += "\"Email\" ";
			} 
			
			toast.setText(txtToast);
			toast.show();
		}
	}

	/* Méthode héritée de la classe Activity. */
	
	/**
	 * Méthode permettant d'empécher la reconstruction de la vue lors de la rotation de l'écran. 
	 * 
	 * @param newConfig L'état de la vue avant la rotation.
	 */
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}
}