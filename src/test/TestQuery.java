package test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import search.TermQuery;
import store.BaseReader;

/**
 * Classe de test pour les requetes La requete peut etre composee de plusieurs
 * mots cles separes par des espaces Utilisation : java test/TestQuery
 * fichierconfig "terme1 terme2 terme3" fichiersortie
 *
 */
public class TestQuery {

    public static void test(String configfilePath, String _query, String outputFilePath) {
	System.out.println("===========[ Test de la requête Q" + outputFilePath.charAt(outputFilePath.length() - 1)
		+ " ]===========");
	try {
	    FileWriter out = new FileWriter(new File(outputFilePath));

	    TermQuery query = new TermQuery(_query);

	    BufferedReader config = new BufferedReader(new FileReader(configfilePath));

	    String ConnectURL;
	    String login = "";
	    String pass = "";
	    ConnectURL = config.readLine();

	    if (ConnectURL != null) {
		login = config.readLine();
	    }
	    if (login != null) {
		pass = config.readLine();
	    }
	    config.close();

	    BaseReader base = new BaseReader(ConnectURL, login, pass);
	    // recherche de tous les documents pertinents de l'index et on
	    // calcule le score de pertinence
	    TreeMap<?, ?> results = query.score(base);

	    System.out.println(results.size() + " resultats");

	    // on trie la TreeMap de resultats selon les valeurs,
	    // cad selon le score (et non selon la cle, cad
	    // selon l'identifiant du doc )
	    List<?> cles = new ArrayList<Object>(results.keySet());
	    Collections.sort(cles, new CompScore(results));

	    for (Iterator<?> it = cles.listIterator(); it.hasNext();) {
		// on recupere l'id du document et on va chercher son nom
		Integer docid = (Integer) it.next();
		String nom_fichier = base.document(docid).name.replace("\\", "/");
		out.write(nom_fichier + "\t" + results.get(docid) + "\n");
		// out.write(node.getTextContent()+"\n\n");
	    }

	    BaseReader.close();
	    out.close();
	} catch (IOException e) {
	    System.out.println("Problem : End of file." + e.getMessage());
	} catch (SQLException e2) {
	    System.out.println("SQL Error ." + e2.getMessage());
	} catch (Exception e) {
	    System.out.println("Error : Unable to process files. " + e.getMessage());
	} finally {
	    System.out.println("===========[ Fin du test de la requête Q"
		    + outputFilePath.charAt(outputFilePath.length() - 1) + " ]===========");
	}

    }

    /*
     * Classe permettant de trier les resultats sur les scores et non sur les
     * identifiants de noeuds
     */
    public static class CompScore implements Comparator<Object> {

	private Map<?, ?> copieresults;

	public CompScore(Map<?, ?> results) {
	    this.copieresults = results;
	}

	public int compare(Object o1, Object o2) {
	    Double s1 = (Double) copieresults.get((Integer) o1);
	    Double s2 = (Double) copieresults.get((Integer) o2);
	    return (s2.compareTo(s1));
	}

    }

} // TestQuery.java
