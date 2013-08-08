import java.io.Serializable;


public class PostingStruct implements Serializable
{
	private int docid,freq;
	private String position;
	
	public PostingStruct(int id, String wordposition)
	{
		docid=id;
		freq = 1;
		position = wordposition;
	}
	public int getId()
	{
		return docid;
	}
	
	public int getFreq()
	{
		return freq;
	}
	public void incrementFreq()
	{
		freq += 1;
	}
	public void addposition(String wordposition)
	{
		position = position + "," + wordposition;
	}
	public String print()
	{
		return docid + "			" + freq + "			" + position;
	}
	public String getPosition() 
	{
		return position;
	}
}
