import java.util.ArrayList;


public class Source {
    private String id;
    private ArrayList<Article> articles;
    
    public String getId(){
    	return id;
    }
    
    public ArrayList<Article> getArticles(){
		return articles;
	}
    
    public void fixDates(){
    	for (Article a: articles){
    		if (a.getDateTime() == null){
    			a.setDateNow();
    		}
    	}
    }
   
}
