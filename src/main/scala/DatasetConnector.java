import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.sdb.SDBFactory;
import com.hp.hpl.jena.sdb.Store;
import com.hp.hpl.jena.sdb.StoreDesc;
import com.hp.hpl.jena.sdb.sql.JDBC;
import com.hp.hpl.jena.sdb.sql.SDBConnection;
import com.hp.hpl.jena.sdb.store.DatabaseType;
import com.hp.hpl.jena.sdb.store.LayoutType;
import com.hp.hpl.jena.sdb.util.StoreUtils;
import com.hp.hpl.jena.tdb.TDBFactory;
import com.hp.hpl.jena.util.FileManager;

/**
 * Creation and use of datasets (SDB and TDB).
 *
 * @author reinaldo
 */
public class DatasetConnector {

    //TODO decide SDB or TDB?
    //TODO write in scala language
    //TODO insert this code in the extractor
    //TODO remove paths hardcoded
    //TODO implement the test
    public static void main(String[] args) {
        DatasetConnector datasetConnector = new DatasetConnector();

        //First, create one of these datasets, then execute a query.
        //datasetConnector.create_SDB_MySQL("jdbc:mysql://localhost:3306/sdb_nt", "", "", "C:\\Users\\reinaldo\\Desktop\\labels_en.nt\\labels_en3.nt");
        datasetConnector.query_SDB_MySQL("jdbc:mysql://localhost:3306/sdb_nt", "", "");

        //datasetConnector.create_SDB_MySQL("jdbc:mysql://localhost:3306/sdb_owl", "", "", "file:C:\\Users\\reinaldo\\Desktop\\dbpedia_3.8.owl\\dbpedia_3.8.owl");
        datasetConnector.query_SDB_MySQL("jdbc:mysql://localhost:3306/sdb_owl", "", "");

        //datasetConnector.create_TDB_Filesystem("C:\\Users\\reinaldo\\Desktop\\DATASETS\\TDB_NT", "C:\\Users\\reinaldo\\Desktop\\labels_en.nt\\labels_en3.nt");
        datasetConnector.query_TDB_Filesystem("C:\\Users\\reinaldo\\Desktop\\DATASETS\\TDB_NT");

        //datasetConnector.create_TDB_Filesystem("C:\\Users\\reinaldo\\Desktop\\DATASETS\\TDB_OWL", "file:C:\\Users\\reinaldo\\Desktop\\dbpedia_3.8.owl\\dbpedia_3.8.owl");
        datasetConnector.query_TDB_Filesystem("C:\\Users\\reinaldo\\Desktop\\DATASETS\\TDB_OWL");
    }

    private void create_SDB_MySQL(String jdbcURL, String jdbcUser, String jdbcPassword, String inputFile){
        try{
            StoreDesc storeDesc = new StoreDesc(LayoutType.LayoutTripleNodesHash, DatabaseType.MySQL) ;
            JDBC.loadDriverMySQL();

            SDBConnection conn = new SDBConnection(jdbcURL, jdbcUser, jdbcPassword) ;
            Store store = SDBFactory.connectStore(conn, storeDesc);
            if (!StoreUtils.isFormatted(store)) {
                store.getTableFormatter().create();
            }
            Dataset dataset = SDBFactory.connectDataset(store);
            Model tdb = dataset.getDefaultModel();
            FileManager.get().readModel(tdb, inputFile);
            tdb.close();
            store.close();
            conn.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void create_TDB_Filesystem(String outputDataset, String inputFile){
        Dataset dataset = TDBFactory.createDataset(outputDataset);
        Model tdb = dataset.getDefaultModel();
        FileManager.get().readModel(tdb, inputFile);
        tdb.close();
    }

    private void query_SDB_MySQL(String jdbcURL, String jdbcUser, String jdbcPassword){
        try{
            StoreDesc storeDesc = new StoreDesc(LayoutType.LayoutTripleNodesHash, DatabaseType.MySQL) ;
            JDBC.loadDriverMySQL();
            SDBConnection conn = new SDBConnection(jdbcURL, jdbcUser, jdbcPassword) ;
            Store store = SDBFactory.connectStore(conn, storeDesc);
            Dataset dataset = SDBFactory.connectDataset(store);

            executeQuery(dataset);

            store.close();
            conn.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void query_TDB_Filesystem(String outputDataset){
        Dataset dataset = TDBFactory.createDataset(outputDataset);
        executeQuery(dataset);
    }

    private void executeQuery(Dataset dataset){
        String sparqlQueryString = "SELECT (count(*) AS ?count) { ?s ?p ?o }" ;
        //String sparqlQueryString = "SELECT * { ?s ?p ?o }" ;
        Query query = QueryFactory.create(sparqlQueryString) ;
        QueryExecution qexec = QueryExecutionFactory.create(query, dataset) ;
        ResultSet results = qexec.execSelect() ;
        ResultSetFormatter.out(results);
        dataset.close();
    }
}
