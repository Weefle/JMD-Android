package org.gl.jmd.utils;

import java.io.*;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.*;
import org.apache.http.client.*;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.*;
import org.apache.http.util.ByteArrayBuffer;

/**
 * Classe utilitaire permettant de simplifier la manipulation des pages WEB et la r�cup�ration de leur contenu.
 * 
 * @author Jordi CHARPENTIER & Yoann VANHOESERLANDE
 */
public class WebUtils {
	
	/**
	 * Constructeur priv� de la classe, pour emp�cher son instanciation.
	 */
	private WebUtils() {
		
	}
	
	/**
	 * M�thode permettant de r�cup�rer le contenu d'une page internet. 
	 * 
	 * @throws ClientProtocolException Si l'utilisateur n'a pas un acc�s internet.
	 * @throws IOException Si la r�cup�ration du contenu de la page �choue.
	 * 
	 * @param url L'url de la page voulue.
	 * @return Le contenu de la page.
	 */
	public static String getPage(String url) throws ClientProtocolException, IOException {
		StringBuffer stringBuffer = new StringBuffer("");
		BufferedReader bufferedReader = null;
		
		url = url.replaceAll(" ", "");

		try {
			HttpClient httpClient = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet();

			URI uri = new URI(url);
			httpGet.setURI(uri);
			HttpResponse httpResponse = httpClient.execute(httpGet);
			InputStream inputStream = httpResponse.getEntity().getContent();
			bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
			String ligneCodeHTML = bufferedReader.readLine();

			while(ligneCodeHTML != null) {
				stringBuffer.append(ligneCodeHTML);
				ligneCodeHTML = bufferedReader.readLine();
			} 
		} catch (Exception e) { 
			e.printStackTrace();
			
			return "-1"; 
		}

		finally {
			if(bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch(IOException e) { 
					return "-1"; 
				}
			}
		}

		return stringBuffer.toString();
	} 
	
	/**
	 * M�thode permettant de t�l�charger un fichier � partir d'une URL.
	 * 
	 * @param urlName L'URL du fichier � t�l�charger.
	 * @param fileName L'adresse o� sera sauvegard� le fichier.
	 */
	public static void downloadFromUrl(String urlName, String fileName) {  
		try {
			URL url = new URL(urlName); 
			File file = new File(fileName);
			URLConnection ucon = url.openConnection();

			InputStream is = ucon.getInputStream();
			BufferedInputStream bis = new BufferedInputStream(is);

			ByteArrayBuffer baf = new ByteArrayBuffer(50);
			int current = 0;
			
			while ((current = bis.read()) != -1) {
				baf.append((byte) current);
			}

			FileOutputStream fos = new FileOutputStream(file);
			fos.write(baf.toByteArray());
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
