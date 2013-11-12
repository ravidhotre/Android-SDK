
package net.getcloudengine;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;

import org.json.JSONObject;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;


public class CloudEngineUtils {
	
	private static String apikey = "";
	private static String appId = "";
	private static final int FILE_BUFFER_SIZE = 1024;
	
	public static String getApiKey(){
		return apikey;
	}

	public static String getAppId(){
		return appId;
	}

	public static boolean isNetworkAvailable(Context ctx) {
	    ConnectivityManager connectivityManager 
	          = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
	
	public static void setApiKey(String key)
	{
		apikey = key;
	}
	
	public static void setAppId(String app_id)
	{
		appId = app_id;
	}
	public static String convertStreamToString(InputStream is) throws CloudException{
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
	
	private static String makeRequest(String address, HttpMethod method, JSONObject payload) throws UnknownHostException
	{
		String TAG = "CloudEngineHttpRequest";
		HttpURLConnection connection = null;
		String response = "";
		
		
		if(apikey == null || apikey == "")
		{
			throw new CloudAuthException("API key not available");
		}
		
		String request_method = method.toString();
		
		try {
			
			
			URL url = new URL(address);
			connection = (HttpURLConnection) url.openConnection();
			
	        connection.setRequestMethod(request_method);
	        connection.addRequestProperty("Authorization", "Token " + apikey);
	        connection.addRequestProperty("AppId", appId);

	        connection.setRequestProperty("Accept","application/json");
	        connection.setUseCaches(false); 
	        connection.setDoInput(true);
	        
	        if(payload != null)
	        {
	        	connection.setRequestProperty("Content-Type","application/json");
	        	connection.setDoOutput(true);
	        	OutputStreamWriter request = new OutputStreamWriter(connection.getOutputStream());
	            request.write(payload.toString());
	            request.flush();
	            request.close();
	        }
	        
	        connection.connect(); 
            InputStream in = new BufferedInputStream(connection.getInputStream());
            response = convertStreamToString(in);
            connection.disconnect();
            int response_code = connection.getResponseCode();
            
            if ((response_code != HttpURLConnection.HTTP_OK)&&
            		(response_code != HttpURLConnection.HTTP_CREATED)) {
                	
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
	
	private static String uploadFile(String address, 
							HttpMethod method,
							FileInputStream stream) 
							throws UnknownHostException 
	{
		String response = null, TAG = "CloudFileUpload";
		HttpURLConnection connection = null;
		byte[] buffer = new byte[FILE_BUFFER_SIZE];
		
		if(apikey == null || apikey == "")
		{
			throw new CloudAuthException("API key not available");
		}
		
		String request_method = method.toString();
		try {
			
			Log.i(TAG, "Uploading file to "+ address);
			URL url = new URL(address);
			connection = (HttpURLConnection) url.openConnection();
			
	        connection.setRequestMethod(request_method);
	        Log.d(TAG, "Setting Auth token: " + apikey);
	        connection.addRequestProperty("Authorization", "Token " + apikey);
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
	
	
	public static String httpRequest(String address, HttpMethod method, FileInputStream stream)
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
	
	
	public static String httpRequest(String address, HttpMethod method, JSONObject payload)
		throws CloudException, CloudAuthException
	{
		String result = null;
		int num_attempts = 0;
		while(true){
			try{
				result = makeRequest(address, method, payload);
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
	
	public static String httpRequest(String address, HttpMethod method)
			throws CloudException, CloudAuthException
	{
		String result = null;
		int num_attempts = 0;
		while(true){
			try{
				result = makeRequest(address, method, null);
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






