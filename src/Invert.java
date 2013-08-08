import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.TreeMap;

public class Invert 
{
	//use the common words stop list
	static boolean wordlist = true; 

	//use stemming 
	static boolean stem = false; 

	// an array to store common words from stop list
	static List<String> wordarray = new ArrayList<String>(); 

	static int docid = 0;
	static int wordposition = 0;

	//Create Tree Map to store frequencies and to make posting
	static TreeMap<String, Integer> frequencyData = new TreeMap<String, Integer>( );
	static TreeMap<String, ArrayList<PostingStruct>> postings = new TreeMap<String, ArrayList<PostingStruct>>();

	//a temporary arraylist to store words, frequencies and positions
	//before making the index
	static ArrayList<Struct> aList = new ArrayList<Struct>();

	public static void main(String[] args) throws IOException 
	{
		System.out.println("Building files ...");
		Scanner sc = null; 

		Stemmer stemming = null;

		if(stem)
		{
			stemming = new Stemmer(); 
		}

		//create file streams to write to temporary files
		FileWriter fstream = new FileWriter("titles.txt");
		BufferedWriter out = new BufferedWriter(fstream);
		FileWriter fstream2 = new FileWriter("docs.txt");
		BufferedWriter out2 = new BufferedWriter(fstream2);
		FileWriter fstream3 = new FileWriter("authors.txt");
		BufferedWriter out3 = new BufferedWriter(fstream3);
		
		// Try to open the file
		try {
			sc = new Scanner(new File("cacm.all"));
		} catch (FileNotFoundException e) {
			System.out.println("cacm.all not found");
			System.exit(1);
		}

		// if the word list is active insert list of words into wordarray
		if (wordlist) {
			parsecommonwords();
		}

		// parse file until finished
		while (sc.hasNextLine()) 
		{
			String line = sc.nextLine();

			//split line based on spaces
			String[] words = line.split("\\s+");

			if (words[0].equals(".I")) {
				docid++;
				wordposition = 0;
				if(docid == 1)
				{
					out2.write(docid + "		");
				}
				else
					out2.write("\n" + docid + "		");
			}
			else if(words[0].equals(".B") || words[0].equals(".W") || words[0].equals(".N")|| words[0].equals(".C")|| words[0].equals(".K"))
			{

			}
			else if(words[0].equals(".A"))
			{
				out3.write(docid +"		");
				while(sc.hasNextLine())
				{
					line = sc.nextLine();
					words = line.split("\\s+");
					if(words[0].equals(".T") || words[0].equals(".B") || words[0].equals(".W") || words[0].equals(".N")|| words[0].equals(".C")|| words[0].equals(".K"))
					{
						break;
					}

					String[] title = line.split("\\s+"); 
					for (int i = 0; i < title.length; i++) 
					{
						out2.write(title[i] + " ");
						title[i] = title[i].toLowerCase(); //remove all uppercases

						if(!(title[i].equals("+") || title[i].equals("-") || title[i].equals("*")))
						{
							title[i] = title[i].replaceAll("[^A-Za-z0-9/=]", ""); //remove non letter/number characters
						}

						if(!(title[i].equals("")))
						{
							wordposition++;
						}

						if (wordarray.contains(title[i]) || title[i].equals("")) 
						{ 

						} else 
						{
							//if stemming is active then stem the word
							if (stem) 
							{ 
								stemming.add(title[i].toCharArray(), title[i].toCharArray().length);
								stemming.stem();
								title[i] = stemming.toString();
							}
							//add word with proper info into array
							aList.add(new Struct(title[i],docid,wordposition));
						}
					}

					out3.write(line + " ");
				}
				out3.write("\n");
			}
			else if(words[0].equals(".T"))
			{
				out.write(docid +"		");
				while(sc.hasNextLine())
				{
					line = sc.nextLine();
					words = line.split("\\s+");
					if(words[0].equals(".T") || words[0].equals(".B") || words[0].equals(".W") || words[0].equals(".N")|| words[0].equals(".C")|| words[0].equals(".K"))
					{
						break;
					}
					else if(words[0].equals(".A"))
					{
						out3.write(docid +"		");
						while(sc.hasNextLine())
						{
							line = sc.nextLine();
							words = line.split("\\s+");
							if(words[0].equals(".T") || words[0].equals(".B") || words[0].equals(".W") || words[0].equals(".N")|| words[0].equals(".C")|| words[0].equals(".K"))
							{
								break;
							}

							String[] title = line.split("\\s+"); 
							for (int i = 0; i < title.length; i++) 
							{
								out2.write(title[i] + " ");
								title[i] = title[i].toLowerCase(); //remove all uppercases

								if(!(title[i].equals("+") || title[i].equals("-") || title[i].equals("*")))
								{
									title[i] = title[i].replaceAll("[^A-Za-z0-9/=]", ""); //remove non letter/number characters
								}

								if(!(title[i].equals("")))
								{
									wordposition++;
								}

								if (wordarray.contains(title[i]) || title[i].equals("")) 
								{ 

								} else 
								{
									//if stemming is active then stem the word
									if (stem) 
									{ 
										stemming.add(title[i].toCharArray(), title[i].toCharArray().length);
										stemming.stem();
										title[i] = stemming.toString();
									}
									//add word with proper info into array
									aList.add(new Struct(title[i],docid,wordposition));
								}
							}

							out3.write(line + " ");
						}
						out3.write("\n");
						break;
					}
					String[] title = line.split("\\s+"); 
					for (int i = 0; i < title.length; i++) 
					{
						out2.write(title[i] + " ");
						title[i] = title[i].toLowerCase(); //remove all uppercases

						if(!(title[i].equals("+") || title[i].equals("-") || title[i].equals("*")))
						{
							title[i] = title[i].replaceAll("[^A-Za-z0-9/=]", ""); //remove non letter/number characters
						}

						if(!(title[i].equals("")))
						{
							wordposition++;
						}

						if (wordarray.contains(title[i]) || title[i].equals("")) 
						{ 

						} else 
						{
							//if stemming is active then stem the word
							if (stem) 
							{ 
								stemming.add(title[i].toCharArray(), title[i].toCharArray().length);
								stemming.stem();
								title[i] = stemming.toString();
							}
							//add word with proper info into array
							aList.add(new Struct(title[i],docid,wordposition));
						}
					}

					out.write(line + " ");
				}
				out.write("\n");
			}

			//ignore .X fields for now so must skip over them
			else if(words[0].equals(".X"))
			{
				while(sc.hasNextLine())
				{
					line = sc.nextLine();
					words = line.split("\\s+");
					if(words[0].equals(".T") || words[0].equals(".A") || words[0].equals(".B") || words[0].equals(".W") || words[0].equals(".N")|| words[0].equals(".C")|| words[0].equals(".K"))
					{
						break;
					}
					else if(words[0].equals(".I"))
					{
						docid++;
						wordposition = 0;
						out2.write("\n" + docid + "		");
						break;
					}
				}
			}
			else
			{
				String[] title = line.split("\\s+");

				for (int i = 0; i < title.length; i++) 
				{
					out2.write(title[i] + " ");
					title[i] = title[i].toLowerCase(); //remove all uppercases

					if(!(title[i].equals("+") || title[i].equals("-") || title[i].equals("*")))
					{
						title[i] = title[i].replaceAll("[^A-Za-z0-9/=]", ""); //remove non letter/number characters
					}


					if(!(title[i].equals("")))
					{
						wordposition++;
					}

					if (wordarray.contains(title[i]) || title[i].equals("")) 
					{ 

					} else 
					{
						//if stemming is active then stem the word
						if (stem == true) 
						{ 
							stemming.add(title[i].toCharArray(), title[i].toCharArray().length);
							stemming.stem();
							title[i] = stemming.toString();
						}

						aList.add(new Struct(title[i],docid,wordposition));
					}
				}
			}
		}

		//close scanner and streams
		sc.close(); 
		out.close();
		out2.close();
		out3.close();

		//make index and postings after gathering information
		try 
		{
			print(aList);
			makeIndex(frequencyData);
			printAllCounts(frequencyData);
			makePostings(postings);
			printPostings(postings);

			//ADDED for A2

			calculateWeights(frequencyData, postings);

		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		System.out.println("Building done.");

	}

	private static void calculateWeights(TreeMap<String, Integer> frequencyData, TreeMap<String, ArrayList<PostingStruct>> postings) throws IOException, ClassNotFoundException
	{
		int df, tf;
		double wtf, idf;
//		WeightTable temp;

//		ArrayList<WeightTable> wt = new ArrayList<WeightTable>();

		TreeMap<String, Double> weights;
		
		System.out.println("Calculating Weights");
		
		//iterate through all terms to get weights
		for(Entry<String, ArrayList<PostingStruct>> entry : postings.entrySet()) 
		{
			String term = entry.getKey();
			ArrayList<PostingStruct> value = entry.getValue();

			df = getCount(term,frequencyData);

			for (PostingStruct s : value)
			{
				File f = new File("doc_weights/"+Integer.toString(s.getId()));

				if(f.exists())
				{
					tf = s.getFreq();
					wtf = 1 + Math.log10(tf);
					idf = Math.log10(docid / df);
//					temp = new WeightTable(term, wtf * idf);

					FileInputStream f_in = new FileInputStream(f);
					ObjectInputStream obj_in = new ObjectInputStream(f_in);

//					wt = (ArrayList<WeightTable>) obj_in.readObject();
					weights = (TreeMap<String, Double>) obj_in.readObject();
					weights.put(term, wtf * idf);
					
//					wt.add(temp);
					f_in.close();
					obj_in.close();

					FileOutputStream f_out = new FileOutputStream(f);
					ObjectOutputStream obj_out = new ObjectOutputStream(f_out);

					obj_out.writeObject(weights);

					f_out.close();
					obj_out.close();
				}
				else
				{
//					wt = new ArrayList<WeightTable>();
					weights = new TreeMap<String, Double>();
					tf = s.getFreq();
					wtf = 1 + Math.log10(tf);
					idf = Math.log10(docid / df);
//					temp = new WeightTable(term, wtf * idf);

//					wt.add(temp);
					weights.put(term, wtf * idf);
					
					FileOutputStream f_out = new FileOutputStream(f);
					ObjectOutputStream obj_out = new ObjectOutputStream(f_out);

					obj_out.writeObject(weights);

					f_out.close();
					obj_out.close();
				}
			}
		}

		//get all weights and normalize and save to file

		File directory = new File("doc_weights");
		File files[] = directory.listFiles();

		System.out.println("Normalizing Weights");

		double sum_weights_sq, nw;

		for (File f : files) 
		{
			sum_weights_sq = 0;
			
			if(!(f.isHidden()))
			{
				FileInputStream f_in = new FileInputStream(f);
				ObjectInputStream obj_in = new ObjectInputStream(f_in);

//				wt = (ArrayList<WeightTable>) obj_in.readObject();
				weights = (TreeMap<String, Double>) obj_in.readObject();
				
				f_in.close();
				obj_in.close();

				for(Entry<String, Double> entry : weights.entrySet())
				{
					sum_weights_sq += Math.pow(entry.getValue(),2);
				}
				
//				for(WeightTable tbl : w)
//				{
//					sum_weights_sq += Math.pow(tbl.getWeight(),2);
//				}

				nw = Math.sqrt(sum_weights_sq);

				//update to normalized weight
				for(Entry<String, Double> entry : weights.entrySet())
				{
					entry.setValue(entry.getValue() / nw);
					
				}
//				for(WeightTable tbl : wt)
//				{
//					tbl.setWeight(tbl.getWeight() / nw);
//				}
//				
				FileOutputStream f_out = new FileOutputStream(f);
				ObjectOutputStream obj_out = new ObjectOutputStream (f_out);

				obj_out.writeObject(weights);

				f_out.close();
				obj_out.close();
			}
		}

	}


	/**
	 * Make the index 
	 * @param frequencyData the location to put all the data
	 * @throws FileNotFoundException
	 */
	private static void makeIndex(TreeMap<String, Integer> frequencyData) throws FileNotFoundException
	{
		//use results file to create index
		Scanner sc=new Scanner(new File("result.txt"));
		String[] line;
		String word;
		int prev,next,count;

		line=sc.nextLine().split("\\s+");
		prev = Integer.valueOf(line[0]);
		frequencyData.put(line[1], 1);
		word = line[1];

		line=sc.nextLine().split("\\s+");
		next = Integer.valueOf(line[0]);

		if(!(next == prev && word.equals(line[1])))
		{
			count = getCount(line[1],frequencyData) + 1;
			frequencyData.put(line[1], count);
		}

		while(sc.hasNextLine())
		{			
			prev = next;
			line=sc.nextLine().split("\\s+");
			next = Integer.valueOf(line[0]);

			if(!(next == prev && word.equals(line[1])))
			{
				count = getCount(line[1],frequencyData) + 1;
				frequencyData.put(line[1], count);
			}
		}
	}

	/**
	 * Make the postings
	 * @param postings the location to put the postings information
	 * @throws FileNotFoundException
	 */
	private static void makePostings(TreeMap<String, ArrayList<PostingStruct>> postings) throws FileNotFoundException
	{
		Scanner sc=new Scanner(new File("result.txt"));
		String[] line;
		int id;
		String position;
		line=sc.nextLine().split("\\s+");
		id = Integer.valueOf(line[0]);
		position = line[2];
		PostingStruct tmp = new PostingStruct(id,position);
		ArrayList<PostingStruct> tmpArray = new ArrayList<PostingStruct>();
		tmpArray.add(tmp);

		postings.put(line[1], tmpArray);

		while(sc.hasNextLine())
		{			
			line=sc.nextLine().split("\\s+");
			id = Integer.valueOf(line[0]);
			position = line[2];
			tmpArray = getArrayList(line[1],postings);

			if(tmpArray == null)
			{
				tmp = new PostingStruct(id,position);
				tmpArray = new ArrayList<PostingStruct>();
				tmpArray.add(tmp);
				postings.put(line[1], tmpArray);
			}
			else
			{
				if(tmpArray.get(tmpArray.size()-1).getId() == id)
				{
					tmp = tmpArray.get(tmpArray.size()-1);
					tmp.incrementFreq();
					tmp.addposition(position);
				}
				else
				{
					tmp = new PostingStruct(id,position);
					tmpArray.add(tmp);
				}
			}
		}
	}

	/**
	 * Get the information about a specific word
	 * @param word the word we want information about
	 * @param postings the location of where the information is stored
	 * @return
	 */
	private static ArrayList<PostingStruct> getArrayList(String word, TreeMap<String, ArrayList<PostingStruct>> postings)
	{
		if(postings.containsKey(word))
		{
			return postings.get(word);
		}
		return null;
	}

	/**
	 * Print the postings to a file
	 * @param postings the information to output
	 * @throws IOException
	 */
	private static void printPostings(TreeMap<String, ArrayList<PostingStruct>> postings) throws IOException
	{
		FileOutputStream f_out = new FileOutputStream("postings.txt");
		ObjectOutputStream obj_out = new ObjectOutputStream (f_out);
		obj_out.writeObject(postings);

		obj_out.close();
		f_out.close();
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
	 * Output raw data to a file to be used later on
	 * @param s the array list to print to file
	 * @throws IOException
	 */
	public static void print(ArrayList<Struct> s) throws IOException
	{
		StructComparator comp = new StructComparator();
		Collections.sort(s,comp);

		FileWriter fstream = new FileWriter("result.txt");
		BufferedWriter out = new BufferedWriter(fstream);

		for (int i = 0; i < s.size(); i++)
		{
			out.write(s.get(i).print() + System.getProperty("line.separator"));
		}

		out.close();
	}

	/**
	 * Print out all the frequency data
	 * @param frequencyData the data to print
	 * @throws IOException
	 */
	public static void printAllCounts(TreeMap<String, Integer> frequencyData) throws IOException
	{
		FileOutputStream f_out = new FileOutputStream("dictionary.txt");
		ObjectOutputStream obj_out = new ObjectOutputStream (f_out);
		obj_out.writeObject(frequencyData);

		obj_out.close();
		f_out.close();

	}

	/**
	 * Get the frequency of a word if it exists
	 * @param word the word to find
	 * @param frequencyData the location of where to find the word
	 * @return
	 */
	public static int getCount(String word, TreeMap<String, Integer> frequencyData)
	{
		if (frequencyData.containsKey(word))
		{  
			return frequencyData.get(word); 
		}
		else
		{  
			return 0;
		}
	}

}
