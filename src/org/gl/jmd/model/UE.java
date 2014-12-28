package org.gl.jmd.model;

import java.io.Serializable;
import java.util.*;

import org.gl.jmd.model.enumeration.*;
import org.gl.jmd.utils.NumberUtils;

/**
 * Classe repr�sentant une UE (d'une ann�e).
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
	 * L'identifiant de l'ann�e li�e � l'UE.
	 */
	private int idAnnee;
	
	/**
	 * La moyenne minimale de l'UE.
	 * Champ optionnel : s'il existe, il est pr�cis� dans le RCC.
	 */
	private double moyenneMini;
	
	/**
	 * Le nombre d'option minimum que l'�tudiant devra choisir dans l'UE.
	 * Champ optionnel.
	 */
	private int nbOptionsMini;
	
	/**
	 * La liste des mati�res de l'UE.
	 */
	private ArrayList<Matiere> listeMatieres = new ArrayList<Matiere>();
	
	/**
	 * Constructeur par d�faut de la classe.
	 */
	public UE() {
		
	}
	
	/**
	 * M�thode permettant de calculer la moyenne de l'UE.
	 * 
	 * @return La moyenne de l'UE.
	 */
	public double getMoyenne() {
		double res = -1.0;
		
		int coeffGlobalUE = 0;
		double produitMatiereCoeff = 0.0;
		
		// R�cup�ration des options.
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
	 * M�thode permettant de savoir si l'UE est d�faillante.
	 * D�faillant : 
	 * - une note de l'UE est inf�rieure � la note minimale d�finie pour la mati�re en question.
	 * - la moyenne de l'UE est inf�rieure � la moyenne minimale d�finie pour cette UE.
	 * 
	 * @return <b>true</b> si l'UE est d�faillante.<br />
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
	 * M�thode retournant l'identifiant de l'UE.
	 * 
	 * @return L'identifiant de l'UE.
	 */
	public int getId() {
		return this.id;
	}
	
	/**
	 * M�thode retournant le nom de l'UE.
	 * 
	 * @return Le nom de l'UE.
	 */
	public String getNom() {
		return this.nom;
	}
	
	/**
	 * M�thode retournant le type de d�coupage de l'UE (trimestre 1, semestre 2, ...).
	 * 
	 * @return Le type de d�coupage de l'UE (trimestre 1, semestre 2, ...).
	 */
	public DecoupageYearType getDecoupage() {
		return this.decoupageYearType;
	}
	
	/**
	 * M�thode retournant l'identifiant de l'ann�e li�e � l'UE.
	 * 
	 * @return L'identifiant de l'ann�e li�e � l'UE.
	 */
	public int getIdAnnee() {
		return this.idAnnee;
	}
	
	/**
	 * M�thode retournant la moyenne minimale de l'UE.
	 * 
	 * @return La moyenne minimale de l'UE.
	 */
	public double getMoyenneMini() {
		return this.moyenneMini;
	}
	
	/**
	 * M�thode retournant le nombre d'options que devra chosiir l'�tudiant dans l'UE.
	 * 
	 * @return Le nombre d'options que devra choisir l'�tudiant dans l'UE.
	 */
	public int getNbOptionsMini() {
		return this.nbOptionsMini;
	}
	
	/**
	 * M�thode retournant la liste des mati�res de l'UE.
	 * 
	 * @return La liste des mati�res de l'UE.
	 */
	public ArrayList<Matiere> getListeMatieres() {
		return this.listeMatieres;
	}
	
	/* Setters. */
	
	/**
	 * M�thode permettant de modifier l'identifiant de l'UE.
	 * 
	 * @param id Le nouvel identifiant de l'UE.
	 */
	public void setId(int id) {
		this.id = id;
	}
	
	/**
	 * M�thode permettant de modifier le nom de l'UE.
	 * 
	 * @param nom Le nouveau nom de l'UE.
	 */
	public void setNom(String nom) {
		this.nom = nom;
	}
	
	/**
	 * M�thode permettant de modifier le type de d�coupage de l'UE (trimestre 1, semestre 2, ...).
	 * 
	 * @param decoupageYearType Le nouveau type de d�coupage de l'UE (trimestre 1, semestre 2, ...).
	 */
	public void setDecoupage(DecoupageYearType decoupageYearType) {
		this.decoupageYearType = decoupageYearType;
	}
	
	/**
	 * M�thode permettant de modifier l'identifiant de l'ann�e li�e � l'UE.
	 * 
	 * @param idAnnee Le nouvel identifiant de l'ann�e li�e � l'UE.
	 */
	public void setIdAnnee(int idAnnee) {
		this.idAnnee = idAnnee;
	}
	
	/**
	 * M�thode permettant de modifier la moyenne minimale de l'UE.
	 * 
	 * @param moyenneMini La nouvelle moyenne minimale de l'UE.
	 */
	public void setMoyenneMini(double moyenneMini) {
		this.moyenneMini = moyenneMini;
	}
	
	/**
	 * M�thode permettant de modifier le nombre d'options minimum que l'�tudiant devra choisir dans l'UE.
	 * 
	 * @param nbOptionsMini Le nouveau nombre d'options minimum que l'�tudiant devra choisir dans l'UE.
	 */
	public void setNbOptionsMini(int nbOptionsMini) {
		this.nbOptionsMini = nbOptionsMini;
	}
	
	/**
	 * M�thode permettant de modifier la liste des mati�res de l'UE.
	 * 
	 * @param listeMatieres La nouvelle liste des mati�res de l'UE.
	 */
	public void setListeMatieres(ArrayList<Matiere> listeMatieres) {
		this.listeMatieres = listeMatieres;
	}
}
