package trainer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import classifier.NaiveBayes;

public class Trainer {
	static NaiveBayes classifier;
	
	public static void startTraining() {
		classifier = NaiveBayes.getInstance();
		
		// Train using bengali choti files
		for (int j = 1; j <= 4; j++) {
			for (int i = 1; i <= 100; i++) {
				File file = new File("./training-docs/pornFiles" + j + "/Porn" + i + ".txt");
	
				if (!file.exists())
					break;
	
				FileReader fr;
				try {
					fr = new FileReader(file);
					BufferedReader br = new BufferedReader(fr);
	
					String doc = "";
					String str = br.readLine();
					while (str != null) {
						str = str.replaceAll("[\\-\\+\\^\"\\{\\}\\(\\[\\]\\)\\'\\/\\:\\n\\`]", "	");
						str = str.replaceAll("\\p{Punct}", " ");
						str = str.replaceAll("\\r|\\n|\\t", " ");
						str = str.replaceAll("\\p{Cntrl}", " ");
						doc = doc + str + ' ';
						str = br.readLine();
					}
	
					classifier.train(doc, NaiveBayes.CLASS_PORN);
					br.close();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		// Train using bengali good files
		for (int j = 1; j <= 8; j++) {
			for (int i = 1; i <= 1000; i++) {
				File file = new File("./training-docs/goodFile" + j + "/Good" + i + ".txt");
	
				if (!file.exists())
					break;
	
				FileReader fr;
				try {
					fr = new FileReader(file);
					BufferedReader br = new BufferedReader(fr);
	
					String doc = "";
					String str = br.readLine();
					while (str != null) {
						str = str.replaceAll("[\\-\\+\\^\"\\{\\}\\(\\[\\]\\)\\'\\/\\:\\n\\`]", " ");
						str = str.replaceAll("\\p{Punct}", " ");
						str = str.replaceAll("\\r|\\n|\\t", " ");
						str = str.replaceAll("\\p{Cntrl}", " ");
						doc = doc + str + ' ';
						str = br.readLine();
					}
	
					classifier.train(doc, NaiveBayes.CLASS_OTHER);
					br.close();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}		
		
		// Train using english porn files
		for (int j = 1; j <= 4; j++) {
			for (int i = 1; ; i++) {
				File file = new File("./training-docs/EnglishPorn" + j + "/File" + i + ".txt");
	
				if (!file.exists())
					break;
	
				FileReader fr;
				try {
					fr = new FileReader(file);
					BufferedReader br = new BufferedReader(fr);
	
					String doc = "";
					String str = br.readLine();
					while (str != null) {
						str = str.replaceAll("[\\-\\+\\^\"\\{\\}\\(\\[\\]\\)\\'\\/\\:\\n\\`]", "	");
						str = str.replaceAll("\\p{Punct}", " ");
						str = str.replaceAll("\\r|\\n|\\t", " ");
						str = str.replaceAll("\\p{Cntrl}", " ");
						doc = doc + str + ' ';
						str = br.readLine();
					}
	
					classifier.train(doc, NaiveBayes.CLASS_PORN);
					br.close();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		try {
			PrintWriter pWriter1 = new PrintWriter("./storage/knowledge-class");
			classifier.getWordOccurencesInClass().forEach( (k,v) -> pWriter1.println(k + "," + v));
			pWriter1.close();
			
			PrintWriter pWriter2 = new PrintWriter("./storage/knowledge-word");
			classifier.getWordOccurences().forEach( (k,v) -> pWriter2.println(k + "," + v));
			pWriter2.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
