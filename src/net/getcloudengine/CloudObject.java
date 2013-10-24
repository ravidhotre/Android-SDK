
package net.getcloudengine;

import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

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
	private JSONObject jsonObj = new JSONObject();
	private static ConcurrentLinkedQueue<SaveTask> SaveQueue = 
						new ConcurrentLinkedQueue<SaveTask>();
	private static ConcurrentLinkedQueue<FetchTask> FetchQueue = 
			new ConcurrentLinkedQueue<FetchTask>();
	private static ConcurrentLinkedQueue<DeleteTask> DeleteQueue = 
			new ConcurrentLinkedQueue<DeleteTask>();
	
	
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
     * 
     */
	public CloudObject(String name, JSONObject obj){
		this.name = name;
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
			return jsonObj.getDouble(name);
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
			return jsonObj.getInt(name);
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
			return jsonObj.getString(name);
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
			return jsonObj.getJSONArray(name);
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
			return jsonObj.getLong(name);
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
			return jsonObj.getJSONObject(name);
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
		HttpMethod method;
		
		if(_id!=null)
		{
			// Updating an existing object
			address = CloudEndPoints.updateCloudObject(name, _id);
			method = HttpMethod.PUT;
		}
		else{
			// Saving a new object
			address = CloudEndPoints.saveCloudObject(name);
			method = HttpMethod.POST;
		}
		try{
			response = CloudEngineUtils.httpRequest(address, method, jsonObj);
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
			CloudEngineUtils.httpRequest(address, HttpMethod.DELETE);
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
			response = CloudEngineUtils.httpRequest(address, 
												HttpMethod.GET, jsonObj);
		
		try{
			this.jsonObj = new JSONObject(response);
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
	
	/**
	 * Saves the object on the server in the current thread.
	 * The method will fail and throw an exception if no network is available.
     * 
     * @throws CloudException
     */
	public void save() throws CloudException
	{		
		Context ctx = CloudEngine.getContext();
		if(CloudEngineUtils.isNetworkAvailable(ctx))
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
		
		if(CloudEngineUtils.isNetworkAvailable(ctx))
		{
			savetask.execute();
		}
		else{
			Log.e(TAG, "No network connection. Putting object in save queue");
			SaveQueue.add(savetask);
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
		
		if(CloudEngineUtils.isNetworkAvailable(ctx))
		{
			savetask.execute();
		}
		else{
			Log.e(TAG, "No network connection. Putting object in save queue");
			SaveQueue.add(savetask);
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
		
		if(CloudEngineUtils.isNetworkAvailable(ctx))
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
		if(CloudEngineUtils.isNetworkAvailable(ctx))
		{
			deletetask.execute();
		}
		else{
			Log.e(TAG, "No network connection. Putting object in delte queue");
			DeleteQueue.add(deletetask);
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
		if(CloudEngineUtils.isNetworkAvailable(ctx))
		{
			deletetask.execute();
		}
		else{
			Log.e(TAG, "No network connection. Putting object in delete queue");
			DeleteQueue.add(deletetask);
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
		if(CloudEngineUtils.isNetworkAvailable(ctx))
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
		if(CloudEngineUtils.isNetworkAvailable(ctx))
		{
			fetchtask.execute();
		}
		else{
			Log.e(TAG, "Unable to fetch CloudEngineObject. No network connection");
			FetchQueue.add(fetchtask);
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
		if(CloudEngineUtils.isNetworkAvailable(ctx))
		{
			fetchtask.execute();
		}
		else{
			Log.e(TAG, "Unable to fetch CloudEngineObject. No network connection");
			FetchQueue.add(fetchtask);
		}
	}
	
	
	private static void savePendingObjects()
	{
		Iterator<SaveTask> iterator = SaveQueue.iterator();
		while(iterator.hasNext())
		{
			SaveTask obj = iterator.next();
			obj.execute();
		}
	}
	
	private static void fetchPendingObjects()
	{
		Iterator<FetchTask> iterator = FetchQueue.iterator();
		while(iterator.hasNext())
		{
			FetchTask obj = iterator.next();
			obj.execute();
		}
	}
	
	private static void deletePendingObjects()
	{
		Iterator<DeleteTask> iterator = DeleteQueue.iterator();
		while(iterator.hasNext())
		{
			DeleteTask obj = iterator.next();
			obj.execute();
		}
	}
	
	
	public static void syncServer()
	{
		savePendingObjects();
		deletePendingObjects();
		fetchPendingObjects();
	}
	
}









