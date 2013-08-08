import java.io.Serializable;

public class Struct implements Serializable
	{
		private int id;
		private int position;
		private String word;
		
		public Struct(String w, int i)
		{
			id=i;
			word=w;
		}
		public Struct(String string, int docid, int wordposition) {
			word=string;
			id=docid;
			position = wordposition;
		}
		public int getId()
		{
			return id;
		}
		public int getPosition()
		{
			return position;
		}
		public String getWord()
		{
			return word;
		}
		public String print()
		{
			return id + "			" + word + "			" + position;
		}
	}