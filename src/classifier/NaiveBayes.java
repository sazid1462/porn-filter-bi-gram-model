package classifier;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.StringTokenizer;

public class NaiveBayes {
	
	// Define the name of the classes with final string for clarity and efficiency
	public static final String CLASS_PORN = "adult";
	public static final String CLASS_OTHER = "other";
	
	public static final int IN_CLASS = 0;
	public static final int IN_TOTAL = 1;
	public static final int IN_BADWORDS = 2;
	
	// Store the instance of the classifier class
	private static NaiveBayes instance;
	
	private static boolean knowledgeAvailable = false;
	
	// HashMaps for counting the words and classes
	private HashMap<String, Double> knowledges[] = new HashMap[3];
	
	private String classes[] = new String[2];
	
	private static double trainingDocCount = 1.0;
	
	private void gatherKnowledge(){
		// Load the previous knowledges from the storage if any
		File file1 =  new File("./storage/knowledge-class");
		File file2 =  new File("./storage/knowledge-word");
		File file3 =  new File("./storage/BadWords");
		
		if (file1.exists() && file2.exists()) {
			knowledgeAvailable = true;
			FileReader fr1, fr2, frB, frE;
			try {
				// Gather word occurrences in specific classes
				fr1 = new FileReader(file1);
				BufferedReader br1 = new BufferedReader(fr1);
				
				String str = br1.readLine();
				while(str != null){
					StringTokenizer tokenizer = new StringTokenizer(str, ",");
					String key = tokenizer.nextToken();
					double val = Double.parseDouble(tokenizer.nextToken());
					
					knowledges[IN_CLASS].put(key, val);
					
					str = br1.readLine();
				}
				br1.close();
				
				// Gather word occurrences in all the classes
				fr2 = new FileReader(file2);
				BufferedReader br2 = new BufferedReader(fr2);
				
				str = br2.readLine();
				while(str != null){
					StringTokenizer tokenizer = new StringTokenizer(str, ",");
					String key = tokenizer.nextToken();
					double val = Double.parseDouble(tokenizer.nextToken());
					
					knowledges[IN_TOTAL].put(key, val);
					
					str = br2.readLine();
				}
				br2.close();
				
				// Gather the Bengali bad words
				frB = new FileReader(file3);
				BufferedReader brB = new BufferedReader(frB);
				
				str = brB.readLine();
				while(str != null && str.isEmpty()==false){
					StringTokenizer tokenizer = new StringTokenizer(str, ",");
					String key = tokenizer.nextToken();
					double val = Double.parseDouble(tokenizer.nextToken());
					
					knowledges[IN_BADWORDS].put(key, val);
					
					str = brB.readLine();
				}
				brB.close();
				
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	// Make the default constructor private to restrict creating more instances
	private NaiveBayes() {
		classes[0] = CLASS_PORN;
		classes[1] = CLASS_OTHER;
		
		knowledges[0] = new HashMap<String, Double>();
		knowledges[1] = new HashMap<String, Double>();
		knowledges[2] = new HashMap<String, Double>();
		
		gatherKnowledge();
	}
	
	public HashMap<String, Double> getWordOccurences() {
		return knowledges[IN_TOTAL];
	}
	
	public HashMap<String, Double> getWordOccurencesInClass() {
		return knowledges[IN_CLASS];
	}
	
	public boolean isKnowledgeAvailable() {
		return knowledgeAvailable;
	}
	
	/** 
	 * Public method that returns the instance of the classifier. If the instance
	 * is null then it will create one.
	 */
	public static NaiveBayes getInstance(){
		if (instance == null){
			instance = new NaiveBayes();
		}
		return instance;
	}
	
	void addWord(String word, int where, double count){
		if (knowledges[where].containsKey(word)){
			double val = knowledges[where].get(word);
			knowledges[where].replace(word, val+count);
		} else {
			knowledges[where].put(word, count);
		}
	}
	
	private HashMap<String, Double> createBagOfWords(String doc){
		HashMap<String, Double> bagOfWords = new HashMap<>();
		
		// Create the Bag of Words
		StringTokenizer tokenizer = new StringTokenizer(doc, " ,.;:!1234567890১২৩৪৫৬৭৮৯০৷।\\\n\t-+*@#$%^&()[]{}~/<>\'\"`’?…“”");
		String word1=null, word2=null;
		
		while(tokenizer.hasMoreTokens()){
			word1 = tokenizer.nextToken();
			
			word1 = word1.toLowerCase();
			
			if (word1.length() < 3) {
				word2 = null;
				continue;
			}
			
			if (bagOfWords.containsKey(word1)){
				double val = bagOfWords.get(word1);
				bagOfWords.replace(word1, val+1);
			} else {
				bagOfWords.put(word1, 1.0);
			}
			
			if (word2 != null) {
				if (bagOfWords.containsKey(word2+" "+word1)){
					double val = bagOfWords.get(word2+" "+word1);
					bagOfWords.replace(word2+" "+word1, val+1);
				} else {
					bagOfWords.put(word2+" "+word1, 1.0);
				}
			}
			
			if (tokenizer.hasMoreTokens()){
				word2 = tokenizer.nextToken();
				
				word2 = word2.toLowerCase();
				
				if (word2.length() < 3) {
					continue;
				}
				
				if (bagOfWords.containsKey(word2)){
					double val = bagOfWords.get(word2);
					bagOfWords.replace(word2, val+1);
				} else {
					bagOfWords.put(word2, 1.0);
				}
				
				if (bagOfWords.containsKey(word1+" "+word2)){
					double val = bagOfWords.get(word1+" "+word2);
					bagOfWords.replace(word1+" "+word2, val+1);
				} else {
					bagOfWords.put(word1+" "+word2, 1.0);
				}
			}
		}
		return bagOfWords;
	}
	
	/**
	 * To train the classifier in supervised way
	 * @param doc is a training document
	 * @param className is the name of the class the training document is classified
	 */
	public void train(String doc, String className) {
//		System.out.println(doc);
		trainingDocCount = trainingDocCount + 1;
		
		HashMap<String, Double> bagOfWords = createBagOfWords(doc);
		
		for (String word : bagOfWords.keySet()){
			addWord(word, IN_TOTAL, bagOfWords.get(word));
			addWord(word+"|"+className, IN_CLASS, bagOfWords.get(word));
		}
	}
	
	private double getProbability(String className, String word) {
		double nominator, denominator;
		if (knowledges[IN_CLASS].containsKey(word+"|"+className)){
			nominator = knowledges[IN_CLASS].get(word+"|"+className) + 1;
		} else {
			nominator = 1;
		}
		if (knowledges[IN_TOTAL].containsKey(word)){
			denominator = knowledges[IN_TOTAL].get(word) + classes.length;
		} else {
			denominator = classes.length;
		}
		return nominator / denominator;
	}
	
	private double getBadWordsScaling(String className, String word) {
		if (className.equals(CLASS_PORN)) {
			if (knowledges[IN_BADWORDS].containsKey(word)) {
//				System.err.println(word+" "+knowledges[IN_BADWORDS].get(word));
//				return knowledges[IN_BADWORDS].get(word);
				return 0;
			} else {
				return 0;
			}
		} else {
			return 0;
		}
	}
	
	/**
	 * Evaluate the belonging class of any document
	 * @param doc is the document to be evaluated
	 * @return the name of the belonging class
	 */
	public String evaluate(String doc) {
		double bestProb = -999e100;
		String bestEvaluation = "Undefined"; 
		
		for (String className : classes) {
			
//			double prob = Math.log(numDocInClass.get(className)/trainingDocCount);
			double prob = 0;
			
//			System.out.println(prob);
			
			HashMap<String, Double> bagOfWords = createBagOfWords(doc);
			
			for (String word : bagOfWords.keySet()){
//				System.out.println(word+" "+Math.log(getProbability(className, word)));
				prob += bagOfWords.get(word) * (Math.log(getProbability(className, word)) + getBadWordsScaling(className, word));
//				prob += bagOfWords.get(word) * Math.log(getProbability(className, word));
//				prob += bagOfWords.get(word) * getBadWordsScaling(className, word);
			}
			
//			System.out.println(prob);
			if (prob > bestProb) {
				bestProb = prob;
				bestEvaluation = className;
			}
		}
		return bestEvaluation;
	}
}
