import java.awt.Desktop;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.TreeMap;


public class Test 
{
	//use stemming 
	static boolean stem = true;

	//use the common words stop list
	static boolean wordlist = true;

	//the Tree Map of the data
	private static TreeMap<String, Integer> frequencyData;
	private static TreeMap<String, ArrayList<PostingStruct>> postings;

	// an array to store common words from stop list
	static List<String> wordarray = new ArrayList<String>(); 

	public static void main(String[] args) throws IOException, ClassNotFoundException
	{
		String word="",query;
		Stemmer stemming = null;

		long avgTime = 0, wordTime, finTime;
		int count=0;

		if (wordlist == true) {
			parsecommonwords();
		}

		//read in data from files
		System.out.println("Loading Database...");
		readFreqData();
		readPost();
		System.out.println("Loading Done");

		System.out.println("Enter term: ");
		Scanner scan = new Scanner(System.in);
		word = scan.nextLine();
		wordTime = System.currentTimeMillis();

		while(!(word.equals("ZZEND")))
		{
			count++;
			query = word.toLowerCase();

			if(!(query.equals("+") || query.equals("-") || query.equals("*")))
			{
				query = query.replaceAll("[^A-Za-z0-9/=]", ""); //remove non letter/number characters
			}

			if (wordarray.contains(query) || query.equals("")) 
			{ 
				System.out.println("Common words not tested");
			}
			else
			{	
				if(stem)
				{
					stemming = new Stemmer(); 
					stemming.add(word.toCharArray(), query.toCharArray().length);
					stemming.stem();
					query = stemming.toString();
				}
				ArrayList<PostingStruct> tmpArray = postings.get(query);

				if(!(tmpArray==null))
				{
					//output results into html file for viewing in browser
					File f = new File(word + ".html");
					BufferedWriter bw = new BufferedWriter(new FileWriter(f));
					bw.write("<!DOCTYPE html>\n");
					bw.write("<html>\n");
					bw.write("<body>\n");

					bw.write("<p><b>Word:</b> " + word + "  \n  </br>   <b>	DocFreq:</b> " + frequencyData.get(query) + "</p>\n");


					for(int i=0;i<tmpArray.size();i++)
					{
						bw.write("<p><b>Doc id:</b> "+ tmpArray.get(i).getId() + "</br>\n	<b>Term Freq:</b> " + tmpArray.get(i).getFreq() + "\n</br> <b>Position:</b> " + tmpArray.get(i).getPosition() + "		\n</br>	<b>Title:</b> " + getTitle(tmpArray.get(i).getId())+ "\n</br><b>Abstract:</b> " + getAbstract(tmpArray.get(i).getId(),tmpArray.get(i).getPosition()) + "</p>\n\n\n");

					}
					bw.write("</body>\n");
					bw.write("</html>");
					bw.close();
					
					
					finTime = System.currentTimeMillis();
					System.out.println("Time to find word: " + (finTime - wordTime) + " ms");
					//automatically open results in default browser
					Desktop.getDesktop().browse(f.toURI());
		
					avgTime += finTime-wordTime;
				}
				else
				{
					System.out.println("No word found");
				}
			}

			System.out.println("Enter term: ");
			word = scan.nextLine();
			wordTime = System.currentTimeMillis();

		}

		System.out.println("Closing Program");
		System.out.println("Average Time for Queries: " + (avgTime/count) + " ms");
	}

	/**
	 * Get the context of the word in the document to display to user
	 * @param docid the document id to check
	 * @param position the position of the word
	 * @return the relevant data with the word in the context
	 * @throws IOException
	 */
	public static String getAbstract(int docid, String position) throws IOException
	{
		Scanner sc=new Scanner(new File("docs.txt"));
		int pos;
		String[] line;
		int length;
		String result="";

		if(position.contains(","))
		{
			pos = Integer.valueOf(position.substring(0,position.indexOf(",")));
		}
		else
		{
			pos = Integer.valueOf(position);
		}

		while(sc.hasNextLine())
		{
			line = sc.nextLine().split("\\s+");

			if(Integer.valueOf(line[0]) == docid)
			{
				String[] title = getTitle(docid).split("\\s+");
				length = title.length;

				if(pos<=length)
				{
					for(int i=1;i<=title.length;i++)
					{
						if(i==pos)
						{
							result += "<b style=\"color:blue\">"+line[i].toUpperCase() + "</b> ";
						}
						else
							result += line[i] + " ";
					}
				}

				else if(line.length<=10)
				{
					for(int i=1;i<=line.length;i++)
					{
						if(i==pos)
						{
							result += "<b style=\"color:blue\">"+line[i].toUpperCase() + "</b> ";
						}
						else
							result += line[i] + " ";
					}
				}

				else if(line.length - pos > 0)
				{
					if(pos - 4 > length)
					{
						if(pos + 5 <=line.length)
						{
							for(int i=pos-4;i<=pos+5;i++)
							{
								if(i==pos)
								{
									result += "<b style=\"color:blue\">"+line[i].toUpperCase() + "</b> ";
								}
								else
									result += line[i] + " ";
							}
						}

					}
					else if(pos - 3 > length)
					{
						if(pos + 6 <=line.length)
						{
							for(int i=pos-3;i<=pos+6;i++)
							{
								if(i==pos)
								{
									result += "<b style=\"color:blue\">"+line[i].toUpperCase() + "</b> ";
								}
								else
									result += line[i] + " ";
							}
						}
					}

					else if(pos - 2 > length)
					{
						if(pos + 7 <=line.length)
						{
							for(int i=pos-2;i<=pos+7;i++)
							{
								if(i==pos)
								{
									result += "<b style=\"color:blue\">"+line[i].toUpperCase() + "</b> ";
								}
								else
									result += line[i] + " ";
							}
						}
					}
					else if(pos - 1 > length)
					{
						if(pos + 8 <=line.length)
						{
							for(int i=pos-1;i<=pos+8;i++)
							{
								if(i==pos)
								{
									result += "<b style=\"color:blue\">"+line[i].toUpperCase() + "</b> ";
								}
								else
									result += line[i] + " ";
							}
						}
					}
					else if(pos > length)
					{
						if(pos + 9 <=line.length)
						{
							for(int i=pos;i<=pos+9;i++)
							{
								if(i==pos)
								{
									result += "<b style=\"color:blue\">"+line[i].toUpperCase() + "</b> ";
								}
								else
									result += line[i] + " ";
							}
						}
					}
					else if(pos - 5 > length)
					{
						if(pos + 4 <=line.length)
						{
							for(int i=pos-5;i<=pos+4;i++)
							{
								if(i==pos)
								{
									result += "<b style=\"color:blue\">"+line[i].toUpperCase() + "</b> ";
								}
								else
									result += line[i] + " ";
							}
						}
					}
					else if(pos - 6 > length)
					{
						if(pos + 3 <=line.length)
						{
							for(int i=pos-6;i<=pos+3;i++)
							{
								if(i==pos)
								{
									result += "<b style=\"color:blue\">"+line[i].toUpperCase() + "</b> ";
								}
								else
									result += line[i] + " ";
							}
						}
					}
					else if(pos - 7 > length)
					{
						if(pos + 2 <=line.length)
						{
							for(int i=pos-7;i<=pos+2;i++)
							{
								if(i==pos)
								{
									result += "<b style=\"color:blue\">"+line[i].toUpperCase() + "</b> ";
								}
								else
									result += line[i] + " ";
							}
						}
					}
					else if(pos - 8 > length)
					{
						if(pos + 1 <=line.length)
						{
							for(int i=pos-8;i<=pos+1;i++)
							{
								if(i==pos)
								{
									result += "<b style=\"color:blue\">"+line[i].toUpperCase() + "</b> ";
								}
								else
									result += line[i] + " ";
							}
						}
					}
					else if(pos - 9 > length)
					{
						if(pos <=line.length)
						{
							for(int i=pos-9;i<=pos;i++)
							{
								if(i==pos)
								{
									result += "<b style=\"color:blue\">"+line[i].toUpperCase() + "</b> ";
								}
								else
									result += line[i] + " ";
							}
						}
					}

				}
				return result;
			}
		}
		return "";
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
}
