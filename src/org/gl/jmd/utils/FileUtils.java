package org.gl.jmd.utils;

import java.io.*;

/**
 * Classe utilitaire permettant de simplifier la manipulation des fichiers dans l'application.
 * 
 * @author Jordi CHARPENTIER & Yoann VANHOESERLANDE
 */
public class FileUtils {
	
	/**
	 * Constructeur privé de la classe.<br />
	 * Il est <i>private</i> pour empécher son instanciation.
	 */
	private FileUtils() {
		
	}
	
	/**
	 * Méthode permettant de lire un fichier et de renvoyer son contenu.
	 * 
	 * @param pathFichier Le chemin du fichier à lire.
	 * @return Le contenu du fichier, ou une chaine vide si le fichier donné en argument n'existe pas.
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
	 * Méthode permettant de lire un fichier et de renvoyer son contenu.
	 * 
	 * @param fichier Le fichier à lire.
	 * @return Le contenu du fichier, ou une chaine vide si le fichier donné en argument n'existe pas.
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
	 * Méthode permettant d'écrire un texte dans un fichier.
	 * 
	 * @param contenu Le contenu à écrire dans le fichier.
	 * @param pathFichier L'adresse du fichier.
	 * 
	 * @return <b>0</b> si le texte a bien été écrit dans le fichier.<br />
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
