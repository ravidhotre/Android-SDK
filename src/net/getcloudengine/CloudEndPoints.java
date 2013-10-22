package net.getcloudengine;

public class CloudEndPoints {
	private static final String protocol = "http";
	private static final String host = "getcloudengine.net";
	public static final String socketServer = "http://getcloudengine.net/default";
	private static final String serviceClasses = "classes";
	private static final String serviceFiles = "files";
	private static final String apiVersion = "v1";
	
	
	private static String buildURI(String service, String... resources){
		
		String address = String.format("%s://%s/api/%s/%s/", protocol, 
				host, apiVersion, service);
		for(String resource: resources)
		{
			address += resource + "/";
		}
		return address;
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
