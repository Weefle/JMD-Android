package org.gl.jmd.model;

import java.io.Serializable;
import java.util.*;

import org.gl.jmd.model.enumeration.*;
import org.gl.jmd.utils.NumberUtils;

/**
 * Classe représentant une année (d'un diplôme).
 * 
 * @author Jordi CHARPENTIER & Yoann VANHOESERLANDE
 */
public class Annee implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * Identifiant de l'année.
	 */
	private int id;
	
	/**
	 * Le nom de l'année.
	 */
	private String nom;
	
	/**
	 * L'établissement de l'année.
	 */
	private Etablissement etablissement;
	
	/**
	 * Le découpage de l'année.
	 */
	private DecoupageType decoupage;
	
	/**
	 * Booléen permettant de savoir si l'année est la dernière du diplôme.
	 */
	private boolean isLast;
	
	/**
	 * La liste des UE de l'année.
	 */
	private List<UE> listeUE = new ArrayList<UE>();
	
	private ArrayList<Regle> listeRegles = new ArrayList<Regle>();
	
	/**
	 * Constructeur par défaut de la classe.
	 */
	public Annee() {
		
	}
	
	public float getAvancement() {
		float res = 0;
		
		float sommeCoeffEntrees = 0;
		float sommeCoeff = 0;
		
		for (int i = 0; i < this.listeUE.size(); i++) {
			for (int j = 0; j < this.listeUE.get(i).getListeMatieres().size(); j++) {
				for (int k = 0; k < this.listeUE.get(i).getListeMatieres().get(j).getListeNotes().size(); k++) {
					if (this.listeUE.get(i).getListeMatieres().get(j).getListeNotes().get(k).getNoteType() == NoteType.SESSION_1) {
						sommeCoeffEntrees += this.listeUE.get(i).getListeMatieres().get(j).getCoefficient();
					}
				}
				
				sommeCoeff += this.listeUE.get(i).getListeMatieres().get(j).getCoefficient();
			}
		}
		
		res = (sommeCoeffEntrees / sommeCoeff) * 100;
		
		return res;
	}
	
	public String getMention() {
		if ((getMoyenne() >= 10.0) && (getMoyenne() < 12.0)) {
			return "Passable";
		} else if ((getMoyenne() >= 12.0) && (getMoyenne() < 14.0)) {
			return "Assez bien";
		} else if ((getMoyenne() >= 14.0) && (getMoyenne() < 16.0)) {
			return "Bien";
		} else if (getMoyenne() >= 16.0) {
			return "Très bien";
		} else {
			return "Pas de mention";
		}
	}
	
	/**
	 * Méthode retournant la moyenne de l'année.
	 * 
	 * @return La moyenne de l'année.
	 */
	public double getMoyenne() {
		double res = -1;
		
		if (this.decoupage == DecoupageType.NULL) {
			double resNULL = 0;
			float sommeCoeffEntrees = 0;
			boolean hasNote = false;
			
			for (int i = 0; i < this.listeUE.size(); i++) {
				for (int j = 0; j < this.listeUE.get(i).getListeMatieres().size(); j++) {
					for (int k = 0; k < this.listeUE.get(i).getListeMatieres().get(j).getListeNotes().size(); k++) {
						if (this.listeUE.get(i).getListeMatieres().get(j).getListeNotes().get(k).getNote() != -1) {
							sommeCoeffEntrees += this.listeUE.get(i).getListeMatieres().get(j).getCoefficient();
							hasNote = true;
							resNULL += this.listeUE.get(i).getListeMatieres().get(j).getCoefficient() * this.listeUE.get(i).getListeMatieres().get(j).getNoteFinale();
						}
					}
				}
			}
			
			if (hasNote) {
				resNULL = resNULL / sommeCoeffEntrees;
				resNULL = NumberUtils.round(resNULL, 2);

				res = resNULL;
			} 
		} else if (this.decoupage == DecoupageType.SEMESTRE) {
			int coeffGlobalUES1 = 0;
			int coeffGlobalUES2 = 0;
			
			for (int i = 0; i < this.listeUE.size(); i++) {
				for (int j = 0; j < this.listeUE.get(i).getListeMatieres().size(); j++) {
					if (this.listeUE.get(i).getDecoupage() == DecoupageYearType.SEM1) {
						coeffGlobalUES1 += this.listeUE.get(i).getListeMatieres().get(j).getCoefficient();
					} else if (this.listeUE.get(i).getDecoupage() == DecoupageYearType.SEM2) {
						coeffGlobalUES2 += this.listeUE.get(i).getListeMatieres().get(j).getCoefficient();
					} else {
						res = -1;
						
						return res;
					}
				}
			}
			
			if ((coeffGlobalUES1 != 30) || (coeffGlobalUES2 != 30)) {
				res = -1;
				
				return res;
			} else {
				// Moyenne S1
				
				double moyenneS1 = 0.0;
				
				for (int i = 0; i < this.listeUE.size(); i++) {	
					int coeffUE = 0;
					
					for (int j = 0; j < this.listeUE.get(i).getListeMatieres().size(); j++) {
						if (this.listeUE.get(i).getDecoupage() == DecoupageYearType.SEM1) {
							coeffUE += this.listeUE.get(i).getListeMatieres().get(j).getCoefficient();
						}
					}
					
					moyenneS1 += this.listeUE.get(i).getMoyenne() * coeffUE;
				}
				
				moyenneS1 = moyenneS1 / coeffGlobalUES1;
				moyenneS1 = NumberUtils.round(moyenneS1, 2);
				
				// Moyenne S2
				
				double moyenneS2 = 0.0;
				
				for (int i = 0; i < this.listeUE.size(); i++) {	
					int coeffUE = 0;
					
					for (int j = 0; j < this.listeUE.get(i).getListeMatieres().size(); j++) {
						if (this.listeUE.get(i).getDecoupage() == DecoupageYearType.SEM2) {
							coeffUE += this.listeUE.get(i).getListeMatieres().get(j).getCoefficient();
						}
					}
					
					moyenneS2 += this.listeUE.get(i).getMoyenne() * coeffUE;
				}
				
				moyenneS2 = moyenneS2 / coeffGlobalUES2;
				moyenneS2 = NumberUtils.round(moyenneS2, 2);
				
				// Moyenne des 2 semestres.
				res = (moyenneS1 + moyenneS2) / 2;
			}			
		} else if (this.decoupage == DecoupageType.TRIMESTRE) {
			int coeffGlobalUET1 = 0;
			int coeffGlobalUET2 = 0;
			int coeffGlobalUET3 = 0;
			
			for (int i = 0; i < this.listeUE.size(); i++) {
				for (int j = 0; j < this.listeUE.get(i).getListeMatieres().size(); j++) {
					if (this.listeUE.get(i).getDecoupage() == DecoupageYearType.TR1) {
						coeffGlobalUET1 += this.listeUE.get(i).getListeMatieres().get(j).getCoefficient();
					} else if (this.listeUE.get(i).getDecoupage() == DecoupageYearType.TR2) {
						coeffGlobalUET2 += this.listeUE.get(i).getListeMatieres().get(j).getCoefficient();
					} else if (this.listeUE.get(i).getDecoupage() == DecoupageYearType.TR3) {
						coeffGlobalUET3 += this.listeUE.get(i).getListeMatieres().get(j).getCoefficient();
					} else {
						res = -1;
						
						return res;
					}
				}
			}
			
			if ((coeffGlobalUET1 != 20) || (coeffGlobalUET2 != 20) || (coeffGlobalUET3 != 20)) {
				res = -1;
				
				return res;
			} else {
				// Moyenne T1
				
				double moyenneT1 = 0.0;
				
				for (int i = 0; i < this.listeUE.size(); i++) {	
					int coeffUE = 0;
					
					for (int j = 0; j < this.listeUE.get(i).getListeMatieres().size(); j++) {
						if (this.listeUE.get(i).getDecoupage() == DecoupageYearType.TR1) {
							coeffUE += this.listeUE.get(i).getListeMatieres().get(j).getCoefficient();
						}
					}
					
					moyenneT1 += this.listeUE.get(i).getMoyenne() * coeffUE;
				}
				
				moyenneT1 = moyenneT1 / coeffGlobalUET1;
				moyenneT1 = NumberUtils.round(moyenneT1, 2);
				
				// Moyenne T2
				
				double moyenneT2 = 0.0;
				
				for (int i = 0; i < this.listeUE.size(); i++) {	
					int coeffUE = 0;
					
					for (int j = 0; j < this.listeUE.get(i).getListeMatieres().size(); j++) {
						if (this.listeUE.get(i).getDecoupage() == DecoupageYearType.TR2) {
							coeffUE += this.listeUE.get(i).getListeMatieres().get(j).getCoefficient();
						}
					}
					
					moyenneT2 += this.listeUE.get(i).getMoyenne() * coeffUE;
				}
				
				moyenneT2 = moyenneT2 / coeffGlobalUET2;
				moyenneT2 = NumberUtils.round(moyenneT2, 2);
				
				// Moyenne T3
				
				double moyenneT3 = 0.0;
				
				for (int i = 0; i < this.listeUE.size(); i++) {	
					int coeffUE = 0;
					
					for (int j = 0; j < this.listeUE.get(i).getListeMatieres().size(); j++) {
						if (this.listeUE.get(i).getDecoupage() == DecoupageYearType.TR3) {
							coeffUE += this.listeUE.get(i).getListeMatieres().get(j).getCoefficient();
						}
					}
					
					moyenneT3 += this.listeUE.get(i).getMoyenne() * coeffUE;
				}
				
				moyenneT3 = moyenneT3 / coeffGlobalUET3;
				moyenneT3 = NumberUtils.round(moyenneT3, 2);
				
				// Moyenne des 3 trimestres
				res = (moyenneT1 + moyenneT2 + moyenneT3) / 3;
			}			
		}
		
		return res;
	}
	
	public boolean isValid() {
		boolean result = true;
		double moyenne = getMoyenne();
		
		for (int i = 0; i < this.listeRegles.size(); i++) {
			if (this.listeRegles.get(i).equals(RegleType.NOTE_MINIMALE)) {
				if (moyenne < this.listeRegles.get(i).getValeur()) {
					result = false;
				}
			}
		}
		
		return result;
	}
	
	/* Getters. */
	
	/**
	 * Méthode retournant l'identifiant de l'année.
	 * 
	 * @return L'identifiant de l'année.
	 */
	public int getId() {
		return this.id;
	}
	
	/**
	 * Méthode retournant le nom de l'année.
	 * 
	 * @return Le nom de l'année.
	 */
	public String getNom() {
		return this.nom;
	}
	
	/**
	 * Méthode retournant l'établissement de l'année.
	 * 
	 * @return L'établissement de l'année.
	 */
	public Etablissement getEtablissement() {
		return this.etablissement;
	}
	
	/**
	 * Méthode retournant le découpage de l'année.
	 * 
	 * @return Le découpage de l'année.
	 */
	public DecoupageType getDecoupage() {
		return this.decoupage;
	}
	
	public boolean isLast() {
		return this.isLast;
	}
	
	/**
	 * Méthode retournant la liste des UE de l'année.
	 * 
	 * @return La liste des UE de l'année.
	 */
	public List<UE> getListeUE() {
		return this.listeUE;
	}
	
	public ArrayList<Regle> getListeRegles() {
		return this.listeRegles;
	}
	
	/* Setters. */
	
	/**
	 * Méthode permettant de modifier l'identifiant de l'année.
	 * 
	 * @param id Le nouvel identifiant de l'année.
	 */
	public void setId(int id) {
		this.id = id;
	}
	
	/**
	 * Méthode permettant de modifier le nom de l'année.
	 * 
	 * @param nom Le nouveau nom de l'année.
	 */
	public void setNom(String nom) {
		this.nom = nom;
	}
	
	/**
	 * Méthode permettant de modifier l'établissement de l'année.
	 * 
	 * @param etablissement Le nouvel établissement de l'année.
	 */
	public void setEtablissement(Etablissement etablissement) {
		this.etablissement = etablissement;
	}
	
	/**
	 * Méthode permettant de modifier le découpage de l'année.
	 * 
	 * @param decoupage Le nouveau découpage de l'année.
	 */
	public void setDecoupage(DecoupageType decoupage) {
		this.decoupage = decoupage;
	}
	
	public void setIsLast(boolean isLast) {
		this.isLast = isLast;
	}
	
	/**
	 * Méthode permettant de modifier la liste des UE de l'année.
	 * 
	 * @param listeUE La nouvelle liste des UE de l'année.
	 */
	public void setListeUE(ArrayList<UE> listeUE) {
		this.listeUE = listeUE;
	}
	
	public void setListeRegles(ArrayList<Regle> listeRegles) {
		this.listeRegles = listeRegles;
	}
}
