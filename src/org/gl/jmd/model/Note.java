package org.gl.jmd.model;

import java.io.Serializable;

import org.gl.jmd.model.enumeration.NoteType;

/**
 * Classe repr�sentant une note.
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
	 * Le type (1�re session, 2�me session, contr�le continu) de la note.
	 */
	private NoteType type;
	
	/**
	 * La valeur de la note.
	 */
	private double note = -1;
	
	/**
	 * Le coefficient de la note SI elle est de type "Contr�le Continu".
	 */
	private Fraction coefficient;
	
	/**
	 * Constructeur par d�faut de la classe.
	 */
	public Note() {
		
	}
	
	/* Getters. */

	/**
	 * M�thode retournant l'identifiant de la note.
	 * 
	 * @return L'identifiant de la note.
	 */
	public int getId() {
		return this.id;
	}
	
	/**
	 * M�thode retournant le type de la note.
	 * 
	 * @return Le type de la note.
	 */
	public NoteType getNoteType() {
		return this.type;
	}

	/**
	 * M�thode retournant la valeur de la note.
	 * 
	 * @return La valeur de la note.
	 */
	public double getNote() {
		return this.note;
	}
	
	/**
	 * M�thode retournant le coefficient de la note SI elle est de type "Contr�le Continu".
	 * 
	 * @return Le coefficient de la note SI elle est de type "Contr�le Continu".
	 */
	public Fraction getCoefficient() {
		return this.coefficient;
	}
	
	/* Setters. */
	
	/**
	 * M�thode permettant de modifier l'identifiant de la note.
	 * 
	 * @param id Le nouvel identifiant de la note.
	 */
	public void setId(int id) {
		this.id = id;
	}
	
	/**
	 * M�thode permettant de modifier le type de la note.
	 * 
	 * @param type Le nouveau type de la note.
	 */
	public void setNoteType(NoteType type) {
		this.type = type;
	}

	/**
	 * M�thode permettant de modifier la valeur de la note.
	 * 
	 * @param note La nouvelle valeur de la note.
	 */
	public void setNote(double note) {
		this.note = note;
	}
	
	/**
	 * M�thode permettant de modifier le coefficient de la note SI elle est de type "Contr�le Continu".
	 * 
	 * @param coefficient Le nouveau coefficient de la note SI elle est de type "Contr�le Continu".
	 */
	public void setCoefficient(Fraction coefficient) {
		this.coefficient = coefficient;
	}
}
