package org.gl.jmd.model;

import java.io.Serializable;
import java.util.*;

import org.gl.jmd.model.enumeration.*;
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
	
	/**
	 * L'identifiant de l'année liée à l'UE.
	 */
	private int idAnnee;
	
	/**
	 * La moyenne minimale de l'UE.
	 * Champ optionnel : s'il existe, il est précisé dans le RCC.
	 */
	private double moyenneMini;
	
	/**
	 * Le nombre d'option minimum que l'étudiant devra choisir dans l'UE.
	 * Champ optionnel.
	 */
	private int nbOptionsMini;
	
	/**
	 * La liste des matières de l'UE.
	 */
	private ArrayList<Matiere> listeMatieres = new ArrayList<Matiere>();
	
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
		double res = -1.0;
		
		int coeffGlobalUE = 0;
		double produitMatiereCoeff = 0.0;
		
		// Récupération des options.
		ArrayList<Matiere> listOptions = new ArrayList<Matiere>();
		
		for (int i = 0; i < this.listeMatieres.size(); i++) {
			if ((this.listeMatieres.get(i).getNoteFinale() != -1.0) &&
					(this.listeMatieres.get(i).isOption())) {
				
				listOptions.add(this.listeMatieres.get(i));
			}
		}
		
		// Tri de la liste des options.
		for (int i = listOptions.size() - 1; i >= 0; i--) {
	        for (int j = 0; j < i; j++) {
	            if (listOptions.get(j).getNoteFinale() < listOptions.get(j + 1).getNoteFinale()) {
	                Matiere temp = listOptions.get(j);
	                listOptions.set(j, listOptions.get(j + 1));
	                listOptions.set(j + 1, temp);
	            }
	        }
	    }
		
		// Moyenne des n meilleurs options.
		if (listOptions.size() > this.nbOptionsMini) {				
			for (int i = 0; i < this.nbOptionsMini; i++) {
				if (listOptions.get(i).getNoteFinale() != -1.0) {					
					coeffGlobalUE += listOptions.get(i).getCoefficient();
					produitMatiereCoeff += listOptions.get(i).getNoteFinale() * listOptions.get(i).getCoefficient();
				}
			}
		} else {
			for (int i = 0; i < listOptions.size(); i++) {
				if (listOptions.get(i).getNoteFinale() != -1.0) {					
					coeffGlobalUE += listOptions.get(i).getCoefficient();
					produitMatiereCoeff += listOptions.get(i).getNoteFinale() * listOptions.get(i).getCoefficient();
				}
			}
		}
		
		// Calcul de la moyenne.
		for (int i = 0; i < this.listeMatieres.size(); i++) {
			if ((this.listeMatieres.get(i).getNoteFinale() != -1.0) &&
					(!this.listeMatieres.get(i).isOption())) {
				
				coeffGlobalUE += this.listeMatieres.get(i).getCoefficient();
				produitMatiereCoeff += this.listeMatieres.get(i).getNoteFinale() * this.listeMatieres.get(i).getCoefficient();
			}
		}
		
		if ((coeffGlobalUE != 0) && (produitMatiereCoeff != 0.0)) {
			res = produitMatiereCoeff / coeffGlobalUE;
			res = NumberUtils.round(res, 2);
		}	
		
		return res;
	}
	
	/**
	 * Méthode permettant de savoir si l'UE est défaillante.
	 * Défaillant : 
	 * - une note de l'UE est inférieure à la note minimale définie pour la matière en question.
	 * - la moyenne de l'UE est inférieure à la moyenne minimale définie pour cette UE.
	 * 
	 * @return <b>true</b> si l'UE est défaillante.<br />
	 * <b>false</b> sinon.
	 */
	public boolean isDefaillant() {		
		boolean isMatiereDef = false;
		
		for (int k = 0; k < this.listeMatieres.size(); k++) {	
			if ((this.listeMatieres.get(k).isDefaillant()) && (this.listeMatieres.get(k).getNoteFinale() != -1.0)) {
				isMatiereDef = true;
				break;
			}
		}
		
		return isMatiereDef || (this.getMoyenne() < this.moyenneMini);
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
	 * Méthode retournant l'identifiant de l'année liée à l'UE.
	 * 
	 * @return L'identifiant de l'année liée à l'UE.
	 */
	public int getIdAnnee() {
		return this.idAnnee;
	}
	
	/**
	 * Méthode retournant la moyenne minimale de l'UE.
	 * 
	 * @return La moyenne minimale de l'UE.
	 */
	public double getMoyenneMini() {
		return this.moyenneMini;
	}
	
	/**
	 * Méthode retournant le nombre d'options que devra chosiir l'étudiant dans l'UE.
	 * 
	 * @return Le nombre d'options que devra choisir l'étudiant dans l'UE.
	 */
	public int getNbOptionsMini() {
		return this.nbOptionsMini;
	}
	
	/**
	 * Méthode retournant la liste des matières de l'UE.
	 * 
	 * @return La liste des matières de l'UE.
	 */
	public ArrayList<Matiere> getListeMatieres() {
		return this.listeMatieres;
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
	
	/**
	 * Méthode permettant de modifier l'identifiant de l'année liée à l'UE.
	 * 
	 * @param idAnnee Le nouvel identifiant de l'année liée à l'UE.
	 */
	public void setIdAnnee(int idAnnee) {
		this.idAnnee = idAnnee;
	}
	
	/**
	 * Méthode permettant de modifier la moyenne minimale de l'UE.
	 * 
	 * @param moyenneMini La nouvelle moyenne minimale de l'UE.
	 */
	public void setMoyenneMini(double moyenneMini) {
		this.moyenneMini = moyenneMini;
	}
	
	/**
	 * Méthode permettant de modifier le nombre d'options minimum que l'étudiant devra choisir dans l'UE.
	 * 
	 * @param nbOptionsMini Le nouveau nombre d'options minimum que l'étudiant devra choisir dans l'UE.
	 */
	public void setNbOptionsMini(int nbOptionsMini) {
		this.nbOptionsMini = nbOptionsMini;
	}
	
	/**
	 * Méthode permettant de modifier la liste des matières de l'UE.
	 * 
	 * @param listeMatieres La nouvelle liste des matières de l'UE.
	 */
	public void setListeMatieres(ArrayList<Matiere> listeMatieres) {
		this.listeMatieres = listeMatieres;
	}
}
