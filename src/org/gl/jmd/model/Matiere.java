package org.gl.jmd.model;

import java.io.Serializable;
import java.util.ArrayList;

import org.gl.jmd.model.enumeration.NoteType;
import org.gl.jmd.utils.NumberUtils;

/**
 * Classe représentant une matière.
 * 
 * @author Jordi CHARPENTIER & Yoann VANHOESERLANDE
 */
public class Matiere implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/**
	 * Identifiant de la matière.
	 */
	private int id;
	
	/**
	 * Le nom de la matière.
	 */
	private String nom;
	
	/**
	 * Le coefficient de la matière.
	 */
	private int coefficient;
	
	/**
	 * Booléen permettant de savoir si la matière est une option, ou non.
	 */
	private boolean isOption;
	
	/**
	 * Le coefficient du contrôle continu de la matière.
	 */
	private Fraction coeffCC;
	
	/**
	 * Les notes de la matière.
	 */
	private ArrayList<Note> listeNotes = new ArrayList<Note>();
	
	/**
	 * Les règles que devra respecter la matière.
	 */
	private ArrayList<Regle> listeRegles = new ArrayList<Regle>();
	
	/**
	 * Constructeur par défaut de la classe.
	 */
	public Matiere() {
		
	}
	
	/* Getters. */
	
	/**
	 * Méthode retournant l'identifiant de la matière.
	 * 
	 * @return L'identifiant de la matière.
	 */
	public int getId() {
		return this.id;
	}

	/**
	 * Méthode retournant le nom de la matière.
	 * 
	 * @return Le nom de la matière.
	 */
	public String getNom() {
		return this.nom;
	}
	
	/**
	 * Méthode retournant le coefficient de la matière.
	 * 
	 * @return Le coefficient de la matière.
	 */
	public int getCoefficient() {
		return this.coefficient;
	}
	
	/**
	 * Méthode permettant de savoir si la matière est une option, ou non.
	 * 
	 * @return <b>true</b> si la matière est une option.<br />
	 * <b>false</b> sinon.
	 */
	public boolean isOption() {
		return this.isOption;
	}
	
	/**
	 * Méthode retournant le coefficient du contrôle continu de la matière.
	 * 
	 * @return Le coefficient du contrôle continu de la matière.
	 */
	public Fraction getCoefficientControleContinu() {
		return this.coeffCC;
	}

	/**
	 * Méthode retournant les notes de la matière.
	 * 
	 * @return Les notes de la matière.
	 */
	public ArrayList<Note> getListeNotes() {
		return this.listeNotes;
	}
	
	/**
	 * Méthode retournant les règles de la matière.
	 * 
	 * @return Les règles de la matière.
	 */
	public ArrayList<Regle> getListeRegles() {
		return this.listeRegles;
	}
	
	/* Setters. */
	
	/**
	 * Méthode permettant de modifier l'identifiant de la matière.
	 * 
	 * @param id Le nouvel identifiant de la matière.
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Méthode permettant de modifier le nom de la matière.
	 * 
	 * @param nom Le nouveau nom de la matière.
	 */
	public void setNom(String nom) {
		this.nom = nom;
	}
	
	/**
	 * Méthode permettant de modifier le coefficient de la matière.
	 * 
	 * @param coefficient Le nouveau coefficient de la matière.
	 */
	public void setCoefficient(int coefficient) {
		this.coefficient = coefficient;
	}
	
	/**
	 * Méthode permettant de modifier le fait que la matière soit une option, ou non. 
	 * 
	 * @param isOption Le booléan représentant le fait que la matière soit une option, ou non.
	 */
	public void setIsOption(boolean isOption) {
		this.isOption = isOption;
	}
	
	/**
	 * Méthode permettant de modifier le coefficient du contrôle continu de la matière.
	 * 
	 * @param coeffCC Le nouveau coefficient du contrôle continu de la matière.
	 */
	public void setCoefficientControleContinu(Fraction coeffCC) {
		this.coeffCC = coeffCC;
	}

	/**
	 * Méthode permettant de modifier les notes de la matière.
	 * 
	 * @param listeNotes Les nouvelle notes de la matière.
	 */
	public void setListeNotes(ArrayList<Note> listeNotes) {
		this.listeNotes = listeNotes;
	}
	
	/**
	 * Méthode permettant de modifier les règles de la matière.
	 * 
	 * @param listeRegles Les nouvelle règles de la matière.
	 */
	public void setListeRegles(ArrayList<Regle> listeRegles) {
		this.listeRegles = listeRegles;
	}
	
	/* Autres. */
	
	/**
	 * Méthode permettant de calculer la note finale de la matière.
	 * 
	 * @return La note finale de la matière.
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
