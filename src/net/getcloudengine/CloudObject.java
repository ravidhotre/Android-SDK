
package net.getcloudengine;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;

import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

/**
 * All data objects are stored as instances of this class. Data can be stored 
 *  as simple key, value pairs within a CloudObject. The data needs to be
 *  JSON compatible in order to be saved within a CloudObject.
 *  
 */
public class CloudObject {
	
	private String name;
	private String _id = null;
	private static final String TAG = "CloudEngineObject";
	private static final String queueFilename = "SyncQueue.txt";
	private static final String SAVEQ = "saveQueue";
	private static final String DELETEQ = "deleteQueue";
	private static final int maxFileSize = 512 * 1024 ;	// Max file size of 500 KB
	private JSONObject jsonObj = new JSONObject();
	
	CloudEngineUtils utils = CloudEngineUtils.getInstance();
	
	protected CloudObject(){
		
	}
	
	/**
	 * Constructs a new CloudObject.
	 * 
     * @param name The name of the object
     * 
     */
	public CloudObject(String name)
	{
		this.name = name;
	}
	
	/**
	 * Constructs a new CloudObject from the provided
	 * JSON object. Sets properties of the object 
	 * as per the keys and values of the JSON object. 
	 * 
	 * @param name The name of the object
     *            
     * @param obj The JSON object from which to initialize object's
     * 				properties
	 * @throws JSONException 
     * 
     */
	public CloudObject(String name, JSONObject obj) throws JSONException{
		this.name = name;
		if(obj.has("_id"))
		{
			_id = obj.getString("_id");
			obj.remove("_id");
		}
		this.jsonObj = obj;
	}
	
	/**
	 * Get the value of a particular object property. 
	 * 
     * @param key The name of the property 
     * 
     * @return the value of the given property
     */
	public Object get(String key){
		
		try {
			return jsonObj.get(key);
		} catch (JSONException e) {
			return null;
		}
		
	}
	
	
	/**
	 * Get the id of this object. Every CloudObject is assigned a 
	 * unique id the first time it is saved on the server. 
	 * 
	 * @return unique id of this object
     * 
     */
	public String getId()
	{
		return _id;
	}
	
	/**
	 * Get a list of all properties set on this object
	 * 
	 * @return iterator for properties of the object  
	 * 
     */
	@SuppressWarnings("unchecked")
	public Iterator<String> keys()
	{
		return (Iterator<String>)jsonObj.keys();
	}
	
	/**
	 * Set the value of a particular property of the object
	 * 
     * @param key the name of the property 
     * 
     * @param value the value to be set for the property
     * 
     * @throws CloudObjectException
     * 
     */
	public void put(String key, Object value) throws CloudObjectException
	{
		
		try {
			jsonObj.put(key, value);
			
		} catch (JSONException e) {
			
			Log.e(TAG, "Error while adding property " + key, e);
			throw new CloudObjectException("Invalid Property value.");
		}
		
	}
	
	/**
	 * Remove a particular property from the object
	 * 
     * @param key the name of the property to be removed
     * 
     */
	public void remove(String key)
	{
		jsonObj.remove(key);
		return;
	}
	
	/**
	 * Check if the object has a value set for a particular property
	 * 
     * @param key the name of the property
     * 
     * @return true if the property has a value. false otherwise 
     * 
     */
	public boolean has(String key)
	{
		return jsonObj.has(key);
	}
	
	/**
	 * Get a value of boolean property of the object
	 *  
     * @param key the name of the property
     * 
     * @return value of the property 
     * 
     * @throws CloudObjectException
     * 
     */
	public boolean getBoolean(String key) throws CloudObjectException
	{	
		
		if (key == null)
	        throw new CloudObjectException("Null key.");
	    
		if(!jsonObj.has(key))
			throw new CloudObjectException("CloudEngineObject[" + JSONObject.quote(key) +
					"] not found");
		
		try {
			return jsonObj.getBoolean(key);
		} catch (JSONException e) {
			throw new CloudObjectException("CloudEngineObject[" + JSONObject.quote(key) +
							"] is not a Boolean");
		}
	}
	
	/**
	 * Get a value of double property of the object
	 *  
     * @param key the name of the property
     * 
     * @return value of the property 
     * 
     * @throws CloudObjectException
     * 
     */
	public double getDouble(String key) throws CloudObjectException
	{
		if (key == null)
	        throw new CloudObjectException("Null key.");
	    
		if(!jsonObj.has(key))
			throw new CloudObjectException("CloudEngineObject[" + JSONObject.quote(key) +
					"] not found");
		
		try {
			return jsonObj.getDouble(key);
		} catch (JSONException e) {
			throw new CloudObjectException("CloudEngineObject[" + JSONObject.quote(key) +
					"] is not a number");
		}
		
	}
	
	
	/**
	 * Get a value of integer property of the object
	 *  
     * @param key the name of the property
     * 
     * @return value of the property 
     * 
     * @throws CloudObjectException
     * 
     */
	public int getInt(String key) throws CloudObjectException
	{
		if (key == null)
	        throw new CloudObjectException("Null key.");
	    
		if(!jsonObj.has(key))
			throw new CloudObjectException("CloudEngineObject[" + JSONObject.quote(key) +
					"] not found");
		
		try {
			return jsonObj.getInt(key);
		} catch (JSONException e) {
			throw new CloudObjectException("CloudEngineObject[" + JSONObject.quote(key) +
					"] is not a int");
		}
	}
	
	
	/**
	 * Get a value of Sting property of the object
	 *  
     * @param key the name of the property
     * 
     * @return value of the property 
     * 
     * @throws CloudObjectException
     * 
     */
	public String getString(String key) throws CloudObjectException
	{
		if (key == null)
	        throw new CloudObjectException("Null key.");
	    
		if(!jsonObj.has(key))
			throw new CloudObjectException("CloudEngineObject[" + JSONObject.quote(key) +
					"] not found");
		
		try {
			return jsonObj.getString(key);
		} catch (JSONException e) {
			throw new CloudObjectException("CloudEngineObject[" + JSONObject.quote(key) +
					"] is not a string");
		}
		
	}
	
	
	/**
	 * Get a value of list property of the object. The list is
	 * stored in the form of JSONArray within the object
	 *  
     * @param key the name of the property
     * 
     * @return value of the property 
     * 
     * @throws CloudObjectException
     * 
     */

	public JSONArray getJSONArray(String key) throws CloudObjectException
	{
		if (key == null)
	        throw new CloudObjectException("Null key.");
	    
		if(!jsonObj.has(key))
			throw new CloudObjectException("CloudEngineObject[" + JSONObject.quote(key) +
					"] not found");
		
		try {
			return jsonObj.getJSONArray(key);
		} catch (JSONException e) {
			throw new CloudObjectException("CloudEngineObject[" + JSONObject.quote(key) +
					"] is not a JSONArray");
		}
		
	}
	
	
	/**
	 * Initialize the Cloudengine library functions and services.
	 * 
	 * @param ctx
     *            The application context object
     * 
     * @param key The REST API client key given to the user
     * 
     * @param appid The application id for current application
     * 
     * @throws CloudObjectException
     * 
     */
	public long getLong(String key) throws CloudObjectException
	{
		if (key == null)
	        throw new CloudObjectException("Null key.");
	    
		if(!jsonObj.has(key))
			throw new CloudObjectException("CloudEngineObject[" + JSONObject.quote(key) +
					"] not found");
		
		try {
			return jsonObj.getLong(key);
		} catch (JSONException e) {
			throw new CloudObjectException("CloudEngineObject[" + JSONObject.quote(key) +
					"] is not a long");
		}
		
	}
	
	
	/**
	 * Get a value of long property of the object
	 *  
     * @param key the name of the property
     * 
     * @return value of the property 
     * 
     * @throws CloudObjectException
     * 
     */
	public JSONObject getJSONObject(String key) throws CloudObjectException
	{
		if (key == null)
	        throw new CloudObjectException("Null key.");
	    
		if(!jsonObj.has(key))
			throw new CloudObjectException("CloudEngineObject[" + JSONObject.quote(key) +
					"] not found");
		
		try {
			return jsonObj.getJSONObject(key);
		} catch (JSONException e) {
			throw new CloudObjectException("CloudEngineObject[" + JSONObject.quote(key) +
					"] is not a JSONObject");
		}
		
	}
	
	private class DeleteTask extends AsyncTask<Void, Void, CloudException> {
		
		DeleteCallback callback = null;
		
		public void setCallback(DeleteCallback cbk){
			callback = cbk;
		}
		
		@Override
		protected CloudException doInBackground(Void... args) {
				
				try{
					delete_object();
				}
				catch(CloudAuthException e){
					return e;
				}
				catch(CloudException e)	{
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
	
	private class SaveTask extends AsyncTask<Void, Void, CloudException> {
		
		SaveCallback callback = null;
		
		public void setCallback(SaveCallback cbk){
			callback = cbk;
		}
		
		@Override
		protected CloudException doInBackground(Void... args) {
					
				try{
					save_object();
				}
				catch(CloudException e)
				{
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
	
	private class FetchResult {
		public CloudObject obj = null;
		public CloudObjectException exception = null;
				
	}
	
	private void save_object() throws CloudAuthException, 
								CloudException{
		
		String response =null, address = null;
		HttpRequestBase method;
		
		if(_id!=null)
		{
			// Updating an existing object
			address = CloudEndPoints.updateCloudObject(name, _id);
			Log.i(TAG, "Updating CloudObject at endpoint " + address);
			method = new HttpPut();
		}
		else{
			
			// Saving a new object
			address = CloudEndPoints.saveCloudObject(name);
			Log.i(TAG, "Saving CloudObject at endpoint " + address);
			method = new HttpPost();
		}
		try{
			
			response = utils.httpRequest(address, method, jsonObj);
			JSONObject obj = new JSONObject(response);
			_id = obj.getString("_id");
		}
		catch (CloudAuthException e){
			throw e;
		}
		catch (CloudException e){
			throw e;
		}
		catch(Exception e){
			throw new CloudException(e.getMessage());
		}
		
	}
	
	private void delete_object() throws CloudAuthException, 
								CloudException{
		
		String address = CloudEndPoints.deleteCloudObject(name, _id);
		
		try {
			utils.httpRequest(address, new HttpDelete());
		} 
		catch (CloudAuthException e){
			 throw e;
		}
		catch (CloudException e){
			 throw e;
		}
		
	}
	
	private CloudObject retrieve_object(){
		
		String address = CloudEndPoints.retrieveCloudObject(name, _id);
		String response = null;
		response = utils.httpRequest(address, new HttpGet());
		try{
			JSONObject JSONResponse = new JSONObject(response);
			JSONObject obj = JSONResponse.getJSONObject("result");
			String id = obj.getString("_id");
			if(this._id != id){
				// We should never have come here
				return this;	// unable to update the object
			}
			obj.remove("_id");
			this.jsonObj = obj;
		}
		catch(JSONException e)
		{
			throw new CloudException("Invalid response received");
		}		
    	return this;
		
	}
	
	private class FetchTask extends AsyncTask<Void, Void, FetchResult> {
		
		FetchCallback callback = null;
		
		public void setCallback(FetchCallback cbk){
			callback = cbk;
		}
		
		@Override
		protected FetchResult doInBackground(Void... args) {
			
			FetchResult result =  new FetchResult();
			result.obj = null;
			result.exception = null;
			
			try{
				CloudObject obj = retrieve_object();
				result.obj = obj;
			}
			catch (CloudException e){
				result.exception = (CloudObjectException) e;
			}
			
			return result;	
		}
		
		protected void onPostExecute(FetchResult result) {
			
	         if(callback != null){
	        	 callback.done(result.obj,  result.exception);
	         }
	     }
	}
	
	private JSONObject Serialize(){
		
		JSONObject newobj = new JSONObject();
		Iterator<String> iter = jsonObj.keys();
		while(iter.hasNext()){
			String key = iter.next();
			try {
				newobj.put(key, jsonObj.get(key));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		try {
			newobj.put("CloudObjectName", this.name);
			if(_id != null){
				newobj.put("_id", this._id);
			}
			
		} catch (JSONException e) {
			
			e.printStackTrace();
			return null;	// We cannot deserialize the object without this info
		}
		
		return newobj;
	}
	
	private static CloudObject Deserialize(JSONObject obj)
	{
		
		String name;
		CloudObject cloudObj = null;
		try {
			name = obj.getString("CloudObjectName");
			obj.remove("CloudObjectName");
			cloudObj = new CloudObject(name, obj);
		} catch (JSONException e) {
			return null;
		}
		return cloudObj;
	}
	
	
	private static String readPendingFile(Context ctx, String filename){
		
		FileInputStream inputStream = null;
		String filedump = null;
		File file = new File(ctx.getFilesDir(), queueFilename);
		// Read the file as one big string
		try {
			
		  inputStream = ctx.openFileInput(queueFilename);
		  byte[] buffer = new byte[(int) file.length()];
		  inputStream.read(buffer);
		  filedump = new String(buffer, "UTF-8");
		  
		} catch(FileNotFoundException e){
			// if the file does not exist, it will 
			// be created below. continue.			
			return null;
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		finally{
			if(inputStream != null){
				try {
					inputStream.close();
				} catch (IOException e) {
				}
			}
		}
		return filedump;
	}
	
	
	
	// Since the internal representation of CloudObject is JSON, we leverage
	// that to persist the object instead of serializing entire CloudObject
	// while writing to the file.
	// todo: add better error handling than printStackTrace
	private class AddPending extends AsyncTask<String, Void, Void> {
		
		
		@Override
		protected Void doInBackground(String... args) {
				
			int indentSpaces = 4;
			FileOutputStream outputStream = null;
			String filedump = null;
			JSONObject allQueues = null;
			String qstring = null;
			String queue = args[0];
			
			Context ctx = CloudEngine.getContext();
			// Check file size before writing
			// If file size has exceeded maxFileSize, we don't
			// save any more data.
			File file = new File(ctx.getFilesDir(), queueFilename);
			if(file.exists() && file.length() >= maxFileSize)
			{
				return null;
			}
			
			filedump = readPendingFile(ctx, queueFilename);
			if(filedump != null){
				
				// Create JSON structures from file data
				try {
					allQueues = new JSONObject(filedump);
				} catch (JSONException e) {
					e.printStackTrace();
					return null;
				}
			}else{
				allQueues = new JSONObject();
			}
			
			JSONArray pendingQueue = null;
			try {
				
				if(allQueues.has(queue)){
					 pendingQueue = allQueues.getJSONArray(queue);
				}else{
					pendingQueue = new JSONArray();
				}
				
				if(queue == SAVEQ){
					JSONObject currObj = Serialize();
					if(currObj == null){
						// Unable to serialize object.
						return null;
					}
					pendingQueue.put(currObj);
				}
				else{
					// For delete requests, we only need to save the id
					// and the object name
					JSONObject currObj = new JSONObject();
					currObj.put("CloudObjectName", name);
					currObj.put("_id", _id);
					pendingQueue.put(currObj);
				}
				allQueues.put(queue, pendingQueue);
				qstring = allQueues.toString(indentSpaces);
				
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			} 
			
			// Write the updated queues to the file
			if(file.exists())
			{	// remove existing file
				file.delete();
			}
			
			try {
				outputStream = ctx.openFileOutput(queueFilename, Context.MODE_PRIVATE);
				outputStream.write(qstring.getBytes());
				
			} catch (Exception e) {
				e.printStackTrace();
				
			}
			finally{
				if(outputStream!=null){
					try {
						outputStream.close();
					} catch (IOException e) {
					}
				}
			}
			return null;
		}
	}
	

	/**
	 * Saves the object on the server in the current thread.
	 * The method will fail and throw an exception if no network is available.
     * 
     * @throws CloudException
     */
	public void save() throws CloudException
	{		
		Context ctx = CloudEngine.getContext();
		if(utils.isNetworkAvailable(ctx))
		{
			save_object();
		}
		else{
			throw new CloudException("Cannot save object. Network not available");
		}			
	}
	
	/**
	 * Saves the object on the server in a background thread. If no network
	 * is currently available. The object will be eventually saved when the
	 * network is available again. 
	 * 
     */
	public void saveInBackground()
	{
		Context ctx = CloudEngine.getContext();
		SaveTask savetask = new SaveTask();
		if(utils.isNetworkAvailable(ctx))
		{
			savetask.execute();
		}
		else{
			Log.e(TAG, "No network connection. Putting object in save queue");
			AddPending task =  new AddPending();
			task.execute(SAVEQ);
		}
	}
	
	
	
	/**
	 * Saves the object on the server in a background thread. If no network
	 * is currently available. The object will be eventually saved when the
	 * network is available again. Upon completion of save, the method will 
	 * call the callback function provided as an argument.
	 * 
     * @param callback The callback function to be called after the 
     * 	save operation. Information about any exception that occured 
     *  during the save operation will be passed to the callback 
     * 
     */
	public void saveInBackground(SaveCallback callback)
	{
		Context ctx = CloudEngine.getContext();
		SaveTask savetask = new SaveTask();
		savetask.setCallback(callback);
		
		if(utils.isNetworkAvailable(ctx))
		{
			savetask.execute();
		}
		else{
			Log.e(TAG, "No network connection. Putting object in save queue");
			AddPending task =  new AddPending();
			task.execute(SAVEQ);
		}		
	}
	
	
	/**
	 * Delete the object on the server in the current thread.
	 * The method will fail and throw an exception if no network is available.
     * Please note that this method only deletes the object on the server. 
     * This does not automatically deletes the current Java object. 
     * 
     * @throws CloudException
     */
	public void delete() throws CloudException {
		
		Context ctx = CloudEngine.getContext();
		if(_id == null){
			throw new CloudException("Invalid object. Save the object before deleting it.");
		}
		
		if(utils.isNetworkAvailable(ctx))
		{
			delete_object();
		}
		else{
			throw new CloudException("Cannot delete object. Network not available");
		}
		
	}
	
	/** 
	 * Deletes the object on the server in a background thread. If no network
	 * is currently available. The object will be eventually deleted when the
	 * network is available again. Upon completion of delete operation, 
	 * the method will call the callback function provided as an argument.
	 * Please note that this method only deletes the object on the server. 
     * This does not automatically deletes the current Java object. 
	 * 
     * @param callback The callback function to be called after the 
     * 	delete operation. Information of any exception that occurred
     *  during the delete operation will be passed to the callback function.
     *  
     */
	public void deleteInBackground(){
		
		if(_id == null){
			throw new CloudException("Invalid object. Save the object before deleting it.");
		}
		
		Context ctx = CloudEngine.getContext();
		DeleteTask deletetask = new DeleteTask();
		if(utils.isNetworkAvailable(ctx))
		{
			deletetask.execute();
		}
		else{
			Log.e(TAG, "No network connection. Putting object in delte queue");
			AddPending task =  new AddPending();
			task.execute(DELETEQ);
		}
	}
	
	
	/**
	 * Deletes the object on the server in a background thread. If no network
	 * is currently available. The object will be eventually deleted when the
	 * network is available again. Upon completion of delete operation, 
	 * the method will call the callback function provided as an argument.
	 * Please note that this method only deletes the object on the server. 
     * This does not automatically deletes the current Java object. 
	 * 
     * @param callback The callback function to be called after the 
     * 	delete operation. Information of any exception that occurred
     *  during the delete operation will be passed to the callback function.
     *  
     */
	public void deleteInBackground(DeleteCallback callback){
		
		if(_id == null){
			throw new CloudException("Invalid object. Save the object before deleting it.");
		}
		
		Context ctx = CloudEngine.getContext();
		DeleteTask deletetask = new DeleteTask();
		deletetask.setCallback(callback);
		if(utils.isNetworkAvailable(ctx))
		{
			deletetask.execute();
		}
		else{
			Log.e(TAG, "No network connection. Putting object in delete queue");
			AddPending task =  new AddPending();
			task.execute(DELETEQ);
		}
	}
	
	/**
	 * Fetch this object from the server and update it according to the 
	 * server data. Use this method when you want the current object state
	 * to reflect exactly as it is on the server. This method will try to 
	 * fetch the object in the current thread and will throw an exception if
	 * network is not available
	 * 
	 * @throws CloudException
     * 
     */
	public CloudObject fetch() throws CloudException
	{
		if(_id == null){
			throw new CloudException("Invalid object. Save the object before fetching it.");
		}
		
		Context ctx = CloudEngine.getContext();
		if(utils.isNetworkAvailable(ctx))
		{
			return retrieve_object();
		}
		else{
			throw new CloudException("Network unavailable");
		}
			
	}
	
	
	/**
	 * Fetch this object from the server and update it according to the 
	 * server data. Use this method when you want the current object state
	 * to reflect exactly as it is on the server. This method will fetch
	 * the object data in a background thread and call the callback
	 * function provided as argument upon completion of the fetch operation.
	 * Information of any exception that occurred during the fetch
	 * operation will be passed to the callback function.
	 * 
	 * @throws CloudException
     * 
     */
	public void fetchInBackground()
	{
		if(_id == null){
			throw new CloudException("Invalid object. Save the object before fetching it.");
		}
		Context ctx = CloudEngine.getContext();
		FetchTask fetchtask = new FetchTask();
		if(utils.isNetworkAvailable(ctx))
		{
			fetchtask.execute();
		}
		else{
			Log.e(TAG, "Unable to fetch CloudEngineObject. No network connection");
		}
	}
	
	
	/**
	 * Fetch this object from the server and update it according to the 
	 * server data. Use this method when you want the current object state
	 * to reflect exactly as it is on the server. This method will fetch
	 * the object data in a background thread and call the callback
	 * function provided as argument upon completion of the fetch operation.
	 * Information of any exception that occurred during the fetch
	 * operation will be passed to the callback function.
	 * 
	 * @throws CloudException
     * 
     */
	public void fetchInBackground(FetchCallback callback)
	{
		if(_id == null){
			throw new CloudException("Invalid object. Save the object before fetching it.");
		}
		Context ctx = CloudEngine.getContext();
		FetchTask fetchtask = new FetchTask();
		fetchtask.setCallback(callback);
		if(utils.isNetworkAvailable(ctx))
		{
			fetchtask.execute();
		}
		else{
			Log.e(TAG, "Unable to fetch CloudEngineObject. No network connection");
		}
	}
	
	
	
	
	public static void syncServer(Context ctx)
	{
		
		String filedump = null;
		File file = new File(ctx.getFilesDir(), queueFilename);
		
		// Read the file as one big string
		filedump = readPendingFile(ctx, queueFilename);
		
		
		JSONObject allQueues = null;
		JSONArray queue = null;
		CloudObject cloudObj = null;
		if(filedump != null){
			
			try {
				allQueues = new JSONObject(filedump);
				JSONObject qobj = null;
				
				if(allQueues.has(SAVEQ))
				{
					// Save pending objects
					queue = allQueues.getJSONArray(SAVEQ);
					for (int i = 0; i < queue.length(); i++) {
					  qobj = queue.getJSONObject(i);
					  cloudObj = Deserialize(qobj);
					  if(cloudObj != null){
						  cloudObj.saveInBackground();
					  }
					}
				}
				
				if(allQueues.has(DELETEQ)){
					// delete pending object
					queue = allQueues.getJSONArray(DELETEQ);
					for (int i = 0; i < queue.length(); i++) {
						
						qobj = queue.getJSONObject(i);
						cloudObj = Deserialize(qobj);
						if(cloudObj != null){
							cloudObj.deleteInBackground();
						}
					}
				}
				
			} catch (JSONException e) {
				e.printStackTrace();
				return;
			}
		}
		file.delete();
		
		
	}
	
}









