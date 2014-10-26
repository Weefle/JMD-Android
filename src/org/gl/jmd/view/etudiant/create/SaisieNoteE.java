package org.gl.jmd.view.etudiant.create;

import java.util.*;

import org.gl.jmd.R;
import org.gl.jmd.dao.EtudiantDAO;
import org.gl.jmd.model.*;
import org.gl.jmd.model.enumeration.NoteType;
import org.gl.jmd.model.user.Etudiant;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import android.app.*;
import android.content.res.Configuration;

/**
 * Activité correspondant à la vue d'ajout d'une note pour un étudiant.
 * 
 * @author Jordi CHARPENTIER & Yoann VANHOESERLANDE
 */
public class SaisieNoteE extends Activity {

	private ArrayList<Note> listeNotes = new ArrayList<Note>();
	
	private Etudiant etudiant = EtudiantDAO.load();

	private Matiere matiere;

	private Toast toast;

	private Activity activity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.etudiant_saisie_note);
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

		activity = this;
		toast = Toast.makeText(activity, "", Toast.LENGTH_SHORT);
		
		matiere = (Matiere) getIntent().getSerializableExtra("matiere");

		initViewTitle();
		initListe();
	}
	
	private void initViewTitle() {
		// On donne comme titre à la vue le nom de la matière choisie.
		TextView tvTitre = (TextView) findViewById(R.id.etudiant_saisie_note_titre);
		tvTitre.setText(matiere.getNom());
	}

	private void initListe() {		
		final ListView liste = (ListView) findViewById(android.R.id.list);

		final ArrayList<HashMap<String, String>> listItem = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> map;

		map = new HashMap<String, String>();
		map.put("titre", "Partiel");

		listItem.add(map);

		map = new HashMap<String, String>();
		map.put("titre", "Coefficient Partiel");

		listItem.add(map);

		map = new HashMap<String, String>();
		map.put("titre", "Rattrapage");

		listItem.add(map);

		SimpleAdapter mSchedule = new SimpleAdapter (getBaseContext(), listItem, R.layout.etudiant_saisie_note_list, new String[] {"titre", "note"}, new int[] {R.id.titre, R.id.note_list1});

		liste.setAdapter(mSchedule); 

		TextView headerListe1 = new TextView(activity);
		headerListe1.setText("NOTE PARTIEL & RATTRAPAGE");
		headerListe1.setPadding(50, 0, 0, 20);
		headerListe1.setTextSize(16);

		liste.addHeaderView(headerListe1);

		// Liste des notes de CC déjà entrées pour la matière.
		final ListView liste2 = (ListView) findViewById(R.id.list2);

		final ArrayList<HashMap<String, String>> listItem2 = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> map2 = null;


		final SimpleAdapter mSchedule2 = new SimpleAdapter (getBaseContext(), listItem2, R.layout.etudiant_saisie_note_note_cc_list, new String[] {"titre", "coeff", "note", "img"}, new int[] {R.id.titre, R.id.coeff_list, R.id.note_list, R.id.img});

		liste2.setAdapter(mSchedule2); 

		TextView headerListe2 = new TextView(activity);
		headerListe2.setText("CONTRÔLE CONTINU");
		headerListe2.setPadding(50, 0, 0, 20);
		headerListe2.setTextSize(16);

		liste2.addHeaderView(headerListe2);

		// Bouton "ajouter une note".
		final Button ajouterNote = (Button) findViewById(R.id.etudiant_saisie_note_btn_add);; 
		
		ajouterNote.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final Dialog dialog = new Dialog(activity);
		        dialog.setContentView(R.layout.etudiant_saisie_note_cc);
		        dialog.setTitle("Ajouter une épreuve");
		        dialog.setCancelable(true);
		        
		        final Button button = (Button) dialog.findViewById(R.id.valider_bouton);
	            button.setOnClickListener(new View.OnClickListener() {
	                public void onClick(View v) {
	                	EditText epreuveNom = (EditText) dialog.findViewById(R.id.nom_epreuve);
	                	EditText epreuveNote = (EditText) dialog.findViewById(R.id.note_epreuve);
	                	EditText epreuveCoeff = (EditText) dialog.findViewById(R.id.coeff_epreuve);
	                	
	                	Note note = new Note();
	                	note.setCoefficient(Integer.parseInt(epreuveCoeff.getText().toString()));
	                	note.setNote(Double.parseDouble(epreuveNote.getText().toString()));
	                	note.setNom(epreuveNom.getText().toString());
	                	note.setNoteType(NoteType.CONTROLE_CONTINU);
	                	
	                	listeNotes.add(note);
	                	
	                	HashMap<String, String> mapTemp = null;
	            		
	                	mapTemp = new HashMap<String, String>();
	            		mapTemp.put("titre", note.getNom());
	            		mapTemp.put("coeff", "Coefficient : " + note.getCoefficient());
	            		mapTemp.put("note", note.getNote() + "/20");
	            		
	            		listItem2.add(mapTemp);
	            		
	            		mSchedule2.notifyDataSetChanged();
	            		
	                	dialog.hide();
	                }
	            });
				
		        dialog.show();
			}
		}); 
	}
	
	public void save(View view) {
		finish();
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