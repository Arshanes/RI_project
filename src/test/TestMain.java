package test;

/*
 * Classe d'ex�cution de tous les tests - Automatique
 */
public class TestMain {

    public final static String CONFIG_FILE_PATH = "./src/config.txt";

    public final static String CORPUS_DIR_PATH = "./corpus-utf8";

    public final static String QREL_DIR_PATH = "./src/eval";

    public final static String OUTPUT_DIR_PATH = "./src/results";

    public final static String[] REQUETES = new String[] { "personnes Intouchables", "lieu naissance Omar Sy",
	    "personnes recompens�es Intouchables", "palmar�s Globes de Cristal 2012",
	    "membre du jury Globes de Cristal 2012", "prix, Omar Sy Globes de Cristal 2012", "lieu Globes Cristal 2012",
	    "prix Omar Sy", "acteur jou� avec Omar Sy" };

    public static void main(String[] args) {

	// Indexation de la base de donn�e
	try {
	    TestIndexer.test(CORPUS_DIR_PATH, CONFIG_FILE_PATH);
	} catch (Exception e) {
	    e.printStackTrace();
	}

	// Test des requ�tes
	for (int i = 0; i < REQUETES.length; i++) {
	    TestQuery.test(CONFIG_FILE_PATH, REQUETES[i], OUTPUT_DIR_PATH + "/outputQ" + (i + 1));
	    System.out.println();
	}

	// Evaluation des requ�tes
	for (int i = 0; i < REQUETES.length; i++) {
	    TestEval.test(OUTPUT_DIR_PATH + "/outputQ" + (i + 1), QREL_DIR_PATH + "/qrelQ" + (i + 1) + ".txt");
	    System.out.println();
	}
    }

}
