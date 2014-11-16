package org.gl.jmd.model;

import java.io.Serializable;
import java.util.*;

/**
 * Classe représentant un étudiant.
 * 
 * @author Jordi CHARPENTIER & Yoann VANHOESERLANDE
 */
public class Etudiant implements Serializable {
	
	private static final long serialVersionUID = 1L;

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
	
	/**
	 * Méthode permettant de modifier la liste des diplômes de l'étudiant.
	 * 
	 * @param listeDiplomes La nouvelle liste des diplômes de l'étudiant.
	 */
	public void setListeDiplomes(ArrayList<Diplome> listeDiplomes) {
		this.listeDiplomes = listeDiplomes;
	}
}
