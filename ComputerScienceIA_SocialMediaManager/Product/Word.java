public class Word {
	private String s;
	private int count;
	
	public Word(String s){
		this.s = s;
		count = 0;
	}
	
	public void increase(){
		count ++;
	}
	
	public int getCount(){
		return count;
	}
}


