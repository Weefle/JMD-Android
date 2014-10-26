package org.gl.jmd.model;

import java.io.Serializable;
import java.util.*;

import org.gl.jmd.model.enumeration.DecoupageYearType;
import org.gl.jmd.utils.NumberUtils;

/**
 * Classe représentant une UE (d'une année).
 * 
 * @author Jordi CHARPENTIER & Yoann VANHOESERLANDE
 */
public class UE implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/**
	 * Identifiant de l'UE.
	 */
	private int id;
	
	/**
	 * Le nom de l'UE.
	 */
	private String nom;
	
	/**
	 * Le type dont fait partie l'UE (semestre, ue, rien).
	 */
	private DecoupageYearType decoupageYearType;
	
	private int idAnnee;
	
	/**
	 * La liste des matières de l'UE.
	 */
	private ArrayList<Matiere> listeMatieres = new ArrayList<Matiere>();
	
	/**
	 * Les règles que devra respecter l'UE.
	 */
	private ArrayList<Regle> listeRegles = new ArrayList<Regle>();
	
	/**
	 * Constructeur par défaut de la classe.
	 */
	public UE() {
		
	}
	
	/**
	 * Méthode permettant de calculer la moyenne de l'UE.
	 * 
	 * @return La moyenne de l'UE.
	 */
	public double getMoyenne() {
		double res = -1;
		
		int coeffGlobalUE = 0;
		double produitMatiereCoeff = 0.0;
		
		for (int i = 0; i < this.listeMatieres.size(); i++) {
			if (this.listeMatieres.get(i).getNoteFinale() == -2) {
				res = -2;
				
				return res;
			}
			
			coeffGlobalUE += this.listeMatieres.get(i).getCoefficient();
			produitMatiereCoeff += this.listeMatieres.get(i).getNoteFinale() * this.listeMatieres.get(i).getCoefficient();
		}
		
		res = produitMatiereCoeff / coeffGlobalUE;
		res = NumberUtils.round(res, 2);
		
		return res;
	}
	
	/* Getters. */
	
	/**
	 * Méthode retournant l'identifiant de l'UE.
	 * 
	 * @return L'identifiant de l'UE.
	 */
	public int getId() {
		return this.id;
	}
	
	/**
	 * Méthode retournant le nom de l'UE.
	 * 
	 * @return Le nom de l'UE.
	 */
	public String getNom() {
		return this.nom;
	}
	
	/**
	 * Méthode retournant le type de découpage de l'UE (trimestre 1, semestre 2, ...).
	 * 
	 * @return Le type de découpage de l'UE (trimestre 1, semestre 2, ...).
	 */
	public DecoupageYearType getDecoupage() {
		return this.decoupageYearType;
	}
	
	/**
	 * Méthode retournant la liste des matières de l'UE.
	 * 
	 * @return La liste des matières de l'UE.
	 */
	public ArrayList<Matiere> getListeMatieres() {
		return this.listeMatieres;
	}
	
	public int getIdAnnee() {
		return this.idAnnee;
	}
	
	/**
	 * Méthode retournant les règles de la matière.
	 * 
	 * @return Les règles de la matière.
	 */
	public ArrayList<Regle> getListeRegles() {
		return this.listeRegles;
	}
	
	/* Setters. */
	
	/**
	 * Méthode permettant de modifier l'identifiant de l'UE.
	 * 
	 * @param id Le nouvel identifiant de l'UE.
	 */
	public void setId(int id) {
		this.id = id;
	}
	
	/**
	 * Méthode permettant de modifier le nom de l'UE.
	 * 
	 * @param nom Le nouveau nom de l'UE.
	 */
	public void setNom(String nom) {
		this.nom = nom;
	}
	
	/**
	 * Méthode permettant de modifier le type de découpage de l'UE (trimestre 1, semestre 2, ...).
	 * 
	 * @param decoupageYearType Le nouveau type de découpage de l'UE (trimestre 1, semestre 2, ...).
	 */
	public void setDecoupage(DecoupageYearType decoupageYearType) {
		this.decoupageYearType = decoupageYearType;
	}
	
	public void setIdAnnee(int idAnnee) {
		this.idAnnee = idAnnee;
	}
	
	/**
	 * Méthode permettant de modifier la liste des matières de l'UE.
	 * 
	 * @param listeMatieres La nouvelle liste des matières de l'UE.
	 */
	public void setListeMatieres(ArrayList<Matiere> listeMatieres) {
		this.listeMatieres = listeMatieres;
	}
	
	/**
	 * Méthode permettant de modifier les règles de la matière.
	 * 
	 * @param listeRegles Les nouvelle règles de la matière.
	 */
	public void setListeRegles(ArrayList<Regle> listeRegles) {
		this.listeRegles = listeRegles;
	}
}
