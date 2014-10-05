package org.gl.jmd.model.user;

import java.util.*;

import org.gl.jmd.model.Diplome;

/**
 * Classe repr�sentant un �tudiant.
 * 
 * @author Jordi CHARPENTIER & Yoann VANHOESERLANDE
 */
public class Etudiant extends Utilisateur {
	
	private static final long serialVersionUID = -8565819799531716139L;

	private String nom;
	
	private String prenom;
	
	private String mail;

	/**
	 * La liste des dipl�mes de l'�tudiant.
	 */
	private ArrayList<Diplome> listeDiplomes = new ArrayList<Diplome>();
	
	/**
	 * Constructeur par d�faut de la classe.
	 */
	public Etudiant() {
		
	}
	
	/**
	 * M�thode retournant la liste des dipl�mes de l'�tudiant.
	 * 
	 * @return La liste des dipl�mes de l'�tudiant.
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
	 * M�thode permettant de modifier la liste des dipl�mes de l'�tudiant.
	 * 
	 * @param listeDiplomes La nouvelle liste des dipl�mes de l'�tudiant.
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
