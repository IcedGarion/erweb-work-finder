package it.erweb.crawler.httpClientUtil;

import java.io.IOException;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

/**
 *	A simple class for HTTP functions
 */
public class HttpGetter
{
	/**
	 * Performs an HTTP GET with target the specified URL
	 * 
	 * @param URL	target URL of the web page / resource  requested
	 * @return	A string representing the resource (i.e. an html)
	 */
    public static String get(String URL) 
    {
        HttpGet request = new HttpGet(URL);
        String body = "ERROR";

        try
        (
        		CloseableHttpClient httpclient = HttpClients.createDefault();
        		CloseableHttpResponse result = httpclient.execute(request);
        )
        {									
            body = EntityUtils.toString(result.getEntity());
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return body;
    }
}
