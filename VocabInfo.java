import java.util.HashMap;
import java.util.Set;

public class VocabInfo {
	
	// word -> WordInfo
	private HashMap<String, WordInfo> vocab;
	
	// class -> number of distinct word positions in class
	private HashMap<String, Integer> distinctWordPos;
	
	// class -> probability of class
	private HashMap<String, Double> pClass;

	public VocabInfo() {
		vocab = new HashMap<String, WordInfo>();
		distinctWordPos = new HashMap<String, Integer>();
		distinctWordPos.put("spam", 0);
		distinctWordPos.put("ham", 0);
	}
	
	protected void add(String cl, String word, int count) {
		assert cl.equals("spam") || cl.equals("ham");
		// Determine if this word has been seen before, and
		// if not, increment vocabSize, and
		// if so, add it to each class list.
		if (vocab.containsKey(word)) {
			vocab.get(word).increment(cl, count);
		} else {
			vocab.put(word, new WordInfo(cl, count));
		}
		
		// increment number of distinct word positions in class
		distinctWordPos.put(cl, distinctWordPos.get(cl) + count);

	}

	protected void setProbOfClasses(HashMap<String, Double> p) {
		assert p != null;
		pClass = new HashMap<String, Double>();
		Set<String> classes = p.keySet();
		for (String c : classes) {
			pClass.put(c, p.get(c));
		}
		assert classes.size() == 2;
		assert pClass.get("spam") + pClass.get("ham") == 1;
	}
	
	protected void computeCondProb(double smoothingValue) {
		
		if (smoothingValue == -1) {
			smoothingValue = vocab.size();
		}

		// prior for unseen terms
		double p = (1.0 / ((double) vocab.size()));
		assert p > 0;
		// keys "spam" and "ham"
		String[] classes = {"spam", "ham"};
		// words in vocabulary
		Set<String> words = vocab.keySet();
		for (int i = 0; i < classes.length; i++) {
			for (String word : words) {
				// number of times word occurs in class
				double n = (double) vocab.get(word).getCount(classes[i]);
				// p = prior estimate
				double num = n + (smoothingValue * p);
				double den = ((double) distinctWordPos.get(classes[i])) + smoothingValue;
				vocab.get(word).setcProb(classes[i], (num / den));
			}
		}

	}
	
	protected double getProbOfClass(String cl) {
		assert cl.equals("spam") || cl.equals("ham");
		
		return pClass.get(cl);
		
	}
	
	protected double getCondProb(String word, String cl) {
		assert cl.equals("spam") || cl.equals("ham");
		
		return vocab.get(word).getcProb(cl);
		
	}
	
}
