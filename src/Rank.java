import java.io.Serializable;


public class Rank implements Serializable
{
	private double score;
	private int doc;
	
	public Rank(int doc, double score)
	{
		this.doc = doc;
		this.score = score;
	}
	
	public int getDoc()
	{
		return doc;
	}
	
	public double getScore()
	{
		return score;
	}
}
