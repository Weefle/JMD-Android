package org.gl.jmd.utils;

import java.io.*;
import java.net.SocketTimeoutException;

import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.*;
import org.apache.http.util.EntityUtils;

public class WebUtils {

	public final static int GET = 1;

	private WebUtils() {

	}

	public static String call(String url, int method) throws ClientProtocolException, IOException, SocketTimeoutException {
		HttpParams httpParameters = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParameters, 5000);
		HttpConnectionParams.setSoTimeout(httpParameters, 5000);

		DefaultHttpClient httpClient = new DefaultHttpClient(httpParameters);
		HttpEntity httpEntity = null;
		HttpResponse httpResponse = null;

		if (method == GET) {				
			HttpGet httpGet = new HttpGet(url);
			httpResponse = httpClient.execute(httpGet);
		} else {
			return "unimplemented";
		}

		httpEntity = httpResponse.getEntity();
		String response = EntityUtils.toString(httpEntity);

		if (httpEntity != null) {
			try {
				httpEntity.consumeContent();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return response;
	}
}