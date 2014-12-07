package org.gl.jmd.view.etudiant.create;

import java.util.*;

import org.gl.jmd.R;
import org.gl.jmd.dao.EtudiantDAO;
import org.gl.jmd.model.*;

import android.os.*;
import android.view.View;
import android.widget.*;
import android.app.*;
import android.content.DialogInterface;
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

	private Activity activity;
	
	private Toast toast;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.etudiant_saisie_note);
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

		activity = this;
		toast = Toast.makeText(activity, "", Toast.LENGTH_SHORT);
		
		matiere = (Matiere) getIntent().getExtras().getSerializable("matiere");

		initViewTitle();
		initListe();
	}

	private void initViewTitle() {
		// On donne comme titre à la vue le nom de la matière choisie.
		TextView tvTitre = (TextView) findViewById(R.id.etudiant_saisie_note_titre);
		
		if (matiere.getNom().length() < 20) {
			tvTitre.setText(matiere.getNom());
		} else {
			tvTitre.setText(matiere.getNom().substring(0, 20) + "...");
		}
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

		// Si des infos sont déjà stockées pour la matière (note, coeff, ...).
		liste.post(new Runnable() {
			@Override
			public void run() {
				if (matiere.getNoteSession1().getNote() != -1) {
					View tempView1 = (View) liste.getChildAt(1);
					EditText noteS1 = (EditText) tempView1.findViewById(R.id.noteCC);
					noteS1.setText("" + matiere.getNoteSession1().getNote());	
				}

				if (matiere.getCoeffPartiel() != 0.0) {
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

		// Liste des notes de CC déjà entrées pour la matière.
		final ListView liste2 = (ListView) findViewById(R.id.list2);

		final ArrayList<HashMap<String, String>> listItem2 = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> mapTemp = null;

		mapTemp = new HashMap<String, String>();
		mapTemp.put("titre", "Ajouter une note");
		mapTemp.put("img", String.valueOf(R.drawable.ajouter));

		listItem2.add(mapTemp);

		for (int i = 0; i < matiere.getListeNotesCC().size(); i++) {
			mapTemp = new HashMap<String, String>();
			mapTemp.put("titre", matiere.getListeNotesCC().get(i).getNom());
			mapTemp.put("coeff", "Coefficient : " + matiere.getListeNotesCC().get(i).getCoefficient());
			mapTemp.put("note", matiere.getListeNotesCC().get(i).getNote() + "/20");

			listItem2.add(mapTemp);

			listeNotes.add(matiere.getListeNotesCC().get(i));
		}

		final SimpleAdapter mSchedule2 = new SimpleAdapter (getBaseContext(), listItem2, R.layout.etudiant_saisie_note_note_cc_list, new String[] {"titre", "coeff", "note", "img"}, new int[] {R.id.titre, R.id.coeff_list, R.id.note_list, R.id.img});

		liste2.setAdapter(mSchedule2); 

		TextView headerListe2 = new TextView(activity);
		headerListe2.setText("CONTRÔLE CONTINU");
		headerListe2.setPadding(50, 0, 0, 20);
		headerListe2.setTextSize(16);

		liste2.addHeaderView(headerListe2);

		liste2.post(new Runnable() {
			@Override
			public void run() {
				View tempView1 = (View) liste2.getChildAt(1);
				
				TextView tvTitre = (TextView) tempView1.findViewById(R.id.titre);
				
				RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)tvTitre.getLayoutParams();
				layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
				layoutParams.setMargins(250, 0, 0, 0);
				
				tvTitre.setLayoutParams(layoutParams);
				
				TextView tvBadgeNote = (TextView) tempView1.findViewById(R.id.note_list);
				tvBadgeNote.setVisibility(View.GONE);

				liste2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					public void onItemClick(AdapterView<?> arg0, View arg1, final int position, long arg3) {
						if (position == 1) {
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
	
									if ((epreuveNom.getText().toString().length() > 0) && 
											(epreuveNote.getText().toString().length() > 0) && 
												(epreuveCoeff.getText().toString().length() > 0)) {
										
										Note note = new Note();
										note.setCoefficient(Integer.parseInt(epreuveCoeff.getText().toString()));
										note.setNote(Double.parseDouble(epreuveNote.getText().toString()));
										note.setNom(epreuveNom.getText().toString());
		
										listeNotes.add(note);
		
										HashMap<String, String> mapTemp = null;
		
										mapTemp = new HashMap<String, String>();
										mapTemp.put("titre", note.getNom());
										mapTemp.put("coeff", "Coefficient : " + note.getCoefficient());
										mapTemp.put("note", note.getNote() + "/20");
		
										listItem2.add(mapTemp);
		
										mSchedule2.notifyDataSetChanged();
		
										dialog.hide();
									} else {
										boolean isNomOK = true;
										boolean isNoteOK = true;
										boolean isCoeffOK = true;
										
										String txtToast = "";
										
										if (epreuveNom.getText().toString().length() == 0) {
											epreuveNom.setBackgroundResource(R.drawable.border_edittext_error);
											isNomOK = false;
										} else {
											epreuveNom.setBackgroundResource(R.drawable.border_edittext);
										}
										
										if (epreuveNote.getText().toString().length() == 0) {
											epreuveNote.setBackgroundResource(R.drawable.border_edittext_error);
											isNoteOK = false;
										} else {
											epreuveNote.setBackgroundResource(R.drawable.border_edittext);
										}
										
										if (epreuveCoeff.getText().toString().length() == 0) {
											epreuveCoeff.setBackgroundResource(R.drawable.border_edittext_error);
											isCoeffOK = false;
										} else {
											epreuveCoeff.setBackgroundResource(R.drawable.border_edittext);
										}
										
										if (!isNomOK && !isNoteOK && !isCoeffOK) {
											txtToast = "Les trois champs sont vides.";
										} else {
											if (!isNomOK) {
												txtToast = "Le champ \"Nom\" est vide.\n";
											}
											
											if (!isNoteOK) {
												txtToast = "Le champ \"Note\" est vide.\n";
											}
											
											if (!isCoeffOK) {
												txtToast = "Le champ \"Coefficient\" est vide.";
											}
										}
										
										toast.setText(txtToast);
										toast.show();
									}
								}
							});
	
							dialog.show();
						} 
					}
				});
				
				liste2.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
					public boolean onItemLongClick(AdapterView<?> arg0, View arg1, final int arg2, final long arg3) {
						if (arg2 > 1) {
							final AlertDialog.Builder confirmQuitter = new AlertDialog.Builder(SaisieNoteE.this);
							confirmQuitter.setTitle("Suppression");
							confirmQuitter.setMessage("Voulez-vous vraiment supprimer cette note ?");
							confirmQuitter.setCancelable(false);
							confirmQuitter.setPositiveButton("Oui", new AlertDialog.OnClickListener() {
								public void onClick(DialogInterface dialog, int id) {	
									listeNotes.remove(arg2 - 2);
									listItem2.remove(arg2 - 1);
									
									mSchedule2.notifyDataSetChanged();
								}
							});
	
							confirmQuitter.setNegativeButton("Non", null);
							confirmQuitter.show();
						}
						
						return true;
					}
				}); 
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

		if (noteS1ET.getText().toString().length() > 0) {
			Note noteS1 = new Note();
			noteS1.setNote(Double.parseDouble(noteS1ET.getText().toString()));
			
			matiere.setNoteSession1(noteS1);
		} else {
			matiere.setNoteSession1(new Note());
		}
		
		if (noteS2ET.getText().toString().length() > 0) {
			Note noteS2 = new Note();
			noteS2.setNote(Double.parseDouble(noteS2ET.getText().toString()));
			
			matiere.setNoteSession2(noteS2);
		} else {
			matiere.setNoteSession2(new Note());
		}
		
		matiere.setListeNotesCC(listeNotes);
		
		if (coeffPartiel.getText().toString().length() > 0) {
			matiere.setCoeffPartiel(Double.parseDouble(coeffPartiel.getText().toString()));
		} else {
			matiere.setCoeffPartiel(0.0);
		}
		
		// Contrôle des informations saisies.
		
		// Si une note de seconde session est définie mais pas de note de première session.
		if ((noteS2ET.getText().toString().length() > 0) && (noteS1ET.getText().toString().length() == 0)) {
			noteS1ET.setBackgroundResource(R.drawable.border_edittext_error);
			noteS2ET.setBackgroundResource(R.drawable.border_edittext);
			
			toast.setText("Il faut définir une note de première session avant d'en définir une note de seconde.");
			toast.show();
			
			return;
		}
		
		// Si uniquement un coefficient de partiel est définie.
		if (((noteS1ET.getText().toString().length() == 0) && (noteS2ET.getText().toString().length() == 0))
				&& (coeffPartiel.getText().toString().length() > 0)) {
			
			noteS1ET.setBackgroundResource(R.drawable.border_edittext_error);
			noteS2ET.setBackgroundResource(R.drawable.border_edittext_error);
			
			toast.setText("Il faut définir au moins une note de première session.");
			toast.show();
					
			return;
		}
		
		// Si au moins un contrôle continu existe mais que le coefficient partiel n'est pas défini.
		if ((matiere.getListeNotesCC().size() > 0) && (coeffPartiel.getText().toString().length() == 0)) {
			coeffPartiel.setBackgroundResource(R.drawable.border_edittext_error);
			
			toast.setText("Il faut définir un coefficient pour le partiel s'il y a eu un contrôle continu.");
			toast.show();
			
			return;
		}
		
		ProgressDialog progress = new ProgressDialog(activity);
		progress.setMessage("Chargement...");
		new SaveAll(progress).execute();	
	}
	
	/* Classe interne. */
	
	private class SaveAll extends AsyncTask<Void, Void, Void> {
		private ProgressDialog progress;

		public SaveAll(ProgressDialog progress) {
			this.progress = progress;
		}

		public void onPreExecute() {
			progress.show();
		}

		public void onPostExecute(Void unused) {
			progress.dismiss();
		}

		protected Void doInBackground(Void... arg0) {			
			// Sauvegarde de la matière.
			for (int i = 0; i < etudiant.getListeDiplomes().size(); i++) {
				for (int j = 0; j < etudiant.getListeDiplomes().get(i).getListeAnnees().size(); j++) {
					for (int k = 0; k < etudiant.getListeDiplomes().get(i).getListeAnnees().get(j).getListeUE().size(); k++) {
						for (int l = 0; l < etudiant.getListeDiplomes().get(i).getListeAnnees().get(j).getListeUE().get(k).getListeMatieres().size(); l++) {
							if (etudiant.getListeDiplomes().get(i).getListeAnnees().get(j).getListeUE().get(k).getListeMatieres().get(l).getId() == matiere.getId()) {
								etudiant.getListeDiplomes().get(i).getListeAnnees().get(j).getListeUE().get(k).getListeMatieres().remove(l);
								etudiant.getListeDiplomes().get(i).getListeAnnees().get(j).getListeUE().get(k).getListeMatieres().add(matiere);
							}
						}
					}
				}
			}

			EtudiantDAO.save(etudiant);

			// Fin de la vue.
			
			finish();

			return null;
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