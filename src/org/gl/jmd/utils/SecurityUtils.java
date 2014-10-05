package org.gl.jmd.utils;

import java.math.BigInteger;
import java.security.*;

/**
 * Classe utilitaire permettant de simplifier la manipulation de méthodes liées à la sécurité (hashage de mot de passe, par exemple) dans l'application.
 * 
 * @author Jordi CHARPENTIER & Yoann VANHOESERLANDE
 */
public class SecurityUtils {

	/**
	 * Constructeur privé de la classe.<br />
	 * Il est <i>private</i> pour empécher son instanciation.
	 */
	private SecurityUtils() {
		
	}
	
	/**
	 * Méthode permettant d'hasher une chaîne de caractères en MD5.
	 * 
	 * @param s La chaîne à hasher en MD5.
	 * 
	 * @return Une chaîne hashée en MD5.
	 */
	public static String md5(String strings) {
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("MD5");
			md.update(strings.getBytes());
		} catch (NoSuchAlgorithmException ex) {
			throw new RuntimeException("Le MD5 n'est pas supporté.");
		}

		final BigInteger bigInt = new BigInteger(1, md.digest());

		return String.format("%032x", bigInt);
	}

	/**
	 * Méthode permettant d'hasher une chaîne de caractères en SHA-256.
	 * 
	 * @param passwordToHash La chaîne à hasher.
	 * 
	 * @return La chaîne hashée en SHA-256.
	 */
	public static String sha256(String passwordToHash) {
		String generatedPassword = null;
		
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			byte[] bytes = md.digest(passwordToHash.getBytes());
			StringBuilder sb = new StringBuilder();
			
			for(int i=0; i < bytes.length; i++) {
				sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
			}
			
			generatedPassword = sb.toString();
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("Le SHA-256 n'est pas supporté.");
		}
		
		return generatedPassword;
	}
}