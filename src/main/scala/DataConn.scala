/* Copyright 2012 Intrinsic Ltda.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*
* Check our project website for information on how to acknowledge the
* authors and how to contribute to the project:
* http://spotlight.dbpedia.org
*/

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
