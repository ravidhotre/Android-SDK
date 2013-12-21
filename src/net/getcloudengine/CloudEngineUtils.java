
package net.getcloudengine;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import android.webkit.CookieManager;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;


public class CloudEngineUtils {
	
	private static final int FILE_BUFFER_SIZE = 1024;
	HttpClient httpclient = null;
	CookieManager cookieManager = null;
	private static CloudEngineUtils instance = null;
	String TAG = "CloudEngineUtils";
	
	protected CloudEngineUtils(){
		
		 httpclient = new DefaultHttpClient();
		 	cookieManager =  CookieManager.getInstance();
		 
	}
	
	public void addCookie(String address, HttpCookie cookie){
		
		cookieManager.setCookie(address, cookie.toString());
		
	}
	
	
	public static CloudEngineUtils getInstance(){
		 
		if(instance == null){
			instance = new CloudEngineUtils();
		}
		return instance;
	}
	
	
	public boolean isNetworkAvailable(Context ctx) {
	    
		ConnectivityManager connectivityManager 
	          = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
	
	
	public String convertStreamToString(InputStream is) throws CloudException{
        
		BufferedReader reader = new BufferedReader(new InputStreamReader(is), 8*1024);
        StringBuilder sb = new StringBuilder();
 
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();		
            throw new CloudException(e);
            
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
                throw new CloudException(e);
            }
        }	     
        return sb.toString();
    }
	
	
	private String makeRequest(String address, 
			HttpRequestBase request, JSONObject payload) 
					throws UnknownHostException,
					CloudAuthException
					
	{
		String responseString = null;
		StatusLine statusLine = null;
		String apiKey = CloudEngine.getApiKey();
		String appId = CloudEngine.getAppId();
		 HttpResponse response = null;
		
		if(apiKey == null || apiKey == "" || appId == null || appId == "")
		{
			throw new CloudAuthException("API/App key not available");
		}
		
		request.addHeader("Authorization", "Token " + apiKey);
		request.addHeader("AppId", appId);
		request.addHeader("Accept", "application/json" );
		         
		try{	
				URL url = new URL(address);
				request.setURI(url.toURI());
				
				if(payload != null){
					
					request.addHeader("Content-Type","application/json" );
					HttpPost post = (HttpPost) request;
					StringEntity entity = new StringEntity(payload.toString());
					post.setEntity(entity);
					
				}
				
			   response = httpclient.execute(request);
			   statusLine = response.getStatusLine();
			   ByteArrayOutputStream out = new ByteArrayOutputStream();
		       response.getEntity().writeTo(out);
		       out.close();
		       responseString = out.toString();
			   
		}
		
		catch(Exception e){
			
			Log.e(TAG, "Unable to connect to server. " + e.getMessage());
			throw new CloudException("Unable to connect to server");
		} 
		 
		 int status = statusLine.getStatusCode();
		 Log.d(TAG, "HTTP response status code: " + status);
		 Log.d(TAG, "HTTP reason: " + statusLine.getReasonPhrase());
		 Log.d(TAG, "HTTP response: " + responseString);
		 
		 return responseString;
		 
	}
	
	private String uploadFile(String address, 
							HttpRequestBase method,
							FileInputStream stream) 
							throws UnknownHostException 
	{
		String response = null, TAG = "CloudFileUpload";
		HttpURLConnection connection = null;
		byte[] buffer = new byte[FILE_BUFFER_SIZE];
		
		String apiKey = CloudEngine.getApiKey();
		String appId = CloudEngine.getAppId();
		
		
		if(apiKey == null || apiKey == "" || appId == null || appId == "")
		{
			throw new CloudAuthException("API key not available");
		}
		
		String request_method = method.toString();
		try {
			
			Log.i(TAG, "Uploading file to "+ address);
			URL url = new URL(address);
			connection = (HttpURLConnection) url.openConnection();
			
	        connection.setRequestMethod(request_method);
	        connection.addRequestProperty("Authorization", "Token " + apiKey);
	        connection.addRequestProperty("AppId", appId);

	        connection.setRequestProperty("Accept","application/json");
	        connection.setUseCaches(false); 
	        connection.setDoInput(true);
	        connection.setRequestProperty("Content-Type","text/plain");
        	connection.setDoOutput(true);
        	DataOutputStream request = new DataOutputStream(connection.getOutputStream());
        	int numBytes = stream.read(buffer, 0, FILE_BUFFER_SIZE);

            while (numBytes > 0) {
            	request.write(buffer, 0, numBytes);
                numBytes = stream.read(buffer, 0, FILE_BUFFER_SIZE);
            }
        	request.flush();
            request.close();
	        
	        
	        connection.connect(); 
            InputStream in = new BufferedInputStream(connection.getInputStream());
            response = convertStreamToString(in);
            connection.disconnect();
            int response_code = connection.getResponseCode();
            
            if (response_code != HttpURLConnection.HTTP_CREATED){
                	
        		Log.e(TAG, "Error connecting to server");
        		Log.e(TAG, "Response code: " + response_code);
        		Log.e(TAG, "Response: " + response);
                if(response_code == HttpURLConnection.HTTP_UNAUTHORIZED)
                {
                	throw new CloudAuthException(response);
                }
                else{
                	throw  new CloudException(response);
                }
            }
            
		} 
		catch (UnknownHostException e){
			 throw e;		// Let the caller handle this
		}
		catch (Exception e){
			// For unreachable host, this might be java.net.UnknownHostException
			 Log.d(TAG, "Error while connecting to server."+ e.getMessage());
			 e.printStackTrace();
			 throw new CloudException(e);
		}
		finally{
			if(connection != null)
			{
				connection.disconnect();
			}
		}
		
		return response;
		
	}
	
	
	public String httpRequest(String address, 
			HttpRequestBase method, FileInputStream stream)
			throws CloudException, CloudAuthException
	{
		String result = null;
		int num_attempts = 1;
		while(true){
			try{
				result = uploadFile(address, method, stream);
				break;
			}
			catch(UnknownHostException e){
				if(num_attempts<3)
				{
					num_attempts += 1;
					Log.d("CloudEngineHttpRequest", "Host unreachable. retrying to connect...");
					try {
						Thread.sleep(300);
					} catch (InterruptedException e1) {
						
					}
					continue;
				}
				throw new CloudException("Server is unreachable.");
			}
		}
		return result;
	}
	
	
	public String httpRequest(String address, HttpRequestBase request, JSONObject payload)
		throws CloudException, CloudAuthException
	{
		String result = null;
		int num_attempts = 0;
		while(true){
			try{
				result = makeRequest(address, request, payload);
				break;
			}
			catch(UnknownHostException e){
				if(num_attempts<3)
				{
					num_attempts += 1;
					Log.d("CloudEngineHttpRequest", "Host unreachable. retrying to connect...");
					try {
						Thread.sleep(300);
					} catch (InterruptedException e1) {
						
					}
					continue;
				}
				throw new CloudException("Server is unreachable.");
			}
		}
		return result;
	}
	
	public String httpRequest(String address, HttpRequestBase request)
			throws CloudException, CloudAuthException
	{
		String result = null;
		int num_attempts = 0;
		while(true){
			try{
				result = makeRequest(address, request, null);
				break;
			}
			catch(UnknownHostException e){
				if(num_attempts<3)
				{
					num_attempts += 1;
					Log.d("CloudEngineHttpRequest", "Host unreachable. retrying to connect...");
					try {
						Thread.sleep(300);
					} catch (InterruptedException e1) {
						
					}
					continue;
				}
				throw new CloudException("Server is unreachable.");
			}
		}
		return result;
	}

}






