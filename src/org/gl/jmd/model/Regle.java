package org.gl.jmd.model;

import java.io.Serializable;

import org.gl.jmd.model.enumeration.OperateurType;

/**
 * Classe repr�sentant une r�gle de gestion.
 * 
 * @author Jordi CHARPENTIER & Yoann VANHOESERLANDE
 */
public class Regle implements Serializable {
	
	private static final long serialVersionUID = 1L;

	/**
	 * L'identifiant de la r�gle.
	 */
	private int id;
	
	/**
	 * La r�gle (1 - NB_OPT_MINI, 2 - NOTE_MINIMALE).
	 */
	private int regle;
	
	/**
	 * L'op�rateur de la r�gle (>, <, <=, ...).
	 */
	private int operateur;
	
	/**
	 * La valeur de la r�gle.
	 */
	private int valeur;
	
	/**
	 * L'identifiant de l'UE sur laquelle s'applique la r�gle.
	 */
	private int idUE;
	
	/**
	 * L'identifiant de l'ann�e sur laquelle s'applique la r�gle.
	 */
	private int idAnnee;
	
	/**
	 * L'identifiant de la mati�re sur laquelle s'applique la r�gle.
	 */
	private int idMatiere;
	
	/**
	 * Le nom de l'UE associ�e � la r�gle.
	 */
	private String nomUE;
	
	/**
	 * Constructeur par d�faut de la classe.
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
			operateurAffiche = "sup�rieure";
		} else if (o.equals(OperateurType.SUPERIEUR_EGAL)) {
			operateurAffiche = "sup�rieure ou �gale";
		} else if (o.equals(OperateurType.EGAL)) {
			operateurAffiche = "�gale";
		} else if (o.equals(OperateurType.INFERIEUR_EGAL)) {
			operateurAffiche = "inf�rieure ou �gale";
		} else if (o.equals(OperateurType.INFERIEUR)) {
			operateurAffiche = "inf�rieure";
		}
		
		if (this.regle == 1) {
			res = "Nb d'option(s) minimale(s) " + operateurAffiche + " � " + this.valeur;
		} else if (this.regle == 2) {
			res = "Note minimale " + operateurAffiche + " � " + this.valeur;
		} 
		
		return res;
	}

	/* Getters. */
	
	/**
	 * M�thode retournant l'identifiant de la r�gle.
	 * 
	 * @return L'identifiant de la r�gle.
	 */
	public int getId() {
		return this.id;
	}

	/**
	 * M�thode retournant la r�gle (1 - NB_OPT_MINI, 2 - NOTE_MINIMALE).
	 * 
	 * @return La r�gle (1 - NB_OPT_MINI, 2 - NOTE_MINIMALE).
	 */
	public int getRegle() {
		return this.regle;
	}

	/**
	 * M�thode retournant l'op�rateur de la r�gle (>, <, <=, ...).
	 * 
	 * @return L'op�rateur de la r�gle.
	 */
	public int getOperateur() {
		return this.operateur;
	}

	/**
	 * M�thode retournant la valeur de la r�gle.
	 * 
	 * @return La valeur de la r�gle.
	 */
	public int getValeur() {
		return this.valeur;
	}
	
	/**
	 * M�thode retournant l'identifiant de l'UE sur laquelle s'applique la r�gle.
	 * 
	 * @return L'identifiant de l'UE sur laquelle s'applique la r�gle.
	 */
	public int getIdUE() {
		return this.idUE;
	}

	/**
	 * M�thode retournant l'identifiant de l'ann�e sur laquelle s'applique la r�gle.
	 * 
	 * @return L'identifiant de l'ann�e sur laquelle s'applique la r�gle.
	 */
	public int getIdAnnee() {
		return this.idAnnee;
	}
	
	/**
	 * M�thode retournant l'identifiant de la mati�re sur laquelle s'applique la r�gle.
	 * 
	 * @return L'identifiant de la mati�re sur laquelle s'applique la r�gle.
	 */
	public int getIdMatiere() {
		return this.idMatiere;
	}
	
	/**
	 * M�thode retournant le nom de l'UE associ�e � la r�gle.
	 * 
	 * @return Le nom de l'UE associ�e � la r�gle.
	 */
	public String getNomUE() {
		return this.nomUE;
	}
	
	/* Setters. */

	/**
	 * M�thode permettant de modifier l'identifiant de la r�gle.
	 * 
	 * @param id Le nouvel identifiant de la r�gle.
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * M�thode permettant de modifier la r�gle.
	 * 
	 * @param regle La nouvelle r�gle (1 - NB_OPT_MINI, 2 - NOTE_MINIMALE).
	 */
	public void setRegle(int regle) {
		this.regle = regle;
	}

	/**
	 * M�thode permettant de modifier l'op�rateur de la r�gle.
	 * 
	 * @param operateur Le nouvel op�rateur de la r�gle.
	 */
	public void setOperateur(int operateur) {
		this.operateur = operateur;
	}

	/**
	 * M�thode permettant de modifier la valeur de la r�gle.
	 * 
	 * @param valeur La nouvelle valeur de la r�gle.
	 */
	public void setValeur(int valeur) {
		this.valeur = valeur;
	}

	/**
	 * M�thode permettant de modifier l'identifiant de l'UE sur laquelle s'applique la r�gle.
	 * 
	 * @param idUE Le nouvel identifiant de l'UE sur laquelle s'applique la r�gle.
	 */
	public void setIdUE(int idUE) {
		this.idUE = idUE;
	}

	/**
	 * M�thode permettant de modifier l'identifiant de l'ann�e sur laquelle s'applique la r�gle.
	 * 
	 * @param idUE Le nouvel identifiant de l'ann�e sur laquelle s'applique la r�gle.
	 */
	public void setIdAnnee(int idAnnee) {
		this.idAnnee = idAnnee;
	}
	
	/**
	 * M�thode permettant de modifier l'identifiant de la mati�re sur laquelle s'applique la r�gle.
	 * 
	 * @param idUE Le nouvel identifiant de la mati�re sur laquelle s'applique la r�gle.
	 */
	public void setIdMatiere(int idMatiere) {
		this.idMatiere = idMatiere;
	}
	
	/**
	 * M�thode permettant de modifier le nom de l'UE associ�e � la r�gle.
	 * 
	 * @param nomUE Le nouveau nom de l'UE associ�e � la r�gle.
	 */
	public void setNomUE(String nomUE) {
		this.nomUE = nomUE;
	}
}