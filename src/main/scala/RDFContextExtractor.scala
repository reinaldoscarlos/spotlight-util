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

import com.typesafe.config.{ConfigFactory, Config}
import java.io._

/**
 * Context Extraction from RDF
 * Used to process the properties and labels and group them by the subject
 * The output is formatted as TSV or JSON
 *
 * @author reinaldo
 */
object RDFContextExtractor extends App {

  /**
   * Choose the format of the output
   * Choose the relevant part to be extracted: object, property or type
   * Fill the input and output file names
   * Check the output to see the extraction
   */

  //Get properties from application.conf
  val config = ConfigFactory.load;

  //Use it to create databases at filesystem
  //DataConn.create_TDB_Filesystem(config.getString("dataSet.location"), config.getString("dataSet.inputFile"));

  //Use it to load a database from filesystem
  DataConn.get_TDB_Filesystem(config.getString("dataSet.location"));

  //Extracting the label
  labelExtraction(config.getString("execution.extraction"), config.getString("execution.outputFormat"), config.getString("execution.inputFile"), config.getString("execution.outputFile"))
  /*
  Reads in files into a Jena Model, performs context extraction, formatting and outputs context into a file.
  Context extraction can focus on property labels, object labels or object type labels.
  */
  def labelExtraction (partToBeExtracted: String, format: String, inputFile: String, outputFile: String) = {
    //Reading the input
    val source = new JenaStatementSource(inputFile)
    // creating the output
    val output = new PrintStream(outputFile)
    // choosing output format
    val formatter = if (format.equals("TSV")) new TSVOutputFormatter else new PigOutputFormatter
    // choosing extraction strategy
    val extractor = if (partToBeExtracted.equals("object")) new ObjectExtractor else new PropertyExtractor
    // applying over input
    source.groupBy( e => e.getSubject ).flatMap {
      case (subject,statements) => {
        val context = extractor.extract(statements).mkString(" ")
        output.println(formatter.format(subject.getLocalName,context))
        Some((subject,context))
      }
      case _ => None
    }.toSeq
     .sortBy( e => e._1.getLocalName )
    output.close()
  }
}
