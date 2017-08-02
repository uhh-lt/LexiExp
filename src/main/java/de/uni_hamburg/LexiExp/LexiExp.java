package de.uni_hamburg.LexiExp;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.*;
import org.apache.commons.cli.*;

/**
 * Created by Sarah Kohail on 12.07.2017.
 */
public class LexiExp {
	/*this program gets the DT expansions for a lexicon and produce a new expanded lexicon
	 arguments are:
	 * -s Seed Set -- Tap separated word \tab polarity list
	 * -e Number of expansions
	 * -db Database to connect to
	 * -o output file
	 */
	public static void main(String[] args)throws IOException, ParseException {
		CommandLineParser parser = new DefaultParser();
		Options options = new Options();
		// add options
		options.addOption("h", false, "help");
		options.addOption( Option.builder("s")
	            .longOpt("seed")
	            .required(false)
	            .desc("Seed set input file word\"\\TAB\"polarity pairs [w_1\\tp_1]\n[w_2\\tp_2]\n\n...\n[w_m\\tp_m]\n(DEFAULT: example file)")
	            .hasArg() 
	            .build());
		options.addOption( Option.builder("e")
	            .longOpt("expansion")
	            .required(false)
	            .desc("Number of expansions (DEFAULT: 10)")
	            .hasArg() 
	            .build());
		options.addOption( Option.builder("db")
	            .longOpt("database")
	            .required(false)
	            .desc("Database name (DEFAULT: reviewsTrigram)")
	            .hasArg() 
	            .build());
		options.addOption( Option.builder("o")
	            .longOpt("output")
	            .required(false)
	            .desc("Output file (DEFAULT: out_expanded_lexicon.txt)")
	            .hasArg() 
	            .build());
		//initialize reader for the seed set
		BufferedReader seedSetReader = null;
		//Hashset to store word#h[sentiment_occ#number_of_occurence]
		HashMap<String,HashMap<String,Integer>> wordSenOcc=new HashMap<String,HashMap<String,Integer>>();  
		try {
			CommandLine cmd = parser.parse(options, args);
			
			if (cmd.hasOption("h")) {
				HelpFormatter formater = new HelpFormatter();
				formater.printHelp("[options] text1 text2 ...", options);
				System.exit(0);
			}
			//Number of expansions passed through the arguments
			int numberOfEntries = cmd.hasOption("e") ? Integer.parseInt(cmd.getOptionValue("e")) : 10;
			String outputPath = cmd.hasOption("o") ? cmd.getOptionValue("o") : "out_expanded_lexicon.txt";
			String inputPath = cmd.hasOption("s") ? cmd.getOptionValue("s") : "lexicon";
			//database to connect to in order to get the expansions
			String database = cmd.hasOption("db") ? cmd.getOptionValue("db") : "reviewsTrigram";
			// results writer
			BufferedWriter expandedLexicon=new BufferedWriter(new FileWriter(outputPath));
			//read seed set from a file 
			seedSetReader = new BufferedReader( new InputStreamReader(new FileInputStream(inputPath), "UTF8"));
			//word-polarity Map
			Map<String, String> seed_words_map = new HashMap <String, String>();
			String line;
			//read seed words in a HashMap
			while ((line = seedSetReader.readLine()) != null) {
				String parts[] = line.split("\t");
				seed_words_map.put(parts[0].trim(), parts[1].trim());
			}
			seedSetReader.close();
			HashMap<String,Integer> listofPolarities = new HashMap<String, Integer>();
			//count polarities distribution in the seed lexicon
			for(String value: seed_words_map.values()){
				listofPolarities.put(value, Collections.frequency(new ArrayList<String>(seed_words_map.values()), value));
			}
			Iterator<Map.Entry<String, String>> it = seed_words_map.entrySet().iterator();
			//count polarity occurrence for expanded words
			while(it.hasNext()){
				Map.Entry<String, String> entry = it.next();
				String word = entry.getKey();
				String polarity = entry.getValue();
				//loop through the expansions of each word in the seed set
				for (String exp: getExpansions(word,numberOfEntries,database)){
					exp = exp.replaceAll("[\\.!?:'\"]", "");
					//check if the word is already in the seed set
					if(!seed_words_map.containsKey(exp) && !exp.isEmpty() && !isNumeric(exp)){
						//check if the word have been in the expansion of other word
						if(wordSenOcc.containsKey(exp.trim())){
							//check if the word occurred with a certain polarity before
							if(wordSenOcc.get(exp).containsKey(polarity)){
								int currentCount=wordSenOcc.get(exp).get(polarity);
								wordSenOcc.get(exp).put(polarity, currentCount+1);
							}
							else{
								wordSenOcc.get(exp).put(polarity, 1);
							}
						}else{
							HashMap<String,Integer> h= new HashMap<String,Integer>();
							h.put(polarity, 1);
							wordSenOcc.put(exp,h);
						}
					}
				}
			}
			//write header word .... polarities 
			expandedLexicon.write("word");
			for (String polarities: listofPolarities.keySet()){
				expandedLexicon.write("\t"+polarities);
			}
			expandedLexicon.write("\n");
			//write results Word#P#N#NE 
			for (String key: wordSenOcc.keySet()){
				double count = 0;
				expandedLexicon.write(key);
				// loop through the new polarity lexicon
				for (String polarities: wordSenOcc.get(key).keySet()){
					int numeberOfWords = wordSenOcc.get(key).get(polarities);
					count+=numeberOfWords;
				}
				for (String polarities: listofPolarities.keySet()){
					if(wordSenOcc.get(key).keySet().contains(polarities)){
						double div = wordSenOcc.get(key).get(polarities) / count;
						double totalPolarities = listofPolarities.get(polarities);
						DecimalFormat df = new DecimalFormat("0.00");   
						expandedLexicon.write("\t"+ df.format(div/totalPolarities));
					}
					else 
						expandedLexicon.write("\t"+0.0);
				}
				expandedLexicon.write("\n");
			}
			expandedLexicon.close();
		}catch (MalformedURLException e){
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static HashSet<String> getExpansions(String word, int numberOfEntries, String database) throws IOException {
		HashSet <String>h= new HashSet<String>();
		String []pair;
		URL oracle = new URL("http://ltmaggie.informatik.uni-hamburg.de/jobimviz/ws/api/"+database+"/jo/similar/"+URLEncoder.encode(word, "UTF-8")+"?numberOfEntries="+numberOfEntries+"&format=tsv");
		URLConnection yc = oracle.openConnection();
		BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream(), "UTF-8"));
		String inputLine="";
		while ( (inputLine = in.readLine()) != null){
			pair=inputLine.split("\t");
			if(pair.length>=2 && !pair[0].equals("# Term"))
				h.add(pair[0].split("#")[0]);
		}
		return h; 
	}
	public static boolean isNumeric(String str)
	{
	  return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
	}

}
