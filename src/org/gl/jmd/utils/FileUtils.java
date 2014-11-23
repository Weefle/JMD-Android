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
	 * @param path Le chemin du fichier à lire.
	 * @return Le contenu du fichier, ou une chaine vide si le fichier donné en argument n'existe pas.
	 */
	public static String readFile(String path) {
		String contenuFichier = "";

		try {
			FileInputStream fis = new FileInputStream(path);
			int n;

			while((n = fis.available()) > 0) {
				byte[] b = new byte[n]; 
				int result = fis.read(b);
				
				if(result == -1) {
					break; 
				}
				
				contenuFichier = new String(b);
			}
			
			fis.close();
		} catch (Exception err) {
			return "";
		} 

		return contenuFichier;
	}
	
	/**
	 * Méthode permettant d'écrire un texte dans un fichier.
	 * 
	 * @param content Le contenu à écrire dans le fichier.
	 * @param path L'adresse du fichier.
	 * 
	 * @return <b>true</b> si le texte a bien été écrit dans le fichier.<br />
	 * <b>false</b> sinon.
	 */
	public static boolean writeFile(String content, String path) {
		try { 
			FileWriter lu = new FileWriter(path);
			BufferedWriter out = new BufferedWriter(lu);
			
			out.write(content); 
			out.close(); 
		} catch (IOException b) {
			return false;
		}

		return true;
	}
}
