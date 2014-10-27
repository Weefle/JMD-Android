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
 * Activit� correspondant � la vue d'ajout d'une note pour un �tudiant.
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
		// On donne comme titre � la vue le nom de la mati�re choisie.
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

		liste.setAdapter(new SimpleAdapter (getBaseContext(), listItem, R.layout.etudiant_saisie_note_list, new String[] {"titre", "note"}, new int[] {R.id.titre, R.id.noteCC})); 

		TextView headerListe1 = new TextView(activity);
		headerListe1.setText("NOTE PARTIEL & RATTRAPAGE");
		headerListe1.setPadding(50, 0, 0, 20);
		headerListe1.setTextSize(16);

		liste.addHeaderView(headerListe1);

		// Si des infos sont d�j� stock�es pour la mati�re (note, coeff, ...).
		liste.post(new Runnable() {
			@Override
			public void run() {
				if (matiere.getNoteSession1().getNote() != -1) {
					View tempView1 = (View) liste.getChildAt(1);
					EditText noteS1 = (EditText) tempView1.findViewById(R.id.noteCC);
					noteS1.setText("" + matiere.getNoteSession1().getNote());	
				}
				
				if (matiere.getCoeffPartiel() != -1) {
					View tempView2 = (View) liste.getChildAt(2);
					EditText coeff = (EditText) tempView2.findViewById(R.id.noteCC);
					coeff.setText("" + matiere.getCoeffPartiel());		
				}
				
				if (matiere.getNoteSession2().getNote() != -1) {
					View tempView3 = (View) liste.getChildAt(3);
					EditText noteS2 = (EditText) tempView3.findViewById(R.id.noteCC);
					noteS2.setText("" + matiere.getNoteSession2().getNote());	
				}
			}
		});

		// Liste des notes de CC d�j� entr�es pour la mati�re.
		final ListView liste2 = (ListView) findViewById(R.id.list2);

		final ArrayList<HashMap<String, String>> listItem2 = new ArrayList<HashMap<String, String>>();
		final SimpleAdapter mSchedule2 = new SimpleAdapter (getBaseContext(), listItem2, R.layout.etudiant_saisie_note_note_cc_list, new String[] {"titre", "coeff", "note", "img"}, new int[] {R.id.titre, R.id.coeff_list, R.id.note_list, R.id.img});

		liste2.setAdapter(mSchedule2); 

		TextView headerListe2 = new TextView(activity);
		headerListe2.setText("CONTR�LE CONTINU");
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
				dialog.setTitle("Ajouter une �preuve");
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
		ListView liste = (ListView) findViewById(android.R.id.list);

		View listeS1Et2 = (View) liste.getChildAt(1);
		EditText noteS1ET = (EditText) listeS1Et2.findViewById(R.id.noteCC);

		View listeS2Et2 = (View) liste.getChildAt(3);
		EditText noteS2ET = (EditText) listeS2Et2.findViewById(R.id.noteCC);
		
		View listeCoeffPartiel = (View) liste.getChildAt(2);
		EditText coeffPartiel = (EditText) listeCoeffPartiel.findViewById(R.id.noteCC);
		
		Note noteS1 = new Note();
		noteS1.setNoteType(NoteType.SESSION_1);
		noteS1.setNote(Double.parseDouble(noteS1ET.getText().toString()));

		Note noteS2 = new Note();
		noteS2.setNoteType(NoteType.SESSION_2);
		noteS2.setNote(Double.parseDouble(noteS2ET.getText().toString()));
		
		matiere.setListeNotesCC(listeNotes);
		matiere.setNoteSession1(noteS1);
		matiere.setNoteSession2(noteS2);
		matiere.setCoeffPartiel(Integer.parseInt(coeffPartiel.getText().toString()));

		for (int i = 0; i < this.etudiant.getListeDiplomes().size(); i++) {
			for (int j = 0; j < this.etudiant.getListeDiplomes().get(i).getListeAnnees().size(); j++) {
				for (int k = 0; k < this.etudiant.getListeDiplomes().get(i).getListeAnnees().get(j).getListeUE().size(); k++) {
					for (int l = 0; l < this.etudiant.getListeDiplomes().get(i).getListeAnnees().get(j).getListeUE().get(k).getListeMatieres().size(); l++) {
						if (this.etudiant.getListeDiplomes().get(i).getListeAnnees().get(j).getListeUE().get(k).getListeMatieres().get(l).getId() == this.matiere.getId()) {
							this.etudiant.getListeDiplomes().get(i).getListeAnnees().get(j).getListeUE().get(k).getListeMatieres().remove(l);
							this.etudiant.getListeDiplomes().get(i).getListeAnnees().get(j).getListeUE().get(k).getListeMatieres().add(this.matiere);
						}
					}
				}
			}
		}

		EtudiantDAO.save(this.etudiant);

		finish();
	}

	/* M�thode h�rit�e de la classe Activity. */

	/**
	 * M�thode permettant d'emp�cher la reconstruction de la vue lors de la rotation de l'�cran. 
	 * 
	 * @param newConfig L'�tat de la vue avant la rotation.
	 */
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}
}