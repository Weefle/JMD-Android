package org.gl.jmd.model;

import java.io.Serializable;
import java.util.ArrayList;

import org.gl.jmd.model.enumeration.NoteType;
import org.gl.jmd.utils.NumberUtils;

/**
 * Classe repr�sentant une mati�re.
 * 
 * @author Jordi CHARPENTIER & Yoann VANHOESERLANDE
 */
public class Matiere implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/**
	 * Identifiant de la mati�re.
	 */
	private int id;
	
	/**
	 * Le nom de la mati�re.
	 */
	private String nom;
	
	/**
	 * Le coefficient de la mati�re.
	 */
	private int coefficient;
	
	/**
	 * Bool�en permettant de savoir si la mati�re est une option, ou non.
	 */
	private boolean isOption;
	
	/**
	 * Le coefficient du contr�le continu de la mati�re.
	 */
	private Fraction coeffCC;
	
	/**
	 * Les notes de la mati�re.
	 */
	private ArrayList<Note> listeNotes = new ArrayList<Note>();
	
	/**
	 * Les r�gles que devra respecter la mati�re.
	 */
	private ArrayList<Regle> listeRegles = new ArrayList<Regle>();
	
	/**
	 * Constructeur par d�faut de la classe.
	 */
	public Matiere() {
		
	}
	
	/* Getters. */
	
	/**
	 * M�thode retournant l'identifiant de la mati�re.
	 * 
	 * @return L'identifiant de la mati�re.
	 */
	public int getId() {
		return this.id;
	}

	/**
	 * M�thode retournant le nom de la mati�re.
	 * 
	 * @return Le nom de la mati�re.
	 */
	public String getNom() {
		return this.nom;
	}
	
	/**
	 * M�thode retournant le coefficient de la mati�re.
	 * 
	 * @return Le coefficient de la mati�re.
	 */
	public int getCoefficient() {
		return this.coefficient;
	}
	
	/**
	 * M�thode permettant de savoir si la mati�re est une option, ou non.
	 * 
	 * @return <b>true</b> si la mati�re est une option.<br />
	 * <b>false</b> sinon.
	 */
	public boolean isOption() {
		return this.isOption;
	}
	
	/**
	 * M�thode retournant le coefficient du contr�le continu de la mati�re.
	 * 
	 * @return Le coefficient du contr�le continu de la mati�re.
	 */
	public Fraction getCoefficientControleContinu() {
		return this.coeffCC;
	}

	/**
	 * M�thode retournant les notes de la mati�re.
	 * 
	 * @return Les notes de la mati�re.
	 */
	public ArrayList<Note> getListeNotes() {
		return this.listeNotes;
	}
	
	/**
	 * M�thode retournant les r�gles de la mati�re.
	 * 
	 * @return Les r�gles de la mati�re.
	 */
	public ArrayList<Regle> getListeRegles() {
		return this.listeRegles;
	}
	
	/* Setters. */
	
	/**
	 * M�thode permettant de modifier l'identifiant de la mati�re.
	 * 
	 * @param id Le nouvel identifiant de la mati�re.
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * M�thode permettant de modifier le nom de la mati�re.
	 * 
	 * @param nom Le nouveau nom de la mati�re.
	 */
	public void setNom(String nom) {
		this.nom = nom;
	}
	
	/**
	 * M�thode permettant de modifier le coefficient de la mati�re.
	 * 
	 * @param coefficient Le nouveau coefficient de la mati�re.
	 */
	public void setCoefficient(int coefficient) {
		this.coefficient = coefficient;
	}
	
	/**
	 * M�thode permettant de modifier le fait que la mati�re soit une option, ou non. 
	 * 
	 * @param isOption Le bool�an repr�sentant le fait que la mati�re soit une option, ou non.
	 */
	public void setIsOption(boolean isOption) {
		this.isOption = isOption;
	}
	
	/**
	 * M�thode permettant de modifier le coefficient du contr�le continu de la mati�re.
	 * 
	 * @param coeffCC Le nouveau coefficient du contr�le continu de la mati�re.
	 */
	public void setCoefficientControleContinu(Fraction coeffCC) {
		this.coeffCC = coeffCC;
	}

	/**
	 * M�thode permettant de modifier les notes de la mati�re.
	 * 
	 * @param listeNotes Les nouvelle notes de la mati�re.
	 */
	public void setListeNotes(ArrayList<Note> listeNotes) {
		this.listeNotes = listeNotes;
	}
	
	/**
	 * M�thode permettant de modifier les r�gles de la mati�re.
	 * 
	 * @param listeRegles Les nouvelle r�gles de la mati�re.
	 */
	public void setListeRegles(ArrayList<Regle> listeRegles) {
		this.listeRegles = listeRegles;
	}
	
	/* Autres. */
	
	/**
	 * M�thode permettant de calculer la note finale de la mati�re.
	 * 
	 * @return La note finale de la mati�re.
	 */
	public double getNoteFinale() {
		double res = -1;
		
		if (this.listeNotes.size() > 0) {
			boolean hasControleContinu = false;
			
			double note = 0.0;
			double noteS1 = 0.0;
			double noteS2 = 0.0;
			
			for (int i = 0; i < this.listeNotes.size(); i++) {
				if (this.listeNotes.get(i).getNoteType() == NoteType.SESSION_1) {
					noteS1 = this.listeNotes.get(i).getNote();
				} else if (this.listeNotes.get(i).getNoteType() == NoteType.SESSION_2) {
					noteS2 = this.listeNotes.get(i).getNote();
				} else if (this.listeNotes.get(i).getNoteType() == NoteType.CONTROLE_CONTINU) {
					hasControleContinu = true;
				}
			}
			
			if (noteS1 > noteS2) {
				note = noteS1;
			} else {
				note = noteS2;
			}
	
			if (hasControleContinu) {
				double moyenneCC = 0.0;
				
				for (int i = 0; i < this.listeNotes.size(); i++) {
					if (this.listeNotes.get(i).getNoteType() == NoteType.CONTROLE_CONTINU) {
						moyenneCC += this.listeNotes.get(i).getCoefficient().multiplyByNote(this.listeNotes.get(i).getNote());
					}
				}
				
				res = coeffCC.multiplyByNote(moyenneCC) + coeffCC.getOppose().multiplyByNote(note);
			} else {
				res = note;
			}
			
			res = NumberUtils.round(res, 2);
		} else {
			res = -2;
		}
		
		return res;
	}
}
