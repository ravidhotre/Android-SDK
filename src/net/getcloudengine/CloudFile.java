package net.getcloudengine;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.apache.http.client.methods.HttpPost;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

/**
 * Class to represent a file on the disk. Allows uploading and 
 * downloading of a file to CloudEngine server. 
 *  
 */
public class CloudFile {
	private String name, path;
	private long size;
	private String file_url = null;
	private final String TAG ="CloudFile";
	
	CloudEngineUtils utils = CloudEngineUtils.getInstance();
	
	/**
	 * Constructs a new CloudFile object
	 * 
	 * @param location location of the file on the disc
	 * 
     * @throws CloudException in case file is not found at
     * the specified location
     * 
     */
	public CloudFile(String location){
		
		File file = new File(location);
		this.size = file.length();
		if (!file.exists()) {
            throw new CloudException("File does not exist on the given path");
        }
		
		this.path = location;
		String []names = location.split("/");
		int len = names.length;
		this.name = names[len-1];
		
	}
	
	/**
	 * Get the server URL from where the file can be downloaded
	 * 
     * @return url URL from where the file can be downloaded
     * 
     */
	public String getUrl(){
		return file_url;
	}
	
	/**
	 * Returns the name of the file as provided to the constructor
	 * This is only the name of the file and not its full path.
	 * 
     */
	public String getName(){
		return name;
	}
	
	
	private void saveFile() throws 
			CloudAuthException, CloudException{
		
		String response =null; JSONObject obj = null;
		String url = null;
		String address = CloudEndPoints.saveCloudFile(name);
		HttpPost post = new HttpPost();
		
		Log.i(TAG, "Trying to save file " + name);
		FileInputStream stream;
		try {
			stream = new FileInputStream(new File(path));
		} catch (FileNotFoundException e1) {
			Log.e(TAG, "File is missing");
			throw new CloudException("File is missing at the location");
		}
		
		
		response = utils.httpRequest(address, post, stream);
		Log.i(TAG, "File save complete. Response: " + response);
		

		try {
			obj = new JSONObject(response);
			url = obj.getString("url");
			this.file_url = url;
			
		} catch (JSONException e) {
			throw new CloudException("Invalid response received after file upload");
		}
		

	}
	
	private class SaveTask extends AsyncTask<Void, Void, CloudException> {

		SaveCallback callback = null;
		
		public void setCallback(SaveCallback cbk){
			callback = cbk;
		}
		
		@Override
		protected CloudException doInBackground(Void... args) {
					try{
						saveFile();
					}
					catch (CloudAuthException e){
						 return e;
					}
					catch (CloudException e){
						 return e;
					}
					
				return null;
		}
		
		protected void onPostExecute(CloudException e) {
			
	         if(callback != null){
	        	 callback.done(e);
	         }
	     }
	}
	
	/**
	 * Saves this file on the server in the current thread.
	 * This method throws an exception is there is no network
	 * available.
	 * 
     * @throws CloudException 
     * 
     */
	public void save() throws CloudException{
		
		Context ctx = CloudEngine.getContext();
		if(utils.isNetworkAvailable(ctx)){
			
			saveFile();
		}
		else{
			throw new CloudException("Unable to save object. No network available.");
		}
	}
	
	/**
	 * Saves this file on the server in a background thread.
	 * If there is no network available, this method will save
	 * the object eventually when network becomes again available.
	 * 
     * @throws CloudException 
     * 
     */
	public void saveInBackground(){
		
		Context ctx = CloudEngine.getContext();
		SaveTask savetask = new SaveTask();
		
		if(utils.isNetworkAvailable(ctx))
		{
			savetask.execute();
		}
		else{
			throw new CloudException("Unable to save file. No network available");
		}	
	}
	
	/**
	 * Saves this file on the server in a background thread.
	 * If there is no network available, this method will save
	 * the object eventually when network becomes again available.
	 * Upon completion of save operation the provided callback
	 * function is called along with any Exception information that
	 * may have occured during the save operation.
	 * 
     * @throws CloudException 
     * 
     */
	public void saveInBackground(SaveCallback callback){
		
		Context ctx = CloudEngine.getContext();
		SaveTask savetask = new SaveTask();
		savetask.setCallback(callback);
		
		if(utils.isNetworkAvailable(ctx))
		{
			savetask.execute();
		}
		else{
			throw new CloudException("Unable to save file. No network available");
		}	
	}
	
}








