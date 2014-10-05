package org.gl.jmd.dao;

import java.io.*;
import java.util.ArrayList;

import org.gl.jmd.model.Diplome;
import org.gl.jmd.model.user.Etudiant;

import android.os.Environment;

/**
 * Objet permettant de manipuler des étudiants.
 * 
 * @author Jordi CHARPENTIER & Yoann VANHOESERLANDE
 */
public class EtudiantDAO {
	
	/**
	 * Le fichier où est enregistré l'étudiant.
	 */
	private static File fileProfil = new File(Environment.getExternalStorageDirectory().getPath() + "/cacheJMD/etudiant.cfg");
	
	/**
	 * Constructeur par défaut du DAO.<br />
	 * Le privé empèche son instanciation (classe statique).
	 */
	private EtudiantDAO() {
		
	}

	/**
	 * Méthode permettant de sauver (sérialiser) un étudiant.
	 * 
	 * @return <b>true</b> si l'étudiant a bien été sauvegardé.<br />
	 * <b>false</b> sinon.
	 */
	public static boolean save(Etudiant etudiant) {
		boolean result = false;
		
		try {
			FileOutputStream fichier = new FileOutputStream(fileProfil);
			ObjectOutputStream oos = new ObjectOutputStream(fichier);
			
			oos.writeObject(etudiant);
			oos.flush();
			oos.close();
			
			result = true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return result;    
	}
	
	/**
	 * Méthode permettant de changer l'étudiant.
	 * 
	 * @return Un objet étudiant si le fichier existe.<br />
	 * <b>null</b> sinon.
	 */
	public static Etudiant load() { 		
        if (fileProfil.exists()) {
            try {
                FileInputStream fichier = new FileInputStream(fileProfil);
                ObjectInputStream ois = new ObjectInputStream(fichier);
                
                Etudiant e = ((Etudiant) ois.readObject());
                
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
        } else {
        	Etudiant newEtudiant = new Etudiant();
			ArrayList<Diplome> listeDiplomes = new ArrayList<Diplome>();
			newEtudiant.setListeDiplomes(listeDiplomes);
			
			return newEtudiant;
        }
        
        return null;
    }
}