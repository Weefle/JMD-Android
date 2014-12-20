package org.gl.jmd;

import java.io.File;

/**
 * Classe contenant les constantes de l'application.
 * 
 * @author Jordi CHARPENTIER & Yoann VANHOESERLANDE
 */
public class Constantes {

	/**
	 * Constructeur priv� de la classe.
	 * Emp�che son instanciation (=> classe statique).
	 */
	private Constantes() {
		
	}
	
	/**
	 * Regex pour valider un email.
	 */
	public static final String EMAIL_PATTERN = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,4}$";
	
	/**
	 * URL du serveur sur lesquels sont d�ploy�s les services web de JMD.
	 */
	public static final String URL_SERVER = "http://ns3281017.ip-5-39-94.eu:8080/JMD/webresources/";
	
	/**
	 * La limite de taille des textes � afficher.
	 */
	public static final int LIMIT_TEXT = 20;
	
	/* Fichiers. */
	
	/**
	 * Le fichier contenant le param�tre sur l'accueil de l'appli.
	 * C'est � dire savoir si on arrive sur l'accueil �tudiant ou sur l'accueil admin ?
	 */
	public static final File FILE_PARAM = new File("/sdcard/cacheJMD/param.jmd");
	
	/**
	 * Le fichier contenant le token de session de l'administrateur s'il est connect�.
	 */
	public static final File FILE_TOKEN = new File("/sdcard/cacheJMD/token.jmd");
	
	/**
	 * L'URL du fichier contenant le pseudo de l'administrateur s'il est connect�.
	 */
	public static final File FILE_PSEUDO = new File("/sdcard/cacheJMD/pseudo.jmd");
}