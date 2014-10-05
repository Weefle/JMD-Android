package org.gl.jmd.model;

import java.io.Serializable;
import java.util.*;

/**
 * Classe représentant un diplôme.
 * 
 * @author Jordi CHARPENTIER & Yoann VANHOESERLANDE
 */
public class Diplome implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Identifiant du diplôme.
	 */
	private int id;
	
	/**
	 * Le nom du diplôme.
	 */
	private String nom;
	
	/**
	 * La liste des années du diplôme.
	 */
	private ArrayList<Annee> listeAnnees = new ArrayList<Annee>();
	
	/**
	 * Constructeur par défaut de la classe.
	 */
	public Diplome() {
		
	}
	
	/**
	 * Méthode permettant de savoir si le diplôme est validé.
	 * 
	 * @return <b>true</b> si le diplôme est validé.<br />
	 * <b>false</b> sinon.
	 */
	public boolean isValide() {
		boolean res = false;
		
		for (int i = 0; i < this.listeAnnees.size(); i++) {			
			if (this.listeAnnees.get(i).getMoyenne() > 10.0) {
				res = true;
			} else {
				res = false;
			}
		}
		
		boolean isLastYearOK = false;
		
		for (int i = 0; i < this.listeAnnees.size(); i++) {
			if (this.listeAnnees.get(i).isLast()) {
				if (this.listeAnnees.get(i).getMoyenne() > 10.0) {
					isLastYearOK = true;
				} 
			}
		}
		
		if (!isLastYearOK) {
			res = false;
		} 
		
		return res;
	}
	
	/* Getters. */
	
	/**
	 * Méthode retournant l'identifiant du diplôme.
	 * 
	 * @return L'identifiant du diplôme.
	 */
	public int getId() {
		return this.id;
	}
	
	/**
	 * Méthode retournant le nom du diplôme.
	 * 
	 * @return Le nom du diplôme.
	 */
	public String getNom() {
		return this.nom;
	}
	
	/**
	 * Méthode retournant la liste des années du diplôme.
	 * 
	 * @return La liste des années du diplôme.
	 */
	public ArrayList<Annee> getListeAnnees() {
		return this.listeAnnees;
	}
	
	/* Setters. */
	
	/**
	 * Méthode permettant de modifier l'identifiant du diplôme.
	 * 
	 * @param id Le nouvel identifiant du diplôme.
	 */
	public void setId(int id) {
		this.id = id;
	}
	
	/**
	 * Méthode permettant de modifier le nom du diplôme.
	 * 
	 * @param nom Le nouveau nom du diplôme.
	 */
	public void setNom(String nom) {
		this.nom = nom;
	}
	
	/**
	 * Méthode permettant de modifier la liste des années du diplôme.
	 * 
	 * @param listeAnnees La nouvelle liste des années du diplôme.
	 */
	public void setListeAnnees(ArrayList<Annee> listeAnnees) {
		this.listeAnnees = listeAnnees;
	}
}