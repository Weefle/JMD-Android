package org.gl.jmd.dao;

import java.io.*;

import org.gl.jmd.model.Parametre;

import android.os.Environment;

/**
 * Objet permettant de manipuler des paramètres.
 * 
 * @author Jordi CHARPENTIER & Yoann VANHOESERLANDE
 */
public class ParametreDAO {
	
	/**
	 * Le fichier où sont enregistrés les paramètres de l'application.
	 */
	private static File fileParam = new File(Environment.getExternalStorageDirectory().getPath() + "/cacheJMD/param.config");
	
	/**
	 * Constructeur par défaut du DAO.<br />
	 * Le privé empèche son instanciation (classe statique).
	 */
	private ParametreDAO() {
		
	}
	
	/**
	 * Méthode retournant le fichier associé à l'objet "Parametre".
	 * 
	 * @return Le fichier de l'objet "Parametre".
	 */
	public static File getFile() {
		return fileParam;
	}

	/**
	 * Méthode permettant de sérialiser (i.e, sauvegarder) les paramètres de l'application.
	 * 
	 * @param params L'objet à sauvegarder.
	 * 
	 * @return <b>true</b> si les paramètres ont bien été sauvegardés.<br />
	 * <b>false</b> sinon.
	 */
	public static boolean save(Parametre params) {
		boolean result = false;
		
		try {
			FileOutputStream fichier = new FileOutputStream(fileParam);
			ObjectOutputStream oos = new ObjectOutputStream(fichier);
			
			oos.writeObject(params);
			oos.flush();
			oos.close();
			
			result = true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return result;    
	}

	/**
	 * Méthode permettant de charger les paramètres de l'application.
	 * 
	 * @return Un objet "Parametre" contenant les paramètres de l'application.
	 */
	public static Parametre load() {        
        if (fileParam.exists()) {
            try {
                FileInputStream fichier = new FileInputStream(fileParam);
                ObjectInputStream ois = new ObjectInputStream(fichier);
                
                Parametre e = ((Parametre) ois.readObject());
                
                ois.close();
                fichier.close();
                
                return e;
            } catch (InvalidClassException e){
                e.printStackTrace();
            } catch (java.io.IOException e) {                
                e.printStackTrace();
            } catch (ClassNotFoundException e) {                
                e.printStackTrace();
            }
        }
        
        return null;
    }
}
