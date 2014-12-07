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
	 * Le diplome où l'année est rattachée.
	 */
	private Diplome d;

	/**
	 * Le découpage de l'année.
	 */
	private DecoupageType decoupage;

	/**
	 * Booléen permettant de savoir si l'année est la dernière du diplôme.
	 */
	private boolean isLast;

	/**
	 * Booléen permettant de savoir si l'année est suivie par l'admin ou non.
	 */
	private boolean isFollowed;

	/**
	 * La liste des UE de l'année.
	 */
	private List<UE> listeUE = new ArrayList<UE>();

	/**
	 * La liste des règles de l'année.
	 */
	private ArrayList<Regle> listeRegles = new ArrayList<Regle>();

	/**
	 * Constructeur par défaut de la classe.
	 */
	public Annee() {

	}

	/**
	 * Méthode permettant de retourner l'avancement (en %) de l'année.
	 * L'avancement correspond au pourcentage de matière ayant été "passées", donc ayant au moins une note.
	 * 
	 * @return L'avancement de l'année.
	 */
	public float getAvancement() {
		float res = 0;

		float sommeCoeffEntrees = 0;
		float sommeCoeff = 0;

		for (int i = 0; i < this.listeUE.size(); i++) {
			for (int j = 0; j < this.listeUE.get(i).getListeMatieres().size(); j++) {
				if (this.listeUE.get(i).getListeMatieres().get(j).getNoteSession1().getNote() != -1.0 ) {
					sommeCoeffEntrees += this.listeUE.get(i).getListeMatieres().get(j).getCoefficient();
				}

				sommeCoeff += this.listeUE.get(i).getListeMatieres().get(j).getCoefficient();
			}
		} 

		res = (sommeCoeffEntrees / sommeCoeff) * 100;

		return res;
	}

	/**
	 * Méthode retournant la mention de l'année.
	 * 
	 * @return La mention de l'année.
	 */
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
		double res = -1.0;

		double resNULL = 0;
		float sommeCoeffEntrees = 0;
		boolean hasNote = false;

		for (int i = 0; i < this.listeUE.size(); i++) {				
			for (int j = 0; j < this.listeUE.get(i).getListeMatieres().size(); j++) {
				if (this.listeUE.get(i).getListeMatieres().get(j).getNoteFinale() != -1.0) {
					sommeCoeffEntrees += this.listeUE.get(i).getListeMatieres().get(j).getCoefficient();
					hasNote = true;
					resNULL += this.listeUE.get(i).getListeMatieres().get(j).getCoefficient() * this.listeUE.get(i).getListeMatieres().get(j).getNoteFinale();
				}
			}
		} 

		if (hasNote) {
			resNULL = resNULL / sommeCoeffEntrees;
			resNULL = NumberUtils.round(resNULL, 2);

			res = resNULL;
		} 

		return res;
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

	/**
	 * Méthode retournant le diplôme où l'année est rattachée.
	 * 
	 * @return Le diplôme où l'année est rattachée.
	 */
	public Diplome getDiplome() {
		return this.d;
	}

	/**
	 * Méthode retournant le booléen qui identifie si l'année est la dernière du diplôme, ou non.
	 * 
	 * @return <b>true</b> si l'année est la dernière du diplôme.
	 * <b>false</b> sinon.
	 */
	public boolean isLast() {
		return this.isLast;
	}

	/**
	 * Méthode retournant le booléen qui identifie si l'année est suivi, ou non.
	 * 
	 * @return <b>true</b> si l'année est suivie.
	 * <b>false</b> sinon.
	 */
	public boolean isFollowed() {
		return this.isFollowed;
	}

	/**
	 * Méthode retournant la liste des UE de l'année.
	 * 
	 * @return La liste des UE de l'année.
	 */
	public List<UE> getListeUE() {
		return this.listeUE;
	}

	/**
	 * Méthode retournant la liste des règles de l'année.
	 * 
	 * @return La liste des règles de l'année.
	 */
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

	/**
	 * Méthode permettant de modifier le diplôme où l'année est rattachée.
	 * 
	 * @param d Le nouveau diplôme où l'année sera rattachée.
	 */
	public void setDiplome(Diplome d) {
		this.d = d;
	}

	/**
	 * Méthode permettant de modifier le booléen qui identifie si l'année est la dernière du diplôme, ou non.
	 * 
	 * @param isLast Le nouveau booléen pour savoir si l'année est la dernière du diplôme, ou non.
	 */
	public void setIsLast(boolean isLast) {
		this.isLast = isLast;
	}

	/**
	 * Méthode permettant de modifier le booléen qui identifie si l'année est suivie, ou non.
	 * 
	 * @param isLast Le nouveau booléen pour savoir si l'année est suivie, ou non.
	 */
	public void setIsFollowed(boolean isFollowed) {
		this.isFollowed = isFollowed;
	}

	/**
	 * Méthode permettant de modifier la liste des UE de l'année.
	 * 
	 * @param listeUE La nouvelle liste des UE de l'année.
	 */
	public void setListeUE(ArrayList<UE> listeUE) {
		this.listeUE = listeUE;
	}

	/**
	 * Méthode permettant de modifier la liste des règles de l'année.
	 * 
	 * @param listeRegles La nouvelle liste des règles de l'année.
	 */
	public void setListeRegles(ArrayList<Regle> listeRegles) {
		this.listeRegles = listeRegles;
	}
}
