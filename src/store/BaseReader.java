package store;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.TreeMap;

import index.DocumentAIndexer;
import index.Term;
import index.TermFrequency;
import search.TermQ;

/**
 * Cette classe permet de lire la BD relationnelle contenant l'index Tables
 * pouvant etre lues: Documents, Noeuds, Termes
 */

public class BaseReader {

    static Connection conn;

    /**
     * Constructeur. <br>
     * Effectue une connection a la base
     * 
     * @param ConnectURL
     *            String contenant l'URL pour la connection. Ex :
     *            jdbc:mysql://localhost:3306/databasename
     * @param login
     *            Login pour la connection a la BD
     * @param pass
     *            Password pour la connection a la BD
     */
    public BaseReader(String ConnectURL, String login, String pass) {

	try {
	    // The newInstance() call is a work around for some
	    // broken Java implementations
	    Class.forName("org.postgresql.Driver").newInstance();
	    System.out.println("Driver charge.");
	} catch (Exception ex) {
	    // handle the error
	}

	try {
	    System.out.println("Tentative de connection...");
	    conn = DriverManager.getConnection(ConnectURL, login, pass);
	    System.out.println("Connection ok!");

	} catch (SQLException ex) {
	    // handle any errors
	    System.out.println("SQLException: " + ex.getMessage());
	    System.out.println("SQLState: " + ex.getSQLState());
	    System.out.println("VendorError: " + ex.getErrorCode());
	}

    } // BaseReader()

    /**
     * A REVOIR Trouve l'identifiant de document correspondant a un identifiant
     * de noeud
     * 
     * @param node
     *            identifiant de noeud
     * @return int identifiant de document correspondant
     */
    public int findDoc(int node) throws SQLException {

	Statement stmt = null;
	ResultSet rs = null;
	stmt = conn.createStatement();
	String query = "select * from Noeuds where node_id=" + node;

	rs = stmt.executeQuery(query);
	int doc_id = -1;
	while (rs.next()) {
	    doc_id = rs.getInt("doc_id");

	}
	if (stmt != null) {
	    try {
		stmt.close();
	    } catch (SQLException sqlEx) { // ignore
	    }

	    stmt = null;
	}

	return doc_id;
    } // findDoc()

    /**
     * A REVOIR Trouve le nom de document associe a un noeud donne
     */
    public String findNameDoc(int node) throws SQLException {

	Statement stmt = null;
	ResultSet rs = null;
	stmt = conn.createStatement();
	String query = "select document from Documents, Noeuds where Noeuds.doc_id=Documents.doc_id and node_id="
		+ node;

	rs = stmt.executeQuery(query);
	String doc = "";
	while (rs.next()) {
	    doc = rs.getString("document");

	}
	if (stmt != null) {
	    try {
		stmt.close();
	    } catch (SQLException sqlEx) {
	    }
	    stmt = null;
	}

	return doc;
    }

    /**
     * Trouve un objet DocumentAIndexer correspondant a un id
     * 
     * @param id
     *            l'identifiant du doc
     * @return DocumentAIndexer objet DocumentAIndexer
     * @see DocumentAIndexer
     */
    public DocumentAIndexer document(int iddoc) throws SQLException {

	DocumentAIndexer myDocument = new DocumentAIndexer();
	Statement stmt = null;
	ResultSet rs = null;
	stmt = conn.createStatement();
	String query = "select * from Documents where doc_id=" + iddoc;
	rs = stmt.executeQuery(query);
	while (rs.next()) {
	    myDocument.name = rs.getString("document");

	}
	if (stmt != null) {
	    try {
		stmt.close();
	    } catch (SQLException sqlEx) { // ignore
	    }

	    stmt = null;
	} // stmt

	return myDocument;

    } // document()

    /**
     * A REVOIRRRR Trouve le nombre de termes d'un identifiant de document donne
     * 
     * @param doc_id
     *            idenfiant du document dont on veut le nombre de termes
     * @return int nombre de termes pour l'identifiant de document donne
     */
    public int getDocTermCount(int doc_id) throws SQLException {
	Statement stmt = null;
	ResultSet rs = null;
	stmt = conn.createStatement();
	String query = "select term_nb from Documents where doc_id= " + doc_id;
	int nb = 0;

	rs = stmt.executeQuery(query);
	while (rs.next()) {
	    nb = rs.getInt(1);
	}
	return nb;
    } // getDocTermCount()

    /**
     * Trouve le nombre de documents stockes dans la table Documents
     * 
     * @return int nombre de documents dans la table Documents
     */
    public int maxDoc() throws SQLException {
	Statement stmt = null;
	ResultSet rs = null;
	stmt = conn.createStatement();
	String query = "select count(*) from Documents ";
	int max = 0;

	rs = stmt.executeQuery(query);
	while (rs.next()) {
	    max = rs.getInt(1);
	}
	return max;
    } // maxDoc()

    /**
     * Trouve le nombre de noeuds feuilles de la collection
     * 
     * @return int nombre de noeuds feuilles de la collection
     */
    public int maxLeaf() throws SQLException {
	Statement stmt = null;
	ResultSet rs = null;
	stmt = conn.createStatement();
	String query = "select sum(leaf_nb) from Documents";
	int max = 0;

	rs = stmt.executeQuery(query);
	while (rs.next()) {
	    max = rs.getInt(1);
	}
	return max;
    } // maxLeaf()

    /**
     * Trouve l'objet Term correspondant a un terme en le cherchant dans la
     * table Termes
     * 
     * @see Term
     * @param myTermQuery
     *            nom du terme a chercher
     * @return Term Object Term correspondant a la chaine a rechercher
     */
    public Term readTerm(TermQ myTermQuery) throws SQLException {

	// assume conn is an already created JDBC connection
	Statement stmt = null;
	Statement stmt2 = null;
	ResultSet rs = null;
	stmt = conn.createStatement();
	stmt2 = conn.createStatement();
	String query = "select * from Termes where term=\'" + myTermQuery.text + "\';";
	Term myTerm = null;
	TreeMap<Integer, TermFrequency> tableFrequency = new TreeMap<Integer, TermFrequency>();
	rs = stmt.executeQuery(query);
	while (rs.next()) {
	    int term_id = rs.getInt("term_id");

	    String query2 = "select * from TermesDoc where term_id=" + term_id + ";";
	    ResultSet rs2 = null;
	    rs2 = stmt2.executeQuery(query2);
	    // for each doc in which the term is
	    while (rs2.next()) {
		int doc_id = rs2.getInt("doc_id"); // get the node doc id
		short frequency = rs2.getShort("poids"); // get frequency
		// System.out.println("terme prÃ©sent dans
		// "+this.document(doc_id).name+" de poids "+frequency);
		TermFrequency myTermFrequency = new TermFrequency(doc_id, frequency);
		tableFrequency.put(doc_id, myTermFrequency);

	    }

	    myTerm = new Term(term_id, myTermQuery.text, tableFrequency);
	    myTerm.doc_count = getNbDocWhereTerme(myTerm.text);
	    myTerm.weigth = myTermQuery.weigth;
	} // while rs.next

	if (stmt != null) {
	    try {
		stmt.close();
	    } catch (SQLException sqlEx) { // ignore
	    }
	    stmt = null;
	} // stmt
	return myTerm;

    } // readTerm()

    /**
     * Retourne le nombre de documents où le terme apparait.
     * 
     * @param terme
     * @return
     * @throws SQLException
     */
    public int getNbDocWhereTerme(String terme) throws SQLException {
	Statement stmt = null;
	ResultSet rs = null;
	stmt = conn.createStatement();
	String query = "select count(*) from TermesDoc, Termes where Termes.term='" + terme
		+ "' and Termes.term_id=TermesDoc.term_id;";
	int nbDocs = 0;

	rs = stmt.executeQuery(query);
	while (rs.next()) {
	    nbDocs = rs.getInt(1);
	}
	return nbDocs;
    }

    /**
     * Ferme la connection au serveur MySQL
     */
    public static void close() {
	try {
	    conn.close();

	} catch (SQLException ex) {
	    // handle any errors
	    System.out.println("SQLException: " + ex.getMessage());
	    System.out.println("SQLState: " + ex.getSQLState());
	    System.out.println("VendorError: " + ex.getErrorCode());
	}
    } // close()

}// BaseReader.java
