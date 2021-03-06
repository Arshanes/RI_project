package test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

/**
 * Cette classe permet de sortir les precisions � 5,10 et 25 pour un fichier
 * resultat et un fichier qrel donne Utilisation : java test/TestEval fichierrep
 * fichierqrel
 */
public class TestEval {

    public static void test(String outputFilePath, String qrelFilePath) {
	System.out.println("===========[ Evaluation du r�sultat Q" + outputFilePath.charAt(outputFilePath.length() - 1)
		+ " ]===========");
	try {
	    // vecteur contenant les noeuds pertinents
	    Vector relevants = new Vector();

	    // on lit le fichier de qrel
	    BufferedReader qrel = new BufferedReader(new FileReader(qrelFilePath));

	    String ligne = "";

	    // pour chaque ligne
	    while ((ligne = qrel.readLine()) != null) {
		// System.out.println(ligne);
		String[] maligne = ligne.split("\t");
		// System.out.println(maligne[0] + "-" + maligne[1]);
		String doc = maligne[0];
		// String xpath=maligne[1];
		String pert = maligne[1];
		// System.out.print(pert);
		// on garde les noeuds pertinents
		if (pert.equals("1") || pert.equals("0,5")) {
		    // System.out.println("Ajout");
		    RelevantDoc rn = new RelevantDoc(doc);
		    relevants.add(rn);

		}
	    }
	    qrel.close();

	    // on lit le fichier de resultats
	    BufferedReader res = new BufferedReader(new FileReader(outputFilePath));

	    int rang = 1;
	    int pertinent = 0;
	    int p5 = 0;
	    int p10 = 0;
	    int p25 = 0;

	    while ((ligne = res.readLine()) != null) {

		String[] maligne2 = ligne.split("\t");

		String[] infodoc = maligne2[0].split("/");
		String doc = infodoc[infodoc.length - 1];
		// System.out.println("document" + doc);
		for (int i = 0; i < relevants.size(); i++) {
		    RelevantDoc r = (RelevantDoc) relevants.elementAt(i);
		    // System.out.println("\t"+r.node+"-"+r.doc);
		    if (doc.compareTo(r.doc) == 0) {
			pertinent++;
			boolean ok = relevants.remove(r);
		    }

		}
		if (rang == 5) {
		    p5 = pertinent;
		}
		if (rang == 10) {
		    p10 = pertinent;
		}
		if (rang == 25) {
		    p25 = pertinent;
		}
		rang++;
	    }

	    res.close();

	    System.out.println("P@5: " + (double) p5 / 5);
	    System.out.println("P@10: " + (double) p10 / 10);
	    System.out.println("P@25 :" + (double) p25 / 25);
	} catch (IOException io) {
	    System.out.println("Erreur lecture fichier");
	} finally {
	    System.out.println("===========[ Fin de l'�valuation du r�sultat Q"
		    + outputFilePath.charAt(outputFilePath.length() - 1) + " ]===========");
	}
    }// main

}

class RelevantDoc {

    String doc;

    public RelevantDoc(String d) {

	this.doc = d;
    }

}
