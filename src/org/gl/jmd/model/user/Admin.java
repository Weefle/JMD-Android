package org.gl.jmd.model.user;

/**
 * Classe repr�sentant un administrateur.
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
	 * Bool�en permettant de savoir si l'administrateur est actif ou non.
	 */
	private boolean estActive;
	
	/**
	 * Constructeur par d�faut de la classe.
	 */
	public Admin() {
		
	}
	
	/* Getters. */

	/**
	 * M�thode retournant le pseudo de l'administrateur.
	 * 
	 * @return Le pseudo de l'administrateur.
	 */
	public String getPseudo() {
		return this.pseudo;
	}

	/**
	 * M�thode retournant le mot de passe de l'administrateur.
	 * 
	 * @return Le mot de passe de l'administrateur.
	 */
	public String getPassword() {
		return this.password;
	}
	
	/**
	 * M�thode permettant de savoir si le compte est activ� ou non.
	 * 
	 * @return <b>true</b> si le compte est activ�.<br />
	 * <b>false</b> sinon.
	 */
	public boolean isActive() {
		return this.estActive;
	}
	
	/* Setters. */

	/**
	 * M�thode permettant de modifier le pseudo de l'administrateur.
	 * 
	 * @param pseudo Le nouveau pseudo de l'administrateur.
	 */
	public void setPseudo(String pseudo) {
		this.pseudo = pseudo;
	}

	/**
	 * M�thode permettant de modifier le mot de passe de l'administrateur.
	 * 
	 * @param password Le nouveau mot de passe de l'administrateur.
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	
	/**
	 * M�thode permettant de modifier le fait que le compte soit activ� ou non.
	 * 
	 * @param estActive La nouvelle valeur du bool�en.
	 */
	public void setIsActive(boolean estActive) {
		this.estActive = estActive;
	}
}
