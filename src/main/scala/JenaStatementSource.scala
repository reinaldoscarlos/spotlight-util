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

import com.hp.hpl.jena.rdf.model._
import com.hp.hpl.jena.util.FileManager
import java.io.{IOException, InputStream}

/**
 * Allows reading statements from RDF files using Jena.
 * TODO may need refactoring to read from multiple files
 * TODO may need refactoring to store input from files into a DB-backed model
 */
class JenaStatementSource(inputFile: String) extends Traversable[Statement] {
  //Opening the input
  val model: Model = ModelFactory.createDefaultModel
  val input: InputStream = FileManager.get.open(inputFile)
  if (input != null) {
    model.read(input, null, "N-TRIPLE")
  } else {
    throw new IOException("Cannot open input %s".format(inputFile))
  }

  /*
  This method takes as input a function that maps from a Statement to anything
   */
  override def foreach[U](extract : Statement => U) {
    val inputSubjects: ResIterator = model.listSubjects
    while (inputSubjects.hasNext) {
      //Get the subject
      val subject: Resource = inputSubjects.next
      val properties: StmtIterator = subject.listProperties
      while (properties.hasNext) {
        extract(properties.next)
      }
    }
  }
}
