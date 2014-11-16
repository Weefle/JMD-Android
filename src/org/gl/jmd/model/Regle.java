package org.gl.jmd.model;

import java.io.Serializable;

/**
 * Classe représentant une règle de gestion.
 * 
 * @author Jordi CHARPENTIER & Yoann VANHOESERLANDE
 */
public class Regle implements Serializable {
	
	private static final long serialVersionUID = 1L;

	/**
	 * L'identifiant de la règle.
	 */
	private int id;
	
	/**
	 * La règle (1 - NB_OPT_MINI, 2 - NOTE_MINIMALE).
	 */
	private int regle;
	
	/**
	 * L'opérateur de la règle (>, <, <=, ...).
	 */
	private int operateur;
	
	/**
	 * La valeur de la règle.
	 */
	private int valeur;
	
	/**
	 * L'identifiant de l'UE sur laquelle s'applique la règle.
	 */
	private int idUE;
	
	/**
	 * L'identifiant de l'année sur laquelle s'applique la règle.
	 */
	private int idAnnee;
	
	/**
	 * L'identifiant de la matière sur laquelle s'applique la règle.
	 */
	private int idMatiere;
	
	/**
	 * Constructeur par défaut de la classe.
	 */
	public Regle() {
		
	}

	/* Getters. */
	
	/**
	 * Méthode retournant l'identifiant de la règle.
	 * 
	 * @return L'identifiant de la règle.
	 */
	public int getId() {
		return this.id;
	}

	/**
	 * Méthode retournant la règle (1 - NB_OPT_MINI, 2 - NOTE_MINIMALE).
	 * 
	 * @return La règle (1 - NB_OPT_MINI, 2 - NOTE_MINIMALE).
	 */
	public int getRegle() {
		return this.regle;
	}

	/**
	 * Méthode retournant l'opérateur de la règle (>, <, <=, ...).
	 * 
	 * @return L'opérateur de la règle.
	 */
	public int getOperateur() {
		return this.operateur;
	}

	/**
	 * Méthode retournant la valeur de la règle.
	 * 
	 * @return La valeur de la règle.
	 */
	public int getValeur() {
		return this.valeur;
	}
	
	/**
	 * Méthode retournant l'identifiant de l'UE sur laquelle s'applique la règle.
	 * 
	 * @return L'identifiant de l'UE sur laquelle s'applique la règle.
	 */
	public int getIdUE() {
		return this.idUE;
	}

	/**
	 * Méthode retournant l'identifiant de l'année sur laquelle s'applique la règle.
	 * 
	 * @return L'identifiant de l'année sur laquelle s'applique la règle.
	 */
	public int getIdAnnee() {
		return this.idAnnee;
	}
	
	/**
	 * Méthode retournant l'identifiant de la matière sur laquelle s'applique la règle.
	 * 
	 * @return L'identifiant de la matière sur laquelle s'applique la règle.
	 */
	public int getIdMatiere() {
		return this.idMatiere;
	}
	
	/* Setters. */

	/**
	 * Méthode permettant de modifier l'identifiant de la règle.
	 * 
	 * @param id Le nouvel identifiant de la règle.
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Méthode permettant de modifier la règle.
	 * 
	 * @param regle La nouvelle règle (1 - NB_OPT_MINI, 2 - NOTE_MINIMALE).
	 */
	public void setRegle(int regle) {
		this.regle = regle;
	}

	/**
	 * Méthode permettant de modifier l'opérateur de la règle.
	 * 
	 * @param operateur Le nouvel opérateur de la règle.
	 */
	public void setOperateur(int operateur) {
		this.operateur = operateur;
	}

	/**
	 * Méthode permettant de modifier la valeur de la règle.
	 * 
	 * @param valeur La nouvelle valeur de la règle.
	 */
	public void setValeur(int valeur) {
		this.valeur = valeur;
	}

	/**
	 * Méthode permettant de modifier l'identifiant de l'UE sur laquelle s'applique la règle.
	 * 
	 * @param idUE Le nouvel identifiant de l'UE sur laquelle s'applique la règle.
	 */
	public void setIdUE(int idUE) {
		this.idUE = idUE;
	}

	/**
	 * Méthode permettant de modifier l'identifiant de l'année sur laquelle s'applique la règle.
	 * 
	 * @param idUE Le nouvel identifiant de l'année sur laquelle s'applique la règle.
	 */
	public void setIdAnnee(int idAnnee) {
		this.idAnnee = idAnnee;
	}
	
	/**
	 * Méthode permettant de modifier l'identifiant de la matière sur laquelle s'applique la règle.
	 * 
	 * @param idUE Le nouvel identifiant de la matière sur laquelle s'applique la règle.
	 */
	public void setIdMatiere(int idMatiere) {
		this.idMatiere = idMatiere;
	}
}