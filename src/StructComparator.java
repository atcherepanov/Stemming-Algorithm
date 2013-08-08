import java.util.Comparator;


public class StructComparator implements Comparator<Struct>
{
	@Override
	public int compare(Struct o1, Struct o2) {

		String word1 = o1.getWord();
		String word2 = o2.getWord();

		return word1.compareTo(word2);
	}
}
