package test;

/*
 * Classe d'exécution de tous les tests - Automatique
 */
public class TestMain {

    public final static String CONFIG_FILE_PATH = "./src/config.txt";

    public final static String CORPUS_DIR_PATH = "./corpus-utf8";

    public final static String QREL_DIR_PATH = "./src/eval";

    public final static String OUTPUT_DIR_PATH = "./src/results";

    public final static String[] REQUETES = new String[] { "personnes Intouchables", "lieu naissance Omar Sy",
	    "personnes recompensées Intouchables", "palmarès Globes de Cristal 2012",
	    "membre du jury Globes de Cristal 2012", "prix, Omar Sy Globes de Cristal 2012", "lieu Globes Cristal 2012",
	    "prix Omar Sy", "acteur joué avec Omar Sy" };

    public static void main(String[] args) {

	// Indexation de la base de donnée
	try {
	    TestIndexer.test(CORPUS_DIR_PATH, CONFIG_FILE_PATH);
	} catch (Exception e) {
	    e.printStackTrace();
	}

	// Test des requêtes
	for (int i = 0; i < REQUETES.length; i++) {
	    TestQuery.test(CONFIG_FILE_PATH, REQUETES[i], OUTPUT_DIR_PATH + "/outputQ" + (i + 1));
	    System.out.println();
	}

	// Evaluation des requêtes
	for (int i = 0; i < REQUETES.length; i++) {
	    TestEval.test(OUTPUT_DIR_PATH + "/outputQ" + (i + 1), QREL_DIR_PATH + "/qrelQ" + (i + 1) + ".txt");
	    System.out.println();
	}
    }

}
