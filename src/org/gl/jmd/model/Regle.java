package org.gl.jmd.model;

import java.io.Serializable;

public class Regle implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private int id;
	
	private String regle;
	
	private String operateur;
	
	private String valeur;
	
	private int idUE;
	
	private int idAnnee;
	
	/**
	 * Constructeur par d�faut de la classe.
	 */
	public Regle() {
		
	}
	
	/**
	 * M�thode retournant la r�gle sous la forme d'une cha�ne de caract�res.
	 * 
	 * @return Une repr�sentation de la r�gle sous la forme d'une cha�ne de caract�res.
	 */
	@Override
	public String toString() {
		return this.regle + " " + this.operateur + " " + this.valeur;
	}

	/* Getters. */
	
	public int getId() {
		return this.id;
	}

	public String getRegle() {
		return this.regle;
	}

	public String getOperateur() {
		return this.operateur;
	}

	public String getValeur() {
		return this.valeur;
	}
	
	public int getIdUE() {
		return this.idUE;
	}

	public int getIdAnnee() {
		return this.idAnnee;
	}
	
	/* Setters. */

	public void setId(int id) {
		this.id = id;
	}

	public void setRegle(String regle) {
		this.regle = regle;
	}

	public void setOperateur(String operateur) {
		this.operateur = operateur;
	}

	public void setValeur(String valeur) {
		this.valeur = valeur;
	}

	public void setIdUE(int idUE) {
		this.idUE = idUE;
	}

	public void setIdAnnee(int idAnnee) {
		this.idAnnee = idAnnee;
	}
}