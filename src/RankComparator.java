import java.util.Comparator;


public class RankComparator implements Comparator<Rank> 
{

	@Override
	public int compare(Rank o1, Rank o2) 
	{
		double sim1 = o1.getScore();
		double sim2 = o2.getScore();
		
		if (sim1 < sim2){
            return 1;
        } else if (sim1 == sim2) {
            return 0;
        } else {
            return -1;
        }
		
	}

}
