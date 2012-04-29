package com.welcomehome.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.util.Timer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Handler;
import android.util.Log;


public class Net {
	
	private static Socket socket;
	private static PrintWriter out;
	private static BufferedReader in;
	public static String username = "WINSTON";
	public static final String HOST = "hnet.no.de";
	public static final String URL = "https://dev.tendrilinc.com";
	public static final int PORT_TCP = 1337;
	public static final int PORT_HTTP = 80;
	
	public static ExecutorService executor = Executors.newCachedThreadPool();
	public static Timer timer = new Timer(); 
	
		


	/*---------------general init stuff---------*/
	
	//TODO 
	public static void initNetworking(){
		
		initHttp();
		//initTCPNetworking(listener);
	}
	
	
	/*--------------Core HTTP Stuff --------------*/
	
	private static HttpClient httpClient;
	public static final String USER_AGENT = "HarmonifyAndroid/1.0";
	
	public static void initHttp(){
		if (httpClient == null) {
			synchronized (Net.class) {
				if (httpClient == null) {
					String userAgentString = "HarmonifyAndroidClient/1.0";
					HttpParams  params = new BasicHttpParams();
					HttpConnectionParams.setConnectionTimeout(params, 10000);
					HttpConnectionParams.setSoTimeout(params, 10000);
					params.setParameter(CoreProtocolPNames.USER_AGENT, userAgentString);
					httpClient = new DefaultHttpClient(params);
				}
			}
		}
		
	}
	

	
	
	public static void makeAsyncCoreCall(final JSONObject requestObj, final Handler handler, final NetworkCallbackRunnable<JSONObject> callback){
		Net.executor.execute(
				new Runnable(){
					public void run(){
						
						try{
							final JSONObject responseObject = makeBlockingCoreCall(requestObj);
							handler.post(new Runnable(){
							
							
								public void run(){
									callback.onSuccess(responseObject);
								}
							});
						}
						catch( final Exception e){
							Log.i("Net", "" + e);
							handler.post( new Runnable(){
								public void run(){
									callback.onFailure(e);
								}
							});
						}
					}
				}
				
		);
	}
	
	
	
	
	public static synchronized JSONObject makeBlockingCoreCall(JSONObject requestObj) throws ClientProtocolException, IOException, IllegalStateException, JSONException, ConnectTimeoutException{
		HttpGet post = new HttpGet(URL);
		
		//checks the httpclient is not null and initializes it if it is
		initHttp();
	
			if(requestObj!=null){
				String content = requestObj.toString();
				//post.setEntity(new StringEntity(content));
				
				post.addHeader("Content-Type", "application/json");
				post.addHeader("host", HOST);
				
				Log.i("Net", "About to send HTTP Core Request");

				Log.i("Net", "Request Body: "+content.toString());
				HttpResponse res = httpClient.execute(post);
				Log.i("Net", "HTTP Core Request Sent");
				// Construct JSON object from response.
				JSONObject responseObject = JSON.getJSONObject(res.getEntity().getContent());
				Log.i("Net", "HTTP Core Received Response: "+responseObject.toString());
					return responseObject;
				
			}
			
		
		
		throw new IllegalArgumentException("Cannot make network call with null request object.");
		
	}
	
	
	/*****************************************************************
	******************************************************************
	******************		CHAT METHODS		**********************
	******************************************************************
	*****************************************************************/
	
	/**
	 * Adds a chat/comment message to a specified meetup.
	 * 
	 * @param user username
	 * @param token token
	 * @param mid meetup id
	 * @param handler
	 * @param callback
	 */
	public static void getTendril(Handler handler, NetworkCallbackRunnable<JSONObject> callback) {
		JSONObject requestObj = new JSONObject();
		try {
						
			requestObj.put("limitToLatest", "20");
			requestObj.put("source", "ACTUAL");
			requestObj.put("fromDate", "2012-01-01T00:00:00-07:00");
			requestObj.put("toDate", "2012-01-31T00:00:00-07:00");
			requestObj.put("username", "andrew.wood@tendril.com");
			requestObj.put("password", "password");
			makeAsyncCoreCall(requestObj, handler,  callback);
		}
		catch (JSONException e) {
			e.printStackTrace();
			callback.onFailure(e);
			Log.e("JSONException Error Detected in create sendChatMessage", "Error");
			
		}
	}
	
	
	/**
	 * makes a synchronous request to the Places API, please use with an AysncTask to make it asynchronous
	 * @param url the request route to be appended to https://maps.googleapis.com/maps/api/place/
	 * @param apiKey the Places API key to use
	 * @return a JSON object with the result from the Places API call
	 * @throws IOException
	 * @throws JSONException
	 */
	private static JSONObject makePlacesAPIRequest(String url, String apiKey) throws IOException, JSONException{
		URL googlePlaces = new URL("https://maps.googleapis.com/maps/api/place/"+url+"&key=" +apiKey );
        URLConnection urlConnection = googlePlaces.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

        String line;
        StringBuffer stringBuffer = new StringBuffer();
        while ((line = in.readLine()) != null) {
        	stringBuffer.append(line);
        }
        
        return new JSONObject(stringBuffer.toString());     
	}
	/*
	public static JSONObject autocompleteWithPlaces(String searchTerm, Double lat, Double lng, Context context ) throws IOException, JSONException{
		return makePlacesAPIRequest("autocomplete/json?input="+ URLEncoder.encode(searchTerm, "UTF-8")
        		+"&language=en&radius=2000&sensor=true&location="+lat+","+lng, context.getString(R.string.places_api_key_tim));
	}
	
	public static JSONObject findDetailsWithPlaces(String referenceID, Context context) throws IOException, JSONException{
		return makePlacesAPIRequest("details/json?reference="+referenceID+"&sensor=true", context.getString(R.string.places_api_key_tim));
	}
	
	*/
}