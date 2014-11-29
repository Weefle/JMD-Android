package org.gl.jmd;

/**
 * Classe contenant les constantes de l'application.
 * <i>Exemple</i> : URL du serveur.
 * 
 * @author Jordi CHARPENTIER & Yoann VANHOESERLANDE
 */
public class Constantes {

	/**
	 * Constructeur privé de la classe.
	 * Empèche son instanciation (=> classe statique).
	 */
	private Constantes() {
		
	}
	
	/**
	 * Regex pour valider un email.
	 */
	public static final String EMAIL_PATTERN = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,4}$";
	
	/**
	 * URL du serveur sur lesquels sont déployés les services web de JMD.
	 */
	public static final String URL_SERVER = "http://ns3281017.ip-5-39-94.eu:8080/JMD/webresources/";
	
	/**
	 * La limite de taille des textes à afficher.
	 */
	public static final int LIMIT_TEXT = 20;
}