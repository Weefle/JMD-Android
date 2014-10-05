package org.gl.jmd.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.gl.jmd.model.enumeration.ParamType;

/**
 * Classe représentant les paramètres de l'application.
 * 
 * @author Jordi CHARPENTIER & Yoann VANHOESERLANDE
 */
public class Parametre implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * Ensemble des paramètres de l'application.
	 */
	private Map<ParamType, String> params = new HashMap<ParamType, String>();
	
	/**
	 * Constructeur par défaut de la classe.
	 */
	public Parametre() {
		
	}
	
	/**
	 * Méthode permettant d'ajouter un paramètre à l'application.
	 * 
	 * @param name Le type du paramètre à ajouter.
	 * @param value La valeur du paramètre à ajouter.
	 */
	public void addParam(ParamType name, String value) {
		params.put(name, value);
	}
	
	/**
	 * Méthode permettant de supprimer un paramètre de l'application.
	 * 
	 * @param key La clé du paramètre à supprimer (i.e, le type).
	 */
	public void removeParam(ParamType key) {
		params.remove(key);
	}
	
	/**
	 * Méthode permettant de rechercher un paramètre dans les paramètres de l'application.
	 * 
	 * @param key Le paramètre recherché.
	 * 
	 * @return L'élément recherché, s'il existe.<br />
	 * <b>null</b> sinon.
	 */
	public String get(ParamType key) {
		return params.get(key);
	}
}
