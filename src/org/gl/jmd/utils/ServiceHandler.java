package org.gl.jmd.utils;

import java.io.*;

import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

public class ServiceHandler {

	private String response = null;

	public final static int GET = 1;

	public ServiceHandler() {

	}

	public String makeServiceCall(String url, int method) {
		try {
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpEntity httpEntity = null;
			HttpResponse httpResponse = null;

			if (method == GET) {				
				HttpGet httpGet = new HttpGet(url);
				httpResponse = httpClient.execute(httpGet);
			}
			
			httpEntity = httpResponse.getEntity();
			response = EntityUtils.toString(httpEntity);
			
			if (httpEntity != null) {
			    try {
			        httpEntity.consumeContent();
			    } catch (IOException e) {
			        e.printStackTrace();
			    }
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return response;
	}
}