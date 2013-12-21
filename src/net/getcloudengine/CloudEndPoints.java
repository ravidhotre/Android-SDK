package net.getcloudengine;

public class CloudEndPoints {
	private static final String protocol = "http";
	private static final String host = "getcloudengine.net";
	public static final String socketServer = "http://getcloudengine.net/default";
	private static final String serviceClasses = "classes";
	private static final String serviceFiles = "files";
	private static final String serviceUsers = "users";
	private static final String apiVersion = "v1";
	private static final String LOGIN = "login";
	private static final String LOGOUT = "logout";
	private static final String CURRENT_USER = "me";
	private static final String PASSWORD_RESET = "password_reset";
	
	
	private static String buildURI(String service, String... resources){
		
		String address = String.format("%s://%s/api/%s/%s/", protocol, 
				host, apiVersion, service);
		for(String resource: resources)
		{
			address += resource + "/";
		}
		return address;
	}
	
	public static String getRoot(){
		
		String root = String.format("%s://%s/", protocol, host);
		return root;
	}
	
	public static String createCloudUser()
	{
		return buildURI(serviceUsers);
	}
	
	public static String retreiveCloudUser(String id)
	{
		return buildURI(serviceUsers, id);
	}
	
	public static String updateCloudUser(String id)
	{
		return buildURI(serviceUsers, id);
	}
	
	
	public static String deleteCloudUser(String id){
		
		return buildURI(serviceUsers, id);
	
	}
	
	public static String retrieveCurrentCloudUser(){
		
		return buildURI(serviceUsers, CURRENT_USER);
	
	}
	
	public static String loginCloudUser()
	{
		return buildURI(serviceUsers, LOGIN);
	}
	
	public static String logoutCloudUser()
	{
		return buildURI(serviceUsers, LOGOUT);
	}
	
	public static String passwordReset()
	{
		return buildURI(serviceUsers, PASSWORD_RESET);
	}
	
	
	public static String saveCloudObject(String name)
	{
		return buildURI(serviceClasses, name); 
		
	}
	
	
	public static String retrieveCloudObject(String name, String id)
	{
		
		return buildURI(serviceClasses, name, id); 
	}
	
	public static String updateCloudObject(String name, String id)
	{
		return buildURI(serviceClasses, name, id);
	}
	
	public static String deleteCloudObject(String name, String id)
	{
		return buildURI(serviceClasses, name, id);
	}

	public static String queryCloudObject(String name)
	{
		return buildURI(serviceClasses, name);
	}

	public static String saveCloudFile(String name){
		return buildURI(serviceFiles, name);
	}
}
