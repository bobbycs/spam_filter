Naive Bayes implementation to classify spam email.

Implementation Description:

The project contains 3 Java files: NaiveBayesClassifier.java, VocabInfo.java, and WordInfo.java. A WordInfo object contains information about a single word in the data, including the number of times it occurs in each class and the conditional probability of the word given a class. A VocabInfo object contains the vocabulary and information about the data. The vocabulary is represented as a HashMap that maps a word to its WordInfo object. VocabInfo also contains a HashMap that maps a class from the set {“spam” , “ham”} to the number of distinct word positions in the class, which is used in the conditional probability calculation. Finally, VocabInfo contains the probabilities of each class.

Format for training and testing data files:

[example id] [class] [word] [number of occurrences of word] [word] [number of occurrences of word] ...
[example id] [class] [word] [number of occurrences of word] [word] [number of occurrences of word] ...
…

Example:
01 spam this 1 email 1 is 1 spam 1
02 ham this 1 email 1 is 1 not 1 spam 1