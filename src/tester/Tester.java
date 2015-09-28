package tester;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import classifier.NaiveBayes;
import trainer.Trainer;

public class Tester {
	static NaiveBayes classifier;

	public static void main(String args[]) {
		classifier = NaiveBayes.getInstance();

		if (!classifier.isKnowledgeAvailable()) {
			Trainer.startTraining();
		}

		int cnt = 0;
		for (int i = 6916;; i++) {
			File file = new File("./test-docs/pornFiles/Porn" + i + ".txt");
			FileReader fr;

			if (!file.exists())
				break;

			try {
				fr = new FileReader(file);
				BufferedReader br = new BufferedReader(fr);

				String doc = "";
				String str = br.readLine();
				while (str != null) {
					str = str.replaceAll("[\\-\\+\\^\"\\{\\}\\(\\[\\]\\)\\'\\/\\:\\n\\`]", "");
					str = str.replaceAll("\\p{Punct}", "");
					str = str.replaceAll("\\r|\\n|\\t", "");
					str = str.replaceAll("\\p{Cntrl}", "");
					doc = doc + str + ' ';
					str = br.readLine();
				}

				String dClass = classifier.evaluate(doc);
				if (dClass.equals(NaiveBayes.CLASS_PORN) == false) {
					cnt++;
					System.out.println(file.getName());
					System.out.println("Detected as " + dClass);
				}

				br.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		for (int i = 1;; i++) {
			File file = new File("./test-docs/goodFile8/Good" + i + ".txt");
			FileReader fr;

			if (!file.exists())
				break;

			try {
				fr = new FileReader(file);
				BufferedReader br = new BufferedReader(fr);

				String doc = "";
				String str = br.readLine();
				while (str != null) {
					str = str.replaceAll("[\\-\\+\\^\"\\{\\}\\(\\[\\]\\)\\'\\/\\:\\n\\`]", "");
					str = str.replaceAll("\\p{Punct}", "");
					str = str.replaceAll("\\r|\\n|\\t", "");
					str = str.replaceAll("\\p{Cntrl}", "");
					doc = doc + str + ' ';
					str = br.readLine();
				}

				String dClass = classifier.evaluate(doc);
				if (dClass.equals(NaiveBayes.CLASS_OTHER) == false) {
					cnt++;
					System.out.println(file.getName());
					System.out.println("Detected as " + dClass);
				}

				br.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println(cnt + " Files are evaluated incorrenctly.");

	}
}
