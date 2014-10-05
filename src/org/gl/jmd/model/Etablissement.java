package org.gl.jmd.model;

import java.io.Serializable;

/**
 * Classe représentant un établissement.
 * 
 * @author Jordi CHARPENTIER & Yoann VANHOESERLANDE
 */
public class Etablissement implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * Identifiant de l'établissement.
	 */
	private int id;

	/**
	 * Le nom de l'établissement.
	 */
	private String nom;
	
	/**
	 * La ville de l'établissement.
	 */
	private String ville;
	
	/**
	 * Constructeur par défaut de la classe.
	 */
	public Etablissement() {
		
	}
	
	/* Getters. */
	
	/**
	 * Méthode retournant l'identifiant de l'établissement.
	 * 
	 * @return L'identifiant de l'établissement.
	 */
	public int getId() {
		return this.id;
	}
	
	/**
	 * Méthode retournant le nom de l'établissement.
	 * 
	 * @return Le nom de l'établissement.
	 */
	public String getNom() {
		return this.nom;
	}
	
	/**
	 * Méthode retournant la ville de l'établissement.
	 * 
	 * @return La ville de l'établissement.
	 */
	public String getVille() {
		return this.ville;
	}
	
	/* Setters. */
	
	/**
	 * Méthode permettant de modifier l'identifiant de l'établissement.
	 * 
	 * @param id Le nouvel identifiant de l'établissement.
	 */
	public void setId(int id) {
		this.id = id;
	}
	
	/**
	 * Méthode permettant de modifier le nom de l'établissement.
	 * 
	 * @param nom Le nouveau nom de l'établissement.
	 */
	public void setNom(String nom) {
		this.nom = nom;
	}
	
	/**
	 * Méthode permettant de modifier la ville de l'établissement.
	 * 
	 * @param ville La nouvelle ville de l'établissement.
	 */
	public void setVille(String ville) {
		this.ville = ville;
	}
}
