import java.util.ArrayList;
import java.util.TreeSet;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.google.gson.Gson;

//import facebook4j.Facebook;
//import facebook4j.FacebookFactory;

//package com.javapapers.java;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.conf.ConfigurationBuilder;

/*
import oauth.signpost.OAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

*/
//import com.restfb.BinaryAttachment;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.exception.FacebookException;
import com.restfb.types.FacebookType;
import com.restfb.types.GraphResponse;


public class User {
	private String name;

    private String fbAccessToken = "";
    private String tConsumerKey;
    private String tConsumerSecret;
    private String tAccessToken;
    private String tAccessSecret;
    private String tUsername;
    private String tPassword;
    private ArrayList<String> prefPlatforms;
    
    private ArrayList<String> topics;
    private int autoPost;			// IN MINUTES
    private int autoRefresh;		// IN MINUTES

    TreeSet<Article> myRecentNews = new TreeSet<Article>();
    private ArrayList<String> mySourceIds = new ArrayList<String>();

    private String apiKey = "3ef9a8998d3c41fc91be676868b5fffd";
    private final String USER_AGENT = "Mozilla/5.0";
    
    
    public User(String name){
    	this.name = name;
    	autoPost = 60; 	// default auto-post every hour
    	autoRefresh = 10;	// refreshes every ten minutes
    	topics = new ArrayList<>();
    }
    public User(){
    	autoPost = 60; 	// default auto-post every hour
    	autoRefresh = 10;	// refreshes every ten minutes
    	topics = new ArrayList<>();
    }
    public void setName(String name){
    	this.name = name;
    }
    
    public String getName(){
    	return name;
    }
    public int getAutoRefr(){
    	return autoRefresh;
    }
    public int getAutoPost(){
    	return autoPost;
    }
    
    public void setTopics(ArrayList<String> topics){
    	this.topics = topics;
    }
    public ArrayList<String> getTopics(){

    	return topics;
    }
    
    public void setPrefPlatforms(ArrayList<String> platforms){
    	prefPlatforms = platforms;
    }
    public void setRefr(int minutes){
    	autoRefresh = minutes;
    }
    public void setAutoPost(int minutes){
    	autoPost = minutes;
    }
    
    public void setSources(){
    	// given list of categories (topics), must send get request and then set ArrayList<String> mySourceIds to URLS
    	mySourceIds.clear();
    	for (String c: topics){
    		
    		String u = "https://newsapi.org/v1/sources?category=" + c;
    		try {
				String reply = sendGet(u).substring(3);
				Category thisCat = new Gson().fromJson(reply, Category.class);
				ArrayList<String> sourceIds = thisCat.getCatSourceIds();
				mySourceIds.addAll(sourceIds);
						
			} catch (Exception e) {
				e.printStackTrace();
			}
    	}
    }

	public void setFb(String token){
		fbAccessToken = token;
    } 
    public void setTwitter(String username, String password, String consKey, String consSecret, String accT, String accS){
    	tUsername = username;
    	tPassword = password;
    	tConsumerKey = consKey;
    	tConsumerSecret = consSecret;
    	tAccessToken = accT;
    	tAccessSecret = accS;
    }
    
    public boolean credValid(String s){
    	if (s.length() > 1 && !s.equals("")){
    		return true;
    	}
    	return false;
    }
    public boolean tCredIsValid(){
    	if (credValid(tUsername) && credValid(tPassword) && credValid(tConsumerKey) && credValid(tConsumerSecret) && credValid(tAccessToken) && credValid(tAccessSecret)){
    		return true;
    	}
    	return false;
    	
    }
    public boolean fbCredIsValid(){
    	if (credValid(fbAccessToken)){
    		return false;
    	}
    	return true;
    }
    
    public boolean postToFb(String message){
    	try{
	    	@SuppressWarnings("deprecation")
			FacebookClient client = new DefaultFacebookClient(fbAccessToken);
	        client.publish("me/feed", GraphResponse.class, Parameter.with("message", message));
	    	return true;
    	}catch(FacebookException e){
    		return false;
    	}
    }
    public boolean postToTwitter(String tweet){
		ConfigurationBuilder cb = new ConfigurationBuilder();
	    cb.setDebugEnabled(true)
	        .setOAuthConsumerKey(tConsumerKey)
	        .setOAuthConsumerSecret(tConsumerSecret)
	        .setOAuthAccessToken(tAccessToken)
	        .setOAuthAccessTokenSecret(tAccessSecret);
	
	    TwitterFactory tf = new TwitterFactory(cb.build());
	    Twitter t = tf.getInstance();
	
	    try {
	    	t.updateStatus(tweet);
	    	return true;
	    } catch (TwitterException te) {
	    	te.printStackTrace();
	        return false;
	    }		
    }
    
    public void refreshNews(){
    	TreeSet<Article> articles = new TreeSet<Article>();
    	for (String s: mySourceIds){
    		String response = "";
    		try {
    			String u =  "https://newsapi.org/v1/articles?source=" + s + "&sortBy=top&apiKey=" + apiKey;
    			 response = sendGet(u);
			} catch (Exception e) {
				try{
					String x =  "https://newsapi.org/v1/articles?source=" + s + "&sortBy=latest&apiKey=" + apiKey;
					response = sendGet(x);
				} catch (Exception er){
					er.printStackTrace();
				}
			}
    		String reply = response.substring(3);
			Source thisS = new Gson().fromJson(reply, Source.class);
			thisS.fixDates();
			articles.addAll(thisS.getArticles());
    	}
    	for (Article a: articles){
    		a.generateWords();
    		
    	}
    	myRecentNews = articles;
    }
    public TreeSet<Article> getRecentNews(){
    	return myRecentNews;
    }
    
    public String sendGet(String url) throws Exception{
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // optional default is GET
        con.setRequestMethod("GET");

        // add request header
        con.setRequestProperty("User-Agent", USER_AGENT);

        int responseCode = con.getResponseCode();

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        String response = new String();

        while ((inputLine = in.readLine()) != null){
            response += inputLine;
        }
        in.close();

        return responseCode + response;
    }
    
    public ArrayList<String> getSourceIds(){
    	return mySourceIds;
    }
    
    public ArrayList<String> getPlatforms(){
    	return prefPlatforms;
    }   
    public String toJson(){
    	Gson gson = new Gson();
    	String s = gson.toJson(this);
    	return s;
    }
}
