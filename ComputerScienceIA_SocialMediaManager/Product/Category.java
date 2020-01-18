import java.util.ArrayList;

public class Category {
	private ArrayList<Source> sources;
	
	public ArrayList<String> getCatSourceIds(){
		ArrayList<String> ids = new ArrayList<String>();
		for (Source s: sources){
			ids.add(s.getId());
		}
		return ids;
	}
}




