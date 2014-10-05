package org.gl.jmd.utils;

import java.io.*;

/**
 * Classe utilitaire permettant de simplifier la manipulation des fichiers dans l'application.
 * 
 * @author Jordi CHARPENTIER & Yoann VANHOESERLANDE
 */
public class FileUtils {
	
	/**
	 * Constructeur priv� de la classe.<br />
	 * Il est <i>private</i> pour emp�cher son instanciation.
	 */
	private FileUtils() {
		
	}
	
	/**
	 * M�thode permettant de lire un fichier et de renvoyer son contenu.
	 * 
	 * @param pathFichier Le chemin du fichier � lire.
	 * @return Le contenu du fichier, ou une chaine vide si le fichier donn� en argument n'existe pas.
	 */
	public static String lireFichier(String pathFichier) {
		String contenuFichier = "";

		try {
			FileInputStream fis = new FileInputStream(pathFichier);
			int n;

			while((n = fis.available()) > 0) {
				byte[] b = new byte[n]; 
				int result = fis.read(b);
				
				if(result == -1) {
					break; 
				}
				
				contenuFichier = new String(b);
			}
		} catch (Exception err) {
			return "";
		} 

		return contenuFichier;
	}
	
	/**
	 * M�thode permettant de lire un fichier et de renvoyer son contenu.
	 * 
	 * @param fichier Le fichier � lire.
	 * @return Le contenu du fichier, ou une chaine vide si le fichier donn� en argument n'existe pas.
	 */
	public static String lireFichier(File fichier) {
		String contenuFichier = "";

		try {
			FileInputStream fis = new FileInputStream(fichier);
			int n;

			while((n = fis.available()) > 0) {
				byte[] b = new byte[n]; 
				int result = fis.read(b);
				
				if(result == -1) {
					break; 
				}
				
				contenuFichier = new String(b);
			}
		} catch (Exception err) {
			return "";
		} 

		return contenuFichier;
	}
	
	/**
	 * M�thode permettant d'�crire un texte dans un fichier.
	 * 
	 * @param contenu Le contenu � �crire dans le fichier.
	 * @param pathFichier L'adresse du fichier.
	 * 
	 * @return <b>0</b> si le texte a bien �t� �crit dans le fichier.<br />
	 * <b>-1</b> sinon.
	 */
	public static int ecrireTexteFichier(String contenu, String pathFichier) {
		try { 
			FileWriter lu = new FileWriter(pathFichier);
			BufferedWriter out = new BufferedWriter(lu);
			
			out.write(contenu); 
			out.close(); 
		} catch (final IOException b) {
			return -1;
		}

		return 0;
	}
}
