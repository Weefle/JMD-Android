package org.gl.jmd.model.user;

/**
 * Classe représentant un administrateur.
 * 
 * @author Jordi CHARPENTIER & Yoann VANHOESERLANDE
 */
public class Admin extends Utilisateur {

	private static final long serialVersionUID = 1L;

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
