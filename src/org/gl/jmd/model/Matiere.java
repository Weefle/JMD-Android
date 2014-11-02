package org.gl.jmd.model;

import java.io.Serializable;
import java.util.ArrayList;

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
	private float coefficient;
	
	/**
	 * Booléen permettant de savoir si la matière est une option, ou non.
	 */
	private boolean isOption;
	
	/**
	 * Le coefficient du partiel.
	 */
	private double coeffPartiel = 0;
	
	/**
	 * La note de première session.
	 */
	private Note noteSession1 = new Note();
	
	/**
	 * La note de seconde session.
	 */
	private Note noteSession2 = new Note();
	
	/**
	 * Les notes de contrôle continu de la matière.
	 */
	private ArrayList<Note> listeNotesCC = new ArrayList<Note>();
	
	/**
	 * Les règles que devra respecter la matière.
	 */
	private ArrayList<Regle> listeRegles = new ArrayList<Regle>();
	
	/**
	 * Constructeur par défaut de la classe.
	 */
	public Matiere() {
		
	}
	
	/**
	 * Méthode permettant de calculer la note finale de la matière.
	 * 
	 * @return La note finale de la matière.
	 */
	public double getNoteFinale() {
		double res = -1;
		
		if ((this.noteSession2 != null) &&
				(this.noteSession2.getNote() > this.noteSession1.getNote())) {
			
			res = this.noteSession2.getNote();
		} else if (this.noteSession1 != null) {
			res = this.noteSession1.getNote();
		}
		
		if (this.listeNotesCC.size() > 0) {
			double sommeNoteCoeff = 0;
			double sommeCoeff = 0;
			
			for (int i = 0; i < this.listeNotesCC.size(); i++) {
				sommeNoteCoeff +=  this.listeNotesCC.get(i).getNote() * this.listeNotesCC.get(i).getCoefficient();
				sommeCoeff += this.listeNotesCC.get(i).getCoefficient();
			}
			
			double moyenneCC = sommeNoteCoeff / sommeCoeff;
			double coeff = (this.coeffPartiel / (this.coeffPartiel + 1));
			double coeffCC = (1.0 / (this.coeffPartiel + 1));
			
			res = NumberUtils.round((res * coeff) + (moyenneCC * coeffCC), 2);
		} 
		
		return res;
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
	public double getCoefficient() {
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
	 * Méthode retournant le coefficient du partiel de la matière.
	 * 
	 * @return Le coefficient du partiel de la matière.
	 */
	public double getCoeffPartiel() {
		return this.coeffPartiel;
	}
	
	/**
	 * Méthode retournant la note de première session de la matière.
	 * 
	 * @return La note de première session de la matière.
	 */
	public Note getNoteSession1() {
		return this.noteSession1;
	}
	
	/**
	 * Méthode retournant la note de seconde session de la matière.
	 * 
	 * @return La note de seconde session de la matière.
	 */
	public Note getNoteSession2() {
		return this.noteSession2;
	}

	/**
	 * Méthode retournant les notes de contrôle continu de la matière.
	 * 
	 * @return Les notes de contrôle continu de la matière.
	 */
	public ArrayList<Note> getListeNotesCC() {
		return this.listeNotesCC;
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
	public void setCoefficient(float coefficient) {
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
	 * Méthode permettant de modifier le coefficient du partiel de la matière.
	 * 
	 * @param coeffPartiel Le nouveau coefficient du partiel de la matière.
	 */
	public void setCoeffPartiel(double coeffPartiel) {
		this.coeffPartiel = coeffPartiel;
	}

	/**
	 * Méthode permettant de modifier la note de première session de la matière.
	 * 
	 * @param noteSession1 La nouvelle note de première session de la matière.
	 */
	public void setNoteSession1(Note noteSession1) {
		this.noteSession1 = noteSession1;
	}
	
	/**
	 * Méthode permettant de modifier la note de seconde session de la matière.
	 * 
	 * @param noteSession1 La nouvelle note de seconde session de la matière.
	 */
	public void setNoteSession2(Note noteSession2) {
		this.noteSession2 = noteSession2;
	}
	
	/**
	 * Méthode permettant de modifier les notes de contrôle continu de la matière.
	 * 
	 * @param listeNotes Les nouvelle notes de contrôle continu de la matière.
	 */
	public void setListeNotesCC(ArrayList<Note> listeNotesCC) {
		this.listeNotesCC = listeNotesCC;
	}
	
	/**
	 * Méthode permettant de modifier les règles de la matière.
	 * 
	 * @param listeRegles Les nouvelle règles de la matière.
	 */
	public void setListeRegles(ArrayList<Regle> listeRegles) {
		this.listeRegles = listeRegles;
	}
}
