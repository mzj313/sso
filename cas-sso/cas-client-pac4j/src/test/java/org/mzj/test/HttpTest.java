package org.mzj.test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.util.HttpUtils;

public class HttpTest {
	@Before
	public void init() {		
		HttpUtils.setConnectTimeout(5000);
		HttpUtils.setReadTimeout(50000);
	}
	
	@Test
	public void test01() {
		HttpURLConnection connection = null;
		try {
			connection = HttpUtils.openPostConnection(new URL("http://yunti.xkw.com:8083/dev/v1/tickets"));
			String username = "18801293499";
			String password = "123abc1";
			final String payload = HttpUtils.encodeQueryParam(Pac4jConstants.USERNAME, username) + "&"
					+ HttpUtils.encodeQueryParam(Pac4jConstants.PASSWORD, password);
			System.out.println("payload: " + payload);

			final BufferedWriter out = new BufferedWriter(
					new OutputStreamWriter(connection.getOutputStream(), "UTF-8"));
			out.write(payload);
			out.close();

			final String locationHeader = connection.getHeaderField("location");//这个会发起请求
			final int responseCode = connection.getResponseCode();
			if (locationHeader != null && responseCode == HttpConstants.CREATED) {
				System.out.println(locationHeader.substring(locationHeader.lastIndexOf("/") + 1));
			}

			if (connection.getErrorStream() != null) {
				BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getErrorStream(), "utf-8"));
				String result = "";
				String line = null;
				while ((line = reader.readLine()) != null) {
					result += line;
				}
				reader.close();
				System.out.println(result);
			}
		} catch (final Exception e) {
			e.printStackTrace();
		} finally {
			HttpUtils.closeConnection(connection);
		}

		try {
			TimeUnit.SECONDS.sleep(30);
		} catch (InterruptedException e) {
		}
	}

}
