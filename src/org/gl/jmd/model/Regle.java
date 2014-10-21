package org.gl.jmd.model;

import java.io.Serializable;

public class Regle implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private int id;
	
	private int regle;
	
	private int operateur;
	
	private int valeur;
	
	private int idUE;
	
	private int idAnnee;
	
	private int idMatiere;
	
	/**
	 * Constructeur par défaut de la classe.
	 */
	public Regle() {
		
	}
	
	/**
	 * Méthode retournant la règle sous la forme d'une chaîne de caractères.
	 * 
	 * @return Une représentation de la règle sous la forme d'une chaîne de caractères.
	 */
	@Override
	public String toString() {
		return this.regle + " " + this.operateur + " " + this.valeur;
	}

	/* Getters. */
	
	public int getId() {
		return this.id;
	}

	public int getRegle() {
		return this.regle;
	}

	public int getOperateur() {
		return this.operateur;
	}

	public int getValeur() {
		return this.valeur;
	}
	
	public int getIdUE() {
		return this.idUE;
	}

	public int getIdAnnee() {
		return this.idAnnee;
	}
	
	public int getIdMatiere() {
		return this.idMatiere;
	}
	
	/* Setters. */

	public void setId(int id) {
		this.id = id;
	}

	public void setRegle(int regle) {
		this.regle = regle;
	}

	public void setOperateur(int operateur) {
		this.operateur = operateur;
	}

	public void setValeur(int valeur) {
		this.valeur = valeur;
	}

	public void setIdUE(int idUE) {
		this.idUE = idUE;
	}

	public void setIdAnnee(int idAnnee) {
		this.idAnnee = idAnnee;
	}
	
	public void setIdMatiere(int idMatiere) {
		this.idMatiere = idMatiere;
	}
}