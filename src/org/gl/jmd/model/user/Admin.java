package org.gl.jmd.model.user;

/**
 * Classe représentant un administrateur.
 * 
 * @author Jordi CHARPENTIER & Yoann VANHOESERLANDE
 */
public class Admin extends Utilisateur {

	private static final long serialVersionUID = 1L;
	
	/**
	 * L'identifiant de l'utilisateur.
	 */
	private int id;

	/**
	 * Le nom de l'utilisateur.
	 */
	private String nom;
	
	/**
	 * Le prénom de l'utilisateur.
	 */
	private String prenom;
	
	/**
	 * Le mot de passe de l'administrateur.
	 */
	private String pseudo;
	
	/**
	 * Le mot de passe de l'administrateur.
	 */
	private String password;
	
	/**
	 * Booléen permettant de savoir si l'administrateur est actif ou non.
	 */
	private boolean estActive;
	
	/**
	 * Constructeur par défaut de la classe.
	 */
	public Admin() {
		
	}
	
	/* Getters. */
	
	/**
	 * Méthode retournant l'identifiant de l'utilisateur.
	 * 
	 * @return L'identifiant de l'utilisateur.
	 */
	public int getId() {
		return this.id;
	}
	
	/**
	 * Méthode retournant le nom de l'utilisateur.
	 * 
	 * @return Le nom de l'utilisateur.
	 */
	public String getNom() {
		return this.nom;
	}
	
	/**
	 * Méthode retournant le prénom de l'utilisateur.
	 * 
	 * @return Le prénom de l'utilisateur.
	 */
	public String getPrenom() {
		return this.prenom;
	}

	/**
	 * Méthode retournant le pseudo de l'administrateur.
	 * 
	 * @return Le pseudo de l'administrateur.
	 */
	public String getPseudo() {
		return this.pseudo;
	}

	/**
	 * Méthode retournant le mot de passe de l'administrateur.
	 * 
	 * @return Le mot de passe de l'administrateur.
	 */
	public String getPassword() {
		return this.password;
	}
	
	/**
	 * Méthode permettant de savoir si le compte est activé ou non.
	 * 
	 * @return <b>true</b> si le compte est activé.<br />
	 * <b>false</b> sinon.
	 */
	public boolean isActive() {
		return this.estActive;
	}
	
	/* Setters. */
	
	/**
	 * Méthode permettant de modifier l'identifiant de l'utilisateur.
	 * 
	 * @param id Le nouvel identifiant de l'utilisateur.
	 */
	public void setId(int id) {
		this.id = id;
	}
	
	/**
	 * Méthode permettant de modifier le nom de l'utilisateur.
	 * 
	 * @param nom Le nouveau nom de l'utilisateur.
	 */
	public void setNom(String nom) {
		this.nom = nom;
	}
	
	/**
	 * Méthode permettant de modifier le prénom de l'utilisateur.
	 * 
	 * @param prenom Le nouveau prénom de l'utilisateur.
	 */
	public void setPrenom(String prenom) {
		this.prenom = prenom;
	}

	/**
	 * Méthode permettant de modifier le pseudo de l'administrateur.
	 * 
	 * @param pseudo Le nouveau pseudo de l'administrateur.
	 */
	public void setPseudo(String pseudo) {
		this.pseudo = pseudo;
	}

	/**
	 * Méthode permettant de modifier le mot de passe de l'administrateur.
	 * 
	 * @param password Le nouveau mot de passe de l'administrateur.
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	
	/**
	 * Méthode permettant de modifier le fait que le compte soit activé ou non.
	 * 
	 * @param estActive La nouvelle valeur du booléen.
	 */
	public void setIsActive(boolean estActive) {
		this.estActive = estActive;
	}
}