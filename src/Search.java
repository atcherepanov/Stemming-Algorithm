import java.awt.Desktop;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.Map.Entry;


public class Search 
{
	//use stemming 
	static boolean stem = false;

	//use the common words stop list
	static boolean wordlist = true;

	//threshold for top documents to display
	private static double threshold = 50;

	//the Tree Map of the data
	private static TreeMap<String, Integer> frequencyData;
	private static TreeMap<String, ArrayList<PostingStruct>> postings;
	private static TreeMap<Integer, String> titles;
	private static TreeMap<Integer, String> authors;

	// an array to store common words from stop list
	static List<String> wordarray = new ArrayList<String>(); 

	public static void main(String[] args) throws IOException, ClassNotFoundException
	{
		String word="";
		Stemmer stemming = null;

		long avgTime = 0, wordTime, finTime;
		int count=0;

		if (wordlist) {
			parsecommonwords();
		}

		//read in data from files
		System.out.println("Loading Database...");
		readFreqData();
		readPost();
		readTitles();
		readAuthors();
		System.out.println("Loading Done");

	}

	public void load() throws IOException, ClassNotFoundException
	{
		System.out.println("Loading Database...");
		readFreqData();
		readPost();
		readTitles();
		readAuthors();
		System.out.println("Loading Done");
	}

	/**
	 * Get the context of the word in the document to display to user
	 * @param docid the document id to check
	 * @param position the position of the word
	 * @return the relevant data with the word in the context
	 * @throws IOException
	 * @throws ClassNotFoundException 
	 */
	public String query(String word) throws IOException, ClassNotFoundException
	{

		Stemmer stemming = null;

		int count=0;

		if (wordlist) {
			parsecommonwords();
		}
		String value = "";

		word = word.toLowerCase();
		String[] query = word.split("\\s+");
		count++;

		if(wordlist)
		{
			for(int i=0;i<query.length;i++)
			{
				if (wordarray.contains(query[i]) || query[i].equals("")) 
				{
					query[i] = "";
				}
			}
		}


		for(int i=0;i<query.length;i++)
		{
			if(!(query[i].equals("+") || query[i].equals("-") || query[i].equals("*")))
			{
				query[i] = query[i].replaceAll("[^A-Za-z0-9/=]", ""); //remove non letter/number characters
			}
			if(stem)
			{
				stemming = new Stemmer(); 
				stemming.add(query[i].toCharArray(), query[i].toCharArray().length);
				stemming.stem();
				query[i] = stemming.toString();
			}
		}


		Arrays.sort(query);


		word = "";
		for(String w : query)
		{
			word += w + " ";
		}
		word = word.trim().replaceAll(" +", " ");

		if(!(word.equals("")))
		{
			query = word.split("\\s+");

			int tf;
			double w, sum_weights_sq = 0;

			int same = 0;

			TreeMap<String, Double> weights = new TreeMap<String, Double>();

			//term frequency calculation
			for(int i=0;i<query.length;i++)
			{
				for(int j=same+1;j<query.length;j++)
				{
					if(query[i].equals(query[j]))
					{
						same = j;
					}
				}

				tf = same+1-i;
				w = 1 + Math.log10(tf);

				weights.put(query[i], w);

				i = same;
				same++;
			}

			for(Entry<String, Double> entry : weights.entrySet())
			{
				sum_weights_sq += Math.pow(entry.getValue(),2);

			}

			double nw = Math.sqrt(sum_weights_sq);

			//update to normalized weight
			for(Entry<String, Double> entry : weights.entrySet())
			{
				entry.setValue(entry.getValue() / nw);

			}

			ArrayList<Integer> docs = new ArrayList<Integer>();

			//get docs which have these words
			for(Entry<String, Double> entry : weights.entrySet())
			{
				String term = entry.getKey();

				ArrayList<PostingStruct> ps = postings.get(term);

				if(!(ps == null))
				{ 
					for(PostingStruct pstruct : ps)
					{
						if(!(docs.contains(pstruct.getId())))
							docs.add(pstruct.getId());
					}
				}
			}

			//holds normalized weights of documents
			TreeMap<String, Double> docWeight;

			//holds similarity values of documents
			ArrayList<Rank> sim = new ArrayList<Rank>();

			double similarity;

			//similarity function
			for(int d : docs)
			{
				similarity = 0;

				//read in TreeMap of document
				FileInputStream f_in = new FileInputStream("doc_weights/" + Integer.toString(d));
				ObjectInputStream obj_in = new ObjectInputStream(f_in);

				docWeight = (TreeMap<String, Double>) obj_in.readObject();
				f_in.close();
				obj_in.close();

				//match query terms to those in document
				for(Entry<String, Double> entry : weights.entrySet())
				{
					String term = entry.getKey();

					if(docWeight.get(term) != null)
					{
						similarity += docWeight.get(term) * entry.getValue();
					}
				}

				sim.add(new Rank(d, similarity));
			}

			RankComparator comp = new RankComparator();
			Collections.sort(sim,comp);

			int counter = 1;
			for(Rank r : sim)
			{
				value = value + (counter + ".	Doc: " + r.getDoc() + "		Score: " + (Math.round( r.getScore() * 100000.0 ) / 100000.0) + "		Title: " + titles.get(r.getDoc()) + "\n					 Authors: " + authors.get(r.getDoc()));
				value = value + "\n" + "\n";

				counter++;
				if(counter > threshold)
				{
					break;
				}
			}

		}
		else
		{
			value = "Common words not indexed.";
		}

		return value;

	}


	/**
	 * Get the title of the document
	 * @param docid the document id
	 * @return the title of the document
	 * @throws IOException
	 */
	public static String getTitle(int docid) throws IOException
	{
		Scanner sc=new Scanner(new File("titles.txt"));
		String[] line;
		int id;

		while(sc.hasNextLine())
		{
			line=sc.nextLine().split("\\s+");
			id = Integer.valueOf(line[0]);

			if(id == docid)
			{
				String title ="";
				for(int i=1;i<line.length;i++)
				{
					title += line[i] + " ";
				}
				return title;
			}
		}

		return "";


	}

	/**
	 * Gather the common words into the wordarray
	 */
	private static void parsecommonwords() 
	{
		Scanner sc = null;
		try {
			sc = new Scanner(new File("common_words"));
		} catch (FileNotFoundException e) {
			System.out.println("common_words not found");
			System.exit(1);
		}

		while (sc.hasNextLine()) {
			String line = sc.nextLine();
			line = line.toLowerCase();
			wordarray.add(line);
		}
		sc.close();

	}

	/**
	 * Read in the frequency data from file
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static void readFreqData() throws IOException, ClassNotFoundException
	{
		FileInputStream f_in = new FileInputStream("dictionary.txt");
		ObjectInputStream obj_in = new ObjectInputStream (f_in);
		frequencyData =  (TreeMap<String, Integer>) obj_in.readObject();

		obj_in.close();
		f_in.close();
	}

	/**
	 * Read in the postings data from the file
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static void readPost() throws IOException, ClassNotFoundException
	{
		FileInputStream f_in = new FileInputStream("postings.txt");
		ObjectInputStream obj_in = new ObjectInputStream (f_in);
		postings = (TreeMap<String, ArrayList<PostingStruct>>) obj_in.readObject();

		obj_in.close();
		f_in.close();
	}

	/**
	 * read in titles
	 * @throws FileNotFoundException
	 */
	public static void readTitles() throws FileNotFoundException 
	{
		Scanner scan = new Scanner(new File("titles.txt"));
		String line;
		int doc;

		titles = new TreeMap<Integer, String>();

		while(scan.hasNextLine())
		{
			line = "";
			Scanner pair = new Scanner(scan.nextLine());

			doc = pair.nextInt();
			while(pair.hasNext())
			{
				line += " " + pair.next();
			}
			titles.put(doc, line);
		}
	}

	/**
	 * read in authors
	 * @throws FileNotFoundException
	 */
	public static void readAuthors() throws FileNotFoundException 
	{
		Scanner scan = new Scanner(new File("authors.txt"));
		String line;
		int doc;

		authors = new TreeMap<Integer, String>();

		while(scan.hasNextLine())
		{
			line = "";
			Scanner pair = new Scanner(scan.nextLine());

			doc = pair.nextInt();
			while(pair.hasNext())
			{
				line += " " + pair.next();
			}
			authors.put(doc, line);
		}
	}
}
