package org.gl.jmd.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.gl.jmd.model.Annee;
import org.gl.jmd.model.enumeration.DecoupageType;

import android.content.Context;
import android.graphics.*;
import android.graphics.Paint.Style;
import android.graphics.pdf.PdfDocument;
import android.graphics.pdf.PdfDocument.*;
import android.print.PrintAttributes;
import android.print.PrintAttributes.Margins;
import android.print.pdf.PrintedPdfDocument;

/**
 * Classe utilitaire permettant de manipuler des fichiers PDF.
 * 
 * @author Jordi CHARPENTIER & Yoann VANHOESERLANDE
 */
public class PdfUtils {

	/**
	 * Constructeur privé de la classe.<br />
	 * Il est <i>private</i> pour empécher son instanciation.
	 */
	private PdfUtils() {

	}

	/**
	 * Méthode permettant de générer un rapport PDF à partir d'une année.
	 * 
	 * @param ann L'année utilisée pour générer le rapport.
	 * @param c
	 * 
	 * @return <b>true</b> si le rapport a bien été généré et sauvé.
	 * <b>false</b> sinon.
	 */
	public static boolean generateYearRapport(Annee ann, Context c) {
		boolean res = false;
		
		PrintAttributes printAttrs = new PrintAttributes.Builder().
				setColorMode(PrintAttributes.COLOR_MODE_MONOCHROME).
				setMediaSize(PrintAttributes.MediaSize.ISO_A4).
				setMinMargins(Margins.NO_MARGINS).
				build();
		
		PdfDocument document = new PrintedPdfDocument(c, printAttrs);
		PageInfo pageInfo = new PageInfo.Builder(595, 842, 1).create();
		
		Page page = document.startPage(pageInfo);
		
		Paint paint = new Paint(); 
		paint.setColor(Color.WHITE); 
		paint.setStyle(Style.FILL); 
		
		page.getCanvas().drawPaint(paint); 

		paint.setColor(Color.BLACK); 
		paint.setTextSize(12); 
		
		paint.setFakeBoldText(true);
		paint.setTextSize(18);
		page.getCanvas().drawText("RELEVE DE NOTES ET RESULTATS", 150, 40, paint);
		paint.setFakeBoldText(false);
		paint.setTextSize(12);
		page.getCanvas().drawText("Inscrit en : " + ann.getNom() + ".", 50, 70, paint);
		
		if (ann.getMoyenne() != -1.0) {
			// Si l'année n'a pas de découpage (=> Apprentissage).
			if (ann.getDecoupage() == DecoupageType.NULL) {
				page.getCanvas().drawText("A obtenu les notes suivantes :", 50, 85, paint); 
				
				int startY = 115;
			
				for (int i = 0; i < ann.getListeUE().size(); i++) {
					paint.setFakeBoldText(true);
					
					String txtUE = ann.getListeUE().get(i).getNom() + " (coefficient " + ann.getListeUE().get(i).getTotalCoeff() + ") : ";
					
					if (ann.getListeUE().get(i).getMoyenne() != -1.0) {
						txtUE += ann.getListeUE().get(i).getMoyenne() + "/20";
					} 
					
					page.getCanvas().drawText(txtUE, 50, startY, paint); 
					
					for (int j = 0; j < ann.getListeUE().get(i).getListeMatieres().size(); j++) {
						paint.setFakeBoldText(false);
						startY += 15;
						String txtMatiere = ann.getListeUE().get(i).getListeMatieres().get(j).getNom() + " (coefficient " + ann.getListeUE().get(i).getListeMatieres().get(j).getCoefficient() + ")"; 
						
						if (ann.getListeUE().get(i).getListeMatieres().get(j).getNoteFinale() != -1.0) {
							page.getCanvas().drawText(txtMatiere + " : " + ann.getListeUE().get(i).getListeMatieres().get(j).getNoteFinale() + "/20", 50, startY, paint); 
						} else {
							page.getCanvas().drawText(txtMatiere + " : Aucune note.", 50, startY, paint); 
						}
					}
					
					startY += 20;
				}
				
				startY += 10;
				
				String txtRes = "Résultat : ";
				
				if (ann.getMoyenne() >= 10) {
					txtRes += "Admis - Mention " + ann.getMention();
				} else {
					txtRes += "Ajourné";
				}
				
				page.getCanvas().drawText("Moyenne générale : " + ann.getMoyenne() + "/20", 50, startY, paint);
				startY += 15;
				page.getCanvas().drawText(txtRes, 50, startY, paint);
				startY += 30;
	
				page.getCanvas().drawText("Fait à " + ann.getEtablissement().getVille() + ", le " + new SimpleDateFormat("dd/MM/yyyy").format(new Date()) + ".", 50, startY, paint);	
			} else if (ann.getDecoupage() == DecoupageType.SEMESTRE) {
				// TODO
			} else if (ann.getDecoupage() == DecoupageType.TRIMESTRE) {
				// TODO
			}
		} else {
			page.getCanvas().drawText("Aucune note entrée.", 50, 100, paint);
			page.getCanvas().drawText("Fait à " + ann.getEtablissement().getVille() + ", le " + new SimpleDateFormat("dd/MM/yyyy").format(new Date()), 50, 130, paint);	
		}
		
		document.finishPage(page);
		
		try {
			document.writeTo(new FileOutputStream(new File("/sdcard/cacheJMD/rapport-" + ann.getId() + ".pdf")));
			res = true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return res;
	}
}