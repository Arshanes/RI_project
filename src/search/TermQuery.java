package search;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.TreeMap;
import java.util.Vector;

import index.Term;
import store.BaseReader;
import utils.Similarity;

/**
 * Classe permettant de traiter les requetes : recherche des termes dans
 * l'index, calcul des scores des elements
 */
final public class TermQuery {

    // vecteur contenant les termes de la requetes (objets Term)
    private Vector<TermQ> terms;

    /**
     * Constructeur : construit le vecteur des termes de la requete
     */
    public TermQuery(String query) {

	terms = new Vector<TermQ>();
	System.out.println("La requete est:" + query);
	String[] termstable = query.split(" ");

	// on pourrait lemmatiser mais on ne le fait pas!

	for (int i = 0; i < termstable.length; i++) {
	    String[] termpoid = termstable[i].split(":");
	    TermQ monTerme = null;
	    Short un = 1;
	    if (termpoid.length < 2)
		monTerme = new TermQ(termpoid[0].toLowerCase(), un);
	    else
		monTerme = new TermQ(termpoid[0].toLowerCase(), Short.parseShort(termpoid[1]));
	    terms.add(monTerme);
	}

	System.out.println("Fin de la requete");
    }

    /**
     * Calcule les scores des documents contenant au moins un terme de la
     * requete
     */
    public TreeMap<Integer, Double> score(BaseReader reader) throws IOException {

	TreeMap<Integer, Double> result = new TreeMap<Integer, Double>();

	// Parcours de chaque terme de la requête
	for (Enumeration<TermQ> e = terms.elements(); e.hasMoreElements();) {

	    TermQ myTermQuery = null;
	    Term myTerm = null;
	    try {
		myTermQuery = (TermQ) e.nextElement();
		System.out.println("recherche dans l'index des doc pour " + myTermQuery.text);
		myTerm = reader.readTerm(myTermQuery);

		if (myTerm != null) {

		    // Calcul de IDF:
		    double idf = Similarity.idf(reader.getNbDocWhereTerme(myTerm.text), reader.maxDoc());

		    // Calcul de tous les poids d'un terme dans les documents à
		    // l'aide de tf-idf:
		    TreeMap<Integer, Double> resultTerme = Similarity.tfidf(myTerm, idf);

		    // Concaténation des résultats avec la liste result:
		    result = Similarity.concatScores(result, resultTerme);
		}
	    } catch (SQLException ex) {
		System.out.println("Erreur de recuperation du terme ou de calcul du poids");
		System.out.println("SQLException: " + ex.getMessage());
		System.out.println("SQLState: " + ex.getSQLState());
		System.out.println("VendorError: " + ex.getErrorCode());
	    }

	}
	return result;

    } // scorer

} // termQuery.java
