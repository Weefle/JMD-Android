package org.gl.jmd.model;

import java.io.Serializable;

import org.gl.jmd.model.enumeration.NoteType;

/**
 * Classe représentant une note.
 * 
 * @author Jordi CHARPENTIER & Yoann VANHOESERLANDE
 */
public class Note implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * Identifiant de la note.
	 */
	private int id;
	
	/**
	 * Le nom de l'épreuve associée à la note.
	 */
	private String nom;
	
	/**
	 * Le type (1ère session, 2ème session, contrôle continu) de la note.
	 */
	private NoteType type;
	
	/**
	 * La valeur de la note.
	 */
	private double note = -1.0;
	
	/**
	 * Le coefficient de la note SI elle est de type "Contrôle Continu".
	 */
	private int coefficient;
	
	/**
	 * Constructeur par défaut de la classe.
	 */
	public Note() {
		
	}
	
	/* Getters. */

	/**
	 * Méthode retournant l'identifiant de la note.
	 * 
	 * @return L'identifiant de la note.
	 */
	public int getId() {
		return this.id;
	}
	
	/**
	 * Méthode retournant le nom de l'épreuve associée à la note.
	 * 
	 * @return Le nom de l'épreuve associée à la note.
	 */
	public String getNom() {
		return this.nom;
	}
	
	/**
	 * Méthode retournant le type de la note.
	 * 
	 * @return Le type de la note.
	 */
	public NoteType getNoteType() {
		return this.type;
	}

	/**
	 * Méthode retournant la valeur de la note.
	 * 
	 * @return La valeur de la note.
	 */
	public double getNote() {
		return this.note;
	}
	
	/**
	 * Méthode retournant le coefficient de la note SI elle est de type "Contrôle Continu".
	 * 
	 * @return Le coefficient de la note SI elle est de type "Contrôle Continu".
	 */
	public int getCoefficient() {
		return this.coefficient;
	}
	
	/* Setters. */
	
	/**
	 * Méthode permettant de modifier l'identifiant de la note.
	 * 
	 * @param id Le nouvel identifiant de la note.
	 */
	public void setId(int id) {
		this.id = id;
	}
	
	/**
	 * Méthode permettant de modifier le nom de l'épreuve associée à la note.
	 * 
	 * @param nom Le nouveau nom de l'épreuve associée à la note.
	 */
	public void setNom(String nom) {
		this.nom = nom;
	}
	
	/**
	 * Méthode permettant de modifier le type de la note.
	 * 
	 * @param type Le nouveau type de la note.
	 */
	public void setNoteType(NoteType type) {
		this.type = type;
	}

	/**
	 * Méthode permettant de modifier la valeur de la note.
	 * 
	 * @param note La nouvelle valeur de la note.
	 */
	public void setNote(double note) {
		this.note = note;
	}
	
	/**
	 * Méthode permettant de modifier le coefficient de la note SI elle est de type "Contrôle Continu".
	 * 
	 * @param coefficient Le nouveau coefficient de la note SI elle est de type "Contrôle Continu".
	 */
	public void setCoefficient(int coefficient) {
		this.coefficient = coefficient;
	}
}
