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
import android.content.Intent;
import android.content.res.Configuration;

/**
 * Activit� correspondant � la vue d'ajout d'une note pour un �tudiant.
 * 
 * @author Jordi CHARPENTIER & Yoann VANHOESERLANDE
 */
public class SaisieNoteE extends Activity {

	private Etudiant etud = EtudiantDAO.load();
	
	private Intent lastIntent;
	
	private int idDiplome = 0;
	
	private int idAnnee = 0;
	
	private int idUE = 0;
	
	private int idMatiere = 0;
	
	private Matiere mat;
	
	private Toast toast;

	private Activity activity;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.etudiant_saisie_note);
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
		
		activity = this;
		toast = Toast.makeText(activity, "", Toast.LENGTH_SHORT);
		
		lastIntent = getIntent();
		
		idDiplome = lastIntent.getExtras().getInt("idDiplome");
		idAnnee = lastIntent.getExtras().getInt("idAnnee");
		idUE = lastIntent.getExtras().getInt("idUE");
		idMatiere = Integer.parseInt(lastIntent.getExtras().getString("idMatiere"));
	
		mat = (Matiere) lastIntent.getSerializableExtra("matiere");
		
		initListe();
	}
	
	public void initListe() {
		// Si la mati�re a d�j� une note de 1�re session et une note de seconde session, on les recherche.
		double note1S = 0.0;
		double note2S = 0.0;
		
		for (int i = 0; i < mat.getListeNotes().size(); i++) {
			if (mat.getListeNotes().get(i).getNoteType() == NoteType.SESSION_1) {
				note1S = mat.getListeNotes().get(i).getNote();
			} else if (mat.getListeNotes().get(i).getNoteType() == NoteType.SESSION_2) {
				note2S = mat.getListeNotes().get(i).getNote();
			}
		}
		
		// Initialisation de la liste.		
		final ListView liste = (ListView) findViewById(android.R.id.list);

		final ArrayList<HashMap<String, String>> listItem = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> map;

		map = new HashMap<String, String>();
		map.put("titre", "1�re session");
		
		if (note1S > 0.0) {
			map.put("note", "" + note1S);
		} 
		
		listItem.add(map);
		
		map = new HashMap<String, String>();
		map.put("titre", "2�me session");
		
		if (note2S > 0.0) {
			map.put("note", "" + note2S);
		} 
		
		listItem.add(map);

		map = new HashMap<String, String>();
		map.put("titre", "Contr�le continu");
		
		Fraction coeffCC = mat.getCoefficientControleContinu();
		map.put("note", (coeffCC == null ? "" : coeffCC.get()));
		
		listItem.add(map);
		
		SimpleAdapter mSchedule = new SimpleAdapter (getBaseContext(), listItem, R.layout.etudiant_saisie_note_list, new String[] {"titre", "note"}, new int[] {R.id.titre, R.id.note_list1});

		liste.setAdapter(mSchedule); 
		
		// Liste des notes de CC d�j� entr�es pour la mati�re.
		final ListView liste2 = (ListView) findViewById(R.id.list2);

		final ArrayList<HashMap<String, String>> listItem2 = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> map2 = null;
		
		for (int i = 0; i < mat.getListeNotes().size(); i++) {
			if (mat.getListeNotes().get(i).getNoteType() == NoteType.CONTROLE_CONTINU) {
				map2 = new HashMap<String, String>();
				map2.put("titre", "Note");
				map2.put("coeff", "" + mat.getListeNotes().get(i).getCoefficient().get());
				map2.put("note", "" + mat.getListeNotes().get(i).getNote());
				
				listItem2.add(map2);
			}
		}
		
		SimpleAdapter mSchedule2 = new SimpleAdapter (getBaseContext(), listItem2, R.layout.etudiant_saisie_note_note_cc_list, new String[] {"titre", "coeff", "note"}, new int[] {R.id.titre, R.id.coeff_list, R.id.note_list});

		liste2.setAdapter(mSchedule2); 
		
		Button buttonAddNote = (Button) findViewById(R.id.etudiant_saisie_bout_ajouter_note_cc);
		
		buttonAddNote.setOnClickListener(new View.OnClickListener() {
		    @Override
		    public void onClick(View v) {
		    	View listeCoeffNote = (View) liste.getChildAt(2);
				EditText coeffNote = (EditText) listeCoeffNote.findViewById(R.id.note_list1);
				
				if ((coeffNote.getText().toString() == null) || (coeffNote.getText().toString().length() == 0)) {
					toast.setText("Il faut sp�cifier un coefficient pour le contr�le continu avant de rentrer une note.");
					toast.show();
					
					return;
				}
				
				// R�cup�ration du coefficient. 
				// On parse le texte entr�.
				StringTokenizer chaineEclate = new StringTokenizer(coeffNote.getText().toString(), "/");
				
				final String[] temp = new String[chaineEclate.countTokens()];
				int k = 0;

				while(chaineEclate.hasMoreTokens()) {
					temp[k++] = chaineEclate.nextToken();	
				}

				try {
					Integer.parseInt(temp[0]); // num�rateur
					Integer.parseInt(temp[1]); // d�nominateur
				} catch (Exception e) {
					toast.setText("Le coefficient du contr�le continu doit �tre une fraction.");
					toast.show();
					
					return;
				}
				
			    HashMap<String, String> mapTmp = new HashMap<String, String>();
			    mapTmp.put("titre", "Note");
			    mapTmp.put("coeff", "");
			    mapTmp.put("note", "");
					
				listItem2.add(mapTmp);
					
				SimpleAdapter mSchedule2 = new SimpleAdapter (getBaseContext(), listItem2, R.layout.etudiant_saisie_note_note_cc_list, new String[] {"titre", "coeff", "note"}, new int[] {R.id.titre, R.id.coeff_list, R.id.note_list});
	
				liste2.setAdapter(mSchedule2); 
		    }
		});
		
		Button button = (Button) findViewById(R.id.etudiant_saisie_bout_ajouter);
		
		button.setOnClickListener(new View.OnClickListener() {
		    @Override
		    public void onClick(View v) {
		    	ArrayList<Note> listeNotesCC = new ArrayList<Note>();
		    	
		    	if (liste2.getChildCount() > 0) {
			    	// Somme des coefficients des notes de contr�le continu entr�es.
					// Si != 1, c'est une erreur.
					
					Fraction sommeCoeff = new Fraction(0, 0);
					
					for (int i = 0; i < liste2.getChildCount(); i++) {
						View viewTmp = (View) liste2.getChildAt(i);
						
						EditText noteTmp = (EditText) viewTmp.findViewById(R.id.note_list);
						EditText coeffTmp = (EditText) viewTmp.findViewById(R.id.coeff_list);
						
						if (noteTmp.getText().toString().length() > 0) {						
							// R�cup�ration du coefficient. 
							// On parse le texte entr�.
							StringTokenizer chaineEclate = new StringTokenizer(coeffTmp.getText().toString(), "/");
							
							final String[] temp = new String[chaineEclate.countTokens()];
							int k = 0;
		
							while(chaineEclate.hasMoreTokens()) {
								temp[k++] = chaineEclate.nextToken();	
							}
							
							int numerateur = 0;
							int denominateur = 0;
		
							try {
								numerateur = Integer.parseInt(temp[0]);
								denominateur = Integer.parseInt(temp[1]);
							} catch (Exception e) {
								toast.setText("Le coefficient du contr�le continu doit �tre une fraction.");
								toast.show();
								
								return;
							}
							
							Fraction currentCoeff = new Fraction(numerateur, denominateur);
							
							sommeCoeff = sommeCoeff.sum(currentCoeff);
							
							Note note = new Note();
							note.setNoteType(NoteType.CONTROLE_CONTINU);
							note.setNote(Double.parseDouble(noteTmp.getText().toString()));
							note.setCoefficient(currentCoeff);
							
							listeNotesCC.add(note);
						}
					}
					
					if (!(sommeCoeff.getNumerateur() == sommeCoeff.getDenominateur()) && (sommeCoeff.getNumerateur() != 0)) {
						toast.setText("La somme des coefficients des notes du contr�le continu doit valoir 1.");
						toast.show(); 
						
						return;
					}
		    	}
		    	
		    	View listItemSession1 = (View) liste.getChildAt(0);
				EditText noteET = (EditText) listItemSession1.findViewById(R.id.note_list1);
				
				View listItemSession2 = (View) liste.getChildAt(1);
				EditText noteSession2ET = (EditText) listItemSession2.findViewById(R.id.note_list1);
				
				boolean has2ndeSession = false;
				
				double note = 0.0;
				
				try {
					note = Double.parseDouble(noteET.getText().toString());
				} catch (Exception e) {
					toast.setText("Il faut au moins rentrer une note de 1�re session.");
					toast.show(); 
					
					return;
				}
				
				double noteSession2 = 0.0;
				
				if (noteSession2ET.getText().toString().length() > 0) {
					noteSession2 = Double.parseDouble(noteSession2ET.getText().toString());
					has2ndeSession = true;
				}
				
				View listeCoeffNote = (View) liste.getChildAt(2);
				EditText coeffNote = (EditText) listeCoeffNote.findViewById(R.id.note_list1);
							
				if ((note >= 0.0) && (note <= 20.0)) {
					for (int i = 0; i < etud.getListeDiplomes().size(); i++) {
						for (int j = 0; j < etud.getListeDiplomes().get(i).getListeAnnees().size(); j++) {
							for (int k = 0; k < etud.getListeDiplomes().get(i).getListeAnnees().get(j).getListeUE().size(); k++) {
								for (int l = 0; l < etud.getListeDiplomes().get(i).getListeAnnees().get(j).getListeUE().get(k).getListeMatieres().size(); l++) {
									if ((etud.getListeDiplomes().get(i).getId() == idDiplome) &&
										(etud.getListeDiplomes().get(i).getListeAnnees().get(j).getId() == idAnnee) &&
										(etud.getListeDiplomes().get(i).getListeAnnees().get(j).getListeUE().get(k).getId() == idUE) &&
										(etud.getListeDiplomes().get(i).getListeAnnees().get(j).getListeUE().get(k).getListeMatieres().get(l).getId() == idMatiere)) {
										
										etud.getListeDiplomes().get(i).getListeAnnees().get(j).getListeUE().get(k).getListeMatieres().get(l).getListeNotes().clear();
										
										// Si un coefficient de contr�le continu a �t� rentr�.
										if (coeffNote.getText().toString().length() > 0) {
											StringTokenizer chaineEclate = new StringTokenizer(coeffNote.getText().toString(), "/");
											
											final String[] temp = new String[chaineEclate.countTokens()];
											int o = 0;
						
											while(chaineEclate.hasMoreTokens()) {
												temp[o++] = chaineEclate.nextToken();	
											}
											
											int numerateur = 0;
											int denominateur = 0;
						
											try {
												numerateur = Integer.parseInt(temp[0]);
												denominateur = Integer.parseInt(temp[1]);
											} catch (Exception e) {
												toast.setText("Le coefficient du CC doit �tre une fraction.");
												toast.show();
												
												return;
											}
											
											Fraction coeffCC = new Fraction(numerateur, denominateur);
											
											etud.getListeDiplomes().get(i).getListeAnnees().get(j).getListeUE().get(k).getListeMatieres().get(l).setCoefficientControleContinu(coeffCC);
										}
										
										Note n = new Note();
										n.setNote(note);
										n.setNoteType(NoteType.SESSION_1);
										
										etud.getListeDiplomes().get(i).getListeAnnees().get(j).getListeUE().get(k).getListeMatieres().get(l).getListeNotes().add(n);
										
										if (has2ndeSession) {
											Note nS2 = new Note();
											nS2.setNote(noteSession2);
											nS2.setNoteType(NoteType.SESSION_2);
											
											etud.getListeDiplomes().get(i).getListeAnnees().get(j).getListeUE().get(k).getListeMatieres().get(l).getListeNotes().add(nS2);
										}
										
										etud.getListeDiplomes().get(i).getListeAnnees().get(j).getListeUE().get(k).getListeMatieres().get(l).getListeNotes().addAll(listeNotesCC);
										
										if (EtudiantDAO.save(etud)) {
											finish();
										} else {
											toast.setText("Erreur lors de la sauvegarde de la note.");
											toast.show(); 
											
											return;
										}
									}
								}
							}
						}
					}
				} else {
					toast.setText("La note doit �tre comprise entre 0 et 20.");
					toast.show(); 
				}
		    }
		});
		
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