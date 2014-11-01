import java.util.HashMap;

public class WordInfo {

	// class -> number of occurrences of this word in the given class
	private HashMap<String, Integer> occurrences;
	// class -> conditional probability of this word given class
	private HashMap<String, Double> cProb;

	public WordInfo(String cl, int c) {
		occurrences = new HashMap<String, Integer>();
		occurrences.put("spam", 0);
		occurrences.put("ham", 0);
		increment(cl, c);
		cProb = new HashMap<String, Double>();
	}

	protected void increment(String cl, int c) {
		assert cl.equals("spam") || cl.equals("ham");
		occurrences.put(cl, occurrences.get(cl) + c);
		assert occurrences.size() == 2;
	}

	protected int getCount(String cl) {
		return occurrences.get(cl);
	}

	protected void setcProb(String cl, double p) {
		assert cl.equals("spam") || cl.equals("ham");
		assert cProb.size() <= 2;
		cProb.put(cl, p);
	}

	protected double getcProb(String cl) {
		assert cl.equals("spam") || cl.equals("ham");
		return cProb.get(cl);
	}

}
