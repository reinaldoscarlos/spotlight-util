import com.hp.hpl.jena.query._
import com.hp.hpl.jena.rdf.model.{Model}
import com.hp.hpl.jena.tdb.TDBFactory
import com.hp.hpl.jena.util.FileManager
import scala.Predef.String

/**
 * Object to create and to load a database connection
 * It provides a database connection (variable dataSet) that is used in the whole project to execute queries
 *
 * @author reinaldo
 */
object DataConn {

  var dataSet: Dataset = null

  def create_TDB_Filesystem(outputData: String, inputFile: String) {
    dataSet = TDBFactory.createDataset(outputData)
    val tdb: Model = dataSet.getDefaultModel
    FileManager.get.readModel(tdb, inputFile)
    tdb.close
  }

  def get_TDB_Filesystem(outputData: String) {
    dataSet = TDBFactory.createDataset(outputData)
  }

  def executeQuery(uri: String): String = {

    val queryStr: ParameterizedSparqlString = new ParameterizedSparqlString("SELECT * WHERE { ?s ?p ?o }")
    queryStr.setIri("s", uri)
    queryStr.setIri("p", "http://www.w3.org/2000/01/rdf-schema#label")
    val query: Query = QueryFactory.create(queryStr.toString)
    val queryExec: QueryExecution = QueryExecutionFactory.create(query, dataSet)
    val results: ResultSet = queryExec.execSelect
    var result: String = null
    if (results.hasNext) {
      val querySolution = results.next
      result = querySolution.getLiteral("o").getString
    }
    return result
  }
}
