package org.gl.jmd.utils;

/**
 * Classe utilitaire offrant des opérations sur les nombres.
 * 
 * @author Jordi CHARPENTIER & Yoann VANHOESERLANDE
 */
public class NumberUtils {
	
	/**
	 * Constructeur privé de la classe.<br />
	 * Il est <i>private</i> pour empécher son instanciation.
	 */
	private NumberUtils() {
		
	}
	
	/**
	 * Méthode permettant d'arrondir une valeur.
	 * 
	 * @param value La valeur à arrondir.
	 * @param places Le nombre de décimales à garder.
	 * 
	 * @return La valeur, arrondie.
	 */
	public static double round(double value, int places) {
	    long factor = (long) Math.pow(10, places);
	    value = value * factor;
	    long tmp = Math.round(value);
	    
	    return (double) tmp / factor;
	}
}
