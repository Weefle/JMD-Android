package org.gl.jmd.model;

import java.io.Serializable;

import org.gl.jmd.model.enumeration.OperateurType;

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
	 * Le nom de l'UE associée à la règle.
	 */
	private String nomUE;
	
	/**
	 * Constructeur par défaut de la classe.
	 */
	public Regle() {
		
	}
	
	@Override
	public String toString() {
		String res = "";
		
		OperateurType o = null;
		
		if (this.operateur == 0) {
			o = OperateurType.SUPERIEUR;
		} else if (this.operateur == 1) {
			o = OperateurType.SUPERIEUR_EGAL;
		} else if (this.operateur == 2) {
			o = OperateurType.EGAL;
		} else if (this.operateur == 3) {
			o = OperateurType.INFERIEUR_EGAL;
		} else if (this.operateur == 4) {
			o = OperateurType.INFERIEUR;
		} 

		String operateurAffiche = "";

		if (o.equals(OperateurType.SUPERIEUR)) {
			operateurAffiche = "supérieure";
		} else if (o.equals(OperateurType.SUPERIEUR_EGAL)) {
			operateurAffiche = "supérieure ou égale";
		} else if (o.equals(OperateurType.EGAL)) {
			operateurAffiche = "égale";
		} else if (o.equals(OperateurType.INFERIEUR_EGAL)) {
			operateurAffiche = "inférieure ou égale";
		} else if (o.equals(OperateurType.INFERIEUR)) {
			operateurAffiche = "inférieure";
		}
		
		if (this.regle == 1) {
			res = "Nb d'option(s) minimale(s) " + operateurAffiche + " à " + this.valeur;
		} else if (this.regle == 2) {
			res = "Note minimale " + operateurAffiche + " à " + this.valeur;
		} 
		
		return res;
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
	
	/**
	 * Méthode retournant le nom de l'UE associée à la règle.
	 * 
	 * @return Le nom de l'UE associée à la règle.
	 */
	public String getNomUE() {
		return this.nomUE;
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
	
	/**
	 * Méthode permettant de modifier le nom de l'UE associée à la règle.
	 * 
	 * @param nomUE Le nouveau nom de l'UE associée à la règle.
	 */
	public void setNomUE(String nomUE) {
		this.nomUE = nomUE;
	}
}