import java.io.IOException;
import java.util.Date;
import java.util.TreeSet;

public class Article implements Comparable<Article>{
    private String author;
    private String title;
    private String description;
    private String url;
    private String urlToImage;
    private Date publishedAt;
    private boolean beenPosted = false;
    private TreeSet<String> allWords = new TreeSet<String>();
    private TreeSet<String> keyWords = new TreeSet<String>();
    
    
    @Override
    public int compareTo(Article o) {
      return o.getDateTime().compareTo(publishedAt);
    }


	public Date getDateTime() {
		// TODO Auto-generated method stub
		return publishedAt;
	}
	
	public void setDateNow(){
		publishedAt = new Date();
	}
    
	public String getUrl(){
		return url;
	}
	
	public String getImageUrl(){
		return urlToImage;
	}
	
	public boolean hasBeenPosted(){
		return beenPosted;
	}
	public void setPosted(){
		beenPosted = true;
	}
	public void generateWords(){
		for (String s: title.split(" ")){
			allWords.add(s);
		}
		try {
			for (String s: description.split(" ")){
				allWords.add(s);
			}
			for (String s: allWords){
				if (Character.isUpperCase(s.charAt(0)) && s.length() > 3){    // scrapes off common words
					keyWords.add(s);
				}
			}
		} catch (NullPointerException e) {
			description = "";
		} catch (StringIndexOutOfBoundsException s){
			//pass
		}
	}
	
	public TreeSet<String> getKeyWords(){
		return keyWords;
	}
	
	@Override
	public String toString(){
		return title + " | " + description + " | " + url + " | " + publishedAt;
	}
}
