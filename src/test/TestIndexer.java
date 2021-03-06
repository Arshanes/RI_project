package test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;

import index.IndexWriter;
import store.BaseWriter;

/**
 * 
 * Cette classe permet d'indexer une simple collection de documents HTML.<br>
 * L'index est stocke dans une base de donn�es relationnelle POstGres.
 *
 * Utilisation: java test/TestIndexer dir config <br>
 * o� dir est le repertoire contenant les documents, et config est le fichier
 * de configuration pour la connexion à la base.
 */

public class TestIndexer {

    public static void test(String corpusDirPath, String configFilePath) throws IOException, SQLException {

	System.out.println("===========[ Indexation de la collection ]===========");

	// connection à la base
	BufferedReader config = new BufferedReader(new FileReader(configFilePath));
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

	BaseWriter maBase = new BaseWriter(ConnectURL, login, pass);

	System.out.println("Creation de la base");
	maBase.create();

	// Parcours des documents avec le parseur Jsoup et construction des
	// structures de stockage
	System.out.println("Parcours des documents");
	IndexWriter monIndexWriter = new IndexWriter(corpusDirPath, maBase);
	monIndexWriter.construct();

	// fermeture de la base
	maBase.close();

	System.out.println("===========[ Fin de l'indexation ]===========");

    } // main(String[])

} // class TestIndexer.java
