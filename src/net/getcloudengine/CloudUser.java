package net.getcloudengine;

import java.net.HttpCookie;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.webkit.CookieManager;

/**
 * Class to represent an app user. Allows new user account
 * creation, logging in and out of a user, query currently logged
 * in user, requesting resetting of a user's password.
 *  
 */
public class CloudUser {
	
	private String username = null, password = null, email = null;
	private String id = null, first_name = null, last_name = null;
	private final static String TAG = "CloudUser";
	private final static String SESSION_TOKEN = "sessionid";
	private String token = null;
	static CloudEngineUtils utils = CloudEngineUtils.getInstance();
	
	private void  signup_user()
	{
		String address = CloudEndPoints.createCloudUser();
		JSONObject body = null;
		String response = null;
		
		try{
			
			body = CloudUsertoJSON();
			if(body == null){
				throw new CloudException("Invalid CloudUser object");
			}
			response = utils.httpRequest(address, new HttpPost(), body);
			JSONObject obj = new JSONObject(response);
			this.token = obj.getString(SESSION_TOKEN);
			this.id = obj.getString("id");
		}
		
		catch (CloudAuthException e){
			Log.e(TAG, e.getMessage());
		}
		catch(Exception e){
			
			Log.e(TAG, e.getMessage());
			e.printStackTrace();
		}
	}
	
	private static CloudUser get_user(String sessionid){
		
		String address = CloudEndPoints.retrieveCurrentCloudUser();
		String response = null;
		CloudUser user = null;
		JSONObject result;
		
		try{
			CookieManager cookieManager = CookieManager.getInstance();
			HttpCookie cookie = new HttpCookie(SESSION_TOKEN, sessionid);
			cookie.setDomain("getcloudengine.net");
			cookie.setPath("/");
			cookie.setVersion(0);
			utils.addCookie("http://getcloudengine.net", cookie);
			
			response = utils.httpRequest(address, new HttpGet());
			JSONObject obj = new JSONObject(response);
			user = new CloudUser();
			
			try{
				
				result = obj.getJSONObject("result");
				user.setUsername(result.getString("username"));
			}
			catch(JSONException e){
				throw new CloudException("Invalid session id");
			}
			
			try{
				String email = result.getString("email");
				user.setEmail(email);
			}	
			catch( JSONException e){				
			}
			
			try{
				String first_name = result.getString("fist_name");
				user.setFirstname(first_name);
			}	
			catch( JSONException e){				
			}
			
			try{
				String last_name = result.getString("last_name");
				user.setLastname(last_name);
			}	
			catch( JSONException e){				
			}
			
			return user;
		}
		catch (CloudAuthException e){
			throw e;
		}
		catch(Exception e){
			throw new CloudException(e.getMessage());
		}
	}
	
	private static CloudUser login_user(String username, String password){
		
		String address = CloudEndPoints.loginCloudUser();
		JSONObject body = null;
		String response = null;
		CloudUser user = null;
		try{
			body = new JSONObject();
			body.put("username", username);
			body.put("password", password);
			response = utils.httpRequest(address, new HttpPost(), body);
			JSONObject obj = new JSONObject(response);
			user = new CloudUser(username, password);
			user.token = obj.getString(SESSION_TOKEN);
		}
		catch (CloudAuthException e){
			
			Log.e(TAG, e.getMessage());
			throw new CloudException(e.getMessage());
			
		}
		catch(Exception e){
			
			Log.e(TAG, e.getMessage());
			e.printStackTrace();
			throw new CloudException(e.getMessage());
		}
		return user;
	}
	
	
	private void logout_user(){
		
		String address = CloudEndPoints.logoutCloudUser();
		
		try{
			utils.httpRequest(address, new HttpGet());
			this.token = null;
		}
		catch (CloudAuthException e){
			
			Log.e(TAG, e.getMessage());
			throw new CloudException(e.getMessage());
			
		}
		catch(Exception e){
			
			Log.e(TAG, e.getMessage());
			e.printStackTrace();
			throw new CloudException(e.getMessage());
		}
	}
	
	private static void reset_password(String email){
		
		String address = CloudEndPoints.passwordReset();
		
		try{
			utils.httpRequest(address, new HttpGet());
		}
		catch (CloudAuthException e){
			
			Log.e(TAG, e.getMessage());
			throw new CloudException(e.getMessage());
			
		}
		catch(Exception e){
			
			Log.e(TAG, e.getMessage());
			e.printStackTrace();
			throw new CloudException(e.getMessage());
		}
	}
	
	
	
	private JSONObject CloudUsertoJSON(){
		
		JSONObject obj = new JSONObject();
		try {
			
			obj.put("username", this.username);
			obj.put("password", this.password);
			
			if(this.email !=  null)
				obj.put("email", this.email);
			
			if(this.first_name !=  null)
				obj.put("first_name", this.first_name);
			
			if(this.last_name !=  null)
				obj.put("last_name", this.last_name);
			
			return obj;
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	private class SignupTask extends AsyncTask<Void, Void, CloudException> {
		
		SignupCallback callback = null;
		
		public void setCallback(SignupCallback cbk){
			callback = cbk;
		}
		
		@Override
		protected CloudException doInBackground(Void... args) {
					
				try{
					signup_user();
				}
				catch(CloudException e)
				{
					return e;
				}
			return null;
		}
		
		protected void onPostExecute(CloudException e) {
			
	         if(callback != null)
	        	 callback.done(e);
	     }
		
	}
	
	private static class LoginResult {
		public CloudUser user = null;
		public CloudException exception = null;
	}
	
	private static class LoginTask extends AsyncTask<String, Void, LoginResult> {
		
		LoginCallback callback = null;		
		public void setCallback(LoginCallback cbk){
			callback = cbk;
		}
		
		@Override
		protected LoginResult doInBackground(String... credentials) {
					
				LoginResult result = new LoginResult();
				CloudUser user = null;
				try{
					String username = credentials[0], 
							password = credentials[1];
					user = login_user(username, password);
					result.user =  user;
				}
				catch(CloudException e)
				{
					result.exception = e;
				}
				return result;
		}
		
		protected void onPostExecute(LoginResult result) {
			
	         if(callback != null)
	        	 callback.done(result.user, result.exception);
	     }
		
	}
	
	private class LogoutTask extends AsyncTask<Void, Void, CloudException> {
		
		LogoutCallback callback = null;		
		
		public void setCallback(LogoutCallback cbk){
			callback = cbk;
		}
		
		@Override
		protected CloudException doInBackground(Void... args) {
					
				try{
					logout_user();
				}
				catch(CloudException e)
				{
					return e;
				}
				return null;
		}
		
		protected void onPostExecute(CloudException exception) {
			
	         if(callback != null)
	        	 callback.done(exception);
	     }
		
	}
	
	private static class GetUserTask extends AsyncTask<String, Void, LoginResult> {
		
		LoginCallback callback = null;
		public void setCallback(LoginCallback cbk){
			callback = cbk;
		}
		
		@Override
		protected LoginResult doInBackground(String... sessionids) {
			
			LoginResult result = new LoginResult();
			String sessionid = sessionids[0];
			CloudUser user = null;
			
			try{
				user = get_user(sessionid);
				result.user = user;
			}
			catch(CloudException e)
			{
				result.exception = e;
			}
			return result;
		}
		
		protected void onPostExecute(LoginResult res) {
			
	         if(callback != null)
	        	 callback.done(res.user, res.exception);
	     }
		
	}
	
	
	private static class PasswordResetTask extends AsyncTask<String, Void, CloudException> {
		
		PasswordResetCallback callback = null;
		public void setCallback(PasswordResetCallback cbk){
			callback = cbk;
		}
		
		@Override
		protected CloudException doInBackground(String... args) {
			
			LoginResult result = new LoginResult();
			String email = args[0];
			
			try{
				reset_password(email);
			}
			catch(CloudException e)
			{
				return e;
			}
			return null;
		}
		
		protected void onPostExecute(CloudException exception) {
			
	         if(callback != null)
	        	 callback.done(exception);
	     }
		
	}
	
	/**
	 * Constructs a new CloudUser object. 
	 *  
     */
	public CloudUser() {
		// TODO Auto-generated constructor stub
	}
	
	
	/**
	 * Constructs a new CloudUser object and sets
	 * the username and password as provided in the 
	 * arguments.  
	 * 
	 * @param username Username to be set for the object
     *            
     * @param password Password to be set for the object
     * 
     */
	public CloudUser(String username, String password){
		
		this.username = username;
		this.password = password;
		
	}
	

	/**
	 * Returns true if this user has been authenticated by the 
	 * server else false.
	 * 
     */
	public boolean isAuthenticated(){
		
		if(this.id!=null)
			return true;
		else
			return false;
	}
	
	
	/**
	 * Creates a new account for the user on the server
	 * in the current thread.  
	 * 
	 * @throws CloudException if unable to connect to 
	 * server or an account with the given username already
	 * exists. 
     * 
     */
	public void signUp(){
		
		if(this.username == null || this.password == null){
			throw new CloudException("Username and password are not set");
		}
		
		Context ctx = CloudEngine.getContext();
		if(!utils.isNetworkAvailable(ctx))
		{
			throw new CloudException("Cannot signup user. Network not available");
		}
		signup_user();
	}
	
	/**
	 * Creates a new account for the user on the server
	 * in a background thread. The newly created user is immediately
	 * logged in on the server.  
     * 
     */
	public void signUpInBackground(){
		
		if(this.username == null || this.password == null){
			throw new CloudException("Username and password are not set");
		}
		
		Context ctx = CloudEngine.getContext();
		if(!utils.isNetworkAvailable(ctx))
		{
			throw new CloudException("Cannot signup user. Network not available");
			
		}
		SignupTask signuptask = new SignupTask();
		signuptask.execute();
		
	}
	
	/**
	 * Creates a new account for the user on the server
	 * in a background thread
	 * 
     * @param callback Instance of SignupCallback. The done function
     * of the class will be called when the request is completed. 
     * 
     * 
     * @throws CloudException if username or password are not set or
     * if the server is not reachable or server didn't return expected
     * response
     * 
     */
	public void signUpInBackground(SignupCallback callback){
		
		if(this.username == null || this.password == null ||
				username == "" || password == ""){
			throw new CloudException("Username and password are not set");
		}
		
		Context ctx = CloudEngine.getContext();
		if(!utils.isNetworkAvailable(ctx))
		{
			throw new CloudException("Cannot signup user. Network unavailable");
			
		}
		SignupTask signuptask = new SignupTask();
		signuptask.setCallback(callback);
		signuptask.execute();
	}
	
	
	
	
	/**
	 * Retrive the user object which corresponds to the given
	 * sessionid String in the current thread.
	 * 
     * @param sessionid The sessionid string corresponding to the user
     * which was returned while signing up the user or last login of 
     * the user.
     * 
     * @throws CloudException if server is not reachable or server didn't
     * return expected response.
     * 
     * @return the CloudUser instance corresponding to the given 
     * session id
     * 
     */
	public static CloudUser getUser(String sessionid){
		
		CloudUser user = null;
		Context ctx = CloudEngine.getContext();
		if(!utils.isNetworkAvailable(ctx))
		{
			throw new CloudException("Cannot retrieve user. Network unavailable");
		}
		user = get_user(sessionid);
		
		return user;
	}
	
	/**
	 * Retrieve the user object which corresponds to the given
	 * sessionid String in a background thread.
	 * 
     * @param sessionid The sessionid string corresponding to the user
     * which was returned while signing up the user or last login of 
     * the user.
     * 
     * @param callback an instance of LoginCallback. The done function 
     * of the object will be called upon the completion of the request.
     * 
     * @throws CloudException if server is not reachable or server didn't
     * return expected response.
     * 
     * @return the CloudUser instance corresponding to the given 
     * session id
     * 
     */
	public static CloudUser getUserInBackground(String sessionid, 
										LoginCallback callback)
	{
		
		Context ctx = CloudEngine.getContext();
		if(!utils.isNetworkAvailable(ctx))
		{
			throw new CloudException("Cannot retrieve user. Network unavailable");
			
		}
		GetUserTask task = new GetUserTask();
		task.execute(sessionid);
		task.setCallback(callback);
	
		return null;
	}
	
	/**
	 * Request the server to reset the password of the user with
	 * the given email. The request will be sent in the current thread.
	 * The server will send an email to the given 
	 * email id with the link to reset the password. 
	 * 
     * @param email email id of the user account whose password 
     * needs to be reset.
     * 
     * @throws CloudException if the email is not valid or
     * server is not reachable.
     * 
     */
	public static void requestPasswordReset(String email){
		
		if(email == null || email == "")
		{
			throw new CloudException("Invalid email id");
		}
		
		Context ctx = CloudEngine.getContext();
		if(!utils.isNetworkAvailable(ctx))
		{
			throw new CloudException("Cannot signup user. Network unavailable");
			
		}
		reset_password(email);
	}
	
	/**
	 * Request the server to reset the password of the user with
	 * the given email. The request is sent in a background thread.
	 * The server will send an email to the given 
	 * email id with the link to reset the password. 
	 * 
     * @param email email id of the user account whose password 
     * needs to be reset.
     * 
     * @throws CloudException if the email is not valid or
     * server is not reachable.
     * 
     */
	public static void requestPasswordResetInBackground(String email){
		
		
		if(email == null || email == "")
		{
			throw new CloudException("Invalid email id");
		}
		
		Context ctx = CloudEngine.getContext();
		if(!utils.isNetworkAvailable(ctx))
		{
			throw new CloudException("Cannot signup user. Network unavailable");
			
		}
		PasswordResetTask passwordresettask = new PasswordResetTask();
		passwordresettask.execute(email);
	}
	
	
	/**
	 * Request the server to reset the password of the user with
	 * the given email. The request is sent in a background thread.
	 * The server will send an email to the given 
	 * email id with the link to reset the password. 
	 * 
     * @param email email id of the user account whose password 
     * needs to be reset.
     * 
     * @param callback instance of PasswordResetCallback. The done 
     * function of the object will be called upon the completion of
     * the request
     * 
     * @throws CloudException if the email is not valid or
     * server is not reachable.
     * 
     */
	public static void requestPasswordResetInBackground(String email, 
				PasswordResetCallback callback){
		
		
		if(email == null || email == "")
		{
			throw new CloudException("Invalid email id");
		}
		
		Context ctx = CloudEngine.getContext();
		if(!utils.isNetworkAvailable(ctx))
		{
			throw new CloudException("Cannot send request. Network unavailable");
			
		}
		PasswordResetTask passwordresettask = new PasswordResetTask();
		passwordresettask.setCallback(callback);
		passwordresettask.execute(email);
		
	}
	
	
	/**
	 * Set the username of this user object 
	 * 
     * @param username The username string
     * 
     * 
     */
	public void setUsername(String username){
		this.username = username;
	}
	
	
	/**
	 * Set the email of this user object 
	 * 
     * @param email The email id string of the user
     * 
     * 
     */
	public void setEmail(String email){
		this.email = email;
	}
	
	/**
	 * Set the first name of this user object 
	 * 
     * @param first_name first name of the user
     * 
     * 
     */
	public void setFirstname(String first_name){
		
		this.first_name = first_name;
	}
	
	/**
	 * Set the last name of this user object 
	 * 
     * @param last_name last name of the user
     * 
     * 
     */
	public void setLastname(String last_name){
		
		this.last_name = last_name;
	}
	
	
	/**
	 * Set the password of this user object 
	 * 
     * @param password the password string
     * 
     * 
     */
	public void setPassword(String password){
		this.password = password;
	}
	
	/**
	 * Returns the username of this user object 
	 * 
     * @return username The username string
     * 
     * 
     */
	public String getUsername(){
		
		return this.username;
	}
	
	/**
	 * Returns the email id of this user object 
	 * 
     * @return email The email id string
     * 
     * 
     */
	public String getEmail(){
		
		return this.email;
	}
	
	/**
	 * Returns the session token of this user object
	 * available from the last login 
	 * 
     * @return session id string
     * 
     * 
     */
	public String getSessionToken(){
		
		return this.token;
	}
	
	/**
	 * Login into the server using the given username and password
	 * as credentials. The login request will be sent in the current
	 * thread.
	 * 
	 * @param username the username string to be used to login
	 * 
	 * @param password the password string to be used to login
	 * 
     * @return CloudUser object corresponding to the logged in
     * user. 
     * 
     * 
     */
	public static CloudUser login(String username, String password){
		
		if(username == null || password == null){
			throw new CloudException("Username and password are not set");
		}
		
		Context ctx = CloudEngine.getContext();
		if(!utils.isNetworkAvailable(ctx))
		{
			throw new CloudException("Cannot signup user. Network unavailable");
			
		}
		CloudUser user = null;
		user = login_user(username, password);
		
		return user;
	}
	
	/**
	 * Login into the server using the given username and password
	 * as credentials. The login request will be sent in a 
	 * background thread.
	 * 
	 * @param username the username string to be used to login
	 * 
	 * @param password the password string to be used to login
	 * 
     * 
     */
	public static void loginInBackground(String username, String password){
		
		if(username == null || password == null){
			throw new CloudException("Username and password are not set");
		}
		
		Context ctx = CloudEngine.getContext();
		if(!utils.isNetworkAvailable(ctx))
		{
			throw new CloudException("Cannot signup user. Network unavailable");
			
		}
		LoginTask logintask = new LoginTask();
		logintask.execute(username, password);
	}
	
	/**
	 * Login into the server using the given username and password
	 * as credentials. The login request will be sent in a background
	 * thread.
	 * 
	 * @param username the username string to be used to login
	 * 
	 * @param password the password string to be used to login
	 * 
	 * @param callback LoginCallback instance. The done method of the
	 * object will be called upon the completion of the request.
	 * 
     * @return CloudUser object corresponding to the logged in
     * user. 
     * 
     * 
     */
	public static void loginInBackground(String username, String password, 
							LoginCallback callback){
		
		if(username == null || password == null){
			throw new CloudException("Username and password are not set");
		}
		
		Context ctx = CloudEngine.getContext();
		if(!utils.isNetworkAvailable(ctx))
		{
			throw new CloudException("Cannot signup user. Network unavailable");
			
		}
		
		LoginTask logintask = new LoginTask();
		logintask.setCallback(callback);
		logintask.execute(username, password);
		
	}
	
	/**
	 * Logout a currently logged in user. The logout request is sent
	 * in the current thread.
     * 
     */
	public void logout(){
		
		
		Context ctx = CloudEngine.getContext();
		if(!utils.isNetworkAvailable(ctx))
		{
			throw new CloudException("Cannot signup user. Network unavailable");
			
		}
		logout_user();
		
		
	}
	
	
	/**
	 * Logout a currently logged in user. The logout request will 
	 * be sent in a background thread.
	 * 
     */
	public void logoutInBackground(){
		
		
		Context ctx = CloudEngine.getContext();
		if(!utils.isNetworkAvailable(ctx))
		{
			throw new CloudException("Cannot signup user. Network unavailable");
			
		}
		LogoutTask logouttask = new LogoutTask();
		logouttask.execute();
		
	}
	
	/**
	 * Logout a currently logged in user. The logout request 
	 * will be sent in a background thread.
	 * 
	 * @param callback instance of LogoutCallback. The done 
	 * method of the class will be called upon the completion 
	 * of the request.
     * 
     */
	public void logoutInBackground(LogoutCallback callback){
		
		
		Context ctx = CloudEngine.getContext();
		if(!utils.isNetworkAvailable(ctx))
		{
			throw new CloudException("Cannot signup user. Network unavailable");
			
		}
		LogoutTask logouttask = new LogoutTask();
		logouttask.setCallback(callback);
		logouttask.execute();
		
	}

}
