package org.gl.jmd.model.user;

import java.util.*;

import org.gl.jmd.model.Diplome;

/**
 * Classe représentant un étudiant.
 * 
 * @author Jordi CHARPENTIER & Yoann VANHOESERLANDE
 */
public class Etudiant extends Utilisateur {
	
	private static final long serialVersionUID = -8565819799531716139L;

	private String nom;
	
	private String prenom;
	
	private String mail;

	/**
	 * La liste des diplômes de l'étudiant.
	 */
	private ArrayList<Diplome> listeDiplomes = new ArrayList<Diplome>();
	
	/**
	 * Constructeur par défaut de la classe.
	 */
	public Etudiant() {
		
	}
	
	/**
	 * Méthode retournant la liste des diplômes de l'étudiant.
	 * 
	 * @return La liste des diplômes de l'étudiant.
	 */
	public ArrayList<Diplome> getListeDiplomes() {
		return this.listeDiplomes;
	}
	
	public String getNom() {
		return this.nom;
	}

	public String getPrenom() {
		return this.prenom;
	}

	public String getMail() {
		return this.mail;
	}
	
	/**
	 * Méthode permettant de modifier la liste des diplômes de l'étudiant.
	 * 
	 * @param listeDiplomes La nouvelle liste des diplômes de l'étudiant.
	 */
	public void setListeDiplomes(ArrayList<Diplome> listeDiplomes) {
		this.listeDiplomes = listeDiplomes;
	}
	
	public void setNom(String nom) {
		this.nom = nom;
	}

	public void setPrenom(String prenom) {
		this.prenom = prenom;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}
}
