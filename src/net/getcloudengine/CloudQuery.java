package net.getcloudengine;


import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

/**
 * CloudQuery class allows the user to query for CloudObjects
 * satisfying a particular condition. The query is typically constructed
 * by making multiple where* method calls on a new CloudQuery object 
 * to set the appropriate condition. Then you can call a get or a find
 * method to actually run the query.
 * 
 */
public class CloudQuery {
	
	private static final String TAG = "CloudQuery";
	private String collection = null;
	private JSONObject queryObj = new JSONObject();
	
	/**
	 * Constructs a new CloudQuery 
	 * 
	 * @param objName name of the CloudObject type that the query is 
	 * being constructed for. This is the same name that was passed 
	 * while constructing that particular CloudObject  
	 * 
	 */
	public CloudQuery(String objName){
		this.collection = objName;
	}
	
	private class GetResult{
		public CloudException exception = null;
		public CloudObject obj = null;
	}
	
	private class FindResult{
		public CloudException exception = null;
		public List<CloudObject> objects = null;
	}

	private CloudObject gettask(String id) throws CloudAuthException,
													CloudException
	{
		String response, address = null;
		CloudObject obj;
		address = CloudEndPoints.retrieveCloudObject(collection, id);
		try{
			
			response = CloudEngineUtils.httpRequest(address, HttpMethod.GET);
			obj = new CloudObject(collection, new JSONObject(response));
		}
		
		catch (JSONException e)
		{
			throw new CloudException("Invalid CloudObject received");
		}
		
		return obj;
	}
	
	private class GetTask extends AsyncTask<String, Void, GetResult> {
		
		GetCallback callback = null;
		
		public void setCallback(GetCallback cbk){
			callback = cbk;
		}
		
		
		@Override
		protected GetResult doInBackground(String... ids) {
			GetResult result = new GetResult();
			String id = ids[0];
			try{
				result.obj = gettask(id);
			}
			catch (CloudAuthException e){
				 result.exception = e;
			}
			catch (CloudException e){
				result.exception = e;
			}
			
			return result;
				
		}
		
		protected void onPostExecute(GetResult result) {
			
	         if(callback != null){
	        	 callback.done(result.obj, result.exception);
	         }
	     }
		
	}
	
	private List<CloudObject> findtask() throws CloudAuthException, 
											CloudException
	{
		String response= null, address = null;
		JSONObject curr_obj;
		CloudObject curr_cloud_obj;
		
		List<CloudObject> objList = new ArrayList<CloudObject>();
		try{
			
			address = CloudEndPoints.queryCloudObject(collection);
			Log.i(TAG, "Running query on server: " + queryObj.toString());
			
			Log.i(TAG, "connecting to endpoint : " + address);
			address += "?query=" + URLEncoder.encode(queryObj.toString());
			response = CloudEngineUtils.httpRequest(address, HttpMethod.GET);
			Log.i(TAG, "query response received: "+ response);
			JSONObject query_response = new JSONObject(response);
			JSONArray arr = query_response.getJSONArray("result");
			for(int i = 0; i < arr.length(); i++)
			{
				curr_obj = arr.getJSONObject(i);
				curr_cloud_obj = new CloudObject(collection, curr_obj);
				objList.add(curr_cloud_obj);
				
			}
			
		}
		catch (JSONException e){
			throw new CloudException("Invalid CloudObject received");
		}
		return objList;
	}
	
	
	private class FindTask extends AsyncTask<Void, Void, FindResult> {
		
		private static final String TAG = "QueryTask";
		FindCallback callback = null;
		
		public void setCallback(FindCallback cbk){
			callback = cbk;
		}
		

		@Override
		protected FindResult doInBackground(Void... args) {
			
			FindResult result = new FindResult();
			try{
				result.objects = findtask();
			}
			catch (CloudAuthException e){
				 result.exception = e;
			}
			catch (CloudException e){
				result.exception = e;
			}	
			return result;
		}
		
		protected void onPostExecute(FindResult result) {
			
	         if(callback != null){
	        	 callback.done(result.objects, result.exception);
	         }
	     }
		
	}
	
	/**
	 * Run a query to retrieve a particular object whose objectId is already known.
	 * This method fetches the object in the current thread.
	 * 
	 * @param objId Unique Id of the object assigned when the object was saved
	 * 
	 * @param callback The callback function that will be called after the
	 * object has been retrieved. Exception information, if any, that occured 
	 * during the operation will be passed to the callback function.
	 * 
	 */
	public CloudObject get(String objId, GetCallback callback)
							throws CloudAuthException, CloudException
	{
		return gettask(objId);
	}
	
	/**
	 * Run a query to retrieve a particular object whose objectId is already known.
	 * This method fetches the object in a background thread.
	 * 
	 * @param objId Unique Id of the object assigned when the object was saved
	 * 
	 * @param callback The callback function that will be called after the
	 * object has been retrieved. Exception information, if any, that occured 
	 * during the operation will be passed to the callback function.
	 * 
	 */
	public void getInBackground(String objId, GetCallback callback)
	{
		GetTask task = new GetTask();
		task.setCallback(callback);
		task.execute(objId);
	}
	
	/**
	 * Run a query to retrieve one or more CloudObjects satisfying all the
	 * condition set in this query object. This method fetches the objects
	 * in a background thread.
	 * 
	 * 
	 * @param callback The callback function that will be called after the
	 * objects have been retrieved. Exception information, if any, that occurred 
	 * during the operation will be passed to the callback function.
	 * 
	 */
	public void findInBackground(FindCallback callback)
	{
		FindTask task = new FindTask();
		task.setCallback(callback);
		task.execute();
	}
	
	/**
	 * Run a query to retrieve one or more CloudObjects satisfying all the
	 * condition set in this query object. This method fetches the objects
	 * in the current thread.
	 * 
	 * 
	 * @param callback The callback function that will be called after the
	 * objects have been retrieved. Exception information, if any, that occurred 
	 * during the operation will be passed to the callback function.
	 * 
	 */
	public List<CloudObject> find() throws CloudAuthException, 
										CloudException
	{
		return findtask();
	}
	
	/**
	 * Sets a query condition. The condition checks if the value of a property of 
	 * a CloudObject matches one of the objects in the given Collection. The
	 * CloudObject will be selected if the match is found. 
	 * 
	 * @param key the name of the property of the CloudObject
	 * 
	 * @param values Collection containing a set of desirable values that
	 * a CloudObject should have. 
	 * 
	 * * @return the current query object. You can set more condition on the same
	 * 	object
	 * 
	 */
	public CloudQuery whereContainedIn(String key, Collection<? extends Object> values)
	{
		JSONObject condition;
		try {
			
			try{
				condition = (JSONObject) queryObj.get(key);
			}
			catch (JSONException e){
				condition = new JSONObject();
			}
			condition.put("$in", values);
			queryObj.put(key, condition);
			
		} catch (JSONException e) {
			e.printStackTrace();
			Log.e(TAG, "Error in adding query condition", e);
			throw new CloudException("Invalid query");	
		}
		return this;
	}
    
	/**
	 * Sets a query condition. The condition checks if the CloudObject
	 * has some value set for a particular property. The object is not selected
	 * if the property is set.
	 * 
	 * @param key the name of the property of the CloudObject
	 * 
	 * @return the current query object. You can set more condition on the same
	 * 	object
	 * 
	 */
	public CloudQuery whereDoesNotExist(String key) 
	{
		JSONObject condition;
		try {
			
			try{
				condition = (JSONObject) queryObj.get(key);
			}
			catch (JSONException e){
				condition = new JSONObject();
			}
			condition.put("$exists", false);
			queryObj.put(key, condition);
			
		} catch (JSONException e) {
			e.printStackTrace();
			Log.e(TAG, "Error in adding query condition", e);
			throw new CloudException("Invalid query");	
		}
		return this;
	}
	
	/**
	 * Sets a query condition. The condition checks whether the value of
	 * a given property of a CloudObject is equal to the provided value.
	 * The object is selected if the value matches.
	 * 
	 * @param key the name of the property of the CloudObject
	 * 
	 * @param value the desired value of the property
	 * 
	 * @return the current query object. You can set more condition on the same
	 * 	object
	 * 
	 */
	public CloudQuery whereEqualTo(String key, Object value) 
	{
		
		try {
			
			queryObj.put(key, value);
		
		} catch (JSONException e) {
		
			e.printStackTrace();
			Log.e(TAG, "Error in adding query condition", e);
			throw new CloudException("Invalid query");	
		}
		return this;
	}
	
	
	/**
	 * Sets a query condition. The condition checks if the CloudObject
	 * has some value set for a particular property. The object is selected
	 * if the property is set.
	 * 
	 * @param key the name of the property of the CloudObject
	 * 
	 * @return the current query object. You can set more condition on the same
	 * 	object
	 * 
	 */
	public CloudQuery whereExists(String key) 
	{
		JSONObject condition;
		try {
			
			try{
				condition = (JSONObject) queryObj.get(key);
			}
			catch (JSONException e){
				condition = new JSONObject();
			}
			condition.put("$exists", true);
			queryObj.put(key, condition);
			
		} catch (JSONException e) {
			e.printStackTrace();
			Log.e(TAG, "Error in adding query condition", e);
			throw new CloudException("Invalid query");	
		}
		return this;
	}
	
	/**
	 * Sets a query condition. The condition checks whether the value of
	 * a given property of a CloudObject is greater than the provided value.
	 * The object is selected if the object's property value is greater.
	 * 
	 * @param key the name of the property of the CloudObject
	 * 
	 * @param value the desired value of the property
	 * 
	 * @return the current query object. You can set more condition on the same
	 * 	object
	 * 
	 */
	public CloudQuery whereGreaterThan(String key, Object value) 
	{
		JSONObject condition;
		try {			
			try{
				condition = (JSONObject) queryObj.get(key);
			}
			catch (JSONException e){
				condition = new JSONObject();
			}
			condition.put("$gt", value);
			queryObj.put(key, condition);
			
		} catch (JSONException e) {
			e.printStackTrace();
			Log.e(TAG, "Error in adding query condition", e);
			throw new CloudException("Invalid query");	
		}
		return this;
	}
	
	/**
	 * Sets a query condition. The condition checks whether the value of
	 * a given property of a CloudObject is greater than or equal to 
	 * the provided value. The object is selected if the object's property 
	 * value is greater than or equal to the given value.
	 * 
	 * @param key the name of the property of the CloudObject
	 * 
	 * @param value the desired value of the property
	 * 
	 * @return the current query object. You can set more condition on the same
	 * 	object
	 * 
	 */
	public CloudQuery whereGreaterThanOrEqualTo(String key, Object value) 
	{
		JSONObject condition;
		try {
			try{
				condition = (JSONObject) queryObj.get(key);
			}
			catch (JSONException e){
				condition = new JSONObject();
			}			
			condition.put("$gte", value);
			queryObj.put(key, condition);
			
		} catch (JSONException e) {
			e.printStackTrace();
			Log.e(TAG, "Error in adding query condition", e);
			throw new CloudException("Invalid query");	
		}
		return this;
	}
	
	
	/**
	 * Sets a query condition. The condition checks whether the value of
	 * a given property of a CloudObject is less than the provided value. 
	 * The object is selected if the object's property value is less 
	 * than the given value.
	 * 
	 * @param key the name of the property of the CloudObject
	 * 
	 * @param value the desired value of the property
	 * 
	 * @return the current query object. You can set more condition on the same
	 * 	object
	 * 
	 */
	public CloudQuery whereLessThan(String key, Object value) 
	{
		JSONObject condition;
		try {			
			try{
				condition = (JSONObject) queryObj.get(key);
			}
			catch (JSONException e){
				condition = new JSONObject();
			}
			condition.put("$lt", value);
			queryObj.put(key, condition);
			
		} catch (JSONException e) {
			e.printStackTrace();
			Log.e(TAG, "Error in adding query condition", e);
			throw new CloudException("Invalid query");	
		}
		return this;
	}
	
	/**
	 * Sets a query condition. The condition checks whether the value of
	 * a given property of a CloudObject is less than or equal to 
	 * the provided value. The object is selected if the object's property 
	 * value is less than or equal to the given value.
	 * 
	 * @param key the name of the property of the CloudObject
	 * 
	 * @param value the desired value of the property
	 * 
	 * @return the current query object. You can set more condition on the same
	 * 	object
	 * 
	 */
	public CloudQuery whereLessThanOrEqualTo(String key, Object value) 
	{
		JSONObject condition;
		try {
			try{
				condition = (JSONObject) queryObj.get(key);
			}
			catch (JSONException e){
				condition = new JSONObject();
			}
			
			condition.put("$lte", value);
			queryObj.put(key, condition);
			
		} catch (JSONException e) {
			e.printStackTrace();
			Log.e(TAG, "Error in adding query condition", e);
			throw new CloudException("Invalid query");	
		}
		return this;
	}
	
	/**
	 * Sets a query condition. The condition checks whether the value of
	 * a given string property of a CloudObject matches the given regular 
	 * expression. The object is selected if there is a match.
	 * 
	 * @param key the name of the property of the CloudObject
	 * 
	 * @param regex the regular expression pattern to match with.
	 * 
	 * @return the current query object. You can set more condition on the same
	 * 	object
	 * 
	 */
	public CloudQuery whereMatches(String key, String regex) 
	{
		JSONObject condition;
		try {
			
			try{
				condition = (JSONObject) queryObj.get(key);
			}
			catch (JSONException e){
				condition = new JSONObject();
			}
			condition.put("$regex", regex);
			queryObj.put(key, condition);
			
		} catch (JSONException e) {
			e.printStackTrace();
			Log.e(TAG, "Error in adding query condition", e);
			throw new CloudException("Invalid query");	
		}
		return this;
	}
	
	/**
	 * Sets a query condition. The condition checks whether the value of
	 * a given string property of a CloudObject matches the given regular 
	 * expression. The object is selected if there is a match.
	 * 
	 * @param key the name of the property of the CloudObject
	 * 
	 * @param regex the regular expression pattern to match with.
	 * 
	 * @param modifiers the following PCRE modifiers can be provided
	 * 	i - Case insensitive regex matching
	 *  m - regex matching across multiple lines
	 * 
	 * @return the current query object. You can set more condition on the same
	 * 	object
	 * 
	 */
	public CloudQuery whereMatches(String key, String regex, String modifiers) 
	{
		JSONObject condition;
		try {
			
			try{
				condition = (JSONObject) queryObj.get(key);
			}
			catch (JSONException e){
				condition = new JSONObject();
			}
			condition.put("$regex", regex);
			condition.put("$options", modifiers);
			queryObj.put(key, condition);
			
		} catch (JSONException e) {
			e.printStackTrace();
			Log.e(TAG, "Error in adding query condition", e);
			throw new CloudException("Invalid query");	
		}
		
		return this;
	}
	
	/**
	 * Sets a query condition. The condition checks if the value of a property of 
	 * a CloudObject matches any of the objects in the given Collection. The
	 * CloudObject will not be selected if the match is found. 
	 * 
	 * @param key the name of the property of the CloudObject
	 * 
	 * @param values Collection containing a set of desirable values that
	 * a CloudObject should have. 
	 * 
	 * * @return the current query object. You can set more condition on the same
	 * 	object
	 * 
	 */
	public CloudQuery whereNotContainedIn(String key, Collection<? extends Object> values) 
	{
		JSONObject condition;
		try {
			
			try{
				condition = (JSONObject) queryObj.get(key);
			}
			catch (JSONException e){
				condition = new JSONObject();
			}
			condition.put("$nin", values);
			queryObj.put(key, condition);
			
		} catch (JSONException e) {
			e.printStackTrace();
			Log.e(TAG, "Error in adding query condition", e);
			throw new CloudException("Invalid query");	
		}
		return this;
	}
	
	/**
	 * Sets a query condition. The condition checks whether the value of
	 * a given property of a CloudObject is equal to the provided value.
	 * The object is selected if the value doesn't match.
	 * 
	 * @param key the name of the property of the CloudObject
	 * 
	 * @param value the desired value of the property
	 * 
	 * @return the current query object. You can set more condition on the same
	 * 	object
	 * 
	 */
	public CloudQuery whereNotEqualTo(String key, Object value) 
	{
		JSONObject condition;
		try {
			
			try{
				condition = (JSONObject) queryObj.get(key);
			}
			catch (JSONException e){
				condition = new JSONObject();
			}
			condition.put("$ne", value);
			queryObj.put(key, condition);
			
		} catch (JSONException e) {
			e.printStackTrace();
			Log.e(TAG, "Error in adding query condition", e);
			throw new CloudException("Invalid query");	
		}
		return this;
	}
	
}
