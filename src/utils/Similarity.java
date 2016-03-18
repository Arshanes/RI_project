package utils;

import java.util.Iterator;
import java.util.TreeMap;

import index.Term;
import index.TermFrequency;

/**
 * Classe permettant le calcul de stocres
 */
public final class Similarity {

    private Similarity() {
    } // no public constructor

    /**
     * This method either compute the inverse element frequency or inverse
     * document frequency for a given term.
     * 
     * @param term
     *            the term for which the computation is made
     * @param reader
     *            BaseReader allowing the indx to be readed
     */
    // public static final double inverseFrequency(Term term, BaseReader reader)
    // throws IOException, SQLException {

    // Simple ief (Inverse Element Frequency) computation
    // return ief(term.element_count, reader.maxLeaf());

    // Simple idf (Inverse Document Frequency) computation
    // System.out.println(term.doc_count+" "+reader.maxDoc());
    // return idf(term.doc_count, reader.maxDoc());

    // } // inverseFrequency()

    /**
     * Computes Inverse Element Frequency
     **/
    public static final double ief(int eltFreq, int numElts) {
	return (double) (Math.log(numElts / (double) (eltFreq + 1)) + 1.0);
    }

    /**
     * Computes Inverse Document Frequency
     **/
    public static final double idf(int docFreq, int numDocs) {
	return (double) (Math.log(numDocs / (double) (docFreq + 1)) + 1.0);

    }

    /**
     * Computes the Innerproduct for a TERM find in a doc and in a query
     */
    public static final double InnerProd(double poidDoc, short poidReq) {
	return (double) (poidDoc * poidReq);
    }

    /**
     * Computes TF-IDF for a specified Term
     */
    public static final TreeMap<Integer, Double> tfidf(Term myTerm, double idf) {

	// On initie le TreeMap résultat
	TreeMap<Integer, Double> result = new TreeMap<Integer, Double>();

	// Parcours des TermFrequencies pour récupérer tf
	for (Iterator<?> it = myTerm.frequency.keySet().iterator(); it.hasNext();) {
	    TermFrequency mafrequence = (TermFrequency) myTerm.frequency.get(it.next());

	    double tfidf = mafrequence.frequency / idf;

	    // Ajout du document et du poid du terme étudié dans la liste
	    result.put(mafrequence.doc_id, tfidf / Similarity.InnerProd(tfidf, myTerm.weigth));

	}
	return result;
    }

    /**
     * Concat two TreeMaps into the first one.
     */
    public static TreeMap<Integer, Double> concatScores(TreeMap<Integer, Double> result,
	    TreeMap<Integer, Double> resultTerm) {
	for (Iterator<Integer> it = resultTerm.keySet().iterator(); it.hasNext();) {
	    Integer doc = it.next();
	    if (result.containsKey(doc)) {
		double ancienScore = result.get(doc);
		result.remove(doc);
		result.put(doc, ancienScore + resultTerm.get(doc) + ancienScore);
	    } else {
		result.put(doc, resultTerm.get(doc));
	    }
	}
	return result;
    }

} // Similarity.java
