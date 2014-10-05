package org.gl.jmd.model.user;

import java.io.Serializable;

/**
 * Classe abstraite repr�sentant un utilisateur de l'application.
 * 
 * @author Jordi CHARPENTIER & Yoann VANHOESERLANDE
 */
public abstract class Utilisateur implements Serializable {

	private static final long serialVersionUID = -1239022711079974855L;
	
	/**
	 * L'identifiant de l'utilisateur.
	 */
	protected int id;
	
	/**
	 * Constructeur par d�faut de la classe.
	 */
	protected Utilisateur() {
		
	}

	/**
	 * M�thode retournant l'identifiant de l'utilisateur.
	 * 
	 * @return L'identifiant de l'utilisateur.
	 */
	protected int getId() {
		return this.id;
	}

	/**
	 * M�thode permettant de modifier l'identifiant de l'utilisateur.
	 * 
	 * @param id Le nouvel identifiant de l'utilisateur.
	 */
	protected void setId(int id) {
		this.id = id;
	}
}
