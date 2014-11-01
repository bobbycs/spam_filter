import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

/**
 * NaiveBayesClassifier
 * 
 * 
 * @author Bobby Simon
 * 
 */
public class NaiveBayesClassifier {

	/**
	 * @param args
	 *            Smoothing parameter for m-estimate. If more than one is given,
	 *            the program will be run separately for each one. The value -1
	 *            refers to the default smoothing value, which is the size of
	 *            the vocabulary, and will be used if no arguments are given.
	 */
	public static void main(String[] args) {

		String train = "train.txt";
		String test = "test.txt";
		// default smoothing value of -1 will use the size of 
		// the vocabulary once it is determined
		double smoothingValue = -1;
		VocabInfo vocabInfo = new VocabInfo();

		try {

			if (args.length > 0) {
				for (int i = 0; i < args.length; i++) {
					// parse program argument to double for smoothing value
					smoothingValue = Double.parseDouble(args[i]);

					// LEARN
					learn(vocabInfo, train, smoothingValue);

					if (smoothingValue != -1) {
						System.out
								.println("Smoothing value: " + smoothingValue);
					} else {
						System.out.println("Smoothing value: |vocabulary|");
					}

					// CLASSIFY
					classify(vocabInfo, test);
					System.out.println("");
				}
			} else {
				// LEARN
				learn(vocabInfo, train, smoothingValue);
				System.out.println("Smoothing value: |vocabulary|");
				// CLASSIFY
				classify(vocabInfo, test);
				System.out.println("");
			}

		} catch (NumberFormatException e) {
			System.err
					.print("Error: smoothing parameter must be a parsable double");
			System.exit(1);
		}

	}

	// Private helper method to learn from the data.
	private static void learn(VocabInfo vocabInfo, String dataFile,
			double smoothingValue) {

		BufferedReader dataReader;
		String line;
		String delimiter = " ";

		String[] data;
		String actualClass;

		// number of documents in each class
		HashMap<String, Integer> docCount = new HashMap<String, Integer>();
		docCount.put("spam", 0);
		docCount.put("ham", 0);

		try {
			dataReader = new BufferedReader(new FileReader(dataFile));
			while ((line = dataReader.readLine()) != null) {

				data = line.split(delimiter);
				assert (data.length % 2 == 0);

				actualClass = data[1];
				assert actualClass.equals("spam") || actualClass.equals("ham");
				// increment document count for class
				docCount.put(actualClass, docCount.get(actualClass) + 1);
				// read words and word counts
				for (int i = 2; i < data.length; i += 2) {
					// add word to vocabulary
					vocabInfo.add(actualClass, data[i],
							Integer.parseInt(data[i + 1]));
				}

			}

			// compute probability of each class
			assert docCount.size() == 2;
			int examples = docCount.get("spam") + docCount.get("ham");
			double pSpam = (double) docCount.get("spam") / (double) examples;
			double pHam = (double) docCount.get("ham") / (double) examples;
			assert pSpam + pHam == 1;
			HashMap<String, Double> p = new HashMap<String, Double>();
			p.put("spam", pSpam);
			p.put("ham", pHam);
			vocabInfo.setProbOfClasses(p);

			// compute conditional probabilities
			vocabInfo.computeCondProb(smoothingValue);

			dataReader.close();

		} catch (FileNotFoundException e) {
			System.err.println("Error in file format - file not found");
			System.exit(1);
		} catch (IOException e) {
			System.err.println("Error in file format - IOException");
			System.exit(1);
		} catch (NumberFormatException e) {
			System.err.println("NumberFormatException in learn()");
			System.exit(1);
		}

	}

	// Private helper method to classifier each test example and compute accuracy.
	// Uses a sum of logs of probabilities instead of a product of probabilities
	// to avoid underflow.
	private static void classify(VocabInfo vocabInfo, String dataFile) {

		BufferedReader dataReader;
		String line;
		String delimiter = " ";

		// read from data
		String[] data;
		String actualClass, classification;
		// accuracy
		double correct = 0, total = 0;
		try {

			dataReader = new BufferedReader(new FileReader(dataFile));
			// for each test example
			while ((line = dataReader.readLine()) != null) {

				data = line.split(delimiter);
				assert (data.length % 2 == 0);

				actualClass = data[1];
				assert actualClass.equals("spam") || actualClass.equals("ham");

				String[] classes = { "spam", "ham" };
				assert vocabInfo.getProbOfClass(classes[0])
						+ vocabInfo.getProbOfClass(classes[1]) == 1;
				double[] sumOfLogs = new double[classes.length]; // results
				double condProb;
				// for each class
				for (int i = 0; i < classes.length; i++) {
					sumOfLogs[i] = Math.log(vocabInfo
							.getProbOfClass(classes[i]));
					// for each word
					for (int j = 2; j < data.length; j += 2) {
						// start with probability of the class
						condProb = vocabInfo.getCondProb(data[j], classes[i]);
						assert condProb > 0 && condProb < 1;
						assert Math.log(condProb) < 0;
						// multiply log by number of times this word occurs in
						// the example and add to result
						sumOfLogs[i] += (Math.log(condProb) * Integer
								.parseInt(data[j + 1]));
					}
					// assert sumOfLogs[i] < 0; // ?
				}
				classification = sumOfLogs[0] > sumOfLogs[1] ? classes[0]
						: classes[1];
				if (classification.equals(actualClass)) {
					correct++;
				}
				total++;
			}
			if (total != 0) {				
				System.out.println("Accuracy: " + (correct / total));
			} else {
				System.out.println("Accuracy: no test examples");
			}

		} catch (FileNotFoundException e) {
			System.err.println("Error in file format - file not found");
			System.exit(1);
		} catch (IOException e) {
			System.err.println("Error in file format - IOException");
			System.exit(1);
		} catch (NumberFormatException e) {
			System.err.println("NumberFormatException in classify()");
			System.exit(1);
		}

	}

}